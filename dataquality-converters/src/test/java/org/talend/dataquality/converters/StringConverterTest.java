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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test for class {@link StringConverter}.
 * 
 * @author msjian
 * @version 2017.02.08
 */
public class StringConverterTest {

    private static final String expected = "abc"; //$NON-NLS-1$

    @Test
    public void TestRemoveTrailingAndLeading() {
        // assertEquals(expected, " abc ".trim()); //$NON-NLS-1$
        // assertEquals('\t', '\u0009');

        // test for default character (whitespace)
        assertEquals(expected, new StringConverter(expected).removeTrailingAndLeadingCharacters());
        assertEquals(expected, new StringConverter(" abc").removeTrailingAndLeadingCharacters()); //$NON-NLS-1$
        assertEquals(expected, new StringConverter(" abc ").removeTrailingAndLeadingCharacters()); //$NON-NLS-1$
        assertEquals(expected, new StringConverter(" abc  ").removeTrailingAndLeadingCharacters()); //$NON-NLS-1$
        assertEquals(expected, new StringConverter("  abc ").removeTrailingAndLeadingCharacters()); //$NON-NLS-1$
        assertEquals(expected, new StringConverter("  abc  ").removeTrailingAndLeadingCharacters()); //$NON-NLS-1$

        assertEquals("ab c", new StringConverter(" ab c").removeTrailingAndLeadingCharacters()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("a b c", new StringConverter(" a b c ").removeTrailingAndLeadingCharacters()); //$NON-NLS-1$ //$NON-NLS-2$

        // test for other characters
        assertEquals(expected, new StringConverter("\t" + expected).removeTrailingAndLeadingCharacters("\t")); //$NON-NLS-1$ //$NON-NLS-2$ 
        assertEquals(expected, new StringConverter(expected + "\t").removeTrailingAndLeadingCharacters("\t")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(expected, new StringConverter('\u0009' + expected).removeTrailingAndLeadingCharacters("\t")); //$NON-NLS-1$
        assertEquals(expected,
                new StringConverter('\u0009' + expected + '\u0009' + '\u0009').removeTrailingAndLeadingCharacters("\t")); //$NON-NLS-1$

        assertEquals("abc ", new StringConverter("\t" + "abc ").removeTrailingAndLeadingCharacters("\t")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertEquals("a" + "\t" + "bc", new StringConverter("\t" + "a" + "\t" + "bc").removeTrailingAndLeadingCharacters("\t")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        assertEquals("\t" + expected, new StringConverter("\t" + "abc ").removeTrailingAndLeadingCharacters()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 

        assertEquals(expected, new StringConverter("\n" + expected).removeTrailingAndLeadingCharacters("\n")); //$NON-NLS-1$ //$NON-NLS-2$ 
        assertEquals("abc ", new StringConverter("\n" + "abc ").removeTrailingAndLeadingCharacters("\n")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        assertEquals(expected, new StringConverter(expected).removeTrailingAndLeadingCharacters("\r")); //$NON-NLS-1$
        assertEquals(expected, new StringConverter("\r" + expected).removeTrailingAndLeadingCharacters("\r")); //$NON-NLS-1$ //$NON-NLS-2$ 
        assertEquals(expected, new StringConverter("\r" + expected + "\r").removeTrailingAndLeadingCharacters("\r")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        assertEquals("abc ", new StringConverter("\r" + "abc ").removeTrailingAndLeadingCharacters("\r")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertEquals("abc ", new StringConverter("\r" + "abc " + "\r").removeTrailingAndLeadingCharacters("\r")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

        String[] whitespace_chars = new String[] { "" /* dummy empty string for homogeneity *///$NON-NLS-1$
                , "\\u0009" // CHARACTER TABULATION //$NON-NLS-1$
                , "\\u000A" // LINE FEED (LF) //$NON-NLS-1$
                , "\\u000B" // LINE TABULATION //$NON-NLS-1$
                , "\\u000C" // FORM FEED (FF) //$NON-NLS-1$
                , "\\u000D" // CARRIAGE RETURN (CR) //$NON-NLS-1$
                , "\\u0020" // SPACE //$NON-NLS-1$
                , "\\u0085" // NEXT LINE (NEL) //$NON-NLS-1$
                , "\\u00A0" // NO-BREAK SPACE //$NON-NLS-1$
                , "\\u1680" // OGHAM SPACE MARK //$NON-NLS-1$
                , "\\u180E" // MONGOLIAN VOWEL SEPARATOR //$NON-NLS-1$
                , "\\u2000" // EN QUAD //$NON-NLS-1$
                , "\\u2001" // EM QUAD //$NON-NLS-1$
                , "\\u2002" // EN SPACE //$NON-NLS-1$
                , "\\u2003" // EM SPACE //$NON-NLS-1$
                , "\\u2004" // THREE-PER-EM SPACE //$NON-NLS-1$
                , "\\u2005" // FOUR-PER-EM SPACE //$NON-NLS-1$
                , "\\u2006" // SIX-PER-EM SPACE //$NON-NLS-1$
                , "\\u2007" // FIGURE SPACE //$NON-NLS-1$
                , "\\u2008" // PUNCTUATION SPACE //$NON-NLS-1$
                , "\\u2009" // THIN SPACE //$NON-NLS-1$
                , "\\u200A" // HAIR SPACE //$NON-NLS-1$
                , "\\u2028" // LINE SEPARATOR //$NON-NLS-1$
                , "\\u2029" // PARAGRAPH SEPARATOR //$NON-NLS-1$
                , "\\u202F" // NARROW NO-BREAK SPACE //$NON-NLS-1$
                , "\\u205F" // MEDIUM MATHEMATICAL SPACE //$NON-NLS-1$
                , "\\u3000" // IDEOGRAPHIC SPACE //$NON-NLS-1$
        };
        for (String testChar : whitespace_chars) {
            assertEquals(expected, new StringConverter(testChar + expected).removeTrailingAndLeadingCharacters(testChar));
            assertEquals(expected,
                    new StringConverter(testChar + expected + testChar).removeTrailingAndLeadingCharacters(testChar));
            assertEquals(expected,
                    new StringConverter(testChar + expected + testChar + testChar).removeTrailingAndLeadingCharacters(testChar));
        }

    }
}
