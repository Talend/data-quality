// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.duplicating;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.duplicating.FieldModifier.Function;

public class FieldModifierTest {

    private static FieldModifier dataModifier;

    @BeforeClass
    public static void beforeClass() {
        dataModifier = new FieldModifier();
    }

    @Before
    public void before() {
        dataModifier.setSeed(TestConstants.RANDOM_SEED);
    }

    private static final String STRING_TO_TEST = "Suresnes"; //$NON-NLS-1$

    private static final String NUMBER_TO_TEST = "92150"; //$NON-NLS-1$

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private static final int DEFAULT_MODIF_COUNT = 3;

    @Test
    public void testSetToNull() {
        Object dup =
                dataModifier.generateDuplicate(STRING_TO_TEST, Function.SET_TO_NULL, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertNull(dup);
    }

    @Test
    public void testSetToBlank() {
        Object dup = dataModifier.generateDuplicate(STRING_TO_TEST, Function.SET_TO_BLANK, DEFAULT_MODIF_COUNT,
                EMPTY_STRING);
        assertEquals(EMPTY_STRING, dup);
    }

    @Test
    public void testReplaceLetters() {

        String EXPECTED_WORD = "SuPesKeZ"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(STRING_TO_TEST, Function.REPLACE_LETTER, DEFAULT_MODIF_COUNT,
                EMPTY_STRING);
        assertEquals(EXPECTED_WORD, dup);
    }

    @Test
    public void testAddLetters() {
        String EXPECTED_WORD = "SuresPKneZs"; //$NON-NLS-1$
        Object dup =
                dataModifier.generateDuplicate(STRING_TO_TEST, Function.ADD_LETTER, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(EXPECTED_WORD, dup);

    }

    @Test
    public void testRemoveLetters() {
        String EXPECTED_WORD = "Suese"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(STRING_TO_TEST, Function.REMOVE_LETTER, DEFAULT_MODIF_COUNT,
                EMPTY_STRING);
        assertEquals(EXPECTED_WORD, dup);
    }

    @Test
    public void testReplaceDigits() {
        String EXPECTED_NUMBER = "12120"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(NUMBER_TO_TEST, Function.REPLACE_DIGIT, DEFAULT_MODIF_COUNT,
                EMPTY_STRING);
        assertEquals(EXPECTED_NUMBER, dup);
    }

    @Test
    public void testAddDigits() {
        String EXPECTED_NUMBER = "92121510"; //$NON-NLS-1$
        Object dup =
                dataModifier.generateDuplicate(NUMBER_TO_TEST, Function.ADD_DIGIT, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(EXPECTED_NUMBER, dup);
    }

    @Test
    public void testRemoveDigits() {
        String EXPECTED_NUMBER = "92"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(NUMBER_TO_TEST, Function.REMOVE_DIGIT, DEFAULT_MODIF_COUNT,
                EMPTY_STRING);
        assertEquals(EXPECTED_NUMBER, dup);
    }

    @Test
    public void testExchageChars() {
        String EXPECTED_WORD = "Susernes"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(STRING_TO_TEST, Function.EXCHANGE_CHAR, DEFAULT_MODIF_COUNT,
                EMPTY_STRING);
        assertEquals(EXPECTED_WORD, dup);
    }

    @Test
    public void testSoundexReplace() {
        String EXPECTED_WORD = "Suresmek"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(STRING_TO_TEST, Function.SOUNDEX_REPLACE, DEFAULT_MODIF_COUNT,
                EMPTY_STRING);
        assertEquals(EXPECTED_WORD, dup);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.duplicating.FieldModifier#generateDuplicate(java.util.Date, org.talend.dataquality.duplicating.FieldModifier.Function, int, java.lang.String)}
     * .
     * 
     * case1 Function and date are both null case
     */
    @Test
    public void testGenerateDuplicateDateFunctionIntStringCase1() {
        FieldModifier fieldModifier = new FieldModifier();
        Date generateDuplicate = fieldModifier.generateDuplicate(null, null, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertNull(generateDuplicate);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.duplicating.FieldModifier#generateDuplicate(java.util.Date, org.talend.dataquality.duplicating.FieldModifier.Function, int, java.lang.String)}
     * .
     * 
     * case2 only function is null case
     */
    @Test
    public void testGenerateDuplicateDateFunctionIntStringCase2() {
        FieldModifier fieldModifier = new FieldModifier();
        Date date = new Date();
        Date generateDuplicate = fieldModifier.generateDuplicate(date, null, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(generateDuplicate, date);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.duplicating.FieldModifier#generateDuplicate(java.util.Date, org.talend.dataquality.duplicating.FieldModifier.Function, int, java.lang.String)}
     * .
     * 
     * case3 Only date is null case
     */
    @Test
    public void testGenerateDuplicateDateFunctionIntStringCase3() {
        FieldModifier fieldModifier = new FieldModifier();
        for (Function function : Function.values()) {
            // test when the Date is null,then the method will be return null
            Date generateDuplicate = fieldModifier.generateDuplicate(null, function, DEFAULT_MODIF_COUNT, EMPTY_STRING);
            assertNull(generateDuplicate);
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.duplicating.FieldModifier#generateDuplicate(java.util.Date, org.talend.dataquality.duplicating.FieldModifier.Function, int, java.lang.String)}
     * .
     * 
     * case4 function is REPLACE_BY_RANDOM_DATE case
     */
    @Test
    public void testGenerateDuplicateDateFunctionIntStringCase4() {
        FieldModifier fieldModifier = new FieldModifier();
        // test REPLACE_BY_RANDOM_DATE
        Date date = new Date();
        Date generateDuplicate = fieldModifier.generateDuplicate(date, Function.REPLACE_BY_RANDOM_DATE,
                DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertNotEquals(date, generateDuplicate);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.duplicating.FieldModifier#generateDuplicate(java.util.Date, org.talend.dataquality.duplicating.FieldModifier.Function, int, java.lang.String)}
     * .
     * 
     * case5 function is MODIFY_DATE_VALUE case
     */
    @Test
    public void testGenerateDuplicateDateFunctionIntStringCase5() {
        FieldModifier fieldModifier = new FieldModifier();
        // test MODIFY_DATE_VALUE
        Date date = new Date();
        Date generateDuplicate =
                fieldModifier.generateDuplicate(date, Function.MODIFY_DATE_VALUE, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertNotEquals(date, generateDuplicate);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.duplicating.FieldModifier#generateDuplicate(java.util.Date, org.talend.dataquality.duplicating.FieldModifier.Function, int, java.lang.String)}
     * .
     * 
     * case6 function is SWITCH_DAY_MONTH_VALUE case
     */
    @Test
    public void testGenerateDuplicateDateFunctionIntStringCase6() {
        FieldModifier fieldModifier = new FieldModifier();
        // test MODIFY_DATE_VALUE
        LocalDate localeDate = LocalDate.now();
        if (localeDate.getDayOfMonth() == localeDate.getMonthValue()) {
            localeDate = localeDate.plusDays(1);
        }
        Date date = Date.from(localeDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date generateDuplicate = fieldModifier.generateDuplicate(date, Function.SWITCH_DAY_MONTH_VALUE,
                DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertNotEquals(date, generateDuplicate);
    }
}
