package org.talend.dataquality.semantic.extraction;

import org.apache.commons.lang3.StringUtils;

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

    private static String DASH = "-";

    private static String SPACE = " ";

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

        String originalValue = originalField.getValue();

        //searching for the character start index
        int indexStart = 0;
        List<String> tokens = originalField.getTokens();
        for (int i = 0; i < start; i++) {
            indexStart += tokens.get(i).length() + 1; // +1 is for space
        }

        //remove all accents,  dash on original value
        String originalWithModif = StringUtils.stripAccents(originalValue);
        originalWithModif = originalWithModif.substring(indexStart).toLowerCase().replaceAll(DASH, SPACE);

        //remove all accents,  dash on lucene match
        String match = StringUtils.stripAccents(luceneMatch);
        match = match.toLowerCase().replaceAll(DASH, SPACE);

        String commonSubString = longestCommonSubstring(originalWithModif, match.toLowerCase());
        if (!commonSubString.isEmpty()) { //we have found the longest common substring between value and lucene
            int indexOf = originalWithModif.indexOf(commonSubString);
            exactMatch = originalValue.substring(indexStart + indexOf, indexStart + indexOf + commonSubString.length()); //sub spart with good case and separator
        }
    }

    private static String longestCommonSubstring(String S1, String S2) {
        int Start = 0;
        int Max = 0;
        for (int i = 0; i < S1.length(); i++) {
            for (int j = 0; j < S2.length(); j++) {
                int x = 0;
                while (S1.charAt(i + x) == S2.charAt(j + x)) {
                    x++;
                    if (((i + x) >= S1.length()) || ((j + x) >= S2.length()))
                        break;
                }
                if (x > Max) {
                    Max = x;
                    Start = i;
                }
            }
        }
        return S1.substring(Start, (Start + Max));
    }
}
