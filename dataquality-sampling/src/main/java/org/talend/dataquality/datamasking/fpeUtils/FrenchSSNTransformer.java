package org.talend.dataquality.datamasking.fpeUtils;

import com.idealista.fpe.transformer.IntToTextTransformer;
import com.idealista.fpe.transformer.TextToIntTransformer;
import org.talend.dataquality.datamasking.generic.GenerateUniqueRandomPatterns;
import org.talend.dataquality.datamasking.ssnUtils;

import java.math.BigInteger;
import java.util.List;

public class FrenchSSNTransformer implements TextToIntTransformer, IntToTextTransformer {

    private GenerateUniqueRandomPatterns ssnPattern;

    private int radix;

    private int numSeqLength;

    public FrenchSSNTransformer(int radix, GenerateUniqueRandomPatterns ssnPattern) {
        this.ssnPattern = ssnPattern;
        this.radix = radix;
        this.numSeqLength = ssnPattern.getLongestWidth().toString(radix).length();

    }

    @Override
    public String transform(int[] data) {
        BigInteger rank = new BigInteger(tabInt2String(data), radix);

        String result = getStringFromRank(rank);

        if (result == null) {
            result = "NotValid:" + tabInt2String(data);
        }

        return result;
    }

    @Override
    public int[] transform(String ssn) {

        int[] data = new int[numSeqLength];

        // This case happens when a value has already been enciphered and the output is not valid.
        // In this case we just convert the specified rank back into an int table
        if ("NotValid".matches(ssn.substring(0, 8))) {
            String numRank = ssn.split(":")[1];
            for (int i = 0; i < numSeqLength; i++) {
                data[i] = Character.getNumericValue(numRank.charAt(i));
            }
            return data;
        }

        // Compute the rank of the string to encrypt
        BigInteger rank = getRank(ssn);

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

    private String getStringFromRank(BigInteger rank) {
        if (rank.compareTo(ssnPattern.getLongestWidth()) >= 0) {
            return null;
        }

        // uniqueMaskedNumberList is the unique list created from uniqueMaskedNumber
        List<BigInteger> numericFields = ssnPattern.getFieldsFromRank(rank);

        // decode the fields
        StringBuilder result = ssnPattern.decodeFields(numericFields);

        return result.toString();
    }

    public BigInteger getRank(String str) {

        // TODO :  Call UtilsSsnFr instead of ssnUtils
        List<String> strs = ssnUtils.splitFields(str);

        List<BigInteger> intFields = ssnPattern.getBigIntFieldList(strs);

        return ssnPattern.getSSNRank(intFields);
    }

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
