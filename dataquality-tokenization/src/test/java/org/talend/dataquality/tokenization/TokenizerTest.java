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
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenizerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenizerTest.class);

    @Test
    public void testTokenizeJP() {
        final String text = "お寿司が食べたい。";
        final List<String> expectedTokens = Arrays.asList("お", "寿司", "が", "食べ", "たい", "。");
        assertEquals(expectedTokens , Tokenizer.tokenizeJP(text));
        for(Tokenizer.DictionaryJP dict :Tokenizer.DictionaryJP.values()){
            assertEquals(expectedTokens ,Tokenizer.tokenizeJP(text, dict));
        }
    }
}
