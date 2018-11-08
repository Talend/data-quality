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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class KanaUtils {

    private KanaUtils() {
        // no need to implement
    }

    public static final int DIFF_FULLKATAKANA_HIRAGANA = 0x60; // code point difference between full-width katakana and
                                                               // hiragana

    private static final Map<Character, Character> MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA = new HashMap<>();

    static {
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('｡', '。');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('｢', '「');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('｣', '」');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('､', '、');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('･', '・');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｧ', 'ァ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｨ', 'ィ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｩ', 'ゥ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｪ', 'ェ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｫ', 'ォ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｬ', 'ャ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｭ', 'ュ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｮ', 'ョ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｯ', 'ッ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｰ', 'ー');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｱ', 'ア');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｲ', 'イ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｳ', 'ウ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｴ', 'エ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｵ', 'オ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｶ', 'カ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｷ', 'キ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｸ', 'ク');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｹ', 'ケ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｺ', 'コ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｻ', 'サ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｼ', 'シ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｽ', 'ス');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｾ', 'セ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｿ', 'ソ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾀ', 'タ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾁ', 'チ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾂ', 'ツ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾃ', 'テ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾄ', 'ト');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾅ', 'ナ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾆ', 'ニ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾇ', 'ヌ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾈ', 'ネ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾉ', 'ノ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾊ', 'ハ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾋ', 'ヒ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾌ', 'フ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾍ', 'ヘ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾎ', 'ホ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾏ', 'マ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾐ', 'ミ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾑ', 'ム');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾒ', 'メ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾓ', 'モ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾔ', 'ヤ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾕ', 'ユ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾖ', 'ヨ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾗ', 'ラ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾘ', 'リ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾙ', 'ル');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾚ', 'レ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾛ', 'ロ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾜ', 'ワ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｴ', 'ヱ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｦ', 'ヲ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾝ', 'ン');
    }

    /**
     * Convert half width katakana to full width katakana.
     * 
     * @param katakanaToken katakana string
     * @return string of full width katakana.
     */
    public static String half2FullKatakana(String katakanaToken) {

        StringBuilder sb = new StringBuilder(katakanaToken);
        char katakanaChar;
        for (int i = 0; i < sb.length(); i++) {
            katakanaChar = sb.charAt(i);
            if (!isFullWidthKatakana(katakanaChar)) {
                if (i > 0 && (katakanaChar == 'ﾞ' || katakanaChar == 'ﾟ')) {
                    if (katakanaChar == 'ﾞ') {
                        sb.setCharAt(i - 1, (char) (sb.charAt(i - 1) + 1));// code point distance 1
                    } else { // katakanaChar == 'ﾟ'
                        sb.setCharAt(i - 1, (char) (sb.charAt(i - 1) + 2));// code point distance 2
                    }
                    sb.deleteCharAt(i);
                    if (i < sb.length()) {
                        katakanaChar = sb.charAt(i);
                    }
                }
                if (i < sb.length()) {
                    Character fullWidthChar = MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.get(katakanaChar);
                    if (fullWidthChar != null) {
                        sb.setCharAt(i, fullWidthChar);
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
