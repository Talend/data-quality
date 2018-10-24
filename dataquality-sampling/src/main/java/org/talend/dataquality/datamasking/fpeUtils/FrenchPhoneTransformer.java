package org.talend.dataquality.datamasking.fpeUtils;

import com.idealista.fpe.transformer.IntToTextTransformer;
import com.idealista.fpe.transformer.TextToIntTransformer;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class FrenchPhoneTransformer implements TextToIntTransformer, IntToTextTransformer {

    private static final int MOD97 = 97; // $NON-NLS-1$

    /**
     * The list of all possible values for each field
     */
    private List<AbstractField> fields = createFieldsListFromPattern();

    /**
     * The product of width fields, i.e. the combination of all possibles values
     */
    private BigInteger longestWidth;

    /**
     * BasedWidthsList is used to go from a base to an other
     */
    private List<BigInteger> basedWidthsList;

    private int bitSeqLength;

    private int radix;

    public FrenchPhoneTransformer(int radix) {

        this.radix = radix;

        // longestWidth init
        longestWidth = BigInteger.ONE;
        for (int i = 0; i < getFieldsNumber(); i++) {
            BigInteger width = this.fields.get(i).getWidth();
            longestWidth = longestWidth.multiply(width);
        }

        bitSeqLength = longestWidth.toString(2).length();

        // basedWidthsList init
        basedWidthsList = new ArrayList<BigInteger>();
        basedWidthsList.add(BigInteger.ONE);
        for (int i = getFieldsNumber() - 2; i >= 0; i--)
            basedWidthsList.add(0, this.fields.get(i + 1).getWidth().multiply(this.basedWidthsList.get(0)));
    }

    public int getFieldsNumber() {
        return fields.size();
    }

    @Override
    public String transform(int[] data) {
        StringBuilder binString = new StringBuilder(bitSeqLength);

        for (int b : data) {
            binString.append(b);
        }

        BigInteger rank = new BigInteger(binString.toString(), radix);

        // uniqueMaskedNumberList is the unique list created from uniqueMaskedNumber
        List<BigInteger> numericFields = new ArrayList<BigInteger>();
        for (int i = 0; i < getFieldsNumber(); i++) {
            // baseRandomNumber is the quotient of the Euclidean division between uniqueMaskedNumber and
            // basedWidthsList.get(i)
            BigInteger baseRandomNumber = rank.divide(basedWidthsList.get(i));
            numericFields.add(baseRandomNumber);
            // we reiterate with the remainder of the Euclidean division
            rank = rank.mod(basedWidthsList.get(i));
        }
        // decode the fields
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < getFieldsNumber(); i++) {
            String field = fields.get(i).decode(numericFields.get(i));
            result.append(field);
        }

        return result.toString();
    }

    @Override
    public int[] transform(String text) {

        int[] data = new int[bitSeqLength];

        List<String> strs = splitFields(text);

        // check inputs
        if (strs.size() != getFieldsNumber())
            return null;

        // encode the fields into BigInteger
        List<BigInteger> numericFields = new ArrayList<BigInteger>();
        BigInteger encodeNumber;
        for (int i = 0; i < getFieldsNumber(); i++) {
            encodeNumber = fields.get(i).encode(strs.get(i));
            if (encodeNumber.equals(BigInteger.valueOf(-1))) {
                return null;
            }
            numericFields.add(encodeNumber);
        }

        // Compute the rank of the string to encrypt
        BigInteger rank = BigInteger.ZERO;
        for (int i = 0; i < getFieldsNumber(); i++)
            rank = rank.add(numericFields.get(i).multiply(basedWidthsList.get(i)));

        // Convert the rank into a binary string
        String binString = rank.toString(radix);

        // Add 0-padding if necessary.
        int paddingLength = bitSeqLength - binString.length();
        if (paddingLength > 0) {
            for (int i = 0; i < paddingLength; i++) {
                data[i] = 0;
            }
        }

        // Fill the data tab, starting from the end of the padding.
        int pos = paddingLength;
        for (char c : binString.toCharArray()) {
            data[pos] = Character.getNumericValue(c);
            pos++;
        }

        return data;
    }

    /**
     *
     * @return the list of each field
     */
    protected static List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<AbstractField>();
        long max = (long) Math.pow(10, 6) - 1;
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(max)));
        return fields;
    }

    private List<String> splitFields(String str) {
        // Split the string into the different fields
        List<String> strs = new ArrayList<String>();
        strs.add(str.substring(str.length() - 6, str.length()));
        return strs;
    }
}
