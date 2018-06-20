// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import com.atilika.kuromoji.TokenBase;
import com.atilika.kuromoji.TokenizerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.jp.common.KuromojiDict;

public class TextTokenizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextTokenizer.class);

    private static TokenizerBase tokenizer;

    private static String dictName;

    private TextTokenizer() {
    }

    private static class LazyHolder {

        private static final TextTokenizer INSTANCE = new TextTokenizer();
    }

    public static TextTokenizer getInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        // set default dictionary IPADIC
        return getInstance(KuromojiDict.IPADIC);
    }

    public static TextTokenizer getInstance(KuromojiDict dict)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final String dictName = dict.getDictName();
        if (tokenizer == null || dictName != TextTokenizer.dictName) {
            LOGGER.info("Initialise tokenizer with the dictionary: mecab-" + dictName);
            tokenizer = (TokenizerBase) Class.forName("com.atilika.kuromoji." + dictName + ".Tokenizer").newInstance();
            TextTokenizer.dictName = dictName;
        }
        return LazyHolder.INSTANCE;
    }

    /**
     *
     * @param text
     * @return List of TokenBase
     */
    public List<? extends TokenBase> tokenize(String text) {
        return tokenizer.tokenize(text);
    }

    private Stream<String> getTokenSurface(String text) {

        return this.tokenize(text).stream().map(token -> token.getSurface());
    }

    /**
     *
     * @param text
     * @return List of tokens
     */
    public List<String> getListTokens(String text) {
        return getTokenSurface(text).collect(Collectors.toList());
    }

    /**
     *
     * @param text
     * @param delimiter
     * @return tokenized string with delimiter
     */
    public String getTokenizedString(String text, String delimiter) {
        return getTokenSurface(text).collect(Collectors.joining(delimiter));
    }

    /**
     *
     * @param text
     * @return tokenized string with space as delimiter
     */
    public String getTokenizedString(String text) {
        return getTokenizedString(text, " ");
    }

}