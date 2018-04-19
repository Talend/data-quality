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
 * @author jteuladedenantes
 * 
 * US pattern: aaa-bb-cccc aaa: 001 -> 665 ; 667 -> 899 bb: 01 -> 99 cccc: 0001->9999
 */
public class GenerateUniqueSsnUs extends AbstractGenerateUniqueSsn {

    private static final long serialVersionUID = 948793448882763445L;

    @Override
    protected List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<AbstractField>();
        List<String> firstField = new ArrayList<String>();
        for (int i = 1; i < 900; i++) {
            if (i < 10)
                firstField.add("00" + i);
            else if (i < 100)
                firstField.add("0" + i);
            else if (i != 666)
                firstField.add(String.valueOf(i));
        }
        fields.add(new FieldEnum(firstField, 3));
        fields.add(new FieldInterval(BigInteger.ONE, BigInteger.valueOf(99)));
        fields.add(new FieldInterval(BigInteger.ONE, BigInteger.valueOf(9999)));

        return fields;
    }

    @Override
    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input strWithoutSpaces
        List<String> strs = new ArrayList<String>();
        strs.add(str.substring(0, 3));
        strs.add(str.substring(3, 5));
        strs.add(str.substring(5, 9));

        return ssnPattern.generateUniqueString(strs);
    }

}
