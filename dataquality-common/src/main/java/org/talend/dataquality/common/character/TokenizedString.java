package org.talend.dataquality.common.character;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used to handle a tokenized string.
 *
 * The String {@link #value} is tokenized according to the {@link #separatorPattern},
 * i.e every token is separated by a sequence of punctuation marks and/or spaces of any length.
 *
 * {@link #separators} between tokens are also kept in order to be able to reconstruct the matching parts as they were.
 *
 * @author afournier
 */
public class TokenizedString {

    private static final String DEFAULT_SEPARATORS = "[[\\p{Punct}&&[^'.]]\\s\\u00A0\\u2007\\u202F\\u3000]+";

    private Pattern separatorPattern = Pattern.compile(DEFAULT_SEPARATORS);

    private final String value;

    private final List<String> tokens;

    private final List<String> separators;

    private boolean startingWithSeparator;

    private boolean endingWithSeparator;

    public TokenizedString(String str, String listOfSeparators) {
        value = str;
        separatorPattern = Pattern.compile(listOfSeparators);
        tokens = tokenize(value);
        separators = new ArrayList<>(tokens.size());
    }

    public TokenizedString(String str) {
        value = str;
        tokens = tokenize(value);
        separators = new ArrayList<>(tokens.size());
    }

    public boolean isStartingWithSeparator() {
        return startingWithSeparator;
    }

    public boolean isEndingWithSeparator() {
        return endingWithSeparator;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public List<String> getSeparators() {
        if (separators.isEmpty()) {
            extractSeparators();
        }
        return separators;
    }

    public List<String> tokenize(String field) {

        if (field == null) {
            return new ArrayList<>();
        }

        List<String> fieldTokens = new ArrayList<>(Arrays.asList(separatorPattern.split(field)));
        if (!fieldTokens.isEmpty() && fieldTokens.get(0).isEmpty()) {
            fieldTokens.remove(0);
        }

        return fieldTokens;
    }

    private void extractSeparators() {
        Matcher matcher = separatorPattern.matcher(value);

        startingWithSeparator = false;
        endingWithSeparator = false;
        while (matcher.find()) {
            if (matcher.start() == 0) {
                startingWithSeparator = true;
            } else if (matcher.end() == value.length()) {
                endingWithSeparator = true;
            }
            separators.add(matcher.group());
        }
    }

    public Pattern getSeparatorPattern() {
        return separatorPattern;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TokenizedString that = (TokenizedString) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
