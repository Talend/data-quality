package org.talend.dataquality.semantic.broadcast;

import java.io.Serializable;

import org.talend.dataquality.semantic.index.DictionarySearchMode;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

/**
 * A container object for DQ dictionaries serialization.
 */
public class TdqCategories implements Serializable {

    private static final long serialVersionUID = 8077049508746278932L;

    private BroadcastMetadataObject categoryMetadata;

    private BroadcastIndexObject dictionary;

    private BroadcastIndexObject customDictionary;

    private BroadcastIndexObject keyword;

    private BroadcastRegexObject regex;

    // No argument constructor needed for Jackson Deserialization
    public TdqCategories() {
    }

    public TdqCategories(BroadcastMetadataObject categoryMetadata, BroadcastIndexObject dictionary,
            BroadcastIndexObject customDictionary, BroadcastIndexObject keyword, BroadcastRegexObject regex) {
        this.categoryMetadata = categoryMetadata;
        this.dictionary = dictionary;
        this.customDictionary = customDictionary;
        this.keyword = keyword;
        this.regex = regex;
    }

    public BroadcastMetadataObject getCategoryMetadata() {
        return categoryMetadata;
    }

    public void setCategoryMetadata(BroadcastMetadataObject categoryMetadata) {
        this.categoryMetadata = categoryMetadata;
    }

    public BroadcastIndexObject getDictionary() {
        return dictionary;
    }

    public void setDictionary(BroadcastIndexObject dictionary) {
        this.dictionary = dictionary;
    }

    public BroadcastIndexObject getCustomDictionary() {
        return customDictionary;
    }

    public void setCustomDictionary(BroadcastIndexObject customDictionary) {
        this.customDictionary = customDictionary;
    }

    public BroadcastIndexObject getKeyword() {
        return keyword;
    }

    public void setKeyword(BroadcastIndexObject keyword) {
        this.keyword = keyword;
    }

    public BroadcastRegexObject getRegex() {
        return regex;
    }

    public void setRegex(BroadcastRegexObject regex) {
        this.regex = regex;
    }

    public DictionarySnapshot asDictionarySnapshot() {
        return new DictionarySnapshot(categoryMetadata.getDQCategoryMap(), //
                new LuceneIndex(dictionary.asDirectory(), DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY), //
                new LuceneIndex(customDictionary.asDirectory(), DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY), //
                new LuceneIndex(keyword.asDirectory(), DictionarySearchMode.MATCH_SEMANTIC_KEYWORD), //
                regex.getRegexClassifier());
    }

}
