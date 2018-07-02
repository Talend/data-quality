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
package org.talend.windowkey;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test for class AlgoBox, created by msjian.
 * 
 */
public class AlgoBoxTest {

    private static final String QUO_STR = "\""; //$NON-NLS-1$

    private static final String SPACE_STR = " "; //$NON-NLS-1$

    private static final String NULL_STR = "null"; //$NON-NLS-1$

    private static final String BLANK_STR = ""; //$NON-NLS-1$

    private static final String TEST_STR = "test"; //$NON-NLS-1$

    private final String MIXTD_SURROGATEPAIR = "𠀀𠀐我𠀑ab"; //$NON-NLS-1$

    /**
     * Test method for {@link org.talend.windowkey#add_Left_Char(String, String)}
     */
    @Test
    public void testAdd_Left_Char() {
        assertEquals("<test", AlgoBox.add_Left_Char(TEST_STR, "<")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(TEST_STR, AlgoBox.add_Left_Char(TEST_STR, null));
        assertEquals(NULL_STR, AlgoBox.add_Left_Char(NULL_STR, BLANK_STR));
        assertEquals("\\test", AlgoBox.add_Left_Char(TEST_STR, "\\")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(QUO_STR, AlgoBox.add_Left_Char(null, QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#add_Right_Char(String, String)}
     */
    @Test
    public void testAdd_Right_Char() {
        assertEquals("test<", AlgoBox.add_Right_Char(TEST_STR, "<")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(TEST_STR, AlgoBox.add_Right_Char(TEST_STR, null));
        assertEquals(NULL_STR, AlgoBox.add_Right_Char(NULL_STR, BLANK_STR));
        assertEquals("test\\", AlgoBox.add_Right_Char(TEST_STR, "\\")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(QUO_STR, AlgoBox.add_Right_Char(null, QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#colognePhonetic(String)}
     */
    @Test
    public void testColognePhonetic() {
        assertEquals(null, AlgoBox.colognePhonetic(null));
        assertEquals("282", AlgoBox.colognePhonetic(TEST_STR)); //$NON-NLS-1$
        assertEquals(BLANK_STR, AlgoBox.colognePhonetic(BLANK_STR));
        assertEquals("65", AlgoBox.colognePhonetic(NULL_STR)); //$NON-NLS-1$
        assertEquals(BLANK_STR, AlgoBox.colognePhonetic(QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#doublemetaphone(String)}
     */
    @Test
    public void testDoublemetaphone() {
        assertEquals(BLANK_STR, AlgoBox.doublemetaphone(null));
        assertEquals("TST", AlgoBox.doublemetaphone(TEST_STR)); //$NON-NLS-1$
        assertEquals(null, AlgoBox.doublemetaphone(BLANK_STR));
        assertEquals("NL", AlgoBox.doublemetaphone(NULL_STR)); //$NON-NLS-1$
        assertEquals(BLANK_STR, AlgoBox.doublemetaphone(QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#exact(String)}
     */
    @Test
    public void testExact() {
        assertEquals(BLANK_STR, AlgoBox.exact(null));
        assertEquals(TEST_STR, AlgoBox.exact(TEST_STR));
        assertEquals(BLANK_STR, AlgoBox.exact(BLANK_STR));
        assertEquals(NULL_STR, AlgoBox.exact(NULL_STR));
        assertEquals(QUO_STR, AlgoBox.exact(QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#fingerPrintKey(String)}
     */
    @Test
    public void testFingerPrintKey() {
        assertEquals(null, AlgoBox.fingerPrintKey(null));
        assertEquals(TEST_STR, AlgoBox.fingerPrintKey(TEST_STR));
        assertEquals(BLANK_STR, AlgoBox.fingerPrintKey(BLANK_STR));
        assertEquals(NULL_STR, AlgoBox.fingerPrintKey(NULL_STR));
        assertEquals(BLANK_STR, AlgoBox.fingerPrintKey(QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#first_Char_EW(String)}
     */
    @Test
    public void testFirst_Char_EW() {
        assertEquals(BLANK_STR, AlgoBox.first_Char_EW(null));
        assertEquals("t", AlgoBox.first_Char_EW(TEST_STR)); //$NON-NLS-1$
        assertEquals(BLANK_STR, AlgoBox.first_Char_EW(BLANK_STR));
        assertEquals("n", AlgoBox.first_Char_EW(NULL_STR)); //$NON-NLS-1$
        assertEquals(QUO_STR, AlgoBox.first_Char_EW(QUO_STR));
        // TDQ-15079: Support Chinese　and surrogate pair characters
        assertEquals("拓", AlgoBox.first_Char_EW("拓蓝科技")); //$NON-NLS-1$//$NON-NLS-2$
        assertEquals("𠀀", AlgoBox.first_Char_EW(MIXTD_SURROGATEPAIR)); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.windowkey#first_N_Char(String, int)}
     */
    @Test
    public void testFirst_N_Char() {
        assertEquals(BLANK_STR, AlgoBox.first_N_Char(null, 1));
        assertEquals(TEST_STR, AlgoBox.first_N_Char(TEST_STR, 4));
        assertEquals(TEST_STR, AlgoBox.first_N_Char(TEST_STR, 5));
        assertEquals(TEST_STR, AlgoBox.first_N_Char(TEST_STR, -1));
        assertEquals(BLANK_STR, AlgoBox.first_N_Char(BLANK_STR, 1));
        assertEquals("n", AlgoBox.first_N_Char(NULL_STR, 1)); //$NON-NLS-1$
        assertEquals(QUO_STR, AlgoBox.first_N_Char(QUO_STR, 1));
        assertEquals(MIXTD_SURROGATEPAIR, AlgoBox.first_N_Char(MIXTD_SURROGATEPAIR, 10));
        assertEquals("𠀀𠀐", AlgoBox.first_N_Char(MIXTD_SURROGATEPAIR, 2)); //$NON-NLS-1$
        assertEquals("𠀀𠀐我𠀑a", AlgoBox.first_N_Char(MIXTD_SURROGATEPAIR, 5)); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.windowkey#first_N_Char_EW(String, int)}
     */
    @Test
    public void testFirst_N_Char_EW() {
        assertEquals(BLANK_STR, AlgoBox.first_N_Char_EW(null, 1));
        assertEquals(TEST_STR, AlgoBox.first_N_Char_EW(TEST_STR, 4));
        assertEquals(TEST_STR, AlgoBox.first_N_Char_EW(TEST_STR, 5));
        assertEquals("tt", AlgoBox.first_N_Char_EW("test\ntest", 1)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("tete", AlgoBox.first_N_Char_EW("test\ttest", 2)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("testes", AlgoBox.first_N_Char_EW("test\ftest", 3)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("tete", AlgoBox.first_N_Char_EW("test test", 2)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("testtest", AlgoBox.first_N_Char_EW("test\rtest", 4)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BLANK_STR, AlgoBox.first_N_Char_EW(BLANK_STR, 1));
        assertEquals("n", AlgoBox.first_N_Char_EW(NULL_STR, 1)); //$NON-NLS-1$
        assertEquals(QUO_STR, AlgoBox.first_N_Char_EW(QUO_STR, 1));
        // TDQ-15079: Support Chinese　and surrogate pair characters
        assertEquals("拓科", AlgoBox.first_N_Char_EW("拓蓝\r科技", 1)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("𠀀𠀐𠀑a", AlgoBox.first_N_Char_EW("𠀀𠀐我\t𠀑ab", 2)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for {@link org.talend.windowkey#first_N_Consonants(String, int)}
     */
    @Test
    public void testFirst_N_Consonants() {
        assertEquals(BLANK_STR, AlgoBox.first_N_Consonants(null, 1));
        assertEquals("tst", AlgoBox.first_N_Consonants(TEST_STR, 2000)); //$NON-NLS-1$
        assertEquals("t", AlgoBox.first_N_Consonants("test\ntest", 1)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("ts", AlgoBox.first_N_Consonants("test\ttest", 2)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("tstts", AlgoBox.first_N_Consonants("test test", 5)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BLANK_STR, AlgoBox.first_N_Consonants(BLANK_STR, 1));
        assertEquals(BLANK_STR, AlgoBox.first_N_Consonants(SPACE_STR, 1));
        assertEquals("n", AlgoBox.first_N_Consonants(NULL_STR, 1)); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.windowkey#first_N_Vowels(String, int)}
     */
    @Test
    public void testFirst_N_Vowels() {
        assertEquals(BLANK_STR, AlgoBox.first_N_Vowels(null, 1));
        assertEquals("e", AlgoBox.first_N_Vowels(TEST_STR, 2000)); //$NON-NLS-1$
        assertEquals("e", AlgoBox.first_N_Vowels("test\ntest", 1)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("ee", AlgoBox.first_N_Vowels("test\ttest", 2)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("ee", AlgoBox.first_N_Vowels("test test", 5)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BLANK_STR, AlgoBox.first_N_Vowels(BLANK_STR, 1));
        assertEquals(BLANK_STR, AlgoBox.first_N_Vowels(SPACE_STR, 1));
        assertEquals("u", AlgoBox.first_N_Vowels(NULL_STR, 1)); //$NON-NLS-1$
        assertEquals(BLANK_STR, AlgoBox.first_N_Vowels(QUO_STR, 1));
    }

    /**
     * Test method for {@link org.talend.windowkey#last_N_Char(String, int)}
     */
    @Test
    public void testLast_N_Char() {
        assertEquals(BLANK_STR, AlgoBox.last_N_Char(null, 1));
        assertEquals("test", AlgoBox.last_N_Char(TEST_STR, 2000)); //$NON-NLS-1$
        assertEquals("t", AlgoBox.last_N_Char("test\ntest", 1)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("st", AlgoBox.last_N_Char("test\ttest", 2)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(" test", AlgoBox.last_N_Char("test test", 5)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BLANK_STR, AlgoBox.last_N_Char(BLANK_STR, 1));
        assertEquals(SPACE_STR, AlgoBox.last_N_Char(SPACE_STR, 1));
        assertEquals("l", AlgoBox.last_N_Char(NULL_STR, 1)); //$NON-NLS-1$
        assertEquals(QUO_STR, AlgoBox.last_N_Char(QUO_STR, 1));
        assertEquals(QUO_STR, AlgoBox.last_N_Char(QUO_STR, 1));
        // TDQ-15079: Support Chinese　and surrogate pair characters
        assertEquals(MIXTD_SURROGATEPAIR, AlgoBox.last_N_Char(MIXTD_SURROGATEPAIR, 100)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("𠀑ab", AlgoBox.last_N_Char(MIXTD_SURROGATEPAIR, 3)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("𠀐我𠀑ab", AlgoBox.last_N_Char(MIXTD_SURROGATEPAIR, 5)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for {@link org.talend.windowkey#lowerCase(String)}
     */
    @Test
    public void testLowerCase() {
        assertEquals(null, AlgoBox.lowerCase(null));
        assertEquals("test", AlgoBox.lowerCase(TEST_STR)); //$NON-NLS-1$
        assertEquals("test\ntest", AlgoBox.lowerCase("Test\ntest")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("test\ttest", AlgoBox.lowerCase("Test\ttest")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("test test123", AlgoBox.lowerCase("Test test123")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BLANK_STR, AlgoBox.lowerCase(BLANK_STR));
        assertEquals(SPACE_STR, AlgoBox.lowerCase(SPACE_STR));
        assertEquals(NULL_STR, AlgoBox.lowerCase("Null")); //$NON-NLS-1$
        assertEquals(QUO_STR, AlgoBox.lowerCase(QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#metaphone(String)}
     */
    @Test
    public void testMetaphone() {
        assertEquals(BLANK_STR, AlgoBox.metaphone(null));
        assertEquals("TST", AlgoBox.metaphone(TEST_STR)); //$NON-NLS-1$
        assertEquals("TSTT", AlgoBox.metaphone("Test\ntest")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("TSTT", AlgoBox.metaphone("Test\ttest")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("TSTT", AlgoBox.metaphone("Test test123t")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BLANK_STR, AlgoBox.metaphone(BLANK_STR));
        assertEquals(SPACE_STR, AlgoBox.metaphone(SPACE_STR));
        assertEquals("NL", AlgoBox.metaphone("Null")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(QUO_STR, AlgoBox.metaphone(QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#nGramKey(String)}
     */
    @SuppressWarnings("nls")
    @Test
    public void testNGramKey() {
        assertEquals(null, AlgoBox.nGramKey(null));
        assertEquals("esstte", AlgoBox.nGramKey(TEST_STR)); //$NON-NLS-1$
        assertEquals("essttett", AlgoBox.nGramKey("Test\ntest")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("essttett", AlgoBox.nGramKey("Test\ttest")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("122aabbcesstt1tett", AlgoBox.nGramKey("Test test12abc")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BLANK_STR, AlgoBox.nGramKey(BLANK_STR));
        assertEquals(BLANK_STR, AlgoBox.nGramKey(SPACE_STR));
        assertEquals("llnuul", AlgoBox.nGramKey(NULL_STR)); //$NON-NLS-1$
        assertEquals(BLANK_STR, AlgoBox.nGramKey(QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#pick_Char(String, String)}
     */
    @Test
    public void testPick_Char() {
        assertEquals(BLANK_STR, AlgoBox.pick_Char(TEST_STR, BLANK_STR));
        assertEquals(BLANK_STR, AlgoBox.pick_Char(TEST_STR, null));
        assertEquals(BLANK_STR, AlgoBox.pick_Char(BLANK_STR, "test")); //$NON-NLS-1$
        assertEquals(BLANK_STR, AlgoBox.pick_Char(null, "test")); //$NON-NLS-1$
        assertEquals(BLANK_STR, AlgoBox.pick_Char(TEST_STR, "test")); //$NON-NLS-1$
        assertEquals("bcdf", AlgoBox.pick_Char("abcdef", "1;2-4;5")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("etest", AlgoBox.pick_Char(TEST_STR, "1-2;40;0-5")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("e", AlgoBox.pick_Char("Test test", "1-2")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(BLANK_STR, AlgoBox.pick_Char(SPACE_STR, BLANK_STR));
        assertEquals(BLANK_STR, AlgoBox.pick_Char(TEST_STR, SPACE_STR));
        assertEquals(BLANK_STR, AlgoBox.pick_Char(QUO_STR, "1")); //$NON-NLS-1$
        // TDQ-15079: Support Chinese　and surrogate pair characters
        assertEquals(BLANK_STR, AlgoBox.pick_Char(MIXTD_SURROGATEPAIR, BLANK_STR));
        assertEquals("𠀐", AlgoBox.pick_Char(MIXTD_SURROGATEPAIR, "1")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("我", AlgoBox.pick_Char(MIXTD_SURROGATEPAIR, "2")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("𠀑", AlgoBox.pick_Char(MIXTD_SURROGATEPAIR, "3")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("a", AlgoBox.pick_Char(MIXTD_SURROGATEPAIR, "4")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for {@link org.talend.windowkey#removeDiacriticalMarks(String)}
     */
    @Test
    public void testRemoveDiacriticalMarks() {
        assertEquals(null, AlgoBox.removeDiacriticalMarks(null));
        assertEquals(TEST_STR, AlgoBox.removeDiacriticalMarks(TEST_STR));
        assertEquals(BLANK_STR, AlgoBox.removeDiacriticalMarks(BLANK_STR));
        assertEquals("1-2;40;0-5", AlgoBox.removeDiacriticalMarks("1-2;40;0-5")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Test test", AlgoBox.removeDiacriticalMarks("Test test")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SPACE_STR, AlgoBox.removeDiacriticalMarks(SPACE_STR));
        assertEquals(QUO_STR, AlgoBox.removeDiacriticalMarks(QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#removeDMAndLowerCase(String)}
     */
    @Test
    public void testRemoveDMAndLowerCase() {
        assertEquals(null, AlgoBox.removeDMAndLowerCase(null));
        assertEquals(TEST_STR, AlgoBox.removeDMAndLowerCase(TEST_STR));
        assertEquals(BLANK_STR, AlgoBox.removeDMAndLowerCase(BLANK_STR));
        assertEquals("testdtestm", AlgoBox.removeDMAndLowerCase("TestDtestM")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("test test12", AlgoBox.removeDMAndLowerCase("Test test12")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SPACE_STR, AlgoBox.removeDMAndLowerCase(SPACE_STR));
        assertEquals(QUO_STR, AlgoBox.removeDMAndLowerCase(QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#removeDMAndUpperCase(String)}
     */
    @Test
    public void testRemoveDMAndUpperCase() {
        assertEquals(null, AlgoBox.removeDMAndUpperCase(null));
        assertEquals("TEST", AlgoBox.removeDMAndUpperCase(TEST_STR)); //$NON-NLS-1$
        assertEquals(BLANK_STR, AlgoBox.removeDMAndUpperCase(BLANK_STR));
        assertEquals("TESTDTESTM1-2;40;0-5", AlgoBox.removeDMAndUpperCase("TestDtestM1-2;40;0-5")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("TESTDTESTMTEST TEST", AlgoBox.removeDMAndUpperCase("TestDtestMTest test")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(SPACE_STR, AlgoBox.removeDMAndUpperCase(SPACE_STR));
        assertEquals(QUO_STR, AlgoBox.removeDMAndUpperCase(QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#soundex(String)}
     */
    @Test
    public void testSoundex() {
        assertEquals(BLANK_STR, AlgoBox.soundex(null));
        assertEquals("T230", AlgoBox.soundex(TEST_STR)); //$NON-NLS-1$
        assertEquals(BLANK_STR, AlgoBox.soundex(BLANK_STR));
        assertEquals("T232", AlgoBox.soundex("Test test")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BLANK_STR, AlgoBox.soundex(SPACE_STR));
        assertEquals(BLANK_STR, AlgoBox.soundex(QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#subStr(String, String)}
     */
    @Test
    public void testSubStr() {
        assertEquals(BLANK_STR, AlgoBox.subStr(TEST_STR, BLANK_STR));
        assertEquals(BLANK_STR, AlgoBox.subStr(TEST_STR, null));
        assertEquals(BLANK_STR, AlgoBox.subStr(BLANK_STR, TEST_STR));
        assertEquals(BLANK_STR, AlgoBox.subStr(null, TEST_STR));
        assertEquals(BLANK_STR, AlgoBox.subStr(TEST_STR, SPACE_STR));
        assertEquals("est", AlgoBox.subStr(TEST_STR, "1;100")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("cd", AlgoBox.subStr("abcdef", "2;4")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Test
    public void testSubStrSurrogatePair() {
        assertEquals("𠀐我", AlgoBox.subStr(MIXTD_SURROGATEPAIR, "1;3")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("我𠀑a", AlgoBox.subStr(MIXTD_SURROGATEPAIR, "2;5")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for {@link org.talend.windowkey#upperCase(String)}
     */
    @Test
    public void testUpperCase() {
        assertEquals(null, AlgoBox.upperCase(null));
        assertEquals("TEST", AlgoBox.upperCase(TEST_STR)); //$NON-NLS-1$
        assertEquals("TEST\nTEST", AlgoBox.upperCase("Test\ntest")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("TEST\tTEST", AlgoBox.upperCase("Test\ttest")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("TEST TEST12", AlgoBox.upperCase("Test test12")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BLANK_STR, AlgoBox.upperCase(BLANK_STR));
        assertEquals(SPACE_STR, AlgoBox.upperCase(SPACE_STR));
        assertEquals("NULL ", AlgoBox.upperCase("Null ")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(QUO_STR, AlgoBox.upperCase(QUO_STR));
    }

    /**
     * Test method for {@link org.talend.windowkey#useDefault(String, String)}
     */
    @Test
    public void testUseDefault() {
        assertEquals(TEST_STR, AlgoBox.useDefault(BLANK_STR, TEST_STR));
        assertEquals(SPACE_STR, AlgoBox.useDefault(SPACE_STR, BLANK_STR));
        assertEquals(TEST_STR, AlgoBox.useDefault(null, TEST_STR));
        assertEquals(TEST_STR, AlgoBox.useDefault(TEST_STR, SPACE_STR));
        assertEquals("Test test12", AlgoBox.useDefault("Test test12", NULL_STR)); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
