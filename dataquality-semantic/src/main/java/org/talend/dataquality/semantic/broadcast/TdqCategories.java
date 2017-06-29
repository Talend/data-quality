package org.talend.dataquality.semantic.broadcast;

import java.io.Serializable;

/**
 * A container object for DQ dictionaries.
 */
public class TdqCategories implements Serializable {

    private static final long serialVersionUID = 8077049508746278932L;

    private BroadcastMetadataObject categoryMetadata;

    private BroadcastIndexObject dictionary;

    private BroadcastIndexObject keyword;

    private BroadcastRegexObject regex;

    public TdqCategories() {
    }

    public TdqCategories(BroadcastMetadataObject categoryMetadata, BroadcastIndexObject dictionary, BroadcastIndexObject keyword,
            BroadcastRegexObject regex) {
        this.categoryMetadata = categoryMetadata;
        this.dictionary = dictionary;
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

}
