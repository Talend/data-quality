package org.talend.dataquality.common.util;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

public class AvroUtilsTest {

    private Schema createSimpleRecordSchema() {
        SchemaBuilder.RecordBuilder<Schema> recordBuilder =
                SchemaBuilder.record("test").namespace("org.talend.dataquality");
        SchemaBuilder.FieldAssembler<Schema> fieldAssembler = recordBuilder.fields();

        Schema stringSchema = Schema.create(Schema.Type.STRING);
        stringSchema.addProp("quality", "not so bad");

        fieldAssembler.name("firstname").type(stringSchema).noDefault();
        fieldAssembler.name("lastname").type(stringSchema).noDefault();

        return fieldAssembler.endRecord();
    }

    private Schema createComplexRecordSchema() {
        SchemaBuilder.RecordBuilder<Schema> recordBuilder =
                SchemaBuilder.record("test").namespace("org.talend.dataquality");
        SchemaBuilder.FieldAssembler<Schema> fieldAssembler = recordBuilder.fields();

        Schema stringSchema = Schema.create(Schema.Type.STRING);
        stringSchema.addProp("quality", "not so bad");
        Schema intSchema = Schema.create(Schema.Type.INT);
        intSchema.addProp("quality", "not so bad");
        Schema unionSchema = SchemaBuilder.unionOf().type(stringSchema).and().type(intSchema).endUnion();

        Schema emailsSchema = Schema.createArray(stringSchema);

        Schema locationSchema =
                SchemaBuilder.record("location").fields().name("country").type(stringSchema).noDefault().endRecord();

        fieldAssembler.name("emails").type(emailsSchema).noDefault();
        fieldAssembler.name("location").type(locationSchema).noDefault();
        fieldAssembler.name("age").type(unionSchema).noDefault();

        return fieldAssembler.endRecord();
    }

    @Test
    public void testExtractNull() {
        Map<String, Object> props = AvroUtils.extractProperties(null, "");
        assertTrue(props.size() == 0);

        props = AvroUtils.extractProperties(Schema.create(Schema.Type.STRING), "");
        assertTrue(props.size() == 0);
    }

    @Test
    public void testExtractSimpleRecord() {
        Schema schema = createSimpleRecordSchema();
        Map<String, Object> props = AvroUtils.extractProperties(schema, "quality");

        assertEquals(2, props.size());
        assertEquals("not so bad", props.get("firstname"));
        assertEquals("not so bad", props.get("lastname"));
    }

    @Test
    public void testExtractComplexRecord() {
        Schema schema = createComplexRecordSchema();
        Map<String, Object> props = AvroUtils.extractProperties(schema, "quality");

        assertEquals(4, props.size());
        assertEquals("not so bad", props.get("emails"));
        assertEquals("not so bad", props.get("location.country"));
        assertEquals("not so bad", props.get("age.string"));
        assertEquals("not so bad", props.get("age.int"));
    }

    @Test
    public void testDereferencingOfSwitch() throws IOException {
        String path = AvroUtilsTest.class.getResource("./Switch.avro").getPath();
        File fileEntry = new File(path);
        DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
        Schema schemaWithRefType = dateAvroReader.getSchema();
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schemaWithRefType);
        assertNotEquals(schemaWithRefType, schemaWithoutRefTypes);

        // We should be able to read the file using the dereferenced schema.
        dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>(schemaWithoutRefTypes));
        assertNotNull(dateAvroReader.iterator().next());
    }

    @Test
    public void testDereferencingIsIdempotent() throws IOException {
        String path = AvroUtilsTest.class.getResource("./Switch.avro").getPath();
        File fileEntry = new File(path);
        DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
        Schema schemaWithRefType = dateAvroReader.getSchema();
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schemaWithRefType);
        assertEquals(schemaWithoutRefTypes, AvroUtils.dereferencing(schemaWithoutRefTypes));
    }

    @Test
    public void testDereferencingIsIdentityForSchemaWithoutRef() {
        Schema schemaWithoutRefTypes = new Schema.Parser().parse(
                "{\"type\": \"record\",\"name\": \"base\",\"namespace\": \"tdc.qa\",\"fields\": [{ \"name\": \"string1\", \"type\": \"string\" },{ \"name\": \"null1\", \"type\": \"null\" },{ \"name\": \"boolean1\", \"type\": \"boolean\" },{ \"name\": \"int1\", \"type\": \"int\" },{ \"name\": \"long1\", \"type\": \"long\" },{ \"name\": \"float1\", \"type\": \"float\" },{ \"name\": \"double1\", \"type\": \"double\" },{ \"name\": \"bytes1\", \"type\": \"bytes\" },{\"name\": \"enum1\",\"type\": {\"type\": \"enum\",\"name\": \"enum11\",\"symbols\": [\"One\", \"Two\", \"Three\", \"For\"]}},{ \"name\": \"array1\", \"type\": { \"type\": \"array\", \"items\": \"string\" } },{ \"name\": \"map1\", \"type\": { \"type\": \"map\", \"values\": \"long\" } },{ \"name\": \"union1\", \"type\": [\"null\", \"string\", \"boolean\"] },{\"name\": \"fixed1\",\"type\": { \"type\": \"fixed\", \"name\": \"fixed\", \"size\": 15 }}]}");
        assertEquals(schemaWithoutRefTypes, AvroUtils.dereferencing(schemaWithoutRefTypes));
    }

    @Test
    public void testDereferencingOfnoFancy() throws IOException {
        String path = AvroUtilsTest.class.getResource("./no-fancy-structures-10.avro").getPath();
        File fileEntry = new File(path);
        DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
        Schema schemaWithRefType = dateAvroReader.getSchema();
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schemaWithRefType);
        assertNotEquals(schemaWithRefType, schemaWithoutRefTypes);

        // We should be able to read the file using the dereferenced schema.
        dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>(schemaWithoutRefTypes));
        assertNotNull(dateAvroReader.iterator().next());
    }

    @Test
    public void testDereferencingOfUnionOfComplexRefType() throws IOException {
        String path = AvroUtilsTest.class.getResource("./UnionOfComplexRefType.avro").getPath();
        File fileEntry = new File(path);
        DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
        Schema schemaWithRefType = dateAvroReader.getSchema();
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schemaWithRefType);
        assertNotEquals(schemaWithRefType, schemaWithoutRefTypes);

        // We should be able to read the file using the dereferenced schema.
        dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>(schemaWithoutRefTypes));
        assertNotNull(dateAvroReader.iterator().next());
    }

    @Test
    public void testDereferencingOfUnionOfMapArrayRefType() throws IOException {
        String path = AvroUtilsTest.class.getResource("./UnionOfMapArrayRefType.avro").getPath();
        File fileEntry = new File(path);
        DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
        Schema schemaWithRefType = dateAvroReader.getSchema();
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schemaWithRefType);
        assertNotEquals(schemaWithRefType, schemaWithoutRefTypes);

        // We should be able to read the file using the dereferenced schema.
        dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>(schemaWithoutRefTypes));
        assertNotNull(dateAvroReader.iterator().next());
    }

    @Test
    public void testDereferencingOfExample2() throws IOException {
        String path = AvroUtilsTest.class.getResource("./example2.avro").getPath();
        File fileEntry = new File(path);
        DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
        Schema schemaWithRefType = dateAvroReader.getSchema();
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schemaWithRefType);
        assertNotEquals(schemaWithRefType, schemaWithoutRefTypes);

        // We should be able to read the file using the dereferenced schema.
        dateAvroReader = new DataFileReader<>(fileEntry, new GenericDatumReader<>(schemaWithoutRefTypes));
        assertNotNull(dateAvroReader.iterator().next());

        assertEquals(
                "{\"type\":\"record\",\"name\":\"Person\",\"namespace\":\"experiment.sample\",\"fields\":[{\"name\":\"firstName\",\"type\":\"string\"},{\"name\":\"midleName\",\"type\":[\"null\",\"string\"]},{\"name\":\"lastName\",\"type\":\"string\"},{\"name\":\"homeAddress\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"Address\",\"namespace\":\"experiment.sample.a\",\"fields\":[{\"name\":\"line\",\"type\":\"string\"},{\"name\":\"postalCode\",\"type\":\"string\"},{\"name\":\"city\",\"type\":\"string\"}]}]},{\"name\":\"companyAddress\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"Address\",\"namespace\":\"experiment.sample.b\",\"fields\":[{\"name\":\"line\",\"type\":\"string\"},{\"name\":\"postalCode\",\"type\":\"string\"},{\"name\":\"city\",\"type\":\"string\"}]}]}]}",
                schemaWithoutRefTypes.toString());
    }

    @Test
    public void testDereferencingArray() throws URISyntaxException, IOException {
        byte[] avsc = Files.readAllBytes(Paths.get(getClass().getResource("./person.avsc").toURI()));
        Schema schema = new Schema.Parser().parse(new String(avsc));
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schema);
        assertNotEquals(schema, schemaWithoutRefTypes);
    }

    @Test
    public void testDereferencingoneLevelComplex() throws URISyntaxException, IOException {
        byte[] avsc = Files.readAllBytes(Paths.get(getClass().getResource("./oneLevelComplex.avsc").toURI()));
        Schema schema = new Schema.Parser().parse(new String(avsc));
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schema);
        assertEquals(schema, schemaWithoutRefTypes);
    }

    @Test
    public void testDereferencingMultiLevelComplex() throws URISyntaxException, IOException {
        byte[] avsc = Files.readAllBytes(Paths.get(getClass().getResource("./multiLevelComplex.avsc").toURI()));
        Schema schema = new Schema.Parser().parse(new String(avsc));
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schema);
        assertNotEquals(schema, schemaWithoutRefTypes);
    }

    @Test
    public void testDereferencingUnionOfComplex() throws URISyntaxException, IOException {
        byte[] avsc = Files.readAllBytes(Paths.get(getClass().getResource("./unionOfComplex.avsc").toURI()));
        Schema schema = new Schema.Parser().parse(new String(avsc));
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schema);
        assertNotEquals(schema, schemaWithoutRefTypes);
    }

    @Test
    public void testCleanSchemaSimplePrimitive() throws URISyntaxException, IOException {
        Schema schema = SchemaBuilder
                .record("record")
                .fields()
                .name("int1")
                .type()
                .intBuilder()
                .prop("prop1", "value1")
                .prop("prop2", "value2")
                .prop("prop3", "value3")
                .endInt()
                .intDefault(2)
                .endRecord();
        Schema cleanSchema1 = AvroUtils.cleanSchema(schema, Collections.emptyList());
        Schema cleanSchema2 = AvroUtils.cleanSchema(schema, Collections.singletonList("prop2"));
        Schema cleanSchema3 = AvroUtils.cleanSchema(schema, Arrays.asList("prop1", "prop3"));
        assertEquals(schema, cleanSchema1);
        assertNull(cleanSchema2.getField("int1").schema().getObjectProp("prop2"));
        assertEquals(2, cleanSchema2.getField("int1").schema().getObjectProps().size());
        assertNull(cleanSchema3.getField("int1").schema().getObjectProp("prop1"));
        assertNull(cleanSchema3.getField("int1").schema().getObjectProp("prop3"));
        assertEquals(1, cleanSchema3.getField("int1").schema().getObjectProps().size());

        schema = SchemaBuilder
                .record("record")
                .fields()
                .name("int1")
                .type()
                .optional()
                .intBuilder()
                .prop("prop1", "value1")
                .prop("prop2", "value2")
                .prop("prop3", "value3")
                .endInt()
                .endRecord();
        cleanSchema1 = AvroUtils.cleanSchema(schema, Collections.emptyList());
        cleanSchema2 = AvroUtils.cleanSchema(schema, Collections.singletonList("prop2"));
        cleanSchema3 = AvroUtils.cleanSchema(schema, Arrays.asList("prop1", "prop3"));
        assertEquals(schema, cleanSchema1);
        assertNull(cleanSchema2.getField("int1").schema().getTypes().get(1).getObjectProp("prop2"));
        assertEquals(2, cleanSchema2.getField("int1").schema().getTypes().get(1).getObjectProps().size());
        assertNull(cleanSchema3.getField("int1").schema().getTypes().get(1).getObjectProp("prop1"));
        assertNull(cleanSchema3.getField("int1").schema().getTypes().get(1).getObjectProp("prop3"));
        assertEquals(1, cleanSchema3.getField("int1").schema().getTypes().get(1).getObjectProps().size());
    }

    @Test
    public void testCleanSchemaEnum() throws URISyntaxException, IOException {
        Schema schema = SchemaBuilder
                .record("record")
                .fields()
                .name("enum1")
                .type()
                .enumeration("enum1")
                .prop("prop1", "value1")
                .prop("prop2", "value2")
                .prop("prop3", "value3")
                .symbols("A", "B", "C")
                .enumDefault("B")
                .endRecord();
        Schema cleanSchema1 = AvroUtils.cleanSchema(schema, Collections.emptyList());
        Schema cleanSchema2 = AvroUtils.cleanSchema(schema, Collections.singletonList("prop2"));
        Schema cleanSchema3 = AvroUtils.cleanSchema(schema, Arrays.asList("prop1", "prop3"));

        assertEquals(schema, cleanSchema1);
        assertNull(cleanSchema2.getField("enum1").schema().getObjectProp("prop2"));
        assertEquals(2, cleanSchema2.getField("enum1").schema().getObjectProps().size());
        assertNull(cleanSchema3.getField("enum1").schema().getObjectProp("prop1"));
        assertNull(cleanSchema3.getField("enum1").schema().getObjectProp("prop3"));
        assertEquals(1, cleanSchema3.getField("enum1").schema().getObjectProps().size());

        schema = SchemaBuilder
                .record("record")
                .fields()
                .name("enum1")
                .type()
                .optional()
                .enumeration("enum1")
                .prop("prop1", "value1")
                .prop("prop2", "value2")
                .prop("prop3", "value3")
                .symbols("A", "B", "C")
                .endRecord();
        cleanSchema1 = AvroUtils.cleanSchema(schema, Collections.emptyList());
        cleanSchema2 = AvroUtils.cleanSchema(schema, Collections.singletonList("prop2"));
        cleanSchema3 = AvroUtils.cleanSchema(schema, Arrays.asList("prop1", "prop3"));

        assertEquals(schema, cleanSchema1);
        assertNull(cleanSchema2.getField("enum1").schema().getTypes().get(1).getObjectProp("prop2"));
        assertEquals(2, cleanSchema2.getField("enum1").schema().getTypes().get(1).getObjectProps().size());
        assertNull(cleanSchema3.getField("enum1").schema().getTypes().get(1).getObjectProp("prop1"));
        assertNull(cleanSchema3.getField("enum1").schema().getTypes().get(1).getObjectProp("prop3"));
        assertEquals(1, cleanSchema3.getField("enum1").schema().getTypes().get(1).getObjectProps().size());
    }

    @Test
    public void testCleanSchemaFixed() throws URISyntaxException, IOException {
        Schema schema = SchemaBuilder
                .record("record")
                .fields()
                .name("fixed1")
                .type()
                .fixed("fixed1")
                .prop("prop1", "value1")
                .prop("prop2", "value2")
                .prop("prop3", "value3")
                .size(3)
                .fixedDefault("ABC")
                .endRecord();
        Schema cleanSchema1 = AvroUtils.cleanSchema(schema, Collections.emptyList());
        Schema cleanSchema2 = AvroUtils.cleanSchema(schema, Collections.singletonList("prop2"));
        Schema cleanSchema3 = AvroUtils.cleanSchema(schema, Arrays.asList("prop1", "prop3"));

        assertEquals(schema, cleanSchema1);
        assertNull(cleanSchema2.getField("fixed1").schema().getObjectProp("prop2"));
        assertEquals(2, cleanSchema2.getField("fixed1").schema().getObjectProps().size());
        assertNull(cleanSchema3.getField("fixed1").schema().getObjectProp("prop1"));
        assertNull(cleanSchema3.getField("fixed1").schema().getObjectProp("prop3"));
        assertEquals(1, cleanSchema3.getField("fixed1").schema().getObjectProps().size());

        schema = SchemaBuilder
                .record("record")
                .fields()
                .name("fixed1")
                .type()
                .optional()
                .fixed("fixed1")
                .prop("prop1", "value1")
                .prop("prop2", "value2")
                .prop("prop3", "value3")
                .size(3)
                .endRecord();
        cleanSchema1 = AvroUtils.cleanSchema(schema, Collections.emptyList());
        cleanSchema2 = AvroUtils.cleanSchema(schema, Collections.singletonList("prop2"));
        cleanSchema3 = AvroUtils.cleanSchema(schema, Arrays.asList("prop1", "prop3"));

        assertEquals(schema, cleanSchema1);
        assertNull(cleanSchema2.getField("fixed1").schema().getTypes().get(1).getObjectProp("prop2"));
        assertEquals(2, cleanSchema2.getField("fixed1").schema().getTypes().get(1).getObjectProps().size());
        assertNull(cleanSchema3.getField("fixed1").schema().getTypes().get(1).getObjectProp("prop1"));
        assertNull(cleanSchema3.getField("fixed1").schema().getTypes().get(1).getObjectProp("prop3"));
        assertEquals(1, cleanSchema3.getField("fixed1").schema().getTypes().get(1).getObjectProps().size());
    }

    @Test
    public void testCleanSchemaMap() throws URISyntaxException, IOException {
        Schema schema = SchemaBuilder
                .record("record")
                .fields()
                .name("map1")
                .type()
                .map()
                .prop("prop1", "value1")
                .prop("prop2", "value2")
                .prop("prop3", "value3")
                .values(SchemaBuilder.builder().intType())
                .mapDefault(new HashMap<>())
                .endRecord();
        Schema cleanSchema1 = AvroUtils.cleanSchema(schema, Collections.emptyList());
        Schema cleanSchema2 = AvroUtils.cleanSchema(schema, Collections.singletonList("prop2"));
        Schema cleanSchema3 = AvroUtils.cleanSchema(schema, Arrays.asList("prop1", "prop3"));

        assertEquals(schema, cleanSchema1);
        assertNull(cleanSchema2.getField("map1").schema().getObjectProp("prop2"));
        assertEquals(2, cleanSchema2.getField("map1").schema().getObjectProps().size());
        assertNull(cleanSchema3.getField("map1").schema().getObjectProp("prop1"));
        assertNull(cleanSchema3.getField("map1").schema().getObjectProp("prop3"));
        assertEquals(1, cleanSchema3.getField("map1").schema().getObjectProps().size());

        schema = SchemaBuilder
                .record("record")
                .fields()
                .name("map1")
                .type()
                .optional()
                .map()
                .prop("prop1", "value1")
                .prop("prop2", "value2")
                .prop("prop3", "value3")
                .values(SchemaBuilder.builder().intType())
                .endRecord();
        cleanSchema1 = AvroUtils.cleanSchema(schema, Collections.emptyList());
        cleanSchema2 = AvroUtils.cleanSchema(schema, Collections.singletonList("prop2"));
        cleanSchema3 = AvroUtils.cleanSchema(schema, Arrays.asList("prop1", "prop3"));

        assertEquals(schema, cleanSchema1);
        assertEquals(schema, cleanSchema1);
        assertNull(cleanSchema2.getField("map1").schema().getTypes().get(1).getObjectProp("prop2"));
        assertEquals(2, cleanSchema2.getField("map1").schema().getTypes().get(1).getObjectProps().size());
        assertNull(cleanSchema3.getField("map1").schema().getTypes().get(1).getObjectProp("prop1"));
        assertNull(cleanSchema3.getField("map1").schema().getTypes().get(1).getObjectProp("prop3"));
        assertEquals(1, cleanSchema3.getField("map1").schema().getTypes().get(1).getObjectProps().size());
    }

    @Test
    public void testCleanSchemaArray() throws URISyntaxException, IOException {
        Schema schema = SchemaBuilder
                .record("record")
                .fields()
                .name("array1")
                .type()
                .array()
                .prop("prop1", "value1")
                .prop("prop2", "value2")
                .prop("prop3", "value3")
                .items(SchemaBuilder.builder().intType())
                .arrayDefault(new ArrayList<>())
                .endRecord();
        Schema cleanSchema1 = AvroUtils.cleanSchema(schema, Collections.emptyList());
        Schema cleanSchema2 = AvroUtils.cleanSchema(schema, Collections.singletonList("prop2"));
        Schema cleanSchema3 = AvroUtils.cleanSchema(schema, Arrays.asList("prop1", "prop3"));

        assertEquals(schema, cleanSchema1);
        assertNull(cleanSchema2.getField("array1").schema().getObjectProp("prop2"));
        assertEquals(2, cleanSchema2.getField("array1").schema().getObjectProps().size());
        assertNull(cleanSchema3.getField("array1").schema().getObjectProp("prop1"));
        assertNull(cleanSchema3.getField("array1").schema().getObjectProp("prop3"));
        assertEquals(1, cleanSchema3.getField("array1").schema().getObjectProps().size());

        schema = SchemaBuilder
                .record("record")
                .fields()
                .name("array1")
                .type()
                .optional()
                .array()
                .prop("prop1", "value1")
                .prop("prop2", "value2")
                .prop("prop3", "value3")
                .items(SchemaBuilder.builder().intType())
                .endRecord();
        cleanSchema1 = AvroUtils.cleanSchema(schema, Collections.emptyList());
        cleanSchema2 = AvroUtils.cleanSchema(schema, Collections.singletonList("prop2"));
        cleanSchema3 = AvroUtils.cleanSchema(schema, Arrays.asList("prop1", "prop3"));

        assertEquals(schema, cleanSchema1);
        assertEquals(schema, cleanSchema1);
        assertNull(cleanSchema2.getField("array1").schema().getTypes().get(1).getObjectProp("prop2"));
        assertEquals(2, cleanSchema2.getField("array1").schema().getTypes().get(1).getObjectProps().size());
        assertNull(cleanSchema3.getField("array1").schema().getTypes().get(1).getObjectProp("prop1"));
        assertNull(cleanSchema3.getField("array1").schema().getTypes().get(1).getObjectProp("prop3"));
        assertEquals(1, cleanSchema3.getField("array1").schema().getTypes().get(1).getObjectProps().size());
    }

    @Test
    public void testCleanSchemaRecord() throws URISyntaxException, IOException {
        Schema schema = SchemaBuilder
                .record("record")
                .fields()
                .name("record1")
                .type()
                .record("record1_1")
                .prop("prop1", "value1")
                .prop("prop2", "value2")
                .prop("prop3", "value3")
                .fields()
                .requiredInt("int1")
                .endRecord()
                .noDefault()
                .endRecord();
        Schema cleanSchema1 = AvroUtils.cleanSchema(schema, Collections.emptyList());
        Schema cleanSchema2 = AvroUtils.cleanSchema(schema, Collections.singletonList("prop2"));
        Schema cleanSchema3 = AvroUtils.cleanSchema(schema, Arrays.asList("prop1", "prop3"));

        assertEquals(schema, cleanSchema1);
        assertNull(cleanSchema2.getField("record1").schema().getObjectProp("prop2"));
        assertEquals(2, cleanSchema2.getField("record1").schema().getObjectProps().size());
        assertNull(cleanSchema3.getField("record1").schema().getObjectProp("prop1"));
        assertNull(cleanSchema3.getField("record1").schema().getObjectProp("prop3"));
        assertEquals(1, cleanSchema3.getField("record1").schema().getObjectProps().size());

        schema = SchemaBuilder
                .record("record")
                .fields()
                .name("record1")
                .type()
                .optional()
                .record("record1_1")
                .prop("prop1", "value1")
                .prop("prop2", "value2")
                .prop("prop3", "value3")
                .fields()
                .requiredInt("int1")
                .endRecord()
                .endRecord();
        cleanSchema1 = AvroUtils.cleanSchema(schema, Collections.emptyList());
        cleanSchema2 = AvroUtils.cleanSchema(schema, Collections.singletonList("prop2"));
        cleanSchema3 = AvroUtils.cleanSchema(schema, Arrays.asList("prop1", "prop3"));

        assertEquals(schema, cleanSchema1);
        assertEquals(schema, cleanSchema1);
        assertNull(cleanSchema2.getField("record1").schema().getTypes().get(1).getObjectProp("prop2"));
        assertEquals(2, cleanSchema2.getField("record1").schema().getTypes().get(1).getObjectProps().size());
        assertNull(cleanSchema3.getField("record1").schema().getTypes().get(1).getObjectProp("prop1"));
        assertNull(cleanSchema3.getField("record1").schema().getTypes().get(1).getObjectProp("prop3"));
        assertEquals(1, cleanSchema3.getField("record1").schema().getTypes().get(1).getObjectProps().size());
    }
}
