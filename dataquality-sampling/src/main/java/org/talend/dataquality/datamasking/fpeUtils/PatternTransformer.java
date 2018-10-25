package org.talend.dataquality.datamasking.fpeUtils;

import com.idealista.fpe.transformer.IntToTextTransformer;
import com.idealista.fpe.transformer.TextToIntTransformer;
import org.talend.dataquality.datamasking.generic.GenerateUniqueRandomPatterns;

import java.math.BigInteger;
import java.util.List;

/**
 * @author afournier
 *
 * This abstract class contains the methods required to transform String data into int[] format.
 * these methods are used during the format-preserving encryption using
 * <a href="https://github.com/idealista/format-preserving-encryption-java">idealista's library</a>.
 *
 * The class is composed of the same instance of {@link GenerateUniqueRandomPatterns}
 * as its corresponding {@code GenerateUniqueX} class.
 */
public abstract class PatternTransformer implements TextToIntTransformer, IntToTextTransformer {

    protected GenerateUniqueRandomPatterns pattern;

    protected int radix;

    protected int numSeqLength;

    protected PatternTransformer(int radix, GenerateUniqueRandomPatterns pattern) {
        this.pattern = pattern;
        this.radix = radix;
        this.numSeqLength = pattern.getLongestWidth().toString(radix).length();
    }

    /**
     * Transform the an encrypted array of {@code int}s into the corresponding {@code String} representation.
     */
    @Override
    public String transform(int[] data) {
        BigInteger rank = new BigInteger(tabInt2String(data), radix);

        String result = getStringFromRank(rank);

        if (result == null) {
            result = "NotValid:" + tabInt2String(data);
        }

        return result;
    }

    /**
     * Transform the {@code String} element into an array of {@code int}s for FF1 encryption.
     */
    @Override
    public int[] transform(String str) {

        int[] data = new int[numSeqLength];

        // This case happens when a value has already been enciphered and the output is not valid.
        // In this case we just convert the specified rank back into an int table
        if ("NotValid".matches(str.substring(0, 8))) {
            String numRank = str.split(":")[1];
            for (int i = 0; i < numSeqLength; i++) {
                data[i] = Character.getNumericValue(numRank.charAt(i));
            }
            return data;
        }

        // Compute the rank of the string to encrypt
        BigInteger rank = getRank(str);

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

    /**
     * @param rank A {@code BigInteger} corresponding to the rank of an element
     * @return The corresponding string representation of the element
     */
    private String getStringFromRank(BigInteger rank) {
        if (rank.compareTo(pattern.getLongestWidth()) >= 0) {
            return null;
        }

        // uniqueMaskedNumberList is the unique list created from uniqueMaskedNumber
        List<BigInteger> numericFields = pattern.getFieldsFromRank(rank);

        // decode the fields
        StringBuilder result = pattern.decodeFields(numericFields);

        return result.toString();
    }

    /**
     * @param str the {@code String} representation of the element
     * @return the corresponding rank as a {@code BigInteger}
     */
    public abstract BigInteger getRank(String str);

    public String tabInt2String(int[] data) {
        StringBuilder sb = new StringBuilder();

        if (data.length > 0) {
            for (int n : data) {
                sb.append(n);
            }
        }
        return sb.toString();
    }
}
