package org.talend.dataquality.semantic.broadcast;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.model.DQCategory;

/**
 * Factory to produce serializable object containing DQ categories.
 */
public class TdqCategoriesFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TdqCategoriesFactory.class);

    public static final TdqCategories createEmptyTdqCategories() {
        return new TdqCategories( //
                new BroadcastMetadataObject(Collections.emptyMap()), //
                new BroadcastIndexObject(Collections.emptyList()), //
                new BroadcastIndexObject(Collections.emptyList()), //
                new BroadcastIndexObject(Collections.emptyList()), //
                new BroadcastRegexObject(new UserDefinedClassifier()));
    }

    /**
     * Load categories from local lucene index and produce a TdqCategories object.
     * 
     * @return the serializable object
     */
    public static final TdqCategories createFullTdqCategories() {
        return createTdqCategories(null);
    }

    /**
     * Load categories from local lucene index and produce a TdqCategories object.
     * 
     * @param categoryNames
     * @return the serializable object
     */
    public static final TdqCategories createTdqCategories(Set<String> categoryNames) {
        CategoryRegistryManager crm = CategoryRegistryManager.getInstance();
        final Map<String, DQCategory> selectedCategoryMap = new HashMap<>();
        for (DQCategory dqCat : crm.listCategories(false)) {
            if (categoryNames == null || categoryNames.contains(dqCat.getName())) {
                selectedCategoryMap.put(dqCat.getId(), dqCat);
            }
        }
        final BroadcastIndexObject sharedDictionary;
        final BroadcastIndexObject customDictionary;
        final BroadcastIndexObject keyword;
        final BroadcastRegexObject regex;
        final BroadcastMetadataObject meta;
        sharedDictionary = new BroadcastIndexObject(crm.getSharedDataDictDirectory(), selectedCategoryMap.keySet());
        LOGGER.debug("Returning shared data dictionary.");

        Directory customDataDictDir = crm.getCustomDictionaryHolder().getDataDictDirectory();
        customDictionary = new BroadcastIndexObject(customDataDictDir, selectedCategoryMap.keySet());
        LOGGER.debug("Returning custom data dictionary.");

        keyword = new BroadcastIndexObject(crm.getSharedKeywordDirectory(), selectedCategoryMap.keySet());
        LOGGER.debug("Returning shared keyword index.");

        UserDefinedClassifier classifiers = crm.getCustomDictionaryHolder().getRegexClassifier();
        regex = new BroadcastRegexObject(classifiers, selectedCategoryMap.keySet());
        LOGGER.debug("Returning regexes.");

        meta = new BroadcastMetadataObject(selectedCategoryMap);
        LOGGER.debug("Returning category metadata.");
        return new TdqCategories(meta, sharedDictionary, customDictionary, keyword, regex);
    }

    /**
     * Load the shared categories and produce a TdqCategories object.
     *
     * @return the TdqCategories object containing all shared categories which are used for validation
     */
    public static final TdqCategories createSharedTdqCategories() {
        final CategoryRegistryManager crm = CategoryRegistryManager.getInstance();
        Map<String, DQCategory> selectedCategoryMap = crm.getSharedCategoryMetadata().entrySet().stream() //
                // keep only the categories which are used for validation
                .filter(entry -> Boolean.TRUE.equals(entry.getValue().getCompleteness())) //
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        return new TdqCategories( //
                new BroadcastMetadataObject(selectedCategoryMap), //
                new BroadcastIndexObject(crm.getSharedDataDictDirectory(), selectedCategoryMap.keySet()), //
                new BroadcastIndexObject(Collections.emptyList()), // custom data dictionary not needed
                new BroadcastIndexObject(crm.getSharedKeywordDirectory(), selectedCategoryMap.keySet()), //
                new BroadcastRegexObject(crm.getRegexClassifier(), selectedCategoryMap.keySet()));
    }
}
