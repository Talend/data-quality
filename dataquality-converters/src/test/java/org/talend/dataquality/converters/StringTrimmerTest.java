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

import org.junit.Test;

/**
 * Test for class {@link StringTrimmer}.
 * 
 * @author msjian
 * @version 2017.02.08
 */
public class StringTrimmerTest {

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
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading("\t" + expected, "\t")); //$NON-NLS-1$ //$NON-NLS-2$ 
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading(expected + "\t", "\t")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading('\u0009' + expected, "\t")); //$NON-NLS-1$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading('\u0009' + expected, '\u0009' + "")); //$NON-NLS-1$
        assertEquals(expected, stringTrimmer.removeTrailingAndLeading('\u0009' + expected + '\u0009' + '\u0009', "\t")); //$NON-NLS-1$

        assertEquals("abc ", stringTrimmer.removeTrailingAndLeading("\t" + "abc ", "\t")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertEquals("a" + "\t" + "bc", stringTrimmer.removeTrailingAndLeading("\t" + "a" + "\t" + "bc", "\t")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        assertEquals("\t" + expected, stringTrimmer.removeTrailingAndLeading("\t" + "abc ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        assertEquals(expected, ("\t" + "abc ").trim()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3

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
        for (String removechar : stringTrimmer.WHITESPACE_CHARS) {
            inputData = inputData + removechar;
        }
        assertEquals(expected, stringTrimmer.removeTrailingAndLeadingWhitespaces(inputData));
    }

}
