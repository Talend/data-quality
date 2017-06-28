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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Parameters bean used by histogram analyzer.
 * 
 * @author zhao
 *
 */
public class HistogramParameter implements Serializable {

    private static final long serialVersionUID = -1315253782432071580L;

    // -- default values of min, max and number of bins --
    private double defaultMin = Double.MIN_VALUE;

    private double defaultMax = Double.MAX_VALUE;

    private int defaultNumBins = 10;

    // -- specific parameters of each column --
    /**
     * The map is maintaining column index to parameter set relationship. Key of the map is column index starting from
     * zero while value is the parameters min,max and number of bins.
     */
    private Map<Integer, HistogramColumnParameter> columnParameters = new HashMap<>();// NOSONAR

    public double getDefaultMin() {
        return defaultMin;
    }

    public double getDefaultMax() {
        return defaultMax;
    }

    public int getDefaultNumBins() {
        return defaultNumBins;
    }

    public void setDefaultParameters(double defaultMin, double defaultMax, int defaultNumBins) {
        if (defaultMax < defaultMin || defaultNumBins < 0)
            throw new IllegalArgumentException("Max = " + defaultMax + ", Min = " + defaultMin + ", numBins =" + defaultNumBins
                    + "\n Max must greater than Min and numBins must be a positive integers(>0)!");
        this.defaultMin = defaultMin;
        this.defaultMax = defaultMax;
        this.defaultNumBins = defaultNumBins;
    }

    /**
     * Add parameter to column
     * 
     * @param columnIdx column index starting from zero.
     * @param columnParameter column parameter bean.
     */
    public void putColumnParameter(Integer columnIdx, HistogramColumnParameter columnParameter) {
        columnParameters.put(columnIdx, columnParameter);
    }

    /**
     * Get histogram parameter of given column index.
     * 
     * @param columnIndex column index of which column the parameters obtained.
     * @return {{@link HistogramColumnParameter} of this column, or null if not specified.
     */
    public HistogramColumnParameter getColumnParameter(Integer columnIndex) {
        return columnParameters.get(columnIndex);
    }
}
