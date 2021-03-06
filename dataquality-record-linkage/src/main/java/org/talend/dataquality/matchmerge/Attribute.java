// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

/**
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.talend.dataquality.matchmerge;

import org.apache.commons.collections.iterators.IteratorChain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;

/**
 * A attribute is a "column" in a {@link org.talend.dataquality.matchmerge.Record record}.
 */
public class Attribute implements Serializable {

    private static final long serialVersionUID = -4408981541104332570L;

    private final String label;

    /**
     * The index of the column in a record.
     */
    private final int columnIndex;

    /**
     * The index of the reference column in a record.
     */
    private int referenceColumnIndex;

    private String referenceValue;

    private String value;

    private final AttributeValues<String> values = new AttributeValues<String>();

    public Attribute(String label) {
        this(label, 0, null, 0);
    }

    public Attribute(String label, int colIdx) {
        this(label, colIdx, null, colIdx);

    }

    public Attribute(String label, int colIdx, String value) {
        this(label, colIdx, value, colIdx);
    }

    public Attribute(String label, int colIdx, String value, int referenceIdx) {
        this.label = label;
        this.columnIndex = colIdx;
        this.value = value;
        this.referenceColumnIndex = referenceIdx;
    }

    /**
     * @return The column name.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return The column's value (always as string, never typed).
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the value which used to be compare with others
     * 
     * @return value if reference column is not valid else return referenceValue
     */
    public String getCompareValue() {
        if (columnIndex == referenceColumnIndex) {
            return value;
        }
        return referenceValue;
    }

    /**
     * Set the merged column value.
     * 
     * @param value A string value for the column. For custom types, provide a consistent representation of the data
     * since the string is used for match.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Getter for referencevalue.
     * 
     * @return the referencevalue
     */
    public String getReferenceValue() {
        return this.referenceValue;
    }

    /**
     * Sets the referencevalue.
     * 
     * @param referencevalue the referencevalue to set
     */
    public void setReferenceValue(String referencevalue) {
        this.referenceValue = referencevalue;
    }

    /**
     * Getter for referenceColumnIndex.
     * 
     * @return the referenceColumnIndex
     */
    public int getReferenceColumnIndex() {
        return this.referenceColumnIndex;
    }

    /**
     * Getter for columnIndex.
     * 
     * @return the columnIndex
     */
    public int getColumnIndex() {
        return this.columnIndex;
    }

    /**
     * @return All the values that lead to the merged value (i.e. the value returned by {@link #getValue()}).
     */
    public AttributeValues<String> getValues() {
        return values;
    }

    public Iterator<String> allValues() {
        return new IteratorChain(new Iterator[] { Collections.singleton(value).iterator(), values.iterator() });
    }

    /**
     * Sets the referenceColumnIndex.
     * 
     * @param referenceColumnIndex the referenceColumnIndex to set
     */
    protected void setReferenceColumnIndex(int referenceColumnIndex) {
        this.referenceColumnIndex = referenceColumnIndex;
    }

}
