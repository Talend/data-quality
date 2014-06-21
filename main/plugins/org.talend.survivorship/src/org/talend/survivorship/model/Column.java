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
import java.util.HashSet;

/**
 * This class represents a column from input.
 */
public class Column {

    private String name;

    private String dataType;

    /**
     * contains all attributes of the column in the group.
     */
    private HashMap<Record, Attribute> attributeMap = new HashMap<Record, Attribute>();

    private int ruleCount = 0;

    private boolean resolved = true;

    private HashSet<String> conflictDesc = new HashSet<String>();

    /**
     * Column constructor .
     * 
     * @param name
     * @param dataType
     */
    public Column(String name, String dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    /**
     * Getter for name.
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for dataType.
     * 
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Getter for attributes.
     * 
     * @return
     */
    public Collection<Attribute> getAttributes() {
        return attributeMap.values();
    }

    /**
     * Add an attribute to the column.
     * 
     * @param rec
     * @param attribute
     */
    public void putAttribute(Record rec, Attribute attribute) {
        attributeMap.put(rec, attribute);
    }

    /**
     * Setter for ruleCount.
     * 
     * @param ruleCount
     */
    public void setRuleCount(int ruleCount) {
        this.ruleCount = ruleCount;
    }

    /**
     * Getter for ruleCount.
     * 
     * @return
     */
    public int getRuleCount() {
        return ruleCount;
    }

    /**
     * Getter for resolved.
     * 
     * @return the resolved
     */
    public boolean isResolved() {
        return resolved;
    }

    /**
     * Sets the resolved.
     * 
     * @param resolved the resolved to set
     */
    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    /**
     * Getter for conflictDesc.
     * 
     * @return the conflictDesc
     */
    public HashSet<String> getConflictDesc() {
        return conflictDesc;
    }

    /**
     * initialize the column.
     */
    public void init() {
        resolved = true;
        attributeMap.clear();
        conflictDesc.clear();
    }

}
