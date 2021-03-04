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
public class HiraganaTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}
     * .
     */
    @Test
    public void testHandleRequest() {
        Hiragana hiragana = new Hiragana();
        String handleRequest = hiragana.handleRequest("もしtalendは第二の話、そして誰が最強だ？"); //$NON-NLS-1$
        Assert.assertEquals("HHtalendH第二H話、HHH誰H最強H？", handleRequest); //$NON-NLS-1$
    }

}
