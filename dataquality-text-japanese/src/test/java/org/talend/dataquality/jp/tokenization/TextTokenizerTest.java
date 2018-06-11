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
package org.talend.dataquality.jp.tokenization;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TextTokenizerTest {

    private static final Map<String, List<String>> textsWithExpectedTokens = new HashMap<>();
    static {
        textsWithExpectedTokens.put("お寿司が食べたい。", Arrays.asList("お", "寿司", "が", "食べ", "たい", "。"));
        textsWithExpectedTokens.put("おsushiが食べたい。", Arrays.asList("お", "sushi", "が", "食べ", "たい", "。")); // japanese-english text
    }

    private static final Map<String, String> textsWithExpectedTokenizedString = new HashMap<>();
    static {
        textsWithExpectedTokenizedString.put("お寿司が食べたい。", "お 寿司 が 食べ たい 。");
        textsWithExpectedTokenizedString.put("おsushiが食べたい。", "お sushi が 食べ たい 。"); // japanese-english text

    }

    @Test
    public void testGetListTokens() throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        for (String text : textsWithExpectedTokens.keySet()) { // case: without init dict
            assertEquals(textsWithExpectedTokens.get(text), TextTokenizer.getListTokens(text));
        }

        for (TextTokenizer.KuromojiDict dict : TextTokenizer.KuromojiDict.values()) { // case: with init dict
            TextTokenizer.init(dict);
            for (String text : textsWithExpectedTokens.keySet()) {
                assertEquals(textsWithExpectedTokens.get(text), TextTokenizer.getListTokens(text));
            }
        }

    }

    @Test
    public void testGetTokenizedString() throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        final String delimiter = " ";

        for (String text : textsWithExpectedTokenizedString.keySet()) { // case: without init dict
            assertEquals(textsWithExpectedTokenizedString.get(text), TextTokenizer.getTokenizedString(text));
            assertEquals(textsWithExpectedTokenizedString.get(text), TextTokenizer.getTokenizedString(text, delimiter));
        }

        for (TextTokenizer.KuromojiDict dict : TextTokenizer.KuromojiDict.values()) { // case: with init dict
            TextTokenizer.init(dict);
            for (String text : textsWithExpectedTokenizedString.keySet()) {
                assertEquals(textsWithExpectedTokenizedString.get(text), TextTokenizer.getTokenizedString(text));
                assertEquals(textsWithExpectedTokenizedString.get(text), TextTokenizer.getTokenizedString(text, delimiter));
            }
        }

    }
}