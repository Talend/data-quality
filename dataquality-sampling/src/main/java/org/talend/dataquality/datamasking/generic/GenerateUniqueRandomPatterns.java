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

import java.math.BigInteger;
import java.util.List;

import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;

/**
 * @author jteuladedenantes
 * <p>
 * This class allows to generate unique random pattern from a list of fields. Each field define a set of possible values.
 */
public class GenerateUniqueRandomPatterns extends AbstractGeneratePattern {

    public GenerateUniqueRandomPatterns(List<AbstractField> fields) {
        super(fields);
    }

    /**
     * @param strs, the string input to encode
     * @return the new string encoding
     */
    public StringBuilder generateUniqueString(List<String> strs, SecretManager secretMng) {
        // check inputs
        if (strs.size() != getFieldsNumber()) {
            return null;
        }

        // encode the fields
        List<BigInteger> listToMask = encodeFields(strs);

        if (listToMask == null) {
            return null;
        }

        // generate the unique random number from the old one
        List<BigInteger> uniqueMaskedNumberList = getUniqueRandomNumber(listToMask, secretMng);

        return decodeFields(uniqueMaskedNumberList);

    }

    /**
     * @param listToMask, the numbers list for each field
     * @return uniqueMaskedNumberList, the masked list
     */
    private List<BigInteger> getUniqueRandomNumber(List<BigInteger> listToMask, SecretManager secretMng) {

        // numberToMask is the number to masked created from listToMask
        BigInteger numberToMask = getNumberToMask(listToMask);

        BigInteger coprimeNumber = BigInteger.valueOf(findLargestCoprime(Math.abs(secretMng.getKey())));

        // uniqueMaskedNumber is the number we masked
        BigInteger uniqueMaskedNumber = (numberToMask.multiply(coprimeNumber)).mod(getLongestWidth());

        return getFieldsFromNumber(uniqueMaskedNumber);
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
}
