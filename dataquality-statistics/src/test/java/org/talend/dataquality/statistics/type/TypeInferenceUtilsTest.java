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
package org.talend.dataquality.statistics.type;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class TypeInferenceUtilsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeInferenceUtilsTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIsBoolean() throws Exception {
        List<String> values = loadData("testBoolean.csv");
        int countOfBooleans = 0;
        long timeStart = System.currentTimeMillis();
        // Assert total count.
        Assert.assertEquals(10000, values.size());
        for (String value : values) {
            if (TypeInferenceUtils.isBoolean(value)) {
                countOfBooleans++;
            }
        }
        long timeEnd = System.currentTimeMillis();
        // Assert count of matches.
        Assert.assertEquals(2000, countOfBooleans);
        long difference = timeEnd - timeStart;

        LOGGER.debug("The method isBoolean spent " + difference + " ms on 10000 values.");
        // Assert.assertTrue(difference < 5); //This assert depends on machine performance , so won't assert it.
    }

    @Test
    public void testIsEmpty() throws Exception {
        List<String> values = loadData("testString.csv");
        int countOfEmpties = 0;
        long timeStart = System.currentTimeMillis();
        // Assert total count.
        Assert.assertEquals(10000, values.size());
        for (String value : values) {
            if (TypeInferenceUtils.isEmpty(value)) {
                countOfEmpties++;
            }
        }
        long timeEnd = System.currentTimeMillis();
        // Assert count of matches.
        Assert.assertEquals(2000, countOfEmpties);
        long difference = timeEnd - timeStart;

        LOGGER.debug("The method isEmpty spent " + difference + " ms on 10000 values.");
        // assertTrue(difference < 6);
    }

    @Test
    public void testIsInteger() throws Exception {
        List<String> values = loadData("testInteger.csv");
        int countOfIntegers = 0;
        long timeStart = System.currentTimeMillis();
        // Assert total count.
        Assert.assertEquals(10000, values.size());
        for (String value : values) {
            if (TypeInferenceUtils.isInteger(value)) {
                countOfIntegers++;
            }
        }
        long timeEnd = System.currentTimeMillis();
        // Assert count of matches.
        Assert.assertEquals(3000, countOfIntegers);
        // Assert time span.
        long difference = timeEnd - timeStart;

        LOGGER.debug("The method isInteger spent " + difference + " ms on 10000 values.");
        // assertTrue(difference < 80);
    }

    @Test
    public void testIsDouble() throws Exception {
        String[] validEnDoubleValues = { "0.8", "1.2", "100", "100.0", "-2.0", "1.0e-04", "1.0e+4", "1E-4", "1.0 e-4", "1 E+12",
                "25%", "589.94 %", "1.65E-5%" };
        String[] validFrDoubleValues = { "0,9", "1,0e-4", "8,9568%" };
        String[] invalidDoubleValues = { "NaN", "3.4d", "123L", "123l", " 0.8", "0.8 ", "0. 8", "1. 0e-4", "1.0e -4", "3%4",
                "8%E6", "123%.4" };

        for (String value : (String[]) ArrayUtils.addAll(validEnDoubleValues, validFrDoubleValues)) {
            Assert.assertTrue(value + " is expected to be a valid decimal value but actually not.",
                    TypeInferenceUtils.isDouble(value));
        }

        for (String value : invalidDoubleValues) {
            Assert.assertFalse(value + " is expected to be an invalid decimal value but actually not.",
                    TypeInferenceUtils.isDouble(value));
        }
    }

    @Test
    public void testIsDecimal() throws Exception {

        String[] validEnDoubleValues = { "5538297118", "1045.35", "1,045.35", "1,045", "1,045,350", "2.68435E+17",
                "268 435 000 000 000 000", "265" + '\u00A0' + "435" + '\u2007' + "000" + '\u202F' + "000" };
        String[] validFrDoubleValues = { "1045,35", "1 045,35", "1.045,35", "1.045", "1 045", "1.045.350", "1 045 350" };
        String[] invalidDoubleValues = { "1 045.35", // no space allowed in US format
                "1.045.35", "1,045,35", // decimal point should not repeat
                "1,045 35", "1.045 35", // no space is allowed in decimal part
                "1,045 350", "1.045 350", // different thousands separators should not be mixed
                "1 045 35" };// grouped parts must contain 3 digits except the first one

        for (String value : validEnDoubleValues) {
            Assert.assertTrue(value + " is expected to be a valid decimal value but actually not.",
                    TypeInferenceUtils.isDouble(value));
        }

        for (String value : validFrDoubleValues) {
            Assert.assertTrue(value + " is expected to be a valid decimal value but actually not.",
                    TypeInferenceUtils.isDouble(value));
        }

        for (String value : invalidDoubleValues) {
            Assert.assertFalse(value + " is expected to be an invalid decimal value but actually not.",
                    TypeInferenceUtils.isDouble(value));
        }

    }

    @Test
    public void testPerformanceIsDouble() throws Exception {
        // test the performance of TypeInferenceUtils.isDouble method
        List<String> values = loadData("testDouble.csv");
        int countOfDoubles = 0;
        long timeStart = System.currentTimeMillis();
        // Assert total count.
        Assert.assertEquals(10000, values.size());
        for (String value : values) {
            if (TypeInferenceUtils.isDouble(value)) {
                countOfDoubles++;
            }
        }
        long timeEnd = System.currentTimeMillis();
        // Assert count of matches.
        Assert.assertEquals(5002, countOfDoubles);
        // Assert time span.
        long difference = timeEnd - timeStart;

        LOGGER.debug("The method isDouble spent " + difference + " ms on 10000 values.");
        // assertTrue(difference < 180);

    }

    @Test
    public void testIsDate() throws Exception {
        List<String> values = loadData("testDate.csv");
        int countOfDates = 0;

        TypeInferenceUtils.isDate("12/02/99");// init DateTimeFormatters
        long timeStart = System.currentTimeMillis();
        // Assert total count.
        Assert.assertEquals(10000, values.size());
        for (String value : values) {
            if (TypeInferenceUtils.isDate(value)) {
                countOfDates++;
            }
        }
        long timeEnd = System.currentTimeMillis();
        // Assert count of matches.
        Assert.assertEquals(5001, countOfDates);
        long difference = timeEnd - timeStart;

        LOGGER.debug("The method isDate spent " + difference + " ms on 10000 values.");
        // assertTrue(difference < 430);
    }

    @Test
    public void testIsDateWithCustom() throws Exception {
        String date = "Feb.12.2014=";
        assertFalse(TypeInferenceUtils.isDate(date));
        assertTrue(TypeInferenceUtils.isDate(date, Collections.singletonList("MMM.dd.yyyy=")));
    }

    @Test
    public void testIsDateWithCustomAndLocale() throws Exception {
        String date = "févr..12.2014=";
        assertFalse(TypeInferenceUtils.isDate(date));
        assertTrue(TypeInferenceUtils.isDate(date, Collections.singletonList("MMM.dd.yyyy="), Locale.FRANCE));
    }

    @Test
    public void testIsDateWithCustomAndWrongLocale() throws Exception {
        String date = "Feb.12.2014=";
        assertFalse(TypeInferenceUtils.isDate(date));
        assertFalse(TypeInferenceUtils.isDate(date, Collections.singletonList("MMM.dd.yyyy="), Locale.FRANCE));
        assertTrue(TypeInferenceUtils.isDate(date, Collections.singletonList("MMM.dd.yyyy="), Locale.US));
    }

    @Test
    public void testIsDateddMMMyyyy() {
        String dateStr = "15-Sep-2014";
        assertTrue(TypeInferenceUtils.isDate(dateStr));
    }

    @Test
    public void testIsJapaneseDateddMMMyyyy() {
        assertTrue(TypeInferenceUtils.isDate("22-五月-1999"));
        assertFalse(TypeInferenceUtils.isDate("22-明天-1999"));
    }

    private List<String> loadData(String path) throws IOException {
        List<String> values = IOUtils.readLines(this.getClass().getResourceAsStream(path), "UTF-8");
        return values;
    }

}
