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

    public static String SEPARATORS = "[[\\p{Punct}&&[^'.]]\\s\\u00A0\\u2007\\u202F\\u3000]+";

    private static final Pattern separatorPattern = Pattern.compile(SEPARATORS);

    private final String value;

    private final List<String> tokens;

    private final List<String> separators;

    private boolean startingWithSeparator;

    private boolean endingWithSeparator;

    public TokenizedString(String str) {
        value = str;
        tokens = tokenize(value);
        separators = new ArrayList<>(tokens.size());
        extractSeparators();
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
        return separators;
    }

    public static List<String> tokenize(String field) {
        List<String> tokens = new ArrayList<>(Arrays.asList(separatorPattern.split(field)));

        if (!tokens.isEmpty() && tokens.get(0).isEmpty()) {
            tokens.remove(0);
        }

        return tokens;
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
}
