package org.talend.dataquality.semantic.extraction;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenizedString {

    private final Pattern separatorPattern = Pattern.compile("[\\p{Punct}\\s]+");

    private final String value;

    private final List<String> tokens;

    private final List<String> separators;

    public TokenizedString(String str) {
        value = str;
        Pair<List<String>, List<String>> tokensAndSeparators = tokenize(value);
        tokens = tokensAndSeparators.getKey();
        separators = tokensAndSeparators.getValue();
    }

    public TokenizedString(List<String> tokens, List<String> separators) {
        this.tokens = tokens;
        this.separators = separators;
        checkTokens();
        value = concatTokens();
    }

    public List<String> getTokens() {
        return tokens;
    }

    public List<String> getSeparators() {
        return separators;
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

    protected String concatTokens() {
        StringBuilder sb = new StringBuilder(tokens.get(0));
        for (int i = 1; i < tokens.size(); i++) {
            sb.append(separators.get(i - 1)).append(tokens.get(i));
        }
        return sb.toString();
    }

    private void checkTokens() {
        if (tokens.size() - 1 != separators.size()) {
            throw new IllegalArgumentException(
                    "Invalid tokens and/or separators ! There must be one less separator than tokens.\nNumber of tokens : "
                            + tokens.size() + "\nNumber of separators : " + separators.size());
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
