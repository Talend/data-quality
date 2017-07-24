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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

import org.talend.dataquality.statistics.exception.DQStatisticsRuntimeException;

/**
 * Histogram statistics bean.
 * 
 * @author zhao
 *
 */
public class HistogramStatistics {

    private double min, max;

    private int numBins;

    private long countBelowMin, countAboveMax;

    private long[] result = new long[numBins];

    private BigDecimal binSize;

    int scale = 1000;

    /**
     * Set the parameters of the statistics <br>
     * Note that max must be greater than min
     * 
     * @param max
     * @param min
     * @param numBins number of bins , It should be a none zero integer.
     */
    public void setParameters(double max, double min, int numBins) {
        if (max <= min) {
            throw new DQStatisticsRuntimeException("max must be greater than min");
        }
        if (numBins <= 0) {
            throw new DQStatisticsRuntimeException(
                    "invalid numBins value :" + numBins + " , numBins must be a none zero integer");
        }
        this.max = max;
        this.min = min;
        this.numBins = numBins;
        binSize = BigDecimal.valueOf(max - min).divide(BigDecimal.valueOf(numBins), 10, RoundingMode.UP);
        result = new long[numBins];

    }

    public void add(double d) {
        double bin = BigDecimal.valueOf(d - min).divide(binSize, 10, RoundingMode.UP).doubleValue();
        if (bin < 0) { /* this data is smaller than min */
            countBelowMin++;
        } else if (bin > numBins) { /* this data point is bigger than max */
            countAboveMax++;
        } else {
            if (Double.compare(bin, numBins) == 0) {
                result[(int) bin - 1] += 1; // Include count of the upper boundary.
            } else {
                result[(int) bin] += 1;
            }
        }
    }

    /**
     * Get histograms as a map
     * 
     * @return the histogram map where Key is the range and value is the freqency. <br>
     * Note that the returned ranges are in pattern of [Min,
     * Min+binSize),[Min+binSize,Min+binSize*2)...[Max-binSize,Max<b>]</b>
     */
    public Map<Range, Long> getHistogram() {
        Map<Range, Long> histogramMap = new LinkedHashMap<Range, Long>();
        double currentMin = min;
        for (int i = 0; i < numBins; i++) {
            double currentMax = currentMin + binSize.doubleValue();
            if ((i + 1) == numBins) {
                currentMax = max;
            }
            Range r = new Range(currentMin, currentMax);
            histogramMap.put(r, result[i]);
            currentMin = currentMin + binSize.doubleValue();
        }
        return histogramMap;
    }

    /**
     * @return returns when all values in the histogram (no value is outside the given range)
     */
    public boolean isComplete() {
        return countBelowMin == 0 && countAboveMax == 0;
    }

    public long getCountBelowMin() {
        return countBelowMin;
    }

    public long getCountAboveMax() {
        return countAboveMax;
    }

}
