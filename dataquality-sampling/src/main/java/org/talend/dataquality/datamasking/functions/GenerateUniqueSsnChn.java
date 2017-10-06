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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldDate;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

/**
 * 
 * @author dprot
 * 
 * The Chinese SSN has 4 fields : the first one, on 6 digits, stands for the birth place; the second one, with format
 * YYYYMMDD for the date of birth; the third one, with 3 digits; the last one, on one digit, is a checksum key
 */
public class GenerateUniqueSsnChn extends AbstractGenerateUniqueSsn {

    private static final long serialVersionUID = 4514471121590047091L;

    private static final Logger LOGGER = Logger.getLogger(GenerateUniqueSsnChn.class);

    private static final List<Integer> keyWeight = Collections
            .unmodifiableList(Arrays.asList(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2));

    private static final int KEYMOD = 11; // $NON-NLS-1$

    private static final List<String> keyString = Collections
            .unmodifiableList(Arrays.asList("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"));

    private String computeChineseKey(String ssnNumber) {
        int key = 0;
        for (int i = 0; i < 17; i++) {
            key += Character.getNumericValue(ssnNumber.charAt(i)) * keyWeight.get(i);
        }
        key = key % KEYMOD;
        return keyString.get(key);
    }

    @Override
    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input strWithoutSpaces
        List<String> strs = new ArrayList<String>();
        strs.add(str.substring(0, 6));
        strs.add(str.substring(6, 14));
        strs.add(str.substring(14, 17));

        StringBuilder result = ssnPattern.generateUniqueString(strs);
        if (result == null) {
            return null;
        }

        // Add the security key specified for Chinese SSN
        String controlKey = computeChineseKey(result.toString());
        result.append(controlKey);

        return result;
    }

    @Override
    protected List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<AbstractField>();

        InputStream is = GenerateUniqueSsnChn.class.getResourceAsStream("RegionListChina.txt");
        try {
            List<String> places = IOUtils.readLines(is, "UTF-8");

            fields.add(new FieldEnum(places, 6));

        } catch (IOException e) {
            LOGGER.error("The file of chinese regions is not correctly loaded " + e.getMessage(), e);
        }
        fields.add(new FieldDate());
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(999)));
        checkSumSize = 1;
        return fields;
    }

}
