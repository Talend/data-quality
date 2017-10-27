package org.talend.dataquality.semantic.api;

import java.io.IOException;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;
import org.talend.dataquality.semantic.model.DQCategory;

/**
 * Utility class for loading category metadata from index.
 */
public class CategoryMetadataUtils {

    private static final Logger LOGGER = Logger.getLogger(CategoryMetadataUtils.class);

    /**
     * Fill dqCategories which contains for each category ID, the metadata with all the children and the parents.
     *
     * @param directory
     * @throws IOException
     */
    public static Map<String, DQCategory> loadMetadataFromIndex(Directory directory) {
        HashMap<String, DQCategory> sharedMetadata = new HashMap<>();
        Map<String, Set<String>> categoryToParents = new HashMap<>();
        try (final DirectoryReader reader = DirectoryReader.open(directory)) {
            Bits liveDocs = MultiFields.getLiveDocs(reader);
            // add the categories
            for (int i = 0; i < reader.maxDoc(); i++) {
                if (liveDocs != null && !liveDocs.get(i)) {
                    continue;
                }
                Document doc = reader.document(i);
                DQCategory dqCat = DictionaryUtils.categoryFromDocument(doc);
                sharedMetadata.put(dqCat.getId(), dqCat);
                if (!CollectionUtils.isEmpty(dqCat.getChildren())) {
                    fillCategoryToParents(categoryToParents, dqCat);
                }
            }

            // add the parent references in the child
            for (Map.Entry<String, Set<String>> entry : categoryToParents.entrySet()) {
                if (sharedMetadata.get(entry.getKey()) != null) {
                    List<DQCategory> parentCategoryList = new ArrayList<>();
                    for (String childCategoryId : entry.getValue()) {
                        parentCategoryList.add(new DQCategory(childCategoryId));
                    }
                    sharedMetadata.get(entry.getKey()).setParents(parentCategoryList);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return sharedMetadata;
    }

    /**
     * fill the map child -> parents
     * 
     * @param categoryToParents, the map child -> parents
     * @param dqCat, the category used to create the reference child -> parents
     */
    private static void fillCategoryToParents(Map<String, Set<String>> categoryToParents, DQCategory dqCat) {
        for (DQCategory cat : dqCat.getChildren()) {
            if (categoryToParents.get(cat.getId()) == null) {
                categoryToParents.put(cat.getId(), new HashSet<String>());
            }
            categoryToParents.get(cat.getId()).add(dqCat.getId());
        }
    }
}
