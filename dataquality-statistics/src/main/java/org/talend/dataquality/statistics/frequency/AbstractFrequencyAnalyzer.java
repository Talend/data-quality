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

import org.apache.commons.lang.NotImplementedException;
import org.talend.dataquality.common.inference.Analyzer;
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
public abstract class AbstractFrequencyAnalyzer<T extends AbstractFrequencyStatistics> implements Analyzer<T> {

    private static final long serialVersionUID = 5073865267265592024L;

    protected ResizableList<T> freqTableStatistics = null;

    protected EFrequencyAlgorithm algorithm = EFrequencyAlgorithm.NAIVE;

    /**
     * Set the algorithm used to compute the frequency table.
     * 
     * @param algorithm
     */
    public void setAlgorithm(EFrequencyAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

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
            AbstractFrequencyStatistics freqStats = freqTableStatistics.get(i);
            analyzeField(record[i], freqStats);
        }
        return true;
    }

    protected void analyzeField(String field, AbstractFrequencyStatistics freqStats) {
        freqStats.add(field);
    }

    @Override
    public void end() {
    }

    @Override
    public Analyzer<T> merge(Analyzer<T> another) {
        throw new NotImplementedException();
    }

    @Override
    public List<T> getResult() {
        return freqTableStatistics;
    }

    @Override
    public void close() throws Exception {

    }

}
