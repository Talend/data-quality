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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * created by scorreia on Jan 7, 2013 Detailled comment
 * 
 */
@SuppressWarnings("nls")
public class SoundexMatcherTest {

    private static final double EPSILON = 0.000001;

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.attribute.SoundexMatcher#getWeight(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetWeight() {
        SoundexMatcher soundexMatcher = new SoundexMatcher();
        String a = "John"; //$NON-NLS-1$
        String b = "Jon"; //$NON-NLS-1$
        double matchingWeight = soundexMatcher.getMatchingWeight(a, b);
        assertEquals(1.0d, matchingWeight, EPSILON);

        a = "\n";
        b = "Hulme";
        matchingWeight = soundexMatcher.getMatchingWeight(a, b);
        assertTrue("input strings are not the same " + a + " and " + b, 0.0d == matchingWeight);

    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.attribute.SoundexMatcher#getMatchType()}.
     */
    @Test
    public void testGetMatchType() {
        assertEquals(AttributeMatcherType.SOUNDEX, new SoundexMatcher().getMatchType());
        assertEquals("SOUNDEX", new SoundexMatcher().getMatchType().name()); //$NON-NLS-1$
        assertEquals("Soundex", new SoundexMatcher().getMatchType().getLabel()); //$NON-NLS-1$
        assertEquals("SOUNDEX", new SoundexMatcher().getMatchType().toString()); //$NON-NLS-1$
    }

}
