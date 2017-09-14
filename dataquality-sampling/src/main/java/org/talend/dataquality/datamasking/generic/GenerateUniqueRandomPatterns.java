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
package org.talend.dataquality.datamasking.generic;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.sampling.exception.DQRuntimeException;

/**
 * @author jteuladedenantes
 * <p>
 * This class allows to generate unique random pattern from a list of fields. Each field define a set of possible
 * values.
 */
public class GenerateUniqueRandomPatterns implements Serializable {

    private static final long serialVersionUID = -5509905086789639724L;

    /**
     * The maximum width value we can handle (around Long.MaxValue / 11000)
     */
    private static final long WIDTH_THRESHOLD = 838000000000000L;

    /**
     * The random key to make impossible the decoding
     */
    private Integer key;

    /**
     * The list of all possible values for each field
     */
    private List<AbstractField> fields;

    /**
     * The product of width fields, i.e. the combination of all possibles values
     */
    private long longestWidth;

    /**
     * BasedWidthsList is used to go from a base to an other
     */
    private List<Long> basedWidthsList;

    public GenerateUniqueRandomPatterns(List<AbstractField> fields) {
        super();
        this.fields = fields;

        // longestWidth init
        BigInteger longestWidthBig = new BigInteger("1");
        for (int i = 0; i < getFieldsNumber(); i++) {
            long width = this.fields.get(i).getWidth();
            longestWidthBig = longestWidthBig.multiply(BigInteger.valueOf(width));
        }
        if (longestWidthBig.compareTo(BigInteger.valueOf(WIDTH_THRESHOLD)) > 0)
            throw new DQRuntimeException("The pattern is too large to be handled with this component");

        longestWidth = longestWidthBig.longValue();

        // basedWidthsList init
        basedWidthsList = new ArrayList<Long>();
        basedWidthsList.add(1L);
        for (int i = getFieldsNumber() - 2; i >= 0; i--)
            basedWidthsList.add(0, this.fields.get(i + 1).getWidth() * this.basedWidthsList.get(0));
    }

    public List<AbstractField> getFields() {
        return fields;
    }

    public void setFields(List<AbstractField> fields) {
        this.fields = fields;
    }

    public int getFieldsNumber() {
        return fields.size();
    }

    public void setKey(int key) {
        this.key = key;
    }

    /**
     * @param strs, the string input to encode
     * @return the new string encoding
     */
    public StringBuilder generateUniqueString(List<String> strs) {
        // check inputs
        if (strs.size() != getFieldsNumber())
            return null;

        // encode the fields
        List<Long> listToMask = new ArrayList<Long>();
        long encodeNumber;
        for (int i = 0; i < getFieldsNumber(); i++) {
            encodeNumber = fields.get(i).encode(strs.get(i));
            if (encodeNumber == -1) {
                return null;
            }
            listToMask.add(encodeNumber);
        }

        // generate the unique random number from the old one
        List<Long> uniqueMaskedNumberList = getUniqueRandomNumber(listToMask);

        // decode the fields
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < getFieldsNumber(); i++) {
            result.append(fields.get(i).decode(uniqueMaskedNumberList.get(i)));
        }
        return result;
    }

    /**
     * @param listToMask, the numbers list for each field
     * @return uniqueMaskedNumberList, the masked list
     */
    private List<Long> getUniqueRandomNumber(List<Long> listToMask) {

        // numberToMask is the number to masked created from listToMask
        long numberToMask = 0L;
        for (int i = 0; i < getFieldsNumber(); i++)
            numberToMask += listToMask.get(i) * basedWidthsList.get(i);

        if (key == null)
            setKey((new SecureRandom()).nextInt() % 10000 + 1000);
        long coprimeNumber = findLargestCoprime(Math.abs(key));
        // uniqueMaskedNumber is the number we masked
        long uniqueMaskedNumber = (numberToMask * coprimeNumber) % longestWidth;

        // uniqueMaskedNumberList is the unique list created from uniqueMaskedNumber
        List<Long> uniqueMaskedNumberList = new ArrayList<Long>();
        for (int i = 0; i < getFieldsNumber(); i++) {
            // baseRandomNumber is the quotient of the Euclidean division between uniqueMaskedNumber and
            // basedWidthsList.get(i)
            long baseRandomNumber = uniqueMaskedNumber / basedWidthsList.get(i);
            uniqueMaskedNumberList.add(baseRandomNumber);
            // we reiterate with the remainder of the Euclidean division
            uniqueMaskedNumber %= basedWidthsList.get(i);
        }

        return uniqueMaskedNumberList;
    }

    /**
     * @param key the key from we want to find a coprime number with longestWidth
     * @return the largest coprime number with longestWidth less than key
     */
    private long findLargestCoprime(long key) {
        if (pgcdModulo(key, longestWidth) == 1) {
            return key;
        } else {
            return findLargestCoprime(key - 1);
        }
    }

    private long pgcdModulo(long a, long b) {
        if (b == 0)
            return a;
        return pgcdModulo(b, a % b);
    }

    /**
     * @return the sum of fields length (i.e. the number of characters in a field)
     */
    public int getFieldsCharsLength() {
        int length = 0;
        for (AbstractField field : fields) {
            length += field.getLength();
        }
        return length;
    }

}
