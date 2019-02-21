package org.talend.dataquality.semantic.extraction;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;

/**
 * Factory for instantiation of {@link FieldExtractionFunction}
 * according to the specified semantic categories and dictionary snapshot.
 *
 * @author afournier
 */
public class SemanticExtractionFunctionFactory {

    private SemanticExtractionFunctionFactory() {

    }

    public static FieldExtractionFunction createFieldExtractionFunction(List<String> categoryList) {
        return createFieldExtractionFunction(categoryList, null);
    }

    public static FieldExtractionFunction createFieldExtractionFunction(List<String> categoryList,
            DictionarySnapshot dictionarySnapshot) {

        dictionarySnapshot = dictionarySnapshot != null ? dictionarySnapshot : new StandardDictionarySnapshotProvider().get();

        List<ExtractFromSemanticType> extractFunctions = new ArrayList<>();
        for (String semanticCategory : categoryList) {

            DQCategory category = dictionarySnapshot != null ? dictionarySnapshot.getDQCategoryByName(semanticCategory)
                    : CategoryRegistryManager.getInstance().getCategoryMetadataByName(semanticCategory);

            if (category == null) {
                throw new IllegalArgumentException("Invalid Semantic Category Name : " + semanticCategory);
            }

            ExtractFromSemanticType function = getFunction(category, dictionarySnapshot);

            if (function != null) {
                extractFunctions.add(function);
            }
        }
        return new FieldExtractionFunction(extractFunctions);
    }

    protected static ExtractFromSemanticType getFunction(DQCategory category, DictionarySnapshot dicoSnapshot) {
        ExtractFromSemanticType function = null;
        switch (category.getType()) {
        case DICT:
            function = new ExtractFromDictionary(dicoSnapshot, category);
            break;
        case REGEX:
            function = new ExtractFromRegex(dicoSnapshot, category);
            break;
        case COMPOUND:
            function = new ExtractFromCompound(dicoSnapshot, category);
            break;
        default:
            break;
        }
        return function;
    }
}
