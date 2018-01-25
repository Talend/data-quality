package org.talend.dataquality.semantic.broadcast;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQCategoryForValidation;

/**
 * A serializable object to hold all category metadata.
 */
public class BroadcastMetadataObject implements Serializable {

    private static final long serialVersionUID = 6228494634405067399L;

    private static final Logger LOGGER = Logger.getLogger(BroadcastMetadataObject.class);

    private Map<String, DQCategoryForValidation> metadata;

    public BroadcastMetadataObject() {
    }

    public BroadcastMetadataObject(Map<String, DQCategory> dqCategoryMap) {
        metadata = new HashMap<>();
        dqCategoryMap.values().forEach(value -> {
            DQCategoryForValidation dqCategoryForValidation = new DQCategoryForValidation();
            try {
                BeanUtils.copyProperties(dqCategoryForValidation, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.error(e.getMessage(), e);
            }
            metadata.put(value.getId(), dqCategoryForValidation);
        });
    }

    public Map<String, DQCategory> getDQCategoryMap() {
        Map<String, DQCategory> dqCategoryMap = new HashMap<>();
        metadata.values().forEach(value -> {
            DQCategory dqCategory = new DQCategory();
            try {
                BeanUtils.copyProperties(dqCategory, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.error(e.getMessage(), e);
            }
            dqCategoryMap.put(value.getId(), dqCategory);
        });
        return dqCategoryMap;
    }

    public Map<String, DQCategoryForValidation> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, DQCategoryForValidation> metadata) {
        this.metadata = metadata;
    }
}
