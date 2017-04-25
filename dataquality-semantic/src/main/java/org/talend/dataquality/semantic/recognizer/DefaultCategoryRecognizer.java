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
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
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

    private final LFUCache<String, Set<String>> knownCategoryCache = new LFUCache<String, Set<String>>(10, 1000, 0.01f);

    private long emptyCount = 0;

    private long total = 0;

    private CategoryRegistryManager crm;

    public DefaultCategoryRecognizer(Index dictionary, Index keyword) throws IOException {
        this(dictionary, keyword, CategoryRegistryManager.getInstance().getRegexClassifier(true));
    }

    public DefaultCategoryRecognizer(Index dictionary, Index keyword, UserDefinedClassifier regex) throws IOException {
        dataDictFieldClassifier = new DataDictFieldClassifier(dictionary, keyword);
        this.userDefineClassifier = regex;
        crm = CategoryRegistryManager.getInstance();
    }

    @Override
    public DataDictFieldClassifier getDataDictFieldClassifier() {
        return dataDictFieldClassifier;
    }

    @Override
    public UserDefinedClassifier getUserDefineClassifier() {
        return userDefineClassifier;
    }

    @Override
    public CategoryRegistryManager getCrm() {
        return crm;
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
        case AlphaNumeric:
            subCategorySet.addAll(dataDictFieldClassifier.classify(data));
            if (userDefineClassifier != null) {
                subCategorySet.addAll(userDefineClassifier.classify(data, mainCategory));
            }
            knownCategoryCache.put(data, subCategorySet);
            break;
        case Numeric:
            if (userDefineClassifier != null) {
                subCategorySet.addAll(userDefineClassifier.classify(data, mainCategory));
            }
            knownCategoryCache.put(data, subCategorySet);
            break;
        case NULL:
        case BLANK:
            emptyCount++;
            break;
        case UNKNOWN:
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
        Set<String> categories = new HashSet<>();
        if (!ids.isEmpty()) {
            for (String id : ids) {
                DQCategory meta = crm.getCategoryMetadataById(id);
                if (meta != null) {
                    incrementCategory(meta.getName(), meta.getLabel());
                    incrementAncestorsCategories(id);
                    categories.add(meta.getName());
                }
            }
        } else {
            incrementCategory(StringUtils.EMPTY);
        }
        total++;
        return categories.toArray(new String[categories.size()]);
    }

    private void incrementAncestorsCategories(String id) {
        Deque<String> catToSee = new ArrayDeque<>();
        Set<String> catAlreadySeen = new HashSet<>();
        catToSee.add(id);
        String currentCategory;
        while (!catToSee.isEmpty()) {
            currentCategory = catToSee.pop();
            DQCategory dqCategory = crm.getCategoryMetadataById(currentCategory);
            if (dqCategory != null && !CollectionUtils.isEmpty(dqCategory.getParents()))
                for (DQCategory parent : dqCategory.getParents()) {
                    if (!catAlreadySeen.contains(parent.getId())) {
                        catAlreadySeen.add(parent.getId());
                        catToSee.add(parent.getId());
                        DQCategory meta = crm.getCategoryMetadataByName(parent.getId());
                        if (meta != null)
                            incrementCategory(meta.getName(), meta.getLabel());
                    }
                }
        }
    }

    private void incrementCategory(String catId) {
        incrementCategory(catId, catId);
    }

    private void incrementCategory(String catId, String catName) {
        CategoryFrequency c = categoryToFrequency.get(catId);
        if (c == null) {
            c = new CategoryFrequency(catId, catName);
            categoryToFrequency.put(catId, c);
            catList.add(c);
        }
        c.count++;

    }

    @Override
    public Collection<CategoryFrequency> getResult() {
        for (CategoryFrequency category : categoryToFrequency.values()) {
            category.frequency = Math.round(category.count * 10000 / total) / 100F;
        }

        Collections.sort(catList, new Comparator<CategoryFrequency>() {

            @Override
            public int compare(CategoryFrequency o1, CategoryFrequency o2) {
                // The EMPTY category must always be ranked after the others
                if ("".equals(o1.categoryName)) {
                    return 1;
                } else if ("".equals(o2.categoryName)) {
                    return -1;
                }
                return (int) (o2.count - o1.count);
            }
        });
        return catList;
    }

    @Override
    public void end() {
        dataDictFieldClassifier.closeIndex();
        knownCategoryCache.clear();
    }
}
