package org.talend.dataquality.semantic.api;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.talend.dataquality.semantic.api.internal.CustomDocumentIndexAccess;
import org.talend.dataquality.semantic.api.internal.CustomMetadataIndexAccess;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.model.DQCategory;

public class CustomDictionaryHolder {

    private static final Logger LOGGER = Logger.getLogger(CustomDictionaryHolder.class);

    private Map<String, DQCategory> metadata;

    private Directory customDirectory;

    private UserDefinedClassifier regexClassifier;

    private CustomMetadataIndexAccess customMetadataIndex;

    private CustomDocumentIndexAccess customDocumentIndex;

    public CustomDictionaryHolder(String contextName) {
        File folder = new File(CategoryRegistryManager.getLocalRegistryPath() + "/" + contextName + "/prod/metadata");
        if (folder.exists()) {
            try {
                customMetadataIndex = new CustomMetadataIndexAccess(FSDirectory.open(folder));
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public Map<String, DQCategory> getMetadata() {
        if (metadata == null) {
            return CategoryRegistryManager.getInstance().getCategoryMetadataMap();
        } else {
            return metadata;
        }
    }

    public Directory getCustomDirectory() {
        return customDirectory;
    }

    public UserDefinedClassifier getRegexClassifier() {
        return regexClassifier;
    }

    public void createCategory(DQCategory category) {
        customMetadataIndex.createCategory(category);
    }

    public void updateCategory(DQCategory category) {
        customMetadataIndex.insertOrUpdateCategory(category);
    }

    public void deleteCategory(DQCategory category) {
        customMetadataIndex.deleteCategory(category);
    }

    public void reloadCategoryMetadata() {
        if (customMetadataIndex != null)
            metadata = customMetadataIndex.readCategoryMedatada();
    }
}
