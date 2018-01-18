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
package org.talend.dataquality.semantic.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.dataquality.semantic.recognizer.CategoryFrequency;

/**
 * Semantic type bean which hold semantic type to its count information in a map.
 *
 */
public class SemanticType {

    private Map<CategoryFrequency, Long> categoryToCount = new HashMap<CategoryFrequency, Long>();

    /**
     * Get categoryToCount.
     */
    public Map<CategoryFrequency, Long> getCategoryToCount() {
        return categoryToCount;
    }

    /**
     * Get suggested category.
     */
    public String getSuggestedCategory() {
        return getSuggestedCategories().get(0).getCategoryId();
    }

    /**
     * Get suggested categories, order from best to worst.
     */
    public List<CategoryFrequency> getSuggestedCategories() {
        List<CategoryFrequency> frequencies = new ArrayList<>(categoryToCount.keySet());
        Collections.sort(frequencies, Collections.reverseOrder());
        return frequencies;
    }

    /**
     * Increment the category with count of one category.
     * 
     * @param category
     * @param count
     */
    public void increment(CategoryFrequency category, long count) {
        if (!categoryToCount.containsKey(category)) {
            categoryToCount.put(category, count);
        } else {
            categoryToCount.put(category, categoryToCount.get(category) + count);
        }
    }

}
