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
import org.junit.Test;

public class JapaneseNumberNormalizerTest {

    private static Map<String, String> values = new HashMap<String, String>();

    static {
        values.put("", "");
        values.put("abc", "abc");
        values.put("a-b123", "a-b123");
        values.put("〇〇七", "7");
        values.put("一〇〇〇", "1000");
        values.put("三千2百２十三", "3223");
        values.put("-３．２千", "-3200");
        values.put("１．２万３４５．６７", "12345.67");
        values.put("１．２万３４５．６三", "12345.63");
        values.put("4,647.100", "4647.1");
        values.put("15,7", "157");
        values.put("万", "10000");
        values.put("一万", "10000");
        values.put("億", "100000000");
        values.put("兆", "1000000000000");
        values.put("京", "10000000000000000");
        values.put("垓", "100000000000000000000");
        values.put("九百八十三万 六千七百三", "9836703");
        values.put("二十億 三千六百五十二万 千八百一", "2036521801");
        values.put("abc二十億 三千六百五十二万 千八百一", "abc2036521801");
        values.put("二十億 三千六百五十二万 千八百一abc", "2036521801abc");
        values.put("abc二十億 三千六百五十二万 千八百一def", "abc2036521801def");
        values.put("二十億 三千六百a五十二万 千八百一", "二十億 三千六百a五十二万 千八百一");
        values.put("七十五點四零二五", "75.4025");
        values.put("負一千一百五十八", "-1158");
        values.put("百二十三円", "123円");
        values.put("¥百二十三", "¥123");
        values.put("五八五、四〇〇", "585,400");
        values.put("百五十七・五", "157.5");
        values.put("二分の一", "1/2");
        values.put("三分の二", "2/3");
        values.put("百分の百七十", "170/100");
        values.put("二分の", "二分の");
        values.put("九百八十三万 六千七百三 分の 一千一百五十八", "1158/9836703");
        values.put("分の二", "分の二");
        values.put("二分七", "二分七");
        values.put("負二分の一", "-1/2");
        values.put("負分の一", "負分の一");
        values.put("-３．２千分の一", "-1/3200");
    }

    @Test
    public void number() {

        JapaneseNumberNormalizer japaneseNumberFilter = new JapaneseNumberNormalizer();
        for (String number : values.keySet()) {
            Assert.assertEquals(values.get(number), japaneseNumberFilter.normalizeNumber(number));
        }
    }
}
