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
package org.talend.dataquality.datamasking.semantic;

import org.apache.log4j.Logger;
import org.talend.dataquality.datamasking.FunctionType;

/**
 * Enumeration of all maskable semantic categories.
 */
public enum MaskableCategoryEnum {

    ADDRESS_LINE("Address Line", FunctionType.MASK_ADDRESS),
    CITY("City", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),
    COMPANY("Company", FunctionType.GENERATE_FROM_FILE_STRING_PROVIDED, "company.txt"), //$NON-NLS-2$
    EMAIL("Email", FunctionType.MASK_EMAIL),
    FIRST_NAME("First Name", FunctionType.GENERATE_FROM_FILE_STRING_PROVIDED, "firstName.txt"), //$NON-NLS-1$//$NON-NLS-2$
    LAST_NAME("Last Name", FunctionType.GENERATE_FROM_FILE_STRING_PROVIDED, "lastName.txt"), //$NON-NLS-1$//$NON-NLS-2$
    FR_COMMUNE("FR Commune", FunctionType.GENERATE_FROM_FILE_STRING_PROVIDED, "commune.txt"), //$NON-NLS-2$
    FULL_NAME("Full Name", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),
    IPv4_ADDRESS("IPv4 Address", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),
    IPv6_ADDRESS("IPv6 Address", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),
    JOB_TITLE("Job Title", FunctionType.GENERATE_FROM_FILE_STRING_PROVIDED, "jobTitle.txt"), //$NON-NLS-2$
    GEO_COORDINATE("Geographic coordinate", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),
    GEO_COORDINATES_DEG("Geographic coordinates (degrees)", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),
    MAC_ADDRESS("MAC Address", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),
    ORGANIZATION("Organization", FunctionType.GENERATE_FROM_FILE_STRING_PROVIDED, "organization.txt"), //$NON-NLS-2$
    PASSPORT("Passport", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),

    US_PHONE("US Phone", FunctionType.GENERATE_UNIQUE_PHONE_NUMBER_US),
    FR_PHONE("FR Phone", FunctionType.GENERATE_UNIQUE_PHONE_NUMBER_FRENCH),
    UK_PHONE("UK Phone", FunctionType.GENERATE_UNIQUE_PHONE_NUMBER_UK),
    DE_PHONE("DE Phone", FunctionType.GENERATE_UNIQUE_PHONE_NUMBER_GERMANY),

    US_POSTAL_CODE("US Postal Code", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),
    FR_POSTAL_CODE("FR Postal Code", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),
    DE_POSTAL_CODE("DE Postal Code", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),
    UK_POSTAL_CODE("UK Postal Code", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),
    BE_POSTAL_CODE("BE Postal Code", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),
    FR_CODE_COMMUNE_INSEE("FR Insee Code", FunctionType.REPLACE_CHARACTERS_WITH_GENERATION),

    US_SSN("US Social Security Number", FunctionType.GENERATE_SSN_US),
    FR_SSN("FR Social Security Number", FunctionType.GENERATE_SSN_FRENCH),
    UK_SSN("UK Social Security Number", FunctionType.GENERATE_SSN_UK),

    MASTERCARD("Mastercard Credit Card", FunctionType.GENERATE_CREDIT_CARD_FORMAT_STRING),
    US_CREDIT_CARD("AmEx Credit Card", FunctionType.GENERATE_CREDIT_CARD_FORMAT_STRING),
    VISACARD("Visa Credit Card", FunctionType.GENERATE_CREDIT_CARD_FORMAT_STRING);

    private String displayName;

    private FunctionType functionType;

    private String parameter;

    private static final Logger LOG = Logger.getLogger(MaskableCategoryEnum.class);

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
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
