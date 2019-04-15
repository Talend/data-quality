package org.talend.dataquality.semantic.extraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
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

    private static final Pattern fullSeparatorPattern = Pattern.compile("[\\p{Punct}\\s\\u00A0\\u2007\\u202F\\u3000]+");

    protected ExtractFromDictionary(DictionarySnapshot snapshot, DQCategory category) {
        super(snapshot, category);
        index = (LuceneIndex) initIndex();
    }

    @Override
    public List<MatchedPart> getMatches(TokenizedString tokenizedField) {
        Set<MatchedPart> uniqueMatchedParts = new LinkedHashSet<>();

        uniqueMatchedParts.addAll(getMatchPart(tokenizedField, tokenizedField.getTokens()));

        if (tokenizedField.getValue().contains("'") || tokenizedField.getValue().contains(".")) {
            TokenizedString clone = new TokenizedString(tokenizedField.getValue());

            List<String> tokensWithoutApostrophe = getTokensWithoutApostropheAndDots(tokenizedField);

            clone.getTokens().clear();
            clone.getTokens().addAll(tokensWithoutApostrophe);
            uniqueMatchedParts.addAll(getMatchPart(clone, tokensWithoutApostrophe));
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
                String tokenWithoutAccent = StringUtils.stripAccents(tokens.get(j));

                phrase.add(tokenWithoutAccent);
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

    private List<String> getTokensWithoutApostropheAndDots(TokenizedString tokenizedString) {
        List<String> tokens = tokenizedString.getTokens();
        List<String> tokensWithoutApostrophe = new ArrayList<>(
                Arrays.asList(fullSeparatorPattern.split(tokenizedString.getValue())));

        if (!tokensWithoutApostrophe.isEmpty() && tokensWithoutApostrophe.get(0).isEmpty()) {
            tokens.remove(0);
        }

        return tokensWithoutApostrophe;

    }

    private List<String> findMatches(List<String> phrase) {
        return index.getSearcher().searchPhraseInSemanticCategory(semancticCategory.getId(), StringUtils.join(phrase, ' '));
    }

    private int exactMatchIndex(List<String> phrase, List<String> matches) {
        Collections.sort(matches, Comparator.comparingInt(String::length).reversed());

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
            String word = phrase.get(i);
            if (!tokens.get(i).equalsIgnoreCase(word)) {
                if (i == tokens.size() - 1 && word.endsWith(".")) {
                    word = word.substring(0, word.length() - 1);
                    return tokens.get(i).equalsIgnoreCase(word);
                }
                return false;
            }
        }
        return true;
    }
}
