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
package org.talend.dataquality.datamasking.functions;

/**
 * created by jgonzalez on 18 juin 2015. See NumericVariance.
 *
 */
public class NumericVarianceInteger extends NumericVariance<Integer> {

    private static final long serialVersionUID = -5691096627763244343L;

    @Override
    protected Integer doGenerateMaskedField(Integer i) {
        if (i == null) {
            return 0;
        } else {
            super.init();
            int value = getNonOverAddResult(i, getNonOverMultiResult(i, rate).intValue() / 100);
            return value;
        }
    }

    /**
     * 
     * Get result which value1 * value2 and handle over flow case
     * 
     * @param value1
     * @param value2
     * @return When result is over flow then get one value which nearby MAX_VALUE or MIN_VALUE
     */
    private Integer getNonOverMultiResult(int value1, int value2) {
        if (value1 == 0 || value2 == 0) {
            return 0;
        }
        if (value1 > 0 && value2 > 0) {

            if (value1 < Integer.MAX_VALUE / value2) {
                return value1 * value2;
            } else {
                return Integer.MAX_VALUE - Integer.MAX_VALUE % (value1 > value2 ? value1 : value2);
            }
        } else if (value1 < 0 && value2 < 0) {
            if (value1 != Integer.MIN_VALUE && Math.abs(value1) < Integer.MAX_VALUE / Math.abs(value2)) {
                return value1 * value2;
            } else {
                if (Integer.MIN_VALUE == value1 || Integer.MIN_VALUE == value2) {
                    return Integer.MAX_VALUE;
                }
                return Integer.MAX_VALUE - Integer.MAX_VALUE % (value1 < value2 ? value1 : value2);
            }
        } else {
            if (value1 != Integer.MIN_VALUE && Math.abs(value1) < Integer.MAX_VALUE / Math.abs(value2)) {
                return value1 * value2;
            } else {
                if (Integer.MIN_VALUE == value1 || Integer.MIN_VALUE == value2) {
                    return Integer.MIN_VALUE;
                }
                return Integer.MIN_VALUE - Integer.MIN_VALUE % (Math.abs(value1) > Math.abs(value2) ? value1 : value2);
            }
        }
    }

    /**
     * 
     * Get result which value1 + value2 and handle over flow case
     * 
     * @param value1
     * @param value2
     * @return When resule is over flow then change + or - to avoid over flow generate
     */
    private Integer getNonOverAddResult(int value1, int value2) {
        if (value1 > 0 && value2 > 0) {
            if (value1 < Integer.MAX_VALUE - value2) {
                return value1 + value2;
            } else {
                return Math.abs(value1 - value2);
            }
        } else if (value1 < 0 && value2 < 0) {
            if (value1 > Integer.MIN_VALUE - value2) {
                return value1 + value2;
            } else {
                return -Math.abs(value1 - value2);
            }
        } else {
            return value1 + value2;
        }
    }
}
