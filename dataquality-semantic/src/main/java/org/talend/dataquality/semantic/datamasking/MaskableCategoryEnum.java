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
package org.talend.dataquality.semantic.datamasking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.FunctionType;

/**
 * Enumeration of all maskable semantic categories.
 */
public enum MaskableCategoryEnum {

    ADDRESS_LINE("Address Line", FunctionType.MASK_ADDRESS), //$NON-NLS-1$
    EMAIL("Email", FunctionType.MASK_EMAIL), //$NON-NLS-1$
    FULL_NAME("Full Name", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION), //$NON-NLS-1$
    IPv4_ADDRESS("IPv4 Address", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION), //$NON-NLS-1$
    IPv6_ADDRESS("IPv6 Address", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION), //$NON-NLS-1$
    GEO_COORDINATE("Geographic coordinate", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION), //$NON-NLS-1$
    GEO_COORDINATES_DEG("Geographic coordinates (degrees)", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION), //$NON-NLS-1$
    MAC_ADDRESS("MAC Address", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION), //$NON-NLS-1$
    PASSPORT("Passport", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION), //$NON-NLS-1$

    US_PHONE("US Phone", FunctionType.GENERATE_UNIQUE_PHONE_NUMBER_US), //$NON-NLS-1$
    FR_PHONE("FR Phone", FunctionType.GENERATE_UNIQUE_PHONE_NUMBER_FRENCH), //$NON-NLS-1$
    UK_PHONE("UK Phone", FunctionType.GENERATE_UNIQUE_PHONE_NUMBER_UK), //$NON-NLS-1$
    DE_PHONE("DE Phone", FunctionType.GENERATE_UNIQUE_PHONE_NUMBER_GERMANY), //$NON-NLS-1$

    US_POSTAL_CODE("US Postal Code", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION), //$NON-NLS-1$
    FR_POSTAL_CODE("FR Postal Code", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION), //$NON-NLS-1$
    DE_POSTAL_CODE("DE Postal Code", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION), //$NON-NLS-1$
    UK_POSTAL_CODE("UK Postal Code", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION), //$NON-NLS-1$
    BE_POSTAL_CODE("BE Postal Code", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION), //$NON-NLS-1$
    FR_CODE_COMMUNE_INSEE("FR Insee Code", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION), //$NON-NLS-1$

    US_SSN("US Social Security Number", FunctionType.GENERATE_SSN_US), //$NON-NLS-1$
    FR_SSN("FR Social Security Number", FunctionType.GENERATE_SSN_FRENCH), //$NON-NLS-1$
    UK_SSN("UK Social Security Number", FunctionType.GENERATE_SSN_UK), //$NON-NLS-1$

    MASTERCARD("Mastercard Credit Card", FunctionType.GENERATE_CREDIT_CARD_FORMAT_STRING), //$NON-NLS-1$
    US_CREDIT_CARD("AmEx Credit Card", FunctionType.GENERATE_CREDIT_CARD_FORMAT_STRING), //$NON-NLS-1$
    VISACARD("Visa Credit Card", FunctionType.GENERATE_CREDIT_CARD_FORMAT_STRING); //$NON-NLS-1$

    private String displayName;

    private FunctionType functionType;

    private String parameter;

    private static final Logger LOGGER = LoggerFactory.getLogger(MaskableCategoryEnum.class);

    /**
     * SemanticCategoryEnum constructor.
     * 
     * @param displayName the category shown in Semantic Discovery wizard
     * @param description the description of the category
     */
    private MaskableCategoryEnum(String displayName, FunctionType functionType) {
        this.displayName = displayName;
        this.functionType = functionType;
    }

    /**
     * SemanticCategoryEnum constructor.
     * 
     * @param displayName the category shown in Semantic Discovery wizard
     * @param description the description of the category
     * @param parameter the parameter which used by current functionType
     */
    private MaskableCategoryEnum(String displayName, FunctionType functionType, String parameter) {
        this(displayName, functionType);
        this.parameter = parameter;

    }

    public String getId() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }

    public FunctionType getFunctionType() {
        return functionType;
    }

    /**
     * Getter for parameter.
     * 
     * @return the parameter
     */
    public String getParameter() {
        return parameter;
    }

    public static MaskableCategoryEnum getCategoryById(String catId) {
        try {
            return valueOf(catId);
        } catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage());
            return null;
        }
    }

}
