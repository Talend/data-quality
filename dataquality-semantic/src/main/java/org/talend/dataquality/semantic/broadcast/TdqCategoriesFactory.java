package org.talend.dataquality.semantic.broadcast;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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

    /**
     * Load categories from local lucene index and produce a TdqCategories object.
     * 
     * @return the serializable object
     */
    public static final TdqCategories createTdqCategories() {
        return createTdqCategories(null);
    }

    /**
     * Load categories from local lucene index and produce a TdqCategories object.
     * 
     * @param categories
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
        final BroadcastIndexObject dictionary;
        final BroadcastIndexObject keyword;
        final BroadcastRegexObject regex;
        final BroadcastMetadataObject meta;
        try {
            try (Directory ddDir = FSDirectory.open(new File(crm.getDictionaryURI()))) {
                dictionary = new BroadcastIndexObject(ddDir, selectedCategoryMap.keySet());
                LOGGER.debug("Returning dictionary at path '{" + crm.getDictionaryURI() + "}'.");
            }
            try (Directory kwDir = FSDirectory.open(new File(crm.getKeywordURI()))) {
                keyword = new BroadcastIndexObject(kwDir, selectedCategoryMap.keySet());
                LOGGER.debug("Returning keywords at path '{" + crm.getRegexURI() + "}'.");
            }
            UserDefinedClassifier classifiers = crm.getRegexClassifier(true);
            regex = new BroadcastRegexObject(classifiers, selectedCategoryMap.keySet());
            LOGGER.debug("Returning regexes at path '{" + crm.getRegexURI() + "}'.");
            meta = new BroadcastMetadataObject(selectedCategoryMap);
            LOGGER.debug("Returning category metadata.");
            return new TdqCategories(meta, dictionary, keyword, regex);
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
