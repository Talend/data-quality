package org.talend.dataquality.statistics.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.talend.dataquality.common.inference.AvroQualityAnalyzer.GLOBAL_QUALITY_PROP_NAME;
import static org.talend.dataquality.statistics.type.AvroDataTypeDiscoveryAnalyzer.DATA_TYPE_AGGREGATE;
import static org.talend.dataquality.statistics.type.AvroDataTypeDiscoveryAnalyzer.MATCHINGS_FIELD;
import static org.talend.dataquality.statistics.type.DataTypeEnum.DATE;
import static org.talend.dataquality.statistics.type.DataTypeEnum.DOUBLE;
import static org.talend.dataquality.statistics.type.DataTypeEnum.EMPTY;
import static org.talend.dataquality.statistics.type.DataTypeEnum.STRING;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AvroDataTypeDiscoveryAnalyzerTest {

    private AvroDataTypeDiscoveryAnalyzer analyzer;

    private Schema personSchema;

    private DecoderFactory decoderFactory = new DecoderFactory();

    private GenericRecord createFromJson(String jsonRecord) throws IOException {
        Decoder decoder = decoderFactory.jsonDecoder(personSchema, jsonRecord);
        DatumReader<GenericData.Record> reader = new GenericDatumReader<>(personSchema);
        GenericRecord record = reader.read(null, decoder);

        return record;
    }

    public static Pair<Stream<IndexedRecord>, Schema> streamAvroFile(File file) throws IOException {
        DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(file, new GenericDatumReader<>());
        return Pair.of(StreamSupport.stream(dateAvroReader.spliterator(), false).map(c -> c),
                dateAvroReader.getSchema());
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

    private IndexedRecord applySchemaToRecord(IndexedRecord record, Schema schema) {
        GenericRecordBuilder builder = new GenericRecordBuilder(schema);
        for (Schema.Field outField : schema.getFields()) {
            Object outValue = null;
            Schema.Field inField = record.getSchema().getField(outField.name());
            if (inField != null)
                outValue = record.get(inField.pos());
            builder.set(outField, outValue);
        }
        return builder.build();
    }

    @Before
    public void setUp() throws URISyntaxException, IOException {
        byte[] avsc = Files.readAllBytes(Paths.get(getClass().getResource("/avro/person.avsc").toURI()));
        personSchema = new Schema.Parser().parse(new String(avsc));
        analyzer = new AvroDataTypeDiscoveryAnalyzer();
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

        Map<String, Long> prop = (Map<String, Long>) result.getObjectProp(GLOBAL_QUALITY_PROP_NAME);
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

        Map<String, Object> aggregate =
                (Map<String, Object>) result.getField("birthdate").schema().getObjectProp("talend.component.dqType");

        assertEquals(3l, ((List<Map<String, Object>>) aggregate.get(MATCHINGS_FIELD)).get(0).get("total"));
        assertEquals(DataTypeEnum.DATE.toString(),
                ((List<Map<String, Object>>) aggregate.get(MATCHINGS_FIELD)).get(0).get("dataType"));
        assertEquals(aggregate.get(AvroDataTypeDiscoveryAnalyzer.DATA_TYPE_FIELD), DataTypeEnum.DATE.toString());
    }

    @Test
    public void testSimpleFields() throws IOException, URISyntaxException {
        GenericRecord[] persons = loadPersons("alice");
        Stream<IndexedRecord> records = Arrays.stream(persons);
        Iterator<IndexedRecord> outRecords = analyzer.analyze(records).iterator();

        // Check the output records
        int count = 0;
        while (outRecords.hasNext()) {
            GenericRecord out = (GenericRecord) outRecords.next();
            GenericData.Record firstnameRecord = (GenericData.Record) out.get("firstname");
            DataTypeEnum dataType = (DataTypeEnum) firstnameRecord.get("dataType");
            assertEquals(DataTypeEnum.STRING, dataType);
            count++;
        }
        assertEquals(persons.length, count);

        Schema result = analyzer.getResult();
        assertNotNull(result);

        Map<String, Object> aggregate =
                (Map<String, Object>) result.getField("birthdate").schema().getObjectProp("talend.component.dqType");
        assertEquals(DataTypeEnum.DATE.toString(),
                ((List<Map<String, Object>>) aggregate.get(MATCHINGS_FIELD)).get(0).get("dataType"));
    }

    @Test
    public void testRecordsInMaps() {

        AvroDataTypeDiscoveryAnalyzer semanticAnalyzer = new AvroDataTypeDiscoveryAnalyzer();

        Schema nestedSchema_2 = SchemaBuilder.record("record_2_1").fields().requiredString("string_2_0").endRecord();
        Schema nestedSchema_1 = SchemaBuilder
                .record("record_1")
                .fields()
                .requiredInt("int_1_0")
                .name("map_1_0")
                .type()
                .map()
                .values(nestedSchema_2)
                .noDefault()
                .endRecord();
        Schema schema = SchemaBuilder
                .record("record")
                .fields()
                .name("map_0")
                .type()
                .map()
                .values(nestedSchema_1)
                .noDefault()
                .endRecord();

        GenericRecord nestedRecord_2_1 = new GenericRecordBuilder(nestedSchema_2).set("string_2_0", "France").build();
        GenericRecord nestedRecord_2_2 = new GenericRecordBuilder(nestedSchema_2).set("string_2_0", "Germany").build();

        List<IndexedRecord> list_1 = Stream.of(nestedRecord_2_1).collect(Collectors.toList());
        GenericRecord nestedRecord_1_1 = new GenericRecordBuilder(nestedSchema_1)
                .set("int_1_0", 1)
                .set("map_1_0",
                        IntStream.range(0, list_1.size()).boxed().collect(Collectors.toMap(i -> i, i -> list_1.get(i))))
                .build();
        List<IndexedRecord> list_2 = Stream.of(nestedRecord_2_2).collect(Collectors.toList());
        GenericRecord nestedRecord_1_2 = new GenericRecordBuilder(nestedSchema_1)
                .set("int_1_0", 2)
                .set("map_1_0",
                        IntStream.range(0, list_2.size()).boxed().collect(Collectors.toMap(i -> i, i -> list_2.get(i))))
                .build();
        List<IndexedRecord> list = Stream.of(nestedRecord_1_1, nestedRecord_1_2).collect(Collectors.toList());
        IndexedRecord record = new GenericRecordBuilder(schema)
                .set("map_0",
                        IntStream.range(0, list.size()).boxed().collect(Collectors.toMap(i -> i, i -> list.get(i))))
                .build();

        semanticAnalyzer.init(schema);
        semanticAnalyzer.analyze(record);
        Schema result = semanticAnalyzer.getResult();

        assertNotNull(result);

        assertNotNull(result.getField("map_0").schema().getValueType().getField("int_1_0").schema().getObjectProp(
                "talend.component.dqType"));

        assertNotNull(result
                .getField("map_0")
                .schema()
                .getValueType()
                .getField("map_1_0")
                .schema()
                .getValueType()
                .getField("string_2_0")
                .schema()
                .getObjectProp("talend.component.dqType"));
    }

    @Test
    public void testSchemaAlreadyEnriched() {

        AvroDataTypeDiscoveryAnalyzer semanticAnalyzer = new AvroDataTypeDiscoveryAnalyzer();

        Schema schema = SchemaBuilder.record("record").fields().name("int1").type().intType().noDefault().endRecord();

        GenericRecord record1 = new GenericRecordBuilder(schema).set("int1", 1).build();
        GenericRecord record2 = new GenericRecordBuilder(schema).set("int1", 2).build();
        GenericRecord record3 = new GenericRecordBuilder(schema).set("int1", 3).build();

        Stream<IndexedRecord> records1 = Stream.of(record1, record2, record3);

        semanticAnalyzer.init(schema);
        semanticAnalyzer.analyze(records1).collect(Collectors.toList());
        Schema result1 = semanticAnalyzer.getResult();

        record1 = (GenericRecord) applySchemaToRecord(record1, result1);
        record3 = (GenericRecord) applySchemaToRecord(record3, result1);

        Stream<IndexedRecord> records2 = Stream.of(record1, record3);

        semanticAnalyzer.init(result1);
        semanticAnalyzer.analyze(records2).collect(Collectors.toList());
        Schema result2 = semanticAnalyzer.getResult();

        Map<String, Object> dqTypeProps1 =
                (Map<String, Object>) result1.getField("int1").schema().getObjectProp("talend.component.dqType");
        List<Object> matchings1 = (ArrayList<Object>) dqTypeProps1.get("matchings");
        assertNotNull(result1);
        assertNotNull(result1.getField("int1").schema().getObjectProp("talend.component.dqType"));
        assertEquals(3L, ((Map<String, Object>) matchings1.get(0)).get("total"));

        Map<String, Object> dqTypeProps2 =
                (Map<String, Object>) result2.getField("int1").schema().getObjectProp("talend.component.dqType");
        List<Object> matchings2 = (ArrayList<Object>) dqTypeProps2.get("matchings");
        assertNotNull(result2);
        assertNotNull(result2.getField("int1").schema().getObjectProp("talend.component.dqType"));
        assertEquals(2L, ((Map<String, Object>) matchings2.get(0)).get("total"));
    }

    @Test
    public void testUnion() throws IOException, URISyntaxException {
        GenericRecord[] records = loadPersons("alice", "bob", "charlie");
        List<IndexedRecord> outRecords = analyzer.analyze(Arrays.stream(records)).collect(Collectors.toList());

        Schema result = analyzer.getResult();
        assertNotNull(result);

        Schema zipcodeSchema = result.getField("location").schema().getField("zipcode").schema();

        Schema specificZipcodeSchema =
                zipcodeSchema.getTypes().stream().filter(s -> s.getType() == Schema.Type.STRING).findFirst().get();
        Map<String, Object> prop = (Map<String, Object>) specificZipcodeSchema.getObjectProp(DATA_TYPE_AGGREGATE);
        assertEquals(1l, ((List<Map<String, Object>>) prop.get(MATCHINGS_FIELD)).get(0).get("total"));

        specificZipcodeSchema =
                zipcodeSchema.getTypes().stream().filter(s -> s.getType() == Schema.Type.NULL).findFirst().get();
        prop = (Map<String, Object>) specificZipcodeSchema.getObjectProp(DATA_TYPE_AGGREGATE);
        assertEquals(1, ((List<Map<String, Object>>) prop.get(MATCHINGS_FIELD)).size());

        specificZipcodeSchema =
                zipcodeSchema.getTypes().stream().filter(s -> s.getType() == Schema.Type.RECORD).findFirst().get();
        Schema codeSchema = specificZipcodeSchema.getField("code").schema();
        prop = (Map<String, Object>) codeSchema.getObjectProp(DATA_TYPE_AGGREGATE);
        assertEquals(1l, ((List<Map<String, Object>>) prop.get(MATCHINGS_FIELD)).get(0).get("total"));
    }

    @Test
    public void testSemanticSchemaNotMatchingRecord() {
        try {
            String path = AvroDataTypeDiscoveryAnalyzerTest.class.getResource("../sample/date.avro").getPath();
            File dateAvroFile = new File(path);
            DataFileReader<GenericRecord> dateAvroReader =
                    new DataFileReader<>(dateAvroFile, new GenericDatumReader<>());

            analyzer.init(dateAvroReader.getSchema());
            analyzer.analyze(dateAvroReader.next());
            Schema result = analyzer.getResult();

            assertNotNull(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSupportForDateLogicalTypes() {
        try {
            String path = AvroDataTypeDiscoveryAnalyzerTest.class.getResource("../sample/date.avro").getPath();
            Pair<Stream<IndexedRecord>, Schema> pair = streamAvroFile(new File(path));
            analyzer.init(pair.getRight());
            analyzer.analyze(pair.getLeft()).collect(Collectors.toList());
            Schema result = analyzer.getResult();
            // All field with "logicalType" should be detected as DATE.
            // LogicalTypes.Date, LogicalTypes.TimeMicros, LogicalTypes.TimeMillis, LogicalTypes.TimestampMicros, LogicalTypes.TimestampMillis
            Schema expectedSchema = new Schema.Parser().parse(
                    "{\"type\": \"record\",\"name\": \"dateTimeLogical\",\"namespace\": \"org.talend\",\"fields\": [{\"name\": \"name\",\"type\": {\"type\": \"string\",\"talend.component.dqType\": {\"matchings\": [{\"total\": 3,\"dataType\": \"STRING\"}],\"dataType\": \"STRING\"}}},{\"name\": \"date\",\"type\": {\"type\": \"int\",\"logicalType\": \"date\",\"talend.component.dqType\": {\"matchings\": [{\"total\": 3,\"dataType\": \"DATE\"}],\"dataType\": \"DATE\"}}},{\"name\": \"timemillis\",\"type\": {\"type\": \"int\",\"logicalType\": \"time-millis\",\"talend.component.dqType\": {\"matchings\": [{\"total\": 3,\"dataType\": \"DATE\"}],\"dataType\": \"DATE\"}}},{\"name\": \"timemicros\",\"type\": {\"type\": \"long\",\"logicalType\": \"time-micros\",\"talend.component.dqType\": {\"matchings\": [{\"total\": 3,\"dataType\": \"DATE\"}],\"dataType\": \"DATE\"}}},{\"name\": \"timestampmillis\",\"type\": {\"type\": \"long\",\"logicalType\": \"timestamp-millis\",\"talend.component.dqType\": {\"matchings\": [{\"total\": 3,\"dataType\": \"DATE\"}],\"dataType\": \"DATE\"}}},{\"name\": \"timestampmicros\",\"type\": {\"type\": \"long\",\"logicalType\": \"timestamp-micros\",\"talend.component.dqType\": {\"matchings\": [{\"total\": 3,\"dataType\": \"DATE\"}],\"dataType\": \"DATE\"}}}]}");
            assertEquals(expectedSchema.toString(), result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAvroDataTypeAnalyzerOnSwitch() {
        try {
            String path = AvroDataTypeDiscoveryAnalyzerTest.class.getResource("../sample/Switch.avro").getPath();
            Pair<Stream<IndexedRecord>, Schema> pair = streamAvroFile(new File(path));
            analyzer.init(pair.getRight());
            List<IndexedRecord> results = analyzer.analyze(pair.getLeft()).collect(Collectors.toList());
            Schema result = analyzer.getResult();
            assertNotNull(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAvroDataTypeAnalyzerOnNoFancy() {
        try {
            String path = AvroDataTypeDiscoveryAnalyzerTest.class
                    .getResource("../sample/no-fancy-structures-10.avro")
                    .getPath();
            File fileEntry = new File(path);
            DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
            analyzer.init(dateAvroReader.getSchema());

            dateAvroReader.forEach(analyzer::analyze);

            Schema result = analyzer.getResult();
            assertNotNull(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAvroDataTypeAnalyzerOnExample2() {
        try {
            String path = AvroDataTypeDiscoveryAnalyzerTest.class.getResource("../sample/example2.avro").getPath();
            File fileEntry = new File(path);
            DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
            analyzer.init(dateAvroReader.getSchema());

            dateAvroReader.forEach(analyzer::analyze);

            Schema result = analyzer.getResult();
            HashMap dqTypes = (HashMap) result
                    .getField("midleName")
                    .schema()
                    .getTypes()
                    .get(0)
                    .getObjectProp(DATA_TYPE_AGGREGATE);
            List matchings = (List) dqTypes.get(MATCHINGS_FIELD);
            HashMap matchingProperties = (HashMap) matchings.get(0);
            assertEquals(496l, matchingProperties.get("total"));
            assertNotNull(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAvroDataTypeAnalyzerOn96() {
        try {
            String path = AvroDataTypeDiscoveryAnalyzerTest.class.getResource("../sample/96.avro").getPath();
            File fileEntry = new File(path);
            DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
            analyzer.init(dateAvroReader.getSchema());
            dateAvroReader.forEach(analyzer::analyze);
            Schema result = analyzer.getResult();
            Map<String, Object> dataTypeAggregate = (Map<String, Object>) result
                    .getField("friends")
                    .schema()
                    .getElementType()
                    .getField("name")
                    .schema()
                    .getObjectProp(DATA_TYPE_AGGREGATE);
            assertNotNull(result);
            assertNotNull(dataTypeAggregate);
            assertEquals(dataTypeAggregate.get(AvroDataTypeDiscoveryAnalyzer.DATA_TYPE_FIELD), "STRING");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInputSemanticSchemaWithDataTypeAggregate() {
        try {
            String path = AvroDataTypeDiscoveryAnalyzerTest.class.getResource("../sample/96.avro").getPath();
            File fileEntry = new File(path);
            DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
            analyzer.init(dateAvroReader.getSchema());
            dateAvroReader.forEach(analyzer::analyze);
            Schema result = analyzer.getResult();
            analyzer.init(result);
            dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
            dateAvroReader.forEach(analyzer::analyze);
            result = analyzer.getResult();
            assertNotNull(result.getField("friends").schema().getElementType().getField("name").schema().getObjectProp(
                    DATA_TYPE_AGGREGATE));
            assertNotNull(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAvroDataTypeAnalyzerOnBigBusiness() {
        try {
            String path = AvroDataTypeDiscoveryAnalyzerTest.class.getResource("../sample/big_business.avro").getPath();
            File fileEntry = new File(path);
            DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
            analyzer.init(dateAvroReader.getSchema());
            dateAvroReader.forEach(analyzer::analyze);
            Schema result = analyzer.getResult();

            Map<String, Object> aggregate = (Map<String, Object>) result
                    .getField("business_id")
                    .schema()
                    .getTypes()
                    .get(1)
                    .getObjectProp(DATA_TYPE_AGGREGATE);
            assertEquals(1000l, ((List<Map<String, Object>>) aggregate.get(MATCHINGS_FIELD)).get(0).get("total"));

            aggregate = (Map<String, Object>) result
                    .getField("business")
                    .schema()
                    .getTypes()
                    .get(1)
                    .getField("location")
                    .schema()
                    .getTypes()
                    .get(1)
                    .getField("taxReturnsFiled")
                    .schema()
                    .getTypes()
                    .get(1)
                    .getObjectProp(DATA_TYPE_AGGREGATE);
            List<Map<String, Object>> matchings = ((List<Map<String, Object>>) aggregate.get(MATCHINGS_FIELD));
            assertEquals(682l, matchings.get(0).get("total"));
            assertEquals("INTEGER", matchings.get(0).get("dataType"));
            assertEquals(318l, matchings.get(1).get("total"));
            assertEquals("EMPTY", matchings.get(1).get("dataType"));
            assertNotNull(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAvroDataTypeAnalyzerOnComplexSchemas() {
        try {
            String path = AvroDataTypeDiscoveryAnalyzerTest.class.getResource("../sample/complex").getPath();
            File primitiveFolder = new File(path);
            for (final File fileEntry : Objects.requireNonNull(primitiveFolder.listFiles())) {
                DataFileReader<GenericRecord> dateAvroReader =
                        new DataFileReader<>(fileEntry, new GenericDatumReader<>());
                analyzer.init(dateAvroReader.getSchema());

                dateAvroReader.forEach(analyzer::analyze);

                Schema result = analyzer.getResult();
                assertNotNull(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAvroDataTypeAnalyzerOnPrimitiveSchemas() {
        try {
            String path = AvroDataTypeDiscoveryAnalyzerTest.class.getResource("../sample/primitive").getPath();
            File primitiveFolder = new File(path);
            for (final File fileEntry : Objects.requireNonNull(primitiveFolder.listFiles())) {
                DataFileReader<GenericRecord> dateAvroReader =
                        new DataFileReader<>(fileEntry, new GenericDatumReader<>());
                analyzer.init(dateAvroReader.getSchema());

                dateAvroReader.forEach(analyzer::analyze);

                Schema result = analyzer.getResult();
                assertNotNull(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAvroDataTypeAnalyzerOnStructureSchemas() {
        try {
            String path = AvroDataTypeDiscoveryAnalyzerTest.class.getResource("../sample/structure").getPath();
            File primitiveFolder = new File(path);
            for (final File fileEntry : Objects.requireNonNull(primitiveFolder.listFiles())) {
                DataFileReader<GenericRecord> dateAvroReader =
                        new DataFileReader<>(fileEntry, new GenericDatumReader<>());
                analyzer.init(dateAvroReader.getSchema());

                dateAvroReader.forEach(analyzer::analyze);

                Schema result = analyzer.getResult();
                assertNotNull(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Ignore
    public void testAvroDataTypeAnalyzerOnBigSamples() {
        try {
            String path = AvroDataTypeDiscoveryAnalyzerTest.class.getResource("../sample").getPath();
            File primitiveFolder = new File(path);
            for (final File fileEntry : Objects.requireNonNull(primitiveFolder.listFiles())) {
                if (fileEntry.isFile()) {
                    DataFileReader<GenericRecord> dateAvroReader =
                            new DataFileReader<>(fileEntry, new GenericDatumReader<>());
                    analyzer.init(dateAvroReader.getSchema());

                    dateAvroReader.forEach(analyzer::analyze);

                    Schema result = analyzer.getResult();
                    assertNotNull(result);
                    //                    analyzer.init(result);
                    //                    dateAvroReader =
                    //                            new DataFileReader<>(fileEntry, new GenericDatumReader<>(result));
                    //                    dateAvroReader.forEach(analyzer::analyze);
                    //                    Schema result1 = analyzer.getResult();
                    //                    assertEquals(result, result1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEntryComparator() {

        Map<DataTypeEnum, Long> entries = new HashMap<>();
        entries.put(STRING, 4L);
        entries.put(EMPTY, 3L);
        entries.put(DOUBLE, 7L);
        entries.put(DATE, 6L);

        ArrayList<Map.Entry<DataTypeEnum, Long>> shuffledEntries = new ArrayList<>(entries.entrySet());
        Collections.shuffle(shuffledEntries);

        assertEquals(
                Arrays.asList(new AbstractMap.SimpleEntry<>(EMPTY, 3L), new AbstractMap.SimpleEntry<>(STRING, 4L),
                        new AbstractMap.SimpleEntry<>(DATE, 6L), new AbstractMap.SimpleEntry<>(DOUBLE, 7L)),
                shuffledEntries
                        .stream()
                        .sorted(AvroDataTypeDiscoveryAnalyzer.entryComparator)
                        .collect(Collectors.toList()));

        List<AbstractMap.SimpleEntry<DataTypeEnum, Long>> expectedEntries =
                Arrays.asList(new AbstractMap.SimpleEntry<>(EMPTY, 3L), new AbstractMap.SimpleEntry<>(EMPTY, 4L),
                        new AbstractMap.SimpleEntry<>(STRING, 4L), new AbstractMap.SimpleEntry<>(DATE, 6L),
                        new AbstractMap.SimpleEntry<>(DOUBLE, 6L), new AbstractMap.SimpleEntry<>(DOUBLE, 7L));

        shuffledEntries = new ArrayList<>(expectedEntries);
        Collections.shuffle(shuffledEntries);

        assertEquals(expectedEntries.get(0),
                shuffledEntries.stream().min(AvroDataTypeDiscoveryAnalyzer.entryComparator).get());
        assertEquals(expectedEntries.get(5),
                shuffledEntries.stream().max(AvroDataTypeDiscoveryAnalyzer.entryComparator).get());
    }
}
