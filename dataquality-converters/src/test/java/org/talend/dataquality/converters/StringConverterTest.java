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
import static org.junit.Assert.assertNull;

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
        StringConverter stringConverter = new StringConverter();
        String inputData = " " + expected; //$NON-NLS-1$
        for (String removechar : stringConverter.WHITESPACE_CHARS) {
            inputData = inputData + removechar;
        }
        assertEquals(expected, stringConverter.removeTrailingAndLeadingWhitespaces(inputData));
    }

    @Test
    public void testremoveDuplicate_CR() {
        StringConverter stringConverter = new StringConverter("\r"); //$NON-NLS-1$
        String input = "a\rbccccdeaa\r\r\ry"; //$NON-NLS-1$
        assertEquals("a\rbccccdeaa\ry", stringConverter.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testremoveDuplicate_LF() {
        StringConverter stringConverter = new StringConverter("\n"); //$NON-NLS-1$
        String input = "a\nbccccdeaa\n\n\ny"; //$NON-NLS-1$
        assertEquals("a\nbccccdeaa\ny", stringConverter.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testremoveDuplicate_CRLF() {
        StringConverter stringConverter = new StringConverter("\r\n"); //$NON-NLS-1$
        String input = "a\r\nbccccdeaa\r\n\r\n\r\ny"; //$NON-NLS-1$
        assertEquals("a\r\nbccccdeaa\r\ny", stringConverter.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testremoveDuplicate_TAB() {
        StringConverter stringConverter = new StringConverter("\t"); //$NON-NLS-1$
        String input = "a\tbccccdeaa\t\t\t\t\t\ty"; //$NON-NLS-1$
        assertEquals("a\tbccccdeaa\ty", stringConverter.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testremoveDuplicate_LETTER() {
        StringConverter stringConverter = new StringConverter("c"); //$NON-NLS-1$
        String input = "atbccccdeaaCCtcy"; //$NON-NLS-1$
        assertEquals("atbcdeaaCCtcy", stringConverter.removeRepeatedChar(input)); //$NON-NLS-1$
        stringConverter = new StringConverter("a"); //$NON-NLS-1$
        input = "aaatbccccdeaaCCtcy"; //$NON-NLS-1$
        assertEquals("atbccccdeaCCtcy", stringConverter.removeRepeatedChar(input)); //$NON-NLS-1$
        stringConverter = new StringConverter("ac"); //$NON-NLS-1$
        input = "acacacactbccccdeaCCtaccy"; //$NON-NLS-1$
        assertEquals("actbccccdeaCCtaccy", stringConverter.removeRepeatedChar(input)); //$NON-NLS-1$

        input = "abcdef"; //$NON-NLS-1$
        assertEquals("abcdef", stringConverter.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testremoveDuplicate_NULL1() {
        StringConverter stringConverter = new StringConverter("c"); //$NON-NLS-1$
        String input = null;
        assertEquals(null, stringConverter.removeRepeatedChar(input));
        input = ""; //$NON-NLS-1$
        assertEquals("", stringConverter.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testremoveDuplicate_NULL2() {
        StringConverter stringConverter = new StringConverter();
        String input = "aaabc"; //$NON-NLS-1$
        assertEquals(input, stringConverter.removeRepeatedChar(input));
        stringConverter = new StringConverter(""); //$NON-NLS-1$
        assertEquals(input, stringConverter.removeRepeatedChar(input));
        stringConverter = new StringConverter(null);
        assertEquals(input, stringConverter.removeRepeatedChar(input));
    }

    @Test
    public void testremoveWhiteSpace() {
        StringConverter stringConverter = new StringConverter();
        String input = "a   b\t\t\tc\n\n\nd\r\re\f\ff"; //$NON-NLS-1$
        String cleanStr = stringConverter.removeRepeatedWhitespaces(input);
        assertEquals("a b\tc\nd\re\ff", cleanStr); //$NON-NLS-1$

        // \r\n will not be removed
        input = "aaab\r\n\r\n\r\nx"; //$NON-NLS-1$
        cleanStr = stringConverter.removeRepeatedWhitespaces(input);
        assertEquals("aaab\r\n\r\n\r\nx", cleanStr); //$NON-NLS-1$

        input = "a\u0085\u0085\u0085b\u00A0\u00A0c\u1680\u1680d\u180E\u180Ee\u2000\u2000f\u2001\u2001g\u2002\u2002h\u2003\u2003i\u2004\u2004"; //$NON-NLS-1$
        cleanStr = stringConverter.removeRepeatedWhitespaces(input);
        assertEquals("a\u0085b\u00A0c\u1680d\u180Ee\u2000f\u2001g\u2002h\u2003i\u2004", cleanStr); //$NON-NLS-1$

        input = "a\u2005\u2005\u2005b\u2006\u2006c\u2007\u2007d\u2008\u2008e\u2009\u2009f\u200A\u200Ag\u2028\u2028h\u2029\u2029i\u202F\u202Fj\u205F\u205Fk\u3000\u3000l"; //$NON-NLS-1$
        cleanStr = stringConverter.removeRepeatedWhitespaces(input);
        assertEquals("a\u2005b\u2006c\u2007d\u2008e\u2009f\u200Ag\u2028h\u2029i\u202Fj\u205Fk\u3000l", cleanStr); //$NON-NLS-1$
    }

    @Test
    public void testremoveWhiteSpaceNull() {
        StringConverter stringConverter = new StringConverter();
        String input = ""; //$NON-NLS-1$
        String cleanStr = stringConverter.removeRepeatedWhitespaces(input);
        assertEquals("", cleanStr); //$NON-NLS-1$
        input = null;
        cleanStr = stringConverter.removeRepeatedWhitespaces(input);
        assertNull(cleanStr);
    }

    @Test
    public void testremoveWhiteSpacWithoutSpace() {
        StringConverter stringConverter = new StringConverter();
        String input = "abccdef"; //$NON-NLS-1$
        String cleanStr = stringConverter.removeRepeatedWhitespaces(input);
        assertEquals("abccdef", cleanStr); //$NON-NLS-1$
    }

}
