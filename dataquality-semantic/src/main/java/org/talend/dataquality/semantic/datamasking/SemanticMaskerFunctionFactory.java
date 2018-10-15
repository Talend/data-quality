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

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.FunctionFactory;
import org.talend.dataquality.datamasking.FunctionType;
import org.talend.dataquality.datamasking.TypeTester;
import org.talend.dataquality.datamasking.functions.DateVariance;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.datamasking.functions.ReplaceAll;
import org.talend.dataquality.datamasking.semantic.DateFunctionAdapter;
import org.talend.dataquality.datamasking.semantic.FluctuateNumericString;
import org.talend.dataquality.datamasking.semantic.GenerateFromRegex;
import org.talend.dataquality.datamasking.semantic.ReplaceCharactersWithGeneration;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

public class SemanticMaskerFunctionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticMaskerFunctionFactory.class);

    public static Function<String> createMaskerFunctionForSemanticCategory(String semanticCategory, String dataType) {
        return createMaskerFunctionForSemanticCategory(semanticCategory, dataType, null, null);
    }

    @SuppressWarnings("unchecked")
    public static Function<String> createMaskerFunctionForSemanticCategory(String semanticCategory, String dataType,
            List<String> params, DictionarySnapshot dictionarySnapshot) {
        Function<String> function = null;
        final MaskableCategoryEnum cat = MaskableCategoryEnum.getCategoryById(semanticCategory);
        if (cat != null) {
            try {
                function = (Function<String>) cat.getFunctionType().getClazz().newInstance();
                function.parse(cat.getParameter(), true, null);
                function.setKeepFormat(true);
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
                if (CategoryType.DICT.equals(category.getType())) {
                    function = new GenerateFromDictionaries();
                    function.parse(category.getId(), true, null);
                } else if (CategoryType.REGEX.equals(category.getType())) {
                    final UserDefinedClassifier udc = dictionarySnapshot != null ? dictionarySnapshot.getRegexClassifier()
                            : CategoryRegistryManager.getInstance().getRegexClassifier();
                    final String patternString = udc.getPatternStringByCategoryId(category.getId());
                    if (GenerateFromRegex.isValidPattern(patternString)) {
                        function = new GenerateFromRegex();
                        function.parse(patternString, true, null);
                    }
                }
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
        function.setRandom(new Random());
        return function;
    }

    private static Function<String> adaptForDateFunction(List<String> datePatterns, Function<Date> functionToAdapt,
            String extraParam) {
        functionToAdapt.parse(extraParam, true, null);
        Function<String> function = new DateFunctionAdapter(functionToAdapt, datePatterns);
        return function;
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
        Function<String> function = null;
        try {
            if (FunctionType.KEEP_YEAR.equals(functionType) || FunctionType.DATE_VARIANCE.equals(functionType)) {
                function = adaptForDateFunction(null,
                        (Function<Date>) factory.getFunction(functionType, tester.getTypeByName(dataType)), extraParam);
            } else if (FunctionType.NUMERIC_VARIANCE.equals(functionType)) {
                function = new FluctuateNumericString();
            } else if (FunctionType.REPLACE_ALL.equals(functionType)) {
                function = new ReplaceAll();
            } else {
                function = (Function<String>) factory.getFunction(functionType, tester.getTypeByName(dataType));
            }
            if (StringUtils.isNotEmpty(extraParam)) {
                function.parse(extraParam, true, null);
            }
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
}
