package org.talend.dataquality.semantic.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.model.DQCategory;

public class CustomDictionaryHolder {

    private String contextName;

    private Map<String, DQCategory> metadata = new LinkedHashMap<>();

    private Directory customDirectory;

    private UserDefinedClassifier regexClassifier;

    public CustomDictionaryHolder() {
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public Map<String, DQCategory> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, DQCategory> metadata) {
        this.metadata = metadata;
    }

    public Directory getSharedKeywordDirectory() {
        return customDirectory;
    }

    public Directory getCustomDirectory() {
        return customDirectory;
    }

    public void setCustomDirectory(Directory customDirectory) {
        this.customDirectory = customDirectory;
    }

    public UserDefinedClassifier getRegexClassifier() {
        return regexClassifier;
    }

    public void setRegexClassifier(UserDefinedClassifier regexClassifier) {
        this.regexClassifier = regexClassifier;
    }

}
