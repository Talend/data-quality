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
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KatakanaToRomaji {

    private static final Logger LOGGER = LoggerFactory.getLogger(KatakanaToRomaji.class);

    protected static final Map<String, String[]> KATAKANA_TO_ROMAJI = new HashMap<>();

    // see https://en.wikipedia.org/wiki/Romanization_of_Japanese#Differences_among_romanizations
    static {
        KATAKANA_TO_ROMAJI.put("ア", new String[] { "a" });
        KATAKANA_TO_ROMAJI.put("イ", new String[] { "i" });
        KATAKANA_TO_ROMAJI.put("ウ", new String[] { "u" });
        KATAKANA_TO_ROMAJI.put("エ", new String[] { "e" });
        KATAKANA_TO_ROMAJI.put("オ", new String[] { "o" });

        KATAKANA_TO_ROMAJI.put("ャ", new String[] { "ya" });
        KATAKANA_TO_ROMAJI.put("ュ", new String[] { "yu" });
        KATAKANA_TO_ROMAJI.put("ョ", new String[] { "yo" });

        KATAKANA_TO_ROMAJI.put("カ", new String[] { "ka" });
        KATAKANA_TO_ROMAJI.put("キ", new String[] { "ki" });
        KATAKANA_TO_ROMAJI.put("ク", new String[] { "ku" });
        KATAKANA_TO_ROMAJI.put("ケ", new String[] { "ke" });
        KATAKANA_TO_ROMAJI.put("コ", new String[] { "ko" });

        KATAKANA_TO_ROMAJI.put("キャ", new String[] { "kya" });
        KATAKANA_TO_ROMAJI.put("キュ", new String[] { "kyu" });
        KATAKANA_TO_ROMAJI.put("キョ", new String[] { "kyo" });

        KATAKANA_TO_ROMAJI.put("サ", new String[] { "sa" });
        KATAKANA_TO_ROMAJI.put("シ", new String[] { "shi", "si" });
        KATAKANA_TO_ROMAJI.put("ス", new String[] { "su" });
        KATAKANA_TO_ROMAJI.put("セ", new String[] { "se" });
        KATAKANA_TO_ROMAJI.put("ソ", new String[] { "so" });

        KATAKANA_TO_ROMAJI.put("シャ", new String[] { "sha", "sya" });
        KATAKANA_TO_ROMAJI.put("シュ", new String[] { "shu", "syu" });
        KATAKANA_TO_ROMAJI.put("ショ", new String[] { "sho", "syo" });

        KATAKANA_TO_ROMAJI.put("タ", new String[] { "ta" });
        KATAKANA_TO_ROMAJI.put("チ", new String[] { "chi", "ti" });
        KATAKANA_TO_ROMAJI.put("ツ", new String[] { "tsu", "tu" });
        KATAKANA_TO_ROMAJI.put("テ", new String[] { "te" });
        KATAKANA_TO_ROMAJI.put("ト", new String[] { "to" });

        KATAKANA_TO_ROMAJI.put("チャ", new String[] { "cha", "tya" });
        KATAKANA_TO_ROMAJI.put("チュ", new String[] { "chu", "tyu" });
        KATAKANA_TO_ROMAJI.put("チョ", new String[] { "cho", "tyo" });

        KATAKANA_TO_ROMAJI.put("ナ", new String[] { "na" });
        KATAKANA_TO_ROMAJI.put("ニ", new String[] { "ni" });
        KATAKANA_TO_ROMAJI.put("ヌ", new String[] { "nu" });
        KATAKANA_TO_ROMAJI.put("ネ", new String[] { "ne" });
        KATAKANA_TO_ROMAJI.put("ノ", new String[] { "no" });

        KATAKANA_TO_ROMAJI.put("ニャ", new String[] { "nya" });
        KATAKANA_TO_ROMAJI.put("ニュ", new String[] { "nyu" });
        KATAKANA_TO_ROMAJI.put("ニョ", new String[] { "nyo" });

        KATAKANA_TO_ROMAJI.put("ハ", new String[] { "ha" });
        KATAKANA_TO_ROMAJI.put("ヒ", new String[] { "hi" });
        KATAKANA_TO_ROMAJI.put("フ", new String[] { "fu", "hu" });
        KATAKANA_TO_ROMAJI.put("ヘ", new String[] { "he" });
        KATAKANA_TO_ROMAJI.put("ホ", new String[] { "ho" });

        KATAKANA_TO_ROMAJI.put("ヒャ", new String[] { "hya" });
        KATAKANA_TO_ROMAJI.put("ヒュ", new String[] { "hyu" });
        KATAKANA_TO_ROMAJI.put("ヒョ", new String[] { "hyo" });

        KATAKANA_TO_ROMAJI.put("マ", new String[] { "ma" });
        KATAKANA_TO_ROMAJI.put("ミ", new String[] { "mi" });
        KATAKANA_TO_ROMAJI.put("ム", new String[] { "mu" });
        KATAKANA_TO_ROMAJI.put("メ", new String[] { "me" });
        KATAKANA_TO_ROMAJI.put("モ", new String[] { "mo" });

        KATAKANA_TO_ROMAJI.put("ミャ", new String[] { "mya" });
        KATAKANA_TO_ROMAJI.put("ミュ", new String[] { "myu" });
        KATAKANA_TO_ROMAJI.put("ミョ", new String[] { "myo" });

        KATAKANA_TO_ROMAJI.put("ヤ", new String[] { "ya" });
        KATAKANA_TO_ROMAJI.put("ユ", new String[] { "yu" });
        KATAKANA_TO_ROMAJI.put("ヨ", new String[] { "yo" });

        KATAKANA_TO_ROMAJI.put("ラ", new String[] { "ra" });
        KATAKANA_TO_ROMAJI.put("リ", new String[] { "ri" });
        KATAKANA_TO_ROMAJI.put("ル", new String[] { "ru" });
        KATAKANA_TO_ROMAJI.put("レ", new String[] { "re" });
        KATAKANA_TO_ROMAJI.put("ロ", new String[] { "ro" });

        KATAKANA_TO_ROMAJI.put("リャ", new String[] { "rya" });
        KATAKANA_TO_ROMAJI.put("リュ", new String[] { "ryu" });
        KATAKANA_TO_ROMAJI.put("リョ", new String[] { "ryo" });

        KATAKANA_TO_ROMAJI.put("ワ", new String[] { "wa" });

        KATAKANA_TO_ROMAJI.put("ヰ", new String[] { "i", "wi", "i" });
        KATAKANA_TO_ROMAJI.put("ヱ", new String[] { "e", "we", "e" });
        KATAKANA_TO_ROMAJI.put("ヲ", new String[] { "o", "wo", "o" });

        KATAKANA_TO_ROMAJI.put("ン", new String[] { "n" });

        KATAKANA_TO_ROMAJI.put("ガ", new String[] { "ga" });
        KATAKANA_TO_ROMAJI.put("ギ", new String[] { "gi" });
        KATAKANA_TO_ROMAJI.put("グ", new String[] { "gu" });
        KATAKANA_TO_ROMAJI.put("ゲ", new String[] { "ge" });
        KATAKANA_TO_ROMAJI.put("ゴ", new String[] { "go" });

        KATAKANA_TO_ROMAJI.put("ギャ", new String[] { "gya" });
        KATAKANA_TO_ROMAJI.put("ギュ", new String[] { "gyu" });
        KATAKANA_TO_ROMAJI.put("ギョ", new String[] { "gyo" });

        KATAKANA_TO_ROMAJI.put("ザ", new String[] { "za" });
        KATAKANA_TO_ROMAJI.put("ジ", new String[] { "ji", "zi" });
        KATAKANA_TO_ROMAJI.put("ズ", new String[] { "zu" });
        KATAKANA_TO_ROMAJI.put("ゼ", new String[] { "ze" });
        KATAKANA_TO_ROMAJI.put("ゾ", new String[] { "zo" });

        KATAKANA_TO_ROMAJI.put("ジャ", new String[] { "ja", "zya" });
        KATAKANA_TO_ROMAJI.put("ジュ", new String[] { "ju", "zyu" });
        KATAKANA_TO_ROMAJI.put("ジョ", new String[] { "jo", "zyo" });

        KATAKANA_TO_ROMAJI.put("ダ", new String[] { "da" });
        KATAKANA_TO_ROMAJI.put("ヂ", new String[] { "ji", "di", "zi" });
        KATAKANA_TO_ROMAJI.put("ヅ", new String[] { "zu", "du", "zu" });
        KATAKANA_TO_ROMAJI.put("デ", new String[] { "de" });
        KATAKANA_TO_ROMAJI.put("ド", new String[] { "do" });

        KATAKANA_TO_ROMAJI.put("ヂャ", new String[] { "ja", "dya", "zya" });
        KATAKANA_TO_ROMAJI.put("ヂュ", new String[] { "ju", "dyu", "zyu" });
        KATAKANA_TO_ROMAJI.put("ヂョ", new String[] { "jo", "dyo", "zyo" });

        KATAKANA_TO_ROMAJI.put("バ", new String[] { "ba" });
        KATAKANA_TO_ROMAJI.put("ビ", new String[] { "bi" });
        KATAKANA_TO_ROMAJI.put("ブ", new String[] { "bu" });
        KATAKANA_TO_ROMAJI.put("ベ", new String[] { "be" });
        KATAKANA_TO_ROMAJI.put("ボ", new String[] { "bo" });

        KATAKANA_TO_ROMAJI.put("ビャ", new String[] { "bya" });
        KATAKANA_TO_ROMAJI.put("ビュ", new String[] { "byu" });
        KATAKANA_TO_ROMAJI.put("ビョ", new String[] { "byo" });

        KATAKANA_TO_ROMAJI.put("パ", new String[] { "pa" });
        KATAKANA_TO_ROMAJI.put("ピ", new String[] { "pi" });
        KATAKANA_TO_ROMAJI.put("プ", new String[] { "pu" });
        KATAKANA_TO_ROMAJI.put("ペ", new String[] { "pe" });
        KATAKANA_TO_ROMAJI.put("ポ", new String[] { "po" });

        KATAKANA_TO_ROMAJI.put("ピャ", new String[] { "pya" });
        KATAKANA_TO_ROMAJI.put("ピュ", new String[] { "pyu" });
        KATAKANA_TO_ROMAJI.put("ピョ", new String[] { "pyo" });

        KATAKANA_TO_ROMAJI.put("ティ", new String[] { "ti" });
        KATAKANA_TO_ROMAJI.put("ディ", new String[] { "di" });
        KATAKANA_TO_ROMAJI.put("ツィ", new String[] { "tsi" });
    }

    protected static Stream<String> convert(Stream<String> katakanaStream, TransliterateType type) {
        return katakanaStream.map(x -> toRomaji(KatakanaUtils.toFullWidth(x), type));
    }

    private static String toRomaji(String s, TransliterateType type) {
        StringBuilder t = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i <= s.length() - 2 && KATAKANA_TO_ROMAJI.containsKey(s.substring(i, i + 2))) {// 2 katakanas combination
                // lookup priority: 2 katakanas combination > single katakana (i.e. ショ > シ)
                t.append(getRomajiByType(s.substring(i, i + 2), type));
                i++;
            } else if (KATAKANA_TO_ROMAJI.containsKey(s.substring(i, i + 1))) { // single katakana
                t.append(getRomajiByType(s.substring(i, i + 1), type));
            } else if (s.charAt(i) == 'ー') { // handle chōonpu: see https://en.wikipedia.org/wiki/Ch%C5%8Donpu
                if (t.length() >= 1) {
                    t.replace(t.length() - 1, t.length(), addMacronMark(t.charAt(t.length() - 1)));
                } else {
                    LOGGER.warn("Token: " + s + " shouldn't start with the chōonpu symbol (¯)");
                }
            } else if (s.charAt(i) == 'ッ') { // handle sokuon: see https://en.wikipedia.org/wiki/Sokuon
                if (i <= s.length() - 2) {
                    t.append(getRomajiByType(s.substring(i + 1, i + 2), type).charAt(0));
                } else {
                    LOGGER.warn("Token: " + s + " shouldn't end by the sokuon symbol (小さなつ)");
                }
            } else { // punctuation or other character
                t.append(s.charAt(i));
            }
        }
        return t.toString();
    }

    private static String getRomajiByType(String key, TransliterateType type) {
        final String[] romajis = KATAKANA_TO_ROMAJI.get(key);
        switch (type) {
        case HEPBURN:
            return romajis[0];
        case NIHON_SHIKI:
            return romajis.length > 1 ? romajis[1] : romajis[0];
        case KUNREI_SHIKI:
            return romajis[romajis.length - 1];
        default: {
            LOGGER.warn("Unknown romanization type: " + type + ". Use Hepburn-romanization instead");
            return romajis[0];
        }
        }
    }

    private static String addMacronMark(char c) {
        switch (c) {
        case 'a':
            return "ā";
        case 'i':
            return "ī";
        case 'u':
            return "ū";
        case 'e':
            return "ē";
        case 'o':
            return "ō";
        default: {
            LOGGER.warn("Unknown chōonpu " + c);
            return "¯";
        }
        }
    }
}
