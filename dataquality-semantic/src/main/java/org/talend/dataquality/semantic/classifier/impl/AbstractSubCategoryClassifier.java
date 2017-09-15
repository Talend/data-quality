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

import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.ISubCategoryClassifier;
import org.talend.dataquality.semantic.filter.ISemanticFilter;
import org.talend.dataquality.semantic.validator.ISemanticValidator;

/**
 * Created by sizhaoliu on 20/03/15.
 */
public abstract class AbstractSubCategoryClassifier implements ISubCategoryClassifier {

    private static final long serialVersionUID = -5396224246178196818L;

    protected Set<ISubCategory> potentialSubCategories = new HashSet<>();

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.semantic.classifier.ISubCategoryClassifier#classify(java.lang.String)
     */
    @Override
    public Set<String> classify(String data) {
        Set<String> catSet = new HashSet<>();
        for (ISubCategory classifier : potentialSubCategories) {
            ISemanticFilter filter = classifier.getFilter();

            if (filter != null && !filter.isQualified(data)) {
                continue;
            }
            ISemanticValidator validator = classifier.getValidator();
            if (validator != null && validator.isValid(data)) {
                catSet.add(classifier.getName());
            }
        }
        return catSet;
    }

    public Set<ISubCategory> getClassifiers() {
        return potentialSubCategories;
    }

    public void setClassifiers(Set<ISubCategory> classifiers) {
        this.potentialSubCategories = classifiers;
    }

}
