package org.talend.dataquality.semantic.api;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.talend.dataquality.semantic.api.internal.CustomDocumentIndexAccess;
import org.talend.dataquality.semantic.api.internal.CustomMetadataIndexAccess;
import org.talend.dataquality.semantic.api.internal.CustomRegexClassifierAccess;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedCategory;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
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

    private String contextName;

    public CustomDictionaryHolder(String contextName) {
        this.contextName = contextName;
    }

    public String getContextName() {
        return contextName;
    }

    public Map<String, DQCategory> getMetadata() {
        if (metadata == null) {
            return CategoryRegistryManager.getInstance().getCategoryMetadataMap();
        }
        return metadata;
    }

    public Directory getDataDictDirectory() {
        // allow to return NULL
        return dataDictDirectory;
    }

    public UserDefinedClassifier getRegexClassifier() throws IOException {
        // return shared regexClassifier if NULL
        if (regexClassifier == null) {
            return CategoryRegistryManager.getInstance().getRegexClassifier(false);
        }
        return regexClassifier;
    }

    private void ensureMetadataIndexAccess() {
        if (metadata == null) {
            // clone shared metadata
            metadata = new HashMap<>(CategoryRegistryManager.getInstance().getCategoryMetadataMap());
            String metadataIndexPath = CategoryRegistryManager.getLocalRegistryPath() + File.separator + contextName
                    + File.separator + CategoryRegistryManager.PRODUCTION_FOLDER_NAME + File.separator
                    + CategoryRegistryManager.METADATA_SUBFOLDER_NAME;
            File folder = new File(metadataIndexPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            try {
                customMetadataIndexAccess = new CustomMetadataIndexAccess(FSDirectory.open(folder));
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void ensureDataDictIndexAccess() {
        if (customDataDictIndexAccess == null) {
            String dataDictIndexPath = CategoryRegistryManager.getLocalRegistryPath() + File.separator + contextName
                    + File.separator + CategoryRegistryManager.PRODUCTION_FOLDER_NAME + File.separator
                    + CategoryRegistryManager.DICTIONARY_SUBFOLDER_NAME;
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
        customMetadataIndexAccess.commitChangesAndCloseWriter();
    }

    public void updateCategory(DQCategory category) {
        ensureMetadataIndexAccess();
        customMetadataIndexAccess.insertOrUpdateCategory(category);
        customMetadataIndexAccess.commitChangesAndCloseWriter();
    }

    public void deleteCategory(DQCategory category) {
        ensureMetadataIndexAccess();
        customMetadataIndexAccess.deleteCategory(category);
        customMetadataIndexAccess.commitChangesAndCloseWriter();
    }

    public void reloadCategoryMetadata() {
        if (customMetadataIndexAccess != null) {
            metadata = customMetadataIndexAccess.readCategoryMedatada();
        }
        if (customRegexClassifierAccess != null) {
            regexClassifier = customRegexClassifierAccess.readUserDefinedClassifier();
        }
    }

    public void addDataDictDocument(List<DQDocument> documents) {
        ensureDataDictIndexAccess();
        customDataDictIndexAccess.createDocument(documents);
        customDataDictIndexAccess.commitChangesAndCloseWriter();
    }

    public void close() {
        try {
            if (customMetadataIndexAccess != null) {
                customMetadataIndexAccess.close();
            }
            if (customDataDictIndexAccess != null) {
                customDataDictIndexAccess.close();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void ensureRegexClassifierAccess() {
        if (customRegexClassifierAccess == null) {
            customRegexClassifierAccess = new CustomRegexClassifierAccess(this);
            regexClassifier = customRegexClassifierAccess.readUserDefinedClassifier();
        }
    }

    public void addRegexCategory(DQCategory category) {
        ensureRegexClassifierAccess();
        UserDefinedCategory regEx = UserDefinedCategory.fromDQCategory(category);
        customRegexClassifierAccess.createRegex(regEx);
        regexClassifier = customRegexClassifierAccess.readUserDefinedClassifier();

        ensureMetadataIndexAccess();
        customMetadataIndexAccess.createCategory(category);
        customMetadataIndexAccess.commitChangesAndCloseWriter();
        metadata = customMetadataIndexAccess.readCategoryMedatada();
    }
}
