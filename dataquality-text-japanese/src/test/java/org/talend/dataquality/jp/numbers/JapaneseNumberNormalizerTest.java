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
package org.talend.dataquality.jp.numbers;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JapaneseNumberNormalizerTest {

    private static Map<String, String> values = new HashMap<String, String>();

    private static Map<String, String> minusValues = new HashMap<String, String>();

    private static Map<String, String> fractionalValues = new HashMap<String, String>();

    static {
        values.put("", "");
        values.put(" ", " ");
        values.put("null", "null");
        values.put("abc", "abc");
        values.put("a-b123", "a-b123");
        values.put("〇〇七", "7");
        values.put("一〇〇〇", "1000");
        values.put("三千2百２十三", "3223");
        values.put("１．２万３４５．６７", "12345.67");
        values.put("１．２万３４５．６三", "12345.63");
        values.put("4,647.100", "4647.1");
        values.put("15,7", "157");
        values.put("十", "10");
        values.put("百", "100");
        values.put("千", "1000");
        values.put("万", "万");
        values.put("一万", "10000");
        values.put("六万", "60000");
        values.put("億", "億");
        values.put("一億", "100000000");
        values.put("十億", "1000000000");
        values.put("百億", "10000000000");
        values.put("五百億", "50000000000");
        values.put("千億", "100000000000");
        values.put("三千億", "300000000000");
        values.put("万億", "万億");
        values.put("兆", "兆");
        values.put("一兆", "1000000000000");
        values.put("万兆", "万兆");
        values.put("京", "京");
        values.put("一京", "10000000000000000");
        values.put("万京", "万京");
        values.put("垓", "垓");
        values.put("一垓", "100000000000000000000");
        values.put("万垓", "万垓");
        values.put("九百八十三万 六千七百三", "9836703");
        values.put("二十億 三千六百五十二万 千八百一", "2036521801");
        values.put("abc二十億 三千六百五十二万 千八百一", "abc2036521801");
        values.put("二十億 三千六百五十二万 千八百一abc", "2036521801abc");
        values.put("abc二十億 三千六百五十二万 千八百一def", "abc2036521801def");
        values.put("二十億 三千六百a五十二万 千八百一", "二十億 三千六百a五十二万 千八百一");
        values.put("七十五點四零二五", "75.4025");
        values.put("百二十三円", "123円");
        values.put("¥百二十三", "¥123");
        values.put("五八五、四〇〇", "585,400");
        values.put("百五十七・五", "157.5");
        values.put("百五十七点五", "157.5");
        values.put("百五十七点〇", "157");

        minusValues.put("-３．２千", "-3200");
        minusValues.put("負一千一百五十八", "負1158");
        minusValues.put("負1千一百五十八", "負1158");
        minusValues.put("負一千1百五十八", "負1158");
        minusValues.put("負一千一百5十八", "負1158");
        minusValues.put("負一千一百五十8", "負1158");
        minusValues.put("マイナス一千一百五十八", "-1158");
        minusValues.put("マイナス二分の一", "-1/2");
        minusValues.put("マイナス百五十八", "-158");
        minusValues.put("マイナス三〇〇〇", "-3000");
        minusValues.put("マイナス一万分の一", "-1/10000");
        minusValues.put("マイナス一十分の一", "-1/10");
        minusValues.put("マイナス十分の一", "-1/10");
        minusValues.put("マイナス百分の一", "-1/100");
        minusValues.put("マイナス一百分の一", "-1/100");
        minusValues.put("マイナス千分の一", "-1/1000");
        minusValues.put("マイナス一千分の一", "-1/1000");
        minusValues.put("マイナス57マイナス", "マイナス57マイナス");
        minusValues.put("マイナス万分の一", "マイナス万分の一");
        minusValues.put("マイナス億分の一", "マイナス億分の一");
        minusValues.put("マイナス兆分の一", "マイナス兆分の一");
        minusValues.put("マイナス京分の一", "マイナス京分の一");
        minusValues.put("-３．２千分の一", "-３．２千分の一");
        minusValues.put("マイナス一億分の一", "-1/100000000");
        minusValues.put("マイナス一兆分の一", "-1/1000000000000");
        minusValues.put("マイナス一京分の一", "-1/10000000000000000");
        minusValues.put("マイナス四万分の一", "-1/40000");
        minusValues.put("マイナス七十万分の一", "-1/700000");

        fractionalValues.put("二分の一", "1/2");
        fractionalValues.put("三分の二", "2/3");
        fractionalValues.put("百分の百七十", "170/100");
        fractionalValues.put("二分の", "二分の");
        fractionalValues.put("九百八十三万 六千七百三 分の 一千一百五十八", "1158/9836703");
        fractionalValues.put("分の二", "分の二");
        fractionalValues.put("二分七", "二分七");
        fractionalValues.put("百分の百七十分の百", "百分の百七十分の百");
        fractionalValues.put("十分の一", "1/10");
        fractionalValues.put("百分の一", "1/100");
        fractionalValues.put("千分の一", "1/1000");
        fractionalValues.put("五千分の五", "5/5000");
        fractionalValues.put("万分の一", "万分の一");
        fractionalValues.put("一万分の一", "1/10000");
        fractionalValues.put("八万分の一千", "1000/80000");
        fractionalValues.put("億分の一", "億分の一");
        fractionalValues.put("一億分の億", "一億分の億");
        fractionalValues.put("一億分の一", "1/100000000");
        fractionalValues.put("兆分の一", "兆分の一");
        fractionalValues.put("万兆分の一", "万兆分の一");
        fractionalValues.put("一兆分の一", "1/1000000000000");
        fractionalValues.put("百分の二百七十五点五", "百分の二百七十五点五");
        fractionalValues.put("百分の二百七十五点〇", "百分の二百七十五点〇");
    }

    private JapaneseNumberNormalizer japaneseNumberFilter;

    @Before
    public void init() {
        japaneseNumberFilter = new JapaneseNumberNormalizer();
    }

    @Test
    public void number() {
        for (String number : values.keySet()) {
            Assert.assertEquals(values.get(number), japaneseNumberFilter.normalizeNumber(number));
        }
    }

    @Test
    public void testMinusNumber() {
        for (String number : minusValues.keySet()) {
            Assert.assertEquals(minusValues.get(number), japaneseNumberFilter.normalizeNumber(number));
        }
    }

    @Test
    public void testFractionalNumber() {
        for (String number : fractionalValues.keySet()) {
            Assert.assertEquals(fractionalValues.get(number), japaneseNumberFilter.normalizeNumber(number));
        }
    }

}
