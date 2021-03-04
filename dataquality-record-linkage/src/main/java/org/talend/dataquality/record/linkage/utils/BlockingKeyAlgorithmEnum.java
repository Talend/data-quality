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
 * 
 */
public enum BlockingKeyAlgorithmEnum {

    FIRST_CHAR_EW(0, "first character of each word", "first_Char_EW", false, StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$
    FIRST_N_CHAR_EW(1, "N first characters of each word", "first_N_Char_EW", true, "1"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    FIRST_N_CHAR(2, "first N characters of the string", "first_N_Char", true, "1"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    LAST_N_CHAR(3, "last N characters of the string", "last_N_Char", true, "1"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    FIRST_N_CONSONANTS(4, "first N consonants of the string", "first_N_Consonants", true, "1"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    FIRST_N_VOWELS(5, "first N vowels of the string", "first_N_Vowels", true, "1"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    PICK_CHAR(6, "pick characters", "pick_Char", true, StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$
    SUBSTR(7, "substring(a,b)", "subStr", true, StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$
    SOUNDEX(8, "soundex code", "soundex", false, StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$
    METAPHONE(9, "metaphone code", "metaphone", false, StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$
    D_METAPHONE(10, "double-metaphone code", "doublemetaphone", false, StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$
    EXACT(11, "exact", "exact", false, StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$
    FINGERPRINTKEY(12, "fingerPrintKey", "fingerPrintKey", false, StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$
    NGRAMKEY(13, "nGramKey", "nGramKey", false, StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$
    COLOGNEPHONETIC(14, "colognePhonetic", "colognePhonetic", false, StringUtils.EMPTY);//$NON-NLS-1$ //$NON-NLS-2$

    private int index;

    private String value;

    private String componentValueName;

    private boolean isTakeParameter;

    private String defaultValue;

    BlockingKeyAlgorithmEnum(int index, String value, String componentValueName, boolean isTakeParameter,
            String defaultValue) {
        this.index = index;
        this.value = value;
        this.componentValueName = componentValueName;
        this.isTakeParameter = isTakeParameter;
        this.defaultValue = defaultValue;
    }

    /**
     * Getter for defualtValue.
     * 
     * @return the defualtValue
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Sets the defualtValue.
     * 
     * @param defualtValue the defualtValue to set
     */
    public void setDefaultValue(String defualtValue) {
        this.defaultValue = defualtValue;
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
     * Getter for isTakeParameter.
     * 
     * @return the isTakeParameter
     */
    public boolean isTakeParameter() {
        return this.isTakeParameter;
    }

    /**
     * Getter for componentValueName.
     * 
     * @return the componentValueName
     */
    public String getComponentValueName() {
        return this.componentValueName;
    }

    public static String[] getAllTypes() {
        List<String> list = new ArrayList<String>();
        for (BlockingKeyAlgorithmEnum theType : values()) {
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
    public static BlockingKeyAlgorithmEnum getTypeByValue(String value) {
        for (BlockingKeyAlgorithmEnum element : BlockingKeyAlgorithmEnum.values()) {
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
    public static BlockingKeyAlgorithmEnum getTypeBySavedValue(String value) {
        for (BlockingKeyAlgorithmEnum element : BlockingKeyAlgorithmEnum.values()) {
            if (element.getComponentValueName().equalsIgnoreCase(value)) {
                return element;
            }
        }

        return EXACT;
    }

    /**
     * 
     * 
     * @param index
     * @return null can not find this index
     */
    public static BlockingKeyAlgorithmEnum getTypeByIndex(int index) {
        for (BlockingKeyAlgorithmEnum element : BlockingKeyAlgorithmEnum.values()) {
            if (element.getIndex() == index) {
                return element;
            }
        }

        return null;
    }

}
