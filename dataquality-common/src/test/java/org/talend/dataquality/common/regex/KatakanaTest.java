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
public class KatakanaTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}. case1
     * K
     */
    @Test
    public void testHandleRequestCase1() {
        Katakana katakana = new Katakana();
        String handleRequest = katakana.handleRequest("もしＴｱＬｴＮ Ｄは第二の話、そして誰が最強だ？"); //$NON-NLS-1$
        Assert.assertEquals("もしＴｱＬｴＮ Ｄは第二の話、そして誰が最強だ？", handleRequest); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}. case2
     * small k
     */
    @Test
    public void testHandleRequestCase2() {
        Katakana katakana = new Katakana();
        String handleRequest = katakana.handleRequest("もしＴｱＬｴＮ Ｄは第二の話、そして誰が最強だ？"); //$NON-NLS-1$
        Assert.assertNotEquals("もしＴkＬkＮ Ｄは第二の話、そして誰が最強だ？", handleRequest); //$NON-NLS-1$
    }

}
