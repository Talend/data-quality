package org.talend.dataquality.statistics.quality;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.talend.dataquality.common.inference.AvroQualityAnalyzer.EMPTY_VALUE;
import static org.talend.dataquality.common.inference.AvroQualityAnalyzer.GLOBAL_QUALITY_PROP_NAME;
import static org.talend.dataquality.common.inference.AvroQualityAnalyzer.INVALID_VALUE;
import static org.talend.dataquality.common.inference.AvroQualityAnalyzer.QUALITY_PROP_NAME;
import static org.talend.dataquality.common.inference.AvroQualityAnalyzer.VALID_VALUE;

public class AvroDataTypeQualityAnalyzerTest {

    private AvroDataTypeQualityAnalyzer analyzer;

    private Schema personSchema;

    private DecoderFactory decoderFactory = new DecoderFactory();

    private GenericRecord createFromJson(String jsonRecord) throws IOException {
        Decoder decoder = decoderFactory.jsonDecoder(personSchema, jsonRecord);
        DatumReader<GenericData.Record> reader = new GenericDatumReader<>(personSchema);
        GenericRecord record = reader.read(null, decoder);

        return record;
    }

    private GenericRecord loadPerson(String filename) {
        try {
            byte[] json = Files.readAllBytes(Paths.get(getClass().getResource("/avro/" + filename + ".json").toURI()));
            return createFromJson(new String(json));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private GenericRecord[] loadPersons(String... filenames) {
        return Arrays.asList(filenames).stream().map(filename -> loadPerson(filename)).toArray(GenericRecord[]::new);
    }

    private void checkQuality(Map<String, Long> prop, long expectedValid, long expectedNotValid, long expectedEmpty,
            long expectedTotal) {
        assertEquals(expectedValid, prop.get(Integer.toString(VALID_VALUE)).longValue());
        assertEquals(expectedNotValid, prop.get(Integer.toString(INVALID_VALUE)).longValue());
        assertEquals(expectedEmpty, prop.get(Integer.toString(EMPTY_VALUE)).longValue());
        assertEquals(expectedTotal, prop.get("total").longValue());
    }

    @Before
    public void setUp() throws URISyntaxException, IOException {
        byte[] avsc = Files.readAllBytes(Paths.get(getClass().getResource("/avro/semantic_person.avsc").toURI()));
        Schema schema = new Schema.Parser().parse(new String(avsc));
        analyzer = new AvroDataTypeQualityAnalyzer();
        analyzer.init(schema);

        avsc = Files.readAllBytes(Paths.get(getClass().getResource("/avro/person.avsc").toURI()));
        personSchema = new Schema.Parser().parse(new String(avsc));
    }

    @After
    public void tearDown() {
        analyzer.end();
    }

    @Test
    public void testNull() {
        analyzer.analyze((IndexedRecord) null);

        Schema result = analyzer.getResult();
        assertNotNull(result);

        Map<String, Long> prop = (Map) result.getObjectProp(GLOBAL_QUALITY_PROP_NAME);
        checkQuality(prop, 0, 0, 0, 0);
    }

    @Test
    public void testGlobalQuality() {
        GenericRecord[] records = loadPersons("alice", "bob", "charlie");

        for (GenericRecord record : records) {
            analyzer.analyze(record);
        }

        Schema result = analyzer.getResult();
        assertNotNull(result);

        Map<String, Long> prop = (Map) result.getObjectProp(GLOBAL_QUALITY_PROP_NAME);
        checkQuality(prop, 12, 3, 2, 17);
    }

    @Test
    public void testSimpleFields() throws IOException, URISyntaxException {
        GenericRecord[] records = loadPersons("alice", "bob", "charlie");
        Iterator<IndexedRecord> outRecords = analyzer.analyze(records);

        // Check the output records
        int count = 0;
        while (outRecords.hasNext()) {
            GenericRecord out = (GenericRecord) outRecords.next();
            GenericData.Record firstnameRecord = (GenericData.Record) out.get("firstname");
            int validity = (int) firstnameRecord.get("validity");
            assertEquals(1, validity);
            count++;
        }
        assertEquals(records.length, count);

        Schema result = analyzer.getResult();
        assertNotNull(result);

        Map<String, Long> prop = (Map) result.getField("firstname").schema().getObjectProp(QUALITY_PROP_NAME);
        checkQuality(prop, 3, 0, 0, 3);
    }

    @Test
    public void testUnion() throws IOException, URISyntaxException {
        GenericRecord[] records = loadPersons("alice", "bob", "charlie");
        Iterator<IndexedRecord> outRecords = analyzer.analyze(records);

        Schema result = analyzer.getResult();
        assertNotNull(result);

        Schema zipcodeSchema = result.getField("location").schema().getField("zipcode").schema();

        Schema specificZipcodeSchema =
                zipcodeSchema.getTypes().stream().filter(s -> s.getType() == Schema.Type.STRING).findFirst().get();
        Map<String, Long> prop = (Map) specificZipcodeSchema.getObjectProp(QUALITY_PROP_NAME);
        checkQuality(prop, 1, 0, 0, 1);

        specificZipcodeSchema =
                zipcodeSchema.getTypes().stream().filter(s -> s.getType() == Schema.Type.NULL).findFirst().get();
        prop = (Map) specificZipcodeSchema.getObjectProp(QUALITY_PROP_NAME);
        checkQuality(prop, 0, 0, 1, 1);

        specificZipcodeSchema =
                zipcodeSchema.getTypes().stream().filter(s -> s.getType() == Schema.Type.RECORD).findFirst().get();
        Schema codeSchema = specificZipcodeSchema.getField("code").schema();
        prop = (Map) codeSchema.getObjectProp(QUALITY_PROP_NAME);
        checkQuality(prop, 1, 0, 0, 1);
    }
}
