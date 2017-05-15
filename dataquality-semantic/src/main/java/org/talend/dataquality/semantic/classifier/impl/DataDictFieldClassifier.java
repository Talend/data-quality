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

import org.talend.dataquality.semantic.classifier.ISubCategoryClassifier;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.model.DQCategory;

/**
 * Created by sizhaoliu on 27/03/15.
 */
public class DataDictFieldClassifier implements ISubCategoryClassifier {

    private static final long serialVersionUID = 6174669848299972111L;

    private Index dictionary;

    private Index keyword;

    private final int MAX_TOKEN_FOR_KEYWORDS = 3;

    public DataDictFieldClassifier(Index dictionary, Index keyword) {
        this.dictionary = dictionary;
        this.keyword = keyword;
    }

    @Override
    public Set<String> classify(String data) {
        StringTokenizer t = new StringTokenizer(data, " ");
        final int tokenCount = t.countTokens();

        HashSet<String> result = new HashSet<>();
        // if it's a valid syntactic data --> search in DD
        if (tokenCount < MAX_TOKEN_FOR_KEYWORDS) {
            result.addAll(dictionary.findCategories(data));
        } else {
            result.addAll(dictionary.findCategories(data));
            result.addAll(keyword.findCategories(data));
        }

        return result;
    }

    @Override
    public boolean validCategories(String data, DQCategory semanticType, Set<DQCategory> children) {
        return dictionary.validCategories(data, semanticType, children);
    }

    public void closeIndex() {
        dictionary.closeIndex();
        keyword.closeIndex();
    }

}
