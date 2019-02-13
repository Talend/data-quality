package org.talend.dataquality.semantic.extraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that handles the extraction of the parts of a field that matches with elements in the given semantic categories.
 *
 * Each semantic category is represented by an extraction function
 * {@link ExtractFromSemanticType} in the list {@link #functions}.
 *
 * The extraction of the parts of a field works as follows :
 * <ul>
 *     <li>The input string is transformed into a {@link TokenizedString}.</li>
 *
 *     <li>For each represented semantic category in {@link #functions}, every part that matches
 *     exactly with an element of that category is returned in a list of {@link MatchedPart}.</li>
 *
 *     <li>If two different matches have the same token from the original field, there is a conflict.</li>
 *
 *     <li>A conflict is resolved by choosing the match with the more tokens from the original field.
 *     If both matches have the same number of tokens, the priority is given to the first represented
 *     semantic category in {@link #functions}.
 *     If both matches come from the same semantic category, the first match in the original field is kept.</li>
 *
 *     <li>The list of disjointed matching parts are transformed into strings and returned mapped by semantic category.</li>
 * </ul>
 *
 * @author afournier
 */
public class FieldExtractionFunction {

    private List<ExtractFromSemanticType> functions;

    FieldExtractionFunction(List<ExtractFromSemanticType> functions) {
        this.functions = functions;
    }

    public Map<String, List<String>> extractFieldParts(String field) {
        TokenizedString tokenizedField = new TokenizedString(field);
        List<MatchedPart> matches = new ArrayList<>();
        Map<String, List<String>> matchesByCategory = new HashMap<>();

        for (int i = 0; i < functions.size(); i++) {
            ExtractFromSemanticType function = functions.get(i);
            List<MatchedPart> functionMatches = function.getMatches(tokenizedField);
            List<String> matchString = new ArrayList<>();

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

    private void filter(List<MatchedPart> matches, Map<String, List<String>> matchesByCategory) {
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
