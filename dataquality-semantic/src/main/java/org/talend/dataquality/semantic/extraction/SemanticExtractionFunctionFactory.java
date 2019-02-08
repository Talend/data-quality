package org.talend.dataquality.semantic.extraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.datamasking.GenerateFromCompound;
import org.talend.dataquality.semantic.datamasking.GenerateFromDictionaries;
import org.talend.dataquality.semantic.datamasking.GenerateFromRegex;
import org.talend.dataquality.semantic.datamasking.MaskableCategoryEnum;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;
import org.talend.dataquality.semantic.validator.GenerateValidator;

import java.util.List;

public class SemanticExtractionFunctionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticExtractionFunctionFactory.class);

    /*
    public static FieldExtractionFunction createFieldExtractionFunction(List<String> semanticCategory, List<String> params, DictionarySnapshot dictionarySnapshot) {
    
        FieldExtractionFunction function;
    
        DQCategory category = dictionarySnapshot != null ? dictionarySnapshot.getDQCategoryByName(semanticCategory)
                : CategoryRegistryManager.getInstance().getCategoryMetadataByName(semanticCategory);
        if (category != null) {
            CategoryType categoryType = category.getType();
            String extraParameter = category.getId();
    
            switch (categoryType) {
                case DICT:
                    function = new GenerateFromDictionaries();
                    DictionarySnapshot snapshot = dictionarySnapshot != null ? dictionarySnapshot
                            : new StandardDictionarySnapshotProvider().get();
                    ((GenerateFromDictionaries) function).setDictionarySnapshot(snapshot);
                    break;
                case REGEX:
                    final UserDefinedClassifier udc = dictionarySnapshot != null ? dictionarySnapshot.getRegexClassifier()
                            : CategoryRegistryManager.getInstance().getRegexClassifier();
                    final String patternString = udc.getPatternStringByCategoryId(category.getId());
                    if (GenerateFromRegex.isValidPattern(patternString)) {
                        function = new GenerateFromRegex();
                        extraParameter = patternString;
                    }
                    break;
                case COMPOUND:
    
                    DictionarySnapshot snapshotCompound = dictionarySnapshot != null ? dictionarySnapshot
                            : new StandardDictionarySnapshotProvider().get();
    
                    List types = GenerateValidator.initSemanticTypes(snapshotCompound, category, null);
                    if (types.size() > 0) {
                        function = new GenerateFromCompound();
                        ((GenerateFromCompound) function).setDictionarySnapshot(snapshotCompound);
                        ((GenerateFromCompound) function).setCategoryValues(types);
                    }
    
                    break;
            }
            if (function != null)
                function.parse(extraParameter, true, null);
        }
    
        return function;
    }
    */
}
