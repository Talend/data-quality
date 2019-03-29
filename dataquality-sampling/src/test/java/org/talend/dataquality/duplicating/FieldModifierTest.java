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
package org.talend.dataquality.duplicating;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Assert;
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
        dataModifier.setSeed(AllDataqualitySamplingTests.RANDOM_SEED);
    }

    private static final String STRING_TO_TEST = "Suresnes"; //$NON-NLS-1$

    private static final String NUMBER_TO_TEST = "92150"; //$NON-NLS-1$

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private static final int DEFAULT_MODIF_COUNT = 3;

    @Test
    public void testSetToNull() {
        Object dup = dataModifier.generateDuplicate(STRING_TO_TEST, Function.SET_TO_NULL, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(null, dup);
    }

    @Test
    public void testSetToBlank() {
        Object dup = dataModifier.generateDuplicate(STRING_TO_TEST, Function.SET_TO_BLANK, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(EMPTY_STRING, dup);
    }

    @Test
    public void testReplaceLetters() {

        String EXPECTED_WORD = "SuPesKeZ"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(STRING_TO_TEST, Function.REPLACE_LETTER, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(EXPECTED_WORD, dup);
    }

    @Test
    public void testAddLetters() {
        String EXPECTED_WORD = "SuresPKneZs"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(STRING_TO_TEST, Function.ADD_LETTER, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(EXPECTED_WORD, dup);

    }

    @Test
    public void testRemoveLetters() {
        String EXPECTED_WORD = "Suese"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(STRING_TO_TEST, Function.REMOVE_LETTER, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(EXPECTED_WORD, dup);
    }

    @Test
    public void testReplaceDigits() {
        String EXPECTED_NUMBER = "12120"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(NUMBER_TO_TEST, Function.REPLACE_DIGIT, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(EXPECTED_NUMBER, dup);
    }

    @Test
    public void testAddDigits() {
        String EXPECTED_NUMBER = "92121510"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(NUMBER_TO_TEST, Function.ADD_DIGIT, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(EXPECTED_NUMBER, dup);
    }

    @Test
    public void testRemoveDigits() {
        String EXPECTED_NUMBER = "92"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(NUMBER_TO_TEST, Function.REMOVE_DIGIT, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(EXPECTED_NUMBER, dup);
    }

    @Test
    public void testExchageChars() {
        String EXPECTED_WORD = "Susernes"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(STRING_TO_TEST, Function.EXCHANGE_CHAR, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(EXPECTED_WORD, dup);
    }

    @Test
    public void testSoundexReplace() {
        String EXPECTED_WORD = "Suresmek"; //$NON-NLS-1$
        Object dup = dataModifier.generateDuplicate(STRING_TO_TEST, Function.SOUNDEX_REPLACE, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(EXPECTED_WORD, dup);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.duplicating.FieldModifier#generateDuplicate(java.util.Date, org.talend.dataquality.duplicating.FieldModifier.Function, int, java.lang.String)}
     * .
     * 
     * case1 date is null case
     */
    @Test
    public void testGenerateDuplicateDateFunctionIntStringCase1() {
        FieldModifier fieldModifier = new FieldModifier();
        Date generateDuplicate = fieldModifier.generateDuplicate(null, null, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        Assert.assertNull(generateDuplicate);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.duplicating.FieldModifier#generateDuplicate(java.util.Date, org.talend.dataquality.duplicating.FieldModifier.Function, int, java.lang.String)}
     * .
     * 
     * case2 function is null case
     */
    @Test
    public void testGenerateDuplicateDateFunctionIntStringCase2() {
        FieldModifier fieldModifier = new FieldModifier();
        Date date = new Date();
        Date generateDuplicate = fieldModifier.generateDuplicate(date, null, DEFAULT_MODIF_COUNT, EMPTY_STRING);
        assertEquals(generateDuplicate, date);
    }

}
