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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * created by msjian on 2018年7月2日
 * Detailled comment
 *
 */
public class JaroMatcherTest {

    private static final double EPSILON = 0.000001;

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.attribute.JaroMatcher#getWeight(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetWeight() {
        JaroMatcher jaroMatcher = new JaroMatcher();
        String a = "John"; //$NON-NLS-1$
        String b = "Jon"; //$NON-NLS-1$
        double matchingWeight = jaroMatcher.getMatchingWeight(a, b);
        assertEquals(0.9166666865348816d, matchingWeight, EPSILON);
        a = "23"; //$NON-NLS-1$
        matchingWeight = jaroMatcher.getMatchingWeight(a, a);
        assertEquals("input strings are the same => result should be 1.", 1.0d, matchingWeight, EPSILON); //$NON-NLS-1$
        b = "64"; //$NON-NLS-1$
        matchingWeight = jaroMatcher.getMatchingWeight(a, b);
        assertNotSame("input strings are the same => result should NOT be 1.", 1.0d, matchingWeight); //$NON-NLS-1$
        String c = " "; //$NON-NLS-1$
        matchingWeight = jaroMatcher.getMatchingWeight(c, c);
        assertEquals("input strings are the same => result should be 1.", 1.0d, matchingWeight, EPSILON); //$NON-NLS-1$

        // test long strings
        a = "JohnFit"; //$NON-NLS-1$
        b = "JohnFitzgeraldKennedy"; //$NON-NLS-1$
        matchingWeight = jaroMatcher.getMatchingWeight(a, b);
        assertTrue("input strings are not the same but should not be able to distinguish between " + a + " and " + b, //$NON-NLS-1$ //$NON-NLS-2$
                0.7777778506278992d == matchingWeight);

        a = "\n"; //$NON-NLS-1$
        b = "Hulme"; //$NON-NLS-1$
        matchingWeight = jaroMatcher.getMatchingWeight(a, b);
        assertTrue("input strings are not the same " + a + " and " + b, 0.0d == matchingWeight); //$NON-NLS-1$ //$NON-NLS-2$

        a = "拓蓝科技"; //$NON-NLS-1$
        b = "拓蓝"; //$NON-NLS-1$
        matchingWeight = jaroMatcher.getMatchingWeight(a, b);
        assertEquals(0.8333333134651184d, matchingWeight, EPSILON);
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.attribute.JaroMatcher#getMatchType()}.
     */
    @Test
    public void testGetMatchType() {
        assertEquals(AttributeMatcherType.JARO, new JaroMatcher().getMatchType());
        assertEquals("JARO", new JaroMatcher().getMatchType().name()); //$NON-NLS-1$
        assertEquals("Jaro", new JaroMatcher().getMatchType().getLabel()); //$NON-NLS-1$
        assertEquals("JARO", new JaroMatcher().getMatchType().toString()); //$NON-NLS-1$
    }

    @Test
    public void testGetWeightSurrogate() {
        JaroMatcher matcher = new JaroMatcher();
        double weight = matcher.getMatchingWeight("𠀠𠀡𠀢", "𠀠");
        assertEquals(0.7777778506278992d, weight, EPSILON);
        weight = matcher.getWeight("𠀐𠀑𠀔", "𠀐𠀑𠀔");
        assertEquals(1.0d, weight, 0);
        weight = matcher.getWeight("𠀐𠀑a", "𠀒𠀓𠀔");
        assertEquals(0d, weight, 0);
        weight = matcher.getWeight("𠀐𠀑a", "𠀒𠀑c");
        assertEquals(0.5555555820465088d, weight, 0);
    }

}
