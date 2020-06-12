package org.talend.dataquality.statistics.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.talend.dataquality.common.inference.AvroQualityAnalyzer.GLOBAL_QUALITY_PROP_NAME;
import static org.talend.dataquality.statistics.type.AvroDataTypeAnalyzer.DATA_TYPE_AGGREGATE;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

public class AvroDataTypeAnalyzerTest {

    private AvroDataTypeAnalyzer analyzer;

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

    @Before
    public void setUp() throws URISyntaxException, IOException {
        byte[] avsc = Files.readAllBytes(Paths.get(getClass().getResource("/avro/person.avsc").toURI()));
        personSchema = new Schema.Parser().parse(new String(avsc));
        analyzer = new AvroDataTypeAnalyzer();
        analyzer.init(personSchema);
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
        // checkQuality(prop, 0, 0, 0, 0);
    }

    @Test
    public void testGlobalDataType() {
        GenericRecord[] records = loadPersons("alice", "bob", "charlie");

        for (GenericRecord record : records) {
            analyzer.analyze(record);
        }

        Schema result = analyzer.getResult();
        assertNotNull(result);

        List<Map<String, Object>> aggregations =
                (List<Map<String, Object>>) result.getField("birthdate").schema().getObjectProp("dataTypeAggregate");

        assertEquals(3l, aggregations.get(0).get("total"));
        assertEquals(DataTypeEnum.DATE.toString(), aggregations.get(0).get("dataType"));
    }

    @Test
    public void testSimpleFields() throws IOException, URISyntaxException {
        GenericRecord[] records = loadPersons("alice");
        Iterator<IndexedRecord> outRecords = analyzer.analyze(records);

        // Check the output records
        int count = 0;
        while (outRecords.hasNext()) {
            GenericRecord out = (GenericRecord) outRecords.next();
            GenericData.Record firstnameRecord = (GenericData.Record) out.get("firstname");
            DataTypeEnum dataType = (DataTypeEnum) firstnameRecord.get("dataType");
            assertEquals(DataTypeEnum.STRING, dataType);
            count++;
        }
        assertEquals(records.length, count);

        Schema result = analyzer.getResult();
        assertNotNull(result);

        List<Map<String, Object>> aggregation =
                (List<Map<String, Object>>) result.getField("birthdate").schema().getObjectProp("dataTypeAggregate");
        assertEquals(DataTypeEnum.DATE.toString(), aggregation.get(0).get("dataType"));
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
        List<Map<String, Object>> prop =
                (List<Map<String, Object>>) specificZipcodeSchema.getObjectProp(DATA_TYPE_AGGREGATE);
        assertEquals(1l, prop.get(0).get("total"));

        specificZipcodeSchema =
                zipcodeSchema.getTypes().stream().filter(s -> s.getType() == Schema.Type.NULL).findFirst().get();
        prop = (List<Map<String, Object>>) specificZipcodeSchema.getObjectProp(DATA_TYPE_AGGREGATE);
        assertEquals(0, prop.size());

        specificZipcodeSchema =
                zipcodeSchema.getTypes().stream().filter(s -> s.getType() == Schema.Type.RECORD).findFirst().get();
        Schema codeSchema = specificZipcodeSchema.getField("code").schema();
        prop = (List<Map<String, Object>>) codeSchema.getObjectProp(DATA_TYPE_AGGREGATE);
        assertEquals(1l, prop.get(0).get("total"));
    }
}
