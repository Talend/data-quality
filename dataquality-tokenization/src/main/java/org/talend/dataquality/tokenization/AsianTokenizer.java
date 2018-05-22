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
        IPADIC("ipadic"),
        JUMANDIC("jumandic"),
        NAIST_JDIC("naist.jdic"),
        UNIDIC("unidic"),
        UNIDIC_KANAACCENT("unidic.kanaaccent");

        private final String dictName;

        public String getDictName() {
            return dictName;
        }

        DictionaryJP(String dictName) {
            this.dictName = dictName;
        }
    }

    /**
     *
     * @param text
     * @param dict
     * @return List of tokens
     */
    public static List<String> tokenizeJP(String text, DictionaryJP dict)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        String dictName;

        if (dict == null) {
            LOGGER.warn("Unknown dictionary: " + dict + ", use mecab-ipadic instead.");
            dictName = DictionaryJP.IPADIC.getDictName();
        } else {
            dictName = dict.getDictName();
        }

        TokenizerBase tokenizer = (TokenizerBase) Class.forName("com.atilika.kuromoji." + dictName + ".Tokenizer").newInstance();
        return tokenizer.tokenize(text).stream().map(token -> token.getSurface()).collect(Collectors.toList());
    }

    /**
     *
     * @param text
     * @return List of tokens
     */
    public static List<String> tokenizeJP(String text)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return AsianTokenizer.tokenizeJP(text, DictionaryJP.IPADIC);
    }

}