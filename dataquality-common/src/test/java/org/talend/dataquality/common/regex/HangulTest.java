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
package org.talend.dataquality.common.regex;

import org.junit.Assert;
import org.junit.Test;

/**
 * DOC talend class global comment. Detailled comment
 */
public class HangulTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}.
     */
    @Test
    public void testHandleRequest() {
        Hangul hangul = new Hangul();
        String handleRequest = hangul.handleRequest("만약talend둘째치다말을,그렇게또누가것은최강?"); //$NON-NLS-1$
        Assert.assertEquals("GGtalendGGGGGG,GGGGGGGGGG?", handleRequest); //$NON-NLS-1$
    }

}
