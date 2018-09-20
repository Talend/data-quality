package org.talend.dataquality.jp.transliteration;

import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

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
        assertEquals("そ゛", result.findFirst().get());
    }
}
