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

public class JapaneseNumberNormalizationTest {

    private static Map<String, String> values = new HashMap<String, String>() {

        {
            put("〇〇七", "7");
            put("一〇〇〇", "1000");
            put("三千2百２十三", "3223");
            put("３．２千", "3200");
            put("１．２万３４５．６７", "12345.67");
            put("１．２万３４５．６三", "12345.63");
            put("4,647.100", "4647.1");
            put("15,7", "157");
            put("万", "10000");
            put("一万", "10000");
            put("億", "100000000");
            put("兆", "1000000000000");
            put("京", "10000000000000000");
            put("垓", "100000000000000000000");
            put("九百八十三万 六千七百三", "9836703");
            put("二十億 三千六百五十二万 千八百一", "2036521801");
        }
    };

    @Test
    public void number() {

        JapaneseNumberNormalization japaneseNumberFilter = new JapaneseNumberNormalization();
        for (String number : values.keySet()) {
            Assert.assertEquals(values.get(number), japaneseNumberFilter.normalizeNumber(number));
        }
    }
}
