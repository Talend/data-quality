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

/**
 * Frequency analyzer
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
            freqTableList.add(freqTable);
        }
        freqTableStatistics = new ResizableList<>(freqTableList);
    }

}
