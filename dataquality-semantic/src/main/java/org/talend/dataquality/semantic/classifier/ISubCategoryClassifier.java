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
package org.talend.dataquality.semantic.classifier;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by sizhaoliu on 16.03.15.
 */
public interface ISubCategoryClassifier extends Serializable {

    /**
     * Return a set of category IDs
     * 
     * @param str the string that helps to classify
     * @return the category IDs found for this string
     */
    public Set<String> classify(String str);

    boolean validCategory(String value, String semanticType);
}
