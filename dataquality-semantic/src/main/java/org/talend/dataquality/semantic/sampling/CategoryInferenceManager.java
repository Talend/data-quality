// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.semantic.sampling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.exception.DQSemanticRuntimeException;
import org.talend.dataquality.semantic.index.DictionarySearchMode;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.recognizer.CategoryFrequency;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.DefaultCategoryRecognizer;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class CategoryInferenceManager {

    private static final Logger LOGGER = Logger.getLogger(CategoryInferenceManager.class);

    // One recognizer per column, <Column index, Category recognizer>
    private Map<Integer, CategoryRecognizer> categoryRecognizers = new HashMap<>();

    private DictionarySnapshot dictionarySnapshot;

    /**
     * Constructor
     */
    public CategoryInferenceManager() {
        final CategoryRegistryManager crm = CategoryRegistryManager.getInstance();
        try {
            dictionarySnapshot = new DictionarySnapshot( //
                    crm.getSharedCategoryMetadata(), //
                    new LuceneIndex(crm.getSharedDataDictDirectory(), DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY), //
                    null, // custom data dictionary not needed here
                    new LuceneIndex(crm.getSharedKeywordDirectory(), DictionarySearchMode.MATCH_SEMANTIC_KEYWORD), //
                    crm.getRegexClassifier());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 
     * DOC zhao get semantic category <br>
     * This method must be called after {{@link #inferCategory(Object[])}
     * 
     * @return A column index to category map , <Column index,List<Semantic category>>
     */
    public Map<Integer, List<SemanticCategory>> getSemanticCategory() {
        Map<Integer, List<SemanticCategory>> categories = new HashMap<>();
        for (Entry<Integer, CategoryRecognizer> entry : categoryRecognizers.entrySet()) {
            Integer colIdx = entry.getKey();
            CategoryRecognizer categoryRecognizer = entry.getValue();

            List<SemanticCategory> categoryList = new ArrayList<>();
            Collection<CategoryFrequency> result = categoryRecognizer.getResult();
            for (CategoryFrequency frequencyTableItem : result) {
                SemanticCategory category = new SemanticCategory(frequencyTableItem.getCategoryId(),
                        frequencyTableItem.getCategoryName(), frequencyTableItem.getCount(), frequencyTableItem.getFrequency());
                categoryList.add(category);
            }
            categories.put(colIdx, categoryList);

        }
        return categories;
    }

    /**
     * 
     * DOC zhao inferring the sementic category given an array of record. <br>
     * {{@link #getSemanticCategory()} is intended to be called after all data is passed to get the finally semantic
     * categories.
     * 
     * @param record
     * @return true if inferring is successfully done, false otherwise.
     * @throws RuntimeException thrown when exceptional cases occur.
     */
    public boolean inferCategory(Object[] record) {
        int colIdx = 0;
        for (Object fieldData : record) {
            CategoryRecognizer categoryRecognizer = categoryRecognizers.get(colIdx);
            if (categoryRecognizer == null) {
                categoryRecognizer = newCategoryRecognizer();

                categoryRecognizer.prepare();
                categoryRecognizers.put(colIdx, categoryRecognizer);
            }
            categoryRecognizer.process(fieldData == null ? null : fieldData.toString());
            colIdx++;
        }
        return true;
    }

    private CategoryRecognizer newCategoryRecognizer() {
        // create a new DefaultCategoryRecognizer per column, reusing the same dictionarySnapshot object.
        try {
            return new DefaultCategoryRecognizer(dictionarySnapshot);
        } catch (IOException e) {
            throw new DQSemanticRuntimeException("Unable to find resources.", e);
        }
    }
}
