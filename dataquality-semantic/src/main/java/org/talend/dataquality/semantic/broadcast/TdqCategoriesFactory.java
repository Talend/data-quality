package org.talend.dataquality.semantic.broadcast;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.model.DQCategory;

/**
 * Factory to produce serializable object containing DQ categories.
 */
public class TdqCategoriesFactory {

    private static final Logger LOGGER = Logger.getLogger(TdqCategoriesFactory.class);

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
        try {
            try (Directory ddDir = FSDirectory.open(new File(crm.getDictionaryURI()))) {
                sharedDictionary = new BroadcastIndexObject(ddDir, selectedCategoryMap.keySet());
                LOGGER.debug("Returning shared dictionary.");
            }

            Directory customDataDictDir = crm.getCustomDictionaryHolder().getDataDictDirectory();
            customDictionary = new BroadcastIndexObject(customDataDictDir, selectedCategoryMap.keySet());
            LOGGER.debug("Returning custom dictionary.");

            try (Directory kwDir = FSDirectory.open(new File(crm.getKeywordURI()))) {
                keyword = new BroadcastIndexObject(kwDir, selectedCategoryMap.keySet());
                LOGGER.debug("Returning keywords at path.");
            }

            UserDefinedClassifier classifiers = crm.getCustomDictionaryHolder().getRegexClassifier();
            regex = new BroadcastRegexObject(classifiers, selectedCategoryMap.keySet());
            LOGGER.debug("Returning regexes.");

            meta = new BroadcastMetadataObject(selectedCategoryMap);
            LOGGER.debug("Returning category metadata.");
            return new TdqCategories(meta, sharedDictionary, customDictionary, keyword, regex);
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return createEmptyTdqCategories();
    }
}
