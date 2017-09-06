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
package org.talend.dataquality.semantic.classifier.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.collections.CollectionUtils;
import org.talend.dataquality.semantic.classifier.ISubCategoryClassifier;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.model.DQCategory;

/**
 * Created by sizhaoliu on 27/03/15.
 */
public class DataDictFieldClassifier implements ISubCategoryClassifier {

    private static final long serialVersionUID = 6174669848299972111L;

    private Index sharedDictionary;

    private Index dictionary;

    private Index keyword;

    private final int MAX_TOKEN_FOR_KEYWORDS = 3;

    public DataDictFieldClassifier(Index sharedDictionary, Index dictionary, Index keyword) {
        this.sharedDictionary = sharedDictionary;
        this.dictionary = dictionary;
        this.keyword = keyword;
    }

    @Override
    public Set<String> classify(String data, List<String> sharedCategories, List<String> tenantCategories) {
        StringTokenizer t = new StringTokenizer(data, " ");
        final int tokenCount = t.countTokens();

        HashSet<String> result = new HashSet<>();
        // if it's a valid syntactic data --> search in DD
        if (tokenCount < MAX_TOKEN_FOR_KEYWORDS) {
            if (CollectionUtils.isNotEmpty(tenantCategories))
                result.addAll(dictionary.findCategories(data, tenantCategories));
            if (CollectionUtils.isNotEmpty(sharedCategories))
                result.addAll(sharedDictionary.findCategories(data, sharedCategories));
        } else {
            if (CollectionUtils.isNotEmpty(tenantCategories))
                result.addAll(dictionary.findCategories(data, tenantCategories));
            if (CollectionUtils.isNotEmpty(sharedCategories))
                result.addAll(sharedDictionary.findCategories(data, sharedCategories));
            result.addAll(keyword.findCategories(data, tenantCategories));
        }

        return result;
    }

    @Override
    public boolean validCategories(String data, DQCategory semanticType, Set<DQCategory> children) {
        if (Boolean.TRUE.equals(semanticType.isModified()))
            return dictionary.validCategories(data, semanticType, children);
        return sharedDictionary.validCategories(data, semanticType, children);
    }

    public void closeIndex() {
        dictionary.closeIndex();
        keyword.closeIndex();
    }

}
