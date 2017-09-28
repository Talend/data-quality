package org.talend.dataquality.semantic.api;

import java.util.Map;

import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.api.internal.CustomDocumentIndexAccess;
import org.talend.dataquality.semantic.api.internal.CustomMetadataIndexAccess;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.model.DQCategory;

public class CustomDictionaryHolder {

    private String contextName;

    private Map<String, DQCategory> metadata;

    private Directory customDirectory;

    private UserDefinedClassifier regexClassifier;

    private CustomMetadataIndexAccess customMetadataIndex;

    private CustomDocumentIndexAccess customDocumentIndex;

    private CategoryRegistryManager crm;

    public CustomDictionaryHolder(CategoryRegistryManager crm, String contextName) {
        this.crm = crm;
        this.contextName = contextName;
    }

    public String getContextName() {
        return contextName;
    }

    public Map<String, DQCategory> getMetadata() {
        if (metadata == null) {
            return crm.getCategoryMetadataMap();
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
        metadata = customMetadataIndex.readCategoryMedatada();
    }
}
