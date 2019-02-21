package org.talend.dataquality.semantic.extraction;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TokenizedStringTest {

    @Test
    public void correctTokensAndSeparators() {
        TokenizedString str = new TokenizedString(";This, .is. a test\twith/punctuation.");

        List<String> expectedTokens = Arrays.asList("This", "is", "a", "test", "with", "punctuation");
        List<String> expectedSeparators = Arrays.asList(", .", ". ", " ", "\t", "/");

        assertEquals(expectedTokens, str.getTokens());
        assertEquals(expectedSeparators, str.getSeparators());
    }

    @Test
    public void noBreakSpaces() {
        TokenizedString str = new TokenizedString("A\u00A0B\u2007C\u202FD\u3000E");
        System.out.println(str.getValue());

        List<String> expectedTokens = Arrays.asList("A", "B", "C", "D", "E");
        List<String> expectedSeparators = Arrays.asList("\u00A0", "\u2007", "\u202F", "\u3000");

        assertEquals(expectedTokens, str.getTokens());
        assertEquals(expectedSeparators, str.getSeparators());
    }

    @Test
    public void concatTokens() {
        String expected = "This, .is. a test\twith/punctuation";
        List<String> tokens = Arrays.asList("This", "is", "a", "test", "with", "punctuation");
        List<String> separators = Arrays.asList(", .", ". ", " ", "\t", "/");
        TokenizedString str = new TokenizedString(tokens, separators);

        assertEquals(expected, str.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooMuchSeparators() {
        List<String> tokens = Arrays.asList("This", "is", "a", "test", "with", "punctuation");
        List<String> separators = Arrays.asList(";", ", .", ". ", " ", "\t", "/", ".");
        new TokenizedString(tokens, separators);
    }
}
