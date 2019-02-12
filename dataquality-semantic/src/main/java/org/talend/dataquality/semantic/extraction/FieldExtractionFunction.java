package org.talend.dataquality.semantic.extraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FieldExtractionFunction {

    private List<ExtractFromSemanticType> functions;

    public FieldExtractionFunction(List<ExtractFromSemanticType> functions) {
        this.functions = functions;
    }

    public Map<String, List<String>> extractFieldParts(String field) {
        TokenizedString tokenizedField = new TokenizedString(field);
        List<MatchedPart> matches = new ArrayList<>();
        Map<String, List<String>> matchesByCategory = new HashMap<>();

        for (int i = 0; i < functions.size(); i++) {
            ExtractFromSemanticType function = functions.get(i);
            List<MatchedPart> functionMatches = function.getMatches(tokenizedField);
            List<String> matchString = new ArrayList<>(matches.size());

            for (MatchedPart match : functionMatches) {
                match.setPriority(i);
                matchString.add(match.toString());
            }
            matchesByCategory.put(function.getCategoryName(), matchString);
            matches.addAll(functionMatches);
        }

        Collections.sort(matches);
        filter(matches, matchesByCategory);

        return matchesByCategory;
    }

    protected void filter(List<MatchedPart> matches, Map<String, List<String>> matchesByCategory) {
        Set<Integer> matchedTokens = new HashSet<>();
        for (MatchedPart match : matches) {
            boolean toAdd = true;
            for (Integer token : match.getTokenPositions()) {
                if (matchedTokens.contains(token)) {
                    ExtractFromSemanticType function = functions.get(match.getPriority());
                    matchesByCategory.get(function.getCategoryName()).remove(match.toString());
                    toAdd = false;
                    break;
                }
            }
            if (toAdd) {
                matchedTokens.addAll(match.getTokenPositions());
            }
        }
    }
}
