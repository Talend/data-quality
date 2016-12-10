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
package org.talend.survivorship.model;

import java.util.Collection;
import java.util.HashMap;

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

    private boolean resolved = true;

    private String survivingRuleName;

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

    public void setSurvivingRuleName(String survivingRuleName) {
        this.survivingRuleName = survivingRuleName;
    }

    public String getSurvivingRuleName() {
        return survivingRuleName;
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
     * initialize the column.
     */
    public void init() {
        resolved = true;
        attributeMap.clear();
    }

}
