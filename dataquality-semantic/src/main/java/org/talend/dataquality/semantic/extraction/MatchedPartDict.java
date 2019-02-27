package org.talend.dataquality.semantic.extraction;

import java.util.List;

/**
 * Child class of {@link MatchedPart} for handling matches from semantic dictionaries.
 *
 * The match is built from the first and last positions
 * of matching tokens in the {@link #originalField#tokenPositions}.
 *
 * @author afournier
 */
public class MatchedPartDict extends MatchedPart {

    public MatchedPartDict(TokenizedString originalField, int startToken, int endToken, String luceneMatch) {
        this.originalField = originalField;
        checkBounds(startToken, endToken);
        this.start = startToken;
        this.end = endToken;
        initTokenPositions();
        computeExactMatch(luceneMatch);
    }

    @Override
    protected void checkBounds(int start, int end) {
        if (start < 0 || end < 0 || end < start || end >= originalField.getTokens().size()) {
            throw new IllegalArgumentException("Bounds for match are incorrect : start = {}, end = {}" + start + end);
        }
    }

    private void computeExactMatch(String luceneMatch) {
        StringBuilder match = joinMatchedTokens();
        TokenizedString tokenizedMatch = new TokenizedString(luceneMatch);

        if (tokenizedMatch.isStartingWithSeparator() && this.hasPrecedingSeparator()) {
            addPrecedingSeparators(match, tokenizedMatch.getSeparators());
        }

        if (tokenizedMatch.isEndingWithSeparator() && this.hasFollowingSeparator()) {
            addFollowingSeparators(match, tokenizedMatch.getSeparators());
        }
        exactMatch = match.toString();
    }

    private StringBuilder joinMatchedTokens() {
        List<String> tokens = originalField.getTokens();
        List<String> separators = originalField.getSeparators();

        StringBuilder sb = new StringBuilder(tokens.get(start));
        for (int i = start; i < end; i++) {
            sb.append(separators.get(i)).append(tokens.get(i + 1));
        }
        return sb;
    }

    private boolean hasPrecedingSeparator() {
        return start > 0 || originalField.isStartingWithSeparator();
    }

    private boolean hasFollowingSeparator() {
        return end < originalField.getTokens().size() - 1 || originalField.isEndingWithSeparator();
    }

    private void addPrecedingSeparators(StringBuilder match, List<String> separators) {
        String precedingSeparator = originalField.getSeparators()
                .get(originalField.isStartingWithSeparator() ? start : start - 1);
        String matchFirstSeparator = separators.get(0);

        int curs1 = precedingSeparator.length() - 1;
        int curs2 = matchFirstSeparator.length() - 1;
        while (curs1 >= 0 && curs2 >= 0 && precedingSeparator.charAt(curs1) == matchFirstSeparator.charAt(curs2)) {
            match.insert(0, precedingSeparator.charAt(curs1--));
            curs2--;
        }
    }

    private void addFollowingSeparators(StringBuilder match, List<String> separators) {
        String followingSeparator = originalField.getSeparators().get(originalField.isStartingWithSeparator() ? end + 1 : end);
        String matchLastSeparator = separators.get(separators.size() - 1);

        int curs1 = 0;
        int curs2 = 0;
        while (curs1 < followingSeparator.length() && curs2 < matchLastSeparator.length()
                && followingSeparator.charAt(curs1) == matchLastSeparator.charAt(curs2)) {
            match.append(followingSeparator.charAt(curs1++));
            curs2++;
        }
    }
}
