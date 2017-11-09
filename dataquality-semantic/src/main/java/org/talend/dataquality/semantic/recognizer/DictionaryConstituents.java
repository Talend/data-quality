package org.talend.dataquality.semantic.recognizer;

import java.util.Map;

import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.model.DQCategory;

public class DictionaryConstituents {

    private Map<String, DQCategory> metadata;

    private Index sharedDataDict;

    private Index customDataDict;

    private Index keyword;

    private UserDefinedClassifier regexClassifier;

    public DictionaryConstituents(Map<String, DQCategory> metadata, Index sharedDataDict, Index customDataDict, Index keyword,
            UserDefinedClassifier regexClassifier) {
        this.metadata = metadata;
        this.sharedDataDict = sharedDataDict;
        this.customDataDict = customDataDict;
        this.keyword = keyword;
        this.regexClassifier = regexClassifier;
    }

    public Map<String, DQCategory> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, DQCategory> metadata) {
        this.metadata = metadata;
    }

    public Index getSharedDataDict() {
        return sharedDataDict;
    }

    public void setSharedDataDict(Index sharedDataDict) {
        this.sharedDataDict = sharedDataDict;
    }

    public Index getCustomDataDict() {
        return customDataDict;
    }

    public void setCustomDataDict(Index customDataDict) {
        this.customDataDict = customDataDict;
    }

    public Index getKeyword() {
        return keyword;
    }

    public void setKeyword(Index keyword) {
        this.keyword = keyword;
    }

    public UserDefinedClassifier getRegexClassifier() {
        return regexClassifier;
    }

    public void setRegexClassifier(UserDefinedClassifier regexClassifier) {
        this.regexClassifier = regexClassifier;
    }

}
