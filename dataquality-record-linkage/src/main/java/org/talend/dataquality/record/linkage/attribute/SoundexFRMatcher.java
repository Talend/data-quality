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

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.contribs.algorithm.SoundexFR;
import org.talend.dataquality.record.linkage.utils.StringComparisonUtil;

/**
 * @author scorreia
 * 
 * FR Soundex using Mural's implementation of FR Soundex.
 */
public class SoundexFRMatcher extends AbstractAttributeMatcher {

    private static final long serialVersionUID = 8766998374223515281L;

    private final SoundexFR algorithm = new SoundexFR();

    /**
     * soundex returns 4 when strings are similar. Maximal number of matching characters in SoundexFR.
     */
    private static final double MAX = 4.0;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    @Override
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.SOUNDEX_FR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getWeight(java.lang.String,
     * java.lang.String)
     */
    @Override
    public double getWeight(String str1, String str2) {
        if (str1 == null) {
            return (str2 == null) ? 1 : 0;
        } else { // only str2 is null
            if (str2 == null) {
                return 0;
            }
        }
        // none of the input string is null
        return StringComparisonUtil.difference(algorithm.encode(str1), algorithm.encode(str2)) / MAX;
    }

}
