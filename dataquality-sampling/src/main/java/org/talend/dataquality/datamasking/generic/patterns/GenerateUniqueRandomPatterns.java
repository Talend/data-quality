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

    private static final long serialVersionUID = -6142792466994187170L;

    public GenerateUniqueRandomPatterns(List<AbstractField> fields) {
        super(fields);
    }

    @Override
    public StringBuilder generateUniquePattern(List<String> strs, SecretManager secretMng) {
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
     * @param listToMask, the list of fields expressed as numbers
     * @return the list of masked fields expressed as numbers
     */
    private List<BigInteger> getUniqueRandomNumber(List<BigInteger> listToMask, SecretManager secretMng) {

        // rank is the unique ordinal number corresponding to the listToMask
        BigInteger rank = getRank(listToMask);

        BigInteger coprimeNumber = BigInteger.valueOf(findLargestCoprime(Math.abs(secretMng.getKey())));

        // newRank is the permuted rank and identifies the masked list of fields
        BigInteger newRank = (rank.multiply(coprimeNumber)).mod(getLongestWidth());

        return getFieldsFromNumber(newRank);
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
