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

/**
 * (index, displayed value, value used in code only)
 */
public enum SurvivorShipAlgorithmEnum {

    CONCATENATE(
            0,
            "Concatenate", //$NON-NLS-1$
            "Concatenate", //$NON-NLS-1$
            DefaultSurvivorShipDataTypeEnum.BOOLEAN,
            DefaultSurvivorShipDataTypeEnum.DATE,
            DefaultSurvivorShipDataTypeEnum.STRING),
    PREFER_TRUE(1, "Prefer True (for booleans)", "PreferTrue", DefaultSurvivorShipDataTypeEnum.BOOLEAN), //$NON-NLS-1$ //$NON-NLS-2$
    PREFER_FALSE(2, "Prefer False (for booleans)", "PreferFalse", DefaultSurvivorShipDataTypeEnum.BOOLEAN), //$NON-NLS-1$ //$NON-NLS-2$
    MOST_COMMON(3, "Most common", "MostCommon"), //$NON-NLS-1$ //$NON-NLS-2$
    MOST_RECENT(4, "Most recent", "MostRecent", DefaultSurvivorShipDataTypeEnum.DATE), //$NON-NLS-1$ //$NON-NLS-2$
    MOST_ANCIENT(5, "Most ancient", "MostAncient", DefaultSurvivorShipDataTypeEnum.DATE), //$NON-NLS-1$ //$NON-NLS-2$
    LONGEST(6, "Longest (for strings)", "Longest", DefaultSurvivorShipDataTypeEnum.STRING), //$NON-NLS-1$ //$NON-NLS-2$
    SHORTEST(7, "Shortest (for strings)", "Shortest", DefaultSurvivorShipDataTypeEnum.STRING), //$NON-NLS-1$ //$NON-NLS-2$
    LARGEST(8, "Largest (for numbers)", "Largest", DefaultSurvivorShipDataTypeEnum.NUMBER), //$NON-NLS-1$ //$NON-NLS-2$
    SMALLEST(9, "Smallest (for numbers)", "Smallest", DefaultSurvivorShipDataTypeEnum.NUMBER), //$NON-NLS-1$ //$NON-NLS-2$
    MOST_TRUSTED_SOURCE(10, "Most trusted source", "MostTrustedSource"); //$NON-NLS-1$ //$NON-NLS-2$

    private int index;

    private String value;

    private String componentValueName;

    private DefaultSurvivorShipDataTypeEnum[] dataType;

    SurvivorShipAlgorithmEnum(int index, String value, String componentValueName, DefaultSurvivorShipDataTypeEnum... dataType) {
        this.index = index;
        this.value = value;
        this.componentValueName = componentValueName;
        this.dataType = dataType;
    }

    public int getIndex() {
        return this.index;
    }

    public String getValue() {
        return this.value;
    }

    public String getComponentValueName() {
        return this.componentValueName;
    }

    public static String[] getAllTypes() {
        List<String> list = new ArrayList<String>();
        for (SurvivorShipAlgorithmEnum theType : values()) {
            list.add(theType.getValue());
        }
        return list.toArray(new String[list.size()]);
    }

    public static SurvivorShipAlgorithmEnum getTypeByValue(String value) {
        for (SurvivorShipAlgorithmEnum element : SurvivorShipAlgorithmEnum.values()) {
            if (element.getValue().equalsIgnoreCase(value)) {
                return element;
            }
        }

        return null;
    }

    public static SurvivorShipAlgorithmEnum getTypeBySavedValue(String value) {
        for (SurvivorShipAlgorithmEnum element : SurvivorShipAlgorithmEnum.values()) {
            if (element.getComponentValueName().equalsIgnoreCase(value)) {
                return element;
            }
        }

        return null;
    }

    /**
     * 
     * 
     * @param index
     * @return null can not find this index
     */
    public static SurvivorShipAlgorithmEnum getTypeByIndex(int index) {
        for (SurvivorShipAlgorithmEnum element : SurvivorShipAlgorithmEnum.values()) {
            if (element.getIndex() == index) {
                return element;
            }
        }

        return null;
    }

    /**
     * Getter for dataType.
     * 
     * @return the dataType
     */
    public DefaultSurvivorShipDataTypeEnum[] getDataType() {
        return this.dataType;
    }

}
