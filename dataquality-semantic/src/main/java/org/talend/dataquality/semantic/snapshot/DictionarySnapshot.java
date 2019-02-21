package org.talend.dataquality.semantic.snapshot;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.model.DQCategory;

/**
 * Used for discovery and validation
 * contains information to access indexes for read only
 */
public class DictionarySnapshot {

    private Map<String, DQCategory> metadata;

    private Index sharedDataDict;

    private Index customDataDict;

    private Index keyword;

    private UserDefinedClassifier regexClassifier;

    public DictionarySnapshot(Map<String, DQCategory> metadata, Index sharedDataDict, Index customDataDict, Index keyword,
            UserDefinedClassifier regexClassifier) {
        this.metadata = new HashMap<>();
        metadata.entrySet().forEach(entry -> this.metadata.put(entry.getKey(), SerializationUtils.clone(entry.getValue())));
        this.sharedDataDict = sharedDataDict;
        this.customDataDict = customDataDict;
        this.keyword = keyword;
        this.regexClassifier = SerializationUtils.clone(regexClassifier);
    }

    public Map<String, DQCategory> getMetadata() {
        return metadata;
    }

    /**
     *
     * @param categoryName
     * @return null for the unknown category name otherwise return DQCategory
     */
    public DQCategory getDQCategoryByName(String categoryName) {
        DQCategory dqCategory = null;
        for (DQCategory dqCat : getMetadata().values()) {
            if (dqCat.getName().equals(categoryName)) {
                dqCategory = dqCat;
                break;
            }
        }
        return dqCategory;
    }

    public DQCategory getDQCategoryById(String categoryId) {
        DQCategory dqCategory = null;
        for (DQCategory dqCat : getMetadata().values()) {
            if (dqCat.getId().equals(categoryId)) {
                dqCategory = dqCat;
                break;
            }
        }
        return dqCategory;
    }

    public Index getSharedDataDict() {
        return sharedDataDict;
    }

    public Index getCustomDataDict() {
        return customDataDict;
    }

    public Index getKeyword() {
        return keyword;
    }

    public UserDefinedClassifier getRegexClassifier() {
        return regexClassifier;
    }

}
