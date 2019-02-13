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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.common.inference.QualityAnalyzer;
import org.talend.dataquality.common.inference.ResizableList;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.semantic.classifier.ISubCategoryClassifier;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.impl.DataDictFieldClassifier;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.DefaultCategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.LFUCache;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class SemanticQualityAnalyzer extends QualityAnalyzer<ValueQualityStatistics, String[]> {

    private static final long serialVersionUID = -5951511723860660263L;

    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticQualityAnalyzer.class);

    private final ResizableList<ValueQualityStatistics> results = new ResizableList<>(ValueQualityStatistics.class);

    private final Map<String, LFUCache<String, Boolean>> knownValidationCategoryCache = new HashMap<>();

    private DictionarySnapshot dictionarySnapshot;

    private ISubCategoryClassifier regexClassifier;

    private ISubCategoryClassifier dataDictClassifier;

    public SemanticQualityAnalyzer(DictionarySnapshot dictionarySnapshot, String[] types, boolean isStoreInvalidValues) {
        this.dictionarySnapshot = dictionarySnapshot;
        this.isStoreInvalidValues = isStoreInvalidValues;
        init();
        setTypes(types);
    }

    public SemanticQualityAnalyzer(DictionarySnapshot dictionarySnapshot, String... types) {
        this(dictionarySnapshot, types, false);
    }

    @Override
    public void setTypes(String[] types) {
        List<String> idList = new ArrayList<>();
        for (String type : types) {
            DQCategory dqCat = null;
            for (DQCategory tmpCat : dictionarySnapshot.getMetadata().values()) {
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
            final CategoryRecognizer recognizer = new DefaultCategoryRecognizer(dictionarySnapshot);
            regexClassifier = recognizer.getUserDefineClassifier();
            dataDictClassifier = recognizer.getDataDictFieldClassifier();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        results.clear();
    }

    @Override
    public void setStoreInvalidValues(boolean isStoreInvalidValues) {
        this.isStoreInvalidValues = isStoreInvalidValues;
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
        DQCategory category = dictionarySnapshot.getMetadata().get(catId);
        if (category == null) {
            valueQuality.incrementValid();
            return;
        }
        if (category.getCompleteness() != null && category.getCompleteness().booleanValue()) {
            if (!Boolean.TRUE.equals(category.getDeleted()) && isValid(category, value)) {
                valueQuality.incrementValid();
            } else {
                valueQuality.incrementInvalid();
                processInvalidValue(valueQuality, value);
            }
        } else {
            valueQuality.incrementValid();
        }
    }

    /**
     * 
     * Judge whether or not input value is valid
     * 
     * @param category category of input value
     * @param value input value
     * @return true if input value is valid esle false
     */
    public boolean isValid(DQCategory category, String value) {
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
            if (CategoryType.DICT.equals(cat.getType())) {
                dictChildrenCategories.add(cat);
            } else if (CategoryType.REGEX.equals(cat.getType())) {
                regexChildrenCategories.add(cat);
            }
        }

        if (!CollectionUtils.isEmpty(regexChildrenCategories)) {
            validCat = regexClassifier.validCategories(value, category, regexChildrenCategories);
        }
        if (!validCat && !CollectionUtils.isEmpty(dictChildrenCategories)) {
            validCat = dataDictClassifier.validCategories(value, category, dictChildrenCategories);
        }
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
            DQCategory dqCategory = dictionarySnapshot.getMetadata().get(currentCategory);
            if (dqCategory != null) {
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
    public void close() throws Exception {
        ((DataDictFieldClassifier) dataDictClassifier).closeIndex();
    }

}
