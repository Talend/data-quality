package org.talend.dataquality.semantic.extraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;


import java.util.List;

public class SemanticExtractionFunctionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticExtractionFunctionFactory.class);

    public static FieldExtractionFunction createFieldExtractionFunction(List<String> categoryList, DictionarySnapshot dictionarySnapshot) {

        FieldExtractionFunction function = new FieldExtractionFunction();

        for(String semanticCategory : categoryList) {

            DQCategory category = dictionarySnapshot != null ? dictionarySnapshot.getDQCategoryByName(semanticCategory)
                    : CategoryRegistryManager.getInstance().getCategoryMetadataByName(semanticCategory);
            if (category != null) {
                CategoryType categoryType = category.getType();

                switch (categoryType) {
                    case DICT:
                        break;
                    case REGEX:
                        break;
                    case COMPOUND:
                        break;
                }
            }
        }
        return function;
    }
}
