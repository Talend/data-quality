package org.talend.dataquality.common.character;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringHandlerTest {

    private static final String BLANK_STR = ""; //$NON-NLS-1$

    @Test
    public void nullGivesBlank() {
        String input = null;
        assertEquals(BLANK_STR, StringHandler.firstCharIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperKeepNumeric(input));
    }

    @Test
    public void blankGivesEmpty() {
        String input = BLANK_STR;
        assertEquals(BLANK_STR, StringHandler.firstCharIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperKeepNumeric(input));
    }

    @Test
    public void spaceConsideredAsChar() {
        String input = " ";
        assertEquals(input, StringHandler.firstCharIgnoreNumeric(input));
        assertEquals(input, StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperKeepNumeric(input));
    }

    @Test
    public void quoteConsideredAsChar() {
        String input = "\"";
        assertEquals(input, StringHandler.firstCharIgnoreNumeric(input));
        assertEquals(input, StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperKeepNumeric(input));
    }

    @Test
    public void wordWithSurrogates() {
        String input = "𠀀𠀐我𠀑ab";
        assertEquals("𠀀", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("𠀀", StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperKeepNumeric(input));
    }

    @Test
    public void wordLowerCase() {
        String input = "word";
        assertEquals("w", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("w", StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperKeepNumeric(input));
    }

    @Test
    public void wordWithUpperCase() {
        String input = "Alpha-Methyl-PHEneThylAMINE";
        assertEquals("A", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("A", StringHandler.firstCharKeepNumeric(input));
        assertEquals("A", StringHandler.firstUpperIgnoreNumeric(input));
        assertEquals("A", StringHandler.firstUpperKeepNumeric(input));
        assertEquals("AMPHETAMINE", StringHandler.allUpperIgnoreNumeric(input));
        assertEquals("AMPHETAMINE", StringHandler.allUpperKeepNumeric(input));
    }

    @Test
    public void wordWithUpperCaseAndDigits() {
        String input = "WoRd134";
        assertEquals("W", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("W", StringHandler.firstCharKeepNumeric(input));
        assertEquals("W", StringHandler.firstUpperIgnoreNumeric(input));
        assertEquals("W", StringHandler.firstUpperKeepNumeric(input));
        assertEquals("WR", StringHandler.allUpperIgnoreNumeric(input));
        assertEquals("WR134", StringHandler.allUpperKeepNumeric(input));
    }

    @Test
    public void wordWithSpecialChars() {
        String input = "WoRd134";
        assertEquals("W", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("W", StringHandler.firstCharKeepNumeric(input));
        assertEquals("W", StringHandler.firstUpperIgnoreNumeric(input));
        assertEquals("W", StringHandler.firstUpperKeepNumeric(input));
        assertEquals("WR", StringHandler.allUpperIgnoreNumeric(input));
        assertEquals("WR134", StringHandler.allUpperKeepNumeric(input));
    }

    @Test
    public void digits() {
        String input = "1234";
        assertEquals(BLANK_STR, StringHandler.firstCharIgnoreNumeric(input));
        assertEquals(input, StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperIgnoreNumeric(input));
        assertEquals(input, StringHandler.firstUpperKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperIgnoreNumeric(input));
        assertEquals(input, StringHandler.allUpperKeepNumeric(input));
    }

    @Test
    public void digitsInFront() {
        String input = "3COM";
        assertEquals("C", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("3", StringHandler.firstCharKeepNumeric(input));
        assertEquals("C", StringHandler.firstUpperIgnoreNumeric(input));
        assertEquals("3", StringHandler.firstUpperKeepNumeric(input));
        assertEquals("COM", StringHandler.allUpperIgnoreNumeric(input));
        assertEquals("3COM", StringHandler.allUpperKeepNumeric(input));
    }

    @Test
    public void digitsInMiddle() {
        String input = "abc123def";
        assertEquals("a", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("a", StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperIgnoreNumeric(input));
        assertEquals("123", StringHandler.allUpperKeepNumeric(input));
    }
}