package org.talend.dataquality.common.pattern;

import static org.junit.Assert.*;

import java.util.*;
import java.util.regex.Pattern;

import org.junit.Assert;
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

        checkPattern("ケーキ", "KKK");
        checkPattern("bーキ", "aーK");
        checkPattern("ｰヽヾ", "kKK");
        checkPattern("ゝゞゟ", "HHゟ");
        checkPattern("ほーむ", "HHH");
        checkPattern("dー", "aー");
        checkPattern("abc-d", "aaa-a");
        checkPattern("Straße", "Aaaaaa");
        checkPattern("トンキンｨｩ", "KKKKkk");
        checkPattern("とうきょう", "HHHHH");
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
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount,
                charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterLowerLatinRare() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.LOWER_LATIN_RARE;
        replaceCharMatch((int) 'ß', (int) 'ö', charPattern, random);
        replaceCharMatch((int) 'ø', (int) 'ÿ', charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount,
                charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterUpperLatin() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.UPPER_LATIN;
        replaceCharMatch((int) 'A', (int) 'Z', charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount,
                charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterUpperLatinRare() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.UPPER_LATIN_RARE;
        replaceCharMatch((int) 'À', (int) 'Ö', charPattern, random);
        replaceCharMatch((int) 'Ø', (int) 'Þ', charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount,
                charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterHiragana() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.HIRAGANA;

        replaceCharMatch(0x3041, 0x3096, charPattern, random);
        replaceCharMatch(0x309D, 0x309E, charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount,
                charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterHalfKatakana() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.HALFWIDTH_KATAKANA;

        replaceCharMatch(0xFF66, 0xFF6F, charPattern, random);
        replaceCharMatch(0xFF70, 0xFF9D, charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount,
                charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterFullKatakana() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.FULLWIDTH_KATAKANA;
        replaceCharMatch(0x30A1, 0x30FA, charPattern, random);
        replaceCharMatch(0x30FD, 0x30FE, charPattern, random);
        replaceCharMatch(0x31F0, 0x31FF, charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount,
                charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterKanji() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.KANJI;
        replaceCharMatch(0x4E00, 0x9FEF, charPattern, random);
        replaceCharMatch(0x3005, charPattern, random); // Symbol and punctuation added for TDQ-11343
        replaceCharMatch(0x3007, charPattern, random); // Symbol and punctuation added for TDQ-11343
        replaceCharMatch(0x3021, 0x3029, charPattern, random); // Symbol and punctuation added for TDQ-11343
        replaceCharMatch(0x3038, 0x303B, charPattern, random); // Symbol and punctuation added for TDQ-11343

        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount,
                charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterKanjiRare() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.KANJI_RARE;
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

        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount,
                charPattern.getCodePointSize());
    }

    @Test
    public void replaceCharacterHangul() {
        Random random = new Random();
        CharPattern charPattern = CharPattern.HANGUL;
        replaceCharMatch(0xAC00, 0xD7AF, charPattern, random);
        assertEquals(String.format("Pattern %s has a size issue", charPattern), globalCount,
                charPattern.getCodePointSize());
    }

    @Test
    public void replaceUnrecognizedCharacters() {
        Random random = new Random();
        String input = "@&+-/_";
        for (int i = 0; i < input.length(); i++) {
            Integer codePoint = input.codePointAt(i);
            Integer replaceCodePoint = TextPatternUtil.replaceCharacter(codePoint, random);
            String errorMessage = "Character " + input.charAt(i) + " does not have the codepoint " + replaceCodePoint;
            assertSame(errorMessage, codePoint, replaceCodePoint);
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

    @Test
    public void getPatternLatin() {
        List<Integer> codepoints = Arrays.asList((int) 'a', (int) 'é', (int) '-', (int) 'A', (int) '€', (int) 'b');
        List<Integer> filteredCodepoints = new ArrayList<>();
        Set<CharPattern> charPatternSet = TextPatternUtil.getCharPatterns(codepoints, filteredCodepoints);
        assertTrue(charPatternSet.contains(CharPattern.LOWER_LATIN));
        assertTrue(charPatternSet.contains(CharPattern.LOWER_LATIN_RARE));
        assertTrue(charPatternSet.contains(CharPattern.UPPER_LATIN));
        assertEquals((int) 'a', (int) filteredCodepoints.get(0));
        assertEquals((int) 'é', (int) filteredCodepoints.get(1));
        assertEquals((int) 'A', (int) filteredCodepoints.get(2));
        assertEquals((int) 'b', (int) filteredCodepoints.get(3));
        assertEquals(3, charPatternSet.size());
    }

    @Test
    public void findPatternSetAlwaysWithSameOrder() {
        List<Integer> codepoints1 = Arrays.asList((int) 'a', (int) 'b', (int) 'c', (int) '1', (int) '2', (int) '3');
        List<Integer> codepoints2 = Arrays.asList((int) '1', (int) '2', (int) '3', (int) 'a', (int) 'b', (int) 'c');

        Set<CharPattern> set1 = TextPatternUtil.getCharPatterns(codepoints1, new ArrayList<>());
        Set<CharPattern> set2 = TextPatternUtil.getCharPatterns(codepoints2, new ArrayList<>());
        List<CharPattern> list1 = new ArrayList<>(set1);
        List<CharPattern> list2 = new ArrayList<>(set2);

        assertTrue(set1 instanceof SortedSet);
        assertEquals(list1, list2);
    }

    @Test
    public void replaceStringCodePointsWithMulticharacter() {
        Random random = new Random(123);
        String input = "abc";
        String changedCharacter = "eom";

        for (int i = 0; i < input.length(); i++) {
            String inputChar = input.substring(i, i + 1);
            String replaceChar = changedCharacter.substring(i, i + 1);
            Integer codePoint = replaceChar.codePointAt(0);
            List<Integer> repalceStringCodePoint = TextPatternUtil.replaceStringCodePoints(inputChar, random);
            assertEquals(codePoint, repalceStringCodePoint.get(0));
        }
    }

    @Test
    public void replaceStringCodePointsWithoneCharacterFromMultiCharacter() {

        Random random = new Random(123);
        String input = "testFor@hooseOnecharater";
        String replaceByCharacter = input.substring(7);
        if (replaceByCharacter == "@") {
            Integer codePoint = "9".codePointAt(0);
            List<Integer> repalceStringCodePoint = TextPatternUtil.replaceStringCodePoints(replaceByCharacter, random);
            assertEquals(codePoint, repalceStringCodePoint.get(0));
        }

    }

    @Test
    public void replaceStringCodePointsWithInvalidReplace() {

        Random random = new Random(123);
        String input = "test@sina.com";
        String replaceByCharacter = input.substring(4);
        if (replaceByCharacter == "@") {
            Integer codePoint = "9".codePointAt(0);
            List<Integer> repalceStringCodePoint = TextPatternUtil.replaceStringCodePoints(replaceByCharacter, random);
            assertEquals(codePoint, repalceStringCodePoint.get(0));

        }

    }

    @Test
    public void replaceStringCodePointsFromMulticharacter() {
        Random random = new Random(123);
        String input = "b";
        Integer codePoint = "feiop".codePointAt(1);
        List<Integer> repalceStringCodePoint = TextPatternUtil.replaceStringCodePoints(input, random);
        assertEquals(codePoint, repalceStringCodePoint.get(0));

    }

    @Test
    public void replaceStringCodePointsWithOnecharacter() {
        Random random = new Random(123);
        String input = "c";
        Integer codePoint = "e".codePointAt(0);
        List<Integer> repalceStringCodePoint = TextPatternUtil.replaceStringCodePoints(input, random);
        assertEquals(codePoint, repalceStringCodePoint.get(0));

    }

    @Test
    public void replaceStringCodePointsWithOneNumber() {
        Random random = new Random(123);
        String input = "8";
        Integer codePoint = "2".codePointAt(0);
        List<Integer> repalceStringCodePoint = TextPatternUtil.replaceStringCodePoints(input, random);
        assertEquals(codePoint, repalceStringCodePoint.get(0));

    }

    @Test
    public void replaceStringCodePointsWithOneCapital() {
        Random random = new Random(123);
        String input = "P";
        Integer codePoint = "E".codePointAt(0);
        List<Integer> repalceStringCodePoint = TextPatternUtil.replaceStringCodePoints(input, random);
        assertEquals(codePoint, repalceStringCodePoint.get(0));

    }

    @Test
    public void replacePatternCharacterWithLowerLetter() {
        Random random = new Random(677899);

        char character = 'a';
        int input = Integer.valueOf(character);
        Integer codePoint = "d".codePointAt(0);
        char[] repalceStringCodePoint = TextPatternUtil.replacePatternCharacter(input, random);
        Assert.assertEquals(codePoint.intValue(), new String(repalceStringCodePoint).codePointAt(0));

    }

    @Test
    public void replacePatternCharacterWithNumber() {
        Random random = new Random(569888);
        char character = '9';
        int input = Integer.valueOf(character);
        Integer codePoint = "4".codePointAt(0);
        char[] replaceCharacter = TextPatternUtil.replacePatternCharacter(input, random);
        Assert.assertEquals(codePoint.intValue(), new String(replaceCharacter).codePointAt(0));
    }

    @Test
    public void replacePatternCharacterWithHangulo() {
        Random random = new Random(569888);
        char n = 'G';
        int input = Integer.valueOf(n);
        Integer codePoint = "훨".codePointAt(0);
        char[] replaceCharacter = TextPatternUtil.replacePatternCharacter(input, random);
        Assert.assertEquals(codePoint.intValue(), new String(replaceCharacter).codePointAt(0));
    }

    @Test
    public void replacePatternCharacterWithFullKArakana() {
        Random random = new Random(569888);
        char n = 'K';
        int input = Integer.valueOf(n);
        Integer codePoint = "ゥ".codePointAt(0);
        char[] replaceCharacter = TextPatternUtil.replacePatternCharacter(input, random);
        Assert.assertEquals(codePoint.intValue(), new String(replaceCharacter).codePointAt(0));
    }

    @Test
    public void replacePatternCharacterWithhalfKankana() {
        Random random = new Random(569888);
        char n = 'k';
        int input = Integer.valueOf(n);
        Integer codePoint = "ﾖ".codePointAt(0);
        char[] replaceCharacter = TextPatternUtil.replacePatternCharacter(input, random);
        Assert.assertEquals(codePoint.intValue(), new String(replaceCharacter).codePointAt(0));
    }

    @Test
    public void replacePatternCharacterWithKanji() {
        Random random = new Random(569888);
        char n = 'C';
        int input = Integer.valueOf(n);
        Integer codePoint = "嬶".codePointAt(0);
        char[] replaceCharacter = TextPatternUtil.replacePatternCharacter(input, random);
        Assert.assertEquals(codePoint.intValue(), new String(replaceCharacter).codePointAt(0));
    }
}
