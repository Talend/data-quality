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
package org.talend.dataquality.datamasking.functions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.idealista.fpe.FormatPreservingEncryption;
import com.idealista.fpe.builder.FormatPreservingEncryptionBuilder;
import com.idealista.fpe.builder.steps.Builder;
import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;
import com.idealista.fpe.config.Domain;
import com.idealista.fpe.config.GenericDomain;
import com.idealista.fpe.config.LengthRange;
import org.talend.dataquality.datamasking.fpeUtils.BinaryAlphabet;
import org.talend.dataquality.datamasking.fpeUtils.FrenchSSNTransformer;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;
import org.talend.dataquality.datamasking.ssnUtils;

/**
 * 
 * @author jteuladedenantes
 * 
 * French patter: a-bb-cc-dd-eee-fff a: 1 -> 2 bb: 0 -> 99 cc: 1 -> 12 dd: 1 -> 19 ; (2A, 2B) ; 20 -> 99 eee: 1 -> 990
 * fff: 1 -> 999
 */
public class GenerateUniqueSsnFr extends AbstractGenerateUniqueSsn {

    private static final long serialVersionUID = 4514471121590047091L;

    private static final int MOD97 = 97; // $NON-NLS-1$

    private FormatPreservingEncryption fpeEncrypter = null;
    /*    @Override
    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input str
        List<String> strs = new ArrayList<String>();
        strs.add(str.substring(0, 1));
        strs.add(str.substring(1, 3));
        strs.add(str.substring(3, 5));
        strs.add(str.substring(5, 7));
        strs.add(str.substring(7, 10));
        strs.add(str.substring(10, 13));
    
        StringBuilder result = ssnPattern.generateUniqueString(strs);
        if (result == null) {
            return null;
        }
    
        // add the security key specified for french SSN
        String key = computeFrenchKey(result.toString());
    
        result.append(key);
    
        return result;
    }*/

    @Override
    protected StringBuilder doValidGenerateMaskedField(String str) {

        if (!isValid(str)) {
            return null;
        }

        if (fpeEncrypter == null) {
            fpeEncrypter = buildFPEInstance(ssnPattern.getPseudoRandomFunction());
        }

        byte[] tweak = computeTweak(str);
        String result = fpeEncrypter.encrypt(str, tweak);

        while (!isValid(result)) {
            result = fpeEncrypter.encrypt(result, tweak);
        }

        // add the security key specified for french SSN
        String key = computeFrenchKey(result);

        return new StringBuilder(result).append(key);
    }

    private String computeFrenchKey(String string) {

        StringBuilder keyResult = new StringBuilder(string);

        if (keyResult.charAt(5) == '2') {
            keyResult.setCharAt(5, '1');
            keyResult.setCharAt(6, (keyResult.charAt(6) == 'A') ? '9' : '8');
        }
        // TODO : 97 should be replaced by MOD97 or no ?
        int controlKey = 97 - (int) (Long.valueOf(keyResult.toString()) % MOD97);

        StringBuilder res = new StringBuilder();
        if (controlKey < 10)
            res.append("0");
        return res.append(controlKey).toString();
    }

    /**
     * 
     * @return the list of each field
     */
    @Override
    protected List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<AbstractField>();

        fields.add(new FieldInterval(BigInteger.ONE, BigInteger.valueOf(2)));
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(99)));
        fields.add(new FieldInterval(BigInteger.ONE, BigInteger.valueOf(12)));

        List<String> departments = new ArrayList<String>();
        for (int department = 1; department <= 99; department++) {
            if (department < 10)
                departments.add("0" + department);
            else if (department == 20) {
                departments.add("2A");
                departments.add("2B");
            } else
                departments.add(String.valueOf(department));
        }
        fields.add(new FieldEnum(departments, 2));

        fields.add(new FieldInterval(BigInteger.ONE, BigInteger.valueOf(990)));
        fields.add(new FieldInterval(BigInteger.ONE, BigInteger.valueOf(999)));

        checkSumSize = 2;
        return fields;
    }

    public List<AbstractField> getFields() {
        return createFieldsListFromPattern();
    }

    private FormatPreservingEncryption buildFPEInstance(PseudoRandomFunction prf) {

        FrenchSSNTransformer ssnTransformer = new FrenchSSNTransformer(2, ssnPattern);
        Domain domain = new GenericDomain(new BinaryAlphabet(), ssnTransformer, ssnTransformer);

        Builder fpeBuilder = FormatPreservingEncryptionBuilder.ff1Implementation().withDomain(domain)
                .withPseudoRandomFunction(prf).withLengthRange(new LengthRange(7, 4096));

        return fpeBuilder.build();
    }

    private byte[] computeTweak(String ssn) {
        return new byte[] {};
    }

    protected boolean isValid(String ssn) {
        List<BigInteger> numericFields = ssnPattern.getBigIntFieldList(ssnUtils.splitFields(ssn));
        if (numericFields == null) {
            return false;
        }

        BigInteger rank = ssnPattern.getSSNRank(numericFields);
        return rank.compareTo(ssnPattern.getLongestWidth()) < 0;
    }
}
