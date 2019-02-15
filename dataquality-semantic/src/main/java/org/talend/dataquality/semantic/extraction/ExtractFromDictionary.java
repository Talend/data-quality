package org.talend.dataquality.semantic.extraction;

import org.apache.commons.lang3.StringUtils;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
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

    private Index initIndex() {
        DQCategory cat = dicoSnapshot.getMetadata().get(semancticCategory.getId());
        if (cat != null) {
            if (!cat.getModified()) {
                return dicoSnapshot.getSharedDataDict();
            } else {
                return dicoSnapshot.getCustomDataDict();
            }
        }
        throw new IllegalArgumentException("The semantic category has not been found : " + semancticCategory);
    }

    @Override
    public List<MatchedPart> getMatches(TokenizedString tokenizedField) {
        List<MatchedPart> matchedParts = new ArrayList<>();
        List<String> tokens = tokenizedField.getTokens();

        int i = 0;
        while (i < tokens.size()) {
            int matchStart = -1;
            int matchEnd = -1;
            boolean exactMatch = false;
            List<String> phrase = new ArrayList<>();
            int j = i;
            while (j < tokens.size()) {
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
        Iterator<String> tokensIt = tokens.iterator();
        Iterator<String> phraseIt = phrase.iterator();
        while (tokensIt.hasNext() && phraseIt.hasNext()) {
            if (!tokensIt.next().equalsIgnoreCase(phraseIt.next())) {
                return false;
            }
        }
        return !(tokensIt.hasNext() || phraseIt.hasNext());
    }
}
