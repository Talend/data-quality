// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import static org.talend.dataquality.semantic.datamasking.FunctionBuilder.functionInitializer;

import java.security.SecureRandom;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.FunctionFactory;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.FunctionType;
import org.talend.dataquality.datamasking.TypeTester;
import org.talend.dataquality.datamasking.functions.DateVariance;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.datamasking.functions.ReplaceAll;
import org.talend.dataquality.datamasking.semantic.AbstractDateFunction;
import org.talend.dataquality.datamasking.semantic.DateFunctionAdapter;
import org.talend.dataquality.datamasking.semantic.FluctuateNumericString;
import org.talend.dataquality.datamasking.semantic.ReplaceCharactersWithGeneration;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;
import org.talend.dataquality.semantic.validator.GenerateValidator;

public class SemanticMaskerFunctionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticMaskerFunctionFactory.class);

    public static Function<String> createMaskerFunctionForSemanticCategory(String semanticCategory, String dataType) {
        return createMaskerFunctionForSemanticCategory(semanticCategory, dataType, null, null);
    }

    public static Function<String> createMaskerFunctionForSemanticCategory(String semanticCategory, String dataType,
            List<String> params, DictionarySnapshot dictionarySnapshot, String seed, FunctionMode mode) {

        Function<String> function = null;
        final MaskableCategoryEnum cat = MaskableCategoryEnum.getCategoryById(semanticCategory);
        if (cat != null) {
            try {
                function = functionInitializer(cat);
            } catch (InstantiationException e) {
                LOGGER.debug(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                LOGGER.debug(e.getMessage(), e);
            }
        }

        if (function == null) {
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
                    final boolean isGenerexCompliant = udc.isGenerexCompliant(category.getId());
                    if (GenerateFromRegex.isValidPattern(patternString) && isGenerexCompliant) {
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
        }

        if (function == null) {
            switch (dataType) {
            case "numeric":
            case "integer":
            case "float":
            case "double":
            case "decimal":
                function = new FluctuateNumericString();
                function.parse("10", true, null);
                break;
            case "date":
                DateVariance df = new DateVariance();
                df.parse("61", true, null);
                function = new DateFunctionAdapter(df, params);
                break;
            case "string":
                function = new ReplaceCharactersWithGeneration();
                function.parse("X", true, null);
                break;
            default:
                break;

            }
        }
        if (function == null) {
            throw new IllegalArgumentException("No masking function available for the current column! SemanticCategory: "
                    + semanticCategory + " DataType: " + dataType);
        }
        // setRandom must be call because of there is some special class declaration init operation in the method(e.g
        // AbstractGenerateUniquePhoneNumber)

        function.setMaskingMode(mode);
        if (StringUtils.isNotEmpty(seed))
            function.setSeed(seed);
        else
            function.setRandom(new SecureRandom());

        return function;

    }

    @SuppressWarnings("unchecked")
    public static Function<String> createMaskerFunctionForSemanticCategory(String semanticCategory, String dataType,
            List<String> params, DictionarySnapshot dictionarySnapshot) {
        return createMaskerFunctionForSemanticCategory(semanticCategory, dataType, params, dictionarySnapshot, null,
                FunctionMode.RANDOM);
    }

    private static Function<String> adaptForDateFunction(List<String> datePatterns, AbstractDateFunction functionToAdapt,
            String extraParam) {
        functionToAdapt.parse(extraParam, true, null);
        return new DateFunctionAdapter(functionToAdapt, datePatterns);
    }

    /**
     * creates a data masking function with the given name and parameters
     *
     * @param functionType
     * @param dataType
     * @param extraParam
     */
    public static Function<String> getMaskerFunctionByFunctionName(FunctionType functionType, String dataType,
            String extraParam) {
        FunctionFactory factory = new FunctionFactory();
        TypeTester tester = new TypeTester();
        Function<String> function;
        try {
            if (FunctionType.KEEP_YEAR.equals(functionType) || FunctionType.DATE_VARIANCE.equals(functionType)) {
                function = adaptForDateFunction(null,
                        (AbstractDateFunction) factory.getFunction(functionType, tester.getTypeByName(dataType)), extraParam);
            } else if (FunctionType.NUMERIC_VARIANCE.equals(functionType)) {
                function = new FluctuateNumericString();
            } else if (FunctionType.REPLACE_ALL.equals(functionType)) {
                function = new ReplaceAll();
            } else {
                function = (Function<String>) factory.getFunction(functionType, tester.getTypeByName(dataType));
            }
            function.parse(extraParam, true, null);
            function.setKeepFormat(true);
            function.setKeepEmpty(true);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "No masking function available for the current column!  " + " DataType: " + dataType);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
                    "No masking function available for the current column!  " + " DataType: " + dataType);
        }

        return function;
    }

    /**
     * creates a data masking function with the given name and parameters
     *
     * @param functionType
     * @param dataType
     * @param extraParam
     */
    public static Function<String> getMaskerFunctionByFunctionName(FunctionType functionType, String dataType, String extraParam,
            String seed, FunctionMode mode) {

        Function<String> function = getMaskerFunctionByFunctionName(functionType, dataType, extraParam);
        function.setSeed(seed);
        function.setMaskingMode(mode);
        return function;
    }
}
