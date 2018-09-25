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
import java.util.stream.Stream;

public class KatakanaToHiragana {

    protected static Stream<String> convert(Stream<String> katakanaStream) {

        return katakanaStream.map(katakanaToken -> {
            String fullWidth = handleHalfWidth(katakanaToken);
            String specialCharacters = handleSpecialCharacters(fullWidth);
            final IntStream intStream = handleChoonpu(specialCharacters).codePoints().map(KatakanaToHiragana::toHiragana);
            return intStream.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        });
    }

    private static String handleChoonpu(String katakanaToken) {
        final int id1stChoonpu = katakanaToken.indexOf('ー');
        if (id1stChoonpu >= 0) {// handle Chōonpu, see: https://en.wikipedia.org/wiki/Ch%C5%8Donpu
            char[] kanaChars = katakanaToken.toCharArray();
            for (int i = id1stChoonpu; i < kanaChars.length; i++) { // from the 1st 'ー' till the last char
                if (i > 0 && kanaChars[i] == 'ー') {
                    final char preChar = kanaChars[i - 1]; // get the char before 'ー'
                    if (KatakanaToRomaji.KATAKANA_TO_ROMAJI.containsKey("" + preChar)) {
                        final String s = KatakanaToRomaji.KATAKANA_TO_ROMAJI.get("" + preChar)[0];
                        switch (s.charAt(s.length() - 1)) { // replace 'ー' by the corresponding a-gyō hiragana
                        case 'a':
                            kanaChars[i] = 'あ';
                            break;
                        case 'i':
                            kanaChars[i] = 'い';
                            break;
                        case 'u':
                            kanaChars[i] = 'う';
                            break;
                        case 'e':
                            kanaChars[i] = 'え';
                            break;
                        case 'o':
                            kanaChars[i] = 'お';
                            break;
                        default:
                            break;
                        }
                    }
                }
            }
            return String.valueOf(kanaChars);
        }
        return katakanaToken;
    }

    private static String handleHalfWidth(String katakanaToken) {

        char[] katakanaTokenchars = katakanaToken.toCharArray();
        for (int i = 0; i < katakanaTokenchars.length; i++) {
            char katakanaChar = katakanaTokenchars[i];
            Character fullWidthChar = MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.get(katakanaChar);
            if (fullWidthChar != null) {
                katakanaTokenchars[i] = fullWidthChar;
            }
        }

        return new String(katakanaTokenchars);
    }

    private static String handleSpecialCharacters(String katakanaToken) {
        String result;
        result = katakanaToken.replaceAll("\\u30F7", "ゔぁ");
        result = result.replaceAll("\\u30F8", "ゔぃ");
        result = result.replaceAll("\\u30F9", "ゔぇ");
        return result.replaceAll("\\u30FA", "ゔぉ");
    }

    private static int toHiragana(int cp) {
        char c = (char) cp;
        if (c != 'ー') {
            if (c == '\u30FB') //Middle point need to be skipped
                return c;
            if (isFullWidthKatakana(c)) {
                return (c - 0x60); // code point difference between full-width katakana and hiragana
            }
        }
        return cp;
    }

    private static boolean isFullWidthKatakana(char c) {
        return (('\u30a1' <= c) && (c <= '\u30fe'));
    }

    //Mapping HANKAKU TO ZENKAKU
    private static final Map<Character, Character> MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA;
    static {
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA = new HashMap();
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
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ヮ', 'ヮ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｴ', 'ヱ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ｦ', 'ヲ');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾝ', 'ン');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾞ', '゛');
        MAPPING_HALFWIDTH_TO_FULLWIDTH_KATAKANA.put('ﾟ', '゜');
    }
}
