package org.talend.dataquality.semantic.extraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used to handle a tokenized field for extracting parts of fields.
 *
 * The String {@link #value} is tokenized according to the {@link #separatorPattern},
 * i.e every token is separated by a sequence of punctuation marks and/or spaces of any length.
 *
 * {@link #separators} between tokens are also kept in order to be able to reconstruct the matching parts as they were.
 *
 * @author afournier
 */
public class TokenizedString {

    private static final Pattern separatorPattern = Pattern.compile("[\\p{Punct}\\s]+");

    private final String value;

    private final List<String> tokens;

    private final List<String> separators;

    TokenizedString(String str) {
        value = str;
        tokens = tokenize(value);
        separators = extractSeparators();
    }

    TokenizedString(List<String> tokens, List<String> separators) {
        this.tokens = tokens;
        this.separators = separators;
        checkTokens();
        value = concatTokens();
    }

    List<String> getTokens() {
        return tokens;
    }

    List<String> getSeparators() {
        return separators;
    }

    static List<String> tokenize(String field) {
        List<String> tokens = new ArrayList<>(Arrays.asList(separatorPattern.split(field)));

        if (tokens.get(0).isEmpty()) {
            tokens.remove(0);
        }

        return tokens;
    }

    /**
     *
     */
    private List<String> extractSeparators() {
        List<String> separators = new ArrayList<>(tokens.size() - 1);
        Matcher matcher = separatorPattern.matcher(value);

        while (matcher.find()) {
            if (matcher.start() != 0 && matcher.end() < value.length() - 1) {
                separators.add(matcher.group());
            }
        }

        return separators;
    }

    private String concatTokens() {
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
