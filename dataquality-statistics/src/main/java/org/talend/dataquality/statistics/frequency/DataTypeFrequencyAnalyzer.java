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
package org.talend.dataquality.statistics.frequency;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.common.inference.ResizableList;
import org.talend.dataquality.statistics.frequency.impl.CMSFrequencyEvaluator;
import org.talend.dataquality.statistics.frequency.impl.EFrequencyAlgorithm;
import org.talend.dataquality.statistics.frequency.impl.NaiveFrequencyEvaluator;
import org.talend.dataquality.statistics.frequency.impl.SSFrequencyEvaluator;

/**
 * Frequency analyzer which delegate the computation to {@link NaiveFrequencyEvaluator} , {@link SSFrequencyEvaluator}
 * and {@link CMSFrequencyEvaluator} by specify the algorithm of {@link EFrequencyAlgorithm#NAIVE} ,
 * {@link EFrequencyAlgorithm#SPACE_SAVER} and {@link EFrequencyAlgorithm#COUNT_MIN_SKETCH}
 * 
 * @author mzhao
 *
 */
public class DataTypeFrequencyAnalyzer extends AbstractFrequencyAnalyzer<DataTypeFrequencyStatistics> {

    private static final long serialVersionUID = 1333273197291146797L;

    @Override
    protected void initFreqTableList(int size) {
        List<DataTypeFrequencyStatistics> freqTableList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            DataTypeFrequencyStatistics freqTable = new DataTypeFrequencyStatistics();
            freqTable.setAlgorithm(algorithm);
            freqTableList.add(freqTable);
        }
        freqTableStatistics = new ResizableList<>(freqTableList);
    }

}
