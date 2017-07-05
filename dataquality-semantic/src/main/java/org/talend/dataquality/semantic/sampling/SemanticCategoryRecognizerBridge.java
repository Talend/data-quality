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

import java.util.List;
import java.util.Map;

import org.talend.dataquality.semantic.exception.DQException;

/**
 * created by zhao <br>
 * Bridge for data sampling and semantic category API.
 * 
 */
public class SemanticCategoryRecognizerBridge {

    private CategoryInferenceManager manager;

    private List<Object[]> sampleDataCache;

    // The cursor of reservoir sampling records.
    private int recordCursor = 0;

    private boolean stopRequested = false;

    public SemanticCategoryRecognizerBridge(List<Object[]> sampleDataCache) {
        this.sampleDataCache = sampleDataCache;
        manager = new CategoryInferenceManager();
    }

    /**
     * 
     * DOC zhao Inferring semantic category given sampling data and category recognizer.
     * 
     * @return a map of column index to list of categories <column idx, List<Semantic category>>
     */
    public void inferSemanticCategories() throws DQException {
        if (sampleDataCache != null) {
            for (Object[] record : sampleDataCache) {
                if (stopRequested) {
                    return;
                }
                manager.inferCategory(record);
                recordCursor++;
            }
        }
    }

    public Map<Integer, List<SemanticCategory>> getSemanticCategory() {
        return manager.getSemanticCategory();
    }

    public int getRecordCursor() {
        return recordCursor;
    }

    public boolean isStopRequested() {
        return stopRequested;
    }

    public void setStopRequested(boolean stopRequested) {
        this.stopRequested = stopRequested;
    }

}
