// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.talend.dataquality.semantic.AllSemanticTests;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;

public class ValueDataMaskerTest {

    private static final Map<String[], String> EXPECTED_MASKED_VALUES = new LinkedHashMap<String[], String>() {

        private static final long serialVersionUID = 1L;

        {
            // 0. UNKNOWN
            put(new String[] { " ", "UNKNOWN", "string" }, " ");
            put(new String[] { "91000", "UNKNOWN", "integer" }, "86622");
            put(new String[] { "92000", "UNKNOWN", "decimal" }, "87574");
            put(new String[] { "93000", "UNKNOWN", "numeric" }, "88526");
            put(new String[] { "2023-06-07", "UNKNOWN", "date" }, "2023-07-01");
            put(new String[] { "sdkjs@talend.com", "UNKNOWN", "string" }, "vkfzz@psbbqg.aqa");

            // 1. FIRST_NAME
            put(new String[] { "", SemanticCategoryEnum.FIRST_NAME.name(), "string" }, "");
            put(new String[] { "John", SemanticCategoryEnum.FIRST_NAME.name(), "string" }, "GABRIELA");

            // 2. LAST_NAME
            put(new String[] { "Dupont", SemanticCategoryEnum.LAST_NAME.name(), "string" }, "RANKIN");

            // 3. EMAIL
            put(new String[] { "sdkjs@talend.com", MaskableCategoryEnum.EMAIL.name(), "string" }, "XXXXX@talend.com");
            put(new String[] { "\t", MaskableCategoryEnum.EMAIL.name(), "string" }, "\t");

            // 4. PHONE
            put(new String[] { "3333456789", MaskableCategoryEnum.US_PHONE.name(), "string" }, "3333818829");
            // if we put two 1 at the fifth and sixth position, it's not a US valid number, so we replace all the digit
            put(new String[] { "3333116789", MaskableCategoryEnum.US_PHONE.name(), "string" }, "2873888808");
            put(new String[] { "321938", MaskableCategoryEnum.FR_PHONE.name(), "string" }, "459494");
            put(new String[] { "++044dso44aa", MaskableCategoryEnum.DE_PHONE.name(), "string" }, "++287dso38aa");
            put(new String[] { "666666666", MaskableCategoryEnum.UK_PHONE.name(), "string" }, "666371758");
            put(new String[] { "777777777abc", MaskableCategoryEnum.UK_PHONE.name(), "string" }, "775767051abc");
            put(new String[] { "(301) 231-9473 x 2364", MaskableCategoryEnum.US_PHONE.name(), "string" },
                    "(301) 231-9452 x 1404");
            put(new String[] { "(563) 557-7600 Ext. 2890", MaskableCategoryEnum.US_PHONE.name(), "string" },
                    "(563) 557-7618 Ext. 3290");

            // 5. JOB_TITLE
            put(new String[] { "CEO", SemanticCategoryEnum.JOB_TITLE.name(), "string" }, "Aviation Inspector");

            // 6. ADDRESS_LINE
            put(new String[] { "9 Rue Pagès", MaskableCategoryEnum.ADDRESS_LINE.name(), "string" }, "6 Rue XXXXX");

            // 7 POSTAL_CODE
            put(new String[] { "37218-1324", MaskableCategoryEnum.US_POSTAL_CODE.name(), "string" }, "32515-1655");
            put(new String[] { "92150", MaskableCategoryEnum.FR_POSTAL_CODE.name(), "string" }, "32515");
            put(new String[] { "63274", MaskableCategoryEnum.DE_POSTAL_CODE.name(), "string" }, "32515");
            put(new String[] { "AT1 3BW", MaskableCategoryEnum.UK_POSTAL_CODE.name(), "string" }, "VK5 1ZP");

            // 8 ORGANIZATION

            // 9 COMPANY

            // 10 CREDIT_CARD
            put(new String[] { "5300 1232 8732 8318", MaskableCategoryEnum.US_CREDIT_CARD.name(), "string" },
                    "5332 5151 6550 0021");
            put(new String[] { "5300123287328318", MaskableCategoryEnum.MASTERCARD.name(), "string" }, "5332515165500021");
            put(new String[] { "4300 1232 8732 8318", MaskableCategoryEnum.VISACARD.name(), "string" }, "4325 1516 5500 0249");

            // 11 SSN
            put(new String[] { "728931789", MaskableCategoryEnum.US_SSN.name(), "string" }, "528-73-8888");
            put(new String[] { "17612 38293 28232", MaskableCategoryEnum.FR_SSN.name(), "string" }, "2210622388880 15");
            put(new String[] { "634217823", MaskableCategoryEnum.UK_SSN.name(), "string" }, "RB 87 38 88 D");

            // Company
            put(new String[] { "Talend", SemanticCategoryEnum.COMPANY.name(), "string" }, "G. R. Thanga Maligai");
            // FR Commune
            put(new String[] { "Amancey", SemanticCategoryEnum.FR_COMMUNE.name(), "string" }, "Flexbourg");
            // Organization
            put(new String[] { "Kiva", SemanticCategoryEnum.ORGANIZATION.name(), "string" }, "International Council for Science");

            // EMPTY
            put(new String[] { " ", "UNKNOWN", "integer" }, " ");
            put(new String[] { " ", "UNKNOWN", "numeric" }, " ");
            put(new String[] { " ", "UNKNOWN", "decimal" }, " ");
            put(new String[] { " ", "UNKNOWN", "date" }, " ");

            // NUMERIC
            put(new String[] { "111", "UNKNOWN", "integer" }, "106");
            put(new String[] { "-222.2", "UNKNOWN", "integer" }, "-211.5");
            put(new String[] { "333", "UNKNOWN", "numeric" }, "317");
            put(new String[] { "444,44", "UNKNOWN", "numeric" }, "423.06");
            put(new String[] { "555", "UNKNOWN", "float" }, "528");
            put(new String[] { "666.666", "UNKNOWN", "float" }, "634.595");
            put(new String[] { "Abc123", "UNKNOWN", "float" }, "Zzp655"); // not numeric, mask by char replacement

            // BIG NUMERIC
            put(new String[] { "7777777777777777777777777777777777777", "UNKNOWN", "double" },
                    "7403611837072083888888888888888888888");
            put(new String[] { "7777777777777777777777777777777777777.7777", "UNKNOWN", "double" },
                    "7403611837072083888888888888888888888.8888");

            // ENGINEERING FORMAT
            put(new String[] { "8e28", "UNKNOWN", "double" }, "7.615143603845572E28");
            put(new String[] { "-9.999E29", "UNKNOWN", "double" }, "-9.517977611856484E29");
        }
    };

    /**
     * Test method for {@link org.talend.dataquality.datamasking.DataMasker#process(java.lang.Object, boolean)}.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test
    public void testProcess() throws InstantiationException, IllegalAccessException {

        for (String[] input : EXPECTED_MASKED_VALUES.keySet()) {
            String inputValue = input[0];
            String semanticCategory = input[1];
            String dataType = input[2];

            System.out.print("[" + semanticCategory + "]\n\t" + inputValue + " => ");
            final ValueDataMasker masker = new ValueDataMasker(semanticCategory, dataType);
            masker.getFunction().setRandom(new Random(AllSemanticTests.RANDOM_SEED));
            masker.getFunction().setKeepEmpty(true);
            String maskedValue = masker.maskValue(inputValue);
            System.out.println(maskedValue);
            assertEquals("Test faild on [" + inputValue + "]", EXPECTED_MASKED_VALUES.get(input), maskedValue);

        }

        // Assert.assertNotEquals(city, masker.process(city));
        // masker should generate a city name
        // Assert the masked value is in a list of city names

        // categories to mask
        // First names, last names, email, IP address (v4, v6), localization, GPS coordinates, phone
        // Job title , street, address, zipcode, organization, company, full name, credit card number, account number,
        //

        // for these categories, here are the default functions to use:
        // first name -> another first name (from a fixed list loaded from a data file in a resource folder)
        // last name -> another last name (from a fixed list)
        // email -> mask local part (MaskEmail function)
        // phone -> keep 3 first digits and replace last digits
        // Job title -> another job title (from a fixed list)
        // street -> use MaskAddress
        // zipCode -> replace All digits
        // organization -> another organization (from a fixed list)
        // company -> another company (from a fixed list)
        // credit card -> generate a new one
        // account number -> generate a new one
        //
        // Assertions: masked data must never be identical to original data (don't use random seed for the random
        // generator to check that)
        //

        // data types to mask
        // date, string, numeric

        // create a ValueDataMasker for data that have no semantic category
        // use ValueDataMasker masker = SemanticCategoryMaskerFactory.createMasker(dataType);

        // here are the default functions to use for the different types:
        // date -> DateVariance with parameter 61 (meaning two months)
        // string -> use ReplaceAll
        // numeric -> use NumericVariance

    }

}
