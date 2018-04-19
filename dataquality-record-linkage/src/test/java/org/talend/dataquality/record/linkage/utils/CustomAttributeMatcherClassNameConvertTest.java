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
package org.talend.dataquality.record.linkage.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * created by msjian on 2017年12月27日
 * Detailled comment
 *
 */
public class CustomAttributeMatcherClassNameConvertTest {

    private String classPath = "CustomMatcherTest.jar||MycustomMatch.jar||testCustomMatcher.myCustomMatcher"; //$NON-NLS-1$

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.CustomAttributeMatcherClassNameConvert#getClassName(java.lang.String)} .
     */
    @Test
    public void testGetClassName() {
        String className = CustomAttributeMatcherClassNameConvert.getClassName(classPath);
        assertEquals("testCustomMatcher.myCustomMatcher", className); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.CustomAttributeMatcherClassNameConvert#getClassNameAndAddQuot(java.lang.String)}
     * .
     */
    @Test
    public void testGetClassNameAndAddQuot() {
        String className = CustomAttributeMatcherClassNameConvert.getClassNameAndAddQuot(classPath);
        assertEquals(CustomAttributeMatcherClassNameConvert.QUOTE + "testCustomMatcher.myCustomMatcher" //$NON-NLS-1$
                + CustomAttributeMatcherClassNameConvert.QUOTE, className);
    }

}
