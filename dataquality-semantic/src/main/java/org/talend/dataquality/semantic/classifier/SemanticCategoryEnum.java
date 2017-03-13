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
package org.talend.dataquality.semantic.classifier;

import org.talend.dataquality.semantic.model.CategoryType;

/**
 * Enumeration of all supported semantic categories.
 * <p/>
 * In most cases, the keys of the categories should not be changed.
 */
public enum SemanticCategoryEnum {

    /**
     * the categories defined in Data Dictionary index
     */
    ANIMAL("Animal", "Animal (multilingual)", CategoryType.DICT, false),
    ANSWER("Answer", "Yes/No (in EN, FR, DE and ES)", CategoryType.DICT, false),
    AIRPORT("Airport", "Airport name", CategoryType.DICT, false),
    AIRPORT_CODE("Airport Code", "Airport name", CategoryType.DICT, true),
    CIVILITY("Civility", "Civility (multilingual)", CategoryType.DICT, true),
    CONTINENT("Continent", "Continent name (multilingual)", CategoryType.DICT, true),
    CONTINENT_CODE("Continent Code", "Continent code", CategoryType.DICT, true),
    COUNTRY("Country", "Country Name (EN+FR)", CategoryType.DICT, true),
    COUNTRY_CODE_ISO2("Country Code ISO2", "Country code of ISO3166-1 Alpha-2", CategoryType.DICT, true),
    COUNTRY_CODE_ISO3("Country Code ISO3", "Country code of ISO3166-1 Alpha-3", CategoryType.DICT, true),
    CURRENCY_NAME("Currency Name", "Currency name (EN)", CategoryType.DICT, true),
    CURRENCY_CODE("Currency Code", "Currency alphabetic code", CategoryType.DICT, true),
    HR_DEPARTMENT("HR Department", "Department or service name in company", CategoryType.DICT, false),

    FIRST_NAME("First Name", "First name", CategoryType.DICT, false),
    LAST_NAME("Last Name", "Last name", CategoryType.DICT, false),
    CITY("City", "City name (multilingual)", CategoryType.DICT, false),

    GENDER("Gender", "Gender (Multilingual)", CategoryType.DICT, true),
    JOB_TITLE("Job Title", "Job Title (EN)", CategoryType.DICT, false),
    MONTH("Month", "Month (Multilingual)", CategoryType.DICT, true),
    STREET_TYPE("Street Type", "Street type (multilingual)", CategoryType.DICT, true),
    WEEKDAY("Weekday", "Weekday (multilingual)", CategoryType.DICT, true),

    MUSEUM("Museum", "Museum Names", CategoryType.DICT, false),
    US_COUNTY("US County", "US County Names", CategoryType.DICT, true),
    ORGANIZATION("Organization", "Organization Names", CategoryType.DICT, false),
    COMPANY("Company", "Company Names", CategoryType.DICT, false),
    BEVERAGE("Beverage", "Beverage Names", CategoryType.DICT, false),
    MEASURE_UNIT("Measure Unit", "Units of Measurement", CategoryType.DICT, false),
    INDUSTRY("Industry", "Industry Names", CategoryType.DICT, false),
    INDUSTRY_GROUP("Industry Group", "Industry Group Names", CategoryType.DICT, false),
    SECTOR("Sector", "Economic Sector Names", CategoryType.DICT, false),

    FR_COMMUNE("FR Commune", "French Commune names", CategoryType.DICT, true),
    FR_DEPARTEMENT("FR Departement", "French Departement names", CategoryType.DICT, true),
    FR_REGION("FR Region", "French Region names", CategoryType.DICT, true),
    FR_REGION_LEGACY("FR Region Legacy", "Legacy French Region names", CategoryType.DICT, true),
    LANGUAGE("Language", "Language Name (EN+FR+DE+NATIVE)", CategoryType.DICT, true),
    LANGUAGE_CODE_ISO2("Language Code ISO2", "Language Code of ISO639-1 Alpha-2", CategoryType.DICT, true),
    LANGUAGE_CODE_ISO3("Language Code ISO3", "Language Code of ISO639-2 Alpha-3 (B/T)", CategoryType.DICT, true),
    CA_PROVINCE_TERRITORY("CA Province Territory", "Provinces and Territories of Canada", CategoryType.DICT, true),
    CA_PROVINCE_TERRITORY_CODE("CA Province Territory Code", "Canada Provinces and Territories Code", CategoryType.DICT, true),
    MX_ESTADO("MX Estado", "Federated States and Federal District of Mexico", CategoryType.DICT, true),
    MX_ESTADO_CODE("MX Estado Code", "Mexico States Code", CategoryType.DICT, true),

    /**
     * the categories defined in Keyword index
     */
    ADDRESS_LINE("Address Line", "Address line which contains STREET_TYPE keyword", CategoryType.DICT, false),
    FULL_NAME("Full Name", "Full name which contains CIVILITY keyword", CategoryType.DICT, false),

    /**
     * the categories defined in categorizer.json
     */
    AT_VAT_NUMBER("AT VAT Number", "Austria VAT number", CategoryType.REGEX, true),
    BANK_ROUTING_TRANSIT_NUMBER("Bank Routing Transit Number", "Bank routing transit Number", CategoryType.REGEX, true),
    BE_POSTAL_CODE("BE Postal Code", "Belgium postal code", CategoryType.REGEX, true),
    BG_VAT_NUMBER("BG VAT Number", "Bulgaria VAT number", CategoryType.REGEX, true),
    COLOR_HEX_CODE("Color Hex Code", "Color hexadecimal code", CategoryType.REGEX, true),
    EMAIL("Email", "email", CategoryType.REGEX, true),

    // EN
    EN_MONEY_AMOUNT("Money Amount (EN)", "Amount of money in English format", CategoryType.REGEX, true),
    EN_MONTH("EN Month", "Month in English", CategoryType.REGEX, true),
    EN_MONTH_ABBREV("EN Month Abbrev", "Month English abbreviation", CategoryType.REGEX, true),
    EN_WEEKDAY("EN Weekday", "Weekday or their abbreviation", CategoryType.REGEX, true),

    // FR
    FR_MONEY_AMOUNT("Money Amount (FR)", "Amount of money in French format", CategoryType.REGEX, true),
    FR_PHONE("FR Phone", "French Phone Number", CategoryType.REGEX, true),
    FR_POSTAL_CODE("FR Postal Code", "French postal code", CategoryType.REGEX, true),
    FR_CODE_COMMUNE_INSEE("FR Insee Code", "French Insee code of cities with Corsica and colonies", CategoryType.REGEX, true),
    FR_SSN("FR Social Security Number", "French Social Security Number", CategoryType.REGEX, true),
    FR_VAT_NUMBER("FR VAT Number", "French VAT number", CategoryType.REGEX, true),

    // US
    US_PHONE("US Phone", "US Phone Number", CategoryType.REGEX, true),
    US_POSTAL_CODE("US Postal Code", "US postal code", CategoryType.REGEX, true),
    US_SSN("US Social Security Number", "US Social Security number", CategoryType.REGEX, true),
    US_STATE("US State", "US states", CategoryType.REGEX, true),
    US_STATE_CODE("US State Code", "US State code", CategoryType.REGEX, true),

    // DE
    DE_PHONE("DE Phone", "German phone number", CategoryType.REGEX, true),
    DE_POSTAL_CODE("DE Postal Code", "German postal code", CategoryType.REGEX, true),

    // UK
    UK_PHONE("UK Phone", "UK phone number", CategoryType.REGEX, true),
    UK_POSTAL_CODE("UK Postal Code", "UK postal code", CategoryType.REGEX, true),
    UK_SSN(
            "UK Social Security Number",
            "national identification number, national identity number, or national insurance number generally called an NI Number (NINO)",
            CategoryType.REGEX,
            true),

    GEO_COORDINATES("Geographic Coordinates", "Google Maps style GPS Decimal format", CategoryType.REGEX, true),
    IPv4_ADDRESS("IPv4 Address", "IPv4 address", CategoryType.REGEX, true),
    IPv6_ADDRESS("IPv6 Address", "IPv6 address", CategoryType.REGEX, true),
    ISBN_10("ISBN-10", "International Standard Book Number 10 digits. Such as ISBN 2-711-79141-6", CategoryType.REGEX, true),
    ISBN_13("ISBN-13", "International Standard Book Number 13 digits.", CategoryType.REGEX, true),
    GEO_COORDINATE(
            "Geographic coordinate",
            "Longitude or latitude coordinates with at least meter precision",
            CategoryType.REGEX,
            true),
    GEO_COORDINATES_DEG(
            "Geographic coordinates (degrees)",
            "Latitude and longitude coordinates separated by a comma in the form: N 0:59:59.99,E 0:59:59.99",
            CategoryType.REGEX,
            true),
    MAC_ADDRESS("MAC Address", "MAC Address.", CategoryType.REGEX, true),

    AMEX_CARD("Amex Card", "American Express card", CategoryType.REGEX, true),
    MASTERCARD("MasterCard", "MasterCard", CategoryType.REGEX, true),
    VISA_CARD("Visa Card", "Visa card", CategoryType.REGEX, true),

    PASSPORT("Passport", "Passport number", CategoryType.REGEX, true),
    SEDOL("SEDOL", "Stock Exchange Daily Official List", CategoryType.REGEX, true),
    SE_SSN("SE Social Security Number", "Swedish person number", CategoryType.REGEX, true),
    URL("Web URL", "Web site URL", CategoryType.REGEX, true),
    WEB_DOMAIN("Web Domain", "Web site domain", CategoryType.REGEX, true),

    HDFS_URL("HDFS URL", "HDFS URL", CategoryType.REGEX, true),
    FILE_URL("File URL", "File URL", CategoryType.REGEX, true),
    MAILTO_URL("MailTo URL", "MailTo URL", CategoryType.REGEX, true),
    DATA_URL("Data URL", "Data URL", CategoryType.REGEX, true),
    IBAN("IBAN", "IBAN", CategoryType.REGEX, true),

    UNKNOWN("", "Blank, Null and those who have no other semantic category", CategoryType.OTHER, false),

    /**
     * the categories with specific implementations
     */
    DATE("Date", "Date", CategoryType.OTHER, false);

    private String displayName;

    private String description;

    private CategoryType categoryType;

    private boolean completeness;

    /**
     * SemanticCategoryEnum constructor.
     * 
     * @param displayName the category shown in Semantic Discovery wizard
     * @param description the description of the category
     */
    private SemanticCategoryEnum(String displayName, String description, CategoryType categoryType, boolean completeness) {
        this.displayName = displayName;
        this.description = description;
        this.categoryType = categoryType;
        this.completeness = completeness;
    }

    public String getId() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public boolean getCompleteness() {
        return completeness;
    }

    /**
     * @deprecated use {@link CategoryRegistryManager.getInstance(contextName).getCategoryLabel()} instead
     */
    @Deprecated
    public static SemanticCategoryEnum getCategoryById(String catId) {
        if ("".equals(catId)) {
            return UNKNOWN;
        }
        try {
            return valueOf(catId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
