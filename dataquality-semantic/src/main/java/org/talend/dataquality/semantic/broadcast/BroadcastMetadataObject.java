package org.talend.dataquality.semantic.broadcast;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.semantic.model.DQCategory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A serializable object to hold all category metadata.
 */
public class BroadcastMetadataObject implements Serializable {

    private static final long serialVersionUID = 6228494634405067399L;

    private static final Logger LOGGER = Logger.getLogger(BroadcastMetadataObject.class);

    private Map<String, DQCategoryForValidation> metadata;

    // No argument constructor needed for Jackson Deserialization
    public BroadcastMetadataObject() {
    }

    public BroadcastMetadataObject(Map<String, DQCategory> dqCategoryMap) {
        metadata = new HashMap<>();
        dqCategoryMap.values().forEach(value -> {
            DQCategoryForValidation dqCategoryForValidation = new DQCategoryForValidation();
            try {
                PropertyUtils.copyProperties(dqCategoryForValidation, value);
                // clear children and refill
                List<DQCategory> sourceChildren = value.getChildren();
                if (!CollectionUtils.isEmpty(sourceChildren)) {
                    List<DQCategoryForValidation> copyChildren = new ArrayList<>();
                    for (DQCategory child : sourceChildren) {
                        DQCategoryForValidation copyChild = new DQCategoryForValidation();
                        PropertyUtils.copyProperties(copyChild, child);
                        copyChildren.add(copyChild);
                    }
                    dqCategoryForValidation.setChildren(copyChildren);
                }
                metadata.put(value.getId(), dqCategoryForValidation);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
    }

    @JsonIgnore
    public Map<String, DQCategory> getDQCategoryMap() {
        Map<String, DQCategory> dqCategoryMap = new HashMap<>();
        metadata.values().forEach(value -> {
            try {
                DQCategory dqCategory = new DQCategory();
                PropertyUtils.copyProperties(dqCategory, value);
                dqCategoryMap.put(value.getId(), dqCategory);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                LOGGER.error(e.getMessage(), e);
            }
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
