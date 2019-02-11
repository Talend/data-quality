package org.talend.dataquality.semantic.extraction;

import org.talend.dataquality.semantic.model.DQCategory;

import java.util.List;

public class ExtractFromDictionary extends ExtractFromSemanticType {

    public ExtractFromDictionary(DQCategory category) {
        super(category);
    }

    @Override
    public List<MatchedPart> getMatches(TokenizedString tokenizedField) {
        return null;
    }
}
