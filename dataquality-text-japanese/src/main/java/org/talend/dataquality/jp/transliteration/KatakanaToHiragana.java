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

        return katakanaStream.map(katakana -> {
            final IntStream intStream = katakana.codePoints().map(KatakanaToHiragana::toHiragana);
            return intStream.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        });
    }

    private static int toHiragana(int cp) {
        char c = (char) cp;
        if (isFullWidthKatakana(c)) {
            return (c - 0x60); // code point difference between full-width katakana and hiragana
        } else if (isHalfWidthKatakana(c)) {
            return (c - 0xcf25); // code point difference between half-width katakana and hiragana
        }
        return cp;
    }

    private static boolean isHalfWidthKatakana(char c) {
        return (('\uff66' <= c) && (c <= '\uff9d'));
    }

    private static boolean isFullWidthKatakana(char c) {
        return (('\u30a1' <= c) && (c <= '\u30fe'));
    }
}
