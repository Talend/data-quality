package org.talend.dataquality.common.pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.regex.Pattern;

import org.junit.Test;

public class TextPatternUtilTest {

    private int globalCount = 0;

    @Test
    public void testFindPattern() {

        checkPattern("abc-d", "aaa-a");
        checkPattern("Straße", "Aaaaaa");
        checkPattern("トンキン", "KKKK");
        checkPattern("とうきょう", "HHHhH");
        checkPattern("서울", "GG");
        checkPattern("北京", "CC");
    }

    private void checkPattern(String input, String expectedOutput) {
        assertEquals(expectedOutput, TextPatternUtil.findPattern(input));
    }

    @Test
    public void replaceCharacterLowerLatin() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.LOWER_LATIN;
        int start = globalCount;
        replaceCharMatch('a', 'z', charPatternToRegexEnum, random);
        replaceCharMatch('ß', 'ö', charPatternToRegexEnum, random);
        replaceCharMatch('ø', 'ÿ', charPatternToRegexEnum, random);
        int end = globalCount;
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), end - start,
                charPatternToRegexEnum.getSize());
    }

    @Test
    public void replaceCharacterUpperLatin() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.UPPER_LATIN;
        int start = globalCount;
        replaceCharMatch('A', 'Z', charPatternToRegexEnum, random);
        replaceCharMatch('À', 'Ö', charPatternToRegexEnum, random);
        replaceCharMatch('Ø', 'Þ', charPatternToRegexEnum, random);
        int end = globalCount;
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), end - start,
                charPatternToRegexEnum.getSize());
    }

    @Test
    public void replaceCharacterLowerHiragana() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.LOWER_HIRAGANA;
        int start = globalCount;
        int[] characters = { 0x3041, 0x3043, 0x3045, 0x3047, 0x3049, 0x3063, 0x3083, 0x3085, 0x3087, 0x308E, 0x3095, 0x3096 };
        for (int position : characters)
            replaceCharMatch((char) position, charPatternToRegexEnum, random);
        int end = globalCount;
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), end - start,
                charPatternToRegexEnum.getSize());
    }

    @Test
    public void replaceCharacterUpperHiragana() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.UPPER_HIRAGANA;
        int start = globalCount;
        int[] characters = { 0x3042, 0x3044, 0x3046, 0x3048, 0x3084, 0x3086 };
        for (int position : characters)
            replaceCharMatch((char) position, charPatternToRegexEnum, random);

        replaceCharMatch((char) 0x304A, (char) 0x3062, charPatternToRegexEnum, random);
        replaceCharMatch((char) 0x3064, (char) 0x3082, charPatternToRegexEnum, random);
        replaceCharMatch((char) 0x3088, (char) 0x308D, charPatternToRegexEnum, random);
        replaceCharMatch((char) 0x308F, (char) 0x3094, charPatternToRegexEnum, random);
        int end = globalCount;
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), end - start,
                charPatternToRegexEnum.getSize());
    }

    @Test
    public void replaceCharacterLowerKatakana() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.LOWER_KATAKANA;
        int start = globalCount;
        int[] characters = { 0x30A1, 0x30A3, 0x30A5, 0x30A7, 0x30A9, 0x30C3, 0x30E3, 0x30E5, 0x30E7, 0x30EE, 0x30F5, 0x30F6 };
        for (int position : characters)
            replaceCharMatch((char) position, charPatternToRegexEnum, random);

        replaceCharMatch((char) 0x31F0, (char) 0x31FF, charPatternToRegexEnum, random);
        replaceCharMatch((char) 0xFF67, (char) 0xFF6F, charPatternToRegexEnum, random);
        int end = globalCount;
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), end - start,
                charPatternToRegexEnum.getSize());
    }

    @Test
    public void replaceCharacterUpperKatakana() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.UPPER_KATAKANA;
        int start = globalCount;
        int[] characters = { 0x30A2, 0x30A4, 0x30A6, 0x30A8, 0x30E4, 0x30E6, 0xFF66 };
        for (int position : characters)
            replaceCharMatch((char) position, charPatternToRegexEnum, random);

        replaceCharMatch((char) 0x30AA, (char) 0x30C2, charPatternToRegexEnum, random);
        replaceCharMatch((char) 0x30C4, (char) 0x30E2, charPatternToRegexEnum, random);
        replaceCharMatch((char) 0x30E8, (char) 0x30ED, charPatternToRegexEnum, random);
        replaceCharMatch((char) 0x30EF, (char) 0x30F4, charPatternToRegexEnum, random);
        replaceCharMatch((char) 0x30F7, (char) 0x30FA, charPatternToRegexEnum, random);
        replaceCharMatch((char) 0xFF71, (char) 0xFF9D, charPatternToRegexEnum, random);
        int end = globalCount;
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), end - start,
                charPatternToRegexEnum.getSize());
    }

    @Test
    public void replaceCharacterKanji() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.KANJI;
        int start = globalCount;
        replaceCharMatch((char) 0x4E00, (char) 0x9FBF, charPatternToRegexEnum, random);
        int end = globalCount;
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), end - start,
                charPatternToRegexEnum.getSize());
    }

    @Test
    public void replaceCharacterHangul() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.HANGUL;
        int start = globalCount;
        replaceCharMatch((char) 0xAC00, (char) 0xD7AF, charPatternToRegexEnum, random);
        int end = globalCount;
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), end - start,
                charPatternToRegexEnum.getSize());
    }

    private void replaceCharMatch(char character, CharPatternToRegexEnum charPatternToRegexEnum, Random random) {
        replaceCharMatch(character, character, charPatternToRegexEnum, random);
    }

    private void replaceCharMatch(char rangeStart, char rangeEnd, CharPatternToRegexEnum charPatternToRegexEnum, Random random) {
        Pattern pattern = Pattern.compile(charPatternToRegexEnum.getPattern());
        for (char c = rangeStart; c <= rangeEnd; c++) {
            char output = TextPatternUtil.replaceCharacter(c, random);
            globalCount++;
            assertTrue(String.format("Pattern %s won't match %s", pattern, output),
                    pattern.matcher(String.valueOf(output)).find());
        }
    }

}
