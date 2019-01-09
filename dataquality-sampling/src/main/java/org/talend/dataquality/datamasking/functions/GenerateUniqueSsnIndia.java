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
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;
import org.talend.dataquality.datamasking.utils.ssn.UtilsSsnIndia;

/**
 * 
 * @author dprot
 * 
 * Indian pattern: abbbbbbbbbbc a: 1 -> 9 b: 0 -> 9 c: checksum with Verhoeff' algorithm
 */
public class GenerateUniqueSsnIndia extends AbstractGenerateUniqueSsn {

    private static final long serialVersionUID = 4514471121590047091L;

    @Override
    protected String computeKey(StringBuilder str) {
        return UtilsSsnIndia.computeIndianKey(str);
    }

    /**
     * 
     * @return the list of each field
     */
    @Override
    protected List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<>();

        fields.add(new FieldInterval(BigInteger.ONE, BigInteger.valueOf(9)));
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(9999999999L)));

        checkSumSize = 1;
        return fields;
    }

    @Override
    protected List<String> splitFields(String str) {
        List<String> strs = new ArrayList<>();
        strs.add(str.substring(0, 1));
        strs.add(str.substring(1, 11));
        return strs;
    }
}
