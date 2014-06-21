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

import java.util.Date;
import java.util.HashMap;

import org.talend.survivorship.model.Attribute;
import org.talend.survivorship.model.DataSet;

/**
 * Time Service to determine the latest, earliest value of a given column by predefined date format.
 * <p>
 * TODO complete Time Service
 */
public class TimeService extends AbstractService {

    HashMap<String, Date> latestValueMap;

    HashMap<String, Date> earliestValueMap;

    /**
     * StringService constructor.
     * 
     * @param dataset
     */
    public TimeService(DataSet dataset) {
        super(dataset);
        latestValueMap = new HashMap<String, Date>();
        earliestValueMap = new HashMap<String, Date>();
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

        for (Attribute attr : dataset.getAttributesByColumn(column)) {
            if (attr.isAlive()) {
                Date value = (Date) attr.getValue();
                if (value == null) {
                    continue;
                }

                if (latest == null || value.after(latest)) {
                    latest = value;
                }
                if (earliest == null || value.before(earliest)) {
                    earliest = value;
                }
            }
        }
        latestValueMap.put(column, latest);
        earliestValueMap.put(column, earliest);
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
    }

}
