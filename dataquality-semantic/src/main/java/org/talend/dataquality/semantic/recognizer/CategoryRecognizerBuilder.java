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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.custom.UDCategorySerDeser;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
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

    private URI dataDictPath;

    private URI keywordPath;

    private URI regexPath;

    private LuceneIndex dataDictIndex;

    private LuceneIndex dataDictCustomIndex;

    private LuceneIndex keywordIndex;

    private Directory dataDictDirectory;

    private Directory customDataDictDirectory;

    private Directory keywordDirectory;

    private UserDefinedClassifier regexClassifier;

    private Map<String, DQCategory> metadata;

    private String tenantID = CategoryRegistryManager.DEFAULT_TENANT_ID;

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

    public CategoryRecognizerBuilder ddPath(URI dataDictPath) {
        this.dataDictPath = dataDictPath;
        return this;
    }

    public CategoryRecognizerBuilder ddDirectory(Directory dataDictDirectory) {
        this.dataDictDirectory = dataDictDirectory;
        return this;
    }

    public CategoryRecognizerBuilder ddCustomDirectory(Directory customDataDictDirectory) {
        this.customDataDictDirectory = customDataDictDirectory;
        return this;
    }

    public CategoryRecognizerBuilder kwPath(URI keywordPath) {
        this.keywordPath = keywordPath;
        return this;
    }

    public CategoryRecognizerBuilder kwDirectory(Directory keywordDirectory) {
        this.keywordDirectory = keywordDirectory;
        return this;
    }

    public CategoryRecognizerBuilder regexPath(URI regexPath) {
        this.regexPath = regexPath;
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
            Map<String, DQCategory> meta = getCategoryMetadata();
            LuceneIndex sharedDataDict = getSharedDataDictIndex();
            LuceneIndex customDataDict = getCustomDataDictIndex();
            LuceneIndex keyword = getKeywordIndex();
            UserDefinedClassifier regex = getRegexClassifier();
            return new DefaultCategoryRecognizer(sharedDataDict, customDataDict, keyword, regex, meta);
        case ELASTIC_SEARCH:
            throw new IllegalArgumentException("Elasticsearch mode is not supported any more");
        default:
            throw new IllegalArgumentException("no mode specified.");
        }

    }

    public Map<String, DQCategory> getCategoryMetadata() {
        if (metadata == null) {
            // always return latest metadata from registry without filling the metadata field
            return CategoryRegistryManager.getInstance().getCustomDictionaryHolder(tenantID).getMetadata();
        }
        return metadata;
    }

    private LuceneIndex getSharedDataDictIndex() {
        if (dataDictIndex == null) {
            if (dataDictDirectory == null) {
                if (dataDictPath == null) {
                    try {
                        dataDictPath = CategoryRecognizerBuilder.class.getResource(DEFAULT_DD_PATH).toURI();
                        dataDictIndex = new LuceneIndex(dataDictPath, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
                    } catch (URISyntaxException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                } else {
                    dataDictDirectory = CategoryRegistryManager.getInstance().getSharedDataDictDirectory();
                    dataDictIndex = new LuceneIndex(dataDictDirectory, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
                }
            } else {
                if (dataDictPath == null) {
                    dataDictIndex = new LuceneIndex(dataDictDirectory, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
                } else {
                    throw new IllegalArgumentException("Please call either ddDirectory() or ddPath() but not both!");
                }
            }
        }
        return dataDictIndex;
    }

    private LuceneIndex getCustomDataDictIndex() {
        if (dataDictCustomIndex == null) {
            if (customDataDictDirectory == null) {
                // load from t_default tenant
                Directory dir = CategoryRegistryManager.getInstance().getCustomDictionaryHolder(tenantID).getDataDictDirectory();
                if (dir != null) {
                    dataDictCustomIndex = new LuceneIndex(dir, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
                }
            } else {
                dataDictCustomIndex = new LuceneIndex(customDataDictDirectory, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
            }
        }
        return dataDictCustomIndex;
    }

    private LuceneIndex getKeywordIndex() {
        if (keywordIndex == null) {
            if (keywordDirectory == null) {
                if (keywordPath == null) {
                    try {
                        keywordPath = CategoryRecognizerBuilder.class.getResource(DEFAULT_KW_PATH).toURI();
                    } catch (URISyntaxException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                keywordIndex = new LuceneIndex(keywordPath, DictionarySearchMode.MATCH_SEMANTIC_KEYWORD);
            } else {
                if (keywordPath == null) {
                    keywordIndex = new LuceneIndex(keywordDirectory, DictionarySearchMode.MATCH_SEMANTIC_KEYWORD);
                } else {
                    throw new IllegalArgumentException("Please call either kwDirectory() or kwPath() but not both!");
                }
            }
        }
        return keywordIndex;
    }

    private UserDefinedClassifier getRegexClassifier() {
        if (regexClassifier == null) {
            if (regexPath == null) {
                try {
                    return CategoryRegistryManager.getInstance().getCustomDictionaryHolder(tenantID).getRegexClassifier();
                } catch (IOException e) {
                    LOGGER.error("Failed to load provided regex classifiers", e);
                }
            } else {
                try {
                    return UDCategorySerDeser.readJsonFile(regexPath);
                } catch (IOException e) {
                    LOGGER.error("Failed to load regex classifiers from URI: " + regexPath, e);
                }
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
        if (dataDictCustomIndex != null) {
            try {
                dataDictCustomIndex.initIndex();
            } catch (AlreadyClosedException e) {
                dataDictCustomIndex = null;
                customDataDictDirectory = null;
                getCustomDataDictIndex();
            }
        }
    }

}
