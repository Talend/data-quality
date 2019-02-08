package org.talend.dataquality.semantic.extraction;

import javafx.util.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FieldExtractionFunctionTest {

    FieldExtractionFunction function = new FieldExtractionFunction();

    @Test
    public void tokenize() {
        String input = ";This, .is. a test\twith/punctuation.";
        List<String> tokensExpected = Arrays.asList("This", "is", "a", "test", "with", "punctuation");
        List<String> separatorsExpected = Arrays.asList(", .", ". ", " ", "\t", "/");
        Pair<List<String>, List<String>> tokensAndSeparators = function.tokenize(input);

        assertEquals(tokensExpected, tokensAndSeparators.getKey());
        assertEquals(separatorsExpected, tokensAndSeparators.getValue());

    }

    @Test
    public void concatTokens() {
        String expected = "This, .is. a test   with/punctuation";
        // TODO
    }

}
