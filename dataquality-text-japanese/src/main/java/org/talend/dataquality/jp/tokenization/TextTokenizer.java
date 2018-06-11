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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.atilika.kuromoji.TokenizerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextTokenizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextTokenizer.class);

    private static TokenizerBase tokenizer;

    private static String dictName;

    public enum KuromojiDict {
        IPADIC("ipadic"),
        JUMANDIC("jumandic"),
        NAIST_JDIC("naist.jdic"),
        UNIDIC("unidic"),
        UNIDIC_KANAACCENT("unidic.kanaaccent");

        private final String dictName;

        public String getDictName() {
            return dictName;
        }

        KuromojiDict(String dictName) {
            this.dictName = dictName;
        }
    }

    public static void init(KuromojiDict dict) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (tokenizer == null || dict.getDictName() != dictName) {
            LOGGER.info("Initialise tokenizer with the dictionary: mecab-" + dict.dictName);
            tokenizer = (TokenizerBase) Class.forName("com.atilika.kuromoji." + dict.dictName + ".Tokenizer").newInstance();
            dictName = dict.getDictName();
        }
    }

    private static Stream<String> tokenize(String text)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        if (tokenizer == null) {
            // if the tokenizer haven't been initialized, then init with the default dictionary IPADIC
            LOGGER.warn("The tokenizer haven't been initialized! Use default dictionary: mecab-ipadic.");
            init(KuromojiDict.IPADIC);
        }
        return tokenizer.tokenize(text).stream().map(token -> token.getSurface());
    }

    /**
     *
     * @param text
     * @return List of tokens
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static List<String> getListTokens(String text)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return tokenize(text).collect(Collectors.toList());
    }

    /**
     *
     * @param text
     * @param delimiter
     * @return tokenized string with delimiter
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static String getTokenizedString(String text, String delimiter)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return tokenize(text).collect(Collectors.joining(delimiter));
    }

    /**
     *
     * @param text
     * @return tokenized string with space as delimiter
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static String getTokenizedString(String text)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return getTokenizedString(text, " ");
    }

}