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

    public MatchedPartDict(TokenizedString originalField, int start, int end) {
        checkBounds(start, end);
        this.originalField = originalField;
        this.start = start;
        this.end = end;
        initTokenPositions();
    }

    @Override
    public String getExactMatch() {
        List<String> tokens = originalField.getTokens();
        List<String> separators = originalField.getSeparators();

        StringBuilder sb = new StringBuilder(tokens.get(start));
        for (int i = start; i < end; i++) {
            sb.append(separators.get(i)).append(tokens.get(i + 1));
        }
        return sb.toString();
    }
}
