package org.talend.dataquality.semantic.extraction;

import static org.talend.dataquality.semantic.extraction.SemanticExtractionFunctionFactory.getFunction;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

public class ExtractFromCompound extends ExtractFromSemanticType {

    protected ExtractFromCompound(DictionarySnapshot snapshot, DQCategory category) {
        super(snapshot, category);
    }

    @Override
    public List<MatchedPart> getMatches(TokenizedString tokenizedField) {
        List<MatchedPart> matchedParts = new ArrayList<>();
        this.semancticCategory.getChildren().forEach(category -> {
            category = dicoSnapshot.getDQCategoryById(category.getId());
            ExtractFromSemanticType function = getFunction(category, this.dicoSnapshot);

            if (function != null) {
                matchedParts.addAll(function.getMatches(tokenizedField));
            }
        });
        return matchedParts;
    }
}
