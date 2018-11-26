package org.talend.dataquality.datamasking.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

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

public class GenerateUniqueRandomPatternsTest {

    private AbstractGeneratePattern pattern;

    private SecretManager secretMng;

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
        secretMng = new SecretManager();
        secretMng.setKey(new Random(454594).nextInt() % 10000 + 1000);
    }

    @Test
    public void testGenerateUniqueString() {

        StringBuilder result = pattern.generateUniqueString(new ArrayList<String>(Arrays.asList("U", "KI", "453", "12")),
                secretMng);
        assertEquals("USF40818", result.toString());

        // test with padding 0
        result = pattern.generateUniqueString(new ArrayList<String>(Arrays.asList("U", "KI", "123", "12")), secretMng);
        assertEquals("UKI40518", result.toString());
    }

    @Test
    public void testOutLimit() {

        StringBuilder result = pattern.generateUniqueString(new ArrayList<String>(Arrays.asList("U", "KI", "502", "12")),
                secretMng);
        assertNull(result);
    }

    @Test
    public void testGetMaxRank() {
        BigInteger rank = pattern.getNumberToMask(pattern.encodeFields(Arrays.asList("S", "DU", "500", "20")));
        assertEquals(pattern.getLongestWidth().add(BigInteger.valueOf(-1)), rank);
    }

    @Test
    public void testGetMinRank() {
        BigInteger rank = pattern.getNumberToMask(pattern.encodeFields(Arrays.asList("O", "SF", "000", "5")));
        assertEquals(BigInteger.ZERO, rank);
    }

    @Test
    public void testUnique() {
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
