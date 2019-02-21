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

    private String exactMatch;

    public MatchedPartRegex(TokenizedString tokenizedField, int start, int end) {
        checkBounds(start, end);
        originalField = tokenizedField;
        String field = tokenizedField.getValue();
        exactMatch = field.substring(start, end);
        this.start = getTokenNumber(field.substring(0, start));
        this.end = getTokenNumber(field.substring(0, end)) - 1;
        initTokenPositions();
    }

    private int getTokenNumber(String string) {
        TokenizedString tokenizedString = new TokenizedString(string);
        return tokenizedString.getTokens().size();
    }

    @Override
    public String getExactMatch() {
        return exactMatch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MatchedPartRegex)) {
            return false;
        }

        MatchedPartRegex otherMatchedPart = (MatchedPartRegex) o;
        return originalField.toString().equals(otherMatchedPart.originalField.toString())
                && exactMatch.equals(otherMatchedPart.exactMatch);
    }
}
