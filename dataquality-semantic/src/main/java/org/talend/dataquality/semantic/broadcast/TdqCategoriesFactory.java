package org.talend.dataquality.semantic.broadcast;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

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
        final Collection<DQCategory> dqCats = CategoryRegistryManager.getInstance().listCategories(false);
        final Set<String> catNames = new HashSet<>();
        for (DQCategory cat : dqCats) {
            catNames.add(cat.getName());
        }
        return createTdqCategories(catNames);
    }

    /**
     * Load categories from local lucene index and produce a TdqCategories object.
     * 
     * @param categories
     * @return the serializable object
     */
    public static final TdqCategories createTdqCategories(Set<String> categories) {
        CategoryRegistryManager crm = CategoryRegistryManager.getInstance();
        final Map<String, DQCategory> dqCategoryMap = new HashMap<>();
        for (DQCategory dqCat : crm.listCategories(false)) {
            if (categories.contains(dqCat.getName())) {
                dqCategoryMap.put(dqCat.getId(), dqCat);
            }
        }
        final BroadcastIndexObject dictionary;
        final BroadcastIndexObject keyword;
        final BroadcastRegexObject regex;
        final BroadcastMetadataObject meta;
        try {
            try (Directory ddDir = FSDirectory.open(new File(crm.getDictionaryURI()))) {
                dictionary = new BroadcastIndexObject(ddDir, categories);
                LOGGER.debug("Returning dictionary at path '{" + crm.getDictionaryURI() + "}'.");
            }
            try (Directory kwDir = FSDirectory.open(new File(crm.getKeywordURI()))) {
                keyword = new BroadcastIndexObject(kwDir, categories);
                LOGGER.debug("Returning keywords at path '{" + crm.getRegexURI() + "}'.");
            }
            UserDefinedClassifier classifiers = crm.getRegexClassifier(true);
            regex = new BroadcastRegexObject(classifiers, categories);
            LOGGER.debug("Returning regexes at path '{" + crm.getRegexURI() + "}'.");
            meta = new BroadcastMetadataObject(dqCategoryMap);
            LOGGER.debug("Returning category metadata.");
            return new TdqCategories(meta, dictionary, keyword, regex);
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
