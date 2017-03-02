package org.talend.dataquality.semantic.broadcast;

import java.io.Serializable;

/**
 * A container object for DQ dictionaries.
 */
public class TdqCategories implements Serializable {

    private static final long serialVersionUID = 8077049508746278932L;

    private final BroadcastMetadataObject metadata;

    private final BroadcastIndexObject dictionary;

    private final BroadcastIndexObject keyword;

    private final BroadcastRegexObject regex;

    /**
     * Constructor
     * 
     * @param metadata
     * @param dictionary
     * @param keyword
     * @param regex
     */
    public TdqCategories(BroadcastMetadataObject metadata, BroadcastIndexObject dictionary, BroadcastIndexObject keyword,
            BroadcastRegexObject regex) {
        this.metadata = metadata;
        this.dictionary = dictionary;
        this.keyword = keyword;
        this.regex = regex;
    }

    public BroadcastMetadataObject getCategoryMetadata() {
        return metadata;
    }

    public BroadcastIndexObject getDictionary() {
        return dictionary;
    }

    public BroadcastIndexObject getKeyword() {
        return keyword;
    }

    public BroadcastRegexObject getRegex() {
        return regex;
    }

}
