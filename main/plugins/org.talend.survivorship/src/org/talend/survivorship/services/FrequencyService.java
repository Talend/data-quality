// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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
 * TODO sizhao create a Junit for this class.
 * 
 * This is the frequency service to use in rules.
 */
public class FrequencyService extends AbstractService {

    /**
     * FrequencyService constructor.
     * 
     * @param dataset
     */
    public FrequencyService(DataSet dataset) {
        super(dataset);
    }

    HashMap<String, HashMap<Object, Integer>> frequencyMaps = new HashMap<String, HashMap<Object, Integer>>();

    HashMap<String, Integer> maxOccurence = new HashMap<String, Integer>();

    /**
     * TODO sizhao document the input and returned values
     * 
     * Put attribute values into the frequencyMap of a given column.
     * 
     * @param column
     * @param ignoreBlanks
     * @return
     */
    public HashMap<Object, Integer> putAttributeValues(String column, boolean ignoreBlanks) {
        HashMap<Object, Integer> valueToFreq = frequencyMaps.get(column);

        valueToFreq = new HashMap<Object, Integer>();
        frequencyMaps.put(column, valueToFreq);
        maxOccurence.put(column, 1);

        for (Attribute attr : dataset.getAttributesByColumn(column)) {
            if (attr.isAlive()) {
                Object value = attr.getValue();
                if (value == null || (ignoreBlanks == true && "".equals(value.toString().trim()))) {
                    continue;
                }

                if (valueToFreq.get(value) == null) {
                    // add value to map
                    valueToFreq.put(value, 1);
                } else {
                    if (valueToFreq.get(value) == maxOccurence.get(column)) {
                        maxOccurence.put(column, maxOccurence.get(column) + 1);
                    }
                    // already exists: increment number of occurences
                    valueToFreq.put(value, valueToFreq.get(value) + 1);
                }
            }
        }
        return valueToFreq;
    }

    /**
     * Retrieve the most common value of a given column.
     * 
     * @param column
     * @param ignoreBlanks
     * @return
     */
    public HashSet<Object> getMostCommonValue(String column, boolean ignoreBlanks) {

        HashMap<Object, Integer> valueToFreq = frequencyMaps.get(column);

        if (valueToFreq == null) {
            valueToFreq = putAttributeValues(column, ignoreBlanks);
        }
        int max = maxOccurence.get(column);
        HashSet<Object> mostFrequentValues = new HashSet<Object>();

        for (Object obj : valueToFreq.keySet()) {
            int count = valueToFreq.get(obj);
            if (count == max) {
                mostFrequentValues.add(obj);
            }
        }

        return mostFrequentValues;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.services.AbstractService#init()
     */
    @Override
    public void init() {
    	frequencyMaps.clear();
    	maxOccurence.clear();
    }

}
