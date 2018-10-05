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
public class KatakanaSmallTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}. case1
     * small k
     */
    @Test
    public void testHandleRequestCase1() {
        KatakanaSmall katakanaSmall = new KatakanaSmall();
        String handleRequest = katakanaSmall.handleRequest("もしｔｧｌｪｎｄ は第二の話、そして誰が最強だ？"); //$NON-NLS-1$
        Assert.assertEquals("もしｔkｌkｎｄ は第二の話、そして誰が最強だ？", handleRequest); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}. case2
     * K
     */
    @Test
    public void testHandleRequestCase2() {
        KatakanaSmall katakanaSmall = new KatakanaSmall();
        String handleRequest = katakanaSmall.handleRequest("もしｔｧｌｪｎｄ は第二の話、そして誰が最強だ？"); //$NON-NLS-1$
        Assert.assertNotEquals("もしｔKｌKｎｄ は第二の話、そして誰が最強だ？", handleRequest); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}.
     * 
     * case3 ｧ ｨ ｩ ｪ ｫ ｬ ｭ ｮ ｯ
     */
    @Test
    public void testHandleRequestCase3() {
        KatakanaSmall katakanaSmall = new KatakanaSmall();
        String handleRequest = katakanaSmall.handleRequest("ｧｨｩｪｫｬｭｮｯ"); //$NON-NLS-1$
        Assert.assertEquals("kkkkkkkkk", handleRequest); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}.
     * 
     * case4 ァ ィ ゥ ェ ォッャュョヮヵヶ
     */
    @Test
    public void testHandleRequestCase4() {
        KatakanaSmall katakanaSmall = new KatakanaSmall();
        String handleRequest = katakanaSmall.handleRequest("ァィゥェォッャュョヮヵヶ"); //$NON-NLS-1$
        Assert.assertEquals("ァィゥェォッャュョヮヵヶ", handleRequest); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}.
     * 
     * case5 ㇰㇱㇲㇳㇴㇵㇶㇷㇸㇹㇺㇻㇼㇽㇾㇿ
     */
    @Test
    public void testHandleRequestCase5() {
        KatakanaSmall katakanaSmall = new KatakanaSmall();
        String handleRequest = katakanaSmall.handleRequest("ㇰㇱㇲㇳㇴㇵㇶㇷㇸㇹㇺㇻㇼㇽㇾㇿ"); //$NON-NLS-1$
        Assert.assertEquals("ㇰㇱㇲㇳㇴㇵㇶㇷㇸㇹㇺㇻㇼㇽㇾㇿ", handleRequest); //$NON-NLS-1$
    }

}
