package org.talend.dataquality.semantic.extraction;

import static org.talend.dataquality.semantic.extraction.SemanticExtractionFunctionFactory.getFunction;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

public class ExtractFromCompound extends ExtractFromSemanticType {

    private List<ExtractFromSemanticType> children;

    protected ExtractFromCompound(DictionarySnapshot snapshot, DQCategory category) {
        super(snapshot, category);
        this.children = new ArrayList<>();
        if (semancticCategory.getChildren() != null) {
            this.semancticCategory.getChildren().forEach(child -> {
                child = dicoSnapshot.getDQCategoryById(child.getId());
                children.add(getFunction(child, this.dicoSnapshot));
            });
        }
    }

    @Override
    public List<MatchedPart> getMatches(TokenizedString tokenizedField) {
        List<MatchedPart> matchedParts = new ArrayList<>();
        this.children.forEach(child -> {
            List<MatchedPart> parts = child.getMatches(tokenizedField);
            matchedParts.addAll(parts);
        });
        return matchedParts;
    }
}
