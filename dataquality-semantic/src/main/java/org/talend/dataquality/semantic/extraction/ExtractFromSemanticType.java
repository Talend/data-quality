package org.talend.dataquality.semantic.extraction;

import org.talend.dataquality.semantic.model.DQCategory;

import java.util.List;

public abstract class ExtractFromSemanticType {

    private DQCategory semancticCategory;

    public ExtractFromSemanticType(DQCategory category) {
        semancticCategory = category;
    }

    public String getCategoryName() {
        return semancticCategory.getName();
    }

    public abstract List<MatchedPart> getMatches(TokenizedString tokenizedField);

}
