package org.talend.dataquality.datamasking.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.talend.daikon.pattern.character.CharPattern;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.functions.text.Alphabet;
import org.talend.dataquality.datamasking.functions.text.GenerateFromAlphabet;
import org.talend.dataquality.datamasking.utils.crypto.CipherParameterChecker;

public class GenerateFromAlphabetTest {

    private String password = "data";

    private Alphabet defaultAlphabet = Alphabet.DEFAULT_LATIN;

    @Test
    public void worksWithAllAlphabets() {
        assumeTrue(CipherParameterChecker.IS_AES256_SUPPORTED);
        for (Alphabet alphabet : Alphabet.values()) {
            if (!Alphabet.BEST_GUESS.equals(alphabet)) {
                List<Integer> input = new ArrayList<>();
                input.add(alphabet.getCharactersMap().get(0));
                input.add(alphabet.getCharactersMap().get(1));
                input.add(alphabet.getCharactersMap().get(2));

                GenerateFromAlphabet gfa = new GenerateFromAlphabet(alphabet, FormatPreservingMethod.AES_CBC_PRF, password);
                List<Integer> output = gfa.generateUniqueCodePoints(input);
                assertFalse("Alphabet " + alphabet + " is not masked properly.", output.isEmpty());
            }
        }
    }

    @Test
    public void maskTooShortString() {
        assumeTrue(CipherParameterChecker.IS_AES256_SUPPORTED);
        GenerateFromAlphabet gfa = new GenerateFromAlphabet(defaultAlphabet, FormatPreservingMethod.AES_CBC_PRF, password);
        List<Integer> input = new ArrayList<>();
        input.add(defaultAlphabet.getCharactersMap().get(0));
        List<Integer> result = gfa.generateUniqueCodePoints(input);
        assertTrue("a string of 1 character is too small and should be empty when masked", result.isEmpty());
    }

    @Test
    public void maskTooShortNumber() {
        assumeTrue(CipherParameterChecker.IS_AES256_SUPPORTED);
        GenerateFromAlphabet gfa = new GenerateFromAlphabet(Alphabet.DIGITS, FormatPreservingMethod.AES_CBC_PRF, password);
        List<Integer> input = new ArrayList<>();
        input.add(Alphabet.DIGITS.getCharactersMap().get(0));
        List<Integer> result = gfa.generateUniqueDigits(input);
        assertTrue("a number of 1 digit is too small and should be empty when masked", result.isEmpty());
    }

    @Test
    public void generateDigitsBijectiveWithAES() {
        assumeTrue(CipherParameterChecker.IS_AES256_SUPPORTED);
        Alphabet digits = Alphabet.DIGITS;
        GenerateFromAlphabet gfa = new GenerateFromAlphabet(digits, FormatPreservingMethod.AES_CBC_PRF, password);
        Set<List<Integer>> generatedDigits = new HashSet<>();
        for (int i = 0; i < digits.getRadix(); i++) {
            for (int j = 0; j < digits.getRadix(); j++) {
                List<Integer> input = new ArrayList<>();
                input.add(digits.getCharactersMap().get(i));
                input.add(digits.getCharactersMap().get(j));
                generatedDigits.add(gfa.generateUniqueDigits(input));
            }
        }
        assertEquals("FF1 digit masking is not bijective with AES-CBC.", (int) Math.pow(digits.getRadix(), 2),
                generatedDigits.size());
    }

    @Test
    public void generateDigitsBijectiveWithHmac() {
        Alphabet digits = Alphabet.DIGITS;
        GenerateFromAlphabet gfa = new GenerateFromAlphabet(digits, FormatPreservingMethod.SHA2_HMAC_PRF, password);
        Set<List<Integer>> generatedDigits = new HashSet<>();
        for (int i = 0; i < digits.getRadix(); i++) {
            for (int j = 0; j < digits.getRadix(); j++) {
                List<Integer> input = new ArrayList<>();
                input.add(digits.getCharactersMap().get(i));
                input.add(digits.getCharactersMap().get(j));
                generatedDigits.add(gfa.generateUniqueDigits(input));
            }
        }
        assertEquals("FF1 digit masking is not bijective with HMAC SHA-2.", (int) Math.pow(digits.getRadix(), 2),
                generatedDigits.size());
    }

    @Test
    public void generateBestGuessPatternWithHmac() {
        Alphabet alphabet = Alphabet.BEST_GUESS;
        GenerateFromAlphabet generateFromAlphabet = new GenerateFromAlphabet(alphabet, FormatPreservingMethod.SHA2_HMAC_PRF,
                password);
        List<Integer> codepoints = Arrays.asList((int) 'a', (int) '-', (int) '-', (int) '\u3400', (int) '€');

        List<Integer> output = generateFromAlphabet.generateUniqueCodePoints(codepoints);
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer codePoint : output)
            stringBuilder.append(Character.toChars(codePoint));
        assertTrue("x--\uD85D\uDCBD€".equals(stringBuilder.toString()));
    }

    @Test
    public void isBijectiveBestGuess() {
        Alphabet alphabet = Alphabet.BEST_GUESS;
        GenerateFromAlphabet generateFromAlphabet = new GenerateFromAlphabet(alphabet, FormatPreservingMethod.SHA2_HMAC_PRF, "");
        Set<String> outputs = new LinkedHashSet<>();
        int sizeKatakana = CharPattern.FULLWIDTH_KATAKANA.getCodePointSize();
        for (int k = 0; k < sizeKatakana; k++) {
            for (char i = '0'; i <= '9'; i++) {
                List<Integer> input = new ArrayList<>();
                input.add(CharPattern.FULLWIDTH_KATAKANA.getCodePointAt(k));
                input.add((int) i);
                List<Integer> output = generateFromAlphabet.generateUniqueCodePoints(input);
                StringBuilder stringBuilder = new StringBuilder();
                for (Integer codepoint : output)
                    stringBuilder.append(Character.toChars(codepoint));
                outputs.add(stringBuilder.toString());
            }
        }
        assertTrue(outputs.size() == 10 * sizeKatakana);
    }

    @Test
    public void generateCodePointsBijectiveWithAES() {
        assumeTrue(CipherParameterChecker.IS_AES256_SUPPORTED);
        GenerateFromAlphabet gfa = new GenerateFromAlphabet(defaultAlphabet, FormatPreservingMethod.AES_CBC_PRF, password);
        Set<List<Integer>> generatedCodePoints = new HashSet<>();
        for (int i = 0; i < defaultAlphabet.getRadix(); i++) {
            for (int j = 0; j < defaultAlphabet.getRadix(); j++) {
                List<Integer> input = new ArrayList<>();
                input.add(defaultAlphabet.getCharactersMap().get(i));
                input.add(defaultAlphabet.getCharactersMap().get(j));
                List<Integer> output = gfa.generateUniqueCodePoints(input);
                assertFalse(output.isEmpty());
                generatedCodePoints.add(output);
            }
        }
        assertEquals("FF1 character masking is not bijective with HMAC SHA-2.", (int) Math.pow(defaultAlphabet.getRadix(), 2),
                generatedCodePoints.size());
    }

    @Test
    public void generateCodePointsBijectiveWithHmac() {
        GenerateFromAlphabet gfa = new GenerateFromAlphabet(defaultAlphabet, FormatPreservingMethod.SHA2_HMAC_PRF, password);
        Set<List<Integer>> generatedCodePoints = new HashSet<>();
        for (int i = 0; i < defaultAlphabet.getCharactersMap().size(); i++) {
            for (int j = 0; j < defaultAlphabet.getCharactersMap().size(); j++) {
                List<Integer> input = new ArrayList<>();
                input.add(defaultAlphabet.getCharactersMap().get(i));
                input.add(defaultAlphabet.getCharactersMap().get(j));
                List<Integer> output = gfa.generateUniqueCodePoints(input);
                assertFalse(output.isEmpty());
                generatedCodePoints.add(output);
            }
        }
        assertEquals("FF1 character masking is not bijective with HMAC SHA-2.", (int) Math.pow(defaultAlphabet.getRadix(), 2),
                generatedCodePoints.size());
    }

}
