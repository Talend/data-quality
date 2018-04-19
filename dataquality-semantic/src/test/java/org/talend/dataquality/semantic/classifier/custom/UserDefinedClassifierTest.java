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
package org.talend.dataquality.semantic.classifier.custom;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.ISubCategory;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class UserDefinedClassifierTest {

    // private UserDefinedClassifier userDefinedClassifier = null;

    /**
     * created by talend on 2015-07-28 Detailled comment.
     *
     */
    private static enum STATE {
        Alabama,
        Alaska,
        Arizona,
        Arkansas,
        California,
        Colorado,
        Connecticut,
        Delaware,
        Florida,
        Georgia,
        Hawaii,
        Idaho,
        Illinois,
        Indiana,
        Iowa,
        Kansas,
        Kentucky,
        Louisiana,
        Maine,
        Maryland,
        Massachusetts,
        Michigan,
        Minnesota,
        Mississippi,
        Missouri,
        Montana,
        Nebraska,
        Nevada,
        New_Hampshire,
        New_Jersey,
        New_Mexico,
        New_York,
        North_Carolina,
        North_Dakota,
        Ohio,
        Oklahoma,
        Oregon,
        Pennsylvania,
        Rhode_Island,
        South_Carolina,
        South_Dakota,
        Tennessee,
        Texas,
        Utah,
        Vermont,
        Virginia,
        Washington,
        West_Virginia,
        Wisconsin,
        Wyoming;

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return super.toString().replace("_", " "); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private final String TLD_NAME_URL = "http://data.iana.org/TLD/tlds-alpha-by-domain.txt";

    private final int MAX_TLD_LENGTH = 24;

    /**
     * Map from technical ID to category name
     * 583edc44ec06957a34fa6456 -> EN_MONTH
     * 583edc44ec06957a34fa646a -> EN_MONTH_ABBREV
     * 583edc44ec06957a34fa644c -> UK_SSN
     * 583edc44ec06957a34fa6448 -> SE_SSN
     * 583edc44ec06957a34fa6444 -> FR_SSN
     * 583edc44ec06957a34fa642e -> US_SSN
     * 583edc44ec06957a34fa6430 -> EMAIL
     * 583edc44ec06957a34fa645e -> FR_CODE_COMMUNE_INSEE
     * 583edc44ec06957a34fa643c -> FR_POSTAL_CODE
     * 583edc44ec06957a34fa647c -> DE_POSTAL_CODE
     * 583edc44ec06957a34fa6488 -> US_POSTAL_CODE
     * 583edc44ec06957a34fa6474 -> US_STATE_CODE
     * 583edc44ec06957a34fa6470 -> US_STATE
     * 583edc44ec06957a34fa6434 -> URL
     * 583edc44ec06957a34fa642c -> WEB_DOMAIN
     * 583edc44ec06957a34fa6446 -> ISBN_10
     * 583edc44ec06957a34fa644a -> ISBN_13
     * 583edc44ec06957a34fa6438 -> MAC_ADDRESS
     * 583edc44ec06957a34fa644e -> EN_MONEY_AMOUNT
     * 583edc44ec06957a34fa6468 -> FR_MONEY_AMOUNT
     * 583edc44ec06957a34fa643e -> DE_PHONE
     * 583edc44ec06957a34fa646c -> FR_PHONE
     * 583edc44ec06957a34fa645c -> US_PHONE
     * 583edc44ec06957a34fa6476 -> COLOR_HEX_CODE
     * 583edc44ec06957a34fa6432 -> BG_VAT_NUMBER
     * 583edc44ec06957a34fa6482 -> AT_VAT_NUMBER
     * 583edc44ec06957a34fa646e -> GEO_COORDINATES
     * 583edc44ec06957a34fa6436 -> GEO_COORDINATE
     * 583edc44ec06957a34fa6462 -> GEO_COORDINATES_DEG
     * 583edc44ec06957a34fa643a -> EN_WEEKDAY
     * 583edc44ec06957a34fa6484 -> SEDOL
     * 583edc44ec06957a34fa647a -> HDFS_URL
     * 583edc44ec06957a34fa6472 -> FILE_URL
     * 583edc44ec06957a34fa645a -> MAILTO_URL
     * 583edc44ec06957a34fa6464 -> DATA_URL
     * 583edc44ec06957a34fa6460 -> IBAN
     *
     */
    public static Map<String, String[]> TEST_DATA = new LinkedHashMap<String, String[]>() {

        private static final long serialVersionUID = -5067273062214728849L;

        {
            // put (value, expected categories)
            put("CDG", new String[] {}); //$NON-NLS-1$
            put("suresnes", new String[] {});//$NON-NLS-1$
            put("Paris", new String[] {});//$NON-NLS-1$
            put("France", new String[] {});//$NON-NLS-1$
            put("? - lfd", new String[] {});//$NON-NLS-1$
            put("CHN", new String[] {});//$NON-NLS-1$
            put("cat", new String[] {});//$NON-NLS-1$
            put("2012-02-03 7:08PM", new String[] {});//$NON-NLS-1$
            put("1/2/2012", new String[] {});//$NON-NLS-1$
            put("january", new String[] { "583edc44ec06957a34fa6456" });//$NON-NLS-1$ //$NON-NLS-2$
            put("jan", new String[] { "583edc44ec06957a34fa646a" });//$NON-NLS-1$ //$NON-NLS-2$
            put("february", new String[] { "583edc44ec06957a34fa6456" });//$NON-NLS-1$ //$NON-NLS-2$
            put("march", new String[] { "583edc44ec06957a34fa6456" });//$NON-NLS-1$ //$NON-NLS-2$
            put("auG", new String[] { "583edc44ec06957a34fa646a" });//$NON-NLS-1$ //$NON-NLS-2$
            put("MAY", new String[] { "583edc44ec06957a34fa6456", "583edc44ec06957a34fa646a" });//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            put("januar", new String[] {});//$NON-NLS-1$
            put("janvier", new String[] {});//$NON-NLS-1$

            put("AB123456C", new String[] { "583edc44ec06957a34fa644c" });//$NON-NLS-1$ //$NON-NLS-2$
            put("AB 12 34 56 C", new String[] { "583edc44ec06957a34fa644c" });//$NON-NLS-1$ //$NON-NLS-2$
            put("TN 31 12 58 F", new String[] { "583edc44ec06957a34fa644c" });//$NON-NLS-1$ //$NON-NLS-2$
            put("20120101-3842", new String[] { "583edc44ec06957a34fa6448" });//$NON-NLS-1$ //$NON-NLS-2$
            put("120101-3842", new String[] {});//$NON-NLS-1$

            put("christophe", new String[] {});//$NON-NLS-1$
            put("sda@talend.com", new String[] { "583edc44ec06957a34fa6430" });//$NON-NLS-1$ //$NON-NLS-2$
            put("abc@gmail.com", new String[] { "583edc44ec06957a34fa6430" }); //$NON-NLS-1$ //$NON-NLS-2$
            put(" abc@gmail.com ", new String[] { "583edc44ec06957a34fa6430" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("abc@gmail.com ", new String[] { "583edc44ec06957a34fa6430" }); //$NON-NLS-1$ //$NON-NLS-2$
            put(" abc@gmail.com", new String[] { "583edc44ec06957a34fa6430" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("abc@gmail", new String[] {}); //$NON-NLS-1$
            put("12345", new String[] { "583edc44ec06957a34fa645e", "583edc44ec06957a34fa643c", "583edc44ec06957a34fa647c", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    "583edc44ec06957a34fa6488" }); //$NON-NLS-1$
            put("2A345", new String[] { "583edc44ec06957a34fa645e" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("12345-6789", new String[] { "583edc44ec06957a34fa6488" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            put("Talend", new String[] {}); //$NON-NLS-1$
            put("9 rue pages, 92150 suresnes", new String[] {}); //$NON-NLS-1$
            put("avenue des champs elysees", new String[] {}); //$NON-NLS-1$
            put("MA", new String[] { "583edc44ec06957a34fa6474" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("FL", new String[] { "583edc44ec06957a34fa6474" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("FLorIda", new String[] { "583edc44ec06957a34fa6470" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("FLORIDA", new String[] { "583edc44ec06957a34fa6470" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("New Hampshire", new String[] { "583edc44ec06957a34fa6470" });//$NON-NLS-1$ //$NON-NLS-2$
            put("Arizona", new String[] { "583edc44ec06957a34fa6470" });//$NON-NLS-1$ //$NON-NLS-2$
            put("Alabama", new String[] { "583edc44ec06957a34fa6470" });//$NON-NLS-1$ //$NON-NLS-2$
            put("F", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("M", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Male", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("female", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$

            put("http://www.talend.com", new String[] { "583edc44ec06957a34fa6434" });//$NON-NLS-1$ //$NON-NLS-2$
            put("www.talend.com", new String[] { "583edc44ec06957a34fa642c" });//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            put("talend.com", new String[] { "583edc44ec06957a34fa642c" });//$NON-NLS-1$ //$NON-NLS-2$
            put("talend.com", new String[] { "583edc44ec06957a34fa642c" });//$NON-NLS-1$ //$NON-NLS-2$
            put("talend.veryLongTDL", new String[] { "583edc44ec06957a34fa642c" });//$NON-NLS-1$ //$NON-NLS-2$
            put("talend.TDLlongerThan25Characters", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("talendSmallerThan63Charactersxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.com", //$NON-NLS-1$
                    new String[] { "583edc44ec06957a34fa642c" });//$NON-NLS-1$
            put("talendLongerThan63Charactersxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.com", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$

            put("1 81 04 95 201 569 62", new String[] { "583edc44ec06957a34fa6444" });//$NON-NLS-1$ //$NON-NLS-2$
            put("1810495201569", new String[] { "583edc44ec06957a34fa6444" });//$NON-NLS-1$ //$NON-NLS-2$
            put("123-45-6789", new String[] { "583edc44ec06957a34fa642e" });//$NON-NLS-1$ //$NON-NLS-2$
            put("azjfnskjqnfoajr", new String[] {});//$NON-NLS-1$
            put("ISBN 9-787-11107-5", new String[] { "583edc44ec06957a34fa6446" });//$NON-NLS-1$ //$NON-NLS-2$
            put("SINB 9-787-11107-5", new String[] {});//$NON-NLS-1$
            put("ISBN 2-711-79141-6", new String[] { "583edc44ec06957a34fa6446" });//$NON-NLS-1$ //$NON-NLS-2$
            put("ISBN-13: 978-2711791415", new String[] { "583edc44ec06957a34fa644a" });//$NON-NLS-1$ //$NON-NLS-2$
            put("ISBN: 978-2711791415", new String[] { "583edc44ec06957a34fa644a" });//$NON-NLS-1$ //$NON-NLS-2$
            put("ISBN 978-2711791415", new String[] { "583edc44ec06957a34fa644a" });//$NON-NLS-1$ //$NON-NLS-2$

            put("A4:4E:31:B9:C5:B4", new String[] { "583edc44ec06957a34fa6438" });//$NON-NLS-1$ //$NON-NLS-2$
            put("A4:4E:31:B9:C5:B4:B4", new String[] {});//$NON-NLS-1$
            put("A4-4E-31-B9-C5-B4", new String[] {});//$NON-NLS-1$

            put("$3,000", new String[] { "583edc44ec06957a34fa644e" });//$NON-NLS-1$ //$NON-NLS-2$
            put("$3000", new String[] { "583edc44ec06957a34fa644e" });//$NON-NLS-1$ //$NON-NLS-2$
            put("$ 3000", new String[] {});//$NON-NLS-1$
            put("CA$3000", new String[] { "583edc44ec06957a34fa644e" });//$NON-NLS-1$ //$NON-NLS-2$
            put("€3000", new String[] { "583edc44ec06957a34fa644e" });//$NON-NLS-1$ //$NON-NLS-2$
            put("3000 €", new String[] { "583edc44ec06957a34fa6468" });//$NON-NLS-1$ //$NON-NLS-2$
            put("345,56 €", new String[] { "583edc44ec06957a34fa6468" });//$NON-NLS-1$ //$NON-NLS-2$
            put("35 k€", new String[] { "583edc44ec06957a34fa6468" });//$NON-NLS-1$ //$NON-NLS-2$
            put("35 054 T€", new String[] { "583edc44ec06957a34fa6468" });//$NON-NLS-1$ //$NON-NLS-2$
            put("35 456 544 k£", new String[] { "583edc44ec06957a34fa6468" });//$NON-NLS-1$ //$NON-NLS-2$

            put("00496-8738059275", new String[] { "583edc44ec06957a34fa643e" });//$NON-NLS-1$ //$NON-NLS-2$
            put("00338.01345678", new String[] { "583edc44ec06957a34fa646c" });//$NON-NLS-1$ //$NON-NLS-2$

            put("John Doe", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges W. Bush", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges W. Bush Jr.", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges W. Bush, Jr.", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges W. Bush II", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges W. Bush III", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges W. Bush IV", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Georges Bush IV", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Jean-Michel Louis", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("David F Walker", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("J. S. Smith, Jr.", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$
            put("Catherine Zeta-Jones", new String[] {});//$NON-NLS-1$ //$NON-NLS-2$

            put("#990000", new String[] { "583edc44ec06957a34fa6476" });//$NON-NLS-1$ //$NON-NLS-2$
            put("#AAAAAA", new String[] { "583edc44ec06957a34fa6476" });//$NON-NLS-1$ //$NON-NLS-2$
            put("#cc3366", new String[] { "583edc44ec06957a34fa6476" });//$NON-NLS-1$ //$NON-NLS-2$
            put("#c1d906", new String[] { "583edc44ec06957a34fa6476" });//$NON-NLS-1$ //$NON-NLS-2$

            put("BG123456789", new String[] { "583edc44ec06957a34fa6432" });//$NON-NLS-1$ //$NON-NLS-2$
            put("BG123456789", new String[] { "583edc44ec06957a34fa6432" });//$NON-NLS-1$ //$NON-NLS-2$
            put("AT12345678", new String[] { "583edc44ec06957a34fa6482" });//$NON-NLS-1$ //$NON-NLS-2$

            // put("132.2356", new String[] { "LONGITUDE_LATITUDE_COORDINATE" });
            put("40.7127837,-74.00594130000002", new String[] { "583edc44ec06957a34fa646e" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("30.082993", new String[] { "583edc44ec06957a34fa6436" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("N 0:59:59.99,E 0:59:59.99", new String[] { "583edc44ec06957a34fa6462" }); //$NON-NLS-1$ //$NON-NLS-2$

            put("00:00", new String[] {}); //$NON-NLS-1$ //$NON-NLS-2$
            put("12:00", new String[] {}); //$NON-NLS-1$ //$NON-NLS-2$
            put("11:23", new String[] {}); //$NON-NLS-1$ //$NON-NLS-2$
            put("15:53", new String[] {}); //$NON-NLS-1$ //$NON-NLS-2$
            put("23:59", new String[] {}); //$NON-NLS-1$ //$NON-NLS-2$

            put("Monday", new String[] { "583edc44ec06957a34fa643a" }); //$NON-NLS-1$ //<$NON-NLS-2$
            put("MonDay", new String[] { "583edc44ec06957a34fa643a" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("MOnDay", new String[] { "583edc44ec06957a34fa643a" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("MOn", new String[] { "583edc44ec06957a34fa643a" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("Tue", new String[] { "583edc44ec06957a34fa643a" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("Wed", new String[] { "583edc44ec06957a34fa643a" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("Wednesday", new String[] { "583edc44ec06957a34fa643a" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("Thurs", new String[] { "583edc44ec06957a34fa643a" }); //$NON-NLS-1$ //$NON-NLS-2$

            put("25:59", new String[] {}); // does not match TIME (as expected) //$NON-NLS-1$

            put("0067340", new String[] { "583edc44ec06957a34fa6484" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("4155586", new String[] { "583edc44ec06957a34fa6484", "583edc44ec06957a34fa645c" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("(541) 754-3010", new String[] { "583edc44ec06957a34fa645c" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("B01HL06", new String[] { "583edc44ec06957a34fa6484" }); //$NON-NLS-1$ //$NON-NLS-2$

            put("132.2356", new String[] {}); //$NON-NLS-1$
            put("R&D", new String[] {}); //$NON-NLS-1$

            put("hdfs://127.0.0.1/user/luis/sample.txt", new String[] { "583edc44ec06957a34fa647a" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("hdfs://toto.com/user/luis/sample.txt", new String[] { "583edc44ec06957a34fa647a" }); //$NON-NLS-1$ //$NON-NLS-2$

            put("file://localhost/c/WINDOWS/clock.avi", new String[] { "583edc44ec06957a34fa6472" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("file://localhost/c|/WINDOWS/clock.avi", new String[] { "583edc44ec06957a34fa6472" }); //$NON-NLS-1$ //$NON-NLS-2$
                                                                                                       // "
            put("file://localhost/c:/WINDOWS/clock.avi", new String[] { "583edc44ec06957a34fa6472" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("file:///C:/WORKSPACE/reports.html", new String[] { "583edc44ec06957a34fa6472" }); //$NON-NLS-1$ //$NON-NLS-2$

            put("mailto:?to=&subject=mailto%20with%20examples&body=http://en.wikipedia.org/wiki/Mailto", //$NON-NLS-1$
                    new String[] { "583edc44ec06957a34fa645a" }); //$NON-NLS-1$
            put("mailto:someone@example.com?subject=This%20is%20the%20subject", new String[] { "583edc44ec06957a34fa645a" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("mailto:p.dupond@example.com?subject=Sujet%20du%20courrier&cc=pierre@example.org&cc=jacques@example.net&body=Bonjour", //$NON-NLS-1$
                    new String[] { "583edc44ec06957a34fa645a" }); //$NON-NLS-1$

            put("data:text/html;charset=US-ASCII,%3Ch1%3EHello!%3C%2Fh1%3E", new String[] { "583edc44ec06957a34fa6464" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("data:TEXT/html;charset=US-ASCII,%3Ch1%3EHello!%3C%2Fh1%3E", new String[] { "583edc44ec06957a34fa6464" });
            put("data:text/html;charset=,%3Ch1%3EHello!%3C%2Fh1%3E", new String[] { "583edc44ec06957a34fa6464" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQAQMAAAAlPW0iAAAABlBMVEUAAAD///+l2Z/dAAAA", //$NON-NLS-1$
                    new String[] { "583edc44ec06957a34fa6464" }); //$NON-NLS-1$
            put("data:,Hello World!", new String[] { "583edc44ec06957a34fa6464" }); //$NON-NLS-1$ //$NON-NLS-2$
            put("FR7630001007941234567890185", new String[] { "583edc44ec06957a34fa6460" });
        }
    };

    @Before
    public void prepare() {
        for (STATE state : STATE.values()) {
            TEST_DATA.put(state.toString(), new String[] { "583edc44ec06957a34fa6470" }); //$NON-NLS-1$
        }
    }

    @Test
    public void testClassify() throws IOException {
        UserDefinedClassifier userDefinedClassifier = new UDCategorySerDeser().readJsonFile();
        for (String str : TEST_DATA.keySet()) {
            Set<String> cats = userDefinedClassifier.classify(str);
            String[] expect_values = TEST_DATA.get(str);
            assertEquals("unexpected size for " + str, expect_values.length, cats.size()); //$NON-NLS-1$
            Object[] catsArray = new String[cats.size()];
            int i = 0;
            for (String cat : cats) {
                catsArray[i++] = cat;
            }
            Arrays.sort(catsArray);
            Arrays.sort(expect_values);
            assertArrayEquals("wrong category found for input string: " + str, expect_values, catsArray); //$NON-NLS-1$
        }
    }

    @Test
    public void testUniqueNames() throws IOException {
        UserDefinedClassifier userDefinedClassifier = new UDCategorySerDeser().readJsonFile();
        Set<ISubCategory> classifiers = userDefinedClassifier.getClassifiers();
        Set<String> names = new HashSet<>();
        for (ISubCategory iSubCategory : classifiers) {
            String name = iSubCategory.getLabel();
            assertTrue("Category Name: " + name + " is duplicated!", names.add(name)); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    @Test
    public void testUniqueIds() throws IOException {
        UserDefinedClassifier userDefinedClassifier = new UDCategorySerDeser().readJsonFile();
        Set<ISubCategory> classifiers = userDefinedClassifier.getClassifiers();
        Set<String> ids = new HashSet<>();
        for (ISubCategory iSubCategory : classifiers) {
            String id = iSubCategory.getName();
            assertTrue("Category Id: " + id + " is duplicated!", ids.add(id)); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    @Test
    public void testAddAndRemoveSubCategory() throws IOException {
        UserDefinedClassifier userDefinedClassifier = new UserDefinedClassifier();
        addAndRemoveCategories(userDefinedClassifier);

        userDefinedClassifier = UDCategorySerDeser.readJsonFile();
        addAndRemoveCategories(userDefinedClassifier);
    }

    @Test
    public void testInsertOrUpdate() {
        UserDefinedClassifier userDefinedClassifier = new UserDefinedClassifier();
        String id = "this is the Id"; //$NON-NLS-1$
        UserDefinedCategory cat = new UserDefinedCategory(id);
        assertTrue(userDefinedClassifier.insertOrUpdateSubCategory(cat));
        assertEquals("by default, the name should be same as the id!", id, cat.getLabel()); //$NON-NLS-1$
        cat.setLabel("my name"); //$NON-NLS-1$
        assertTrue(userDefinedClassifier.insertOrUpdateSubCategory(cat));
        Iterator<ISubCategory> it = userDefinedClassifier.getClassifiers().iterator();
        while (it.hasNext()) {
            ISubCategory c = it.next();
            assertEquals(cat.getLabel(), c.getLabel());
        }
    }

    @Test
    public void checkLongestTLD() {
        String outputFilename = "TLD.txt";

        downloadFile(TLD_NAME_URL, outputFilename);
        File file = new File(outputFilename);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                int length = line.trim().length();
                if (!line.startsWith("#") && length > 0) { // the comments don't count
                    assertTrue("Expected MAX_LENGTH of web domain is " + String.valueOf(MAX_TLD_LENGTH),
                            length <= MAX_TLD_LENGTH);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.delete();
    }

    /**
     * download a specified file
     * 
     * @param urlFilename the url to access the file
     * @param outputFilename the complete path+filename for output
     */
    private void downloadFile(String urlFilename, String outputFilename) {
        FileOutputStream outputStream = null;
        try {
            URL url = new URL(urlFilename);
            ReadableByteChannel bytes = Channels.newChannel(url.openStream());
            outputStream = new FileOutputStream(outputFilename);
            outputStream.getChannel().transferFrom(bytes, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addAndRemoveCategories(UserDefinedClassifier userDefinedClassifier) {
        int sizeBefore = userDefinedClassifier.getClassifiers().size();
        String id = "this is the Id"; //$NON-NLS-1$
        UserDefinedCategory cat = new UserDefinedCategory(id);
        userDefinedClassifier.removeSubCategory(cat);
        int sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals("Expect to have the same size because the removed category does not exist in the list of categories. Size=" //$NON-NLS-1$
                + userDefinedClassifier.getClassifiers().size(), sizeBefore, sizeAfter);

        userDefinedClassifier.addSubCategory(cat);
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals(
                "Expect to have a different size because we add a category that does not exist in the list of categories. Size=" //$NON-NLS-1$
                        + userDefinedClassifier.getClassifiers().size(),
                sizeBefore + 1, sizeAfter);

        userDefinedClassifier.addSubCategory(cat);
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals(
                "Expect to have only one more element than the original size because the category now exists in the list of categories. Size=" //$NON-NLS-1$
                        + userDefinedClassifier.getClassifiers().size(),
                sizeBefore + 1, sizeAfter);

        userDefinedClassifier.removeSubCategory(cat);
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals("Expect to have the same size because we removed the added category. Size=" //$NON-NLS-1$
                + userDefinedClassifier.getClassifiers().size(), sizeBefore, sizeAfter);

        userDefinedClassifier.removeSubCategory(cat);
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals("Expect to have the same size because we removed twice the same category. Size=" //$NON-NLS-1$
                + userDefinedClassifier.getClassifiers().size(), sizeBefore, sizeAfter);

        // now add again twice
        assertTrue(userDefinedClassifier.addSubCategory(cat));
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals(
                "Expect to have a different size because we add a category that does not exist in the list of categories. Size=" //$NON-NLS-1$
                        + userDefinedClassifier.getClassifiers().size(),
                sizeBefore + 1, sizeAfter);

        assertFalse(userDefinedClassifier.addSubCategory(cat));
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals(
                "Expect to have a still have the same size because we add a category that already exists in the list of categories. Size=" //$NON-NLS-1$
                        + userDefinedClassifier.getClassifiers().size(),
                sizeBefore + 1, sizeAfter);

        UserDefinedCategory cat2 = new UserDefinedCategory(id);
        cat2.setLabel("my name"); //$NON-NLS-1$
        assertFalse(userDefinedClassifier.addSubCategory(cat2));
        sizeAfter = userDefinedClassifier.getClassifiers().size();
        assertEquals(
                "Expect to have a still have the same size because we add a category that already exists in the list of categories. Size=" //$NON-NLS-1$
                        + userDefinedClassifier.getClassifiers().size(),
                sizeBefore + 1, sizeAfter);

    }
}
