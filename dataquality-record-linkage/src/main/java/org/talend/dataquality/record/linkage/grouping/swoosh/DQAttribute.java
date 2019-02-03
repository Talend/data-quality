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
package org.talend.dataquality.record.linkage.grouping.swoosh;

import org.talend.dataquality.matchmerge.Attribute;

/**
 * created by zhao on Jul 14, 2014 Detailled comment
 * 
 */
public class DQAttribute<TYPE extends Object> extends Attribute {

    private TYPE originalValue;

    /**
     * DOC zhao DQAtrribute constructor comment.
     * 
     * @param label
     */
    public DQAttribute(String label) {
        super(label);
    }

    public DQAttribute(String label, int colIdx) {
        super(label, colIdx);
    }

    /**
     * DOC zhao DQAttribute constructor comment.
     */
    public DQAttribute(String label, int colIdx, TYPE value) {
        this(label, colIdx);
        if (value != null) {
            setValue(value.toString());
            setOriginalValue(value);
        }
    }

    /**
     * The constructor of DQAttribute class.
     */
    public DQAttribute(String label, int colIdx, TYPE value, int referenceIndex) {
        this(label, colIdx, value);
        setReferenceColumnIndex(referenceIndex);
    }

    /**
     * Getter for originalValue.
     * 
     * @return the originalValue
     */
    public TYPE getOriginalValue() {
        return this.originalValue;
    }

    /**
     * Sets the originalValue.
     * 
     * @param originalValue the originalValue to set
     */
    public void setOriginalValue(TYPE originalValue) {
        this.originalValue = originalValue;
    }

}
