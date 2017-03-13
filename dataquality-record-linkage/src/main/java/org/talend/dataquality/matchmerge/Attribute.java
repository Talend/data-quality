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

/**
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.talend.dataquality.matchmerge;

import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.collections.iterators.IteratorChain;

/**
 * A attribute is a "column" in a {@link org.talend.dataquality.matchmerge.Record record}.
 */
public class Attribute {

    private final String label;

    /**
     * The index of the column in a record.
     */
    private final int columnIndex;

    private String value;

    private final AttributeValues<String> values = new AttributeValues<String>();

    public Attribute(String label) {
        this.label = label;
        columnIndex = 0;
    }

    public Attribute(String label, int colIdx) {
        this.label = label;
        this.columnIndex = colIdx;
    }

    public Attribute(String label, int colIdx, String value) {
        this.label = label;
        this.columnIndex = colIdx;
        this.value = value;
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
     * Set the merged column value.
     * 
     * @param value A string value for the column. For custom types, provide a consistent representation of the data
     * since the string is used for match.
     */
    public void setValue(String value) {
        this.value = value;
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

}
