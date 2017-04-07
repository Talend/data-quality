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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test for class {@link DuplicateCharEraser}.
 * 
 * @author qiongli
 * @version 2017.03.30
 */
public class DuplicateCharEraserTest {

    @Test
    public void testRemoveDuplicateCR() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser();
        String input = "a\rbccccdeaa\r\r\ry"; //$NON-NLS-1$
        assertEquals("a\rbccccdeaa\ry", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testRemoveDuplicateLF() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser();
        String input = "a\nbccccdeaa\n\n\ny"; //$NON-NLS-1$
        assertEquals("a\nbccccdeaa\ny", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testRemoveDuplicateCRLF() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser();
        String input = "a\r\nbccccdeaa\r\n\r\n\r\ny"; //$NON-NLS-1$
        assertEquals("a\r\nbccccdeaa\r\ny", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testRemoveDuplicateTAB() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser();
        String input = "a\tbccccdeaa\t\t\t\t\t\ty"; //$NON-NLS-1$
        assertEquals("a\tbccccdeaa\ty", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testRemoveDuplicateLetter() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser('c');
        String input = "atbccccdeaaCCtcy"; //$NON-NLS-1$
        assertEquals("atbcdeaaCCtcy", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
        duplicateCharEraser = new DuplicateCharEraser('a');
        input = "aaatbccccdeaaCCtcy"; //$NON-NLS-1$
        assertEquals("atbccccdeaCCtcy", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
        input = "acacacactbccccdeaCCtaccy"; //$NON-NLS-1$

        input = "abcdef"; //$NON-NLS-1$
        assertEquals("abcdef", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testRemoveDuplicateNumber() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser('1');
        String input = "011111123"; //$NON-NLS-1$
        assertEquals("0123", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$

        duplicateCharEraser = new DuplicateCharEraser('3');
        input = "apple 12333"; //$NON-NLS-1$
        assertEquals("apple 123", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testRemoveDuplicateNull1() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser('c');
        String input = null;
        assertEquals(null, duplicateCharEraser.removeRepeatedChar(input));
        input = ""; //$NON-NLS-1$
        assertEquals("", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$
    }

    @Test
    public void testRemoveDuplicateNull2() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser();
        String input = "aaabc"; //$NON-NLS-1$
        assertEquals(input, duplicateCharEraser.removeRepeatedChar(input));
        duplicateCharEraser = new DuplicateCharEraser(' ');
        assertEquals(input, duplicateCharEraser.removeRepeatedChar(input));
        duplicateCharEraser = new DuplicateCharEraser();
        assertEquals(input, duplicateCharEraser.removeRepeatedChar(input));
    }

    @Test
    public void testRemoveWhiteSpace() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser();
        String input = "a   b\t\t\tc\n\n\nd\r\re\f\ff"; //$NON-NLS-1$
        String cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertEquals("a b\tc\nd\re\ff", cleanStr); //$NON-NLS-1$

        input = "aaab\r\n\r\n\r\nx"; //$NON-NLS-1$
        cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertEquals("aaab\r\nx", cleanStr); //$NON-NLS-1$

        input = "a\u0085\u0085\u0085b\u00A0\u00A0c\u1680\u1680d\u180E\u180Ee\u2000\u2000f\u2001\u2001g\u2002\u2002h\u2003\u2003i\u2004\u2004"; //$NON-NLS-1$
        cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertEquals("a\u0085b\u00A0c\u1680d\u180Ee\u2000f\u2001g\u2002h\u2003i\u2004", cleanStr); //$NON-NLS-1$

        input = "a\u2005\u2005\u2005b\u2006\u2006c\u2007\u2007d\u2008\u2008e\u2009\u2009f\u200A\u200Ag\u2028\u2028h\u2029\u2029i\u202F\u202Fj\u205F\u205Fk\u3000\u3000l"; //$NON-NLS-1$
        cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertEquals("a\u2005b\u2006c\u2007d\u2008e\u2009f\u200Ag\u2028h\u2029i\u202Fj\u205Fk\u3000l", cleanStr); //$NON-NLS-1$
    }

    @Test
    public void testRemoveWhiteSpaceNull() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser();
        String input = ""; //$NON-NLS-1$
        String cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertEquals("", cleanStr); //$NON-NLS-1$
        input = null;
        cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertNull(cleanStr);
    }

    @Test
    public void testRemoveWhiteSpacWithoutSpace() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser();
        String input = "abccdef"; //$NON-NLS-1$
        String cleanStr = duplicateCharEraser.removeRepeatedChar(input);
        assertEquals("abccdef", cleanStr); //$NON-NLS-1$
    }

    @Test
    /**
     *  test specail cahrs('|','(','}','[',']','+','^') in regex.
     */
    public void testRemoveSpecialCharacter() {
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser(')');
        String input = "Gooooalllll))))]]]]]]++++++[[[^^^\\\\(((|||"; //$NON-NLS-1$
        assertEquals("Gooooalllll)]]]]]]++++++[[[^^^\\\\(((|||", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$

        duplicateCharEraser = new DuplicateCharEraser(']');
        assertEquals("Gooooalllll))))]++++++[[[^^^\\\\(((|||", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$

        duplicateCharEraser = new DuplicateCharEraser('+');
        assertEquals("Gooooalllll))))]]]]]]+[[[^^^\\\\(((|||", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$

        duplicateCharEraser = new DuplicateCharEraser('\\');
        assertEquals("Gooooalllll))))]]]]]]++++++[[[^^^\\(((|||", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$

        duplicateCharEraser = new DuplicateCharEraser('^');
        assertEquals("Gooooalllll))))]]]]]]++++++[[[^\\\\(((|||", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$

        duplicateCharEraser = new DuplicateCharEraser('[');
        assertEquals("Gooooalllll))))]]]]]]++++++[^^^\\\\(((|||", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$

        duplicateCharEraser = new DuplicateCharEraser('(');
        assertEquals("Gooooalllll))))]]]]]]++++++[[[^^^\\\\(|||", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$

        duplicateCharEraser = new DuplicateCharEraser('|');
        assertEquals("Gooooalllll))))]]]]]]++++++[[[^^^\\\\(((|", duplicateCharEraser.removeRepeatedChar(input)); //$NON-NLS-1$

    }

    @Test
    public void testSeveralCharsShouldBeDeduplicated() {
        // given
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser("abc");
        String input = "abcabcabc";

        // when
        String cleanStr = duplicateCharEraser.removeRepeatedChar(input);

        // then
        assertEquals("abc", cleanStr);
    }

    @Test
    public void testMustAcceptRegexSpecialChars() {
        // given
        DuplicateCharEraser duplicateCharEraser = new DuplicateCharEraser("a*({].");
        String input = "a*({].a*({].a*({].a*({].";

        // when
        String cleanStr = duplicateCharEraser.removeRepeatedChar(input);

        // then
        assertEquals("a*({].", cleanStr);
    }

}
