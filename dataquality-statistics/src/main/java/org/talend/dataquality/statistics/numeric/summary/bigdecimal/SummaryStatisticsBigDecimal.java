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
package org.talend.dataquality.statistics.numeric.summary.bigdecimal;

import java.math.BigDecimal;

/**
 * Summary statistics bean
 */
public class SummaryStatisticsBigDecimal {

    private BigDecimal max;

    private BigDecimal min;

    private BigDecimal variance;

    private BigDecimal sum;

    private Long valuesCount = 0L;

    /**
     * Add the data to memory so that the variance can be computed given this list.
     *
     * @param value field valued added to the list.<br>
     *              See more about add value mehtod
     */
    public void addData(BigDecimal value) {
        if (!containAValue()) {
            this.max = value;
            this.min = value;
            this.variance = value.pow(2);
            this.sum = value;
        } else {
            if (min.compareTo(value) > 0) {
                this.min = value;
            }
            if (max.compareTo(value) < 0) {
                this.max = value;
            }
            sum = sum.add(value);
            this.variance = variance.add(value.pow(2));
        }
        valuesCount++;
    }

    private boolean containAValue() {
        return valuesCount > 0;
    }

    public BigDecimal getMin() {
        return this.min;
    }

    public BigDecimal getMax() {
        return this.max;
    }

    public BigDecimal getMean() {
        return sum.setScale(4, BigDecimal.ROUND_HALF_UP).divide(BigDecimal.valueOf(valuesCount));
    }

    public BigDecimal getVariance() {
        BigDecimal sum2 = sum.pow(2);
        BigDecimal sum2DivideByCount = sum2.setScale(4, BigDecimal.ROUND_HALF_UP).divide(BigDecimal.valueOf(valuesCount));
        BigDecimal v = variance.subtract(sum2DivideByCount);

        return v.divide(BigDecimal.valueOf(valuesCount - 1));
    }

    public BigDecimal getSum() {
        return this.sum;
    }

    public boolean isValid() {
        return containAValue();
    }

}
