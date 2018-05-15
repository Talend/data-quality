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
package org.talend.dataquality.tokenization;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class AsianTokenizerTest {

    @Test
    public void testTokenizeJP() {
        final Map<String, List<String>> textsWithExpectedTokens = new HashMap<String, List<String>>() {

            {
                put("お寿司が食べたい。", Arrays.asList("お", "寿司", "が", "食べ", "たい", "。"));
                put("おsushiが食べたい。", Arrays.asList("お", "sushi", "が", "食べ", "たい", "。")); // japanese-english text
            }
        };

        for (String text : textsWithExpectedTokens.keySet()) {
            assertEquals(textsWithExpectedTokens.get(text), AsianTokenizer.tokenizeJP(text));

            for (AsianTokenizer.DictionaryJP dict : AsianTokenizer.DictionaryJP.values()) {
                assertEquals(textsWithExpectedTokens.get(text), AsianTokenizer.tokenizeJP(text, dict));
            }
        }
    }
}
