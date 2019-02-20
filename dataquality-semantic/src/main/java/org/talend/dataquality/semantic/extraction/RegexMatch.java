package org.talend.dataquality.semantic.extraction;

/**
 * Child class of {@link MatchedPart} for handling matches from regexes.
 *
 * The match string value is built from the bounds obtained by the Pattern matcher.
 * It is not reconstructed from {@link #originalField#tokenPositions}
 * and {@link #originalField#separators} like for dictionary matches.
 * Therefore a regex match can start or end with a separator.
 *
 * @author afournier
 */
public class RegexMatch extends MatchedPart {

    private String exactMatch;

    public RegexMatch(TokenizedString tokenizedField, int start, int end) {
        super();
        checkBounds(start, end);
        originalField = tokenizedField;
        String field = tokenizedField.toString();
        exactMatch = field.substring(start, end);
        super.start = getTokenNumber(field.substring(0, start));
        super.end = getTokenNumber(field.substring(0, end)) - 1;
        initTokenPositions();
    }

    private int getTokenNumber(String string) {
        TokenizedString tokenizedString = new TokenizedString(string);
        return tokenizedString.getTokens().size();
    }

    @Override
    public String toString() {
        return exactMatch;
    }
}
