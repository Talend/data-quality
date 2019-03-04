package org.talend.dataquality.semantic.extraction;

/**
 * Child class of {@link MatchedPart} for handling matches from regexes.
 *
 * The match is built from the bounds obtained by the Pattern matcher.
 * It is not reconstructed from {@link #originalField#tokenPositions}
 * and {@link #originalField#separators} like for dictionary matches.
 * Therefore a regex match can start or end with a separator.
 *
 * @author afournier
 */
public class MatchedPartRegex extends MatchedPart {

    public MatchedPartRegex(TokenizedString tokenizedField, int startChar, int endChar) {
        originalField = tokenizedField;
        checkBounds(startChar, endChar);
        String field = tokenizedField.getValue();
        exactMatch = field.substring(startChar, endChar);
        this.start = getTokenNumber(field.substring(0, startChar));
        this.end = getTokenNumber(field.substring(0, endChar)) - 1;
        initTokenPositions();
    }

    @Override
    protected void checkBounds(int start, int end) {
        if (start < 0 || end < 0 || end < start || end > originalField.getValue().length()) {
            throw new IllegalArgumentException("Bounds for match are incorrect : start = {}, end = {}" + start + end);
        }
    }

    private int getTokenNumber(String string) {
        TokenizedString tokenizedString = new TokenizedString(string);
        return tokenizedString.getTokens().size();
    }
}
