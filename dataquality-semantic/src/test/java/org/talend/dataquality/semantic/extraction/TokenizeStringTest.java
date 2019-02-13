package org.talend.dataquality.semantic.extraction;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TokenizeStringTest {

    @Test
    public void tokenize() {
        TokenizedString str = new TokenizedString(";This, .is. a test\twith/punctuation.");

        List<String> expectedTokens = Arrays.asList("This", "is", "a", "test", "with", "punctuation");
        List<String> expectedSeparators = Arrays.asList(", .", ". ", " ", "\t", "/");

        assertEquals(expectedTokens, str.getTokens());
        assertEquals(expectedSeparators, str.getSeparators());
    }

    @Test
    public void concatTokens() {
        String expected = "This, .is. a test\twith/punctuation";
        List<String> tokens = Arrays.asList("This", "is", "a", "test", "with", "punctuation");
        List<String> separators = Arrays.asList(", .", ". ", " ", "\t", "/");
        TokenizedString str = new TokenizedString(tokens, separators);

        assertEquals(expected, str.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooMuchSeparators() {
        String expected = "This, .is. a test\twith/punctuation";
        List<String> tokens = Arrays.asList("This", "is", "a", "test", "with", "punctuation");
        List<String> separators = Arrays.asList(";", ", .", ". ", " ", "\t", "/", ".");
        TokenizedString str = new TokenizedString(tokens, separators);
    }
}
