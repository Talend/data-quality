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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;
import org.talend.dataquality.datamasking.generic.patterns.AbstractGeneratePattern;
import org.talend.dataquality.datamasking.generic.patterns.GenerateUniqueRandomPatterns;

import static org.junit.Assert.*;

public class GenerateUniqueRandomPatternsTest {

    private AbstractGeneratePattern pattern;

    private SecretManager secretMng;

    protected String minValue;

    protected String maxValue;

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

        secretMng = new SecretManager();
        secretMng.setKey(new Random(454594).nextInt() % 10000 + 1000);
    }

    @Test
    public void testGenerateUniqueString() {
        StringBuilder result = pattern.generateUniqueString(new ArrayList<String>(Arrays.asList("U", "KI", "453", "12")),
                secretMng);
        assertEquals("USF40818", result.toString());
    }

    @Test
    public void maskMaxRankValue() {
        StringBuilder result = pattern.generateUniqueString(new ArrayList<String>(Arrays.asList("S", "DU", "500", "20")),
                secretMng);
        assertEquals("SDU01810", result.toString());
    }

    @Test
    public void maskMinRankValue() {
        StringBuilder result = pattern.generateUniqueString(new ArrayList<String>(Arrays.asList("O", "SF", "000", "5")),
                secretMng);
        // To bad this behavior exists : the min value is always masked to itself.
        assertEquals(minValue, result.toString());
    }

    @Test
    public void maskOutLimitValue() {
        StringBuilder result = pattern.generateUniqueString(new ArrayList<String>(Arrays.asList("U", "KI", "502", "12")),
                secretMng);
        assertNull(result);
    }

    @Test
    public void ensureBijectivity() {
        Set<StringBuilder> uniqueSetTocheck = new HashSet<StringBuilder>();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(pattern.getFields().get(0).getWidth()) < 0; i = i.add(BigInteger.ONE)) {
            for (BigInteger j = BigInteger.ZERO; j.compareTo(pattern.getFields().get(1).getWidth()) < 0; j = j
                    .add(BigInteger.ONE)) {
                for (BigInteger k = BigInteger.ZERO; k.compareTo(pattern.getFields().get(2).getWidth()) < 0; k = k
                        .add(BigInteger.ONE)) {
                    for (BigInteger l = BigInteger.ZERO; l.compareTo(pattern.getFields().get(3).getWidth()) < 0; l = l
                            .add(BigInteger.ONE)) {
                        StringBuilder uniqueMaskedNumber = pattern.generateUniqueString(
                                new ArrayList<String>(
                                        Arrays.asList(pattern.getFields().get(0).decode(i), pattern.getFields().get(1).decode(j),
                                                pattern.getFields().get(2).decode(k), pattern.getFields().get(3).decode(l))),
                                secretMng);

                        assertFalse(" we found twice the uniqueMaskedNumberList " + uniqueMaskedNumber,
                                uniqueSetTocheck.contains(uniqueMaskedNumber));
                        uniqueSetTocheck.add(uniqueMaskedNumber);
                    }
                }
            }
        }
    }
}
