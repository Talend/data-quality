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
package org.talend.dataquality.jp.transliteration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.jp.tokenization.TextTokenizer;

import com.atilika.kuromoji.TokenBase;

public class TextTransliterator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextTransliterator.class);

    private static final int READING_ID = 7;

    private static final int PRONUNCIATION_ID = 8;

    private static final String KUROMOJI_NA_FEATURE = "*";

    private static TextTokenizer textTokenizer;

    private TextTransliterator() {
    }

    private static class LazyHolder {

        private static final TextTransliterator INSTANCE = new TextTransliterator();
    }

    public static TextTransliterator getInstance() {
        try {
            textTokenizer = TextTokenizer.getInstance();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return LazyHolder.INSTANCE;
    }

    public String transliterate(String text, TransliterateType type) {
        return transliterate(text, type, " ");
    }

    public String transliterate(String text, TransliterateType type, String delimiter) {
        return convert(text, type).collect(Collectors.joining(delimiter));
    }

    private Stream<String> convert(String text, TransliterateType type) {
        if (type.equals(TransliterateType.KATAKANA_READING) || type.equals(TransliterateType.HIRAGANA)) {
            // if output type is KATAKANA_READING or HIRAGANA
            final Stream<String> katakanaRStream = convert2Katakana(text, false);
            if (type.equals(TransliterateType.KATAKANA_READING)) {
                return katakanaRStream;
            }
            return KatakanaToHiragana.convert(katakanaRStream);
            //Katakana reading
        } else {
            // if output type is KATAKANA_PRONUNCIATION or rōmaji, the text should be converted to Katakana pronunciation
            final Stream<String> katakanaPStream = convert2Katakana(text, true);
            if (type.equals(TransliterateType.KATAKANA_PRONUNCIATION)) {
                return katakanaPStream;
            }
            return KatakanaToRomaji.convert(katakanaPStream, type);
        }
    }

    private Stream<String> convert2Katakana(String text, Boolean toKatakanaPronunciation) {
        final List<? extends TokenBase> tokenize = textTokenizer.tokenize(text);
        return tokenize.stream().map(tokenBase -> {
            final String[] features = tokenBase.getAllFeaturesArray();
            final String katakana = toKatakanaPronunciation ? features[PRONUNCIATION_ID] : features[READING_ID];
            return KUROMOJI_NA_FEATURE.equals(katakana) ? tokenBase.getSurface() : katakana;
        });
    }

}
