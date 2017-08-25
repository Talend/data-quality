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

import java.io.IOException;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.classifier.impl.DataDictFieldClassifier;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.MainCategory;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
class DefaultCategoryRecognizer implements CategoryRecognizer {

    private final List<CategoryFrequency> catList = new ArrayList<>();

    private final Map<String, CategoryFrequency> categoryToFrequency = new HashMap<>();

    private final DataDictFieldClassifier dataDictFieldClassifier;

    private final UserDefinedClassifier userDefineClassifier;

    private final Map<String, DQCategory> metadata;

    private final LFUCache<String, Set<String>> knownCategoryCache = new LFUCache<String, Set<String>>(10, 1000, 0.01f);

    private long emptyCount = 0;

    private long total = 0;

    public DefaultCategoryRecognizer(Index dictionary, Index keyword, UserDefinedClassifier regex,
            Map<String, DQCategory> metadata) throws IOException {
        dataDictFieldClassifier = new DataDictFieldClassifier(dictionary, keyword);
        this.userDefineClassifier = regex;
        this.metadata = metadata;
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

    @Override
    public Collection<CategoryFrequency> getResult() {
        for (CategoryFrequency category : categoryToFrequency.values()) {
            category.frequency = Math.round(category.count * 10000 / total) / 100F;
        }

        Collections.sort(catList, Collections.reverseOrder());
        return catList;
    }

    @Override
    public void end() {
        dataDictFieldClassifier.closeIndex();
        knownCategoryCache.clear();
    }
}
