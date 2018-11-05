package org.talend.dataquality.datamasking.generic;

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

    protected AbstractGeneratePattern pattern;

    protected String minValue;

    protected String maxValue;

    protected List<BigInteger> minFields;

    protected List<BigInteger> maxFields;

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
    public void encode_fields_with_max_value() {
        List<BigInteger> encodedList = pattern.encodeFields(Arrays.asList("S", "DU", "500", "20"));
        assertEquals(maxFields, encodedList);
    }

    @Test
    public void encode_fields_with_min_value() {
        List<BigInteger> encodedList = pattern.encodeFields(Arrays.asList("O", "SF", "000", "5"));
        assertEquals(minFields, encodedList);
    }

    @Test
    public void decode_fields_with_max_value() {
        String decodedList = pattern.decodeFields(maxFields).toString();
        assertEquals(maxValue, decodedList);
    }

    @Test
    public void decode_fields_with_min_value() {
        String decodedList = pattern.decodeFields(minFields).toString();
        assertEquals(minValue, decodedList);
    }

    @Test
    public void compute_rank_of_max_value() {
        BigInteger rank = pattern.getNumberToMask(maxFields);
        assertEquals(pattern.getLongestWidth().add(BigInteger.valueOf(-1)), rank);
    }

    @Test
    public void compute_rank_of_min_value() {
        BigInteger rank = pattern.getNumberToMask(minFields);
        assertEquals(BigInteger.ZERO, rank);
    }

    @Test
    public void get_field_values_from_max_rank() {
        List<BigInteger> fieldList = pattern.getFieldsFromNumber(pattern.longestWidth.add(BigInteger.valueOf(-1)));
        assertEquals(maxFields, fieldList);
    }

    @Test
    public void get_field_values_from_min_rank() {
        List<BigInteger> fieldList = pattern.getFieldsFromNumber(BigInteger.ZERO);
        assertEquals(minFields, fieldList);
    }
}
