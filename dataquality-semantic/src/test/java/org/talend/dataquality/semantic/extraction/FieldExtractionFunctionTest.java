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
        List<String> expected = Arrays.asList("This, .is", "test\twith/punctuation");
        List<String> tokens = Arrays.asList("This", "is", "a", "test", "with", "punctuation");
        List<String> separators = Arrays.asList(", .", ". ", " ", "\t", "/");
        List<List<Integer>> matchedTokens = Arrays.asList(Arrays.asList(0, 1), Arrays.asList(3, 4, 5));

        assertEquals(expected, function.concatTokens(matchedTokens, tokens, separators));
    }

}
