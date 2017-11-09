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
package org.talend.dataquality.semantic.recognizer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.index.ClassPathDirectory;
import org.talend.dataquality.semantic.index.DictionarySearchMode;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.DQCategory;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class CategoryRecognizerBuilder {

    private static final Logger LOGGER = Logger.getLogger(CategoryRecognizerBuilder.class);

    public static final String DEFAULT_METADATA_PATH = "/" + CategoryRegistryManager.METADATA_SUBFOLDER_NAME + "/";

    public static final String DEFAULT_DD_PATH = "/" + CategoryRegistryManager.DICTIONARY_SUBFOLDER_NAME + "/";

    public static final String DEFAULT_KW_PATH = "/" + CategoryRegistryManager.KEYWORD_SUBFOLDER_NAME + "/";

    public static final String DEFAULT_RE_PATH = "/" + CategoryRegistryManager.REGEX_SUBFOLDER_NAME + "/"
            + CategoryRegistryManager.REGEX_CATEGRIZER_FILE_NAME;

    private Mode mode;

    private LuceneIndex sharedDataDictIndex;

    private LuceneIndex customDataDictIndex;

    private LuceneIndex keywordIndex;

    private Directory sharedDataDictDirectory;

    private Directory customDataDictDirectory;

    private Directory keywordDirectory;

    private UserDefinedClassifier regexClassifier;

    private Map<String, DQCategory> metadata;

    private String tenantID = CategoryRegistryManager.DEFAULT_TENANT_ID;

    private DictionaryConstituents constituents;

    public static CategoryRecognizerBuilder newBuilder() {
        return new CategoryRecognizerBuilder();
    }

    public CategoryRecognizerBuilder tenantID(String tenantID) {
        this.tenantID = tenantID;
        return this;
    }

    public CategoryRecognizerBuilder metadata(Map<String, DQCategory> metadata) {
        this.metadata = metadata;
        return this;
    }

    public CategoryRecognizerBuilder ddDirectory(Directory sharedDataDictDirectory) {
        this.sharedDataDictDirectory = sharedDataDictDirectory;
        return this;
    }

    public CategoryRecognizerBuilder ddCustomDirectory(Directory customDataDictDirectory) {
        this.customDataDictDirectory = customDataDictDirectory;
        return this;
    }

    public CategoryRecognizerBuilder kwDirectory(Directory keywordDirectory) {
        this.keywordDirectory = keywordDirectory;
        return this;
    }

    public CategoryRecognizerBuilder regexClassifier(UserDefinedClassifier regexClassifier) {
        this.regexClassifier = regexClassifier;
        return this;
    }

    public CategoryRecognizerBuilder lucene() {
        this.mode = Mode.LUCENE;
        return this;
    }

    public CategoryRecognizer build() throws IOException {

        switch (mode) {
        case LUCENE:
            return new DefaultCategoryRecognizer(getDictionaryConstituents());
        case ELASTIC_SEARCH:
            throw new IllegalArgumentException("Elasticsearch mode is not supported any more");
        default:
            throw new IllegalArgumentException("no mode specified.");
        }

    }

    public DictionaryConstituents getDictionaryConstituents() {
        if (constituents == null) {
            Map<String, DQCategory> meta = getCategoryMetadata();
            LuceneIndex sharedDataDict = getSharedDataDictIndex();
            LuceneIndex customDataDict = getCustomDataDictIndex();
            LuceneIndex keyword = getKeywordIndex();
            UserDefinedClassifier regex = getRegexClassifier();
            constituents = new DictionaryConstituents(meta, sharedDataDict, customDataDict, keyword, regex);
        }
        return constituents;
    }

    public Map<String, DQCategory> getCategoryMetadata() {
        if (metadata == null) {
            // always return latest metadata from registry without filling the metadata field
            return CategoryRegistryManager.getInstance().getCustomDictionaryHolder(tenantID).getMetadata();
        }
        return metadata;
    }

    private LuceneIndex getSharedDataDictIndex() {
        if (sharedDataDictIndex == null) {
            if (sharedDataDictDirectory == null) {
                sharedDataDictDirectory = CategoryRegistryManager.getInstance().getSharedDataDictDirectory();
            }
            sharedDataDictIndex = new LuceneIndex(sharedDataDictDirectory, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
        }
        return sharedDataDictIndex;
    }

    private LuceneIndex getCustomDataDictIndex() {
        if (customDataDictIndex == null) {
            if (customDataDictDirectory == null) {
                // load from t_default tenant
                Directory dir = CategoryRegistryManager.getInstance().getCustomDictionaryHolder(tenantID).getDataDictDirectory();
                if (dir != null) {
                    customDataDictIndex = new LuceneIndex(dir, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
                }
            } else {
                customDataDictIndex = new LuceneIndex(customDataDictDirectory, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
            }
        }
        return customDataDictIndex;
    }

    private LuceneIndex getKeywordIndex() {
        if (keywordIndex == null) {
            try {
                if (keywordDirectory == null) {
                    keywordDirectory = ClassPathDirectory.open(CategoryRegistryManager.getInstance().getKeywordURI());
                }
                keywordIndex = new LuceneIndex(keywordDirectory, DictionarySearchMode.MATCH_SEMANTIC_KEYWORD);
            } catch (URISyntaxException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return keywordIndex;
    }

    private UserDefinedClassifier getRegexClassifier() {
        if (regexClassifier == null) {
            try {
                return CategoryRegistryManager.getInstance().getCustomDictionaryHolder(tenantID).getRegexClassifier();
            } catch (IOException e) {
                LOGGER.error("Failed to load provided regex classifiers", e);
            }
        }
        return regexClassifier;
    }

    public enum Mode {
        LUCENE,
        ELASTIC_SEARCH
    }

    public Mode getMode() {
        return mode;
    }

    public void initIndex() {
        if (customDataDictIndex != null) {
            try {
                customDataDictIndex.initIndex();
            } catch (AlreadyClosedException e) {
                customDataDictIndex = null;
                customDataDictDirectory = null;
                getCustomDataDictIndex();
            }
        }
    }
}
