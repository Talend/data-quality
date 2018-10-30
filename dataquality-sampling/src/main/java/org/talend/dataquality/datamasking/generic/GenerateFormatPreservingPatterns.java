package org.talend.dataquality.datamasking.generic;

import com.idealista.fpe.algorithm.Cipher;
import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;
import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;

import java.math.BigInteger;
import java.util.List;

public class GenerateFormatPreservingPatterns extends AbstractGeneratePattern {

    private static Cipher cipher = new com.idealista.fpe.algorithm.ff1.Cipher();

    private int radix;

    private int numSeqLength;

    public GenerateFormatPreservingPatterns(int radix, List<AbstractField> fields) {
        super(fields);

        this.radix = radix;
        this.numSeqLength = this.longestWidth.toString(radix).length();
    }

    /**
     * Transform the an encrypted array of {@code int}s into the corresponding {@code String} representation.
     */
    public StringBuilder transform(int[] data) {
        BigInteger rank = new BigInteger(numeralToString(data), radix);

        if (rank.compareTo(longestWidth) >= 0) {
            return null;
        }

        // uniqueMaskedNumberList is the unique list created from uniqueMaskedNumber
        List<BigInteger> numericFields = getFieldsFromNumber(rank);

        return decodeFields(numericFields);
    }

    /**
     * Transform the {@code String} element into an array of {@code int}s for FF1 encryption.
     */
    public int[] transform(List<String> strs) {

        int[] data = new int[numSeqLength];

        // Compute the rank of the string to encrypt
        BigInteger rank = getNumberToMask(encodeFields(strs));

        // Convert the rank into a binary string
        String numString = rank.toString(radix);

        // Add 0-padding if necessary.
        int paddingLength = numSeqLength - numString.length();
        if (paddingLength > 0) {
            for (int i = 0; i < paddingLength; i++) {
                data[i] = 0;
            }
        }

        // Fill the data tab, starting from the end of the padding.
        int pos = paddingLength;
        for (char c : numString.toCharArray()) {
            data[pos] = Character.getNumericValue(c);
            pos++;
        }

        return data;
    }

    @Override
    public StringBuilder generateUniqueString(List<String> strs, SecretManager secretMng) {
        int[] data = transform(strs);
        byte[] tweak = computeTweak(strs);
        PseudoRandomFunction prf = secretMng.getPseudoRandomFunction();

        int[] result = cipher.encrypt(data, radix, tweak, prf);

        while (!isValid(result)) {
            result = cipher.encrypt(result, radix, tweak, prf);
        }

        return transform(result);
    }

    /**
     * Compute a tweak. For now we don't use tweaks for masking.
     * @return an empty tweak.
     */
    private byte[] computeTweak(List<String> strs) {
        return new byte[] {};
    }

    public boolean isValid(int[] numeralString) {
        BigInteger rank = new BigInteger(numeralToString(numeralString), radix);

        return rank.compareTo(longestWidth) < 0;
    }

    private String numeralToString(int[] data) {
        StringBuilder sb = new StringBuilder();

        if (data.length > 0) {
            for (int n : data) {
                sb.append(n);
            }
        }
        return sb.toString();
    }
}
