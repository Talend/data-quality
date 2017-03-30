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

        StringTrimmer stringConverter = new StringTrimmer();

        // test for default character (whitespace)
        assertEquals(expected, stringConverter.removeTrailingAndLeading(expected));
        assertEquals(expected, stringConverter.removeTrailingAndLeading(" abc")); //$NON-NLS-1$
        assertEquals(expected, stringConverter.removeTrailingAndLeading(" abc ")); //$NON-NLS-1$
        assertEquals(expected, stringConverter.removeTrailingAndLeading(" abc  ")); //$NON-NLS-1$
        assertEquals(expected, stringConverter.removeTrailingAndLeading("  abc ")); //$NON-NLS-1$
        assertEquals(expected, stringConverter.removeTrailingAndLeading("  abc  ")); //$NON-NLS-1$
        assertEquals("ab c", stringConverter.removeTrailingAndLeading(" ab c")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("a b c", stringConverter.removeTrailingAndLeading(" a b c ")); //$NON-NLS-1$ //$NON-NLS-2$

        // test for other characters
        assertEquals(expected, stringConverter.removeTrailingAndLeading("\t" + expected, "\t")); //$NON-NLS-1$ //$NON-NLS-2$ 
        assertEquals(expected, stringConverter.removeTrailingAndLeading(expected + "\t", "\t")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(expected, stringConverter.removeTrailingAndLeading('\u0009' + expected, "\t")); //$NON-NLS-1$
        assertEquals(expected, stringConverter.removeTrailingAndLeading('\u0009' + expected, '\u0009' + "")); //$NON-NLS-1$
        assertEquals(expected, stringConverter.removeTrailingAndLeading('\u0009' + expected + '\u0009' + '\u0009', "\t")); //$NON-NLS-1$

        assertEquals("abc ", stringConverter.removeTrailingAndLeading("\t" + "abc ", "\t")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertEquals("a" + "\t" + "bc", stringConverter.removeTrailingAndLeading("\t" + "a" + "\t" + "bc", "\t")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        assertEquals("\t" + expected, stringConverter.removeTrailingAndLeading("\t" + "abc ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        assertEquals(expected, ("\t" + "abc ").trim()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3

        assertEquals(expected, stringConverter.removeTrailingAndLeading("\n" + expected, "\n")); //$NON-NLS-1$ //$NON-NLS-2$ 
        assertEquals("abc ", stringConverter.removeTrailingAndLeading("\n" + "abc ", "\n")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        assertEquals(expected, stringConverter.removeTrailingAndLeading(expected, "\r")); //$NON-NLS-1$
        assertEquals(expected, stringConverter.removeTrailingAndLeading("\r" + expected, "\r")); //$NON-NLS-1$ //$NON-NLS-2$ 
        assertEquals(expected, stringConverter.removeTrailingAndLeading("\r" + expected + "\r", "\r")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        assertEquals("abc ", stringConverter.removeTrailingAndLeading("\r" + "abc ", "\r")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertEquals("abc ", stringConverter.removeTrailingAndLeading("\r" + "abc " + "\r", "\r")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

        assertEquals("bc", stringConverter.removeTrailingAndLeading(" abc", " a")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(" a", stringConverter.removeTrailingAndLeading(" abc", "bc")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("ab", stringConverter.removeTrailingAndLeading("cabc", "c")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Test
    public void testRemoveTrailingAndLeadingWhitespaces() {
        StringTrimmer stringConverter = new StringTrimmer();
        String inputData = " " + expected; //$NON-NLS-1$
        for (String removechar : stringConverter.WHITESPACE_CHARS) {
            inputData = inputData + removechar;
        }
        assertEquals(expected, stringConverter.removeTrailingAndLeadingWhitespaces(inputData));
    }

}
