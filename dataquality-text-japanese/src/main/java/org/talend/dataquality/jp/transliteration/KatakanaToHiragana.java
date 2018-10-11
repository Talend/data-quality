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

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class KatakanaToHiragana {

    protected static Stream<String> convert(Stream<String> katakanaStream) {

        return katakanaStream.map(katakanaToken -> {
            String fullWidth = KatakanaUtils.toFullWidth(katakanaToken);
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
            if (KatakanaUtils.isFullWidthKatakana(c)) {
                return (c - 0x60); // code point difference between full-width katakana and hiragana
            }
        }
        return cp;
    }
}
