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
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.core.Tenant;
import org.talend.dataquality.semantic.classifier.custom.UDCategorySerDeser;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.index.ClassPathDirectory;
import org.talend.dataquality.semantic.index.DictionarySearchMode;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Singleton class providing API for local category registry management.
 * <p>
 * A local category registry is composed by the following folders:
 * <ul>
 * <li><b>shared/prod/:</b> shared dictionaries between tenants, provided by Talend</li>
 * <li><b>t_id/prod/:</b> tenant specific dictionaries</li>
 * </ul>
 * <p>
 * Inside each of the above folder, the following subfolders can be found:
 * <ul>
 * <li><b>metadata:</b> lucene index containing metadata of all categories. In the tenant specific folder, the metadata of all
 * provided categories are copied from shared metadata.</li>
 * <li><b>dictionary:</b> lucene index containing dictionary documents. In the tenant specific folder, we only include the
 * user-defined data dictionaries, and make the copy for only the modified categories provided by Talend</li>
 * <li><b>keyword:</b> lucene index containing keyword documents. This folder does not exist in tenant specific folder as the
 * modification is not allowed for these categories</li>
 * <li><b>regex:</b> json file containing all categories that can be recognized by regex patterns and eventual subvalidators</li>
 * </ul>
 */
public class CategoryRegistryManager {

    public static final String METADATA_SUBFOLDER_NAME = "metadata";

    public static final String DICTIONARY_SUBFOLDER_NAME = "dictionary";

    public static final String KEYWORD_SUBFOLDER_NAME = "keyword";

    public static final String REGEX_SUBFOLDER_NAME = "regex";

    public static final String REGEX_CATEGORIZER_FILE_NAME = "categorizer.json";

    public static final String SHARED_FOLDER_NAME = "shared";

    public static final String PRODUCTION_FOLDER_NAME = "prod";

    public static final String REPUBLISH_FOLDER_NAME = "republish";

    public static final String DEFAULT_TENANT_ID = "t_default";

    // must use slash here
    public static final String DEFAULT_RE_PATH = "/" + REGEX_SUBFOLDER_NAME + "/" + REGEX_CATEGORIZER_FILE_NAME;

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryRegistryManager.class);

    private static final Map<String, CustomDictionaryHolder> customDictionaryHolderMap = new HashMap<>();

    private static final String DEFAULT_LOCAL_REGISTRY_PATH = System.getProperty("user.home") + "/.talend/dataquality/semantic";

    private static final Object indexExtractionLock = new Object();

    private static CategoryRegistryManager instance;

    /**
     * Whether the local category registry will be used.
     * Default value is false, which means only initial categories are loaded. This is mostly useful for unit tests.
     * More often, the value is set to true when the localRegistryPath is configured. see
     * {@link CategoryRegistryManager#setLocalRegistryPath(String)}
     */
    private static boolean usingLocalCategoryRegistry = false;

    private static String localRegistryPath = DEFAULT_LOCAL_REGISTRY_PATH;

    /**
     * Map between category ID and the object containing its metadata.
     */
    private Map<String, DQCategory> sharedMetadata = new LinkedHashMap<>();

    private UserDefinedClassifier sharedRegexClassifier;

    private Directory sharedDataDictDirectory;

    private Directory sharedKeywordDirectory;

    private ObjectMapper mapper = new ObjectMapper();

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

    static boolean isUsingLocalCategoryRegistry() {
        return usingLocalCategoryRegistry;
    }

    public static void setUsingLocalCategoryRegistry(boolean b) {
        usingLocalCategoryRegistry = b;
    }

    /**
     * @return the path of local registry.
     */
    public static String getLocalRegistryPath() {
        return localRegistryPath;
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
            LOGGER.warn("Cannot set an empty path as local registry location. Use default one: " + localRegistryPath);
        }
    }

    /**
     * @return the {@link LocalDictionaryCache} corresponding to the default tenant ID.
     */
    public LocalDictionaryCache getDictionaryCache() {
        return getCustomDictionaryHolder().getDictionaryCache();
    }

    /**
     * Reload the category from local registry for a given tenant ID. This method is typically called following category
     * or
     * dictionary enrichments.
     */
    public void reloadCategoriesFromRegistry() {
        LOGGER.info("Reload categories from local registry.");
        getCustomDictionaryHolder().reloadCategoryMetadata();
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
                + PRODUCTION_FOLDER_NAME + File.separator + REGEX_SUBFOLDER_NAME + File.separator + REGEX_CATEGORIZER_FILE_NAME);
        loadBaseRegex(regexRegistryFile);
    }

    private void loadBaseRegex(final File regexRegistryFile) throws IOException {
        if (!regexRegistryFile.exists()) {
            // load provided RE into registry
            InputStream is = this.getClass().getResourceAsStream(DEFAULT_RE_PATH);
            StringBuilder sb = new StringBuilder();
            for (String line : IOUtils.readLines(is)) {
                sb.append(line);
            }
            regexRegistryFile.getParentFile().mkdirs();
            FileOutputStream fos = null;
            try {
                JsonNode objNode = mapper.readTree(sb.toString());
                JsonNode array = objNode.get("classifiers");
                fos = new FileOutputStream(regexRegistryFile);
                IOUtils.write(array.toString(), fos);
            } catch (JsonMappingException jsonE) {
                LOGGER.error(jsonE.getMessage(), jsonE);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
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

            // extract base index only if needed
            if (!destSubFolder.exists()) {

                boolean baseIndexExtracted = false;

                // because the classpath can have multiple 'metadata' packages (especially with spring boot uber jar
                // packaging)
                // we need to iterate over the potential resources
                final List<URL> potentialResources = Collections
                        .list(this.getClass().getClassLoader().getResources(sourceSubFolder));
                for (URL url : potentialResources) {

                    final URI uri = url.toURI();
                    LOGGER.info("trying to load base index from " + uri.toString());

                    // try the potential resource one by one
                    try (final Directory srcDir = ClassPathDirectory.open(uri)) {
                        if (usingLocalCategoryRegistry && !destSubFolder.exists()) {
                            DictionaryUtils.rewriteIndex(srcDir, destSubFolder);
                            // if successful, let's not try the remaining ones
                            LOGGER.info("base index loaded from " + uri.toString());
                            baseIndexExtracted = true;
                            break;
                        }
                    } catch (IllegalArgumentException iae) {
                        // that's expected as multiple 'metadata' packages can exist within the classpath
                        LOGGER.debug("could not load base index from " + uri.toString());
                    }
                }

                // if base index could not be loaded, let's make a nice error message
                if (usingLocalCategoryRegistry && !destSubFolder.exists() && !baseIndexExtracted) {
                    final StringBuilder error = new StringBuilder(100);
                    error.append("Could not load base index out of theses locations : [\n");
                    potentialResources.forEach(pr -> error.append('\t').append(pr.toString()).append('\n'));
                    error.append(']');
                    throw new IllegalArgumentException(error.toString());
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
     * Getter for sharedKeywordDirectory.
     */
    public Directory getSharedKeywordDirectory() {
        if (sharedKeywordDirectory == null) {
            try {
                sharedKeywordDirectory = ClassPathDirectory.open(getKeywordURI());
            } catch (URISyntaxException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return sharedKeywordDirectory;
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
     */
    public UserDefinedClassifier getRegexClassifier() {
        if (!usingLocalCategoryRegistry) {
            try {
                return UDCategorySerDeser.getRegexClassifier();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else {
            // load regexes from local registry
            if (sharedRegexClassifier == null) {
                final File regexRegistryFile = new File(
                        localRegistryPath + File.separator + SHARED_FOLDER_NAME + File.separator + PRODUCTION_FOLDER_NAME
                                + File.separator + REGEX_SUBFOLDER_NAME + File.separator + REGEX_CATEGORIZER_FILE_NAME);

                try {
                    if (!regexRegistryFile.exists()) {
                        loadBaseRegex(regexRegistryFile);
                    }

                    sharedRegexClassifier = UDCategorySerDeser.readJsonFile(regexRegistryFile.toURI());
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
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
            // must use slash here
            return this.getClass().getResource("/" + METADATA_SUBFOLDER_NAME + "/").toURI();
        }
    }

    /**
     * get URI of local dictionary index
     */
    public URI getDictionaryURI() throws URISyntaxException {
        if (usingLocalCategoryRegistry) {
            return Paths.get(localRegistryPath, SHARED_FOLDER_NAME, PRODUCTION_FOLDER_NAME, DICTIONARY_SUBFOLDER_NAME).toUri();
        } else {
            // must use slash here
            return this.getClass().getResource("/" + DICTIONARY_SUBFOLDER_NAME + "/").toURI();
        }
    }

    /**
     * get URI of local keyword index
     */
    public URI getKeywordURI() throws URISyntaxException {
        if (usingLocalCategoryRegistry) {
            return Paths.get(localRegistryPath, SHARED_FOLDER_NAME, PRODUCTION_FOLDER_NAME, KEYWORD_SUBFOLDER_NAME).toUri();
        } else {
            // must use slash here
            return this.getClass().getResource("/" + KEYWORD_SUBFOLDER_NAME + "/").toURI();
        }
    }

    /**
     * get URI of local regexes
     */
    public URI getRegexURI() throws URISyntaxException {
        if (usingLocalCategoryRegistry) {
            return Paths.get(localRegistryPath, SHARED_FOLDER_NAME, PRODUCTION_FOLDER_NAME, REGEX_SUBFOLDER_NAME,
                    REGEX_CATEGORIZER_FILE_NAME).toUri();
        } else {
            return this.getClass().getResource(DEFAULT_RE_PATH).toURI();
        }
    }

    /**
     * Get CustomDictioanryHolder instance for the default tenant.
     */
    public CustomDictionaryHolder getCustomDictionaryHolder() {
        Optional<Tenant> tenant = TenancyContextHolder.getContext().getOptionalTenant();
        if (tenant.isPresent()) {
            Object identity = tenant.get().getIdentity();
            if (identity != null && !"null".equals(String.valueOf(identity))) {
                return getCustomDictionaryHolder(String.valueOf(identity));
            }
        }
        return getCustomDictionaryHolder(DEFAULT_TENANT_ID);
    }

    public synchronized CustomDictionaryHolder getCustomDictionaryHolder(String tenantID) {
        CustomDictionaryHolder cdh = customDictionaryHolderMap.get(tenantID);
        if (cdh == null) {
            LOGGER.info("Instantiate CustomDictionaryHolder for [" + tenantID + "]");
            cdh = new CustomDictionaryHolder(tenantID);
            customDictionaryHolderMap.put(tenantID, cdh);
        }
        return cdh;
    }

    /**
     * @param input the input value
     * @param categoryName the category name
     * @param similarity the threshold value, the compared score must be >= similarity
     * @return most similar value from customer dictionary or share dictionary
     */
    public String findMostSimilarValue(String input, String categoryName, double similarity) {
        LuceneIndex index = getLuceneIndex(categoryName);
        if (index == null) {
            return input;
        }
        return index.findMostSimilarFieldInCategory(input, categoryName, similarity);
    }

    /**
     * @param categoryName
     * @return Get a custom or shared LuncenIndex according to the category metadata
     */
    LuceneIndex getLuceneIndex(String categoryName) {
        DQCategory dqCategory = getCategoryMetadataByName(categoryName);
        if (dqCategory != null) {
            Directory dataDictDirectory = null;
            if (dqCategory.getModified()) { // get a custom directory if the Category is modified
                dataDictDirectory = getCustomDictionaryHolder().getDataDictDirectory();
            } else { // otherwise, get shared directory
                dataDictDirectory = getSharedDataDictDirectory();
            }
            return new LuceneIndex(dataDictDirectory, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
        }
        return null;
    }
}
