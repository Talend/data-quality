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

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

/**
 * DOC talend class global comment. Detailled comment
 */
public class KanjiTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}
     * .
     * 
     * case1 normal case
     */
    @Test
    public void testHandleRequestCase1() {
        Kanji kanji = new Kanji();
        String handleRequest = kanji.handleRequest("如果talend算第二的话，那么还有谁能是最强的呢？"); //$NON-NLS-1$
        Assert.assertEquals("CCtalendCCCCC，CCCCCCCCCCC？", handleRequest); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}
     * .
     * 
     * case 2 input value is null
     */
    @Test
    public void testHandleRequestCase2() {
        Kanji kanji = new Kanji();
        String handleRequest = kanji.handleRequest(null);
        Assert.assertEquals(null, handleRequest);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}
     * .
     * 
     * case 3 ReplaceStr is null
     */
    @Test
    public void testHandleRequestCase3() {
        Kanji kanji = new Kanji() {

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.indicators.util.Kanji#getReplaceStr()
             */
            @Override
            protected String getReplaceStr() {
                return null;
            }

        };
        String input = "如果talend算第二的话，那么还有谁能是最强的呢？"; //$NON-NLS-1$
        String handleRequest = kanji.handleRequest(input);
        Assert.assertEquals(input, handleRequest);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}
     * .
     * 
     * case 4 Regex is null
     */
    @Test
    public void testHandleRequestCase4() {
        Kanji kanji = new Kanji() {

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.indicators.util.Kanji#getRegex()
             */
            @Override
            protected Pattern getRegex() {
                return null;
            }

        };
        String input = "如果talend算第二的话，那么还有谁能是最强的呢？"; //$NON-NLS-1$
        String handleRequest = kanji.handleRequest(input);
        Assert.assertEquals(input, handleRequest);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}
     * .
     * 
     * case 5 two handler link to use
     */
    @Test
    public void testHandleRequestCase5() {
        Kanji kanji = new Kanji();
        FullwidthLatinLowercasedLetters fullwidthLatinLowercasedLetters = new FullwidthLatinLowercasedLetters();
        kanji.linkSuccessor(fullwidthLatinLowercasedLetters);
        String input = "如果ｔａｌｅｎｄ算第二的话，那么还有谁能是最强的呢？"; //$NON-NLS-1$
        String handleRequest = kanji.handleRequest(input);
        Assert.assertEquals("CCaaaaaaCCCCC，CCCCCCCCCCC？", handleRequest); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}
     * .
     * 
     * case 5 two handler link to use
     */
    @Test
    public void testHandleRequestCase6() {
        Kanji kanji = new Kanji();
        FullwidthLatinLowercasedLetters fullwidthLatinLowercasedLetters = new FullwidthLatinLowercasedLetters();
        kanji.linkSuccessor(fullwidthLatinLowercasedLetters);

        String input = "々〆〇】〡〻㐀一豈ab123"; //$NON-NLS-1$
        String handleRequest = kanji.handleRequest(input);
        // Non-letter kanji must not be recognized as 'C'
        Assert.assertEquals("C〆C】CC㐀CCab123", handleRequest); //$NON-NLS-1$
    }

    @Test
    public void rareKanji() {
        KanjiRare kanji = new KanjiRare();
        String input = "々〆〇】〡〻㐀一豈ab123"; //$NON-NLS-1$
        String handleRequest = kanji.handleRequest(input);
        // Non-letter kanji must not be recognized as 'C'
        Assert.assertEquals("々〆〇】〡〻C一豈ab123", handleRequest); //$NON-NLS-1$
    }
}
