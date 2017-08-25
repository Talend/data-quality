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
import org.talend.dataquality.semantic.classifier.ISubCategoryClassifier;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.impl.DataDictFieldClassifier;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
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

    private final CategoryRecognizerBuilder builder;

    private ISubCategoryClassifier regexClassifier;

    private ISubCategoryClassifier dataDictClassifier;

    private Map<String, DQCategory> metadata;

    public SemanticQualityAnalyzer(CategoryRecognizerBuilder builder, String[] types, boolean isStoreInvalidValues) {
        this.isStoreInvalidValues = isStoreInvalidValues;
        this.builder = builder;
        init();
        setTypes(types);
    }

    public SemanticQualityAnalyzer(CategoryRecognizerBuilder builder, String... types) {
        this(builder, types, false);
    }

    @Override
    public void setTypes(String[] types) {
        List<String> idList = new ArrayList<>();
        for (String type : types) {
            DQCategory dqCat = null;
            for (DQCategory tmpCat : metadata.values()) {
                if (type.equals(tmpCat.getName())) {
                    tmpCat.setChildren(getChildrenCategories(tmpCat.getId()));
                    dqCat = tmpCat;
                    break;
                }
            }
            if (dqCat == null) {
                idList.add(SemanticCategoryEnum.UNKNOWN.name());
            } else {
                idList.add(dqCat.getId());
            }
        }
        super.setTypes(idList.toArray(new String[idList.size()]));
    }

    @Override
    public void init() {
        try {
            final CategoryRecognizer categoryRecognizer = builder.build();
            regexClassifier = categoryRecognizer.getUserDefineClassifier();
            dataDictClassifier = categoryRecognizer.getDataDictFieldClassifier();
            metadata = builder.getCategoryMetadata();
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

    private void analyzeValue(String catId, String value, ValueQualityStatistics valueQuality) {
        DQCategory category = metadata.get(catId);
        if (category == null) {
            valueQuality.incrementValid();
            return;
        }
        if (category.getCompleteness() != null && category.getCompleteness().booleanValue()) {
            if (isValid(category, value)) {
                valueQuality.incrementValid();
            } else {
                valueQuality.incrementInvalid();
                processInvalidValue(valueQuality, value);
            }
        } else {
            valueQuality.incrementValid();
        }
    }

    private boolean isValid(DQCategory category, String value) {
        LFUCache<String, Boolean> categoryCache = knownValidationCategoryCache.get(category.getId());

        if (categoryCache == null) {
            categoryCache = new LFUCache<String, Boolean>(10, 1000, 0.01f);
            knownValidationCategoryCache.put(category.getId(), categoryCache);
        } else {
            final Boolean isValid = categoryCache.get(value);
            if (isValid != null) {
                return isValid;
            }
        }
        boolean validCat = false;
        switch (category.getType()) {
        case REGEX:
            validCat = regexClassifier.validCategories(value, category, null);
            break;
        case DICT:
            validCat = dataDictClassifier.validCategories(value, category, null);
            break;
        case COMPOUND:
            validCat = isCompoundValid(category, value);
            break;
        default:
            break;
        }
        categoryCache.put(value, validCat);
        return validCat;
    }

    private boolean isCompoundValid(DQCategory category, String value) {
        boolean validCat = false;
        Set<DQCategory> regexChildrenCategories = new HashSet<>();
        Set<DQCategory> dictChildrenCategories = new HashSet<>();
        for (DQCategory cat : category.getChildren()) {
            if (CategoryType.DICT.equals(cat.getType()))
                dictChildrenCategories.add(cat);
            else if (CategoryType.REGEX.equals(cat.getType()))
                regexChildrenCategories.add(cat);
        }

        if (!CollectionUtils.isEmpty(regexChildrenCategories))
            validCat = regexClassifier.validCategories(value, category, regexChildrenCategories);
        if (!validCat && !CollectionUtils.isEmpty(dictChildrenCategories))
            validCat = dataDictClassifier.validCategories(value, category, dictChildrenCategories);
        return validCat;
    }

    private void processInvalidValue(ValueQualityStatistics valueQuality, String invalidValue) {
        if (isStoreInvalidValues) {
            valueQuality.appendInvalidValue(invalidValue);
        }
    }

    /**
     * For the validation of a COMPOUND category, we only have to valid the leaves children categories.
     * This methods find the DICT children categories and the REGEX children categories.
     * 
     * @param id, the category from we search the children
     * @return the DICT children categories and the REGEX children categories with a map.
     */
    private List<DQCategory> getChildrenCategories(String id) {
        Deque<String> catToSee = new ArrayDeque<>();
        Set<String> catAlreadySeen = new HashSet<>();
        List<DQCategory> children = new ArrayList<>();

        catToSee.add(id);
        String currentCategory;
        while (!catToSee.isEmpty()) {
            currentCategory = catToSee.pop();
            DQCategory dqCategory = metadata.get(currentCategory);
            if (dqCategory != null)
                if (!CollectionUtils.isEmpty(dqCategory.getChildren())) {
                    for (DQCategory child : dqCategory.getChildren()) {
                        if (!catAlreadySeen.contains(child.getId())) {
                            catAlreadySeen.add(child.getId());
                            catToSee.add(child.getId());
                        }
                    }
                } else if (!currentCategory.equals(id)) {
                    children.add(dqCategory);
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
