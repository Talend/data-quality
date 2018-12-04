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
 * French pattern: a-bb-cc-dd-eee-fff a: 1 -> 2 bb: 00 -> 99 cc: 1 -> 12 dd: 01 -> 19 ; (2A, 2B) ; 21 -> 99 eee: 001 -> 990
 * fff: 001 -> 999
 */
public class GenerateUniqueSsnFr extends AbstractGenerateUniqueSsn {

    private static final long serialVersionUID = 4514471121590047091L;

    private static final int MOD97 = 97; // $NON-NLS-1$

    @Override
    protected String computeKey(StringBuilder str) {
        StringBuilder keyResult = new StringBuilder(str);

        if (keyResult.charAt(5) == '2') {
            keyResult.setCharAt(5, '1');
            keyResult.setCharAt(6, (keyResult.charAt(6) == 'A') ? '9' : '8');
        }

        int controlKey = MOD97 - (int) (Long.valueOf(keyResult.toString()) % MOD97);

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

    protected List<String> splitFields(String str) {
        // read the input str
        List<String> strs = new ArrayList<String>();
        strs.add(str.substring(0, 1));
        strs.add(str.substring(1, 3));
        strs.add(str.substring(3, 5));
        strs.add(str.substring(5, 7));
        strs.add(str.substring(7, 10));
        strs.add(str.substring(10, 13));

        return strs;
    }
}
