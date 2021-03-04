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
package org.talend.dataquality.statistics.numeric.histogram;

/**
 * Histogram parameters of column.
 * 
 * @author zhao
 *
 */
public class HistogramColumnParameter {

    private double min = Double.MIN_VALUE;

    private double max = Double.MAX_VALUE;

    private int numBins = 10;

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public int getNumBins() {
        return numBins;
    }

    public void setParameters(double min, double max, int numBins) {
        if (max < min || numBins < 0)
            throw new IllegalArgumentException("Max = " + max + ", Min = " + min + ", numBins =" + numBins
                    + "\n Max must greater than Min and numBins must be a positive integers(>0)!");
        this.min = min;
        this.max = max;
        this.numBins = numBins;
    }

}
