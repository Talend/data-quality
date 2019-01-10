package org.talend.dataquality.datamasking.generic;

import org.junit.Test;
import org.talend.dataquality.datamasking.FormatPreservingMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class GenerateFromAlphabetTest {

    private FormatPreservingMethod method = FormatPreservingMethod.AES_CBC_PRF;

    private String password = "data";

    private Alphabet defaultAlphabet = Alphabet.DEFAULT_LATIN;

    private GenerateFromAlphabet gfa = new GenerateFromAlphabet(defaultAlphabet, method, password);

    @Test
    public void worksWithAllAlphabets() {
        for (Alphabet alphabet : Alphabet.values()) {
            List<Integer> input = new ArrayList<>();
            input.add(alphabet.getCharactersMap().get(0));
            input.add(alphabet.getCharactersMap().get(1));
            input.add(alphabet.getCharactersMap().get(2));

            GenerateFromAlphabet gfa = new GenerateFromAlphabet(alphabet, method, password);
            List<Integer> output = gfa.generateUniqueCodePoints(input);
            assertFalse("Alphabet " + alphabet + " is not masked properly.", output.isEmpty());
        }
    }

    @Test
    public void maskTooShortString() {
        List<Integer> input = new ArrayList<>();
        input.add(defaultAlphabet.getCharactersMap().get(0));
        List<Integer> result = gfa.generateUniqueCodePoints(input);
        assertTrue("a string of 1 character is too small and should be empty when masked", result.isEmpty());
    }

    @Test
    public void maskTooShortNumber() {
        GenerateFromAlphabet gfa = new GenerateFromAlphabet(Alphabet.DIGITS, method, password);
        List<Integer> input = new ArrayList<>();
        input.add(Alphabet.DIGITS.getCharactersMap().get(0));
        List<Integer> result = gfa.generateUniqueDigits(input);
        assertTrue("a number of 1 digit is too small and should be empty when masked", result.isEmpty());
    }

    @Test
    public void generateDigitsBijectiveWithAES() {
        Alphabet digits = Alphabet.DIGITS;
        GenerateFromAlphabet gfa = new GenerateFromAlphabet(digits, method, password);
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
    public void generateCodePointsBijectiveWithAES() {
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
