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

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AbstractGeneratePatternTest {

    private AbstractGeneratePattern pattern;

    private String minValue;

    private String maxValue;

    private List<BigInteger> minFields;

    private List<BigInteger> maxFields;

    @Before
    public void setUp() throws Exception {
        // pattern we want to test
        List<AbstractField> fields = new ArrayList<AbstractField>();
        List<String> enums = new ArrayList<String>(Arrays.asList("O", "P", "G", "U", "M", "S"));
        fields.add(new FieldEnum(enums, 1));
        enums = new ArrayList<String>(Arrays.asList("SF", "KI", "QG", "DU"));
        fields.add(new FieldEnum(enums, 2));
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(500)));
        fields.add(new FieldInterval(BigInteger.valueOf(5), BigInteger.valueOf(20)));

        pattern = new GenerateUniqueRandomPatterns(fields);
        minValue = "OSF00005";
        maxValue = "SDU50020";

        minFields = new ArrayList<>();
        minFields.add(BigInteger.valueOf(0));
        minFields.add(BigInteger.valueOf(0));
        minFields.add(BigInteger.valueOf(0));
        minFields.add(BigInteger.valueOf(0));

        maxFields = new ArrayList<>();
        maxFields.add(BigInteger.valueOf(5));
        maxFields.add(BigInteger.valueOf(3));
        maxFields.add(BigInteger.valueOf(500));
        maxFields.add(BigInteger.valueOf(15));
    }

    @Test
    public void encodeMaxValueFields() {
        List<BigInteger> encodedList = pattern.encodeFields(Arrays.asList("S", "DU", "500", "20"));
        assertEquals(maxFields, encodedList);
    }

    @Test
    public void encodeMinValueFields() {
        List<BigInteger> encodedList = pattern.encodeFields(Arrays.asList("O", "SF", "000", "5"));
        assertEquals(minFields, encodedList);
    }

    @Test
    public void decodeMaxValueFields() {
        String decodedList = pattern.decodeFields(maxFields).toString();
        assertEquals(maxValue, decodedList);
    }

    @Test
    public void decodeMinValueFields() {
        String decodedList = pattern.decodeFields(minFields).toString();
        assertEquals(minValue, decodedList);
    }

    @Test
    public void getRankMaxValue() {
        BigInteger rank = pattern.getRank(maxFields);
        assertEquals(pattern.getLongestWidth().add(BigInteger.valueOf(-1)), rank);
    }

    @Test
    public void getRankMinValue() {
        BigInteger rank = pattern.getRank(minFields);
        assertEquals(BigInteger.ZERO, rank);
    }

    @Test
    public void getFieldsMaxRank() {
        List<BigInteger> fieldList = pattern.getFieldsFromNumber(pattern.longestWidth.add(BigInteger.valueOf(-1)));
        assertEquals(maxFields, fieldList);
    }

    @Test
    public void getFieldsMinRank() {
        List<BigInteger> fieldList = pattern.getFieldsFromNumber(BigInteger.ZERO);
        assertEquals(minFields, fieldList);
    }
}
