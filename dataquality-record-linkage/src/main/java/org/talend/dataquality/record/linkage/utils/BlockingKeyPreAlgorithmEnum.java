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
package org.talend.dataquality.record.linkage.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * the algorithms used by the blocking key: (index, displayed value, value used in code only, use parameter or
 * not,default value)
 */
public enum BlockingKeyPreAlgorithmEnum {
    NON_ALGO(0, "-", "NON_ALGO", false, StringUtils.EMPTY), //$NON-NLS-1$//$NON-NLS-2$
    REMOVE_MARKS(1, "remove diacritical marks", "removeDiacriticalMarks", false, StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$
    REMOVE_MARKS_THEN_LOWER_CASE(
            2,
            "remove diacritical marks and lower case", //$NON-NLS-1$
            "removeDMAndLowerCase", //$NON-NLS-1$
            false,
            StringUtils.EMPTY),
    REMOVE_MARKS_THEN_UPPER_CASE(
            3,
            "remove diacritical marks and upper case", //$NON-NLS-1$
            "removeDMAndUpperCase", //$NON-NLS-1$
            false,
            StringUtils.EMPTY),
    LOWER_CASE(4, "lower case", "lowerCase", false, StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$
    UPPER_CASE(5, "upper case", "upperCase", false, StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$
    LEFT_CHAR(6, "add left position character", "add_Left_Char", true, StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$ 
    RIGHT_CHAR(7, "add right position character", "add_Right_Char", true, StringUtils.EMPTY); //$NON-NLS-1$ //$NON-NLS-2$ 

    private int index;

    private String value;

    private String componentValueName;

    private boolean isTakeParameter;

    private String defaultValue;

    BlockingKeyPreAlgorithmEnum(int index, String value, String componentValueName, boolean isTakeParameter,
            String defaultValue) {
        this.index = index;
        this.value = value;
        this.componentValueName = componentValueName;
        this.isTakeParameter = isTakeParameter;
        this.defaultValue = defaultValue;
    }

    /**
     * Getter for componentValueName.
     * 
     * @return the componentValueName
     */
    public String getComponentValueName() {
        return this.componentValueName;
    }

    /**
     * Getter for index.
     * 
     * @return the index
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Getter for value.
     * 
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Getter for defaultValue.
     * 
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Sets the defaultValue.
     * 
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Getter for isTakeParameter.
     * 
     * @return the isTakeParameter
     */
    public boolean isTakeParameter() {
        return this.isTakeParameter;
    }

    public static String[] getAllTypes() {
        List<String> list = new ArrayList<String>();
        for (BlockingKeyPreAlgorithmEnum theType : values()) {
            list.add(theType.getValue());
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
    public static BlockingKeyPreAlgorithmEnum getTypeByValue(String value) {
        for (BlockingKeyPreAlgorithmEnum element : BlockingKeyPreAlgorithmEnum.values()) {
            if (element.getValue().equalsIgnoreCase(value)) {
                return element;
            }
        }

        return null;
    }

    /**
     * 
     * get type of the value which in this Enum
     * 
     * @param value
     * @return null can not find this index
     */
    public static BlockingKeyPreAlgorithmEnum getTypeBySavedValue(String value) {
        for (BlockingKeyPreAlgorithmEnum element : BlockingKeyPreAlgorithmEnum.values()) {
            if (element.getComponentValueName().equalsIgnoreCase(value)) {
                return element;
            }
        }
        return NON_ALGO;
    }

    /**
     * 
     * 
     * @param index
     * @return null can not find this index
     */
    public static BlockingKeyPreAlgorithmEnum getTypeByIndex(int index) {
        for (BlockingKeyPreAlgorithmEnum element : BlockingKeyPreAlgorithmEnum.values()) {
            if (element.getIndex() == index) {
                return element;
            }
        }

        return null;
    }

}
