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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TextTransliteratorTest {

    private static final String delimiter = " ";

    private static final TextTransliterator transliterator = TextTransliterator.getInstance();

    private static final List<String> testTextList = new ArrayList<>();
    static {
        testTextList.add("親譲りの無鉄砲で小供の時から損ばかりしている");
        testTextList.add("東京は夜の七時"); // chōonpu:東京; Multi-pronunciation Kana: は
        testTextList.add("くノ一 female ninja"); // mixed hiragana, katakana, kanji, english
        testTextList.add("日本型の顔文字👨‍🎨『笑い』(≧▽≦)富士山／^o^＼"); // emoticon
        testTextList.add("縮む"); // to shrink
        testTextList.add("ﾂｲｯﾀｰ");
        testTextList.add("がぎぐげご");
        testTextList.add("ぱぴぷぺぽ");
        testTextList.add("きゃきゅきょ");
        testTextList.add("ぎゃぎゅぎょ");
        testTextList.add("ぴゃぴゅぴょ");
        testTextList.add("シュワルツェネッガー");
        testTextList.add("フィジカル");
        testTextList.add("母さん");
    }

    @Test
    public void testTransliterateKatakanaReading() {
        List<String> expectedTextList = new ArrayList<>();
        expectedTextList.add("オヤユズリ ノ ムテッポウ デ ショウ キョウ ノ トキ カラ ソン バカリ シ テ イル");
        expectedTextList.add("トウキョウ ハ ヨル ノ ナナ ジ");
        expectedTextList.add("ク ノ イチ female ninja");
        expectedTextList.add("ニッポン ガタ ノ カオ モジ 👨 ‍ 🎨 『 ワライ 』(≧▽≦) フジサン ／^ o ^＼");
        expectedTextList.add("チヂム");
        expectedTextList.add("ﾂｲｯﾀｰ");
        expectedTextList.add("ガ ギグゲゴ");
        expectedTextList.add("パピプペポ");
        expectedTextList.add("キ ャキュキョ");
        expectedTextList.add("ギャギュギョ");
        expectedTextList.add("ピャピュピョ");
        expectedTextList.add("シュワルツェネッガー");
        expectedTextList.add("フィジカル");
        expectedTextList.add("カアサン");

        for (int i = 0; i < testTextList.size(); i++) {
            final String katakanaReading = transliterator.transliterate(testTextList.get(i), TransliterateType.KATAKANA_READING,
                    delimiter);
            assertEquals(expectedTextList.get(i), katakanaReading);
        }
    }

    @Test
    public void testTransliterateKatakanaPronunciation() {
        List<String> expectedTextList = new ArrayList<>();
        expectedTextList.add("オヤユズリ ノ ムテッポー デ ショー キョー ノ トキ カラ ソン バカリ シ テ イル");
        expectedTextList.add("トーキョー ワ ヨル ノ ナナ ジ");
        expectedTextList.add("ク ノ イチ female ninja");
        expectedTextList.add("ニッポン ガタ ノ カオ モジ 👨 ‍ 🎨 『 ワライ 』(≧▽≦) フジサン ／^ o ^＼");
        expectedTextList.add("チジム");
        expectedTextList.add("ﾂｲｯﾀｰ");
        expectedTextList.add("ガ ギグゲゴ");
        expectedTextList.add("パピプペポ");
        expectedTextList.add("キ ャキュキョ");
        expectedTextList.add("ギャギュギョ");
        expectedTextList.add("ピャピュピョ");
        expectedTextList.add("シュワルツェネッガー");
        expectedTextList.add("フィジカル");
        expectedTextList.add("カーサン");

        for (int i = 0; i < testTextList.size(); i++) {
            final String katakanaPronunciation = transliterator.transliterate(testTextList.get(i),
                    TransliterateType.KATAKANA_PRONUNCIATION);
            assertEquals(expectedTextList.get(i), katakanaPronunciation);
        }
    }

    @Test
    public void testTransliterateHiragana() {
        List<String> expectedTextList = new ArrayList<>();
        expectedTextList.add("おやゆずり の むてっぽう で しょう きょう の とき から そん ばかり し て いる");
        expectedTextList.add("とうきょう は よる の なな じ");
        expectedTextList.add("く の いち female ninja");
        expectedTextList.add("にっぽん がた の かお もじ 👨 ‍ 🎨 『 わらい 』(≧▽≦) ふじさん ／^ o ^＼");
        expectedTextList.add("ちぢむ");
        expectedTextList.add("ついったあ");
        expectedTextList.add("が ぎぐげご");
        expectedTextList.add("ぱぴぷぺぽ");
        expectedTextList.add("き ゃきゅきょ");
        expectedTextList.add("ぎゃぎゅぎょ");
        expectedTextList.add("ぴゃぴゅぴょ");
        expectedTextList.add("しゅわるつぇねっがあ");
        expectedTextList.add("ふぃじかる");
        expectedTextList.add("かあさん");

        for (int i = 0; i < testTextList.size(); i++) {
            final String hiragana = transliterator.transliterate(testTextList.get(i), TransliterateType.HIRAGANA);
            assertEquals(expectedTextList.get(i), hiragana);
        }
    }

    @Test
    public void testTransliterateHepburn() {
        List<String> expectedTextList = new ArrayList<>();
        expectedTextList.add("oyayuzuri no muteppō de shō kyō no toki kara son bakari shi te iru");
        expectedTextList.add("tōkyō wa yoru no nana ji");
        expectedTextList.add("ku no ichi female ninja");
        expectedTextList.add("nippon gata no kao moji 👨 ‍ 🎨 『 warai 』(≧▽≦) fujisan ／^ o ^＼");
        expectedTextList.add("chijimu");
        expectedTextList.add("tsuittā");
        expectedTextList.add("ga gigugego");
        expectedTextList.add("papipupepo");
        expectedTextList.add("ki yakyukyo");
        expectedTextList.add("gyagyugyo");
        expectedTextList.add("pyapyupyo");
        expectedTextList.add("shuwarutsueneggā");
        expectedTextList.add("fuijikaru");
        expectedTextList.add("kāsan");

        for (int i = 0; i < testTextList.size(); i++) {
            final String hepburn = transliterator.transliterate(testTextList.get(i), TransliterateType.HEPBURN);
            assertEquals(expectedTextList.get(i), hepburn);
        }

    }

    @Test
    public void testTransliterateKunreiShiki() {
        List<String> expectedTextList = new ArrayList<>();
        expectedTextList.add("oyayuzuri no muteppô de syô kyô no toki kara son bakari si te iru");
        expectedTextList.add("tôkyô wa yoru no nana zi");
        expectedTextList.add("ku no iti female ninja");
        expectedTextList.add("nippon gata no kao mozi 👨 ‍ 🎨 『 warai 』(≧▽≦) huzisan ／^ o ^＼");
        expectedTextList.add("tizimu");
        expectedTextList.add("tuittâ");
        expectedTextList.add("ga gigugego");
        expectedTextList.add("papipupepo");
        expectedTextList.add("ki yakyukyo");
        expectedTextList.add("gyagyugyo");
        expectedTextList.add("pyapyupyo");
        expectedTextList.add("syuwarutsueneggâ");
        expectedTextList.add("fuizikaru");
        expectedTextList.add("kâsan");

        for (int i = 0; i < testTextList.size(); i++) {
            final String kunrei_shiki = transliterator.transliterate(testTextList.get(i), TransliterateType.KUNREI_SHIKI);
            assertEquals(expectedTextList.get(i), kunrei_shiki);
        }
    }

    @Test
    public void testTransliterateNihonShiki() {
        List<String> expectedTextList = new ArrayList<>();
        expectedTextList.add("oyayuzuri no muteppô de syô kyô no toki kara son bakari si te iru");
        expectedTextList.add("tôkyô wa yoru no nana zi");
        expectedTextList.add("ku no iti female ninja");
        expectedTextList.add("nippon gata no kao mozi 👨 ‍ 🎨 『 warai 』(≧▽≦) huzisan ／^ o ^＼");
        expectedTextList.add("tizimu");
        expectedTextList.add("tuittâ");
        expectedTextList.add("ga gigugego");
        expectedTextList.add("papipupepo");
        expectedTextList.add("ki yakyukyo");
        expectedTextList.add("gyagyugyo");
        expectedTextList.add("pyapyupyo");
        expectedTextList.add("syuwarutsueneggâ");
        expectedTextList.add("fuizikaru");
        expectedTextList.add("kâsan");

        for (int i = 0; i < testTextList.size(); i++) {
            final String nihon_shiki = transliterator.transliterate(testTextList.get(i), TransliterateType.NIHON_SHIKI);
            assertEquals(expectedTextList.get(i), nihon_shiki);
        }
    }

    @Test
    public void testChoonpuHiragana() {
        Map<String, String> tests = new HashMap<>();
        tests.put("ローマ字", "ろおまじ"); // Rōmaji (Roman letters)
        tests.put("エレベーター", "えれべえたあ"); // Erebētā (Elevator)
        tests.put("モーターカー", "もおたあかあ"); // Mōtākā (Motor car)
        tests.put("スポーツカーシリーズ", "すぽおつかあ しりいず"); // Supōtsukā shirīzu (Sports car series)
        tests.put("クーデター", "くうでたあ"); // Kūdetā (Coup d'etat)
        tests.put("ラーメン", "らあめん"); // Rāmen
        tests.put("らーめん", "ら ー めん"); // Rāmen (kuromoji return tokens: ら| ー| めん)
        tests.put("モールス信号 ・・ ・ー ーー ・・・ ーーー ・ー・ ー・ー", // Mōrusu shingō ... (Morse code ...)
                "もおるす しんごう ・ ・ ・ー ーー ・ ・ ・ ーーー ・ー・ ー・ー");

        for (Map.Entry<String, String> t : tests.entrySet()) {
            assertEquals(t.getValue(), transliterator.transliterate(t.getKey(), TransliterateType.HIRAGANA));
        }

    }
}