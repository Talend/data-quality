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

    private Column column;

    private Object value;

    /**
     * this value indicates if the current attribute is eliminated by one rule.
     */
    private boolean alive = true;

    /**
     * this value indicates if the current attribute is survived by one rule.
     */
    private boolean survived = false;

    /**
     * Attribute constructor.
     * 
     * @param record
     * @param column
     * @param value
     */
    public Attribute(Record record, Column column, Object value) {
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
     * Getter for alive.
     * 
     * @return the alive
     */
    public boolean isAlive() {
        return alive;
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
    public void setColumn(Column column) {
        this.column = column;
    }

    /**
     * Getter for column.
     * 
     * @return
     */
    public Column getColumn() {
        return column;
    }

    public boolean isSurvived() {
        return survived;
    }

    public void setSurvived(boolean survived) {
        this.survived = survived;
    }

    @Override
    public String toString() {
        return "Attribute[" + "record=" + record.getId() + ",  column=" + column.getName() + ", value=" + value + "]"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$
    }

}
