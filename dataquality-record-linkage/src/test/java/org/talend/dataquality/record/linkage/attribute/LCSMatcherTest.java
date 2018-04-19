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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * DOC dprot // Your Name class global comment. Detailled comment
 */
public class LCSMatcherTest {

    private static final double EPSILON = 0.000001;

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.attribute.LCSMatcher#getMatchingWeight(java.lang.String, java.lang.String)} .
     */
    @Test
    public void testGetMatchingWeight() {
        LCSMatcher lcsMatcher = new LCSMatcher();
        String a = " "; //$NON-NLS-1$
        double matchingWeight1 = lcsMatcher.getMatchingWeight(a, a);
        assertEquals("input strings are the same => result should be 1.", 1.0d, matchingWeight1, EPSILON);

        a = "malequa"; //$NON-NLS-1$
        matchingWeight1 = lcsMatcher.getMatchingWeight(a, a);
        assertEquals("input strings are the same => result should be 1.", 1.0d, matchingWeight1, EPSILON);

        String b = "malequz"; //$NON-NLS-1$
        matchingWeight1 = lcsMatcher.getMatchingWeight(a, b);
        assertTrue("input strings are different => result should be 6/7",
                matchingWeight1 < 6.0 / 7.0 + EPSILON && matchingWeight1 > 6.0 / 7.0 - EPSILON);
        b = "isleqthan";//$NON-NLS-1$
        matchingWeight1 = lcsMatcher.getMatchingWeight(a, b);
        assertTrue("input strings are different => result should be 3/9",
                matchingWeight1 < 3.0 / 9.0 + EPSILON && matchingWeight1 > 3.0 / 9.0 - EPSILON);

    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.attribute.LCSMatcher#getMatchType()}.
     */
    @Test
    public void testGetMatchType() {

        assertEquals(AttributeMatcherType.LCS, new LCSMatcher().getMatchType());
        assertEquals("LCS", new LCSMatcher().getMatchType().name()); //$NON-NLS-1$
        assertEquals("LCS", new LCSMatcher().getMatchType().toString()); //$NON-NLS-1$
    }

}
