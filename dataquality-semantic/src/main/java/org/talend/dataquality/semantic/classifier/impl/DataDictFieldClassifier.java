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

    private final int MAX_TOKEN_FOR_KEYWORDS = 3;

    private Index sharedDictionary;

    private Index customDictionary;

    private Index keyword;

    public DataDictFieldClassifier(Index sharedDictionary, Index customDictionary, Index keyword) {
        this.sharedDictionary = sharedDictionary;
        this.customDictionary = customDictionary;
        this.keyword = keyword;
    }

    @Override
    public Set<String> classify(String data) {
        StringTokenizer t = new StringTokenizer(data, " ");
        final int tokenCount = t.countTokens();

        HashSet<String> result = new HashSet<>();

        result.addAll(sharedDictionary.findCategories(data));
        if (customDictionary != null) {
            result.addAll(customDictionary.findCategories(data));
        }
        if (tokenCount >= MAX_TOKEN_FOR_KEYWORDS) {
            result.addAll(keyword.findCategories(data));
        }

        return result;
    }

    @Override
    public boolean validCategories(String data, DQCategory semanticType, Set<DQCategory> children) {
        boolean isValid = false;
        if (CollectionUtils.isEmpty(children)) { // if there are no children, it's easy
            if (!Boolean.TRUE.equals(semanticType.getDeleted())) {
                if (Boolean.TRUE.equals(semanticType.getModified()))
                    isValid = customDictionary.validCategories(data, semanticType, children);
                else
                    isValid = sharedDictionary.validCategories(data, semanticType, children);
            }
        } else {
            Set<DQCategory> customChildren = new HashSet<>();
            Set<DQCategory> sharedChildren = new HashSet<>();
            // we split the children in the custom categories and the shared categories
            children.forEach(child -> {
                if (!Boolean.TRUE.equals(child.getDeleted())) {
                    if (Boolean.TRUE.equals(child.getModified()))
                        customChildren.add(child);
                    else
                        sharedChildren.add(child);
                }
            });
            // we look for in each dictionary
            if (CollectionUtils.isNotEmpty(customChildren))
                isValid = customDictionary.validCategories(data, semanticType, customChildren);
            if (!isValid && CollectionUtils.isNotEmpty(sharedChildren))
                isValid = sharedDictionary.validCategories(data, semanticType, sharedChildren);
        }
        return isValid;
    }

    public void closeIndex() {
        sharedDictionary.closeIndex();
        if (customDictionary != null) {
            customDictionary.closeIndex();
        }
        keyword.closeIndex();
    }

}
