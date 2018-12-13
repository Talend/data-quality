// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.generic.patterns;

import com.idealista.fpe.algorithm.Cipher;
import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;
import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;

import java.math.BigInteger;
import java.util.List;

/**
 *
 * This class handles the FF1 encyrption of patterns composed of a list of {@link AbstractField}.
 *
 * @author afournier
 * @see SecretManager
 */
public class GenerateFormatPreservingPatterns extends AbstractGeneratePattern {

    private static final long serialVersionUID = -2998638592803380290L;

    /**
     * The cipher used to encrypt data.
     * It is taken from <a href="https://github.com/idealista/format-preserving-encryption-java">
     * idealista library</a>
     * and corresponds to the <a href="https://nvlpubs.nist.gov/nistpubs/specialpublications/nist.sp.800-38g.pdf">
     * NIST-validated FF1 algorithm.</a>.
     *
     * The current implementation requires the input data to be encoded in an array of integers in a certain base
     * {@link GenerateFormatPreservingPatterns#radix}.
     */
    private static Cipher cipher = new com.idealista.fpe.algorithm.ff1.Cipher();

    /**
     * The radix of the numeral representations of patterns to encrypt
     */
    private int radix;

    /**
     * The {@link AbstractGeneratePattern#longestWidth} expressed as an array of integers.
     * <br>
     * It represents the upper bound of valid ranks expressed as an array of integers.
     * <br>
     * It is used to verify the validity of the encrypted data.
     */
    private int[] numeralMaxRank;

    public GenerateFormatPreservingPatterns(int radix, List<AbstractField> fields) {
        super(fields);
        char[] maxRankStr = this.longestWidth.toString(radix).toCharArray();
        this.radix = radix;

        numeralMaxRank = new int[maxRankStr.length];
        for (int i = 0; i < numeralMaxRank.length; i++) {
            numeralMaxRank[i] = Character.getNumericValue(maxRankStr[i]);
        }
    }

    /**
     * This method generates a unique pattern using FF1 encryption.
     * <br>
     * If the encrypted result is not a valid pattern, it is re-encrypted until the output is a valid pattern.
     * This method is called cycle-walking and ensures that the output is valid and unique for the original input.
     * <br>
     * However this method can be slow if there are a lot of invalid values in the domain used ({@code [0, radix^numeralRank.length]}).
     *
     * @param strs the string fields to encode
     * @param secretMng, the SecretManager instance providing the secrets to generate a unique string
     */
    @Override
    public StringBuilder generateUniquePattern(List<String> strs, SecretManager secretMng) {
        int[] data = transform(strs);

        if (data.length == 0) {
            return null;
        }

        byte[] tweak = new byte[] {};
        PseudoRandomFunction prf = secretMng.getPseudoRandomFunction();

        int[] result = cipher.encrypt(data, radix, tweak, prf);

        while (!isValid(result)) {
            result = cipher.encrypt(result, radix, tweak, prf);
        }

        return transform(result);
    }

    /**
     * Transform the encrypted array of {@code int}s into the corresponding {@code String} representation.
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
        // Convert the fields from String to BigInteger.
        List<BigInteger> encodedFields = encodeFields(strs);

        if (encodedFields == null) {
            return new int[] {};
        }

        // Compute the rank of the string to encrypt
        BigInteger rank = getRank(encodedFields);

        // Convert the rank into a binary string
        String numString = rank.toString(radix);

        int[] data = new int[numeralMaxRank.length];

        // Pad with 0s if necessary.
        int paddingLength = numeralMaxRank.length - numString.length();
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

    /**
     * Verify the validity of encrypted data by comparing to the maxRank value.
     *
     * @param encryptedData result of format-preserving encryption.
     * @return whether the encrypted data is lower to the max rank.
     */
    public boolean isValid(int[] encryptedData) {
        int i = 0;
        while (i < numeralMaxRank.length && numeralMaxRank[i] == encryptedData[i]) {
            i++;
        }

        if (i == numeralMaxRank.length) {
            // encrypted data is equal to the numeral max rank
            return false;
        } else {
            return encryptedData[i] < numeralMaxRank[i];
        }
    }

    /**
     * Converts the array of integers containing the ranked data to the corresponding numeral String.
     */
    private String numeralToString(int[] data) {
        StringBuilder sb = new StringBuilder();

        if (data.length > 0) {
            for (int n : data) {
                sb.append(Integer.toString(n, radix));
            }
        }
        return sb.toString();
    }
}
