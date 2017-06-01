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

import java.util.Map;

import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;

/**
 * Enumeration of all supported semantic categories.
 * <p/>
 * In most cases, the keys of the categories should not be changed.
 */
public enum SemanticCategoryEnum {
    /**
     * the categories defined in Data Dictionary index
     */
    ANIMAL("5836fb6f42b02a69874f3a95", "Animal", "Animal (multilingual)", CategoryType.DICT, false),
    ANSWER("5836fb7042b02a69874f3aff", "Answer", "Yes/No (in EN, FR, DE and ES)", CategoryType.DICT, false),
    AIRPORT("5836fb7042b02a69874f3b0b", "Airport", "Airport name", CategoryType.DICT, false),
    AIRPORT_CODE("5836fb7642b02a69874f77e3", "Airport Code", "Airport code", CategoryType.DICT, true),
    CIVILITY("5836fbb042b02a698752e66b", "Civility", "Civility (multilingual)", CategoryType.DICT, true),
    CONTINENT("5836fbb042b02a698752e679", "Continent", "Continent name (multilingual)", CategoryType.DICT, true),
    CONTINENT_CODE("5836fbb042b02a698752e68f", "Continent Code", "Continent code", CategoryType.DICT, true),
    COUNTRY("5836fbb042b02a698752e6a1", "Country", "Country Name (EN+FR)", CategoryType.DICT, true),
    COUNTRY_CODE_ISO2(
            "5836fbb042b02a698752e895",
            "Country Code ISO2",
            "Country code of ISO3166-1 Alpha-2",
            CategoryType.DICT,
            true),
    COUNTRY_CODE_ISO3(
            "5836fbb042b02a698752ea89",
            "Country Code ISO3",
            "Country code of ISO3166-1 Alpha-3",
            CategoryType.DICT,
            true),
    CURRENCY_NAME("5836fbb142b02a698752ec7d", "Currency Name", "Currency name (EN)", CategoryType.DICT, true),
    CURRENCY_CODE("5836fbb142b02a698752edad", "Currency Code", "Currency alphabetic code", CategoryType.DICT, true),
    HR_DEPARTMENT("5836fbb142b02a698752eed9", "HR Department", "Department or service name in company", CategoryType.DICT, false),
    FIRST_NAME("5836fbb142b02a698752eedd", "First Name", "First name", CategoryType.DICT, false),
    LAST_NAME("5836fbb942b02a6987534b81", "Last Name", "Last name", CategoryType.DICT, false),
    CITY("5836fb7c42b02a69874fb511", "City", "City name (multilingual)", CategoryType.DICT, false),
    GENDER("5836fbc142b02a698753a93f", "Gender", "Gender (Multilingual)", CategoryType.DICT, true),
    JOB_TITLE("5836fbc142b02a698753a951", "Job Title", "Job Title (EN)", CategoryType.DICT, false),
    MONTH("5836fbc242b02a698753b2a3", "Month", "Month (Multilingual)", CategoryType.DICT, true),
    STREET_TYPE("5836fbc242b02a698753b2bd", "Street Type", "Street type (multilingual)", CategoryType.DICT, true),
    WEEKDAY("5836fbc242b02a698753b2e3", "Weekday", "Weekday (multilingual)", CategoryType.DICT, true),
    MUSEUM("5836fbc242b02a698753b2f3", "Museum", "Museum Names", CategoryType.DICT, false),
    US_COUNTY("5836fbc542b02a698753d671", "US County", "US County Names", CategoryType.DICT, true),
    ORGANIZATION("5836fbc642b02a698753e4c7", "Organization", "Organization Names", CategoryType.DICT, false),
    COMPANY("5836fbcc42b02a69875431b3", "Company", "Company Names", CategoryType.DICT, false),
    BEVERAGE("5836fbeb42b02a698755c54d", "Beverage", "Beverage Names", CategoryType.DICT, false),
    MEASURE_UNIT("5836fbec42b02a698755caef", "Measure Unit", "Units of Measurement", CategoryType.DICT, false),
    INDUSTRY("5836fbec42b02a698755cbb5", "Industry", "Industry Names", CategoryType.DICT, false),
    INDUSTRY_GROUP("5836fbec42b02a698755cbb9", "Industry Group", "Industry Group Names", CategoryType.DICT, false),
    SECTOR("5836fbec42b02a698755cbbd", "Sector", "Economic Sector Names", CategoryType.DICT, false),
    FR_COMMUNE("5836fbec42b02a698755cbc1", "FR Commune", "French Commune names", CategoryType.DICT, true),
    FR_DEPARTEMENT("5836fc0042b02a698756d667", "FR Departement", "French Departement names", CategoryType.DICT, true),
    FR_REGION("5836fc0042b02a698756d733", "FR Region", "French Region names", CategoryType.DICT, true),
    FR_REGION_LEGACY("5836fc0042b02a698756d759", "FR Region Legacy", "Legacy French Region names", CategoryType.DICT, true),
    LANGUAGE("5836fc0042b02a698756d791", "Language", "Language Name (EN+FR+DE+NATIVE)", CategoryType.DICT, true),
    LANGUAGE_CODE_ISO2(
            "5836fc0042b02a698756d903",
            "Language Code ISO2",
            "Language Code of ISO639-1 Alpha-2",
            CategoryType.DICT,
            true),
    LANGUAGE_CODE_ISO3(
            "5836fc0042b02a698756da75",
            "Language Code ISO3",
            "Language Code of ISO639-2 Alpha-3 (B/T)",
            CategoryType.DICT,
            true),
    CA_PROVINCE_TERRITORY(
            "5836fc0042b02a698756dbe7",
            "CA Province Territory",
            "Provinces and Territories of Canada",
            CategoryType.DICT,
            true),
    CA_PROVINCE_TERRITORY_CODE(
            "5836fc0042b02a698756dc03",
            "CA Province Territory Code",
            "Canada Provinces and Territories Code",
            CategoryType.DICT,
            true),
    MX_ESTADO(
            "5836fc0042b02a698756dc1f",
            "MX Estado",
            "Federated States and Federal District of Mexico",
            CategoryType.DICT,
            true),
    MX_ESTADO_CODE("5836fc0042b02a698756dc61", "MX Estado Code", "Mexico States Code", CategoryType.DICT, true),

    /**
     * the categories defined in Keyword index
     */
    ADDRESS_LINE("58eb5a0deee16441b0d2d76c", "Address Line", "Address line which contains STREET_TYPE keyword", CategoryType.KEYWORD, false),
    FULL_NAME("58eb5a0feee16441b0d2d792", "Full Name", "Full name which contains CIVILITY keyword", CategoryType.KEYWORD, false),

    /**
     * the categories defined in categorizer.json
     */
    AT_VAT_NUMBER("583edc44ec06957a34fa6482", "AT VAT Number", "Austria VAT number", CategoryType.REGEX, true),
    BANK_ROUTING_TRANSIT_NUMBER(
            "583edc44ec06957a34fa6450",
            "Bank Routing Transit Number",
            "Bank routing transit number",
            CategoryType.REGEX,
            true),
    BE_POSTAL_CODE("583edc44ec06957a34fa6480", "BE Postal Code", "Belgium postal code", CategoryType.REGEX, true),
    BG_VAT_NUMBER("583edc44ec06957a34fa6432", "BG VAT Number", "Bulgaria VAT number", CategoryType.REGEX, true),
    COLOR_HEX_CODE("583edc44ec06957a34fa6476", "Color Hex Code", "Color hexadecimal code", CategoryType.REGEX, true),
    EMAIL("583edc44ec06957a34fa6430", "Email", "Email address", CategoryType.REGEX, true),
    EN_MONEY_AMOUNT(
            "583edc44ec06957a34fa644e",
            "Money Amount (EN)",
            "Amount of money in English format",
            CategoryType.REGEX,
            true),
    EN_MONTH("583edc44ec06957a34fa6456", "EN Month", "Month in English", CategoryType.DICT, true),
    EN_MONTH_ABBREV("583edc44ec06957a34fa646a", "EN Month Abbrev", "Month English abbreviation", CategoryType.DICT, true),
    EN_WEEKDAY("583edc44ec06957a34fa643a", "EN Weekday", "Weekday or their abbreviation", CategoryType.DICT, true),
    FR_MONEY_AMOUNT(
            "583edc44ec06957a34fa6468",
            "Money Amount (FR)",
            "Amount of money in French format",
            CategoryType.REGEX,
            true),
    FR_PHONE("583edc44ec06957a34fa646c", "FR Phone", "French phone number", CategoryType.REGEX, true),
    FR_POSTAL_CODE("583edc44ec06957a34fa643c", "FR Postal Code", "French postal code", CategoryType.REGEX, true),
    FR_CODE_COMMUNE_INSEE(
            "583edc44ec06957a34fa645e",
            "FR Insee Code",
            "French Insee code of cities with Corsica and colonies",
            CategoryType.REGEX,
            true),
    FR_SSN("583edc44ec06957a34fa6444", "FR Social Security Number", "French social security number", CategoryType.REGEX, true),
    FR_VAT_NUMBER("583edc44ec06957a34fa6478", "FR VAT Number", "French VAT number", CategoryType.REGEX, true),
    US_PHONE("583edc44ec06957a34fa645c", "US Phone", "American phone number", CategoryType.REGEX, true),
    US_POSTAL_CODE("583edc44ec06957a34fa6488", "US Postal Code", "US postal code", CategoryType.REGEX, true),
    US_SSN("583edc44ec06957a34fa642e", "US Social Security Number", "US social security number", CategoryType.REGEX, true),
    US_STATE("583edc44ec06957a34fa6470", "US State", "US states", CategoryType.DICT, true),
    US_STATE_CODE("583edc44ec06957a34fa6474", "US State Code", "US state code", CategoryType.DICT, true),
    DE_PHONE("583edc44ec06957a34fa643e", "DE Phone", "German phone number", CategoryType.REGEX, true),
    DE_POSTAL_CODE("583edc44ec06957a34fa647c", "DE Postal Code", "German postal code", CategoryType.REGEX, true),
    UK_PHONE("583edc44ec06957a34fa647e", "UK Phone", "UK phone number", CategoryType.REGEX, true),
    UK_POSTAL_CODE("583edc44ec06957a34fa6454", "UK Postal Code", "UK postal code", CategoryType.REGEX, true),
    UK_SSN(
            "583edc44ec06957a34fa644c",
            "UK Social Security Number",
            "National Insurance number, generally called an NI Number (NINO)",
            CategoryType.REGEX,
            true),
    GEO_COORDINATES(
            "583edc44ec06957a34fa646e",
            "Geographic Coordinates",
            "Google Maps style GPS Decimal format",
            CategoryType.REGEX,
            true),
    IPv4_ADDRESS("583edc44ec06957a34fa6466", "IPv4 Address", "IPv4 address", CategoryType.REGEX, true),
    IPv6_ADDRESS("583edc44ec06957a34fa6458", "IPv6 Address", "IPv6 address", CategoryType.REGEX, true),
    ISBN_10(
            "583edc44ec06957a34fa6446",
            "ISBN-10",
            "International Standard Book Number 10 digits. Such as ISBN 2-711-79141-6",
            CategoryType.REGEX,
            true),
    ISBN_13("583edc44ec06957a34fa644a", "ISBN-13", "International Standard Book Number 13 digits.", CategoryType.REGEX, true),
    GEO_COORDINATE(
            "583edc44ec06957a34fa6436",
            "Geographic coordinate",
            "Longitude or latitude coordinates with at least meter precision",
            CategoryType.REGEX,
            true),
    GEO_COORDINATES_DEG(
            "583edc44ec06957a34fa6462",
            "Geographic Coordinates (degree)",
            "Latitude and longitude coordinates separated by a comma in the form: N 0:59:59.99,E 0:59:59.99",
            CategoryType.REGEX,
            true),
    MAC_ADDRESS("583edc44ec06957a34fa6438", "MAC Address", "MAC Address.", CategoryType.REGEX, true),
    AMEX_CARD("583edc44ec06957a34fa6452", "Amex Card", "American Express card", CategoryType.REGEX, true),
    MASTERCARD("583edc44ec06957a34fa6442", "MasterCard", "MasterCard", CategoryType.REGEX, true),
    VISA_CARD("583edc44ec06957a34fa6440", "Visa Card", "Visa card", CategoryType.REGEX, true),
    PASSPORT("583edc44ec06957a34fa6486", "Passport", "Passport number", CategoryType.REGEX, true),
    SEDOL("583edc44ec06957a34fa6484", "SEDOL", "Stock Exchange Daily Official List", CategoryType.REGEX, true),
    SE_SSN(
            "583edc44ec06957a34fa6448",
            "SE Social Security Number",
            "The personal identity number (Swedish: personnummer) is the Swedish national identification number.",
            CategoryType.REGEX,
            true),
    URL("583edc44ec06957a34fa6434", "URL", "Website URL", CategoryType.REGEX, true),
    WEB_DOMAIN("583edc44ec06957a34fa642c", "Web Domain", "Website domain", CategoryType.REGEX, true),
    HDFS_URL("583edc44ec06957a34fa647a", "HDFS URL", "HDFS URL", CategoryType.REGEX, true),
    FILE_URL("583edc44ec06957a34fa6472", "File URL", "FILE URL", CategoryType.REGEX, true),
    MAILTO_URL("583edc44ec06957a34fa645a", "MailTo URL", "MAIL TO URL", CategoryType.REGEX, true),
    DATA_URL("583edc44ec06957a34fa6464", "Data URL", "DATA URL", CategoryType.REGEX, true),
    IBAN("583edc44ec06957a34fa6460", "IBAN", "IBAN", CategoryType.REGEX, true),

    /**
     * Compound Types
     */
    PHONE("58f9d2e8b45fc36367e8bc38", "Phone number", "Phone number (DE, FR, UK, US)", CategoryType.COMPOUND, true),
    NA_STATE(
            "5911906440161c655fd8b681",
            "North American state",
            "North American state groups together US and Canadian states",
            CategoryType.COMPOUND,
            true),
    NA_STATE_CODE(
            "591191f640161c655fd8b682",
            "North American state code",
            "North American state code groups together US and Canadian state codes",
            CategoryType.COMPOUND,
            true),

    /**
     * the categories with specific implementations
     */
    UNKNOWN("TALEND_INTERNAL_UNKNOWN", "", "Blank, Null and those who have no other semantic category", CategoryType.OTHER, false),
    DATE("TALEND_INTERNAL_DATE", "Date", "Date", CategoryType.OTHER, false);

    private String technicalId;

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
    private SemanticCategoryEnum(String technicalId, String displayName, String description, CategoryType categoryType,
            boolean completeness) {
        this.technicalId = technicalId;
        this.displayName = displayName;
        this.description = description;
        this.categoryType = categoryType;
        this.completeness = completeness;
    }

    public String getTechnicalId() {
        return technicalId;
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
     * Get a category by its functional ID.
     * Note: this method is called mainly for category sorting purpose, and it will return null for user categories.
     */
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

    /**
     * Run the program to check it the content of this enumeration is identical to the metadata in lucene index.
     */
    public static void main(String[] args) {
        Map<String, DQCategory> idMap = CategoryRegistryManager.getInstance().getCategoryMetadataMap();

        int count = 0;
        for (SemanticCategoryEnum catEnum : SemanticCategoryEnum.values()) {
            DQCategory meta = idMap.get(catEnum.getTechnicalId());
            if (meta != null) {
                String enumString = catEnum.name() + "(\"" + meta.getId() + "\", \"" + catEnum.getDisplayName() + "\", \""
                        + catEnum.getDescription() + "\", CategoryType." + catEnum.getCategoryType().name() + ", "
                        + catEnum.getCompleteness() + "),";

                String dqCatString = meta.getName() + "(\"" + meta.getId() + "\", \"" + meta.getLabel() + "\", \""
                        + meta.getDescription() + "\", CategoryType." + meta.getType().name() + ", " + meta.getCompleteness()
                        + "),";

                if (!enumString.equals(dqCatString)) {
                    System.err.println(">>> The enumeration item {" + catEnum.name()
                            + "} differs from actual metadata. Please update one of them.");
                }
                System.out.println("Enum: " + enumString);
                System.out.println("Meta: " + dqCatString + "\n");

                count++;
            }
        }

        for (DQCategory meta : idMap.values()) {
            if (SemanticCategoryEnum.getCategoryById(meta.getName()) == null) {
                System.err.println(">>> Could not find category {" + meta.getName() + "} in current enumeration. Please add it.");

                String dqCatString = meta.getName() + "(\"" + meta.getId() + "\", \"" + meta.getLabel() + "\", \""
                        + meta.getDescription() + "\", CategoryType." + meta.getType().name() + ", " + meta.getCompleteness()
                        + "),";
                System.err.println(dqCatString + "\n");
            }
        }
    }

}
