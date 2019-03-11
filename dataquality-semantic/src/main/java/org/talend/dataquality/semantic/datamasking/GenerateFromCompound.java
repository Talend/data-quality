// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import com.mifmif.common.regex.Generex;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.datamasking.functions.FunctionString;
import org.talend.dataquality.datamasking.semantic.ReplaceCharactersWithGeneration;
import org.talend.dataquality.semantic.Distribution;
import org.talend.dataquality.semantic.datamasking.model.CategoryValues;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.statistics.SemanticQualityAnalyzer;
import org.talend.dataquality.semantic.utils.RegexUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.talend.dataquality.semantic.datamasking.FunctionBuilder.functionInitializer;
import static org.talend.dataquality.semantic.model.CategoryType.DICT;
import static org.talend.dataquality.semantic.model.CategoryType.REGEX;

/**
 * data masking of a column with the content of compound semantic type
 */
public class GenerateFromCompound extends FunctionString {

    private Logger LOGGER = LoggerFactory.getLogger(GenerateFromCompound.class);

    private List<CategoryValues> categoryValues = null;

    private DictionarySnapshot dictionarySnapshot = null;

    private SemanticQualityAnalyzer analyzer = null;

    @Override
    protected String doGenerateMaskedField(String str, FunctionMode mode) {
        return super.doGenerateMaskedField(str, mode);
    }

    @Override
    protected String doGenerateMaskedField(String value) {
        return doGenerateMaskedFieldWithRandom(value, rnd);
    }

    @Override
    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        String result = EMPTY_STRING;
        analyzer = new SemanticQualityAnalyzer(dictionarySnapshot,
                new String[] { String.valueOf(categoryValues.stream().map(CategoryValues::getName).toArray()) });
        Optional<List<CategoryValues>> categoryValues = findMatchTypes(str);
        if (categoryValues.isPresent()) {
            Distribution distribution = processDistribution(categoryValues.get(), r);
            try {
                result = getMaskedValue(str, distribution, r);
            } catch (IllegalAccessException | InstantiationException e) {
                LOGGER.info(e.getMessage(), e);
            }
        } else {
            ReplaceCharactersWithGeneration function = new ReplaceCharactersWithGeneration();
            function.parse("X", true, null);
            function.setSeed(this.seed);
            result = function.generateMaskedRow(str, this.maskingMode);
        }

        return result;
    }

    private Optional<List<CategoryValues>> findMatchTypes(String value) {
        List<CategoryValues> categoryValuesResult = new ArrayList<>();
        Optional<List<CategoryValues>> result = Optional.empty();

        for (CategoryValues category : categoryValues) {
            if (analyzer.isValid(dictionarySnapshot.getDQCategoryByName(category.getName()), value))
                categoryValuesResult.add(category); //can't use stream because of cast
        }

        if (!categoryValuesResult.isEmpty())
            result = Optional.of(categoryValuesResult);

        return result;
    }

    @Override
    public void parse(String semanticCategoryId, boolean keepNullValues, Random rand) {
        setKeepNull(keepNullValues);
        setRandom(rand);
    }

    private Distribution processDistribution(List<CategoryValues> categories, Random r) {
        int largestDict;
        int nbElem;

        List<List<String>> values = new ArrayList<>();

        for (CategoryValues category : categories) {
            if (DICT.equals(category.getType())) {
                values.add((List<String>) category.getValue()); //can't use stream because of cast
            }
        }

        long nbRegex = categories.stream().filter(cat -> REGEX.equals(cat.getType())).count();
        if (!values.isEmpty()) {
            largestDict = values.stream().max(Comparator.comparing(List::size)).get().size();
            nbElem = (int) (values.stream().mapToInt(List::size).sum() + nbRegex * largestDict);
        } else {
            largestDict = 1;
            nbElem = (int) nbRegex;
        }

        List<Pair<String, Double>> probabilities = new ArrayList<>();

        categories.forEach(categoryValues -> {
            if (DICT.equals(categoryValues.getType()))
                probabilities.add(
                        new Pair(categoryValues.getCategoryId(), ((double) ((List) categoryValues.getValue()).size() / nbElem)));
            else if (REGEX.equals(categoryValues.getType()))
                probabilities.add(new Pair(categoryValues.getCategoryId(), (double) largestDict / nbElem));
        });

        return new Distribution(probabilities, r);
    }

    private String getMaskedValue(String value, Distribution distribution, Random r)
            throws IllegalAccessException, InstantiationException {
        String result = EMPTY_STRING;

        String key = (String) distribution.sample();
        CategoryValues cats = categoryValues.stream().filter(cat -> key.equals(cat.getCategoryId())).findAny().get();

        final MaskableCategoryEnum cat = MaskableCategoryEnum.getCategoryById(cats.getName());
        if (cat != null) { //specific masking
            Function<String> function = functionInitializer(cat);
            function.setSeed(seed);
            result = function.generateMaskedRow(value, maskingMode);
        } else {
            CategoryType type = cats.getType();
            if (DICT.equals(type)) {
                List values = (List) cats.getValue();
                result = (String) values.get(r.nextInt(values.size()));
            } else if (REGEX.equals(type)) {
                String regex = RegexUtils.removeStartingAndEndingAnchors((String) cats.getValue());
                Generex generex = new Generex(regex, r);
                result = generex.random();
            }
        }
        return result;
    }

    public void setCategoryValues(List<CategoryValues> categoryValues) {
        this.categoryValues = categoryValues;
        if (dictionarySnapshot != null)
            analyzer = new SemanticQualityAnalyzer(dictionarySnapshot,
                    new String[] { String.valueOf(categoryValues.stream().map(CategoryValues::getName).toArray()) });
    }

    public void setDictionarySnapshot(DictionarySnapshot dictionarySnapshot) {
        this.dictionarySnapshot = dictionarySnapshot;
    }
}
