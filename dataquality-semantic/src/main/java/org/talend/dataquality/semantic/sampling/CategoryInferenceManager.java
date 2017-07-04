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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.talend.dataquality.semantic.recognizer.CategoryFrequency;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class CategoryInferenceManager {

    // One recognizer per column, <Column index, Category recognizer>
    private Map<Integer, CategoryRecognizer> categoryRecognizers = new HashMap<>();

    public Map<Integer, CategoryRecognizer> getCategoryRecognizers() {
        return categoryRecognizers;
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
        CategoryRecognizerBuilder b = CategoryRecognizerBuilder.newBuilder();
        // get the lucene index.
        try {
            final URI ddPath = this.getClass().getResource(CategoryRecognizerBuilder.DEFAULT_DD_PATH).toURI();
            final URI kwPath = this.getClass().getResource(CategoryRecognizerBuilder.DEFAULT_KW_PATH).toURI();
            return b.lucene().ddPath(ddPath).kwPath(kwPath).build();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Unable to find resources.", e);
        }
        // or get the ES index.
        // TODO use ES index for category inference
        // FIXME avoid instantiation of classifiers inside each categoryRecognizer for each column
    }
}
