package org.talend.dataquality.common.character;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringHandlerTest {

    private static final String BLANK_STR = ""; //$NON-NLS-1$

    private static final String SPACE_STR = " "; //$NON-NLS-1$

    private static final String QUO_STR = "\""; //$NON-NLS-1$

    private static final String LOWER_STR = "abcd";

    private static final String MIX_CASE_STR = "Alpha-Methyl-PHEneThylAMINE";

    private static final String DIGITS = "123";

    private static final String STR_DIGITS = "abc123DEF";

    private static final String SPECIAL_STR = "American Telephone & Telegraph";

    private static final String MIXTD_SURROGATEPAIR = "𠀀𠀐我𠀑ab"; //$NON-NLS-1$

    private static final String JAPANESE_STR = "リンゴを食べる"; //$NON-NLS-1$

    @Test
    public void nullGivesBlank() {
        String input = null;
        assertEquals(BLANK_STR, StringHandler.firstCharIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialKeepNumeric(input));
    }

    @Test
    public void blankGivesEmpty() {
        String input = BLANK_STR;
        assertEquals(BLANK_STR, StringHandler.firstCharIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialKeepNumeric(input));
    }

    @Test
    public void spaceConsideredAsChar() {
        String input = " ";
        assertEquals(input, StringHandler.firstCharIgnoreNumeric(input));
        assertEquals(input, StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialKeepNumeric(input));
    }

    @Test
    public void quoteConsideredAsChar() {
        String input = "\"";
        assertEquals(input, StringHandler.firstCharIgnoreNumeric(input));
        assertEquals(input, StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialKeepNumeric(input));
    }

    @Test
    public void wordWithSurrogates() {
        String input = "𠀀𠀐我𠀑ab";
        assertEquals("𠀀", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("𠀀", StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialKeepNumeric(input));
    }

    @Test
    public void wordLowerCase() {
        String input = "word";
        assertEquals("w", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("w", StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialKeepNumeric(input));
    }

    @Test
    public void wordWithUpperCase() {
        String input = "Alpha-Methyl-PHEneThylAMINE";
        assertEquals("A", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("A", StringHandler.firstCharKeepNumeric(input));
        assertEquals("A", StringHandler.firstUpperOrSpecialIgnoreNumeric(input));
        assertEquals("A", StringHandler.firstUpperOrSpecialKeepNumeric(input));
        assertEquals("AMPHETAMINE", StringHandler.allUpperAndSpecialIgnoreNumeric(input));
        assertEquals("AMPHETAMINE", StringHandler.allUpperAndSpecialKeepNumeric(input));
    }

    @Test
    public void wordWithUpperCaseAndDigits() {
        String input = "WoRd134";
        assertEquals("W", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("W", StringHandler.firstCharKeepNumeric(input));
        assertEquals("W", StringHandler.firstUpperOrSpecialIgnoreNumeric(input));
        assertEquals("W", StringHandler.firstUpperOrSpecialKeepNumeric(input));
        assertEquals("WR", StringHandler.allUpperAndSpecialIgnoreNumeric(input));
        assertEquals("WR134", StringHandler.allUpperAndSpecialKeepNumeric(input));
    }

    @Test
    public void wordWithSpecialChars() {
        String input = "WoRd134";
        assertEquals("W", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("W", StringHandler.firstCharKeepNumeric(input));
        assertEquals("W", StringHandler.firstUpperOrSpecialIgnoreNumeric(input));
        assertEquals("W", StringHandler.firstUpperOrSpecialKeepNumeric(input));
        assertEquals("WR", StringHandler.allUpperAndSpecialIgnoreNumeric(input));
        assertEquals("WR134", StringHandler.allUpperAndSpecialKeepNumeric(input));
    }

    @Test
    public void digits() {
        String input = "1234";
        assertEquals(BLANK_STR, StringHandler.firstCharIgnoreNumeric(input));
        assertEquals(input, StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialIgnoreNumeric(input));
        assertEquals(input, StringHandler.firstUpperOrSpecialKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialIgnoreNumeric(input));
        assertEquals(input, StringHandler.allUpperAndSpecialKeepNumeric(input));
    }

    @Test
    public void digitsInFront() {
        String input = "3COM";
        assertEquals("C", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("3", StringHandler.firstCharKeepNumeric(input));
        assertEquals("C", StringHandler.firstUpperOrSpecialIgnoreNumeric(input));
        assertEquals("3", StringHandler.firstUpperOrSpecialKeepNumeric(input));
        assertEquals("COM", StringHandler.allUpperAndSpecialIgnoreNumeric(input));
        assertEquals("3COM", StringHandler.allUpperAndSpecialKeepNumeric(input));
    }

    @Test
    public void digitsInMiddle() {
        String input = "abc123def";
        assertEquals("a", StringHandler.firstCharIgnoreNumeric(input));
        assertEquals("a", StringHandler.firstCharKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialIgnoreNumeric(input));
        assertEquals(BLANK_STR, StringHandler.firstUpperOrSpecialKeepNumeric(input));
        assertEquals(BLANK_STR, StringHandler.allUpperAndSpecialIgnoreNumeric(input));
        assertEquals("123", StringHandler.allUpperAndSpecialKeepNumeric(input));
    }
}