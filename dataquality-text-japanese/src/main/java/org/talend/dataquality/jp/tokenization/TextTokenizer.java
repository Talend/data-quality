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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.jp.common.KuromojiDict;

import com.atilika.kuromoji.TokenizerBase;

public class TextTokenizer extends TextTokenizerBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextTokenizer.class);

    private TextTokenizer() {
        // TextTokenizer with dictionary IPADIC
        final String dictName = KuromojiDict.IPADIC.getDictName();
        LOGGER.info("Initialise tokenizer with the dictionary: mecab-" + dictName);
        try {
            tokenizer = (TokenizerBase) Class.forName("com.atilika.kuromoji." + dictName + ".Tokenizer").newInstance();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static class LazyHolder {

        private static final TextTokenizer INSTANCE = new TextTokenizer();
    }

    public static TextTokenizer getInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return LazyHolder.INSTANCE;
    }

}