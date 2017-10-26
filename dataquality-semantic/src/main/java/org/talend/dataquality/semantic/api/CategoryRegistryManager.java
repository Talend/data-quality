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

import java.io.*;
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
 * A local category registry is composed by the following subfolders:
 * <ul>
 * <li><b>category:</b> lucene index containing metadata of all categories</li>
 * <li><b>index/dictionary:</b> lucene index containing dictionary documents</li>
 * <li><b>index/keyword:</b> lucene index containing keyword documents</li>
 * <li><b>regex:</b> json file containing all categories that can be recognized by regex patterns and eventual subvalidators</li>
 * </ul>
 * In each of the above subfolders, there is still a level of subfolders representing different contexts. The default context name
 * is "default".
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

    public static final String DEFAULT_CONTEXT_NAME = "t_default";

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
     * @return the {@link LocalDictionaryCache} corresponding to the default context.
     */
    public LocalDictionaryCache getDictionaryCache() {
        return getDictionaryCache(DEFAULT_CONTEXT_NAME);
    }

    /**
     * @param contextName name of the context
     * @return the {@link LocalDictionaryCache} corresponding to a given context.
     */
    public LocalDictionaryCache getDictionaryCache(String contextName) {
        return getCustomDictionaryHolder(contextName).getDictionaryCache();
    }

    /**
     * Reload the category from local registry for a given context. This method is typically called following category or
     * dictionary enrichments.
     */
    public void reloadCategoriesFromRegistry(String context) {
        LOGGER.info("Reload categories from local registry.");
        getCustomDictionaryHolder(context).reloadCategoryMetadata();
    }

    /**
     * Reload the category from local registry for the default context. This method is typically called following category or
     * dictionary enrichments.
     */
    public void reloadCategoriesFromRegistry() {
        reloadCategoriesFromRegistry(DEFAULT_CONTEXT_NAME);
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

    private void loadBaseRegex(final File regexRegistryFile) throws IOException, FileNotFoundException {
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
     * Get the full map between category ID and category metadata from the default context.
     */
    public Map<String, DQCategory> getCategoryMetadataMap() {
        return getCustomDictionaryHolder().getMetadata();
    }

    /**
     * Get the category object by its technical ID from the default context.
     * 
     * @param catId the technical ID of the category
     * @return the category object
     */
    public DQCategory getCategoryMetadataById(String catId) {
        return getCustomDictionaryHolder().getCategoryMetadataById(catId);
    }

    /**
     * Get the category object by its functional ID (aka. name) from the default context.
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
    public URI getMetadataURI() throws URISyntaxException {
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

    public CustomDictionaryHolder getCustomDictionaryHolder(String contextName) {
        CustomDictionaryHolder cdh = customDictionaryHolderMap.get(contextName);
        if (cdh == null) {
            cdh = new CustomDictionaryHolder(contextName);
            customDictionaryHolderMap.put(contextName, cdh);
        }
        return cdh;
    }

    public CustomDictionaryHolder getCustomDictionaryHolder() {
        return getCustomDictionaryHolder(DEFAULT_CONTEXT_NAME);
    }

    public void removeCustomDictionaryHolder(String contextName) {
        CustomDictionaryHolder cdh = customDictionaryHolderMap.get(contextName);
        if (cdh != null) {
            cdh.closeDictionaryAccess();
            File folder = new File(localRegistryPath + File.separator + contextName);
            try {
                FileUtils.deleteDirectory(folder);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
            customDictionaryHolderMap.remove(contextName);
        }
    }
}
