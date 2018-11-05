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

import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

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

    @Override
    protected StringBuilder doValidGenerateMaskedField(String str) {
        if (!isValidWithoutFormat(str)) {
            return null;
        }

        List<String> strs = splitFields(str);

        StringBuilder result = ssnPattern.generateUniqueString(strs, secretMng);

        // add the security key specified for french SSN
        String key = computeFrenchKey(result.toString());

        return result.append(key);
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

    // TODO : Add this method to true UtilsSsnFr class
    public List<String> splitFields(String ssn) {
        // read the input str
        List<String> strs = new ArrayList<String>();
        strs.add(ssn.substring(0, 1));
        strs.add(ssn.substring(1, 3));
        strs.add(ssn.substring(3, 5));
        strs.add(ssn.substring(5, 7));
        strs.add(ssn.substring(7, 10));
        strs.add(ssn.substring(10, 13));

        return strs;
    }

    /**
     * Verifies the validity of an ssn string.
     * @return true if valid, false otherwise.
     */
    protected boolean isValidWithoutFormat(String ssn) {
        return ssnPattern.encodeFields(splitFields(ssn)) != null;
    }
}
