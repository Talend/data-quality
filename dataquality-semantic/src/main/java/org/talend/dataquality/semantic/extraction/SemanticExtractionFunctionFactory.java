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

        DictionarySnapshot snapshotDict = dictionarySnapshot != null ? dictionarySnapshot
                : new StandardDictionarySnapshotProvider().get();

        List<ExtractFromSemanticType> extractFunctions = new ArrayList<>();
        for (String semanticCategory : categoryList) {

            DQCategory category = dictionarySnapshot != null ? dictionarySnapshot.getDQCategoryByName(semanticCategory)
                    : CategoryRegistryManager.getInstance().getCategoryMetadataByName(semanticCategory);

            if (category == null) {
                throw new IllegalArgumentException("Invalid Semantic Category Name : " + semanticCategory);
            }

            switch (category.getType()) {
            case DICT:
                ExtractFromSemanticType fun = new ExtractFromDictionary(snapshotDict, category);
                extractFunctions.add(fun);
                break;
            case REGEX:
                ExtractFromRegex extractFromRegex = new ExtractFromRegex(snapshotDict, category);
                extractFunctions.add(extractFromRegex);
                break;
            case COMPOUND:
                break;
            default:
                break;
            }
        }
        return new FieldExtractionFunction(extractFunctions);
    }
}
