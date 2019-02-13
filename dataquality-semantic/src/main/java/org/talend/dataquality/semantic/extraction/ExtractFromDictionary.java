package org.talend.dataquality.semantic.extraction;

import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtractFromDictionary extends ExtractFromSemanticType {

    public ExtractFromDictionary(DictionarySnapshot snapshot, DQCategory category) {
        super(snapshot, category);
    }

    @Override
    public List<MatchedPart> getMatches(TokenizedString tokenizedField) {
        List<MatchedPart> matches = new ArrayList<>();
        List<String> tokens = tokenizedField.getTokens();

        for (int i = 0; i < tokens.size(); i++) {
            List<String> phrase = new ArrayList<>();
            Set<String> matchedDocumentRaws = new HashSet<>();
            List<Integer> matchedPositions = new ArrayList<>();
            int j;
            for (j = i; j < tokens.size(); j++) {
                phrase.add(tokens.get(j));
                if (!findMatches(phrase).isEmpty()) {
                    matchedPositions.add(j);
                } else {
                    break;
                }
            }
            i = j;
            if (!matchedPositions.isEmpty()) {
                matches.add(new MatchedPart(tokenizedField, matchedPositions));

            }
        }

        return matches;
    }

    private List<String> findMatches(List<String> phrase) {
        return ((LuceneIndex) dicoSnapshot.getSharedDataDict()).getSearcher()
                .searchPhraseInSemanticCategory(semancticCategory.getId(), phrase);
    }

}
