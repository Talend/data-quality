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

import static org.talend.dataquality.converters.character.KanaConstants.HALFWIDTH_ASPIRATED_MARK;
import static org.talend.dataquality.converters.character.KanaConstants.HALFWIDTH_VOICED_MARK;
import static org.talend.dataquality.converters.character.KanaConstants.MAPPING_HALF_TO_FULL_KATAKANA;

import java.util.stream.IntStream;

import org.talend.dataquality.converters.character.KanaConstants;

public class KanaUtils {

    private KanaUtils() {
        // no need to implement
    }

    public static final int DIFF_FULLKATAKANA_HIRAGANA = 0x60; // code point difference between full-width katakana and
                                                               // hiragana

    /**
     * Convert half width katakana to full width katakana.
     * 
     * @param katakanaToken katakana string
     * @return string of full width katakana.
     */
    public static String half2FullKatakana(String katakanaToken) {

        final StringBuilder sb = new StringBuilder(katakanaToken);
        for (int i = 0; i < sb.length(); i++) {
            final char ch = sb.charAt(i);
            if (KanaConstants.isHalfwidthKatakana(ch)) {
                final Character fullwidthChar = MAPPING_HALF_TO_FULL_KATAKANA.get(ch);
                if (fullwidthChar != null) {
                    sb.setCharAt(i, fullwidthChar);
                } else if (i > 0 && (ch == HALFWIDTH_VOICED_MARK || ch == HALFWIDTH_ASPIRATED_MARK)
                        && KanaConstants.isHalfwidthKatakana(sb.charAt(i - 1))) {
                    sb.deleteCharAt(i);
                    i--;
                    if (ch == HALFWIDTH_VOICED_MARK) {
                        sb.setCharAt(i, (char) (sb.charAt(i) + 1));// code point distance 1
                    } else if (ch == HALFWIDTH_ASPIRATED_MARK) {
                        sb.setCharAt(i, (char) (sb.charAt(i) + 2));// code point distance 2
                    }
                }
            }
        }

        return sb.toString();
    }

    /**
     * Convert all hiragana chars to full width katakana chars (keep the other chars as input)
     *
     * @param text string
     * @return string of full width katakana.
     */
    public static String hiragana2FullKatakana(String text) {
        final IntStream cpStream = text.codePoints().map(c -> {
            if (('\u3041' <= c && c <= '\u3096') || (c >= '\u309D' && c <= '\u309F')) {
                return (char) (c + DIFF_FULLKATAKANA_HIRAGANA);
            } else {
                return c;
            }
        });

        return cpStream.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    /**
     * Check whether a char is full width katakana or not.
     * 
     * @param c input char
     * @return true if input char is Full Width Katakana, otherwise return false.
     */
    protected static boolean isFullWidthKatakana(char c) {
        return ('\u30a1' <= c) && (c <= '\u30fe');
    }
}
