package org.talend.dataquality.datamasking.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

public class GenerateUniqueRandomPatternsTest {

    private GenerateUniqueRandomPatterns pattern;

    @Before
    public void setUp() throws Exception {
        // pattern we want to test
        List<AbstractField> fields = new ArrayList<AbstractField>();
        List<String> enums = new ArrayList<String>(Arrays.asList("O", "P", "G", "U", "M", "S"));
        fields.add(new FieldEnum(enums, 1));
        enums = new ArrayList<String>(Arrays.asList("SF", "KI", "QG", "DU"));
        fields.add(new FieldEnum(enums, 2));
        fields.add(new FieldInterval(0, 500));
        fields.add(new FieldInterval(5, 20));

        pattern = new GenerateUniqueRandomPatterns(fields);
        pattern.setKey(new Random(454594).nextInt() % 10000 + 1000);
    }

    @Test
    public void testGenerateUniqueString() {

        StringBuilder result = pattern.generateUniqueString(new ArrayList<String>(Arrays.asList("U", "KI", "453", "12")));
        assertEquals(result.toString(), "USF40818");

        // test with padding 0
        result = pattern.generateUniqueString(new ArrayList<String>(Arrays.asList("U", "KI", "123", "12")));
        assertEquals(result.toString(), "UKI40518");
    }

    @Test
    public void testOutLimit() {

        StringBuilder result = pattern.generateUniqueString(new ArrayList<String>(Arrays.asList("U", "KI", "502", "12")));
        assertEquals(result, null);

    }

    @Test
    public void testUnique() {
        Set<StringBuilder> uniqueSetTocheck = new HashSet<StringBuilder>();
        for (long i = 0; i < pattern.getFields().get(0).getWidth(); i++) {
            for (long j = 0; j < pattern.getFields().get(1).getWidth(); j++) {
                for (long k = 0; k < pattern.getFields().get(2).getWidth(); k++) {
                    for (long l = 0; l < pattern.getFields().get(3).getWidth(); l++) {
                        StringBuilder uniqueMaskedNumber = pattern.generateUniqueString(new ArrayList<String>(
                                Arrays.asList(pattern.getFields().get(0).decode(i), pattern.getFields().get(1).decode(j),
                                        pattern.getFields().get(2).decode(k), pattern.getFields().get(3).decode(l))));

                        assertFalse(" we found twice the uniqueMaskedNumberList " + uniqueMaskedNumber,
                                uniqueSetTocheck.contains(uniqueMaskedNumber));
                        uniqueSetTocheck.add(uniqueMaskedNumber);
                    }
                }
            }
        }
    }
}
