package org.talend.dataquality.common.pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.pattern.character.CharPattern;

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
        CharPattern charPattern = CharPattern.LOWER_LATIN;
        replaceCharMatch((int) 'a', (int) 'z', charPattern, random);
        replaceCharMatch((int) 'ß', (int) 'ö', charPattern, random);
        replaceCharMatch((int) 'ø', (int) 'ÿ', charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount, charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterUpperLatin() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.UPPER_LATIN;
        replaceCharMatch((int) 'A', (int) 'Z', charPattern, random);
        replaceCharMatch((int) 'À', (int) 'Ö', charPattern, random);
        replaceCharMatch((int) 'Ø', (int) 'Þ', charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount, charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterLowerHiragana() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.LOWER_HIRAGANA;
        int[] characters = { 0x3041, 0x3043, 0x3045, 0x3047, 0x3049, 0x3063, 0x3083, 0x3085, 0x3087, 0x308E, 0x3095, 0x3096 };
        for (int position : characters)
            replaceCharMatch(position, charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount, charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterUpperHiragana() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.UPPER_HIRAGANA;
        int[] characters = { 0x3042, 0x3044, 0x3046, 0x3048, 0x3084, 0x3086 };
        for (int position : characters)
            replaceCharMatch(position, charPattern, random);

        replaceCharMatch(0x304A, 0x3062, charPattern, random);
        replaceCharMatch(0x3064, 0x3082, charPattern, random);
        replaceCharMatch(0x3088, 0x308D, charPattern, random);
        replaceCharMatch(0x308F, 0x3094, charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount, charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterLowerKatakana() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.LOWER_KATAKANA;
        int[] characters = { 0x30A1, 0x30A3, 0x30A5, 0x30A7, 0x30A9, 0x30C3, 0x30E3, 0x30E5, 0x30E7, 0x30EE, 0x30F5, 0x30F6 };
        for (int position : characters)
            replaceCharMatch(position, charPattern, random);

        replaceCharMatch(0x31F0, 0x31FF, charPattern, random);
        replaceCharMatch(0xFF67, 0xFF6F, charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount, charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterUpperKatakana() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.UPPER_KATAKANA;
        int[] characters = { 0x30A2, 0x30A4, 0x30A6, 0x30A8, 0x30E4, 0x30E6, 0xFF66 };
        for (int position : characters)
            replaceCharMatch(position, charPattern, random);

        replaceCharMatch(0x30AA, 0x30C2, charPattern, random);
        replaceCharMatch(0x30C4, 0x30E2, charPattern, random);
        replaceCharMatch(0x30E8, 0x30ED, charPattern, random);
        replaceCharMatch(0x30EF, 0x30F4, charPattern, random);
        replaceCharMatch(0x30F7, 0x30FA, charPattern, random);
        replaceCharMatch(0xFF71, 0xFF9D, charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount, charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterKanji() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.KANJI;
        replaceCharMatch(0x4E00, 0x9FEF, charPattern, random);
        replaceCharMatch(0x3400, 0x4DB5, charPattern, random); // Extension A
        replaceCharMatch(0x20000, 0x2A6D6, charPattern, random); // Extension B
        replaceCharMatch(0x2A700, 0x2B734, charPattern, random); // Extension C
        replaceCharMatch(0x2B740, 0x2B81D, charPattern, random); // Extension D
        replaceCharMatch(0x2B820, 0x2CEA1, charPattern, random); // Extension E
        replaceCharMatch(0x2CEB0, 0x2EBE0, charPattern, random); // Extension F
        replaceCharMatch(0xF900, 0xFA6D, charPattern, random); // Compatibility Ideograph part 1
        replaceCharMatch(0xFA70, 0xFAD9, charPattern, random); // Compatibility Ideograph part 2
        replaceCharMatch(0x2F800, 0x2FA1D, charPattern, random); // Compatibility Ideograph Supplement
        replaceCharMatch(0x2F00, 0x2FD5, charPattern, random); // KangXi Radicals
        replaceCharMatch(0x2E80, 0x2E99, charPattern, random); // Radical supplement part 1
        replaceCharMatch(0x2E9B, 0x2EF3, charPattern, random); // Radical supplement part 2
        replaceCharMatch(0x3005, charPattern, random); // Symbol and punctuation added for TDQ-11343
        replaceCharMatch(0x3007, charPattern, random); // Symbol and punctuation added for TDQ-11343
        replaceCharMatch(0x3021, 0x3029, charPattern, random); // Symbol and punctuation added for TDQ-11343
        replaceCharMatch(0x3038, 0x303B, charPattern, random); // Symbol and punctuation added for TDQ-11343

        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount, charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterHangul() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.HANGUL;
        replaceCharMatch(0xAC00, 0xD7AF, charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount, charPattern.getCodePointSize());
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

    private void replaceCharMatch(Integer codePoint, CharPattern charPattern, Random random) {
        replaceCharMatch(codePoint, codePoint, charPattern, random);
    }

    private void replaceCharMatch(Integer cpRangeStart, Integer cpRangeEnd, CharPattern charPattern, Random random) {
        Pattern pattern = Pattern.compile(charPattern.getPattern().getRegex());
        for (Integer codePoint = cpRangeStart; codePoint <= cpRangeEnd; codePoint++) {
            Integer output = TextPatternUtil.replaceCharacter(codePoint, random);
            String correspondingString = String.valueOf(Character.toChars(output));
            globalCount++;
            String errorMessage = String.format("Pattern %s won't match %s", pattern, correspondingString);
            assertTrue(errorMessage, pattern.matcher(correspondingString).find());
        }
    }
}
