// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.constant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * created by msjian on 2018年7月12日
 * Detailled comment
 *
 */
public class RecordMatcherTypeTest {

    @Test
    public void testAll() {
        RecordMatcherType[] allTypes = RecordMatcherType.values();
        assertNotNull(allTypes);
        assertEquals(2, allTypes.length);

        assertEquals("simpleVSRMatcher", RecordMatcherType.simpleVSRMatcher.name()); //$NON-NLS-1$
        assertEquals("T_SwooshAlgorithm", RecordMatcherType.T_SwooshAlgorithm.name()); //$NON-NLS-1$

        for (RecordMatcherType type : allTypes) {
            RecordMatcherType attributeMatcherTypeByName = RecordMatcherType.valueOf(type.name());
            // Assert no one is null
            assertNotNull(attributeMatcherTypeByName);
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.constant.RecordMatcherType#getLabel(java.lang.String)} .
     */
    @Test
    public void testGetLabel() {
        assertEquals("Simple VSR Matcher", RecordMatcherType.simpleVSRMatcher.getLabel()); //$NON-NLS-1$
        assertEquals("t-swoosh", RecordMatcherType.T_SwooshAlgorithm.getLabel()); //$NON-NLS-1$
    }

}
