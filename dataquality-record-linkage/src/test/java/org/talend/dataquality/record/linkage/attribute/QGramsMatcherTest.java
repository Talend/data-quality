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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class QGramsMatcherTest {

    private static final String[] LOOKUP = { "joão", "joão silva", "joão pé de feijão" }; //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$

    private static final String[] MAININPUT = { "joão" }; //$NON-NLS-1$

    private static final String[] POSSIBLE_MATCHES = { "john doe", "John Doe", "Doe John", "Doe john", "jon doe" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

    private static final double EPSILON = 0.000001;

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.attribute.LevenshteinMatcher#getMatchingWeight(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetMatchingWeight() {
        QGramsMatcher m = new QGramsMatcher();
        m.setTokenMethod(TokenizedResolutionMethod.NO);
        m.setInitialComparison(false);
        m.setFingerPrintApply(false);

        for (String elt : MAININPUT) {

            for (String lookupElt : LOOKUP) {
                double matchingWeight = m.getMatchingWeight(elt, lookupElt);
                // System.out.println("Match[" + elt + "," + lookupElt + "]=" + matchingWeight); //$NON-NLS-1$ //$NON-NLS-2$
                // //$NON-NLS-3$
                assertFalse(matchingWeight > 1);
                assertFalse(matchingWeight < 0);
            }
        }

        for (String name1 : POSSIBLE_MATCHES) {
            for (String name2 : POSSIBLE_MATCHES) {
                double matchingWeight = m.getMatchingWeight(name1, name2);
                if (name1.equals(name2)) {
                    assertEquals("weight(" + name1 + "," + name2 + ")=" + matchingWeight, 1.0, matchingWeight, EPSILON); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                } else {
                    assertFalse("weight(" + name1 + "," + name2 + ")=" + matchingWeight, matchingWeight > 1); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    assertFalse("weight(" + name1 + "," + name2 + ")=" + matchingWeight, matchingWeight < 0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            }
        }
    }

    @Test
    public void tokenizationAndPadding() {
        String str1 = "Jon Doe"; //$NON-NLS-1$
        String str2 = "Doe John"; //$NON-NLS-1$

        QGramsMatcher qg = new QGramsMatcher();
        qg.setTokenMethod(TokenizedResolutionMethod.ANYORDER);
        qg.setRegexTokenize(" "); //$NON-NLS-1$
        double wToken = qg.getMatchingWeight(str1, str2);

        assertEquals(0.77272727, wToken, EPSILON);
    }

}
