package org.talend.dataquality.common.character;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringCheckerTest {

    @Test
    public void lowerCase() {
        String input = "lowercase";
        assertTrue(StringChecker.containsLowerCase(input));
        assertFalse(StringChecker.containsUpperCase(input));
        assertFalse(StringChecker.containsLowerAndUpperCase(input));
    }

    @Test
    public void upperCase() {
        String input = "UPPERCASE";
        assertFalse(StringChecker.containsLowerCase(input));
        assertTrue(StringChecker.containsUpperCase(input));
        assertFalse(StringChecker.containsLowerAndUpperCase(input));
    }

    @Test
    public void mixCase() {
        String input = "miXCase";
        assertTrue(StringChecker.containsLowerCase(input));
        assertTrue(StringChecker.containsUpperCase(input));
        assertTrue(StringChecker.containsLowerAndUpperCase(input));
    }

    @Test
    public void surrogate() {
        String input = "𠀀𠀐我𠀑";
        assertFalse(StringChecker.containsLowerCase(input));
        assertFalse(StringChecker.containsUpperCase(input));
        assertFalse(StringChecker.containsLowerAndUpperCase(input));
    }

    @Test
    public void surrogateWithLowerAndUpper() {
        String input = "𠀀a𠀐B我𠀑";
        assertTrue(StringChecker.containsLowerCase(input));
        assertTrue(StringChecker.containsUpperCase(input));
        assertTrue(StringChecker.containsLowerAndUpperCase(input));
    }

}