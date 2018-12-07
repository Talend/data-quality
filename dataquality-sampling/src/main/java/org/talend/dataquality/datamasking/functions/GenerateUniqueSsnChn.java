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
import org.talend.dataquality.datamasking.generic.fields.FieldDate;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;
import org.talend.dataquality.datamasking.utils.ssn.UtilsSsnChn;

/**
 * 
 * @author dprot
 * 
 * The Chinese SSN has 4 fields : the first one, on 6 digits, stands for the birth place; the second one, with format
 * YYYYMMDD for the date of birth; the third one, with 3 digits; the last one, on one digit, is a checksum key
 */
public class GenerateUniqueSsnChn extends AbstractGenerateUniqueSsn {

    private static final long serialVersionUID = 4514471121590047091L;

    @Override
    protected String computeKey(StringBuilder str) {
        return UtilsSsnChn.computeChineseKey(str);
    }

    @Override
    protected List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<AbstractField>();

        fields.add(new FieldEnum(UtilsSsnChn.readChinaRegionFile(), 6));

        fields.add(new FieldDate());
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(999)));
        checkSumSize = 1;
        return fields;
    }

    protected List<String> splitFields(String str) {
        // read the input str
        List<String> strs = new ArrayList<String>();
        strs.add(str.substring(0, 6));
        strs.add(str.substring(6, 14));
        strs.add(str.substring(14, 17));

        return strs;
    }

}
