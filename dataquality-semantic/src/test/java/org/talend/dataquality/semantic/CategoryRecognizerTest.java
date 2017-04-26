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
package org.talend.dataquality.semantic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.recognizer.CategoryFrequency;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class CategoryRecognizerTest {

    private static Logger log = Logger.getLogger(CategoryRecognizerTest.class);

    private static final Map<String, Float> EXPECTED_FREQUECY_TABLE = new LinkedHashMap<String, Float>() {

        private static final long serialVersionUID = -5067273062214728849L;

        {
            put("FIRST_NAME", 9.37F);
            put("MONTH", 9.37F);
            put("AIRPORT_CODE", 6.25F);
            put("CITY", 6.25F);
            put("FR_COMMUNE", 6.25F);
            put("LAST_NAME", 6.25F);
            put("EMAIL", 6.25F);
            put("US_POSTAL_CODE", 6.25F);
            put("ADDRESS_LINE", 6.25F);
            put("FR_SSN", 6.25F);
            put("PHONE", 6.25F);
            put("FR_DEPARTEMENT", 3.12F);
            put("COUNTRY", 3.12F);
            put("COUNTRY_CODE_ISO3", 3.12F);
            put("CONTINENT_CODE", 3.12F);
            put("CURRENCY_CODE", 3.12F);
            put("ANIMAL", 3.12F);
            put("LANGUAGE_CODE_ISO3", 3.12F);
            put("EN_MONTH", 3.12F);
            put("DE_POSTAL_CODE", 3.12F);
            put("FR_POSTAL_CODE", 3.12F);
            put("FR_CODE_COMMUNE_INSEE", 3.12F);
            put("COMPANY", 3.12F);
            put("GENDER", 3.12F);
            put("URL", 3.12F);
            put("US_SSN", 3.12F);
            put("ISBN_10", 3.12F);
            put("DE_PHONE", 3.12F);
            put("FR_PHONE", 3.12F);
            put("FULL_NAME", 3.12F);
            put("IBAN", 3.12F);
            put("", 12.5F);
        }

    };

    private static Map<String, String[]> EXPECTED_CAT_ID = new LinkedHashMap<String, String[]>() {

        private static final long serialVersionUID = -5067273062214728849L;

        {
            put("CDG", new String[] { SemanticCategoryEnum.AIRPORT_CODE.getId() });
            put("suresnes", new String[] { SemanticCategoryEnum.CITY.getId(), //
                    SemanticCategoryEnum.FR_COMMUNE.getId() });
            put("Paris",
                    new String[] { SemanticCategoryEnum.CITY.getId(), //
                            SemanticCategoryEnum.FIRST_NAME.getId(), //
                            SemanticCategoryEnum.LAST_NAME.getId(), //
                            SemanticCategoryEnum.FR_COMMUNE.getId(), //
                            SemanticCategoryEnum.FR_DEPARTEMENT.getId() });
            put("France",
                    new String[] { SemanticCategoryEnum.COUNTRY.getId(), //
                            SemanticCategoryEnum.LAST_NAME.getId(), //
                            SemanticCategoryEnum.FIRST_NAME.getId(), });
            put("CHN", new String[] { SemanticCategoryEnum.AIRPORT_CODE.getId(), //
                    SemanticCategoryEnum.COUNTRY_CODE_ISO3.getId(), });
            put("EUR", new String[] { SemanticCategoryEnum.CURRENCY_CODE.getId(), //
                    SemanticCategoryEnum.CONTINENT_CODE.getId() });
            put("cat", new String[] { SemanticCategoryEnum.ANIMAL.getId(), //
                    SemanticCategoryEnum.LANGUAGE_CODE_ISO3.getId() });
            put("2012-02-03 7:08PM", new String[] {});
            put("1/2/2012", new String[] {});
            put("january", new String[] { SemanticCategoryEnum.MONTH.getId(), //
                    SemanticCategoryEnum.EN_MONTH.getId() });
            put("januar", new String[] { SemanticCategoryEnum.MONTH.getId() });
            put("janvier", new String[] { SemanticCategoryEnum.MONTH.getId() });
            put("christophe", new String[] { SemanticCategoryEnum.FIRST_NAME.getId() });
            put("sda@talend.com", new String[] { SemanticCategoryEnum.EMAIL.getId() });
            put("abc@gmail.com", new String[] { SemanticCategoryEnum.EMAIL.getId() });
            put("12345",
                    new String[] { SemanticCategoryEnum.FR_POSTAL_CODE.getId(), //
                            SemanticCategoryEnum.FR_CODE_COMMUNE_INSEE.getId(), //
                            SemanticCategoryEnum.DE_POSTAL_CODE.getId(), //
                            SemanticCategoryEnum.US_POSTAL_CODE.getId() });
            put("12345-6789", new String[] { SemanticCategoryEnum.US_POSTAL_CODE.getId() });
            put("Talend", new String[] { SemanticCategoryEnum.COMPANY.getId() });
            put("9 rue pages, 92150 suresnes", new String[] { SemanticCategoryEnum.ADDRESS_LINE.getId() });
            put("avenue des champs elysees", new String[] { SemanticCategoryEnum.ADDRESS_LINE.getId() });
            put("F", new String[] { SemanticCategoryEnum.GENDER.getId() });
            put("http://www.talend.com", new String[] { SemanticCategoryEnum.URL.getId() });
            put("1 81 04 95 201 569 62", new String[] { SemanticCategoryEnum.FR_SSN.getId() });
            put("1810495201569", new String[] { SemanticCategoryEnum.FR_SSN.getId() });
            put("123-45-6789", new String[] { SemanticCategoryEnum.US_SSN.getId() });
            put("azjfnskjqnfoajr", new String[] {});
            put("ISBN 9-787-11107-5", new String[] { SemanticCategoryEnum.ISBN_10.getId() });
            put("00496-873805924", new String[] { SemanticCategoryEnum.DE_PHONE.getId() });
            put("00338.01345678", new String[] { SemanticCategoryEnum.FR_PHONE.getId() });
            put("132.2356", new String[] {});
            put("Mr. John Doe", new String[] { SemanticCategoryEnum.FULL_NAME.getId() });
            put("GB87 BARC 2065 8244 9716 55", new String[] { SemanticCategoryEnum.IBAN.getId() });
        }
    };

    private static Map<String, String[]> EXPECTED_DISPLAYNAME = new LinkedHashMap<String, String[]>() {

        private static final long serialVersionUID = -5067273062214728849L;

        {
            put("CDG", new String[] { SemanticCategoryEnum.AIRPORT_CODE.getDisplayName() });
            put("suresnes", new String[] { SemanticCategoryEnum.CITY.getDisplayName(), //
                    SemanticCategoryEnum.FR_COMMUNE.getDisplayName() });
            put("Paris",
                    new String[] { SemanticCategoryEnum.CITY.getDisplayName(), //
                            SemanticCategoryEnum.FIRST_NAME.getDisplayName(), //
                            SemanticCategoryEnum.LAST_NAME.getDisplayName(), //
                            SemanticCategoryEnum.FR_COMMUNE.getDisplayName(), //
                            SemanticCategoryEnum.FR_DEPARTEMENT.getDisplayName() });
            put("France",
                    new String[] { SemanticCategoryEnum.COUNTRY.getDisplayName(), //
                            SemanticCategoryEnum.LAST_NAME.getDisplayName(), //
                            SemanticCategoryEnum.FIRST_NAME.getDisplayName() });
            put("CHN", new String[] { SemanticCategoryEnum.AIRPORT_CODE.getDisplayName(), //
                    SemanticCategoryEnum.COUNTRY_CODE_ISO3.getDisplayName(), });
            put("EUR", new String[] { SemanticCategoryEnum.CURRENCY_CODE.getDisplayName(), //
                    SemanticCategoryEnum.CONTINENT_CODE.getDisplayName() });
            put("cat", new String[] { SemanticCategoryEnum.ANIMAL.getDisplayName(), //
                    SemanticCategoryEnum.LANGUAGE_CODE_ISO3.getDisplayName() });
            put("2012-02-03 7:08PM", new String[] {});
            put("1/2/2012", new String[] {});
            put("january", new String[] { SemanticCategoryEnum.MONTH.getDisplayName(), //
                    SemanticCategoryEnum.EN_MONTH.getDisplayName() });
            put("januar", new String[] { SemanticCategoryEnum.MONTH.getDisplayName() });
            put("janvier", new String[] { SemanticCategoryEnum.MONTH.getDisplayName() });
            put("christophe", new String[] { SemanticCategoryEnum.FIRST_NAME.getDisplayName() });
            put("sda@talend.com", new String[] { SemanticCategoryEnum.EMAIL.getDisplayName() });
            put("abc@gmail.com", new String[] { SemanticCategoryEnum.EMAIL.getDisplayName() });
            put("12345",
                    new String[] { SemanticCategoryEnum.FR_POSTAL_CODE.getDisplayName(), //
                            SemanticCategoryEnum.FR_CODE_COMMUNE_INSEE.getDisplayName(), //
                            SemanticCategoryEnum.DE_POSTAL_CODE.getDisplayName(), //
                            SemanticCategoryEnum.US_POSTAL_CODE.getDisplayName() });
            put("12345-6789", new String[] { SemanticCategoryEnum.US_POSTAL_CODE.getDisplayName() });
            put("Talend", new String[] { SemanticCategoryEnum.COMPANY.getDisplayName() });
            put("9 rue pages, 92150 suresnes", new String[] { SemanticCategoryEnum.ADDRESS_LINE.getDisplayName() });
            put("avenue des champs elysees", new String[] { SemanticCategoryEnum.ADDRESS_LINE.getDisplayName() });
            put("F", new String[] { SemanticCategoryEnum.GENDER.getDisplayName() });
            put("http://www.talend.com", new String[] { SemanticCategoryEnum.URL.getDisplayName() });
            put("1 81 04 95 201 569 62", new String[] { SemanticCategoryEnum.FR_SSN.getDisplayName() });
            put("1810495201569", new String[] { SemanticCategoryEnum.FR_SSN.getDisplayName() });
            put("123-45-6789", new String[] { SemanticCategoryEnum.US_SSN.getDisplayName() });
            put("azjfnskjqnfoajr", new String[] {});
            put("ISBN 9-787-11107-5", new String[] { SemanticCategoryEnum.ISBN_10.getDisplayName() });
            put("00496-873805924", new String[] { SemanticCategoryEnum.DE_PHONE.getDisplayName() });
            put("00338.01345678", new String[] { SemanticCategoryEnum.FR_PHONE.getDisplayName() });
            put("132.2356", new String[] {});
            put("Mr. John Doe", new String[] { SemanticCategoryEnum.FULL_NAME.getDisplayName() });
            put("GB87 BARC 2065 8244 9716 55", new String[] { SemanticCategoryEnum.IBAN.getId() });
        }
    };

    private static Map<String, String[]> EXPECTED_CAT_ID_FOR_SINGLE_VALUES = new LinkedHashMap<String, String[]>() {

        private static final long serialVersionUID = -5067273062214728849L;

        {
            put("KÄNGURU", new String[] { SemanticCategoryEnum.ANIMAL.getId() });

            put("Rueil-Malmaison", new String[] { SemanticCategoryEnum.CITY.getId(), //
                    SemanticCategoryEnum.FR_COMMUNE.getId() });
            put("Buenos Aires", new String[] { SemanticCategoryEnum.CITY.getId(), SemanticCategoryEnum.AIRPORT.getId() });
            put("Bruxelles(Jette)", new String[] { SemanticCategoryEnum.CITY.getId() });

            put("technical support", new String[] {});
            put("Software Engineer", new String[] { SemanticCategoryEnum.JOB_TITLE.getId() });
            put("ABDUL-AZIZ", new String[] { SemanticCategoryEnum.FIRST_NAME.getId() });
            put("Rue de la Cité d'Antin", new String[] { SemanticCategoryEnum.ADDRESS_LINE.getId() });

            put("BEL", new String[] { SemanticCategoryEnum.COUNTRY_CODE_ISO3.getId(),
                    SemanticCategoryEnum.LANGUAGE_CODE_ISO3.getId(), SemanticCategoryEnum.AIRPORT_CODE.getId(), });
            put("AND",
                    new String[] { SemanticCategoryEnum.COUNTRY_CODE_ISO3.getId(), SemanticCategoryEnum.AIRPORT_CODE.getId() });
            put("AVI", new String[] { SemanticCategoryEnum.FIRST_NAME.getId(), SemanticCategoryEnum.AIRPORT_CODE.getId() });

            put("Mr", new String[] { SemanticCategoryEnum.COUNTRY_CODE_ISO2.getId(), //
                    SemanticCategoryEnum.LANGUAGE_CODE_ISO2.getId(), SemanticCategoryEnum.CIVILITY.getId() });

            put("Mr.", new String[] { SemanticCategoryEnum.CIVILITY.getId() });

            put("Hartsfield–Jackson Atlanta International Airport", new String[] { SemanticCategoryEnum.AIRPORT.getId() });

        }
    };

    private static CategoryRecognizer catRecognizer;

    @BeforeClass
    public static void prepare() throws URISyntaxException, IOException {
        CategoryRecognizerBuilder b = CategoryRecognizerBuilder.newBuilder();
        // catRecognizer = b.es().host("localhost").port(9300).cluster("elasticsearch").build();
        final URI ddPath = CategoryRecognizerTest.class.getResource(CategoryRecognizerBuilder.DEFAULT_DD_PATH).toURI();
        final URI kwPath = CategoryRecognizerTest.class.getResource(CategoryRecognizerBuilder.DEFAULT_KW_PATH).toURI();
        final URI rePath = CategoryRecognizerTest.class.getResource(CategoryRecognizerBuilder.DEFAULT_RE_PATH).toURI();
        catRecognizer = b.lucene().ddPath(ddPath).kwPath(kwPath).regexPath(rePath).build();
    }

    @Before
    public void init() {
        catRecognizer.reset();
    }

    public void testProcess() {

        catRecognizer.prepare();

        for (String data : EXPECTED_CAT_ID.keySet()) {

            System.out.println("-------------------------------");
            String[] catNames = catRecognizer.process(data);
            System.out.printf("%-30s  \t  %-20s\n", "[" + data + "]", Arrays.toString(catNames));

            Collection<CategoryFrequency> result = catRecognizer.getResult();
            for (CategoryFrequency frequencyTableItem : result) {

                System.out.println("frequencyTableItem = " + frequencyTableItem.getCategoryId() + " / "
                        + frequencyTableItem.getFrequency() + " %");

            }

        }

    }

    @Test
    public void testSingleColumn() {
        catRecognizer.prepare();
        for (String data : EXPECTED_CAT_ID.keySet()) {
            String[] catNames = catRecognizer.process(data);
            // System.out.println(data + " data: " + Arrays.asList(catNames));
            // System.out.println(data + " expected category id: " + Arrays.asList(EXPECTED_CAT_ID.get(data)));
            // System.out.println(data + " expected displayname: " + Arrays.asList(EXPECTED_DISPLAYNAME.get(data)));
            assertEquals("Invalid assumption for data " + data, EXPECTED_CAT_ID.get(data).length, catNames.length); //$NON-NLS-1$
            assertEquals("Invalid assumption for data " + data, EXPECTED_DISPLAYNAME.get(data).length, catNames.length); //$NON-NLS-1$
            for (String catName : catNames) {
                // System.out.println(Arrays.asList(EXPECTED_DISPLAYNAME.get(data)) + catName.toString());
                assertTrue("Category ID <" + catName + "> is not recognized for data <" + data + ">",
                        Arrays.asList(EXPECTED_CAT_ID.get(data)).contains(catName));
            }
        }

        Collection<CategoryFrequency> result = catRecognizer.getResult();

        assertEquals(EXPECTED_FREQUECY_TABLE.size(), result.size());
        for (CategoryFrequency tableItem : result) {
            log.debug("frequencyTableItem = " + tableItem.getCategoryId() + " / " + tableItem.getCount() + " / "
                    + tableItem.getFrequency() + " %");

            System.out.println("put(\"" + tableItem.getCategoryId() + "\", " + tableItem.getFrequency() + "F);");
            assertEquals(EXPECTED_FREQUECY_TABLE.get(tableItem.getCategoryId()), tableItem.getFrequency(), 0.001);
        }

    }

    @Test
    public void testSingleValues() {
        catRecognizer.prepare();

        for (String data : EXPECTED_CAT_ID_FOR_SINGLE_VALUES.keySet()) {
            String[] catNames = catRecognizer.process(data);
            assertEquals("Invalid assumption for data <" + data + ">, actual categories: " + Arrays.asList(catNames), //$NON-NLS-1$
                    EXPECTED_CAT_ID_FOR_SINGLE_VALUES.get(data).length, catNames.length);
            // System.out.println("\n" + data + " => expected: " + Arrays.asList(EXPECTED_CAT_ID_FOR_SINGLE_VALUES.get(data)));

            for (String catName : catNames) {
                assertTrue(
                        data + " => actual: " + catName + " => expected: "
                                + Arrays.asList(EXPECTED_CAT_ID_FOR_SINGLE_VALUES.get(data)),
                        Arrays.asList(EXPECTED_CAT_ID_FOR_SINGLE_VALUES.get(data)).contains(catName));
            }
        }

    }

}
