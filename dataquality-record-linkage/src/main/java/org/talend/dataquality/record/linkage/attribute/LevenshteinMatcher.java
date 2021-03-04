// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.attribute;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class LevenshteinMatcher extends AbstractAttributeMatcher {

    private static final long serialVersionUID = -6555766042386408458L;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getWeight(java.lang.String,
     * java.lang.String)
     */
    @Override
    public double getWeight(String str1, String str2) {
        // get the max possible levenstein distance score for string
        int maxLen = Math.max(str1.length(), str2.length());

        // check for 0 maxLen
        if (maxLen == 0) {
            return 1.0; // as both strings identically zero length
        } else {
            final int levenshteinDistance = StringUtils.getLevenshteinDistance(str1, str2);
            // return actual / possible levenstein distance to get 0-1 range
            return 1.0 - ((double) levenshteinDistance / maxLen);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    @Override
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.LEVENSHTEIN;
    }

}
