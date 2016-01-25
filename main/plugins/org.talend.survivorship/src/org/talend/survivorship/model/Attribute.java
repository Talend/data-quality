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

/**
 * represents a field of a Record.
 */
public class Attribute {

    private Record record;

    private String column;

    private Object value;

    private boolean alive = true;

    /**
     * this value increments at each time the Attribute is survived. By comparing this value with rule count, an
     * Attribute is alive is it survives from all the rules.
     */
    private int surviveCount = 0;

    /**
     * Attribute constructor.
     * 
     * @param column
     * @param value
     */
    public Attribute(Record record, String column, Object value) {
        this.record = record;
        this.column = column;
        this.value = value;
    }

    /**
     * Setter for alive.
     * 
     * @param alive
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * TODO sizhao explain the logic of this attribute.
     * 
     * Getter for alive.
     * 
     * @return the alive
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Getter for survivedTimes.
     * 
     * @return
     */
    public int getSurviveCount() {
        return surviveCount;
    }

    /**
     * Setter for value.
     * 
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Getter for value.
     * 
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the record.
     * 
     * @param record the record to set
     */
    public void setRecord(Record record) {
        this.record = record;
    }

    /**
     * Getter for record.
     * 
     * @return the record
     */
    public Record getRecord() {
        return record;
    }

    /**
     * Getter for record id.
     * 
     * @return the record
     */
    public int getRecordID() {
        return record.getId();
    }

    /**
     * Setter for column.
     * 
     * @param column
     */
    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * Getter for column.
     * 
     * @return
     */
    public String getColumn() {
        return column;
    }

    /**
     * increments survivedTimes.
     */
    public void survive() {
        surviveCount++;
    }

}
