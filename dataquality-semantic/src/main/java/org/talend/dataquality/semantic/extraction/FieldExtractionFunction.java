package org.talend.dataquality.semantic.extraction;

import javafx.util.Pair;
import org.talend.dataquality.semantic.model.DQCategory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldExtractionFunction {

    private final Pattern separatorPattern = Pattern.compile("[\\p{Punct}\\s]+");

    private List<ExtractFromSemanticType> functions;
    
    public FieldExtractionFunction() {

    }

    public Map<String, List<String>> extractFieldParts(String field) {
        Map<String, List<String>> matchedTokens = new HashMap<>();

        Pair<List<String>, List<String>> tokensAndSeparators = tokenize(field);

        return matchedTokens;
    }

    /**
     * Tokenize the input and return the list of tokens and the list of separators.
     *
     * @param field to tokenize
     * @return a pair containing the list of token (Left) and the list of separators (Right)
     */
    protected Pair<List<String>, List<String>> tokenize(String field) {
        List<String> tokens = new ArrayList<>(Arrays.asList(separatorPattern.split(field)));

        if (tokens.get(0).isEmpty()) {
            tokens.remove(0);
        }

        List<String> separators = new ArrayList<>(tokens.size() - 1);
        Matcher matcher = separatorPattern.matcher(field);

        while (matcher.find()) {
            if (matcher.start() != 0 && matcher.end() < field.length() - 1) {
                separators.add(matcher.group());
            }
        }

        return new Pair<>(tokens, separators);
    }

    protected List<String> concatTokens(List<List<Integer>> matchedTokens, List<String> tokens, List<String> separators) {
        List<String> joinTokenList = new ArrayList<>();

        for(List<Integer> tokenList : matchedTokens) {
            StringBuilder sb = new StringBuilder(tokens.get(tokenList.get(0)));
            for(int i = 1; i<tokenList.size(); i++) {
                sb.append(separators.get(tokenList.get(i-1))).append(tokens.get(tokenList.get(i)));
            }
            joinTokenList.add(sb.toString());
        }
        return joinTokenList;
    }
}
