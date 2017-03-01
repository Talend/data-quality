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
    public void testRemoveTrailingAndLeading() {
        // assertEquals(expected, " abc ".trim()); //$NON-NLS-1$
        // assertEquals('\t', '\u0009');
        StringConverter stringConverter = new StringConverter();

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
        assertEquals(expected, stringConverter.removeTrailingAndLeading('\u0009' + expected + '\u0009' + '\u0009', "\t")); //$NON-NLS-1$

        assertEquals("abc ", stringConverter.removeTrailingAndLeading("\t" + "abc ", "\t")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertEquals("a" + "\t" + "bc", stringConverter.removeTrailingAndLeading("\t" + "a" + "\t" + "bc", "\t")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        assertEquals("\t" + expected, stringConverter.removeTrailingAndLeading("\t" + "abc ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 

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
            assertEquals(expected, stringConverter.removeTrailingAndLeading(testChar + expected, testChar));
            assertEquals(expected, stringConverter.removeTrailingAndLeading(testChar + expected + testChar, testChar));
            assertEquals(expected, stringConverter.removeTrailingAndLeading(testChar + expected + testChar + testChar, testChar));
        }

    }
}
