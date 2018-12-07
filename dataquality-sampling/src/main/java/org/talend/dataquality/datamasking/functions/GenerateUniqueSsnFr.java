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
import org.talend.dataquality.datamasking.utils.ssn.UtilsSsnFr;

/**
 * 
 * @author jteuladedenantes
 * 
 * French pattern: a-bb-cc-dd-eee-fff a: 1 -> 2 bb: 00 -> 99 cc: 1 -> 12 dd: 01 -> 19 ; (2A, 2B) ; 21 -> 99 eee: 001 -> 990
 * fff: 001 -> 999
 */
public class GenerateUniqueSsnFr extends AbstractGenerateUniqueSsn {

    private static final long serialVersionUID = 4514471121590047091L;

    @Override
    protected String computeKey(StringBuilder str) {
        return UtilsSsnFr.computeFrenchKey(str);
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

        fields.add(new FieldEnum(UtilsSsnFr.getFrenchDepartments(), 2));

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
