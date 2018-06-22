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

/**
 * DOC dprot class global comment. Detailled comment
 */
public class LCSMatcher extends AbstractAttributeMatcher {

    private static final long serialVersionUID = 3894610803291924363L;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.LCS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getWeight(java.lang.String,
     * java.lang.String)
     */
    public double getWeight(String string1, String string2) {
        final int lcs = longestSubstr(string1, string2);
        long maxLength = Math.max(string1.codePoints().count(), string2.codePoints().count());
        return ((double) lcs) / maxLength;
    }

    private int longestSubstr(String s, String t) {
        if (s.isEmpty() || t.isEmpty()) {
            return 0;
        }

        int m = s.length();
        int n = t.length();
        int cost;
        int maxLen = 0;
        int[] p = new int[n];
        int[] d = new int[n];

        for (int i = 0; i < m;) {
            int sCP = s.codePointAt(i);
            for (int j = 0; j < n;) {
                int tCP = t.codePointAt(j);
                // calculate cost/score
                if (sCP != tCP) {
                    cost = 0;
                } else {
                    if ((i == 0) || (j == 0)) {
                        cost = 1;
                    } else {
                        cost = p[j - Character.charCount(tCP)] + 1;
                    }
                }
                d[j] = cost;

                if (cost > maxLen) {
                    maxLen = cost;
                }
                j += Character.charCount(tCP);
            }
            int[] swap = p;
            p = d;
            d = swap;
            i += Character.charCount(sCP);
        }

        return maxLen;
    }
}
