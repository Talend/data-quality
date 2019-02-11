package org.talend.dataquality.semantic.extraction;

import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SemanticExtractionFunctionFactory {

    public static FieldExtractionFunction createFieldExtractionFunction(List<String> categoryList,
            DictionarySnapshot dictionarySnapshot) {

        List<ExtractFromSemanticType> extractFunctions = new ArrayList<>();
        for (String semanticCategory : categoryList) {

            DQCategory category = dictionarySnapshot != null ? dictionarySnapshot.getDQCategoryByName(semanticCategory)
                    : CategoryRegistryManager.getInstance().getCategoryMetadataByName(semanticCategory);

            if (category == null) {
                throw new IllegalArgumentException("Invalid Semantic Category Name : " + semanticCategory);
            }

            CategoryType categoryType = category.getType();

            switch (categoryType) {
                case DICT:
                    ExtractFromSemanticType fun = new ExtractFromDictionary(category);
                    extractFunctions.add(fun);
                    break;
                case REGEX:
                    break;
                case COMPOUND:
                    break;
            }
        }
        return new FieldExtractionFunction(extractFunctions);
    }
}
