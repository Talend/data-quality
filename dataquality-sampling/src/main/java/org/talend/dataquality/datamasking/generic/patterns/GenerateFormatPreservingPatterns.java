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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

/**
 *
 * This class handles the FF1 encryption of patterns composed of a list of {@link AbstractField}.
 *
 * @author afournier
 * @see SecretManager
 */
public class GenerateFormatPreservingPatterns extends AbstractGeneratePattern {

    private static final long serialVersionUID = -2998638592803380290L;

    /**
     * Specifies the precision and rounding mode for operations with BigDecimal numbers.
     */
    private static final MathContext MC = new MathContext(34, RoundingMode.UP);

    /**
     * Log2 at a precision of 34.
     */
    private static final double LOG2 = 0.6931471805599453D;

    /**
     * 1 / Log2 at a precision of 34.
     */
    private static final double INV_LOG2 = 1.4426950408889634D;

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
    private Cipher cipher = new com.idealista.fpe.algorithm.ff1.Cipher();

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
        this.radix = radix;
        computeMaxRank();
    }

    public GenerateFormatPreservingPatterns(List<AbstractField> fields) {
        super(fields);
        this.radix = computeOptimalRadix(this.longestWidth);
        computeMaxRank();
    }

    private void computeMaxRank() {
        char[] maxRankStr = this.longestWidth.toString(radix).toCharArray();

        numeralMaxRank = new int[maxRankStr.length];
        for (int i = 0; i < numeralMaxRank.length; i++) {
            numeralMaxRank[i] = Character.digit(maxRankStr[i], radix);
        }
    }

    public int getRadix() {
        return this.radix;
    }

    /**
     * This method generates a unique pattern using FF1 encryption.
     * <br>
     * If the encrypted result is not a valid pattern, it is re-encrypted until the output is a valid pattern.
     * This method is called cycle-walking and ensures that the output is valid and unique for the original input.
     * <br>
     *
     * However this method can be slow if there are a lot of invalid values
     * in the domain used ({@code [0, radix^numeralRank.length[ }).
     * That is why we {@link #computeOptimalRadix(BigInteger)} is called during instantiation.
     *
     * @param strs the string fields to encode
     * @param secretMng, the SecretManager instance providing the secrets to generate a unique string
     *
     * @see #computeOptimalRadix(BigInteger)
     * @see #transform(int[])
     * @see #transform(List)
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
    private StringBuilder transform(int[] data) {
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
    private int[] transform(List<String> strs) {
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
            data[pos] = Character.digit(c, radix);
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
    private boolean isValid(int[] encryptedData) {
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
                sb.append(Character.forDigit(n, radix));
            }
        }
        return sb.toString();
    }

    /**
     * Computes the optimal radix to use for FF1 encryption
     * given the number of possible values {@code N} in the pattern.
     *
     * FF1 encryption works perfectly only for patterns that can be encoded
     * to an ensemble of the form : {@code [0, N = radix^n[ }.
     *
     * For other patterns, FF1 can output an out-of-bound value which must be
     * encrypted again until the result falls inside the ensemble {@code [0, N[ }.
     *
     * For each possible radix (base), computes the density
     * of the pattern over the smallest ensemble of the form {@code [0, radix^n[ }
     * that can encode all the possible pattern values.
     *
     * The closer to 0 the density, the higher the expected number
     * of retries to encrypt a value of the pattern.
     * Therefore we chose the radix that maximizes the density.
     *
     * In order to compute the density, we can compute the base radix logarithm of the pattern cardinality {@code N}.
     * Then we can compute :
     * {@code
     *      N / radix ^ ( ceil( log_radix(N) ))
     * }
     * which is the density.
     *
     * To avoid applying the division at each iteration,
     * we first compare the dividend {@code N} and the divisor.
     *
     * <ul>
     *     <li>If they are equal, then the cardinality N fits perfectly for the given radix.</li>
     *     <li>If N is lower, then the division is applied to compute the density.</li>
     *     <li>If N is higher, this means the Log has been rounded before the ceiling.
     *         It generally happens for Big values when {@code N}
     *         is just a little higher than a perfect power of radix.
     *         For instance it should happen with base 2 for {@code N = 2^128 + 1}.
     *         In that case the density is not computed.</li>
     * </ul>
     *
     * @param card the number of possible distinct values in the pattern.
     * @return the optimal radix to use for FF1 encryption.
     */
    private int computeOptimalRadix(BigInteger card) {
        double cardLog;
        try {
            cardLog = Math.log(card.longValueExact());
        } catch (ArithmeticException e) {
            cardLog = bigIntegerLog(card);
        }

        int optimalRadix = Character.MIN_RADIX;
        double maxDensity = 0D;
        for (int radix = Character.MIN_RADIX; radix <= Character.MAX_RADIX; radix++) {
            // The remainder of the euclidean division by 1 is only the decimal part of the value.
            int ceilLog = (int) Math.ceil(cardLog / Math.log(radix));

            // Compare the cardinality and radix ^ ( ceil( log_radix(N) ))
            BigInteger radixPow = BigInteger.valueOf(radix).pow(ceilLog);
            int compare = card.compareTo(radixPow);

            if (compare == 0) {
                return radix; // The cardinality is a perfect power of radix.
            } else if (compare < 0) {
                double density = new BigDecimal(card).divide(new BigDecimal(radixPow), MC).doubleValue();
                if (Double.compare(density, maxDensity) > 0) {
                    optimalRadix = radix;
                    maxDensity = density;
                }
            } // else the pattern is too sparse on the current radix.
        }

        if (Double.compare(maxDensity, 0.5D) < 0) {
            // This case can happen when the pattern has been considered as
            // too sparse in radix 2 but it was worse with every other radix.
            optimalRadix = Character.MIN_RADIX;
        }

        return optimalRadix;
    }

    /**
     * Computes the Natural logarithm of a BigInteger.
     *
     * Works for really big integers even when the argument
     * falls outside the <tt>double</tt> range
     *
     * Code taken at : https://stackoverflow.com/questions/1235179/simple-way-to-repeat-a-string-in-java,
     * answer of Maarten Bodewes and Mark Jeronimus.
     *
     * The only change is that the result is a BigDecimal,
     * for better precision of the result.
     *
     * @return Natural logarithm, as in <tt>Math.log()</tt>
     */
    private double bigIntegerLog(BigInteger val) {
        // Get the minimum number of bits necessary to hold this value.
        int n = val.bitLength();

        // Calculate the double-precision fraction of this number; as if the
        // binary point was left of the most significant '1' bit.
        // (Get the most significant 53 bits and divide by 2^53)
        long mask = 1L << 52; // mantissa is 53 bits (including hidden bit)
        long mantissa = 0;
        int j = 0;
        for (int i = 1; i < 54; i++) {
            j = n - i;
            if (j < 0)
                break;

            if (val.testBit(j))
                mantissa |= mask;
            mask >>>= 1;
        }
        // Round up if next bit is 1.
        if (j > 0 && val.testBit(j - 1))
            mantissa++;

        double f = mantissa / (double) (1L << 52);

        // Add the logarithm to the number of bits, and subtract 1 because the
        // number of bits is always higher than necessary for a number
        // (ie. log2(val)<n for every val).
        return (n - 1 + Math.log(f) * INV_LOG2) * LOG2;
    }
}
