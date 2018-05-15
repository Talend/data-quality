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

import java.util.List;
import java.util.stream.Collectors;

import com.atilika.kuromoji.TokenizerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsianTokenizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsianTokenizer.class);

    public enum DictionaryJP {
        IPADIC,
        JUMANDIC,
        NAIST_JDIC,
        UNIDIC,
        UNIDIC_KANAACCENT
    }

    /**
     *
     * @param text
     * @param dict
     * @return List of tokens
     */
    public static List<String> tokenizeJP(String text, DictionaryJP dict) {
        TokenizerBase tokenizer;
        switch (dict) {
        case IPADIC:
            tokenizer = new com.atilika.kuromoji.ipadic.Tokenizer();
            break;
        case JUMANDIC:
            tokenizer = new com.atilika.kuromoji.jumandic.Tokenizer();
            break;
        case NAIST_JDIC:
            tokenizer = new com.atilika.kuromoji.naist.jdic.Tokenizer();
            break;
        case UNIDIC:
            tokenizer = new com.atilika.kuromoji.unidic.Tokenizer();
            break;
        case UNIDIC_KANAACCENT:
            tokenizer = new com.atilika.kuromoji.unidic.kanaaccent.Tokenizer();
            break;
        default:
            LOGGER.warn("Unknown dictionary: " + dict + ", use mecab-ipadic instead.");
            tokenizer = new com.atilika.kuromoji.ipadic.Tokenizer();
            break;
        }

        return tokenizer.tokenize(text).stream().map(token -> token.getSurface()).collect(Collectors.toList());
    }

    /**
     *
     * @param text
     * @return List of tokens
     */
    public static List<String> tokenizeJP(String text) {
        return AsianTokenizer.tokenizeJP(text, DictionaryJP.IPADIC);
    }

}
