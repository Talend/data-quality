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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class LevenshteinMatcherTest {

    private static final double EPSILON = 0.000001;

    private static final String[] LOOKUP = { "joão", "joão silva", "joão pé de feijão" }; //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$

    private static final String[] MAININPUT = { "joão" }; //$NON-NLS-1$

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.attribute.LevenshteinMatcher#getMatchingWeight(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetMatchingWeight() {
        LevenshteinMatcher m = new LevenshteinMatcher();

        for (String elt : MAININPUT) {

            for (String lookupElt : LOOKUP) {
                double matchingWeight = m.getMatchingWeight(elt, lookupElt);
                System.out.println("Match[" + elt + "," + lookupElt + "]=" + matchingWeight); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                assertFalse(matchingWeight > 1);
                assertFalse(matchingWeight < 0);
            }
        }

    }

    @Test
    public void tokenizeNoRegex() {
        String str1 = "John Doe";
        String str2 = "Doe John";

        LevenshteinMatcher LevenshteinToken = new LevenshteinMatcher();
        LevenshteinToken.setTokenMethod(TokenizedResolutionMethod.SAMEPLACE);
        LevenshteinToken.setRegexTokenize("titi");
        double wToken = LevenshteinToken.getMatchingWeight(str1, str2);

        LevenshteinMatcher LevenshteinNoToken = new LevenshteinMatcher();
        LevenshteinNoToken.setTokenMethod(TokenizedResolutionMethod.NO);
        double wNoToken = LevenshteinNoToken.getMatchingWeight(str1, str2);

        assertEquals(wNoToken, wToken, EPSILON);
    }

}
