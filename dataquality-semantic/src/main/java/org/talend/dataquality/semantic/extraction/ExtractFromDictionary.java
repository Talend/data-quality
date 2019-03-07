package org.talend.dataquality.semantic.extraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

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

    private static final Pattern separatorPatternWithApostrophe = Pattern.compile("[\\p{Punct}\\s\\u00A0\\u2007\\u202F\\u3000]+");

    protected ExtractFromDictionary(DictionarySnapshot snapshot, DQCategory category) {
        super(snapshot, category);
        index = (LuceneIndex) initIndex();
    }

    @Override
    public List<MatchedPart> getMatches(TokenizedString tokenizedField) {
        Set<MatchedPart> uniqueMatchedParts = new TreeSet<>();

        uniqueMatchedParts.addAll(getMatchPart(tokenizedField, tokenizedField.getTokens()));

        if (tokenizedField.getValue().contains("'")) {
            List<String> tokensWithoutApostrophe = getTokensWithApostrophe(tokenizedField);
            uniqueMatchedParts.addAll(getMatchPart(tokenizedField, tokensWithoutApostrophe));
        }
        return new ArrayList(uniqueMatchedParts);
    }

    private List<MatchedPart> getMatchPart(TokenizedString tokenizedField, List<String> tokens) {
        List<MatchedPart> matchedParts = new ArrayList<>();

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

    private List<String> getTokensWithApostrophe(TokenizedString tokenizedString) {
        List<String> tokens = tokenizedString.getTokens();
        List<String> tokensWithoutApostrophe = new ArrayList<>(
                Arrays.asList(separatorPatternWithApostrophe.split(tokenizedString.getValue())));

        if (!tokensWithoutApostrophe.isEmpty() && tokensWithoutApostrophe.get(0).isEmpty()) {
            tokens.remove(0);
        }

        Iterator tokenIterator = tokensWithoutApostrophe.iterator();
        while (tokenIterator.hasNext()) {
            if (tokenIterator.next().toString().length() < 2) {
                tokenIterator.remove();
            }
        }

        return tokensWithoutApostrophe;

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
