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
 * <p>
 * TODO complete StringService
 */
public class NumberService extends AbstractService {

    HashMap<String, Number> largestValueMap;

    HashMap<String, Number> smallestValueMap;

    /**
     * StringService constructor.
     * 
     * @param dataset
     */
    public NumberService(DataSet dataset) {
        super(dataset);
        largestValueMap = new HashMap<String, Number>();
        smallestValueMap = new HashMap<String, Number>();
    }

    /**
     * Put attribute values into the longest/shortest value map of a given column.
     * 
     * @param column
     * @return
     */
    public void putAttributeValues(String column) {
        Number max = null, min = null;
        for (Attribute attr : dataset.getAttributesByColumn(column)) {

            if (attr.isAlive()) {

                Number value = (Number) attr.getValue();
                if (value == null) {
                    continue;
                }

                if (max == null || min == null) {
                    max = value;
                    min = value;
                } else {
                    if (value.doubleValue() > max.doubleValue()) {
                        max = value;
                    } else if (value.doubleValue() < min.doubleValue()) {
                        min = value;
                    }
                }
            }
        }
        largestValueMap.put(column, max);
        smallestValueMap.put(column, min);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.services.AbstractService#init()
     */
    @Override
    public void init() {
        largestValueMap.clear();
        smallestValueMap.clear();
    }

}
