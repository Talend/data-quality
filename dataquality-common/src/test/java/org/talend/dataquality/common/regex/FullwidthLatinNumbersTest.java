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
package org.talend.dataquality.common.regex;

import org.junit.Assert;
import org.junit.Test;

/**
 * DOC talend class global comment. Detailled comment
 */
public class FullwidthLatinNumbersTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}
     * .
     * 
     * case1 normal case
     */
    @Test
    public void testHandleRequestCase1() {
        FullwidthLatinNumbers fullwidthLatinNumbers = new FullwidthLatinNumbers();
        String handleRequest = fullwidthLatinNumbers.handleRequest("０１３４５６７８９"); //$NON-NLS-1$
        Assert.assertEquals("999999999", handleRequest); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}.
     * 
     * Case2 should not adapt normal 0-9
     */
    @Test
    public void testHandleRequestCase2() {
        FullwidthLatinNumbers fullwidthLatinNumbers = new FullwidthLatinNumbers();
        String handleRequest = fullwidthLatinNumbers.handleRequest("0123456789"); //$NON-NLS-1$
        Assert.assertEquals("0123456789", handleRequest); //$NON-NLS-1$
    }

}
