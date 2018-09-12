package org.talend.dataquality.common.pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

public class TextPatternUtilTest {

    private int globalCount;

    @Before
    public void init() {
        globalCount = 0;
    }

    @Test
    public void testFindPattern() {

        checkPattern("abc-d", "aaa-a");
        checkPattern("Straße", "Aaaaaa");
        checkPattern("トンキン", "KKKK");
        checkPattern("とうきょう", "HHHhH");
        checkPattern("서울", "GG");
        checkPattern("北京", "CC");
        checkPattern("\uD840\uDC00", "C");
        checkPattern("\uD840\uDC40\uD840\uDFD3\uD841\uDC01\uD840\uDFD3", "CCCC");

    }

    private void checkPattern(String input, String expectedOutput) {
        assertEquals(expectedOutput, TextPatternUtil.findPattern(input));
    }

    @Test
    public void replaceCharacterLowerLatin() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.LOWER_LATIN;
        replaceCharMatch((int) 'a', (int) 'z', charPatternToRegexEnum, random);
        replaceCharMatch((int) 'ß', (int) 'ö', charPatternToRegexEnum, random);
        replaceCharMatch((int) 'ø', (int) 'ÿ', charPatternToRegexEnum, random);
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), globalCount,
                charPatternToRegexEnum.getCodePointSize());
    }

    @Test
    public void replaceCharacterUpperLatin() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.UPPER_LATIN;
        replaceCharMatch((int) 'A', (int) 'Z', charPatternToRegexEnum, random);
        replaceCharMatch((int) 'À', (int) 'Ö', charPatternToRegexEnum, random);
        replaceCharMatch((int) 'Ø', (int) 'Þ', charPatternToRegexEnum, random);
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), globalCount,
                charPatternToRegexEnum.getCodePointSize());
    }

    @Test
    public void replaceCharacterLowerHiragana() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.LOWER_HIRAGANA;
        int[] characters = { 0x3041, 0x3043, 0x3045, 0x3047, 0x3049, 0x3063, 0x3083, 0x3085, 0x3087, 0x308E, 0x3095, 0x3096 };
        for (int position : characters)
            replaceCharMatch(position, charPatternToRegexEnum, random);
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), globalCount,
                charPatternToRegexEnum.getCodePointSize());
    }

    @Test
    public void replaceCharacterUpperHiragana() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.UPPER_HIRAGANA;
        int[] characters = { 0x3042, 0x3044, 0x3046, 0x3048, 0x3084, 0x3086 };
        for (int position : characters)
            replaceCharMatch(position, charPatternToRegexEnum, random);

        replaceCharMatch(0x304A, 0x3062, charPatternToRegexEnum, random);
        replaceCharMatch(0x3064, 0x3082, charPatternToRegexEnum, random);
        replaceCharMatch(0x3088, 0x308D, charPatternToRegexEnum, random);
        replaceCharMatch(0x308F, 0x3094, charPatternToRegexEnum, random);
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), globalCount,
                charPatternToRegexEnum.getCodePointSize());
    }

    @Test
    public void replaceCharacterLowerKatakana() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.LOWER_KATAKANA;
        int[] characters = { 0x30A1, 0x30A3, 0x30A5, 0x30A7, 0x30A9, 0x30C3, 0x30E3, 0x30E5, 0x30E7, 0x30EE, 0x30F5, 0x30F6 };
        for (int position : characters)
            replaceCharMatch(position, charPatternToRegexEnum, random);

        replaceCharMatch(0x31F0, 0x31FF, charPatternToRegexEnum, random);
        replaceCharMatch(0xFF67, 0xFF6F, charPatternToRegexEnum, random);
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), globalCount,
                charPatternToRegexEnum.getCodePointSize());
    }

    @Test
    public void replaceCharacterUpperKatakana() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.UPPER_KATAKANA;
        int[] characters = { 0x30A2, 0x30A4, 0x30A6, 0x30A8, 0x30E4, 0x30E6, 0xFF66 };
        for (int position : characters)
            replaceCharMatch(position, charPatternToRegexEnum, random);

        replaceCharMatch(0x30AA, 0x30C2, charPatternToRegexEnum, random);
        replaceCharMatch(0x30C4, 0x30E2, charPatternToRegexEnum, random);
        replaceCharMatch(0x30E8, 0x30ED, charPatternToRegexEnum, random);
        replaceCharMatch(0x30EF, 0x30F4, charPatternToRegexEnum, random);
        replaceCharMatch(0x30F7, 0x30FA, charPatternToRegexEnum, random);
        replaceCharMatch(0xFF71, 0xFF9D, charPatternToRegexEnum, random);
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), globalCount,
                charPatternToRegexEnum.getCodePointSize());
    }

    @Test
    public void replaceCharacterKanji() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.KANJI;
        replaceCharMatch(0x4E00, 0x9FEF, charPatternToRegexEnum, random);
        replaceCharMatch(0x3400, 0x4DB5, charPatternToRegexEnum, random); // Extension A
        replaceCharMatch(0x20000, 0x2A6D6, charPatternToRegexEnum, random); // Extension B
        replaceCharMatch(0x2A700, 0x2B734, charPatternToRegexEnum, random); // Extension C
        replaceCharMatch(0x2B740, 0x2B81D, charPatternToRegexEnum, random); // Extension D
        replaceCharMatch(0x2B820, 0x2CEA1, charPatternToRegexEnum, random); // Extension E
        replaceCharMatch(0x2CEB0, 0x2EBE0, charPatternToRegexEnum, random); // Extension F
        replaceCharMatch(0xF900, 0xFA6D, charPatternToRegexEnum, random); // Compatibility Ideograph part 1
        replaceCharMatch(0xFA70, 0xFAD9, charPatternToRegexEnum, random); // Compatibility Ideograph part 2
        replaceCharMatch(0x2F800, 0x2FA1D, charPatternToRegexEnum, random); // Compatibility Ideograph Supplement
        replaceCharMatch(0x2F00, 0x2FD5, charPatternToRegexEnum, random); // KangXi Radicals
        replaceCharMatch(0x2E80, 0x2E99, charPatternToRegexEnum, random); // Radical supplement part 1
        replaceCharMatch(0x2E9B, 0x2EF3, charPatternToRegexEnum, random); // Radical supplement part 2
        replaceCharMatch(0x3005, charPatternToRegexEnum, random); // Symbol and punctuation added for TDQ-11343
        replaceCharMatch(0x3007, charPatternToRegexEnum, random); // Symbol and punctuation added for TDQ-11343
        replaceCharMatch(0x3021, 0x3029, charPatternToRegexEnum, random); // Symbol and punctuation added for TDQ-11343
        replaceCharMatch(0x3038, 0x303B, charPatternToRegexEnum, random); // Symbol and punctuation added for TDQ-11343

        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), globalCount,
                charPatternToRegexEnum.getCodePointSize());
    }

    @Test
    public void replaceCharacterHangul() {
        Random random = new Random();
        CharPatternToRegexEnum charPatternToRegexEnum = CharPatternToRegexEnum.HANGUL;
        replaceCharMatch(0xAC00, 0xD7AF, charPatternToRegexEnum, random);
        assertEquals(String.format("Pattern %s has a size issue", charPatternToRegexEnum), globalCount,
                charPatternToRegexEnum.getCodePointSize());
    }

    @Test
    public void replaceUnrecognizedCharacters() {
        Random random = new Random();
        String input = "@&+-/_";
        for (int i = 0; i < input.length(); i++) {
            Integer codePoint = input.codePointAt(i);
            Integer replaceCodePoint = TextPatternUtil.replaceCharacter(codePoint, random);
            String errorMessage = "Character " + input.charAt(i) + " does not have the codepoint " + replaceCodePoint;
            assertTrue(errorMessage, codePoint == replaceCodePoint);
        }
    }

    private void replaceCharMatch(Integer codePoint, CharPatternToRegexEnum charPatternToRegexEnum, Random random) {
        replaceCharMatch(codePoint, codePoint, charPatternToRegexEnum, random);
    }

    private void replaceCharMatch(Integer cpRangeStart, Integer cpRangeEnd, CharPatternToRegexEnum charPatternToRegexEnum,
            Random random) {
        Pattern pattern = Pattern.compile(charPatternToRegexEnum.getPattern());
        for (Integer codePoint = cpRangeStart; codePoint <= cpRangeEnd; codePoint++) {
            Integer output = TextPatternUtil.replaceCharacter(codePoint, random);
            String correspondingString = String.valueOf(Character.toChars(output));
            globalCount++;
            String errorMessage = String.format("Pattern %s won't match %s", pattern, correspondingString);
            assertTrue(errorMessage, pattern.matcher(correspondingString).find());
        }
    }
}
