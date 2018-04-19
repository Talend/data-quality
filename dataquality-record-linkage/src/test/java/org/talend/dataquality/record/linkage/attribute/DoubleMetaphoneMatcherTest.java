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
 * created by zhao on Apr 16, 2013 Detailled comment
 * 
 */
@SuppressWarnings("nls")
public class DoubleMetaphoneMatcherTest {

    private static final double EPSILON = 0.000001;

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.attribute.MetaphoneMatcher#getWeight(java.lang.String, java.lang.String)} .
     */
    @Test
    public void testGetWeight() {
        DoubleMetaphoneMatcher doubleMetaphoneMatcher = new DoubleMetaphoneMatcher();
        String a = "John"; //$NON-NLS-1$
        String b = "Jon"; //$NON-NLS-1$
        double matchingWeight = doubleMetaphoneMatcher.getMatchingWeight(a, b);
        assertEquals(1.0d, matchingWeight, EPSILON);
        a = "23";
        matchingWeight = doubleMetaphoneMatcher.getMatchingWeight(a, a);
        assertEquals("input strings are the same => result should be 1.", 1.0d, matchingWeight, EPSILON);
        b = "64";
        matchingWeight = doubleMetaphoneMatcher.getMatchingWeight(a, b);
        assertNotSame("input strings are the same => result should NOT be 1.", 1.0d, matchingWeight);
        String c = " "; //$NON-NLS-1$
        matchingWeight = doubleMetaphoneMatcher.getMatchingWeight(c, c);
        assertEquals("input strings are the same => result should be 1.", 0d, matchingWeight, EPSILON);

        // test long strings
        a = "JohnFit";
        b = "JohnFitzgeraldKennedy";
        matchingWeight = doubleMetaphoneMatcher.getMatchingWeight(a, b);
        assertTrue(
                "input strings are not the same but DoubleMetaphone should not be able to distinguish between " + a + " and " + b,
                1.0d == matchingWeight);

        a = "\n";
        b = "Hulme";
        matchingWeight = doubleMetaphoneMatcher.getMatchingWeight(a, b);
        assertTrue("input strings are not the same " + a + " and " + b, 0.0d == matchingWeight);

    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.attribute.MetaphoneMatcher#getMatchType()}.
     */
    @Test
    public void testGetMatchType() {
        assertEquals(AttributeMatcherType.DOUBLE_METAPHONE, new DoubleMetaphoneMatcher().getMatchType());
        assertEquals("DOUBLE_METAPHONE", new DoubleMetaphoneMatcher().getMatchType().name()); //$NON-NLS-1$
        assertEquals("Double Metaphone", new DoubleMetaphoneMatcher().getMatchType().getLabel()); //$NON-NLS-1$
        assertEquals("DOUBLE_METAPHONE", new DoubleMetaphoneMatcher().getMatchType().toString()); //$NON-NLS-1$
    }

}
