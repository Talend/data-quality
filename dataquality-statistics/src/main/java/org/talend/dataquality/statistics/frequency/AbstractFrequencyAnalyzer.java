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

import java.util.List;

import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.ResizableList;

/**
 * Frequency analyzer
 * 
 * @author mzhao
 *
 */
public abstract class AbstractFrequencyAnalyzer<T extends AbstractFrequencyStatistics> implements Analyzer<T> {

    private static final long serialVersionUID = 5073865267265592024L;

    protected ResizableList<T> freqTableStatistics;

    protected abstract void initFreqTableList(int size);

    @Override
    public void init() {
        if (freqTableStatistics != null) {
            freqTableStatistics.clear();
        }
    }

    @Override
    public boolean analyze(String... record) {
        if (record == null) {
            return true;
        }
        if (freqTableStatistics == null || freqTableStatistics.isEmpty()) {
            initFreqTableList(record.length);
        }
        for (int i = 0; i < record.length; i++) {
            T freqStats = freqTableStatistics.get(i);
            analyzeField(record[i], freqStats);
        }
        return true;
    }

    protected void analyzeField(String field, T freqStats) {
        freqStats.add(field);
    }

    @Override
    public void end() {
        // do nothing
    }

    @Override
    public List<T> getResult() {
        return freqTableStatistics;
    }

    @Override
    public void close() {
        // do nothing
    }

}
