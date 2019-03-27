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
package org.talend.dataquality.semantic.recognizer;

import static org.talend.dataquality.semantic.classifier.SemanticCategoryEnum.UNKNOWN;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.talend.dataquality.common.util.LFUCache;
import org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher;
import org.talend.dataquality.record.linkage.attribute.LevenshteinMatcher;
import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.classifier.impl.DataDictFieldClassifier;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.MainCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class DefaultCategoryRecognizer implements CategoryRecognizer {

    private final List<CategoryFrequency> catList = new ArrayList<>();

    private final Map<String, CategoryFrequency> categoryToFrequency = new HashMap<>();

    private final DataDictFieldClassifier dataDictFieldClassifier;

    private final UserDefinedClassifier userDefineClassifier;

    private final Map<String, DQCategory> metadata;

    private final LFUCache<String, Set<String>> knownCategoryCache = new LFUCache<String, Set<String>>(10, 1000, 0.01f);

    private long emptyCount = 0;

    private long total = 0;

    private AbstractAttributeMatcher defaultMatcher = new LevenshteinMatcher();

    private boolean fingerPrintApply = true;

    private boolean tokenizedApply = true;

    public DefaultCategoryRecognizer(DictionarySnapshot dictionarySnapshot) throws IOException {
        this(dictionarySnapshot.getSharedDataDict(), dictionarySnapshot.getCustomDataDict(), dictionarySnapshot.getKeyword(),
                dictionarySnapshot.getRegexClassifier(), dictionarySnapshot.getMetadata());
    }

    public DefaultCategoryRecognizer(final Index sharedDictionary, Index customDictionary, Index keyword,
            UserDefinedClassifier regex, Map<String, DQCategory> metadata) throws IOException {
        this.userDefineClassifier = regex;
        this.userDefineClassifier.getClassifiers().removeIf(classifier -> metadata.get(classifier.getId()) != null
                && Boolean.TRUE.equals(metadata.get(classifier.getId()).getDeleted()));
        this.metadata = metadata;

        final List<String> sharedCategories = new ArrayList<>();
        final List<String> customCategories = new ArrayList<>();
        for (DQCategory cat : metadata.values()) {
            if (!cat.getDeleted()) {
                if (cat.getModified()) {
                    customCategories.add(cat.getId());
                } else {
                    sharedCategories.add(cat.getId());
                }
            }
        }

        sharedDictionary.setCategoriesToSearch(sharedCategories);
        if (customDictionary != null) {
            customDictionary.setCategoriesToSearch(customCategories);
        }
        dataDictFieldClassifier = new DataDictFieldClassifier(sharedDictionary, customDictionary, keyword);
    }

    @Override
    public DataDictFieldClassifier getDataDictFieldClassifier() {
        return dataDictFieldClassifier;
    }

    @Override
    public UserDefinedClassifier getUserDefineClassifier() {
        return userDefineClassifier;
    }

    /**
     * @param data the input value
     * @return the set of its semantic categories
     */
    public Set<String> getSubCategorySet(String data) {
        if (data == null || StringUtils.EMPTY.equals(data.trim())) {
            emptyCount++;
            return new HashSet<>();
        }
        final Set<String> knownCategory = knownCategoryCache.get(data);
        if (knownCategory != null) {
            return knownCategory;
        }

        MainCategory mainCategory = MainCategory.getMainCategory(data);
        Set<String> subCategorySet = new HashSet<>();

        switch (mainCategory) {
        case Alpha:
        case Numeric:
        case AlphaNumeric:
            subCategorySet.addAll(dataDictFieldClassifier.classify(data));
            if (userDefineClassifier != null) {
                subCategorySet.addAll(userDefineClassifier.classify(data, mainCategory));
            }
            knownCategoryCache.put(data, subCategorySet);
            break;
        case NULL:
        case BLANK:
            emptyCount++;
            break;
        }
        return subCategorySet;
    }

    @Override
    public void prepare() {
        // dictionary.initIndex();
        // keyword.initIndex();
    }

    @Override
    public void reset() {
        catList.clear();
        categoryToFrequency.clear();
        total = 0;
        emptyCount = 0;
        knownCategoryCache.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataquality.semantic.recognizer.CategoryRecognizer#process(java.lang.String)
     */
    @Override
    public String[] process(String data) {
        Set<String> ids = getSubCategorySet(data);
        Map<String, Integer> categoryToLevel = new HashMap<>();
        List<String> categories = new ArrayList<>();
        if (!ids.isEmpty()) {
            for (String id : ids) {
                categoryToLevel.put(id, 0);
                DQCategory meta = metadata.get(id);
                if (meta != null) {
                    if (!CollectionUtils.isEmpty(meta.getParents()))
                        incrementAncestorsCategories(categoryToLevel, id);
                }
            }
            for (Map.Entry<String, Integer> entry : categoryToLevel.entrySet()) {
                DQCategory meta = metadata.get(entry.getKey());
                if (meta != null) {
                    categories.add(meta.getName());
                    incrementCategory(meta.getName(), meta.getLabel(), entry.getValue());
                }
            }
        } else {
            incrementCategory(StringUtils.EMPTY);
        }
        total++;
        return categories.toArray(new String[categories.size()]);
    }

    /**
     * For the discovery, if a category c matches with the data,
     * it means all the ancestor categories of c have to match too.
     * This method increments the ancestor categories of c.
     *
     * @param categoryToLevel, the category result
     * @param id, the category ID of the matched category c
     * @param categoryToLevel
     *
     */
    private void incrementAncestorsCategories(Map<String, Integer> categoryToLevel, String id) {
        Deque<String> catToSee = new ArrayDeque<>();
        catToSee.add(id);
        String currentCategory;
        while (!catToSee.isEmpty()) {
            currentCategory = catToSee.pop();
            DQCategory dqCategory = metadata.get(currentCategory);
            Integer categoryLevel = categoryToLevel.get(currentCategory);
            if (dqCategory != null && !CollectionUtils.isEmpty(dqCategory.getParents())) {
                for (DQCategory parent : dqCategory.getParents()) {
                    String parentId = parent.getId();
                    Integer level = categoryToLevel.get(parentId);
                    if (level == null || level < categoryLevel + 1) {
                        categoryToLevel.put(parentId, categoryLevel + 1);
                        catToSee.add(parentId);
                    }
                }
            }
        }
    }

    private void incrementCategory(String categoryName) {
        incrementCategory(categoryName, categoryName);
    }

    private void incrementCategory(String categoryName, String categoryLabel) {
        incrementCategory(categoryName, categoryLabel, 0);

    }

    private void incrementCategory(String categoryName, String categoryLabel, int categoryLevel) {
        CategoryFrequency c = categoryToFrequency.get(categoryName);
        if (c == null) {
            c = new CategoryFrequency(categoryName, categoryLabel, categoryLevel);
            categoryToFrequency.put(categoryName, c);
            catList.add(c);
        }
        c.count++;

    }

    @Deprecated
    public Collection<CategoryFrequency> getResult() {
        for (CategoryFrequency category : categoryToFrequency.values()) {
            category.score = Math.round(category.count * 10000 / total) / 100F;
        }

        Collections.sort(catList, Collections.reverseOrder());
        return catList;
    }

    @Override
    public Collection<CategoryFrequency> getResult(String columnName, float weight) {
        for (CategoryFrequency category : categoryToFrequency.values()) {
            category.score = category.count * 100F / total;

            if (tokenizedApply) {
                defaultMatcher.setTokenMethod(TokenizedResolutionMethod.ANYORDER);
            }
            defaultMatcher.setFingerPrintApply(fingerPrintApply);
            float scoreOnHeader = 0;
            if (columnName != null && !UNKNOWN.getDisplayName().equals(category.getCategoryName()))
                scoreOnHeader = Double.valueOf(defaultMatcher.getMatchingWeight(columnName, category.getCategoryName()))
                        .floatValue();
            if (scoreOnHeader > 0.7)
                category.score += scoreOnHeader * weight * 100;
            category.score = Math.min(Math.round(category.score * 100) / 100F, 100);
        }

        Collections.sort(catList, Collections.reverseOrder());
        return catList;
    }

    @Override
    public void end() {
        dataDictFieldClassifier.closeIndex();
        knownCategoryCache.clear();
    }

    public void setDefaultMatcher(AbstractAttributeMatcher defaultMatcher) {
        this.defaultMatcher = defaultMatcher;
    }

    public void setFingerPrintApply(boolean fingerPrintApply) {
        this.fingerPrintApply = fingerPrintApply;
    }

    public void setTokenizedApply(boolean tokenizedApply) {
        this.tokenizedApply = tokenizedApply;
    }

}
