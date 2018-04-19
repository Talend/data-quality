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
package org.talend.dataquality.record.linkage.utils;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * created by zshen on Nov 21, 2013 Detailled comment
 * 
 */
public class AlgorithmSwitchTest {

    /**
     * DOC zshen Comment method "setUpBeforeClass".
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * DOC zshen Comment method "tearDownAfterClass".
     * 
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * DOC zshen Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * DOC zshen Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private static final String QUO_STR = "\""; //$NON-NLS-1$

    private static final String SPACE_STR = " "; //$NON-NLS-1$

    private static final String NULL_STR = "null"; //$NON-NLS-1$

    private static final String BLANK_STR = ""; //$NON-NLS-1$

    private static final String TEST_STR = "test"; //$NON-NLS-1$

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.AlgorithmSwitch#getPreAlgoResult(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetPreAlgoResult() {
        // LEFT_CHAR
        assertEquals("<test", //$NON-NLS-1$
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LEFT_CHAR.getComponentValueName(), "<", TEST_STR)); //$NON-NLS-1$
        assertEquals(TEST_STR,
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LEFT_CHAR.getComponentValueName(), null, TEST_STR));
        assertEquals(NULL_STR, AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LEFT_CHAR.getComponentValueName(),
                NULL_STR, BLANK_STR));
        assertEquals("\\test", //$NON-NLS-1$
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LEFT_CHAR.getComponentValueName(), "\\", TEST_STR)); //$NON-NLS-1$
        assertEquals(QUO_STR,
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LEFT_CHAR.getComponentValueName(), QUO_STR, null));

        // RIGHT_CHAR
        assertEquals("test<", //$NON-NLS-1$
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.RIGHT_CHAR.getComponentValueName(), "<", TEST_STR)); //$NON-NLS-1$
        assertEquals(TEST_STR,
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.RIGHT_CHAR.getComponentValueName(), null, TEST_STR));
        assertEquals(NULL_STR, AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.RIGHT_CHAR.getComponentValueName(),
                NULL_STR, BLANK_STR));
        assertEquals("test\\", //$NON-NLS-1$
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.RIGHT_CHAR.getComponentValueName(), "\\", TEST_STR)); //$NON-NLS-1$
        assertEquals(QUO_STR,
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.RIGHT_CHAR.getComponentValueName(), QUO_STR, null));
        // LOWER_CASE

        assertEquals(null,
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LOWER_CASE.getComponentValueName(), null, null));
        assertEquals("test", //$NON-NLS-1$
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LOWER_CASE.getComponentValueName(), null, TEST_STR));
        assertEquals("test\ntest", AlgorithmSwitch //$NON-NLS-1$
                .getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LOWER_CASE.getComponentValueName(), null, "Test\ntest")); //$NON-NLS-1$
        assertEquals("test\ttest", AlgorithmSwitch //$NON-NLS-1$
                .getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LOWER_CASE.getComponentValueName(), null, "Test\ttest")); //$NON-NLS-1$
        assertEquals("test test123", AlgorithmSwitch //$NON-NLS-1$
                .getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LOWER_CASE.getComponentValueName(), null, "Test test123")); //$NON-NLS-1$
        assertEquals(BLANK_STR, AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LOWER_CASE.getComponentValueName(),
                null, BLANK_STR));
        assertEquals(SPACE_STR, AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LOWER_CASE.getComponentValueName(),
                null, SPACE_STR));
        assertEquals(NULL_STR,
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LOWER_CASE.getComponentValueName(), null, "Null")); //$NON-NLS-1$
        assertEquals(QUO_STR,
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.LOWER_CASE.getComponentValueName(), null, QUO_STR));

        // REMOVE_MARKS
        assertEquals(null,
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.REMOVE_MARKS.getComponentValueName(), null, null));
        assertEquals(TEST_STR, AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.REMOVE_MARKS.getComponentValueName(),
                null, TEST_STR));
        assertEquals(BLANK_STR, AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.REMOVE_MARKS.getComponentValueName(),
                null, BLANK_STR));
        assertEquals("1-2;40;0-5", AlgorithmSwitch //$NON-NLS-1$
                .getPreAlgoResult(BlockingKeyPreAlgorithmEnum.REMOVE_MARKS.getComponentValueName(), null, "1-2;40;0-5")); //$NON-NLS-1$
        assertEquals("Test test", AlgorithmSwitch //$NON-NLS-1$
                .getPreAlgoResult(BlockingKeyPreAlgorithmEnum.REMOVE_MARKS.getComponentValueName(), null, "Test test")); //$NON-NLS-1$
        assertEquals(SPACE_STR, AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.REMOVE_MARKS.getComponentValueName(),
                null, SPACE_STR));
        assertEquals(QUO_STR, AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.REMOVE_MARKS.getComponentValueName(),
                null, QUO_STR));

        // REMOVE_MARKS_THEN_LOWER_CASE
        assertEquals(null, AlgorithmSwitch
                .getPreAlgoResult(BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_LOWER_CASE.getComponentValueName(), null, null));
        assertEquals(TEST_STR, AlgorithmSwitch.getPreAlgoResult(
                BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_LOWER_CASE.getComponentValueName(), null, TEST_STR));
        assertEquals(BLANK_STR, AlgorithmSwitch.getPreAlgoResult(
                BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_LOWER_CASE.getComponentValueName(), null, BLANK_STR));
        assertEquals("testdtestm", AlgorithmSwitch.getPreAlgoResult( //$NON-NLS-1$
                BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_LOWER_CASE.getComponentValueName(), null, "TestDtestM")); //$NON-NLS-1$
        assertEquals("test test12", AlgorithmSwitch.getPreAlgoResult(
                BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_LOWER_CASE.getComponentValueName(), null, "Test test12")); //$NON-NLS-1$
        assertEquals(SPACE_STR, AlgorithmSwitch.getPreAlgoResult(
                BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_LOWER_CASE.getComponentValueName(), null, SPACE_STR));
        assertEquals(QUO_STR, AlgorithmSwitch.getPreAlgoResult(
                BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_LOWER_CASE.getComponentValueName(), null, QUO_STR));

        // REMOVE_MARKS_THEN_UPPER_CASE
        assertEquals(null, AlgorithmSwitch
                .getPreAlgoResult(BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_UPPER_CASE.getComponentValueName(), null, null));
        assertEquals("TEST", AlgorithmSwitch.getPreAlgoResult(
                BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_UPPER_CASE.getComponentValueName(), null, TEST_STR));
        assertEquals(BLANK_STR, AlgorithmSwitch.getPreAlgoResult(
                BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_UPPER_CASE.getComponentValueName(), null, BLANK_STR));
        assertEquals("TESTDTESTM1-2;40;0-5", AlgorithmSwitch.getPreAlgoResult(
                BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_UPPER_CASE.getComponentValueName(), null, "TestDtestM1-2;40;0-5")); //$NON-NLS-1$
        assertEquals("TESTDTESTMTEST TEST", AlgorithmSwitch.getPreAlgoResult(
                BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_UPPER_CASE.getComponentValueName(), null, "TestDtestMTest test")); //$NON-NLS-1$
        assertEquals(SPACE_STR, AlgorithmSwitch.getPreAlgoResult(
                BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_UPPER_CASE.getComponentValueName(), null, SPACE_STR));
        assertEquals(QUO_STR, AlgorithmSwitch.getPreAlgoResult(
                BlockingKeyPreAlgorithmEnum.REMOVE_MARKS_THEN_UPPER_CASE.getComponentValueName(), null, QUO_STR));

        // UPPER_CASE
        assertEquals(null,
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.UPPER_CASE.getComponentValueName(), null, null));
        assertEquals("TEST",
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.UPPER_CASE.getComponentValueName(), null, TEST_STR));
        assertEquals("TEST\nTEST", AlgorithmSwitch
                .getPreAlgoResult(BlockingKeyPreAlgorithmEnum.UPPER_CASE.getComponentValueName(), null, "Test\ntest")); //$NON-NLS-1$
        assertEquals("TEST\tTEST", AlgorithmSwitch
                .getPreAlgoResult(BlockingKeyPreAlgorithmEnum.UPPER_CASE.getComponentValueName(), null, "Test\ttest")); //$NON-NLS-1$
        assertEquals("TEST TEST12", AlgorithmSwitch
                .getPreAlgoResult(BlockingKeyPreAlgorithmEnum.UPPER_CASE.getComponentValueName(), null, "Test test12")); //$NON-NLS-1$
        assertEquals(BLANK_STR, AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.UPPER_CASE.getComponentValueName(),
                null, BLANK_STR));
        assertEquals(SPACE_STR, AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.UPPER_CASE.getComponentValueName(),
                null, SPACE_STR));
        assertEquals("NULL ",
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.UPPER_CASE.getComponentValueName(), null, "Null ")); //$NON-NLS-1$
        assertEquals(QUO_STR,
                AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.UPPER_CASE.getComponentValueName(), null, QUO_STR));

        AlgorithmSwitch.getPreAlgoResult(BlockingKeyPreAlgorithmEnum.UPPER_CASE.getComponentValueName(), null, QUO_STR);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.AlgorithmSwitch#getAlgoResult(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetAlgoResult() {
        // COLOGNEPHONETIC
        assertEquals(null,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.COLOGNEPHONETIC.getComponentValueName(), null, null));
        assertEquals("282", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.COLOGNEPHONETIC.getComponentValueName(), null, TEST_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.COLOGNEPHONETIC.getComponentValueName(), null, BLANK_STR));
        assertEquals("65", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.COLOGNEPHONETIC.getComponentValueName(), null, NULL_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.COLOGNEPHONETIC.getComponentValueName(), null, QUO_STR));

        // D_METAPHONE
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.D_METAPHONE.getComponentValueName(), null, null));
        assertEquals("TST", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.D_METAPHONE.getComponentValueName(), null, TEST_STR));
        assertEquals(null,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.D_METAPHONE.getComponentValueName(), null, BLANK_STR));
        assertEquals("NL", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.D_METAPHONE.getComponentValueName(), null, NULL_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.D_METAPHONE.getComponentValueName(), null, QUO_STR));

        // EXACT
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.EXACT.getComponentValueName(), null, null));
        assertEquals(TEST_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.EXACT.getComponentValueName(), null, TEST_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.EXACT.getComponentValueName(), null, BLANK_STR));
        assertEquals(NULL_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.EXACT.getComponentValueName(), null, NULL_STR));
        assertEquals(QUO_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.EXACT.getComponentValueName(), null, QUO_STR));
        // FINGERPRINTKEY
        assertEquals(null,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FINGERPRINTKEY.getComponentValueName(), null, null));
        assertEquals(TEST_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FINGERPRINTKEY.getComponentValueName(), null, TEST_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FINGERPRINTKEY.getComponentValueName(), null, BLANK_STR));
        assertEquals(NULL_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FINGERPRINTKEY.getComponentValueName(), null, NULL_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FINGERPRINTKEY.getComponentValueName(), null, QUO_STR));
        // FIRST_CHAR_EW
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_CHAR_EW.getComponentValueName(), null, null));
        assertEquals("t", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_CHAR_EW.getComponentValueName(), null, TEST_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_CHAR_EW.getComponentValueName(), null, BLANK_STR));
        assertEquals("n", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_CHAR_EW.getComponentValueName(), null, NULL_STR));
        assertEquals(QUO_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_CHAR_EW.getComponentValueName(), null, QUO_STR));

        // FIRST_CHAR_EW
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR.getComponentValueName(), "1", null)); //$NON-NLS-1$
        assertEquals(TEST_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR.getComponentValueName(), "4", TEST_STR)); //$NON-NLS-1$
        assertEquals(TEST_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR.getComponentValueName(), "5", TEST_STR));
        assertEquals(TEST_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR.getComponentValueName(), "-1", TEST_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR.getComponentValueName(), "1", BLANK_STR));
        assertEquals("n", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR.getComponentValueName(), "1", NULL_STR));
        assertEquals(QUO_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR.getComponentValueName(), "1", QUO_STR));

        // FIRST_N_CHAR_EW

        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR_EW.getComponentValueName(), "1", null));
        assertEquals(TEST_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR_EW.getComponentValueName(), "4", TEST_STR));
        assertEquals(TEST_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR_EW.getComponentValueName(), "5", TEST_STR));
        assertEquals("tt", AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR_EW.getComponentValueName(), "1", //$NON-NLS-1$//$NON-NLS-2$
                "test\ntest"));
        assertEquals("tete", AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR_EW.getComponentValueName(), "2", //$NON-NLS-1$//$NON-NLS-2$
                "test\ttest"));
        assertEquals("testes", AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR_EW.getComponentValueName(), //$NON-NLS-1$
                "3", "test\ftest")); //$NON-NLS-1$
        assertEquals("tete", AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR_EW.getComponentValueName(), "2", //$NON-NLS-1$//$NON-NLS-2$
                "test test"));
        assertEquals("testtest", AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR_EW.getComponentValueName(), //$NON-NLS-1$
                "4", "test\rtest")); //$NON-NLS-1$
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR_EW.getComponentValueName(), "1", BLANK_STR));
        assertEquals("n", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR_EW.getComponentValueName(), "1", NULL_STR));
        assertEquals(QUO_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CHAR_EW.getComponentValueName(), "1", QUO_STR));

        // FIRST_N_CONSONANTS
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CONSONANTS.getComponentValueName(), "1", null));
        assertEquals("tst", AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CONSONANTS.getComponentValueName(), //$NON-NLS-1$
                "2000", TEST_STR));
        assertEquals("t", AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CONSONANTS.getComponentValueName(), "1", //$NON-NLS-1$//$NON-NLS-2$
                "test\ntest"));
        assertEquals("ts", AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CONSONANTS.getComponentValueName(), "2", //$NON-NLS-1$//$NON-NLS-2$
                "test\ttest"));
        assertEquals("tstts", AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CONSONANTS.getComponentValueName(), //$NON-NLS-1$
                "5", "test test")); //$NON-NLS-1$
        assertEquals(BLANK_STR, AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CONSONANTS.getComponentValueName(),
                "1", BLANK_STR));
        assertEquals(BLANK_STR, AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CONSONANTS.getComponentValueName(),
                "1", SPACE_STR));
        assertEquals("n", AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CONSONANTS.getComponentValueName(), "1", //$NON-NLS-1$
                NULL_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_CONSONANTS.getComponentValueName(), "1", QUO_STR));

        // FIRST_N_VOWELS

        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_VOWELS.getComponentValueName(), "1", null));
        assertEquals("e", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_VOWELS.getComponentValueName(), "2000", TEST_STR));
        assertEquals("e", AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_VOWELS.getComponentValueName(), "1", //$NON-NLS-1$//$NON-NLS-2$
                "test\ntest"));
        assertEquals("ee", AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_VOWELS.getComponentValueName(), "2", //$NON-NLS-1$//$NON-NLS-2$
                "test\ttest"));
        assertEquals("ee", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_VOWELS.getComponentValueName(), "5", "test test")); //$NON-NLS-1$
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_VOWELS.getComponentValueName(), "1", BLANK_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_VOWELS.getComponentValueName(), "1", SPACE_STR));
        assertEquals("u", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_VOWELS.getComponentValueName(), "1", NULL_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.FIRST_N_VOWELS.getComponentValueName(), "1", QUO_STR));

        // LAST_N_CHAR

        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.LAST_N_CHAR.getComponentValueName(), "1", null));
        assertEquals("test", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.LAST_N_CHAR.getComponentValueName(), "2000", TEST_STR));
        assertEquals("t", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.LAST_N_CHAR.getComponentValueName(), "1", "test\ntest")); //$NON-NLS-1$
        assertEquals("st", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.LAST_N_CHAR.getComponentValueName(), "2", "test\ttest")); //$NON-NLS-1$
        assertEquals(" test", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.LAST_N_CHAR.getComponentValueName(), "5", "test test")); //$NON-NLS-1$
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.LAST_N_CHAR.getComponentValueName(), "1", BLANK_STR));
        assertEquals(SPACE_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.LAST_N_CHAR.getComponentValueName(), "1", SPACE_STR));
        assertEquals("l", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.LAST_N_CHAR.getComponentValueName(), "1", NULL_STR));
        assertEquals(QUO_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.LAST_N_CHAR.getComponentValueName(), "1", QUO_STR));

        // METAPHONE
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.METAPHONE.getComponentValueName(), null, null));
        assertEquals("TST", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.METAPHONE.getComponentValueName(), null, TEST_STR));
        assertEquals("TSTT", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.METAPHONE.getComponentValueName(), null, "Test\ntest")); //$NON-NLS-1$
        assertEquals("TSTT", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.METAPHONE.getComponentValueName(), null, "Test\ttest")); //$NON-NLS-1$
        assertEquals("TSTT", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.METAPHONE.getComponentValueName(), null, "Test test123t")); //$NON-NLS-1$
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.METAPHONE.getComponentValueName(), null, BLANK_STR));
        assertEquals(SPACE_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.METAPHONE.getComponentValueName(), null, SPACE_STR));
        assertEquals("NL", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.METAPHONE.getComponentValueName(), null, "Null")); //$NON-NLS-1$
        assertEquals(QUO_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.METAPHONE.getComponentValueName(), null, QUO_STR));

        // NGRAMKEY

        assertEquals(null, AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.NGRAMKEY.getComponentValueName(), null, null));
        assertEquals("esstte",
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.NGRAMKEY.getComponentValueName(), null, TEST_STR));
        assertEquals("essttett",
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.NGRAMKEY.getComponentValueName(), null, "Test\ntest"));
        assertEquals("essttett",
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.NGRAMKEY.getComponentValueName(), null, "Test\ttest"));
        assertEquals("122aabbcesstt1tett",
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.NGRAMKEY.getComponentValueName(), null, "Test test12abc"));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.NGRAMKEY.getComponentValueName(), null, BLANK_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.NGRAMKEY.getComponentValueName(), null, SPACE_STR));
        assertEquals("llnuul",
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.NGRAMKEY.getComponentValueName(), null, NULL_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.NGRAMKEY.getComponentValueName(), null, QUO_STR));

        // PICK_CHAR

        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.PICK_CHAR.getComponentValueName(), BLANK_STR, TEST_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.PICK_CHAR.getComponentValueName(), null, TEST_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.PICK_CHAR.getComponentValueName(), "test", BLANK_STR)); //$NON-NLS-1$
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.PICK_CHAR.getComponentValueName(), "test", null)); //$NON-NLS-1$
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.PICK_CHAR.getComponentValueName(), "test", TEST_STR)); //$NON-NLS-1$
        assertEquals("etest", AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.PICK_CHAR.getComponentValueName(), //$NON-NLS-1$
                "1-2;40;0-5", TEST_STR)); //$NON-NLS-1$
        assertEquals("e", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.PICK_CHAR.getComponentValueName(), "1-2", "Test test")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.PICK_CHAR.getComponentValueName(), BLANK_STR, SPACE_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.PICK_CHAR.getComponentValueName(), SPACE_STR, TEST_STR));
        AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.PICK_CHAR.getComponentValueName(), "1", QUO_STR); //$NON-NLS-1$

        // SOUNDEX
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.SOUNDEX.getComponentValueName(), null, null));
        assertEquals("T230", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.SOUNDEX.getComponentValueName(), null, TEST_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.SOUNDEX.getComponentValueName(), null, BLANK_STR));
        assertEquals("T232", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.SOUNDEX.getComponentValueName(), null, "Test test")); //$NON-NLS-1$
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.SOUNDEX.getComponentValueName(), null, SPACE_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.SOUNDEX.getComponentValueName(), null, QUO_STR));

        // SUBSTR
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.SUBSTR.getComponentValueName(), BLANK_STR, TEST_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.SUBSTR.getComponentValueName(), null, TEST_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.SUBSTR.getComponentValueName(), TEST_STR, BLANK_STR));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.SUBSTR.getComponentValueName(), TEST_STR, null));
        assertEquals(BLANK_STR,
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.SUBSTR.getComponentValueName(), SPACE_STR, TEST_STR));
        assertEquals("est", //$NON-NLS-1$
                AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.SUBSTR.getComponentValueName(), "1;100", TEST_STR)); //$NON-NLS-1$

        AlgorithmSwitch.getAlgoResult(BlockingKeyAlgorithmEnum.SUBSTR.getComponentValueName(), "1;100", TEST_STR);

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.AlgorithmSwitch#getPostAlgoResult(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetPostAlgoResult() {
        // USE_DEFAULT
        assertEquals(TEST_STR, AlgorithmSwitch.getPostAlgoResult(BlockingKeyPostAlgorithmEnum.USE_DEFAULT.getComponentValueName(),
                TEST_STR, BLANK_STR));
        assertEquals(SPACE_STR, AlgorithmSwitch
                .getPostAlgoResult(BlockingKeyPostAlgorithmEnum.USE_DEFAULT.getComponentValueName(), BLANK_STR, SPACE_STR));
        assertEquals(TEST_STR, AlgorithmSwitch.getPostAlgoResult(BlockingKeyPostAlgorithmEnum.USE_DEFAULT.getComponentValueName(),
                TEST_STR, null));
        assertEquals(TEST_STR, AlgorithmSwitch.getPostAlgoResult(BlockingKeyPostAlgorithmEnum.USE_DEFAULT.getComponentValueName(),
                SPACE_STR, TEST_STR));
        assertEquals("Test test12", AlgorithmSwitch //$NON-NLS-1$
                .getPostAlgoResult(BlockingKeyPostAlgorithmEnum.USE_DEFAULT.getComponentValueName(), NULL_STR, "Test test12")); //$NON-NLS-1$
    }

}
