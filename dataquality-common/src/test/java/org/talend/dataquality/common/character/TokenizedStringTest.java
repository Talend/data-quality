package org.talend.dataquality.common.character;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TokenizedStringTest {

    @Test
    public void correctTokensAndSeparators() {
        TokenizedString str = new TokenizedString(";This, .is. a test\twith/punctuation.");

        List<String> expectedTokens = Arrays.asList("This", ".is.", "a", "test", "with", "punctuation.");
        List<String> expectedSeparators = Arrays.asList(";", ", ", " ", " ", "\t", "/");

        assertEquals(expectedTokens, str.getTokens());
        assertEquals(expectedSeparators, str.getSeparators());
        assertTrue(str.isStartingWithSeparator());
        assertFalse(str.isEndingWithSeparator());
    }

    @Test
    public void noBreakSpaces() {
        TokenizedString str = new TokenizedString("A\u00A0B\u2007C\u202FD\u3000E");

        List<String> expectedTokens = Arrays.asList("A", "B", "C", "D", "E");
        List<String> expectedSeparators = Arrays.asList("\u00A0", "\u2007", "\u202F", "\u3000");

        assertEquals(expectedTokens, str.getTokens());
        assertEquals(expectedSeparators, str.getSeparators());
    }

    public static String firstCharIgnoreNumeric(String str) {
        if (str == null || "".equals(str)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        int cpCount = str.codePointCount(0, str.length());
        for (int j = 0; j < cpCount; j++) {
            int codePointPos = str.offsetByCodePoints(0, j);
            int codePoint = str.codePointAt(codePointPos);
            if (!Character.isDigit(codePoint)) {
                return new String(Character.toChars(codePoint));
            }
        }
        return "";
    }

    public static String firstCharKeepNumeric(String str) {
        if (str == null || "".equals(str)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();
        int cpCount = str.codePointCount(0, str.length());
        int i = 0;
        int codePoint;
        do {
            int codePointPos = str.offsetByCodePoints(0, i);
            codePoint = str.codePointAt(codePointPos);
            sb.append(new String(Character.toChars(codePoint)));
            i++;
        } while (i < cpCount && Character.isDigit(codePoint));

        return sb.toString();
    }

    public static String firstCharEWIgnoreNumeric(String str) {
        return firstCharEWIgnoreNumeric(str, "[ \t\n\r\f]+");
    }

    public static String firstCharEWIgnoreNumeric(String str, String separatorPattern) {
        if (str == null || "".equals(str)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();
        String[] tokens = str.split(separatorPattern);

        for (String token : tokens) {
            sb.append(firstCharIgnoreNumeric(token));
        }
        return sb.toString();
    }

    @Test
    public void rtre() {
        String str = "\uD840\uDC40\uD840\uDFD3\uD841\uDC01\uD840\uDFD3";
        int strLen = str.length();
        int cpLength = str.codePointCount(0, strLen);
        System.out.println("Str Length = " + strLen + "\nCPLength = " + cpLength);

        int i = 0;
        String lastChar = "";
        while (i < cpLength) {
            int startOffset = str.offsetByCodePoints(0, i);
            int endOffset = str.offsetByCodePoints(startOffset, 1);
            String newChar = new String(Character.toChars(str.codePointAt(startOffset)));
            System.out.println(newChar);
            assertNotEquals(lastChar, newChar);
            lastChar = newChar;
            i++;
        }
    }
}