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

import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class GenerateFormatPreservingPatternsTest {

    private static GenerateFormatPreservingPatterns pattern;

    private static SecretManager secretMng;

    private static String minValue;

    private static String maxValue;

    private static List<String> minStringList;

    private static List<String> maxStringList;

    @BeforeClass
    public static void setUp() {
        // pattern we want to test
        List<AbstractField> fields = new ArrayList<AbstractField>();
        List<String> enums = new ArrayList<String>(Arrays.asList("O", "P", "G", "U", "M", "S"));
        fields.add(new FieldEnum(enums, 1));
        enums = new ArrayList<String>(Arrays.asList("SF", "KI", "QG", "DU"));
        fields.add(new FieldEnum(enums, 2));
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(50)));
        fields.add(new FieldInterval(BigInteger.valueOf(5), BigInteger.valueOf(20)));

        pattern = new GenerateFormatPreservingPatterns(2, fields);
        minValue = "OSF0005";
        maxValue = "SDU5020";

        minStringList = Arrays.asList("O", "SF", "00", "5");
        maxStringList = Arrays.asList("S", "DU", "50", "20");

        secretMng = new SecretManager(FormatPreservingMethod.SHA2_HMAC_PRF, "#Datadriven2018");
    }

    @Test
    public void transformMinRankValue() {
        String str = pattern.transform(pattern.transform(minStringList)).toString();
        assertEquals(minValue, str);
    }

    @Test
    public void transformMaxRankValue() {
        String str = pattern.transform(pattern.transform(maxStringList)).toString();
        assertEquals(maxValue, str);
    }

    @Test
    public void transformOutLimitValue() {
        int[] outLimit = pattern.transform(Arrays.asList("U", "KI", "52", "12"));
        assertEquals(0, outLimit.length);
    }

    @Test
    public void generateUniqueStringAES() {
        SecretManager AESSecMng = new SecretManager(FormatPreservingMethod.AES_CBC_PRF, "#Datadriven2018");
        StringBuilder result = pattern.generateUniqueString(Arrays.asList("U", "KI", "45", "12"), AESSecMng).orElse(null);

        String expected;
        if (AESSecMng.getCryptoSpec().getKeyLength() == 32) {
            expected = "OKI0208";
        } else {
            expected = "SKI1816";
        }
        assertNotNull(result);
        assertEquals(expected, result.toString());
    }

    @Test
    public void generateUniqueStringHMAC() {
        StringBuilder result = pattern.generateUniqueString(Arrays.asList("U", "KI", "45", "12"), secretMng).orElse(null);

        String expected;
        if (secretMng.getCryptoSpec().getKeyLength() == 32) {
            expected = "OKI1514";
        } else {
            expected = "OSF4017";
        }
        assertNotNull(result);
        assertEquals(expected, result.toString());
    }

    @Test
    public void severalRadix() {
        for (int i = Character.MIN_RADIX; i <= Character.MAX_RADIX; i++) {
            pattern = new GenerateFormatPreservingPatterns(i, pattern.getFields());
            StringBuilder output = pattern.generateUniquePattern(Arrays.asList("U", "KI", "45", "12"), secretMng);
            assertNotNull("Masking did not work with radix value of : " + i, output);
        }
    }

    @Test
    public void maskMinRankValue() {
        StringBuilder result = pattern.generateUniqueString(Arrays.asList("O", "SF", "00", "5"), secretMng).orElse(null);
        assertNotNull(result);
        assertNotEquals(minValue, result.toString());
    }

    @Test
    public void maskMaxRankValue() {
        StringBuilder result = pattern.generateUniqueString(Arrays.asList("S", "DU", "50", "20"), secretMng).orElse(null);
        assertNotNull(result);
        assertNotEquals(maxValue, result.toString());
    }

    @Test
    public void maskOutLimitValue() {
        StringBuilder result = pattern.generateUniqueString(Arrays.asList("U", "KI", "52", "12"), secretMng).orElse(null);
        assertNull(result);
    }

    @Test
    public void ensureUniqueness() {
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
                                secretMng).orElse(null);

                        assertFalse(" we found twice the uniqueMaskedNumberList " + uniqueMaskedNumber,
                                uniqueSetTocheck.contains(uniqueMaskedNumber));
                        uniqueSetTocheck.add(uniqueMaskedNumber);
                    }
                }
            }
        }
    }
}
