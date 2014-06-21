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
package org.talend.survivorship.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Represents one input line from the job.
 */
public class Record {

    /**
     * ID of the record
     */
    private Integer id;

    /**
     * Contains attributes of a Record.
     */
    private HashMap<String, Attribute> attributeMap = new LinkedHashMap<String, Attribute>();

    /**
     * This value increments when one of the Attribute is survived. A @Record having more survivors is considered more
     * credible. it will be used to automatically resolve conflicts.
     */
    private int survivorCount;

    /**
     * Getter for id.
     * 
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Put an attribute into attributeMap.
     * 
     * @param col
     * @param attribute
     */
    public void putAttribute(String col, Attribute attribute) {
        attributeMap.put(col, attribute);
    }

    /**
     * Get an attribute from the record.
     * 
     * @param col
     * @return
     */
    public Attribute getAttribute(String col) {
        return attributeMap.get(col);
    }

    /**
     * Getter for all attributes in the data set.
     * 
     * @return
     */
    public Collection<Attribute> getAttributes() {
        return attributeMap.values();
    }

    /**
     * Count the number of survived attributes in a record.
     * 
     * @return
     */
    public int getSurvivorCount() {
        return survivorCount;
    }

}
