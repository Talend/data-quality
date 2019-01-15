package org.talend.dataquality.datamasking.generic;

import com.idealista.fpe.algorithm.Cipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.SecretManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        int[] data = transform(codePoints);

        int[] result = encryptData(data);

        if (result.length == 0) {
            return Collections.emptyList();
        }

        return transform(result, codePoints);
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

    /**
     * Transform the {@code String} element into an array of {@code int}s for FF1 encryption.
     */
    private int[] transform(List<Integer> codePoints) {
        List<Integer> filteredCodePoints = new ArrayList<>(codePoints);
        return filteredCodePoints.stream().filter(i -> alphabet.getRanksMap().containsKey(i))
                .mapToInt(i -> alphabet.getRanksMap().get(i)).toArray();
    }
}
