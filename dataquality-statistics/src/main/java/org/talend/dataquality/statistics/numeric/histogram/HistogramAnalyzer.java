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
package org.talend.dataquality.statistics.numeric.histogram;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.talend.daikon.number.BigDecimalParser;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.ResizableList;
import org.talend.dataquality.statistics.numeric.NumericalStatisticsAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeEnum;
import org.talend.dataquality.statistics.type.TypeInferenceUtils;

/**
 * Analyzer the histograms.<br>
 * Note that the min,max and number of bins parameters are needed.<br>
 * If min max and number of bins are not set, then default values are used. (As defined in {@link HistogramParameter})
 * 
 * 
 * @author zhao
 *
 */
public class HistogramAnalyzer extends NumericalStatisticsAnalyzer<HistogramStatistics> {

    private static final long serialVersionUID = -3756520692420812485L;

    private ResizableList<HistogramStatistics> stats = new ResizableList<>(HistogramStatistics.class);

    private HistogramParameter histogramParameter = null;

    /**
     * 
     * @param types data types
     * @param histogramParameter Histogram analzyer's parameter
     */
    public HistogramAnalyzer(DataTypeEnum[] types, HistogramParameter histogramParameter) {
        super(types);
        if (histogramParameter == null) {
            throw new IllegalArgumentException("Histogram analyzer's parameter should is null.");
        }
        setParameters(histogramParameter);
    }

    /**
     * Set histogram analyzer's parameters
     * 
     * @param histogramParameter Histogram analzyer's parameter
     */
    private void setParameters(HistogramParameter histogramParameter) {
        this.histogramParameter = histogramParameter;
    }

    @Override
    public boolean analyze(String... record) {
        DataTypeEnum[] types = getTypes();

        if (record.length != types.length)
            throw new IllegalArgumentException("Each column of the record should be declared a DataType.Type corresponding! \n"
                    + types.length + " type(s) declared in this histogram analyzer but " + record.length
                    + " column(s) was found in this record. \n"
                    + "Using method: setTypes(DataType.Type[] types) to set the types. ");

        if (stats.resize(record.length)) {
            int colIdx = 0;
            for (HistogramStatistics stat : stats) {
                HistogramColumnParameter columnParameter = histogramParameter.getColumnParameter(colIdx);
                // Set column parameters to histogram statistics.
                double max = histogramParameter.getDefaultMax();
                double min = histogramParameter.getDefaultMin();
                int numBins = histogramParameter.getDefaultNumBins();
                if (columnParameter != null) {
                    min = columnParameter.getMin();
                    max = columnParameter.getMax();
                    numBins = columnParameter.getNumBins();
                }
                stat.setParameters(max, min, numBins);
                colIdx++;
            }
        }

        for (int idx : this.getStatColIdx()) { // analysis each numerical column in the record
            if (!TypeInferenceUtils.isValid(types[idx], record[idx])) {
                continue;
            }
            analyzerHistogram(idx, record);
        }
        return true;
    }

    private void analyzerHistogram(int index, String... record) {
        HistogramStatistics histStats = stats.get(index);
        try {
            histStats.add(BigDecimalParser.toBigDecimal(record[index]).doubleValue());
        } catch (NumberFormatException e) {
            // skip
        }
    }

    @Override
    public Analyzer<HistogramStatistics> merge(Analyzer<HistogramStatistics> another) {
        throw new NotImplementedException();
    }

    @Override
    public void end() {
    }

    @Override
    public List<HistogramStatistics> getResult() {
        return stats;
    }

}
