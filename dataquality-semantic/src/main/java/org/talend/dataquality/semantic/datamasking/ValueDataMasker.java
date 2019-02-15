// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.semantic.datamasking;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.datamasking.semantic.ReplaceCharacterHelper;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;
import org.talend.dataquality.semantic.statistics.SemanticQualityAnalyzer;

/**
 * API of data masking action using semantic domain information.
 */
public class ValueDataMasker implements Serializable {

    private static final long serialVersionUID = 7071792900542293289L;

    private Function<String> function;

    private DQCategory category;

    private SemanticQualityAnalyzer semanticQualityAnalyzer = null;

    Function<String> getFunction() {
        return function;
    }

    /**
     * ValueDataMasker constructor.
     *
     * @param function the embedded data masking function
     */
    public ValueDataMasker(Function<String> function) {
        this.function = function;
    }

    /**
     * ValueDataMasker constructor.
     * 
     * @param semanticCategory the semantic domain information
     * @param dataType the data type information
     */
    public ValueDataMasker(String semanticCategory, String dataType) {
        this(semanticCategory, dataType, null);
    }

    /**
     *
     * ValueDataMasker constructor.
     *
     * @param semanticCategory the semantic domain information
     * @param dataType the data type information
     * @param params extra parameters such as date time pattern list
     */
    public ValueDataMasker(String semanticCategory, String dataType, List<String> params) {
        this(semanticCategory, dataType, params, null);
    }

    /**
     *
     * ValueDataMasker constructor.
     *
     * @param semanticCategory the semantic domain information
     * @param dataType the data type information
     * @param params extra parameters such as date time pattern list
     * @param dictionarySnapshot the dictionary snapshot
     */
    public ValueDataMasker(String semanticCategory, String dataType, List<String> params, DictionarySnapshot dictionarySnapshot) {
        this.function = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory(semanticCategory, dataType, params,
                dictionarySnapshot);

        initCategory(semanticCategory, dictionarySnapshot);
    }

    /**
     *
     * ValueDataMasker constructor.
     *
     * @param semanticCategory the semantic domain information
     * @param dataType the data type information
     * @param params extra parameters such as date time pattern list
     * @param dictionarySnapshot the dictionary snapshot
     * @param seed seed used for masking
     */
    public ValueDataMasker(String semanticCategory, String dataType, List<String> params, DictionarySnapshot dictionarySnapshot,
            String seed, FunctionMode mode) {

        this.function = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory(semanticCategory, dataType, params,
                dictionarySnapshot, seed, mode);

        initCategory(semanticCategory, dictionarySnapshot);
    }

    private void initCategory(String semanticCategory, DictionarySnapshot dictionarySnapshot) {
        category = dictionarySnapshot != null ? dictionarySnapshot.getDQCategoryByName(semanticCategory)
                : CategoryRegistryManager.getInstance().getCategoryMetadataByName(semanticCategory);

        if (category != null) {
            DictionarySnapshot dictionary = dictionarySnapshot != null ? dictionarySnapshot
                    : new StandardDictionarySnapshotProvider().get();
            if (category.getCompleteness()) {
                semanticQualityAnalyzer = new SemanticQualityAnalyzer(dictionary, new String[] { category.getName() });
            }

        }
    }

    /**
     * mask the input value.
     * 
     * @param input
     * @return the masked value
     */
    public String maskValue(String input) {
        if (semanticQualityAnalyzer != null && !semanticQualityAnalyzer.isValid(category, input)) {
            Random r = function.getRandom();

            if (FunctionMode.CONSISTENT == function.getMaskingMode()) {
                r = new Random();
                r.setSeed(input.hashCode() ^ function.getSeed().hashCode());
            }

            return ReplaceCharacterHelper.replaceCharacters(input, r);
        }
        // when category is null or input is valid
        try {
            return function.generateMaskedRow(input);
        } catch (RuntimeException e) {
            return ReplaceCharacterHelper.replaceCharacters(input, function.getRandom());
        }
    }

    /**
     * update the extra param for function
     *
     * @param extraParam
     */
    public void resetExtraParameter(String extraParam) {
        function.parse(extraParam, true, null);
    }
}
