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
import static org.talend.dataquality.semantic.TestUtils.mockWithTenant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.talend.dataquality.semantic.AllSemanticTests;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.CustomDictionaryHolder;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;

@PrepareForTest({ CustomDictionaryHolder.class, CategoryRegistryManager.class })
public class ValueDataMaskerTest extends CategoryRegistryManagerAbstract {

    private static final Map<String[], String> EXPECTED_MASKED_VALUES = new LinkedHashMap<String[], String>() {

        private static final long serialVersionUID = 1L;

        {
            // 0. UNKNOWN
            put(new String[] { " ", "UNKNOWN", "string" }, " ");
            put(new String[] { "91000", "UNKNOWN", "integer" }, "86622");
            put(new String[] { "92000", "UNKNOWN", "decimal" }, "87574");
            put(new String[] { "93000", "UNKNOWN", "numeric" }, "88526");
            put(new String[] { "2023-06-07", "UNKNOWN", "date" }, "2023-07-01");
            put(new String[] { "sdkjs@talend.com", "UNKNOWN", "string" }, "zyÿhx@juûâëï.míø");

            // 1. FIRST_NAME
            put(new String[] { "", SemanticCategoryEnum.FIRST_NAME.name(), "string" }, "");
            put(new String[] { "John", SemanticCategoryEnum.FIRST_NAME.name(), "string" }, "GABRIELA");// Rsgy
            put(new String[] { "PRUDENCE", SemanticCategoryEnum.FIRST_NAME.name(), "string", }, "GABRIELA");
            put(new String[] { "XUE", SemanticCategoryEnum.FIRST_NAME.name(), "string", }, "GABRIELA");

            // 2. LAST_NAME
            put(new String[] { "Dupont", SemanticCategoryEnum.LAST_NAME.name(), "string" }, "RANKIN");
            put(new String[] { "SMITH", SemanticCategoryEnum.LAST_NAME.name(), "string" }, "RANKIN");

            // 3. EMAIL
            put(new String[] { "sdkjs@talend.com", MaskableCategoryEnum.EMAIL.name(), "string" }, "XXXXX@talend.com");
            put(new String[] { "\t", MaskableCategoryEnum.EMAIL.name(), "string" }, "\t");

            // 4. PHONE
            put(new String[] { "630466000", MaskableCategoryEnum.US_PHONE.name(), "string" }, "251516550");
            put(new String[] { "3333456789", MaskableCategoryEnum.US_PHONE.name(), "string" }, "3333818829");
            // if we put two 1 at the fifth and sixth position, it's not a US valid number, so we replace all the digit
            put(new String[] { "3333116789", MaskableCategoryEnum.US_PHONE.name(), "string" }, "2515165500");
            put(new String[] { "321938", MaskableCategoryEnum.FR_PHONE.name(), "string" }, "251516");// regex invalid
            put(new String[] { "0212345678", MaskableCategoryEnum.FR_PHONE.name(), "string" }, "0212157114");
            put(new String[] { "++044dso44aa", MaskableCategoryEnum.DE_PHONE.name(), "string" }, "++251xju55ëï");
            put(new String[] { "666666666", MaskableCategoryEnum.UK_PHONE.name(), "string" }, "251516550");
            put(new String[] { "777777777abc", MaskableCategoryEnum.UK_PHONE.name(), "string" }, "251516550ïmí");
            put(new String[] { "(301) 231-9473 x 2364", MaskableCategoryEnum.US_PHONE.name(), "string" },
                    "(301) 231-9452 x 1404");
            put(new String[] { "(563) 557-7600 Ext. 2890", MaskableCategoryEnum.US_PHONE.name(), "string" },
                    "(251) 516-5500 Iíø. 7033");

            // 5. JOB_TITLE
            put(new String[] { "CEO", SemanticCategoryEnum.JOB_TITLE.name(), "string" }, "Aviation Inspector");

            // 6. ADDRESS_LINE
            put(new String[] { "9 Rue Pagès", MaskableCategoryEnum.ADDRESS_LINE.name(), "string" }, "6 Rue XXXXX");

            // 7 POSTAL_CODE
            // use regex function
            put(new String[] { "37218-1324", SemanticCategoryEnum.US_POSTAL_CODE.name(), "string" }, "82660");
            put(new String[] { "37218-1324", SemanticCategoryEnum.US_POSTAL_CODE.name(), "string", "1111" }, "74201-1254");
            // use regex function
            put(new String[] { "92150", SemanticCategoryEnum.FR_POSTAL_CODE.name(), "string" }, "82660");
            put(new String[] { "63274", SemanticCategoryEnum.DE_POSTAL_CODE.name(), "string" }, "32515");
            put(new String[] { "AT1 3BW", SemanticCategoryEnum.UK_POSTAL_CODE.name(), "string" }, "LÄ5 1ZÕ");

            // 8 ORGANIZATION

            // 9 COMPANY

            // 10 CREDIT_CARD
            put(new String[] { "5300 1232 8732 8318", MaskableCategoryEnum.US_CREDIT_CARD.name(), "string" },
                    "5332 5151 6550 0021");
            put(new String[] { "5300123287328318", MaskableCategoryEnum.MASTERCARD.name(), "string" }, "5332515165500021");
            put(new String[] { "4300 1232 8732 8318", MaskableCategoryEnum.VISACARD.name(), "string" }, "4325 1516 5500 0249");

            // 11 SSN
            put(new String[] { "728931789", MaskableCategoryEnum.US_SSN.name(), "string" }, "325151655");
            put(new String[] { "17612 38293 28232", MaskableCategoryEnum.FR_SSN.name(), "string" }, "2210671796720 95");
            put(new String[] { "634217823", MaskableCategoryEnum.UK_SSN.name(), "string" }, "325151655");

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
            put(new String[] { "Abc123", "UNKNOWN", "float" }, "Úxj655"); // not numeric, mask by char replacement

            // BIG NUMERIC
            put(new String[] { "7777777777777777777777777777777777777", "UNKNOWN", "double" },
                    "7403611837072083888888888888888888888");
            put(new String[] { "7777777777777777777777777777777777777.7777", "UNKNOWN", "double" },
                    "7403611837072083888888888888888888888.8888");

            // ENGINEERING FORMAT
            put(new String[] { "8e28", "UNKNOWN", "double" }, "7.615143603845572E28");
            put(new String[] { "-9.999E29", "UNKNOWN", "double" }, "-9.517977611856484E29");

            // REGEX
            // work well no need modify
            put(new String[] { "AT12345678", SemanticCategoryEnum.AT_VAT_NUMBER.name(), "string" }, "AT66014576");// valid
            put(new String[] { "BT12345678", SemanticCategoryEnum.AT_VAT_NUMBER.name(), "string" }, "ÇÚ51655000");// invalid

            // work well
            put(new String[] { "801234567", SemanticCategoryEnum.BANK_ROUTING_TRANSIT_NUMBER.name(), "string" }, "806601457");// valid
            put(new String[] { "201234567", SemanticCategoryEnum.BANK_ROUTING_TRANSIT_NUMBER.name(), "string" }, "515165500");// invalid

            // exist issue need to use GenerateFromRegex function
            put(new String[] { "F-1234", SemanticCategoryEnum.BE_POSTAL_CODE.name(), "string" }, "F-66014");// valid
            put(new String[] { "F-12345", SemanticCategoryEnum.BE_POSTAL_CODE.name(), "string" }, "F-66014");// valid
            put(new String[] { "B-1234", SemanticCategoryEnum.BE_POSTAL_CODE.name(), "string" }, "F-66014");// valid
            /**
             * invalid input mask to valid reulst
             */
            put(new String[] { "B-12345", SemanticCategoryEnum.BE_POSTAL_CODE.name(), "string" }, "Ç-15165");// invalid

            // work well
            put(new String[] { "BG0123456789", SemanticCategoryEnum.BG_VAT_NUMBER.name(), "string" }, "BG6601457612");// valid
            put(new String[] { "BG123456789", SemanticCategoryEnum.BG_VAT_NUMBER.name(), "string" }, "BG6601457612");// valid
            put(new String[] { "BB123456789", SemanticCategoryEnum.BG_VAT_NUMBER.name(), "string" }, "ÇÚ516550002");// invalid

            // work well
            put(new String[] { "#A1A", SemanticCategoryEnum.COLOR_HEX_CODE.name(), "string" }, "#eae0f4");// valid
            put(new String[] { "#A1A1A1", SemanticCategoryEnum.COLOR_HEX_CODE.name(), "string" }, "#eae0f4");// valid
            put(new String[] { "#A1G", SemanticCategoryEnum.COLOR_HEX_CODE.name(), "string" }, "#Ç1Z");// invalid

            /**
             * Special case valid mask to invalid after improve code maybe this case should be removed or modify
             */
            // valid to invalid
            put(new String[] { "CA$1,123.22", SemanticCategoryEnum.EN_MONEY_AMOUNT.name(), "string" }, "LÄ$5,151.65");
            put(new String[] { "¥1,123k", SemanticCategoryEnum.EN_MONEY_AMOUNT.name(), "string" }, "¥3,251x");// valid
            put(new String[] { "¥1,123.00k", SemanticCategoryEnum.EN_MONEY_AMOUNT.name(), "string" }, "¥3,251.51u");// invalid

            /**
             * Special case valid mask to invalid after improve code maybe this case should be removed or modify
             */
            put(new String[] { "123 k$ CA", SemanticCategoryEnum.FR_MONEY_AMOUNT.name(), "string" }, "325 h$ ZÕ");// valid
            put(new String[] { "123k$CA", SemanticCategoryEnum.FR_MONEY_AMOUNT.name(), "string" }, "325h$ZÕ");// invalid

            // work well
            // valid
            put(new String[] { "FR1A 123456789", SemanticCategoryEnum.FR_VAT_NUMBER.name(), "string" }, "OU 601457612");
            put(new String[] { "1A 123456789", SemanticCategoryEnum.FR_VAT_NUMBER.name(), "string" }, "OU 601457612");// valid
            // invalid
            put(new String[] { "1a 123456789", SemanticCategoryEnum.FR_VAT_NUMBER.name(), "string" }, "5h 516550002");

            // work well
            // valid
            put(new String[] { "F-2A001", SemanticCategoryEnum.FR_CODE_COMMUNE_INSEE.name(), "string" }, "FRA 11457");
            // valid
            put(new String[] { "FRA-2B001", SemanticCategoryEnum.FR_CODE_COMMUNE_INSEE.name(), "string" }, "FRA 11457");
            // invalid
            put(new String[] { "FA-2B001", SemanticCategoryEnum.FR_CODE_COMMUNE_INSEE.name(), "string" }, "ÇÚ-5Õ655");

            // work well
            // valid
            put(new String[] { "123.456 -123.456", SemanticCategoryEnum.GEO_COORDINATES.name(), "string" }, "82.6 ,45.");
            put(new String[] { "123. -123.", SemanticCategoryEnum.GEO_COORDINATES.name(), "string" }, "82.6 ,45.");// valid
            put(new String[] { "123 -123", SemanticCategoryEnum.GEO_COORDINATES.name(), "string" }, "515 -165");// invalid

            // valid
            put(new String[] { "192.168.33.211", SemanticCategoryEnum.IPv4_ADDRESS.name(), "string" }, "325.151.65.500");
            // valid
            put(new String[] { "000.168.33.211", SemanticCategoryEnum.IPv4_ADDRESS.name(), "string" }, "325.151.65.500");
            // invalid
            put(new String[] { "256.168.33.211", SemanticCategoryEnum.IPv4_ADDRESS.name(), "string" }, "325.151.65.500");

            // need to find out new pattern regex
            // valid
            put(new String[] { "192.168.255.33.211", SemanticCategoryEnum.IPv6_ADDRESS.name(), "string" }, "325.151.655.00.024");
            // valid
            put(new String[] { "000.168.255.255.33.211", SemanticCategoryEnum.IPv6_ADDRESS.name(), "string" },
                    "325.151.655.000.24.703");//
            // invalid
            put(new String[] { "256.168.33", SemanticCategoryEnum.IPv6_ADDRESS.name(), "string" }, "325.151.65");

            // need to remove "(?:" from pattern regex
            // valid
            put(new String[] { "ISBN 7-302-13910-5", SemanticCategoryEnum.ISBN_10.name(), "string" }, "LÄÇÚ 5-165-50002-4");
            // invalid
            put(new String[] { "ISBN 7-302-13910-51", SemanticCategoryEnum.ISBN_10.name(), "string" }, "LÄÇÚ 5-165-50002-47");

            // need to remove "(?:" from pattern regex
            put(new String[] { "ISBN 978-7-121-05498-3", SemanticCategoryEnum.ISBN_13.name(), "string" },
                    "LÄÇÚ 516-5-500-02470-3");// valid
            put(new String[] { "ISBN 978-7-121-05498-31", SemanticCategoryEnum.ISBN_13.name(), "string" },
                    "LÄÇÚ 516-5-500-02470-33");// invalid

            // all of mask result is invalid
            put(new String[] { "111.012345", SemanticCategoryEnum.GEO_COORDINATE.name(), "string" }, "92.60145");// valid
            put(new String[] { "-110.012345", SemanticCategoryEnum.GEO_COORDINATE.name(), "string" }, "92.60145");// valid
            put(new String[] { "-010.012345", SemanticCategoryEnum.GEO_COORDINATE.name(), "string" }, "-515.165500");// invalid

            // Whatever input data is valid or not result alaways is invalid
            put(new String[] { "N 1:12:12.1,W 1:12:12.1", SemanticCategoryEnum.GEO_COORDINATES_DEG.name(), "string" },
                    "L 2:51:51.6,N 5:00:02.4");// valid
            put(new String[] { "S 1:12:12.1,E 1:12:12.1", SemanticCategoryEnum.GEO_COORDINATES_DEG.name(), "string" },
                    "L 2:51:51.6,N 5:00:02.4");// valid
            put(new String[] { "S 1:62:12.1,E 1:62:12.1", SemanticCategoryEnum.GEO_COORDINATES_DEG.name(), "string" },
                    "L 2:51:51.6,N 5:00:02.4");// invalid

            // Whatever input data is valid or not result alaways is invalid
            put(new String[] { "0a:1b:2c:3d:4f:5A", SemanticCategoryEnum.MAC_ADDRESS.name(), "string" }, "Ee:e0:4B:6f:0F:A0");// valid
            put(new String[] { "0A:1B:2C:3D:4E:5a", SemanticCategoryEnum.MAC_ADDRESS.name(), "string" }, "Ee:e0:4B:6f:0F:A0");// valid
            put(new String[] { "0a:1b:2c:3d:4f:5g", SemanticCategoryEnum.MAC_ADDRESS.name(), "string" }, "5h:5j:6û:5ë:0m:2ø");// invalid

            // work well
            // valid
            put(new String[] { "370123456789012", SemanticCategoryEnum.AMEX_CARD.name(), "string" }, "376601457612031");
            // valid
            put(new String[] { "340123456789012", SemanticCategoryEnum.AMEX_CARD.name(), "string" }, "376601457612031");
            // invalid
            put(new String[] { "350123456789012", SemanticCategoryEnum.AMEX_CARD.name(), "string" }, "515165500024703");

            // work well
            // valid
            put(new String[] { "4012345678901234", SemanticCategoryEnum.VISA_CARD.name(), "string" }, "4266014576120312");
            // invalid
            put(new String[] { "5012345678901234", SemanticCategoryEnum.VISA_CARD.name(), "string" }, "5151655000247033");

            // exist issue need to use GenerateFromRegex function
            put(new String[] { "<012345670ABC0123456A0123456<012345678901<01", SemanticCategoryEnum.PASSPORT.name(), "string" },
                    "<UUK0N4<<6XGE3120436S11194394OW<65<3<D750<68");// valid
            put(new String[] { "<012345670ABC0123456A0123456<012345678901<<<", SemanticCategoryEnum.PASSPORT.name(), "string" },
                    "<515165500IOE7033168À8260805<005965293605<<<");// invalid

            // have a exception but i think the code can be improve as below:
            // ^(?<Sedol>[B-Db-dF-Hf-hJ-Nj-nP-Tp-tV-Xv-xYyZz\d]{6}\d)$==>^([B-Db-dF-Hf-hJ-Nj-nP-Tp-tV-Xv-xYyZz\d]{6}\d)$
            // exist SedolValidator to be second time check
            put(new String[] { "B108899", SemanticCategoryEnum.SEDOL.name(), "string" }, "L251516");// valid
            put(new String[] { "7108899", SemanticCategoryEnum.SEDOL.name(), "string" }, "3251516");// valid
            put(new String[] { "E108899", SemanticCategoryEnum.SEDOL.name(), "string" }, "L251516");// invalid

            // remove "|" from pattern regex
            put(new String[] { "10010000-0123", SemanticCategoryEnum.SE_SSN.name(), "string" }, "2|661125-6120");// valid
            put(new String[] { "29010000-0123", SemanticCategoryEnum.SE_SSN.name(), "string" }, "2|661125-6120");// valid
            put(new String[] { "30010000-0123", SemanticCategoryEnum.SE_SSN.name(), "string" }, "51516550-0024");// invalid

            put(new String[] { "https://www.baidu.com", SemanticCategoryEnum.URL.name(), "string" }, "zyÿhx://juû.âëïmí.øìï");// valid
            put(new String[] { "http://www.google.com.cn", SemanticCategoryEnum.URL.name(), "string" },
                    "zyÿh://xju.ûâëïmí.øìï.îæ");// valid
            put(new String[] { "httpss://www.baidu.com", SemanticCategoryEnum.URL.name(), "string" }, "zyÿhxj://uûâ.ëïmíø.ìïî");// invalid

            put(new String[] { "baidu.com", SemanticCategoryEnum.WEB_DOMAIN.name(), "string" },
                    "C---0.4---1ge--S0-dKs1d1-4394ow.6--LfD7v0-6.415M4n-1-8Y.b---B-7.w0E.e-5--p.4N.j5ioJ9-v.g.e.M-Y---3YZ8w5-c-icw.LpHjHBVAIq");//
            // valid
            put(new String[] { "baidu.cn", SemanticCategoryEnum.WEB_DOMAIN.name(), "string" },
                    "C---0.4---1ge--S0-dKs1d1-4394ow.6--LfD7v0-6.415M4n-1-8Y.b---B-7.w0E.e-5--p.4N.j5ioJ9-v.g.e.M-Y---3YZ8w5-c-icw.LpHjHBVAIq");//
            // valid
            put(new String[] { "baidu.c", SemanticCategoryEnum.WEB_DOMAIN.name(), "string" }, "ÿhxju.û");// invalid

            put(new String[] { "hdfs://localhost/user/hadoop/data/hello.txt", SemanticCategoryEnum.HDFS_URL.name(), "string" },
                    "zyÿh://xjuûâëïmí/øìïî/æýuþqë/goõi/ñäiøý.öcâ");// valid
            put(new String[] { "hdfs://localhost/$user/#hadoop/data/hello.txt", SemanticCategoryEnum.HDFS_URL.name(), "string" },
                    "zyÿh://xjuûâëïmí/$øìïî/#æýuþqë/goõi/ñäiøý.öcâ");// valid
            put(new String[] { "hdfs://localhost:9000/user/hadoop/data/hello.txt", SemanticCategoryEnum.HDFS_URL.name(),
                    "string" }, "zyÿh://xjuûâëïmí:4703/æýuþ/qëgoõi/ñäiø/ýöcâg.fpc");// invalid

            put(new String[] { "file:///C:/code/g5/readme.txt", SemanticCategoryEnum.FILE_URL.name(), "string" },
                    "zyÿh:///Z:/juûâ/ë0/míøìïî.æýu");// valid
            put(new String[] { "file://localhost/readme.txt", SemanticCategoryEnum.FILE_URL.name(), "string" },
                    "zyÿh://xjuûâëïmí/øìïîæý.uþq");// valid
            put(new String[] { "file:/C:/code/g5/readme.txt", SemanticCategoryEnum.FILE_URL.name(), "string" },
                    "zyÿh:/Z:/juûâ/ë0/míøìïî.æýu");// invalid

            put(new String[] { "mailto:admin@talend.com?body=hello", SemanticCategoryEnum.MAILTO_URL.name(), "string" },
                    "zyÿhxj:uûâëï@míøìïî.æýu?þqëg=oõiñä");// valid
            put(new String[] { "mailto:admin@talend.com?cc=master@talend.com?body=hello", SemanticCategoryEnum.MAILTO_URL.name(),
                    "string" }, "zyÿhxj:uûâëï@míøìïî.æýu?þq=ëgoõiñ@äiøýöc.âgf?pcåz=zùábð");// valid
            put(new String[] { "mailto:admin@talend.com body=hello", SemanticCategoryEnum.MAILTO_URL.name(), "string" },
                    "zyÿhxj:uûâëï@míøìïî.æýu þqëg=oõiñä");// invalid

            put(new String[] { "data:data/test;charset=UTF-8,hello_world", SemanticCategoryEnum.DATA_URL.name(), "string" },
                    "zyÿh:xjuû/âëïm;íøìïîæý=GÌÀ-8,goõiñ_äiøýö");// valid
            put(new String[] { "data:data/test;charset=UTF-8;base64,hello_world", SemanticCategoryEnum.DATA_URL.name(),
                    "string" }, "zyÿh:xjuû/âëïm;íøìïîæý=GÌÀ-8;goõi05,iøýöc_âgfpc");// valid
            put(new String[] { "data:data/test;charset=UTF-8", SemanticCategoryEnum.DATA_URL.name(), "string" },
                    "zyÿh:xjuû/âëïm;íøìïîæý=GÌÀ-8");// invalid

            put(new String[] { "DE07 5011 0200 9000 0104 01", SemanticCategoryEnum.IBAN.name(), "string" }, "cu660n4R 6xG0 ");// valid
            put(new String[] { "DE07 5011 0200 9000 0104 0111", SemanticCategoryEnum.IBAN.name(), "string" },
                    "ÇÚ51 6550 0024 7033 1688 8260");// valid
            put(new String[] { "DE07 5011 0200 9000 01041 01", SemanticCategoryEnum.IBAN.name(), "string" },
                    "ÇÚ51 6550 0024 7033 16888 26");// invalid

        }
    };

    private static final Map<String[], String> EXPECTED_MASKED_VALUES_EXIST = new LinkedHashMap<String[], String>() {

        private static final long serialVersionUID = 2L;

        {
            // custom dictionary
            put(new String[] { "true", "NEW_CAT_NAME", "string" }, "false");
            put(new String[] { "false", "NEW_CAT_NAME", "string" }, "false");
            put(new String[] { "TRUE", "NEW_CAT_NAME", "string" }, "LÄÇÚ");
            put(new String[] { "FALSE", "NEW_CAT_NAME", "string" }, "LÄÇÚZ");
        }
    };

    /**
     * Test method for {@link org.talend.dataquality.datamasking.DataMasker#process(java.lang.Object, boolean)}.
     *
     */
    @Test
    public void testProcess() {

        for (String[] input : EXPECTED_MASKED_VALUES.keySet()) {
            String inputValue = input[0];
            String semanticCategory = input[1];
            String dataType = input[2];
            String specialRANDOM = null;
            if (input.length > 3) {
                specialRANDOM = input[3];

            }

            System.out.print("[" + semanticCategory + "]\n\t" + inputValue + " => ");
            final ValueDataMasker masker = new ValueDataMasker(semanticCategory, dataType);
            masker.getFunction().setRandom(new Random(AllSemanticTests.RANDOM_SEED));
            if (specialRANDOM != null) {
                masker.getFunction().setRandom(new Random(Long.parseLong(input[3])));
            }
            masker.getFunction().setKeepEmpty(true);
            String maskedValue = masker.maskValue(inputValue);
            assertEquals("Test faild on [" + inputValue + "]", EXPECTED_MASKED_VALUES.get(input), maskedValue);
        }
    }

    /**
     * Test method for {@link org.talend.dataquality.datamasking.DataMasker#process(java.lang.Object, boolean)}.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test
    public void testProcessCustomCategory() {
        String mockedTenantID = this.getClass().getSimpleName() + "_CustomIndex";
        MockitoAnnotations.initMocks(this);
        mockWithTenant(mockedTenantID);
        CategoryRegistryManager.setUsingLocalCategoryRegistry(true);
        CategoryRegistryManager instance = CategoryRegistryManager.getInstance();
        CustomDictionaryHolder holder = instance.getCustomDictionaryHolder(mockedTenantID);

        DQCategory answerCategory = holder.getMetadata().get(SemanticCategoryEnum.ANSWER.getTechnicalId());
        DQCategory categoryClone = SerializationUtils.clone(answerCategory); // make a clone instead of modifying the
                                                                             // shared
                                                                             // category metadata
        categoryClone.setId("NEW_CAT_ID");
        categoryClone.setName("NEW_CAT_NAME");
        categoryClone.setModified(true);
        categoryClone.setCompleteness(true);
        holder.updateCategory(categoryClone);

        DQDocument newDoc = new DQDocument();
        newDoc.setCategory(categoryClone);
        newDoc.setId("the_doc_id");
        newDoc.setValues(new HashSet<>(Arrays.asList("true", "false")));
        holder.addDataDictDocuments(Collections.singletonList(newDoc));

        for (String[] input : EXPECTED_MASKED_VALUES_EXIST.keySet()) {
            String inputValue = input[0];
            String semanticCategory = input[1];
            String dataType = input[2];

            final ValueDataMasker masker = new ValueDataMasker(semanticCategory, dataType);
            masker.getFunction().setRandom(new Random(AllSemanticTests.RANDOM_SEED));
            masker.getFunction().setKeepEmpty(true);
            String maskedValue = masker.maskValue(inputValue);
            assertEquals("Test failed on [" + inputValue + "]", EXPECTED_MASKED_VALUES_EXIST.get(input), maskedValue);
        }
        holder.deleteDataDictDocuments(Collections.singletonList(newDoc));
    }

}
