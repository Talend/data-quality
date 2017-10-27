// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.semantic.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.talend.dataquality.semantic.classifier.custom.UDCategorySerDeser;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.index.ClassPathDirectory;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;

/**
 * Singleton class providing API for local category registry management.
 * 
 * A local category registry is composed by the following folders:
 * <ul>
 * <li><b>shared/prod/:</b> shared dictionaries between tenants, provided by Talend</li>
 * <li><b>t_id/prod/:</b> tenant specific dictionaries</li>
 * </ul>
 * 
 * Inside each of the above folder, the following subfolders can be found:
 * <ul>
 * <li><b>metadata:</b> lucene index containing metadata of all categories. In the tenant specific folder, the metadata of
 * all provided categories are copied from shared metadata.</li>
 * <li><b>dictionary:</b> lucene index containing dictionary documents. In the tenant specific folder, we only include the
 * user-defined data dictionaries, and make the copy for only the modified categories provided by Talend</li>
 * <li><b>keyword:</b> lucene index containing keyword documents. This folder does not exist in tenant specific folder as the
 * modification is not allowed for these categories</li>
 * <li><b>regex:</b> json file containing all categories that can be recognized by regex patterns and eventual subvalidators</li>
 * </ul>
 */
public class CategoryRegistryManager {

    private static final Logger LOGGER = Logger.getLogger(CategoryRegistryManager.class);

    private static CategoryRegistryManager instance;

    private static final Map<String, CustomDictionaryHolder> customDictionaryHolderMap = new HashMap<>();

    /**
     * Whether the local category registry will be used.
     * Default value is false, which means only initial categories are loaded. This is mostly useful for unit tests.
     * More often, the value is set to true when the localRegistryPath is configured. see
     * {@link CategoryRegistryManager#setLocalRegistryPath(String)}
     */
    private static boolean usingLocalCategoryRegistry = false;

    private static final String DEFAULT_LOCAL_REGISTRY_PATH = System.getProperty("user.home") + "/.talend/dataquality/semantic";

    private static String localRegistryPath = DEFAULT_LOCAL_REGISTRY_PATH;

    public static final String METADATA_SUBFOLDER_NAME = "metadata";

    public static final String DICTIONARY_SUBFOLDER_NAME = "dictionary";

    public static final String KEYWORD_SUBFOLDER_NAME = "keyword";

    public static final String REGEX_SUBFOLDER_NAME = "regex";

    public static final String REGEX_CATEGRIZER_FILE_NAME = "categorizer.json";

    public static final String SHARED_FOLDER_NAME = "shared";

    public static final String PRODUCTION_FOLDER_NAME = "prod";

    public static final String REPUBLISH_FOLDER_NAME = "republish";

    public static final String DEFAULT_TENANT_ID = "t_default";

    /**
     * Map between category ID and the object containing its metadata.
     */
    private Map<String, DQCategory> sharedMetadata = new LinkedHashMap<>();

    private UserDefinedClassifier sharedRegexClassifier;

    private Directory sharedDataDictDirectory;

    private static final Object indexExtractionLock = new Object();

    private CategoryRegistryManager() {
        try {
            if (usingLocalCategoryRegistry) {
                loadRegisteredCategories();
            } else {
                loadInitialCategories();
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static CategoryRegistryManager getInstance() {
        if (instance == null) {
            instance = new CategoryRegistryManager();
        }
        return instance;
    }

    public static void reset() {
        setUsingLocalCategoryRegistry(false);
        localRegistryPath = DEFAULT_LOCAL_REGISTRY_PATH;
        instance = null;
    }

    public static void setUsingLocalCategoryRegistry(boolean b) {
        usingLocalCategoryRegistry = b;
    }

    /**
     * Configure the local category registry path.
     * 
     * @param folder the folder to contain the category registry.
     */
    public static void setLocalRegistryPath(String folder) {
        if (folder != null && folder.trim().length() > 0) {
            localRegistryPath = folder;
            usingLocalCategoryRegistry = true;
            instance = new CategoryRegistryManager();
        } else {
            LOGGER.warn("Cannot set an empty path as local registy location. Use default one: " + localRegistryPath);
        }
    }

    /**
     * @return the path of local registry.
     */
    public static String getLocalRegistryPath() {
        return localRegistryPath;
    }

    /**
     * @return the {@link LocalDictionaryCache} corresponding to the default tenant ID.
     */
    public LocalDictionaryCache getDictionaryCache() {
        return getDictionaryCache(DEFAULT_TENANT_ID);
    }

    /**
     * @param tenantID the ID of the tenant
     * @return the {@link LocalDictionaryCache} corresponding to a given tenant ID.
     */
    public LocalDictionaryCache getDictionaryCache(String tenantID) {
        return getCustomDictionaryHolder(tenantID).getDictionaryCache();
    }

    /**
     * Reload the category from local registry for a given tenant ID. This method is typically called following category or
     * dictionary enrichments.
     * 
     * @param tenantID the ID of the tenant
     */
    public void reloadCategoriesFromRegistry(String tenantID) {
        LOGGER.info("Reload categories from local registry.");
        getCustomDictionaryHolder(tenantID).reloadCategoryMetadata();
    }

    /**
     * Reload the category from local registry for the default tenant. This method is typically called following category or
     * dictionary enrichments.
     */
    public void reloadCategoriesFromRegistry() {
        reloadCategoriesFromRegistry(DEFAULT_TENANT_ID);
    }

    private void loadRegisteredCategories() throws IOException, URISyntaxException {
        // read local DD categories
        LOGGER.info("Loading categories from local registry.");
        final File categorySubFolder = new File(localRegistryPath + File.separator + SHARED_FOLDER_NAME + File.separator
                + PRODUCTION_FOLDER_NAME + File.separator + METADATA_SUBFOLDER_NAME);
        loadBaseIndex(categorySubFolder, METADATA_SUBFOLDER_NAME);
        if (categorySubFolder.exists()) {
            sharedMetadata = CategoryMetadataUtils.loadMetadataFromIndex(FSDirectory.open(categorySubFolder));
        }

        // extract initial DD categories if not present
        final File dictionarySubFolder = new File(localRegistryPath + File.separator + SHARED_FOLDER_NAME + File.separator
                + PRODUCTION_FOLDER_NAME + File.separator + DICTIONARY_SUBFOLDER_NAME);
        loadBaseIndex(dictionarySubFolder, DICTIONARY_SUBFOLDER_NAME);

        // extract initial KW categories if not present
        final File keywordSubFolder = new File(localRegistryPath + File.separator + SHARED_FOLDER_NAME + File.separator
                + PRODUCTION_FOLDER_NAME + File.separator + KEYWORD_SUBFOLDER_NAME);
        loadBaseIndex(keywordSubFolder, KEYWORD_SUBFOLDER_NAME);

        // read local RE categories
        final File regexRegistryFile = new File(localRegistryPath + File.separator + SHARED_FOLDER_NAME + File.separator
                + PRODUCTION_FOLDER_NAME + File.separator + REGEX_SUBFOLDER_NAME + File.separator + REGEX_CATEGRIZER_FILE_NAME);
        loadBaseRegex(regexRegistryFile);
    }

    private void loadBaseRegex(final File regexRegistryFile) throws IOException {
        if (!regexRegistryFile.exists()) {
            // load provided RE into registry
            InputStream is = CategoryRecognizer.class.getResourceAsStream(CategoryRecognizerBuilder.DEFAULT_RE_PATH);
            StringBuilder sb = new StringBuilder();
            for (String line : IOUtils.readLines(is)) {
                sb.append(line);
            }
            JSONObject obj = new JSONObject(sb.toString());
            JSONArray array = obj.getJSONArray("classifiers");
            regexRegistryFile.getParentFile().mkdirs();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(regexRegistryFile);
                IOUtils.write(array.toString(2), fos);
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

    private void loadInitialCategories() throws IOException, URISyntaxException {
        sharedMetadata = CategoryMetadataUtils.loadMetadataFromIndex(ClassPathDirectory.open(getMetadataURI()));
    }

    private void loadBaseIndex(final File destSubFolder, String sourceSubFolder) throws IOException, URISyntaxException {
        synchronized (indexExtractionLock) {
            if (!destSubFolder.exists()) {
                final URI indexSourceURI = this.getClass().getResource("/" + sourceSubFolder).toURI();
                try (final Directory srcDir = ClassPathDirectory.open(indexSourceURI)) {
                    if (usingLocalCategoryRegistry && !destSubFolder.exists()) {
                        DictionaryUtils.rewriteIndex(srcDir, destSubFolder);
                    }
                }
            }
        }
    }

    /**
     * List all categories.
     * 
     * @return collection of category objects
     */
    public Collection<DQCategory> listCategories() {
        return getCustomDictionaryHolder().listCategories();
    }

    /**
     * List all categories.
     * 
     * @param includeOpenCategories whether include incomplete categories
     * @return collection of category objects
     */
    public Collection<DQCategory> listCategories(boolean includeOpenCategories) {
        return getCustomDictionaryHolder().listCategories(includeOpenCategories);
    }

    /**
     * List all categories of a given {@link CategoryType}.
     * 
     * @param type the given category type
     * @return collection of category objects of the given type
     */
    public List<DQCategory> listCategories(CategoryType type) {
        return getCustomDictionaryHolder().listCategories(type);
    }

    /**
     * Get the full map between category ID and category metadata.
     */
    public Map<String, DQCategory> getSharedCategoryMetadata() {
        return sharedMetadata;
    }

    /**
     * Getter for sharedDataDictDirectory.
     */
    public Directory getSharedDataDictDirectory() {
        if (sharedDataDictDirectory == null) {
            try {
                sharedDataDictDirectory = ClassPathDirectory.open(getDictionaryURI());
            } catch (URISyntaxException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return sharedDataDictDirectory;
    }

    /**
     * Get the full map between category ID and category metadata for the default tenant.
     */
    public Map<String, DQCategory> getCategoryMetadataMap() {
        return getCustomDictionaryHolder().getMetadata();
    }

    /**
     * Get the category object by its technical ID for the default tenant.
     * 
     * @param catId the technical ID of the category
     * @return the category object
     */
    public DQCategory getCategoryMetadataById(String catId) {
        return getCustomDictionaryHolder().getCategoryMetadataById(catId);
    }

    /**
     * Get the category object by its functional ID (aka. name) for the default tenant.
     * 
     * @param catName the functional ID (aka. name)
     * @return the category object
     */
    public DQCategory getCategoryMetadataByName(String catName) {
        return getCustomDictionaryHolder().getCategoryMetadataByName(catName);
    }

    /**
     * get instance of UserDefinedClassifier
     * 
     * @param refresh whether classifiers should be reloaded from local json file
     */
    public UserDefinedClassifier getRegexClassifier(boolean refresh) throws IOException {
        if (!usingLocalCategoryRegistry) {
            return UDCategorySerDeser.getRegexClassifier();
        }

        // load regexes from local registry
        if (sharedRegexClassifier == null || refresh) {
            final File regexRegistryFile = new File(
                    localRegistryPath + File.separator + SHARED_FOLDER_NAME + File.separator + PRODUCTION_FOLDER_NAME
                            + File.separator + REGEX_SUBFOLDER_NAME + File.separator + REGEX_CATEGRIZER_FILE_NAME);

            if (!regexRegistryFile.exists()) {
                loadBaseRegex(regexRegistryFile);
            }

            sharedRegexClassifier = UDCategorySerDeser.readJsonFile(regexRegistryFile.toURI());
        }
        return sharedRegexClassifier;
    }

    /**
     * get URI of local category metadata
     */
    private URI getMetadataURI() throws URISyntaxException {
        if (usingLocalCategoryRegistry) {
            return Paths.get(localRegistryPath, SHARED_FOLDER_NAME, PRODUCTION_FOLDER_NAME, METADATA_SUBFOLDER_NAME).toUri();
        } else {
            return CategoryRecognizerBuilder.class.getResource(CategoryRecognizerBuilder.DEFAULT_METADATA_PATH).toURI();
        }
    }

    /**
     * get URI of local dictionary index
     */
    public URI getDictionaryURI() throws URISyntaxException {
        if (usingLocalCategoryRegistry) {
            return Paths.get(localRegistryPath, SHARED_FOLDER_NAME, PRODUCTION_FOLDER_NAME, DICTIONARY_SUBFOLDER_NAME).toUri();
        } else {
            return CategoryRecognizerBuilder.class.getResource(CategoryRecognizerBuilder.DEFAULT_DD_PATH).toURI();
        }
    }

    /**
     * get URI of local keyword index
     */
    public URI getKeywordURI() throws URISyntaxException {
        if (usingLocalCategoryRegistry) {
            return Paths.get(localRegistryPath, SHARED_FOLDER_NAME, PRODUCTION_FOLDER_NAME, KEYWORD_SUBFOLDER_NAME).toUri();
        } else {
            return CategoryRecognizerBuilder.class.getResource(CategoryRecognizerBuilder.DEFAULT_KW_PATH).toURI();
        }
    }

    /**
     * get URI of local regexes
     */
    public URI getRegexURI() throws URISyntaxException {
        if (usingLocalCategoryRegistry) {
            return Paths.get(localRegistryPath, SHARED_FOLDER_NAME, PRODUCTION_FOLDER_NAME, REGEX_SUBFOLDER_NAME,
                    REGEX_CATEGRIZER_FILE_NAME).toUri();
        } else {
            return CategoryRecognizerBuilder.class.getResource(CategoryRecognizerBuilder.DEFAULT_RE_PATH).toURI();
        }
    }

    /**
     * Get CustomDictioanryHolder instance for the given tenant ID.
     * 
     * @param tenantID the ID of the tenant.
     */
    public CustomDictionaryHolder getCustomDictionaryHolder(String tenantID) {
        CustomDictionaryHolder cdh = customDictionaryHolderMap.get(tenantID);
        if (cdh == null) {
            cdh = new CustomDictionaryHolder(tenantID);
            customDictionaryHolderMap.put(tenantID, cdh);
        }
        return cdh;
    }

    /**
     * Get CustomDictioanryHolder instance for the default tenant.
     */
    public CustomDictionaryHolder getCustomDictionaryHolder() {
        return getCustomDictionaryHolder(DEFAULT_TENANT_ID);
    }

    /**
     * Remove the CustomDictionaryHolder for a given tenant ID.
     * 
     * @param tenantID the ID of the tenant.
     */
    public void removeCustomDictionaryHolder(String tenantID) {
        CustomDictionaryHolder cdh = customDictionaryHolderMap.get(tenantID);
        if (cdh != null) {
            cdh.closeDictionaryAccess();
            File folder = new File(localRegistryPath + File.separator + tenantID);
            try {
                FileUtils.deleteDirectory(folder);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
            customDictionaryHolderMap.remove(tenantID);
        }
    }
}
