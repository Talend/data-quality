package org.talend.dataquality.datamasking.generic;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.pattern.character.CharPattern;
import org.talend.dataquality.common.pattern.TextPatternUtil;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;
import org.talend.dataquality.datamasking.generic.patterns.GenerateFormatPreservingPatterns;

import com.idealista.fpe.algorithm.Cipher;

public class GenerateFromAlphabet implements Serializable {

    private static final long serialVersionUID = 4131439329223094305L;

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateFromAlphabet.class);

    /**
     * The cipher used to encrypt data.
     * It is taken from <a href="https://github.com/idealista/format-preserving-encryption-java">
     * idealista library</a>
     * and corresponds to the <a href="https://nvlpubs.nist.gov/nistpubs/specialpublications/nist.sp.800-38g.pdf">
     * NIST-validated FF1 algorithm.</a>.
     *
     * The current implementation requires the input data to be encoded in an array of integers in a certain base.
     */
    private static final Cipher cipher = new com.idealista.fpe.algorithm.ff1.Cipher();

    /**
     * Alphabet used during FF1 encryption for bijective methods.
     */
    private Alphabet alphabet;

    /**
     * The minimal length for a string to be valid to replace with FF1.
     */
    private int minLength;

    /**
     * The maximum number of different charPattern that can be included into the input string to branch standard best guess
     */
    private static final int MAX_ALPHABET_NUMBER = 5;

    /**
     * The SecretManager handles keys and secret used to generate masked values with FF1
     */
    private SecretManager secretMng;

    public GenerateFromAlphabet(Alphabet alphabet, FormatPreservingMethod method, String password) {
        this.alphabet = alphabet;
        this.secretMng = new SecretManager(method, password);
        minLength = Math.max(2, (int) Math.ceil(Math.log(100) / Math.log(alphabet.getRadix())));
        LOGGER.info("Any string to mask must have a length of at least {}", minLength);
    }

    /**
     * @param codePoints, the string input to encode
     * @return the new encoded string
     */
    public List<Integer> generateUniqueCodePoints(List<Integer> codePoints) {
        if (Alphabet.BEST_GUESS.equals(alphabet))
            return generateUniqueCodePointsBestGuess(codePoints);

        int[] data = transform(codePoints);

        int[] result = encryptData(data);

        if (result.length == 0) {
            return Collections.emptyList();
        }
        return transform(result, codePoints);
    }

    public List<Integer> generateUniqueCodePointsBestGuess(List<Integer> codePoints) {

        int[] data;
        List<Integer> filteredCodepoints = new ArrayList<>();
        Set<CharPattern> charPatternSet = TextPatternUtil.getCharPatterns(codePoints, filteredCodepoints);

        if (needPatternMasking(charPatternSet))
            return maskByCharPattern(codePoints, filteredCodepoints);

        alphabet.setRadix(computeRadix(charPatternSet));
        data = transformBestGuess(codePoints, charPatternSet);

        return cycleWalking(data, codePoints, filteredCodepoints, charPatternSet);
    }

    private List<Integer> cycleWalking(int[] data, List<Integer> codePoints, List<Integer> filteredCodepoints,
            Set<CharPattern> charPatternSet) {
        List<Integer> transformBestGuess;
        int[] result = data;
        do {
            result = encryptData(result);
            if (result.length == 0) {
                return Collections.emptyList();
            }
            transformBestGuess = transformBestGuess(result, codePoints, charPatternSet);
        } while (TextPatternUtil.getCharPatterns(transformBestGuess, filteredCodepoints).size() != charPatternSet.size());
        return transformBestGuess;
    }

    private boolean needPatternMasking(Set<CharPattern> charPatternSet) {
        return charPatternSet.size() > MAX_ALPHABET_NUMBER || charPatternSet.contains(CharPattern.HANGUL)
                || charPatternSet.contains(CharPattern.KANJI) || charPatternSet.contains(CharPattern.KANJI_RARE);
    }

    private List<Integer> maskByCharPattern(List<Integer> originalCodePoints, List<Integer> filteredCodepoints) {
        List<AbstractField> fields = new ArrayList<>();
        List<String> positions = new ArrayList<>();
        for (Integer codepoint : filteredCodepoints) {
            CharPattern charPattern = TextPatternUtil.getCharPattern(codepoint);
            if (charPattern != null) {
                FieldInterval fieldInterval = new FieldInterval(BigInteger.ZERO,
                        BigInteger.valueOf(charPattern.getCodePointSize() - 1));
                fields.add(fieldInterval);
                Integer position = alphabet.getCharPatternRankMap().get(charPattern).get(codepoint);
                positions.add(String.valueOf(position));
            }
        }
        GenerateFormatPreservingPatterns generateFormatPreservingPatterns = new GenerateFormatPreservingPatterns(10, fields);
        StringBuilder maskedPositions = generateFormatPreservingPatterns.generateUniquePattern(positions, secretMng);
        return getMaskedCodePoints(maskedPositions, fields, originalCodePoints);
    }

    private List<Integer> getMaskedCodePoints(StringBuilder maskedPositions, List<AbstractField> fields,
            List<Integer> originalCodePoints) {
        List<Integer> finalCodePoints = new ArrayList<>();
        int currentField = 0;
        int currentPosition = 0;
        for (Integer codepoint : originalCodePoints) {
            CharPattern charPattern = TextPatternUtil.getCharPattern(codepoint);
            if (charPattern == null)
                finalCodePoints.add(codepoint);
            else {
                FieldInterval fieldInterval = (FieldInterval) fields.get(currentField++);
                int length = fieldInterval.getLength();
                Integer position = Integer.valueOf(maskedPositions.substring(currentPosition, currentPosition + length));
                currentPosition += length;
                Integer codePoint = alphabet.getCharPatternCharacterMap().get(charPattern).get(position);
                finalCodePoints.add(codePoint);
            }
        }
        return finalCodePoints;
    }

    private int computeRadix(Set<CharPattern> charPatternSet) {
        int sum = 0;
        for (CharPattern charPattern : charPatternSet)
            sum += charPattern.getCodePointSize();
        return sum;
    }

    /**
     * @param digits, the string input to encode
     * @return the new encoded string
     */
    public List<Integer> generateUniqueDigits(List<Integer> digits) {
        int[] data = new int[digits.size()];

        for (int i = 0; i < digits.size(); i++) {
            data[i] = digits.get(i);
        }

        int[] result = encryptData(data);

        List<Integer> newDigits = new ArrayList<>();
        for (int digit : result) {
            newDigits.add(digit);
        }

        return newDigits;
    }

    private int[] encryptData(int[] data) {
        if (data.length < minLength) {
            return new int[] {};
        }

        byte[] tweak = new byte[] {};

        return cipher.encrypt(data, alphabet.getRadix(), tweak, secretMng.getPseudoRandomFunction());
    }

    /**
     * Transform the encrypted array of {@code int}s into the corresponding {@code String} representation.
     */
    private List<Integer> transform(int[] data, List<Integer> originalCodePoints) {

        List<Integer> codePoints = new ArrayList<>();

        int counter = 0;
        for (int codePoint : originalCodePoints) {
            if (alphabet.getRanksMap().containsKey(codePoint)) {
                codePoints.add(alphabet.getCharactersMap().get(data[counter++]));
            } else {
                codePoints.add(codePoint);
            }
        }
        return codePoints;
    }

    private List<Integer> transformBestGuess(int[] data, List<Integer> originalCodePoints, Set<CharPattern> charPatternSet) {
        List<Integer> codePoints = new ArrayList<>();
        int counter = 0;
        for (int codePoint : originalCodePoints) {
            int rank = getRank(codePoint, charPatternSet);
            if (rank != -1) {
                codePoints.add(getFromRank(data[counter++], charPatternSet));
            } else {
                codePoints.add(codePoint);
            }
        }
        return codePoints;
    }

    /**
     * Transform the {@code String} element into an array of {@code int}s for FF1 encryption.
     */
    private int[] transform(List<Integer> codePoints) {
        List<Integer> filteredCodePoints = new ArrayList<>(codePoints);
        return filteredCodePoints.stream().filter(i -> alphabet.getRanksMap().containsKey(i))
                .mapToInt(i -> alphabet.getRanksMap().get(i)).toArray();
    }

    private int[] transformBestGuess(List<Integer> codePoints, Set<CharPattern> charPatternSet) {
        List<Integer> filteredCodePoints = new ArrayList<>(codePoints);
        return filteredCodePoints.stream().mapToInt(codepoint -> getRank(codepoint, charPatternSet)).filter(rank -> rank != -1)
                .toArray();
    }

    private int getRank(Integer codePoint, Set<CharPattern> charPatternSet) {
        int rank = 0;
        for (CharPattern charPattern : charPatternSet) {
            if (charPattern.contains(codePoint))
                return (rank + alphabet.getCharPatternRankMap().get(charPattern).get(codePoint));
            else
                rank += alphabet.getCharPatternRankMap().get(charPattern).size();
        }
        return -1;
    }

    private int getFromRank(int rank, Set<CharPattern> charPatternSet) {
        int remaining = rank;
        for (CharPattern charPattern : charPatternSet) {
            if (alphabet.getCharPatternCharacterMap().get(charPattern).size() > remaining)
                return alphabet.getCharPatternCharacterMap().get(charPattern).get(remaining);
            else
                remaining -= alphabet.getCharPatternCharacterMap().get(charPattern).size();
        }
        throw new IllegalArgumentException("getFromRank parsing failed");
    }
}
