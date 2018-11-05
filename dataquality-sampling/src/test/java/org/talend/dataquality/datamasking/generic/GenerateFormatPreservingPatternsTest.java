package org.talend.dataquality.datamasking.generic;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.*;

public class GenerateFormatPreservingPatternsTest {

    private GenerateFormatPreservingPatterns pattern;

    private SecretManager secretMng;

    private String minValue;

    private String maxValue;

    private List<String> minStringList;

    private List<String> maxStringList;

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

        pattern = new GenerateFormatPreservingPatterns(2, fields);
        minValue = "OSF00005";
        maxValue = "SDU50020";

        minStringList = Arrays.asList("O", "SF", "000", "5");
        maxStringList = Arrays.asList("S", "DU", "500", "20");

        secretMng = new SecretManager();
        secretMng.setPrfAlgo(2);
        secretMng.setPassword("#Datadriven2018");
    }

    @Test
    public void transform_value_with_min_rank() {
        String str = pattern.transform(pattern.transform(minStringList)).toString();
        assertEquals(minValue, str);
    }

    @Test
    public void transform_value_with_max_rank() {
        String str = pattern.transform(pattern.transform(maxStringList)).toString();
        assertEquals(maxValue, str);
    }

    @Test
    public void transform_value_out_limit() {
        int[] outLimit = pattern.transform(Arrays.asList("U", "KI", "502", "12"));
        assertNull(outLimit);
    }

    @Test
    public void generate_AES_CBC_encrypted_string() {
        SecretManager AESSecMng = new SecretManager();
        AESSecMng.setPrfAlgo(1);
        AESSecMng.setPassword("#Datadriven2018");
        StringBuilder result = pattern.generateUniqueString(Arrays.asList("U", "KI", "453", "12"), AESSecMng);
        assertEquals("GDU45211", result.toString());
    }

    @Test
    public void generate_SHA2_HMAC_encrypted_string() {
        StringBuilder result = pattern.generateUniqueString(Arrays.asList("U", "KI", "453", "12"), secretMng);
        assertEquals("PKI24614", result.toString());
    }

    @Test
    public void mask_value_with_max_rank() {
        StringBuilder result = pattern.generateUniqueString(Arrays.asList("S", "DU", "500", "20"), secretMng);
        assertNotEquals(maxValue, result.toString());
        assertNotNull(result);
    }

    @Test
    public void mask_value_with_min_rank() {
        StringBuilder result = pattern.generateUniqueString(Arrays.asList("O", "SF", "000", "5"), secretMng);
        assertNotEquals(minValue, result.toString());
        assertNotNull(result);
    }

    @Test
    public void mask_value_out_limits() {
        StringBuilder result = pattern.generateUniqueString(Arrays.asList("U", "KI", "502", "12"), secretMng);
        assertNull(result);
    }

    @Test
    public void ensure_uniqueness() {
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
