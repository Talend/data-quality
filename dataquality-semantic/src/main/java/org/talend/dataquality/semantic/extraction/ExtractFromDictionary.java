package org.talend.dataquality.semantic.extraction;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

/**
 * This function extracts parts of fields that matches exactly with elements in a semantic dictionary.
 *
 * @author afournier
 */
public class ExtractFromDictionary extends ExtractFromSemanticType {

    private final LuceneIndex index;

    protected ExtractFromDictionary(DictionarySnapshot snapshot, DQCategory category) {
        super(snapshot, category);
        index = (LuceneIndex) initIndex();
    }

    @Override
    public List<MatchedPart> getMatches(TokenizedString tokenizedField) {
        List<MatchedPart> matchedParts = new ArrayList<>();
        List<String> tokens = tokenizedField.getTokens();

        int nbOfTokens = tokens.size();
        int i = 0;
        while (i < nbOfTokens) {
            int matchStart = -1;
            int matchEnd = -1;
            String luceneMatch = null;
            List<String> phrase = new ArrayList<>();

            int j = i;
            while (j < nbOfTokens) {
                phrase.add(StringUtils.stripAccents(tokens.get(j)));
                List<String> matches = findMatches(phrase);

                if (matches.isEmpty()) {
                    break;
                }

                int match = exactMatchIndex(phrase, matches);
                if (match > -1) {
                    luceneMatch = matches.get(match);
                    matchStart = i;
                    matchEnd = j;
                }
                j++;
            }

            if (luceneMatch != null) {
                matchedParts.add(new MatchedPartDict(tokenizedField, matchStart, matchEnd, luceneMatch));
                i = matchEnd;
            }
            i++;
        }
        return matchedParts;
    }

    private List<String> findMatches(List<String> phrase) {
        return index.getSearcher().searchPhraseInSemanticCategory(semancticCategory.getId(), StringUtils.join(phrase, ' '));
    }

    private int exactMatchIndex(List<String> phrase, List<String> matches) {
        for (int i = 0; i < matches.size(); i++) {
            List<String> matchTokens = TokenizedString.tokenize(StringUtils.stripAccents(matches.get(i)));
            if (equalsIgnoreCase(matchTokens, phrase)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Check lists equality with case insensitivity for the String objects.
     */
    private boolean equalsIgnoreCase(List<String> tokens, List<String> phrase) {
        if (tokens == null || phrase == null) {
            return false;
        }

        if (tokens.size() != phrase.size()) {
            return false;
        }

        for (int i = 0; i < tokens.size(); i++) {
            if (!tokens.get(i).equalsIgnoreCase(phrase.get(i))) {
                return false;
            }
        }
        return true;
    }
}
