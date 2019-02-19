package org.talend.dataquality.semantic.extraction;

import org.apache.commons.lang3.StringUtils;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

import java.util.ArrayList;
import java.util.List;

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
            boolean exactMatch = false;
            List<String> phrase = new ArrayList<>();
            int j = i;
            while (j < nbOfTokens) {
                phrase.add(tokens.get(j));
                List<String> matches = findMatches(phrase);
                if (matches.isEmpty()) {
                    break;
                }
                if (containsExactMatch(phrase, matches)) {
                    exactMatch = true;
                    matchStart = i;
                    matchEnd = j;
                }
                j++;
            }
            if (exactMatch) {
                matchedParts.add(new MatchedPart(tokenizedField, matchStart, matchEnd));
                i = matchEnd;
            }
            i++;
        }

        return matchedParts;
    }

    private List<String> findMatches(List<String> phrase) {
        return index.getSearcher().searchPhraseInSemanticCategory(semancticCategory.getId(), StringUtils.join(phrase, ' '));
    }

    private boolean containsExactMatch(List<String> phrase, List<String> matches) {
        for (String match : matches) {
            if (equalsIgnoreCase(TokenizedString.tokenize(match), phrase)) {
                return true;
            }
        }
        return false;
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
