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
package org.talend.dataquality.record.linkage.attribute;

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.StringComparisonUtil;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class JaroMatcher extends AbstractAttributeMatcher {

    private static final long serialVersionUID = 3894610803291924363L;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    @Override
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.JARO;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getWeight(java.lang.String,
     * java.lang.String)
     */
    @Override
    public double getWeight(String string1, String string2) {
        int str1CPCount = string1.codePointCount(0, string1.length());
        int str2CPCount = string2.codePointCount(0, string2.length());

        // get half the length of the string rounded up - (this is the distance used for acceptable transpositions)
        final int halflen = ((Math.min(str1CPCount, str2CPCount)) / 2) + ((Math.min(str1CPCount, str2CPCount)) % 2);

        // get common characters
        final StringBuilder common1 = StringComparisonUtil.getCommonCharacters(string1, string2, halflen);
        final StringBuilder common2 = StringComparisonUtil.getCommonCharacters(string2, string1, halflen);

        // check for zero in common
        if (common1.length() == 0 || common2.length() == 0) {
            return 0.0f;
        }

        // check for same length common strings returning 0.0f is not the same
        if (common1.codePoints().count() != common2.codePoints().count()) {
            return 0.0f;
        }
        int common1CPCount = common1.codePointCount(0, common1.length());
        int common2CPCount = common2.codePointCount(0, common2.length());

        // get the number of transpositions
        int transpositions = 0;
        int cp1;
        for (int i = 0; i < common1.length(); i += Character.charCount(cp1)) {
            cp1 = common1.codePointAt(i);
            if (cp1 != common2.codePointAt(i))
                transpositions++;
        }
        transpositions /= 2.0f;

        // calculate jaro metric
        return (common1CPCount / ((float) str1CPCount) + common2CPCount / ((float) str2CPCount)
                + (common1CPCount - transpositions) / ((float) common1CPCount)) / 3.0f;
    }

}
