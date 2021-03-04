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
package org.talend.dataquality.record.linkage.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class QGramTokenizerTest {

    private static final String INPUT = "DOC scorreia  class global comment. Detailled comment";

    private static final String expected[] = { "##D", "#DO", "DOC", "OC ", "C s", " sc", "sco", "cor", "orr", "rre",
            "rei", "eia", "ia ", "a  ", "  c", " cl", "cla", "las", "ass", "ss ", "s g", " gl", "glo", "lob", "oba",
            "bal", "al ", "l c", " co", "com", "omm", "mme", "men", "ent", "nt.", "t. ", ". D", " De", "Det", "eta",
            "tai", "ail", "ill", "lle", "led", "ed ", "d c", " co", "com", "omm", "mme", "men", "ent", "nt#", "t##" };

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.QGramTokenizer#tokenizeToArrayList(java.lang.String, int)}.
     */
    @Test
    public void testTokenizeToArrayList() {
        HashSet<String> expect = new HashSet<String>();
        expect.addAll(Arrays.asList(expected));
        QGramTokenizer tokenizer = new QGramTokenizer();
        List<String> tokenized = tokenizer.tokenizeToArrayList(INPUT, 3);
        // StringBuffer buf = new StringBuffer();
        for (String token : tokenized) {
            // buf.append("\"").append(token).append("\",");
            Assert.assertEquals("token not found:" + token, true, expect.contains(token));
        }
        // System.out.println(buf);
    }

}
