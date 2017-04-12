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
package org.talend.survivorship.services;

import java.util.HashMap;

import org.talend.survivorship.model.Attribute;
import org.talend.survivorship.model.DataSet;

/**
 * Service to determine the longest, shortest value, etc. of a given column.
 */
public class NumberService extends AbstractService {

    protected HashMap<String, Number> largestValueMap;

    protected HashMap<String, Number> smallestValueMap;

    protected HashMap<String, Number> secondLargestValueMap;

    protected HashMap<String, Number> secondSmallestValueMap;

    /**
     * StringService constructor.
     * 
     * @param dataset
     */
    public NumberService(DataSet dataset) {
        super(dataset);
        largestValueMap = new HashMap<>();
        smallestValueMap = new HashMap<>();
        secondLargestValueMap = new HashMap<>();
        secondSmallestValueMap = new HashMap<>();
    }

    /**
     * Put attribute values into the longest/shortest value map of a given column.
     * 
     * @param column
     * @return
     */
    public void putAttributeValues(String column) {
        Number max = null;
        Number min = null;
        Number secondMax = null;
        Number secondMin = null;
        for (Attribute attr : dataset.getAttributesByColumn(column)) {

            if (attr.isAlive()) {

                Number value = (Number) attr.getValue();
                if (value == null) {
                    continue;
                }

                if (max == null || min == null || secondMin == null || secondMax == null) {
                    max = value;
                    min = value;
                    secondMax = value;
                    secondMin = value;
                } else {
                    if (value.doubleValue() > max.doubleValue()) {
                        secondMax = max;
                        max = value;
                        // second input data is max then do that
                        if (secondMax.equals(min)) {
                            secondMin = max;
                        }
                    } else if (value.doubleValue() < min.doubleValue()) {
                        secondMin = min;
                        min = value;
                        // second input data is min then do that
                        if (secondMin.equals(max)) {
                            secondMax = min;
                        }
                    }

                    if (value.doubleValue() < max.doubleValue() && secondMax.doubleValue() < value.doubleValue()) {
                        secondMax = value;
                    }
                    if (value.doubleValue() > min.doubleValue() && secondMin.doubleValue() > value.doubleValue()) {
                        secondMin = value;
                    }
                }
            }
        }
        largestValueMap.put(column, max);
        smallestValueMap.put(column, min);
        secondLargestValueMap.put(column, secondMax);
        secondSmallestValueMap.put(column, secondMin);
    }

    /**
     * Determine if an object is the longest value of a given column.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isLargestValue(Object var, String column) {

        if (largestValueMap.get(column) == null) {
            putAttributeValues(column);
        }

        return largestValueMap.get(column).equals(var);
    }

    /**
     * Determine if an object is the shortest value of a given column.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isSmallestValue(Object var, String column) {

        if (smallestValueMap.get(column) == null) {
            putAttributeValues(column);
        }

        return smallestValueMap.get(column).equals(var);
    }

    /**
     * Determine if an object is the second largest value of a given column.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isSecondLargestValue(Object var, String column) {

        if (secondLargestValueMap.get(column) == null) {
            putAttributeValues(column);
        }

        return secondLargestValueMap.get(column).equals(var);
    }

    /**
     * Determine if an object is the second smallest value of a given column.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isSecondSmallestValue(Object var, String column) {

        if (secondSmallestValueMap.get(column) == null) {
            putAttributeValues(column);
        }

        return secondSmallestValueMap.get(column).equals(var);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.services.AbstractService#init()
     */
    @Override
    public void init() {
        largestValueMap.clear();
        smallestValueMap.clear();
        secondLargestValueMap.clear();
        secondSmallestValueMap.clear();
    }

}
