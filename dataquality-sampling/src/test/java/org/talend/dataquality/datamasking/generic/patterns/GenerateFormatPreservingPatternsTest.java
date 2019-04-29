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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

@RunWith(MockitoJUnitRunner.class)
public class GenerateFormatPreservingPatternsTest {

    private GenerateFormatPreservingPatterns encryptPattern;

    private GenerateFormatPreservingPatterns decryptPattern;

    @Mock
    private com.idealista.fpe.algorithm.ff1.Cipher mockCipher;

    private SecretManager secretMng;

    private String minValue;

    private String maxValue;

    @Before
    public void setUp() {
        // encryptPattern we want to test
        List<AbstractField> fields = new ArrayList<>();
        List<String> enums = new ArrayList<>(Arrays.asList("O", "P", "G", "U", "M", "S"));
        fields.add(new FieldEnum(enums, 1));
        enums = new ArrayList<>(Arrays.asList("SF", "KI", "QG", "DU"));
        fields.add(new FieldEnum(enums, 2));
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(50)));
        fields.add(new FieldInterval(BigInteger.valueOf(5), BigInteger.valueOf(20)));

        encryptPattern = new EncryptionPatterns(2, fields);
        decryptPattern = new DecryptionPatterns(2, fields);
        minValue = "OSF0005";
        maxValue = "SDU5020";

        secretMng = new SecretManager(FormatPreservingMethod.SHA2_HMAC_PRF, "#Datadriven2018");
    }

    @Test
    public void findTrivialOptimalRadix() {
        List<AbstractField> fields = new ArrayList<>();
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(9)));

        GenerateFormatPreservingPatterns arabicDigits = new EncryptionPatterns(fields);
        assertEquals(10, arabicDigits.getRadix());
    }

    @Test
    public void findBinaryOptimalRadix() {
        List<AbstractField> fields = new ArrayList<>();
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(255)));

        GenerateFormatPreservingPatterns octet = new EncryptionPatterns(fields);
        assertEquals(2, octet.getRadix());
    }

    @Test
    public void findBigIntOptimalRadix() {
        List<AbstractField> fields = new ArrayList<>();

        // The Big Integer is equal to 2^10000 and this cardinality perfectly fits in 10k-bit strings
        BigInteger card = new BigInteger("1" + new String(new char[10000]).replace("\0", "0"), 2);
        fields.add(new FieldInterval(BigInteger.ONE, card));

        GenerateFormatPreservingPatterns bigPattern = new EncryptionPatterns(fields);
        assertEquals(2, bigPattern.getRadix());
    }

    @Test
    public void almostFullDenseBinaryPattern() {
        List<AbstractField> fields = new ArrayList<>();

        // Here the cardinality is one away to be full dense on 10k bits.
        BigInteger card = new BigInteger("1" + new String(new char[10000]).replace("\0", "0"), 2).add(BigInteger.valueOf(-1L));
        fields.add(new FieldInterval(BigInteger.ONE, card));

        GenerateFormatPreservingPatterns bigPattern = new EncryptionPatterns(fields);
        assertEquals(2, bigPattern.getRadix());
    }

    @Test
    public void sparseBinaryPattern() {
        List<AbstractField> fields = new ArrayList<>();

        // Here the cardinality is one above to be full dense on 10k bits, so it is very sparse on 10001 bits.
        BigInteger card = new BigInteger("1" + new String(new char[10000]).replace("\0", "0"), 2).add(BigInteger.ONE);
        fields.add(new FieldInterval(BigInteger.ONE, card));

        GenerateFormatPreservingPatterns bigPattern = new EncryptionPatterns(fields);
        assertNotEquals(2, bigPattern.getRadix());
    }

    @Test
    public void almostFullDenseBase35Pattern() {
        List<AbstractField> fields = new ArrayList<>();

        // Here the cardinality is one away to be full dense on base-35
        // strings of 1000 characters.
        BigInteger card = new BigInteger("1" + new String(new char[1000]).replace("\0", "0"), 35).add(BigInteger.valueOf(-1L));

        fields.add(new FieldInterval(BigInteger.ONE, card));

        GenerateFormatPreservingPatterns bigPattern = new EncryptionPatterns(fields);
        assertEquals(35, bigPattern.getRadix());
    }

    @Test
    public void sparseBase35Pattern() {
        List<AbstractField> fields = new ArrayList<>();

        // Here the cardinality is one above to be full dense on base-35
        // strings of 1000 characters, so it is very sparse in the ensemble of base-35 strings of 10001 characters.
        BigInteger card = new BigInteger("1" + new String(new char[1000]).replace("\0", "0"), 35).add(BigInteger.ONE);
        fields.add(new FieldInterval(BigInteger.ONE, card));

        GenerateFormatPreservingPatterns bigPattern = new EncryptionPatterns(fields);
        assertNotEquals(35, bigPattern.getRadix());
    }

    @Test
    public void worksForAllRadix() {
        for (int i = Character.MIN_RADIX; i <= Character.MAX_RADIX; i++) {
            GenerateFormatPreservingPatterns pat = new EncryptionPatterns(i, encryptPattern.getFields());
            StringBuilder output = pat.generateUniquePattern(Arrays.asList("U", "KI", "45", "12"), secretMng);
            assertNotNull("Masking did not work with radix value of : " + i, output);
        }
    }

    @Test
    public void transformMinRankValue() throws NoSuchFieldException {

        GenerateFormatPreservingPatterns mockPattern = new EncryptionPatterns(10,
                Collections.singletonList(new FieldInterval(BigInteger.ZERO, BigInteger.TEN)));

        Mockito.when(mockCipher.encrypt(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any()))
                .thenReturn(new int[] { 0, 0 });

        FieldSetter.setField(mockPattern, GenerateFormatPreservingPatterns.class.getDeclaredField("cipher"), mockCipher);

        List<String> input = new ArrayList<>();
        input.add("00");

        assertEquals("00", mockPattern.generateUniquePattern(input, secretMng).toString());
    }

    @Test
    public void transformMaxRankValue() throws NoSuchFieldException {

        GenerateFormatPreservingPatterns mockPattern = new EncryptionPatterns(10,
                Collections.singletonList(new FieldInterval(BigInteger.ZERO, BigInteger.TEN)));

        Mockito.when(mockCipher.encrypt(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any()))
                .thenReturn(new int[] { 1, 0 });

        FieldSetter.setField(mockPattern, GenerateFormatPreservingPatterns.class.getDeclaredField("cipher"), mockCipher);

        List<String> input = new ArrayList<>();
        input.add("10");

        assertEquals("10", mockPattern.generateUniquePattern(input, secretMng).toString());
    }

    @Test
    public void generateUniqueStringAES() {
        SecretManager AESSecMng = new SecretManager(FormatPreservingMethod.AES_CBC_PRF, "#Datadriven2018");
        StringBuilder result = encryptPattern.generateUniqueString(Arrays.asList("U", "KI", "45", "12"), AESSecMng).orElse(null);

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
        StringBuilder result = encryptPattern.generateUniqueString(Arrays.asList("U", "KI", "45", "12"), secretMng).orElse(null);

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
        StringBuilder result = encryptPattern.generateUniqueString(Arrays.asList("O", "SF", "00", "5"), secretMng).orElse(null);
        assertNotNull(result);
        assertNotEquals(minValue, result.toString());
    }

    @Test
    public void maskMaxRankValue() {
        StringBuilder result = encryptPattern.generateUniqueString(Arrays.asList("S", "DU", "50", "20"), secretMng).orElse(null);
        assertNotNull(result);
        assertNotEquals(maxValue, result.toString());
    }

    @Test
    public void maskOutLimitValue() {
        StringBuilder result = encryptPattern.generateUniqueString(Arrays.asList("U", "KI", "52", "12"), secretMng).orElse(null);
        assertNull(result);
    }

    @Test
    public void ensureUniqueness() {
        Set<StringBuilder> uniqueSetTocheck = new HashSet<StringBuilder>();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(encryptPattern.getFields().get(0).getWidth()) < 0; i = i
                .add(BigInteger.ONE)) {
            for (BigInteger j = BigInteger.ZERO; j.compareTo(encryptPattern.getFields().get(1).getWidth()) < 0; j = j
                    .add(BigInteger.ONE)) {
                for (BigInteger k = BigInteger.ZERO; k.compareTo(encryptPattern.getFields().get(2).getWidth()) < 0; k = k
                        .add(BigInteger.ONE)) {
                    for (BigInteger l = BigInteger.ZERO; l.compareTo(encryptPattern.getFields().get(3).getWidth()) < 0; l = l
                            .add(BigInteger.ONE)) {
                        StringBuilder uniqueMaskedNumber = encryptPattern.generateUniqueString(
                                new ArrayList<String>(Arrays.asList(encryptPattern.getFields().get(0).decode(i),
                                        encryptPattern.getFields().get(1).decode(j), encryptPattern.getFields().get(2).decode(k),
                                        encryptPattern.getFields().get(3).decode(l))),
                                secretMng).orElse(null);

                        assertFalse(" we found twice the uniqueMaskedNumberList " + uniqueMaskedNumber,
                                uniqueSetTocheck.contains(uniqueMaskedNumber));
                        uniqueSetTocheck.add(uniqueMaskedNumber);
                    }
                }
            }
        }
    }

    @Test
    public void encryptDecryptAES() {
        SecretManager AESSecMng = new SecretManager(FormatPreservingMethod.AES_CBC_PRF, "#Datadriven2018");
        String input = "UKI4512";
        String encrypted = encryptPattern.generateUniqueString(convert(input), AESSecMng).orElse(null).toString();
        String decrypted = decryptPattern.generateUniqueString(convert(encrypted), AESSecMng).orElse(null).toString();
        assertEquals(input, decrypted);
    }

    @Test
    public void encryptDecryptHMAC() {
        String input = "UKI4512";
        String encrypted = encryptPattern.generateUniqueString(convert(input), secretMng).orElse(null).toString();
        String decrypted = decryptPattern.generateUniqueString(convert(encrypted), secretMng).orElse(null).toString();
        assertEquals(input, decrypted);
    }

    @Test
    public void encryptDecryptAESDifferentPwd() {
        SecretManager AESSecMngEncrypt = new SecretManager(FormatPreservingMethod.AES_CBC_PRF, "#Datadriven2018");
        SecretManager AESSecMngDecrypt = new SecretManager(FormatPreservingMethod.AES_CBC_PRF, "#Datadriven201");
        String input = "UKI4512";
        String encrypted = encryptPattern.generateUniqueString(convert(input), AESSecMngEncrypt).orElse(null).toString();
        String decrypted = decryptPattern.generateUniqueString(convert(encrypted), AESSecMngDecrypt).orElse(null).toString();
        assertNotEquals(input, decrypted);
    }

    @Test
    public void encryptDecryptDifferentMethod() {
        SecretManager AESSecMngEncrypt = new SecretManager(FormatPreservingMethod.AES_CBC_PRF, "#Datadriven2018");
        String input = "UKI4512";
        String encrypted = encryptPattern.generateUniqueString(convert(input), AESSecMngEncrypt).orElse(null).toString();
        String decrypted = decryptPattern.generateUniqueString(convert(encrypted), secretMng).orElse(null).toString();
        assertNotEquals(input, decrypted);
    }

    private List<String> convert(String input) {
        List<String> list = new ArrayList<>();
        list.add(input.substring(0, 1));
        list.add(input.substring(1, 3));
        list.add(input.substring(3, 5));
        list.add(input.substring(5, 7));
        return list;
    }
}
