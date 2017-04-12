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

import java.util.Date;
import java.util.HashMap;

import org.talend.survivorship.model.Attribute;
import org.talend.survivorship.model.DataSet;

/**
 * Time Service to determine the latest, earliest value of a given column by predefined date format.
 */
public class TimeService extends AbstractService {

    protected HashMap<String, Date> latestValueMap;

    protected HashMap<String, Date> earliestValueMap;

    protected HashMap<String, Date> secondLatestValueMap;

    protected HashMap<String, Date> secondEarliestValueMap;

    /**
     * StringService constructor.
     * 
     * @param dataset
     */
    public TimeService(DataSet dataset) {
        super(dataset);
        latestValueMap = new HashMap<String, Date>();
        earliestValueMap = new HashMap<String, Date>();
        secondLatestValueMap = new HashMap<String, Date>();
        secondEarliestValueMap = new HashMap<String, Date>();
    }

    /**
     * Put attribute values into the longest/shortest value map of a given column.
     * 
     * @param column
     * @return
     */
    public void putAttributeValues(String column) {

        Date latest = null;
        Date earliest = null;
        Date secondLatest = null;
        Date secondEarliest = null;

        for (Attribute attr : dataset.getAttributesByColumn(column)) {
            if (attr.isAlive()) {
                Date value = (Date) attr.getValue();
                if (value == null) {
                    continue;
                }

                if (latest == null || earliest == null || secondLatest == null || secondEarliest == null) {
                    latest = value;
                    earliest = value;
                    secondLatest = value;
                    secondEarliest = value;

                } else {

                    if (value.after(latest)) {
                        secondLatest = latest;
                        latest = value;
                        // second input data is max then do that
                        if (secondLatest.equals(earliest)) {
                            secondEarliest = latest;
                        }
                    } else if (value.before(earliest)) {
                        secondEarliest = earliest;
                        earliest = value;
                        // second input data is max then do that
                        if (secondEarliest.equals(latest)) {
                            secondLatest = earliest;
                        }
                    }
                    if (value.before(latest) && value.after(secondLatest)) {
                        secondLatest = value;
                    }
                    if (value.after(earliest) && value.before(secondEarliest)) {
                        secondEarliest = value;
                    }
                }
            }
        }
        latestValueMap.put(column, latest);
        earliestValueMap.put(column, earliest);
        secondLatestValueMap.put(column, secondLatest);
        secondEarliestValueMap.put(column, secondEarliest);
    }

    /**
     * Determine if an object is the latest value of a given column.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isLatestValue(Object var, String column) {
        if (latestValueMap.get(column) == null) {
            putAttributeValues(column);
        }

        return var.equals(latestValueMap.get(column));
    }

    /**
     * Determine if an object is the earliest value of a given column.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isSecondEarliestValue(Object var, String column) {

        if (secondEarliestValueMap.get(column) == null) {
            putAttributeValues(column);
        }

        return var.equals(secondEarliestValueMap.get(column));
    }

    /**
     * Determine if an object is the second latest value of a given column.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isSecondLatestValue(Object var, String column) {

        if (secondLatestValueMap.get(column) == null) {
            putAttributeValues(column);
        }

        return var.equals(secondLatestValueMap.get(column));
    }

    /**
     * Determine if an object is the second earliest value of a given column.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isEarliestValue(Object var, String column) {

        if (earliestValueMap.get(column) == null) {
            putAttributeValues(column);
        }

        return var.equals(earliestValueMap.get(column));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.services.AbstractService#init()
     */
    @Override
    public void init() {
        latestValueMap.clear();
        earliestValueMap.clear();
        secondLatestValueMap.clear();
        secondEarliestValueMap.clear();
    }

}
