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
package org.talend.dataquality.record.linkage.utils;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.record.linkage.Messages;

/**
 * the values of the match key's handle null field
 * 
 */
public enum HandleNullEnum {
    NULL_MATCH_NULL("nullMatchNull", Messages.getString("HandleNullEnum.NULL_MATCH_NULL")), //$NON-NLS-1$ //$NON-NLS-2$
    NULL_MATCH_NONE("nullMatchNone", Messages.getString("HandleNullEnum.NULL_MATCH_NONE")), //$NON-NLS-1$ //$NON-NLS-2$
    NULL_MATCH_ALL("nullMatchAll", Messages.getString("HandleNullEnum.NULL_MATCH_ALL")); //$NON-NLS-1$ //$NON-NLS-2$

    private String componentValue;

    private String label;

    HandleNullEnum(String componentValue, String displayValue) {
        this.componentValue = componentValue;
        this.label = displayValue;
    }

    /**
     * Getter for value.
     * 
     * @return the value
     */
    public String getValue() {
        return this.componentValue;
    }

    public static String[] getAllTypes() {
        List<String> list = new ArrayList<String>();
        for (HandleNullEnum theType : values()) {
            list.add(theType.getLabel());
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * 
     * get type of the value which in this Enum
     * 
     * @param value
     * @return null can not find this index
     */
    public static HandleNullEnum getTypeByValue(String value) {
        for (HandleNullEnum element : HandleNullEnum.values()) {
            if (element.getValue().equalsIgnoreCase(value)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Getter for label.
     * 
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }

}
