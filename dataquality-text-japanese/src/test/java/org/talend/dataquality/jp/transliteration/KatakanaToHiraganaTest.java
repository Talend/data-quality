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

import java.util.stream.Stream;

import org.junit.Test;

public class KatakanaToHiraganaTest {

    @Test
    public void convertHalfWidth() {
        Stream<String> result = KatakanaToHiragana.convert(Stream.generate(() -> "ｲ"));
        assertEquals("い", result.findFirst().get());
    }

    @Test
    public void convertFullWidth() {
        Stream<String> result = KatakanaToHiragana.convert(Stream.generate(() -> "イ"));
        assertEquals("い", result.findFirst().get());
    }

    @Test
    public void convertSpecialCharacters() {
        Stream<String> result = KatakanaToHiragana.convert(Stream.generate(() -> "ヷヸヹヺ・"));
        assertEquals("ゔぁゔぃゔぇゔぉ・", result.findFirst().get());
    }

    @Test
    public void convertString() {
        Stream<String> result = KatakanaToHiragana.convert(Stream.generate(() -> "ｿﾞ"));
        assertEquals("ぞ", result.findFirst().get());
    }
}
