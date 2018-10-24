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

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;
import org.talend.dataquality.datamasking.fpeUtils.HmacPrf;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;

/**
 * @author jteuladedenantes
 * <p>
 * This class allows to generate unique random pattern from a list of fields. Each field define a set of possible values.
 */
public class GenerateUniqueRandomPatterns implements Serializable {

    private static final long serialVersionUID = -5509905086789639724L;

    /**
     * The random key to make impossible the decoding
     */
    private Integer key;

    /**
     * The keyed pseudo random function used to build a Format-Preserving Encrypter
     */
    private PseudoRandomFunction pseudoRandomFunction;

    /**
     * The list of all possible values for each field
     */
    private List<AbstractField> fields;

    /**
     * The product of width fields, i.e. the combination of all possibles values
     */
    private BigInteger longestWidth;

    /**
     * BasedWidthsList is used to go from a base to an other
     */
    private List<BigInteger> basedWidthsList;

    public GenerateUniqueRandomPatterns(List<AbstractField> fields) {
        super();
        this.fields = fields;

        // longestWidth init
        longestWidth = BigInteger.ONE;
        for (int i = 0; i < getFieldsNumber(); i++) {
            BigInteger width = this.fields.get(i).getWidth();
            longestWidth = longestWidth.multiply(width);
        }

        // basedWidthsList init
        basedWidthsList = new ArrayList<BigInteger>();
        basedWidthsList.add(BigInteger.ONE);
        for (int i = getFieldsNumber() - 2; i >= 0; i--) {
            basedWidthsList.add(0, this.fields.get(i + 1).getWidth().multiply(this.basedWidthsList.get(0)));
        }
        pseudoRandomFunction = new HmacPrf(0);
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
        pseudoRandomFunction = new HmacPrf(key);
    }

    /**
     * @param strs, the string input to encode
     * @return the new string encoding
     */
    public StringBuilder generateUniqueString(List<String> strs) {
        // check inputs
        if (strs.size() != getFieldsNumber()) {
            return null;
        }

        // encode the fields
        List<BigInteger> listToMask = getBigIntFieldList(strs);

        if (listToMask == null) {
            return null;
        }

        // generate the unique random number from the old one
        List<BigInteger> uniqueMaskedNumberList = getUniqueRandomNumber(listToMask);

        return decodeFields(uniqueMaskedNumberList);

    }

    /**
     * @param listToMask, the numbers list for each field
     * @return uniqueMaskedNumberList, the masked list
     */
    private List<BigInteger> getUniqueRandomNumber(List<BigInteger> listToMask) {

        // numberToMask is the number to masked created from listToMask
        BigInteger numberToMask = getSSNRank(listToMask);

        if (key == null) {
            setKey((new SecureRandom()).nextInt(Integer.MAX_VALUE - 1000000) + 1000000);
        }
        BigInteger coprimeNumber = BigInteger.valueOf(findLargestCoprime(Math.abs(key)));
        // uniqueMaskedNumber is the number we masked
        BigInteger uniqueMaskedNumber = (numberToMask.multiply(coprimeNumber)).mod(longestWidth);

        return getFieldsFromRank(uniqueMaskedNumber);
    }

    /**
     * @param key the key from we want to find a coprime number with longestWidth
     * @return the largest coprime number with longestWidth less than key
     */
    private long findLargestCoprime(long key) {
        if (BigInteger.valueOf(key).gcd(longestWidth).equals(BigInteger.ONE)) {
            return key;
        } else {
            return findLargestCoprime(key - 1);
        }
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

    public PseudoRandomFunction getPseudoRandomFunction() {
        return this.pseudoRandomFunction;
    }

    public BigInteger getLongestWidth() {
        return this.longestWidth;
    }

    /**
     * @param uniqueMaskedNumberList The field list encoded as Big Integer
     * @return the decoded field list as a StringBuilder
     */
    public StringBuilder decodeFields(List<BigInteger> uniqueMaskedNumberList) {
        // decode the fields
        StringBuilder decoded = new StringBuilder("");
        for (int i = 0; i < getFieldsNumber(); i++) {
            decoded.append(fields.get(i).decode(uniqueMaskedNumberList.get(i)));
        }
        return decoded;
    }

    /**
     * @param strs, the string input to encode
     * @return the new string encoding
     */
    public List<BigInteger> getBigIntFieldList(List<String> strs) {
        // encode the fields
        List<BigInteger> listToMask = new ArrayList<BigInteger>();
        BigInteger encodeNumber;
        for (int i = 0; i < getFieldsNumber(); i++) {
            encodeNumber = fields.get(i).encode(strs.get(i));
            if (encodeNumber.equals(BigInteger.valueOf(-1))) {
                return null;
            }
            listToMask.add(encodeNumber);
        }

        return listToMask;
    }

    /**
     * @param listToMask, the numbers list for each field
     * @return uniqueMaskedNumberList, the masked list
     */
    public BigInteger getSSNRank(List<BigInteger> listToMask) {
        // numberToMask is the number to masked created from listToMask
        BigInteger numberToMask = BigInteger.ZERO;
        for (int i = 0; i < getFieldsNumber(); i++)
            numberToMask = numberToMask.add(listToMask.get(i).multiply(basedWidthsList.get(i)));

        return numberToMask;
    }

    public List<BigInteger> getFieldsFromRank(BigInteger uniqueMaskedNumber) {
        // uniqueMaskedNumberList is the unique list created from uniqueMaskedNumber
        List<BigInteger> uniqueMaskedNumberList = new ArrayList<BigInteger>();
        for (int i = 0; i < getFieldsNumber(); i++) {
            // baseRandomNumber is the quotient of the Euclidean division between uniqueMaskedNumber and
            // basedWidthsList.get(i)
            BigInteger baseRandomNumber = uniqueMaskedNumber.divide(basedWidthsList.get(i));
            uniqueMaskedNumberList.add(baseRandomNumber);
            // we reiterate with the remainder of the Euclidean division
            uniqueMaskedNumber = uniqueMaskedNumber.mod(basedWidthsList.get(i));
        }
        return uniqueMaskedNumberList;
    }
}
