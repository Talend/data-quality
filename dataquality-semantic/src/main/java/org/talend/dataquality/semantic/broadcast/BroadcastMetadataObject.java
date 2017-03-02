package org.talend.dataquality.semantic.broadcast;

import java.io.Serializable;
import java.util.Map;

import org.talend.dataquality.semantic.model.DQCategory;

/**
 * A serializable object to hold all category metadata.
 */
public class BroadcastMetadataObject implements Serializable {

    private static final long serialVersionUID = 6228494634405067399L;

    private Map<String, DQCategory> metadata;

    /**
     * @param metadata
     */
    public BroadcastMetadataObject(Map<String, DQCategory> metadata) {
        this.metadata = metadata;
    }

    public Map<String, DQCategory> get() {
        return metadata;
    }
}
