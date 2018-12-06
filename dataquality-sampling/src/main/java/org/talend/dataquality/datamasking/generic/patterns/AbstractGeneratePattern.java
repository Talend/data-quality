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

import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This is an abstract class containing all the common methods and attributes for the 'GeneratePattern" classes.
 *
 * @author afournier
 */
public abstract class AbstractGeneratePattern implements Serializable {

    private static final long serialVersionUID = -5509905086789639724L;

    /**
     * The list of all possible values for each field
     */
    private List<AbstractField> fields;

    /**
     * The number of fields of this pattern
     */
    private int fieldsNumber;

    /**
     * The product of width fields, i.e. the combination of all possibles values
     */
    protected BigInteger longestWidth;

    /**
     * BasedWidthsList is used to go from a base to an other
     */
    private List<BigInteger> basedWidthsList;

    public AbstractGeneratePattern(List<AbstractField> fields) {

        this.fields = fields;
        fieldsNumber = fields.size();

        // longestWidth init
        longestWidth = BigInteger.ONE;
        for (int i = 0; i < fieldsNumber; i++) {
            BigInteger width = this.fields.get(i).getWidth();
            longestWidth = longestWidth.multiply(width);
        }

        // basedWidthsList init
        basedWidthsList = new ArrayList<BigInteger>();
        basedWidthsList.add(BigInteger.ONE);
        for (int i = fieldsNumber - 2; i >= 0; i--) {
            basedWidthsList.add(0, this.fields.get(i + 1).getWidth().multiply(this.basedWidthsList.get(0)));
        }
    }

    public List<AbstractField> getFields() {
        return fields;
    }

    public void setFields(List<AbstractField> fields) {
        this.fields = fields;
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

    public BigInteger getLongestWidth() {
        return this.longestWidth;
    }

    /**
     * @param encodedFieldList The field list encoded as Big Integer
     * @return the decoded field list as a StringBuilder
     */
    public StringBuilder decodeFields(List<BigInteger> encodedFieldList) {
        // check inputs
        if (encodedFieldList.size() != fieldsNumber) {
            return null;
        }

        // decode the fields
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < fieldsNumber; i++) {
            decoded.append(fields.get(i).decode(encodedFieldList.get(i)));
        }
        return decoded;
    }

    /**
     * @param strs, the string input to encode
     * @return the new string encoding
     */
    public List<BigInteger> encodeFields(List<String> strs) {
        // encode the fields
        List<BigInteger> listToMask = new ArrayList<BigInteger>();
        BigInteger encodeNumber;
        for (int i = 0; i < fieldsNumber; i++) {
            encodeNumber = fields.get(i).encode(strs.get(i));
            if (encodeNumber.equals(BigInteger.valueOf(-1))) {
                return null;
            }
            listToMask.add(encodeNumber);
        }

        return listToMask;
    }

    /**
     * @param fieldList, the list of fields expressed as numbers
     * @return the unique number representing the field list
     */
    public BigInteger getRank(List<BigInteger> fieldList) {
        // rank is the unique number identifying the list of fields.
        BigInteger rank = BigInteger.ZERO;
        for (int i = 0; i < fieldsNumber; i++)
            rank = rank.add(fieldList.get(i).multiply(basedWidthsList.get(i)));
        return rank;
    }

    /**
     * @param number A {@code BigInteger} corresponding to the unique number representing a value from the pattern
     * @return the corresponding numeric values of each field
     */
    public List<BigInteger> getFieldsFromNumber(BigInteger number) {
        // fieldList is the unique list created from uniqueMaskedNumber
        List<BigInteger> fieldList = new ArrayList<BigInteger>();
        BigInteger base = number;
        for (int i = 0; i < fieldsNumber; i++) {
            // baseRandomNumber is the quotient of the Euclidean division between uniqueMaskedNumber and
            // basedWidthsList.get(i)
            BigInteger baseRandomNumber = base.divide(basedWidthsList.get(i));
            fieldList.add(baseRandomNumber);
            // we reiterate with the remainder of the Euclidean division
            base = base.mod(basedWidthsList.get(i));
        }
        return fieldList;
    }

    /**
     * @param strs, the string input to encode
     * @param secretMng, the SecretManager instance providing the secrets to generate a unique string
     * @return the new encoded string
     */
    public Optional<StringBuilder> generateUniqueString(List<String> strs, SecretManager secretMng) {
        // check inputs
        if (strs.size() != fieldsNumber) {
            return null;
        }

        return Optional.ofNullable(generateUniquePattern(strs, secretMng));
    }

    /**
     *
     * @param strs the string fields to encode
     * @param secretMng, the SecretManager instance providing the secrets to generate a unique string
     * @return the new encoded string
     */
    public abstract StringBuilder generateUniquePattern(List<String> strs, SecretManager secretMng);
}
