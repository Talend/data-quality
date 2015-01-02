// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import java.util.HashSet;

import org.talend.survivorship.model.Attribute;
import org.talend.survivorship.model.DataSet;

/**
 * Service to determine the longest, shortest value, etc. of a given column.
 */
public class StringService extends AbstractService {

    HashMap<String, HashSet<String>> longestValueMap;

    HashMap<String, HashSet<String>> shortestValueMap;

    /**
     * StringService constructor.
     * 
     * @param dataset
     */
    public StringService(DataSet dataset) {
        super(dataset);
        longestValueMap = new HashMap<String, HashSet<String>>();
        shortestValueMap = new HashMap<String, HashSet<String>>();
    }

    /**
     * Put attribute values into the longest/shortest value map of a given column.
     * 
     * @param column
     * @param ignoreBlanks
     * @param ignoreBlanks
     * @return
     */
    public void putAttributeValues(String column, boolean ignoreBlanks) {

        HashSet<String> longestValues = new HashSet<String>();
        longestValueMap.put(column, longestValues);

        HashSet<String> shortestValues = new HashSet<String>();
        shortestValueMap.put(column, shortestValues);

        int max = 0;
        int min = -1;
        for (Attribute attr : dataset.getAttributesByColumn(column)) {

            if (attr.isAlive()) {
                String value = (String) attr.getValue();
                if (value == null || (ignoreBlanks && "".equals(value.trim()))) { //$NON-NLS-1$
                    continue;
                }
                int length = value.length();
                if (length > max) {
                    longestValues.clear();
                    longestValues.add(value);
                    max = length;
                } else if (length == max) {
                    longestValues.add(value);
                }

                if (length < min || min == -1) {
                    shortestValues.clear();
                    shortestValues.add(value);
                    min = length;
                } else if (length == min) {
                    shortestValues.add(value);
                }
            }
        }
    }

    /**
     * Determine if an object is the longest value of a given column.
     * 
     * @param var
     * @param column
     * @param ignoreBlanks
     * @return
     */
    public boolean isLongestValue(Object var, String column, boolean ignoreBlanks) {
        if (longestValueMap.get(column) == null) {
            putAttributeValues(column, ignoreBlanks);
        }
        return longestValueMap.get(column).contains(var);
    }

    /**
     * Determine if an object is the shortest value of a given column.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isShortestValue(Object var, String column, boolean ignoreBlanks) {
        if (shortestValueMap.get(column) == null) {
            putAttributeValues(column, ignoreBlanks);
        }
        return shortestValueMap.get(column).contains(var);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.services.AbstractService#init()
     */
    @Override
    public void init() {
        longestValueMap.clear();
        shortestValueMap.clear();
    }

}
