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

import org.junit.Test;

public class KanaUtilsTest {

    @Test
    public void half2FullKatakanaTest() {
        assertEquals("ツイッター", KanaUtils.half2FullKatakana("ﾂｲｯﾀｰ"));
        assertEquals("シュワルツェネッガー", KanaUtils.half2FullKatakana("ｼｭﾜﾙﾂｪﾈｯｶﾞｰ"));
        assertEquals("パパ", KanaUtils.half2FullKatakana("ﾊﾟﾊﾟ"));
        assertEquals("ピャニッチ", KanaUtils.half2FullKatakana("ﾋﾟｬﾆｯﾁ"));
        assertEquals("フィジカル", KanaUtils.half2FullKatakana("ﾌｨｼﾞｶﾙ"));
    }

    @Test
    public void hiragana2FullKatakanaTest() {
        assertEquals("ガギグゲゴ", KanaUtils.hiragana2FullKatakana("がぎぐげご"));
        assertEquals("パピプペポ", KanaUtils.hiragana2FullKatakana("ぱぴぷぺぽ"));
        assertEquals("キャキュキョ", KanaUtils.hiragana2FullKatakana("きゃきゅきょ"));
        assertEquals("ギャギュギョ", KanaUtils.hiragana2FullKatakana("ぎゃぎゅぎょ"));
        assertEquals("ピャピュピョ", KanaUtils.hiragana2FullKatakana("ぴゃぴゅぴょ"));
        assertEquals("ヽヾヿ", KanaUtils.hiragana2FullKatakana("ゝゞゟ"));
    }
}
