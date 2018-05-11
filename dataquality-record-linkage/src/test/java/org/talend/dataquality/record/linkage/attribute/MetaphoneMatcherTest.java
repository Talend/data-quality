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
public class MetaphoneMatcherTest {

    private static final double EPSILON = 0.000001;

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.attribute.MetaphoneMatcher#getWeight(java.lang.String, java.lang.String)} .
     */
    @Test
    public void testGetWeight() {
        MetaphoneMatcher metaphoneMatcher = new MetaphoneMatcher();
        String a = "John"; //$NON-NLS-1$
        String b = "Jon"; //$NON-NLS-1$
        double matchingWeight = metaphoneMatcher.getMatchingWeight(a, b);
        assertEquals(1.0d, matchingWeight, EPSILON);

        a = " ";
        matchingWeight = metaphoneMatcher.getMatchingWeight(a, a);
        assertEquals("input strings are the same => result should be 1.", 1.0d, matchingWeight, EPSILON);

        a = "23";
        matchingWeight = metaphoneMatcher.getMatchingWeight(a, a);
        assertEquals("input strings are the same => result should be 1.", 1.0d, matchingWeight, EPSILON);
        b = "64";
        matchingWeight = metaphoneMatcher.getMatchingWeight(a, b);
        assertNotSame("input strings are the same => result should NOT be 1.", 1.0d, matchingWeight);

        // test long strings
        a = "JohnFit";
        b = "JohnFitzgeraldKennedy";
        matchingWeight = metaphoneMatcher.getMatchingWeight(a, b);
        assertTrue("input strings are not the same but Metaphone should not be able to distinguish between " + a + " and " + b,
                1.0d == matchingWeight);

        a = "\n";
        b = "Hulme";
        matchingWeight = metaphoneMatcher.getMatchingWeight(a, b);
        assertTrue("input strings are not the same " + a + " and " + b, 0.0d == matchingWeight);

    }

    @Test
    public void testGetWeightWithOneNull() {
        MetaphoneMatcher metaphoneMatcher = new MetaphoneMatcher();
        String a = null; //$NON-NLS-1$
        String b = "Jon"; //$NON-NLS-1$
        double matchingWeight = metaphoneMatcher.getMatchingWeight(a, b);
        assertEquals(0.0d, matchingWeight, EPSILON);
    }

    @Test
    public void testGetWeightWithEverythingNull() {
        MetaphoneMatcher metaphoneMatcher = new MetaphoneMatcher();
        String a = null; //$NON-NLS-1$
        String b = null; //$NON-NLS-1$
        double matchingWeight = metaphoneMatcher.getMatchingWeight(a, b);
        assertEquals(1.0d, matchingWeight, EPSILON);
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.attribute.MetaphoneMatcher#getMatchType()}.
     */
    @Test
    public void testGetMatchType() {

        assertEquals(AttributeMatcherType.METAPHONE, new MetaphoneMatcher().getMatchType());
        assertEquals("METAPHONE", new MetaphoneMatcher().getMatchType().name()); //$NON-NLS-1$
        assertEquals("Metaphone", new MetaphoneMatcher().getMatchType().getLabel()); //$NON-NLS-1$
        assertEquals("METAPHONE", new MetaphoneMatcher().getMatchType().toString()); //$NON-NLS-1$
    }

}
