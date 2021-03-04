// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.statistics.numeric.quantile;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 * Quantile statistics bean with implementation of apache commons.
 * 
 * @author zhao
 *
 */
public class QuantileStatistics {

    private List<Double> data = new ArrayList<Double>();

    Median median = new Median();

    public void add(double value) {
        data.add(value);
    }

    public void endAddValue() {
        median.setData(data.stream().mapToDouble(x -> Double.valueOf(x)).toArray());
    }

    public double getMedian() {
        return median.evaluate(50);
    }

    public double getLowerQuartile() {
        return median.evaluate(25);
    }

    public double getUpperQuartile() {
        return median.evaluate(75);
    }

    /**
     * Get quantile value given percentage.
     * 
     * @param percentage the percentage of the quantile
     * @return quantile value given of specified percentage.
     */
    public double getQuantile(double percentage) {
        return median.evaluate(percentage * 100);
    }

    public List<Double> getData() {
        return data;
    }

}
