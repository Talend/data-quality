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
package org.talend.dataquality.converters;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link StringTrimmer}.
 *
 * @author msjian
 * @version 2017.02.08
 */
public class StringTrimmerTest {

    public String[] WHITESPACE_CHARS = new String[] { "\t" // CHARACTER TABULATION //$NON-NLS-1$
            , "\n" // LINE FEED (LF) //$NON-NLS-1$
            , '\u000B' + "" // LINE TABULATION //$NON-NLS-1$
            , "\f" // FORM FEED (FF) //$NON-NLS-1$
            , "\r" // CARRIAGE RETURN (CR) //$NON-NLS-1$
            , " " // SPACE //$NON-NLS-1$
            , '\u0085' + "" // NEXT LINE (NEL) //$NON-NLS-1$
            , '\u00A0' + "" // NO-BREAK SPACE //$NON-NLS-1$
            , '\u1680' + "" // OGHAM SPACE MARK //$NON-NLS-1$
            , '\u180E' + "" // MONGOLIAN VOWEL SEPARATOR //$NON-NLS-1$
            , '\u2000' + "" // EN QUAD //$NON-NLS-1$
            , '\u2001' + "" // EM QUAD //$NON-NLS-1$
            , '\u2002' + "" // EN SPACE //$NON-NLS-1$
            , '\u2003' + "" // EM SPACE //$NON-NLS-1$
            , '\u2004' + "" // THREE-PER-EM SPACE //$NON-NLS-1$
            , '\u2005' + "" // FOUR-PER-EM SPACE //$NON-NLS-1$
            , '\u2006' + "" // SIX-PER-EM SPACE //$NON-NLS-1$
            , '\u2007' + "" // FIGURE SPACE //$NON-NLS-1$
            , '\u2008' + "" // PUNCTUATION SPACE //$NON-NLS-1$
            , '\u2009' + "" // THIN SPACE //$NON-NLS-1$
            , '\u200A' + "" // HAIR SPACE //$NON-NLS-1$
            , '\u2028' + "" // LINE SEPARATOR //$NON-NLS-1$
            , '\u2029' + "" // PARAGRAPH SEPARATOR //$NON-NLS-1$
            , '\u202F' + "" // NARROW NO-BREAK SPACE //$NON-NLS-1$
            , '\u205F' + "" // MEDIUM MATHEMATICAL SPACE //$NON-NLS-1$
            , '\u3000' + "" // IDEOGRAPHIC SPACE //$NON-NLS-1$
    };

    private static final String expected = "abc"; //$NON-NLS-1$

    @Test
    public void testRemoveTrailingAndLeading() {
        /** Don't remove these commented lines after discussion with Jian */
        // assertEquals(expected, " abc ".trim()); //$NON-NLS-1$
        // assertEquals(" ", '\u0020' + "");
        // assertEquals('\t', '\u0009');
        // assertEquals("\t", '\u0009' + "");
        // assertEquals("a" + "\t", "a" + '\u0009');
        // assertNotEquals("\t", '\u0009');
        // assertNotEquals("\t", "\\u0009");

        StringTrimmer stringTrimmer = new StringTrimmer();

        // test for default character (whitespace)
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading(expected));
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading(" abc")); //$NON-NLS-1$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading(" abc ")); //$NON-NLS-1$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading(" abc  ")); //$NON-NLS-1$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading("  abc ")); //$NON-NLS-1$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading("  abc  ")); //$NON-NLS-1$
        assertEquals("ab c", stringTrimmer.removeTrailingAndLeading(" ab c")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("a b c", stringTrimmer.removeTrailingAndLeading(" a b c ")); //$NON-NLS-1$ //$NON-NLS-2$

        // test for other characters
        assertEquals("", stringTrimmer.removeTrailingAndLeading("aaa", "a")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", stringTrimmer.removeTrailingAndLeading("\t", "\t")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading("\t" + expected, "\t")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading(expected + "\t", "\t")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading('\u0009' + expected, "\t")); //$NON-NLS-1$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading('\u0009' + expected, '\u0009' + "")); //$NON-NLS-1$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading('\u0009' + expected + '\u0009' + '\u0009', "\t")); //$NON-NLS-1$

        assertEquals("abc ", stringTrimmer.removeTrailingAndLeading("\t" + "abc ", "\t")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertEquals("a" + "\t" + "bc", stringTrimmer.removeTrailingAndLeading("\t" + "a" + "\t" + "bc", "\t")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        assertEquals("\t" + expected, stringTrimmer.removeTrailingAndLeading("\t" + "abc ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(expected, ("\t" + "abc ").trim()); //$NON-NLS-1$ //$NON-NLS-2$
        // //$NON-NLS-3

        assertEquals(expected, stringTrimmer.removeTrailingAndLeading("\n" + expected, "\n")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("abc ", stringTrimmer.removeTrailingAndLeading("\n" + "abc ", "\n")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        assertEquals(expected, stringTrimmer.removeTrailingAndLeading(expected, "\r")); //$NON-NLS-1$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading("\r" + expected, "\r")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading("\r" + expected + "\r", "\r")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("abc ", stringTrimmer.removeTrailingAndLeading("\r" + "abc ", "\r")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertEquals("abc ", stringTrimmer.removeTrailingAndLeading("\r" + "abc " + "\r", "\r")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

        assertEquals("bc", stringTrimmer.removeTrailingAndLeading(" abc", " a")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(" a", stringTrimmer.removeTrailingAndLeading(" abc", "bc")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("ab", stringTrimmer.removeTrailingAndLeading("cabc", "c")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Test
    public void testRemoveTrailingAndLeadingWhitespaces() {
        StringTrimmer stringTrimmer = new StringTrimmer();
        String inputData = " " + expected; //$NON-NLS-1$
        for (String removechar : WHITESPACE_CHARS) {
            inputData = inputData + removechar;
        }
        assertEquals(expected, stringTrimmer.removeTrailingAndLeadingWhitespaces(inputData));
        inputData = ""; //$NON-NLS-1$
        for (String removechar : WHITESPACE_CHARS) {
            inputData = inputData + removechar;
        }
        inputData = inputData + expected + " ";
        assertEquals(expected, stringTrimmer.removeTrailingAndLeadingWhitespaces(inputData));
        assertEquals("", stringTrimmer.removeTrailingAndLeadingWhitespaces("  ")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("a", stringTrimmer.removeTrailingAndLeadingWhitespaces(" a ")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("a", stringTrimmer.removeTrailingAndLeadingWhitespaces("a ")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("a", stringTrimmer.removeTrailingAndLeadingWhitespaces(" a")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("a", stringTrimmer.removeTrailingAndLeadingWhitespaces("   a   ")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testRemoveTrailingAndLeadingWhitespacesOptimization() {
        int nbTests = 10000;
        for (int maxNbSpace = 1; maxNbSpace < 10; maxNbSpace += 3) {
            System.out.print("Test with max Nb Space = " + maxNbSpace + "\t");
            String[] ex = generateExamples(nbTests, maxNbSpace);
            for (int i = 0; i < 2; i++) { // first run is taking a long time =>
                                              // run twice at least
                runPerfTests(ex, i);
            }
        }

    }

    private void runPerfTests(String[] ex, int runNumber) {
        // call string trimmer
        StringTrimmer stringTrimmer = new StringTrimmer();
        long start = System.currentTimeMillis();
        for (int i = 0; i < ex.length; i++) {
            stringTrimmer.removeTrailingAndLeadingWhitespaces(ex[i]);
        }
        long duration1 = System.currentTimeMillis() - start;
        if (runNumber == 1) // only display second run results (first run is
                                // taking too much time to start
            System.out.print("New trimmer implementation: " + duration1 + "\t");

        // call original implementation
        start = System.currentTimeMillis();
        for (int i = 0; i < ex.length; i++) {
            originalRemoveTrailingAndLeadingWhitespaces(ex[i]);
        }
        long duration2 = System.currentTimeMillis() - start;
        if (runNumber == 1) {
            System.out.println("Original trimmer implementation: " + duration2);
            Assert.assertTrue("New StringTrimmer implementation should be faster than original implemenation. Run: " + runNumber,
                    duration1 <= (duration2 + 10));
        }
    }

    private String[] generateExamples(int n, int maxNbSpace) {
        List<String> examples = new ArrayList<String>();
        Random rng = new Random();

        for (int i = 0; i < n; i++) {
            StringBuilder base = new StringBuilder("mdjqfdsqjpoierupoezflmdjdfsdvxcsdhnnkqfklm");
            for (int j = 0; j < maxNbSpace; j++) {
                // prepend spaces before base string
                char[] chars = new char[rng.nextInt(maxNbSpace)];
                Arrays.fill(chars, ' ');
                base.insert(0, chars);
                // append spaces after base string
                chars = new char[rng.nextInt(maxNbSpace)];
                Arrays.fill(chars, ' ');
                base.append(chars);
            }
            examples.add(base.toString());
        }
        return examples.toArray(new String[examples.size()]);
    }

    private String originalRemoveTrailingAndLeadingWhitespaces(String inputStr) {
        if (StringUtils.isEmpty(inputStr)) {
            return inputStr;
        }

        String result = inputStr; // original
        // String result = inputStr.trim(); // pull request from davkrause
        while (StringUtils.startsWithAny(result, WHITESPACE_CHARS)) {
            result = StringUtils.removeStart(result, result.substring(0, 1));
        }

        while (StringUtils.endsWithAny(result, WHITESPACE_CHARS)) {
            result = StringUtils.removeEnd(result, result.substring(result.length() - 1, result.length()));
        }
        return result;
    }
}
