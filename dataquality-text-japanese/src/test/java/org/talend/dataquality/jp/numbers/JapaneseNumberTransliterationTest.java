package org.talend.dataquality.jp.numbers;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class JapaneseNumberTransliterationTest {

    @Test
    public void number() {

        Map<String, String> values = new HashMap<String, String>() {

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

        JapaneseNumberTransliteration japaneseNumberFilter = new JapaneseNumberTransliteration();
        for (String number : values.keySet()) {
            Assert.assertEquals(values.get(number), japaneseNumberFilter.normalizeNumber(number));
        }
    }
}
