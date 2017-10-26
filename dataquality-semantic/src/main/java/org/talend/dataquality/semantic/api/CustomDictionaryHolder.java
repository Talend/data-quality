package org.talend.dataquality.semantic.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.talend.dataquality.semantic.api.internal.CustomDocumentIndexAccess;
import org.talend.dataquality.semantic.api.internal.CustomMetadataIndexAccess;
import org.talend.dataquality.semantic.api.internal.CustomRegexClassifierAccess;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedCategory;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;

public class CustomDictionaryHolder {

    private static final Logger LOGGER = Logger.getLogger(CustomDictionaryHolder.class);

    private Map<String, DQCategory> metadata;

    private Directory dataDictDirectory;

    private UserDefinedClassifier regexClassifier;

    private CustomMetadataIndexAccess customMetadataIndexAccess;

    private CustomDocumentIndexAccess customDataDictIndexAccess;

    private CustomRegexClassifierAccess customRegexClassifierAccess;

    private LocalDictionaryCache localDictionaryCache;

    private String contextName;

    public CustomDictionaryHolder(String contextName) {
        this.contextName = contextName;
        checkCustomFolders();
    }

    private void checkCustomFolders() {
        File metadataFolder = new File(getMetadataFolderPath());
        if (metadataFolder.exists()) {
            ensureMetadataIndexAccess();
            File dataDictFolder = new File(getDataDictFolderPath());
            if (dataDictFolder.exists()) {
                ensureDataDictIndexAccess();
            }
        }
        // make a copy of shared regex classifiers
        ensureRegexClassifierAccess();
    }

    public String getContextName() {
        return contextName;
    }

    private String getMetadataFolderPath() {
        return CategoryRegistryManager.getLocalRegistryPath() + File.separator + contextName + File.separator
                + CategoryRegistryManager.PRODUCTION_FOLDER_NAME + File.separator
                + CategoryRegistryManager.METADATA_SUBFOLDER_NAME;
    }

    private String getDataDictFolderPath() {
        return CategoryRegistryManager.getLocalRegistryPath() + File.separator + contextName + File.separator
                + CategoryRegistryManager.PRODUCTION_FOLDER_NAME + File.separator
                + CategoryRegistryManager.DICTIONARY_SUBFOLDER_NAME;
    }

    private String getRegexClassifierFolderPath() {
        return CategoryRegistryManager.getLocalRegistryPath() + File.separator + contextName + File.separator
                + CategoryRegistryManager.PRODUCTION_FOLDER_NAME + File.separator + CategoryRegistryManager.REGEX_SUBFOLDER_NAME
                + File.separator + CategoryRegistryManager.REGEX_CATEGRIZER_FILE_NAME;
    }

    public Map<String, DQCategory> getMetadata() {
        if (metadata == null) {
            return CategoryRegistryManager.getInstance().getSharedCategoryMetadata();
        }
        return metadata;
    }

    public Directory getDataDictDirectory() {
        if (dataDictDirectory == null) {
            ensureDataDictIndexAccess();
        }
        return dataDictDirectory;
    }

    public UserDefinedClassifier getRegexClassifier() throws IOException {
        // return shared regexClassifier if NULL
        if (regexClassifier == null) {
            return CategoryRegistryManager.getInstance().getRegexClassifier(false);
        }
        return regexClassifier;
    }

    private synchronized void ensureMetadataIndexAccess() {
        if (metadata == null) {
            LOGGER.info("Initialize custom metadata access for " + contextName);
            String metadataIndexPath = getMetadataFolderPath();
            File folder = new File(metadataIndexPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            try {
                customMetadataIndexAccess = new CustomMetadataIndexAccess(FSDirectory.open(folder));
                metadata = customMetadataIndexAccess.readCategoryMedatada();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private synchronized void ensureDataDictIndexAccess() {
        if (customDataDictIndexAccess == null) {
            LOGGER.info("Initialize custom data dict access for " + contextName);
            String dataDictIndexPath = getDataDictFolderPath();
            File folder = new File(dataDictIndexPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            try {
                dataDictDirectory = FSDirectory.open(folder);
                customDataDictIndexAccess = new CustomDocumentIndexAccess(dataDictDirectory);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public void createCategory(DQCategory category) {
        ensureMetadataIndexAccess();
        customMetadataIndexAccess.createCategory(category);
        customMetadataIndexAccess.commitChanges();
        metadata = customMetadataIndexAccess.readCategoryMedatada();
    }

    public void updateCategory(DQCategory category) {
        if (CategoryType.DICT.equals(category.getType())) {
            DQCategory dqCat = getMetadata().get(category.getId());
            if (dqCat != null && Boolean.FALSE.equals(dqCat.getModified())) {
                // copy all existing documents from shared directory to custom directory
                ensureDataDictIndexAccess();
                customDataDictIndexAccess.copyBaseDocumentsFromSharedDirectory(dqCat);
            }
        }

        ensureMetadataIndexAccess();
        customMetadataIndexAccess.insertOrUpdateCategory(category);
        customMetadataIndexAccess.commitChanges();
        metadata = customMetadataIndexAccess.readCategoryMedatada();
    }

    public void deleteCategory(DQCategory category) {
        ensureMetadataIndexAccess();
        customMetadataIndexAccess.deleteCategory(category);
        customMetadataIndexAccess.commitChanges();
        metadata = customMetadataIndexAccess.readCategoryMedatada();
    }

    public void reloadCategoryMetadata() {
        checkCustomFolders();
        if (customMetadataIndexAccess != null) {
            metadata = customMetadataIndexAccess.readCategoryMedatada();
        }
        if (customRegexClassifierAccess != null) {
            regexClassifier = customRegexClassifierAccess.readUserDefinedClassifier();
        }
    }

    public void beforeRepublish() {
        // nothing to do
    }

    public void afterRepublish() {
        reloadCategoryMetadata();
    }

    public void addDataDictDocument(List<DQDocument> documents) {
        ensureDataDictIndexAccess();
        customDataDictIndexAccess.createDocument(documents);
        customDataDictIndexAccess.commitChanges();
    }

    void closeDictionaryAccess() {
        try {
            if (customMetadataIndexAccess != null) {
                customMetadataIndexAccess.close();
            }
            if (customDataDictIndexAccess != null) {
                customDataDictIndexAccess.close();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            customMetadataIndexAccess = null;
            customDataDictIndexAccess = null;
            customRegexClassifierAccess = null;
        }
    }

    private synchronized void ensureRegexClassifierAccess() {
        if (customRegexClassifierAccess == null) {
            LOGGER.info("Initialize custom regex classifier access for " + contextName);
            customRegexClassifierAccess = new CustomRegexClassifierAccess(this);
            regexClassifier = customRegexClassifierAccess.readUserDefinedClassifier();
        }
    }

    public void addRegexCategory(DQCategory category) {
        ensureRegexClassifierAccess();
        UserDefinedCategory regEx = DictionaryUtils.regexClassifierfromDQCategory(category);
        customRegexClassifierAccess.createRegex(regEx);
        regexClassifier = customRegexClassifierAccess.readUserDefinedClassifier();

        ensureMetadataIndexAccess();
        customMetadataIndexAccess.createCategory(category);
        customMetadataIndexAccess.commitChanges();
        metadata = customMetadataIndexAccess.readCategoryMedatada();
    }

    public LocalDictionaryCache getDictionaryCache() {
        if (localDictionaryCache == null) {
            localDictionaryCache = new LocalDictionaryCache(this);
        }
        return localDictionaryCache;
    }

    public void closeDictionaryCache() {
        if (localDictionaryCache != null) {
            localDictionaryCache.close();
        }
    }

    /**
     * List all categories.
     * 
     * @return collection of category objects
     */
    public Collection<DQCategory> listCategories() {
        return getMetadata().values();
    }

    /**
     * List all categories.
     * 
     * @param includeOpenCategories whether include incomplete categories
     * @return collection of category objects
     */
    public Collection<DQCategory> listCategories(boolean includeOpenCategories) {
        if (includeOpenCategories) {
            return getMetadata().values();
        } else {
            List<DQCategory> catList = new ArrayList<>();
            for (DQCategory dqCat : getMetadata().values()) {
                if (dqCat.getCompleteness()) {
                    catList.add(dqCat);
                }
            }
            return catList;
        }
    }

    /**
     * List all categories of a given {@link CategoryType}.
     * 
     * @param type the given category type
     * @return collection of category objects of the given type
     */
    public List<DQCategory> listCategories(CategoryType type) {
        List<DQCategory> catList = new ArrayList<>();
        for (DQCategory dqCat : getMetadata().values()) {
            if (type.equals(dqCat.getType())) {
                catList.add(dqCat);
            }
        }
        return catList;
    }

    /**
     * Get the category object by its technical ID.
     * 
     * @param catId the technical ID of the category
     * @return the category object
     */
    public DQCategory getCategoryMetadataById(String catId) {
        return getMetadata().get(catId);
    }

    /**
     * Get the category object by its functional ID (aka. name).
     * 
     * @param catName the functional ID (aka. name)
     * @return the category object
     */
    public DQCategory getCategoryMetadataByName(String catName) {
        for (DQCategory cat : getMetadata().values()) {
            if (cat.getName().equals(catName)) {
                return cat;
            }
        }
        return null;
    }

    public IndexWriter getCategoryIndexWriter() throws IOException {
        ensureMetadataIndexAccess();
        return customMetadataIndexAccess.getWriter();
    }

    public IndexWriter getDataDictIndexWriter() throws IOException {
        ensureDataDictIndexAccess();
        return customDataDictIndexAccess.getWriter();
    }

    /**
     * copy the publish directory in the production directory once the republish is finished
     * 
     * @param luceneFolder
     * @throws IOException
     */
    public synchronized void publishDirectory(String luceneFolder) throws IOException {
        File stagingIndexes = new File(
                luceneFolder + File.separator + contextName + File.separator + CategoryRegistryManager.REPUBLISH_FOLDER_NAME);
        if (stagingIndexes.exists()) {
            File productionIndexes = new File(luceneFolder + File.separator + contextName + File.separator
                    + CategoryRegistryManager.PRODUCTION_FOLDER_NAME);

            File backup = new File(productionIndexes.getPath() + ".old");
            if (!backup.exists()) {
                LOGGER.info("[Post Republish] backup prod");
                FileUtils.copyDirectory(productionIndexes, backup);

                try {
                    LOGGER.info("[Post Republish] insert staging directory into prod");
                    customMetadataIndexAccess.copyStagingContent(
                            stagingIndexes.getAbsolutePath() + File.separator + CategoryRegistryManager.METADATA_SUBFOLDER_NAME);
                    customDataDictIndexAccess.copyStagingContent(stagingIndexes.getAbsolutePath() + File.separator
                            + CategoryRegistryManager.DICTIONARY_SUBFOLDER_NAME);
                    customRegexClassifierAccess.copyStagingContent(
                            stagingIndexes.getAbsolutePath() + File.separator + CategoryRegistryManager.REGEX_SUBFOLDER_NAME
                                    + File.separator + CategoryRegistryManager.REGEX_CATEGRIZER_FILE_NAME);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }

                LOGGER.info("[Post Republish] delete backup");
                FileUtils.deleteDirectory(backup);

                LOGGER.info("[Post Republish] delete staging contents");
                FileUtils.deleteDirectory(stagingIndexes);
            }
        }
    }
}
