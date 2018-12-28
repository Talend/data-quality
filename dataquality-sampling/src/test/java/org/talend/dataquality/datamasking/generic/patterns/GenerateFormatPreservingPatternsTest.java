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
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.runners.MockitoJUnitRunner;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GenerateFormatPreservingPatternsTest {

    private GenerateFormatPreservingPatterns pattern;

    @Mock
    private com.idealista.fpe.algorithm.ff1.Cipher mockCipher;

    private SecretManager secretMng;

    private String minValue;

    private String maxValue;

    @Before
    public void setUp() {
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

        secretMng = new SecretManager(FormatPreservingMethod.SHA2_HMAC_PRF, "#Datadriven2018");
    }

    @Test
    public void findTrivialOptimalRadix() {
        List<AbstractField> fields = new ArrayList<>();
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(9)));

        GenerateFormatPreservingPatterns arabicDigits = new GenerateFormatPreservingPatterns(fields);
        assertEquals(10, arabicDigits.getRadix());
    }

    @Test
    public void findBinaryOptimalRadix() {
        List<AbstractField> fields = new ArrayList<>();
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(255)));

        GenerateFormatPreservingPatterns octet = new GenerateFormatPreservingPatterns(fields);
        assertEquals(2, octet.getRadix());
    }

    @Test
    public void findBigIntOptimalRadix() {
        List<AbstractField> fields = new ArrayList<>();

        // The Big Integer is equal to 2^10000 and this cardinality perfectly fits in 10k-bit strings
        BigInteger card = new BigInteger("1" + new String(new char[10000]).replace("\0", "0"), 2);
        fields.add(new FieldInterval(BigInteger.ONE, card));

        GenerateFormatPreservingPatterns bigPattern = new GenerateFormatPreservingPatterns(fields);
        assertEquals(2, bigPattern.getRadix());
    }

    @Test
    public void almostFullDenseBinaryPattern() {
        List<AbstractField> fields = new ArrayList<>();

        // Here the cardinality is one away to be full dense on 10k bits.
        BigInteger card = new BigInteger("1" + new String(new char[10000]).replace("\0", "0"), 2).add(BigInteger.valueOf(-1L));
        fields.add(new FieldInterval(BigInteger.ONE, card));

        GenerateFormatPreservingPatterns bigPattern = new GenerateFormatPreservingPatterns(fields);
        assertEquals(2, bigPattern.getRadix());
    }

    @Test
    public void sparseBinaryPattern() {
        List<AbstractField> fields = new ArrayList<>();

        // Here the cardinality is one above to be full dense on 10k bits, so it is very sparse on 10001 bits.
        BigInteger card = new BigInteger("1" + new String(new char[10000]).replace("\0", "0"), 2).add(BigInteger.ONE);
        fields.add(new FieldInterval(BigInteger.ONE, card));

        GenerateFormatPreservingPatterns bigPattern = new GenerateFormatPreservingPatterns(fields);
        assertNotEquals(2, bigPattern.getRadix());
    }

    @Test
    public void almostFullDenseBase35Pattern() {
        List<AbstractField> fields = new ArrayList<>();

        // Here the cardinality is one away to be full dense on base-35
        // strings of 1000 characters.
        BigInteger card = new BigInteger("1" + new String(new char[1000]).replace("\0", "0"), 35).add(BigInteger.valueOf(-1L));

        fields.add(new FieldInterval(BigInteger.ONE, card));

        GenerateFormatPreservingPatterns bigPattern = new GenerateFormatPreservingPatterns(fields);
        assertEquals(35, bigPattern.getRadix());
    }

    @Test
    public void sparseBase35Pattern() {
        List<AbstractField> fields = new ArrayList<>();

        // Here the cardinality is one above to be full dense on base-35
        // strings of 1000 characters, so it is very sparse in the ensemble of base-35 strings of 10001 characters.
        BigInteger card = new BigInteger("1" + new String(new char[1000]).replace("\0", "0"), 35).add(BigInteger.ONE);
        fields.add(new FieldInterval(BigInteger.ONE, card));

        GenerateFormatPreservingPatterns bigPattern = new GenerateFormatPreservingPatterns(fields);
        assertNotEquals(35, bigPattern.getRadix());
    }

    @Test
    public void worksForAllRadix() {
        for (int i = Character.MIN_RADIX; i <= Character.MAX_RADIX; i++) {
            GenerateFormatPreservingPatterns pat = new GenerateFormatPreservingPatterns(i, pattern.getFields());
            StringBuilder output = pat.generateUniquePattern(Arrays.asList("U", "KI", "45", "12"), secretMng);
            assertNotNull("Masking did not work with radix value of : " + i, output);
        }
    }

    @Test
    public void transformMinRankValue() throws NoSuchFieldException {

        GenerateFormatPreservingPatterns mockPattern = new GenerateFormatPreservingPatterns(10,
                Collections.singletonList(new FieldInterval(BigInteger.ZERO, BigInteger.TEN)));

        Mockito.when(mockCipher.encrypt(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any()))
                .thenReturn(new int[] { 0, 0 });

        new FieldSetter(mockPattern, GenerateFormatPreservingPatterns.class.getDeclaredField("cipher")).set(mockCipher);

        List<String> input = new ArrayList<>();
        input.add("00");

        assertEquals("00", mockPattern.generateUniquePattern(input, secretMng).toString());
    }

    @Test
    public void transformMaxRankValue() throws NoSuchFieldException {

        GenerateFormatPreservingPatterns mockPattern = new GenerateFormatPreservingPatterns(10,
                Collections.singletonList(new FieldInterval(BigInteger.ZERO, BigInteger.TEN)));

        Mockito.when(mockCipher.encrypt(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any()))
                .thenReturn(new int[] { 1, 0 });

        new FieldSetter(mockPattern, GenerateFormatPreservingPatterns.class.getDeclaredField("cipher")).set(mockCipher);

        List<String> input = new ArrayList<>();
        input.add("10");

        assertEquals("10", mockPattern.generateUniquePattern(input, secretMng).toString());
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
