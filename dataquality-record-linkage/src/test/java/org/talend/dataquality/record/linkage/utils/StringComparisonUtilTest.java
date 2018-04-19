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
public class StringComparisonUtilTest {

    private String str1 = "abc"; //$NON-NLS-1$

    private String str2 = "abc"; //$NON-NLS-1$

    private String str3 = "abcd"; //$NON-NLS-1$

    private String str4 = "abd"; //$NON-NLS-1$

    private String str5 = "bcd"; //$NON-NLS-1$

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.StringComparisonUtil#difference(java.lang.String,java.lang.String)} .
     */
    @Test
    public void testDifference() {
        assertEquals(0, StringComparisonUtil.difference(null, str2));
        assertEquals(0, StringComparisonUtil.difference(str1, null));
        assertEquals(3, StringComparisonUtil.difference(str1, str2));
        assertEquals(3, StringComparisonUtil.difference(str1, str3));
        assertEquals(2, StringComparisonUtil.difference(str1, str4));
        assertEquals(0, StringComparisonUtil.difference(str1, str5));
        assertEquals(0, StringComparisonUtil.difference(" ", "")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(1, StringComparisonUtil.difference(" ", "  ")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for {@link
     * org.talend.dataquality.record.linkage.utils.StringComparisonUtil#getCommonCharacters(java.lang.String,java.lang.String,
     * java.lang.int))} .
     */
    @Test
    public void testGetCommonCharacters() {
        assertEquals("abc", StringComparisonUtil.getCommonCharacters(str1, str2, 1).toString()); //$NON-NLS-1$
        assertEquals("abc", StringComparisonUtil.getCommonCharacters(str1, str3, 1).toString()); //$NON-NLS-1$
        assertEquals("ab", StringComparisonUtil.getCommonCharacters(str1, str4, 1).toString()); //$NON-NLS-1$
        assertEquals("bc", StringComparisonUtil.getCommonCharacters(str1, str5, 1).toString()); //$NON-NLS-1$
        assertEquals(" ", StringComparisonUtil.getCommonCharacters(" ", "  ", 1).toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.StringComparisonUtil#getPrefixLength(java.lang.String,java.lang.String)}
     * .
     */
    @Test
    public void testGetPrefixLength() {
        assertEquals(0, StringComparisonUtil.getPrefixLength(null, str2));
        assertEquals(0, StringComparisonUtil.getPrefixLength(str1, null));
        assertEquals(3, StringComparisonUtil.getPrefixLength(str1, str2));
        assertEquals(3, StringComparisonUtil.getPrefixLength(str1, str3));
        assertEquals(2, StringComparisonUtil.getPrefixLength(str1, str4));
        assertEquals(0, StringComparisonUtil.getPrefixLength(str1, str5));
        assertEquals(0, StringComparisonUtil.getPrefixLength(" ", "")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(1, StringComparisonUtil.getPrefixLength(" ", "  ")); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
