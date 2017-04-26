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
package org.talend.dataquality.semantic.statistics;

import java.io.IOException;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.QualityAnalyzer;
import org.talend.dataquality.common.inference.ResizableList;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.ISubCategoryClassifier;
import org.talend.dataquality.semantic.classifier.impl.DataDictFieldClassifier;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.ValidationMode;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.dataquality.semantic.recognizer.LFUCache;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class SemanticQualityAnalyzer extends QualityAnalyzer<ValueQualityStatistics, String[]> {

    private static final long serialVersionUID = -5951511723860660263L;

    private static final Logger LOG = Logger.getLogger(SemanticQualityAnalyzer.class);

    private final ResizableList<ValueQualityStatistics> results = new ResizableList<>(ValueQualityStatistics.class);

    private final Map<String, LFUCache<String, Boolean>> knownValidationCategoryCache = new HashMap<>();

    private ISubCategoryClassifier regexClassifier;

    private ISubCategoryClassifier dataDictClassifier;

    private CategoryRegistryManager crm;

    private final CategoryRecognizerBuilder builder;

    public SemanticQualityAnalyzer(CategoryRecognizerBuilder builder, String[] types, boolean isStoreInvalidValues) {
        this.isStoreInvalidValues = isStoreInvalidValues;
        this.builder = builder;
        setTypes(types);
        init();
    }

    public SemanticQualityAnalyzer(CategoryRecognizerBuilder builder, String... types) {
        this(builder, types, false);
    }

    @Override
    public void init() {
        try {
            final CategoryRecognizer categoryRecognizer = builder.build();
            regexClassifier = categoryRecognizer.getUserDefineClassifier();
            dataDictClassifier = categoryRecognizer.getDataDictFieldClassifier();
            crm = categoryRecognizer.getCrm();
        } catch (IOException e) {
            LOG.error(e, e);
        }
        results.clear();
    }

    @Override
    public void setStoreInvalidValues(boolean isStoreInvalidValues) {
        this.isStoreInvalidValues = isStoreInvalidValues;
    }

    /**
     * @deprecated use {@link #analyze(String...)}
     * <p>
     * TODO remove this method later
     * 
     * Analyze record of Array of string type, this method is used in scala library which not support parameterized
     * array type.
     * 
     * @param record
     * @return
     */
    @Deprecated
    public boolean analyzeArray(String[] record) {
        return analyze(record);
    }

    /**
     * TODO use String[] as parameter for this method.
     */
    @Override
    public boolean analyze(String... record) {
        if (record == null) {
            results.resize(0);
            return true;
        }
        results.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            String semanticType = getTypes()[i];
            final String value = record[i];
            final ValueQualityStatistics valueQuality = results.get(i);
            if (value == null || value.trim().length() == 0) {
                valueQuality.incrementEmpty();
            } else {
                analyzeValue(semanticType, value, valueQuality);
            }
        }
        return true;
    }

    private void analyzeValue(String catName, String value, ValueQualityStatistics valueQuality) {
        String catId = null;
        DQCategory cat = null;
        for (String id : CategoryRegistryManager.getInstance().getCategoryIds()) {
            DQCategory tmp = CategoryRegistryManager.getInstance().getCategoryMetadataByName(id);
            if (catName.equals(tmp.getName())) {
                catId = id;
                cat = tmp;
                break;
            }
        }
        if (cat == null) {
            valueQuality.incrementValid();
            return;
        }
        if (cat.getCompleteness() != null && cat.getCompleteness().booleanValue()) {
            if (isValid(catId, cat.getType(), value)) {
                valueQuality.incrementValid();
            } else {
                valueQuality.incrementInvalid();
                processInvalidValue(valueQuality, value);
            }
        } else {
            valueQuality.incrementValid();
        }
    }

    private boolean isValid(String catId, CategoryType catType, String value) {
        LFUCache<String, Boolean> categoryCache = knownValidationCategoryCache.get(catId);

        if (categoryCache == null) {
            categoryCache = new LFUCache<String, Boolean>(10, 1000, 0.01f);
            knownValidationCategoryCache.put(catId, categoryCache);
        } else {
            final Boolean isValid = categoryCache.get(value);
            if (isValid != null) {
                return isValid;
            }
        }
        boolean validCat = false;
        ValidationMode validationMode = ValidationMode.EXACT;
        DQCategory dqCategory = crm.getCategoryMetadataByName(catId);
        if (dqCategory != null && dqCategory.getValidationMode() != null)
            validationMode = dqCategory.getValidationMode();
        switch (catType) {
        case REGEX:
            validCat = regexClassifier.validCategories(value, catId, null, validationMode);
            break;
        case DICT:

            validCat = dataDictClassifier.validCategories(value, catId, null, validationMode);
            break;
        case COMPOUND:
            Map<CategoryType, Set<String>> children = getChildrenCategories(catId);
            validCat = regexClassifier.validCategories(value, catId, children.get(CategoryType.REGEX), validationMode);
            if (!validCat)
                validCat = dataDictClassifier.validCategories(value, catId, children.get(CategoryType.DICT), validationMode);
            break;
        default:
            break;
        }
        categoryCache.put(value, validCat);
        return validCat;
    }

    private void processInvalidValue(ValueQualityStatistics valueQuality, String invalidValue) {
        if (isStoreInvalidValues) {
            valueQuality.appendInvalidValue(invalidValue);
        }
    }

    private Map<CategoryType, Set<String>> getChildrenCategories(String id) {
        Deque<String> catToSee = new ArrayDeque<>();
        Set<String> catAlreadySeen = new HashSet<>();
        Map<CategoryType, Set<String>> children = new HashMap<>();
        children.put(CategoryType.REGEX, new HashSet<String>());
        children.put(CategoryType.DICT, new HashSet<String>());
        catToSee.add(id);
        String currentCategory;
        while (!catToSee.isEmpty()) {
            currentCategory = catToSee.pop();
            DQCategory dqCategory = crm.getCategoryMetadataByName(currentCategory);
            if (dqCategory != null)
                if (!CollectionUtils.isEmpty(dqCategory.getChildren())) {
                    for (DQCategory child : dqCategory.getChildren()) {
                        if (!catAlreadySeen.contains(child.getId())) {
                            catAlreadySeen.add(child.getId());
                            catToSee.add(child.getId());
                        }
                    }
                } else if (!currentCategory.equals(id)) {
                    children.get(dqCategory.getType()).add(currentCategory);
                }
        }
        return children;
    }

    @Override
    public void end() {
        // do some finalized thing at here.
    }

    @Override
    public List<ValueQualityStatistics> getResult() {
        return results;
    }

    @Override
    public Analyzer<ValueQualityStatistics> merge(Analyzer<ValueQualityStatistics> analyzer) {
        int idx = 0;
        SemanticQualityAnalyzer mergedValueQualityAnalyze = new SemanticQualityAnalyzer(this.builder, getTypes());
        ((ResizableList<ValueQualityStatistics>) mergedValueQualityAnalyze.getResult()).resize(results.size());
        for (ValueQualityStatistics qs : results) {
            ValueQualityStatistics mergedStats = mergedValueQualityAnalyze.getResult().get(idx);
            ValueQualityStatistics anotherStats = analyzer.getResult().get(idx);
            mergedStats.setValidCount(qs.getValidCount() + anotherStats.getValidCount());
            mergedStats.setInvalidCount(qs.getInvalidCount() + anotherStats.getInvalidCount());
            mergedStats.setEmptyCount(qs.getEmptyCount() + anotherStats.getEmptyCount());
            if (!qs.getInvalidValues().isEmpty()) {
                mergedStats.getInvalidValues().addAll(qs.getInvalidValues());
            }
            if (!anotherStats.getInvalidValues().isEmpty()) {
                mergedStats.getInvalidValues().addAll(anotherStats.getInvalidValues());
            }
            idx++;
        }
        return mergedValueQualityAnalyze;
    }

    @Override
    public void close() throws Exception {
        ((DataDictFieldClassifier) dataDictClassifier).closeIndex();
    }
}
