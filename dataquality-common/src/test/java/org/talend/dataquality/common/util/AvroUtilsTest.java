package org.talend.dataquality.common.util;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class AvroUtilsTest {

    private static final Schema SIMPLE_SCHEMA = SchemaBuilder
            .record("test")
            .namespace("org.talend.dataquality")
            .fields()
            .requiredString("email")
            .requiredInt("age")
            .endRecord();

    private static final Schema COMPLEX_SCHEMA = SchemaBuilder
            .record("test")
            .namespace("org.talend.dataquality")
            .fields()
            .name("emails")
            .type(Schema.createArray(Schema.create(Schema.Type.STRING)))
            .noDefault()
            .name("weights")
            .type(Schema.createMap(Schema.create(Schema.Type.FLOAT)))
            .noDefault()
            .name("location")
            .type(SchemaBuilder
                    .record("location")
                    .fields()
                    .name("country")
                    .type(Schema.create(Schema.Type.STRING))
                    .noDefault()
                    .endRecord())
            .noDefault()
            .name("age")
            .type(Schema.createUnion(Schema.create(Schema.Type.STRING), Schema.create(Schema.Type.INT)))
            .noDefault()
            .endRecord();

    private Schema getAnnotatedSimpleSchema() {
        Schema schema = AvroUtils.copySchema(SIMPLE_SCHEMA);
        schema.getField("email").schema().addProp("quality", "good");
        schema.getField("age").schema().addProp("quality", "excellent");
        return schema;
    }

    private Schema getAnnotatedComplexSchema() {
        Schema schema = AvroUtils.copySchema(COMPLEX_SCHEMA);
        schema.getField("emails").schema().getElementType().addProp("quality", "good");
        schema.getField("weights").schema().getValueType().addProp("quality", "very good");
        schema.getField("location").schema().getField("country").schema().addProp("quality", "excellent");
        schema.getField("age").schema().getTypes().get(0).addProp("quality", "not so bad");
        schema.getField("age").schema().getTypes().get(1).addProp("quality", "ok");
        return schema;
    }

    private Schema createUnionOfRecordsSchema() {
        Schema recordIntSchema =
                SchemaBuilder.record("num_int").fields().requiredInt("im").requiredInt("re").endRecord();
        recordIntSchema.getField("im").schema().addProp("quality", "quality INT");
        Schema recordStrSchema =
                SchemaBuilder.record("num_str").fields().requiredString("im").requiredString("re").endRecord();
        recordStrSchema.getField("im").schema().addProp("quality", "quality STR");

        return SchemaBuilder
                .record("test")
                .namespace("org.talend.dataquality")
                .fields()
                .name("number")
                .type(SchemaBuilder.unionOf().type(recordStrSchema).and().type(recordIntSchema).endUnion())
                .noDefault()
                .endRecord();
    }

    @Test
    public void testExtractNullSchema() {
        assertThrows(NullPointerException.class, () -> AvroUtils.extractProperties(null, ""));
        assertThrows(NullPointerException.class, () -> AvroUtils.extractAllProperties(null));
    }

    @Test
    public void testExtractEmptyResults() {
        Map<String, Object> props = AvroUtils.extractProperties(Schema.create(Schema.Type.STRING), "quality");
        assertTrue(props.isEmpty());
    }

    @Test
    public void testExtractNullPropName() {

        Schema schema = AvroUtils.copySchema(SIMPLE_SCHEMA);
        schema.getField("email").schema().addProp("validity", "yes");
        schema.getField("age").schema().addProp("quality", "excellent");

        Map<String, Object> props = AvroUtils.extractProperties(schema, null);

        assertEquals("yes", ((Map<String, Object>) props.get("email")).get("validity"));
        assertEquals("excellent", ((Map<String, Object>) props.get("age")).get("quality"));
    }

    @Test
    public void testExtractSimpleRecord() {
        Schema schema = getAnnotatedSimpleSchema();
        Map<String, Object> props = AvroUtils.extractProperties(schema, "quality");

        assertEquals(2, props.size());
        assertEquals("good", props.get("email"));
        assertEquals("excellent", props.get("age"));
    }

    @Test
    public void testExtractComplexRecord() {
        Schema schema = getAnnotatedComplexSchema();
        Map<String, Object> props = AvroUtils.extractProperties(schema, "quality");

        assertEquals(5, props.size());
        assertEquals("good", props.get("emails"));
        assertEquals("very good", props.get("weights"));
        assertEquals("excellent", props.get("location.country"));
        assertEquals("not so bad", props.get("age.string"));
        assertEquals("ok", props.get("age.int"));
    }

    @Test
    public void testExtractUnionOfRecords() {
        Schema schema = createUnionOfRecordsSchema();
        Map<String, Object> props = AvroUtils.extractProperties(schema, "quality");

        assertEquals(4, props.size());
        assertEquals("quality INT", props.get("number.num_int.im"));
        assertEquals("quality INT", props.get("number.num_int.re"));
        assertEquals("quality STR", props.get("number.num_str.im"));
        assertEquals("quality STR", props.get("number.num_str.re"));
    }

    @Test
    public void testAddPropsNullSchema() {
        assertThrows(NullPointerException.class, () -> AvroUtils.addProperties(null, "", null));
        assertThrows(NullPointerException.class, () -> AvroUtils.addAllProperties(null, null));
    }

    @Test
    public void testAddPropsSimpleRecord() {
        Schema schema = new Schema.Parser().parse(SIMPLE_SCHEMA.toString());
        Map<String, Object> props = new HashMap<>();
        props.put("email", "good");
        props.put("age", "excellent");
        AvroUtils.addProperties(schema, "quality", props);

        assertEquals(getAnnotatedSimpleSchema().toString(), schema.toString());
    }

    @Test
    public void testAddPropsComplexRecord() {
        Schema schema = new Schema.Parser().parse(COMPLEX_SCHEMA.toString());
        Map<String, Object> props = new HashMap<>();
        props.put("emails", "good");
        props.put("weights", "very good");
        props.put("location.country", "excellent");
        props.put("age.string", "not so bad");
        props.put("age.int", "ok");
        AvroUtils.addProperties(schema, "quality", props);

        assertEquals(getAnnotatedComplexSchema().toString(), schema.toString());
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
    public void testDereferencingIsIdentityForSchemaWithoutRef() throws IOException {
        Schema schemaWithoutRefTypes = readSchemaFromResources("is_identity.avsc");
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
    public void testDereferencingWithProps() {
        String schemaStr = "{ \"namespace\": \"org.talend.dataquality.test.model\"," + "  \"type\": \"record\","
                + "  \"name\": \"Person\", \"fields\": [" + "    { \"name\": \"firstname\", \"type\": \"string\" },"
                + "    { \"name\": \"answer\", \"type\": { \"type\": \"enum\", \"name\": \"Answer\", \"symbols\": [\"Y\", \"N\"]}},"
                + "    { \"name\":\"fruitsInBasket\", \"type\": {  \"type\": \"array\","
                + "                \"items\":{ \"name\":\"Fruit\","
                + "                    \"type\":\"record\",  \"fields\":[ {\"name\":\"name\", \"type\":\"string\"} ]}}},"
                + "    { \"name\":\"preferredFruits\", \"type\": {\"type\":\"array\", \"items\": \"Fruit\"}}]}";
        Schema schema = new Schema.Parser().parse(schemaStr);
        schema.getField("firstname").schema().addProp("qualityX", "good");
        schema.getField("answer").schema().addProp("qualityY", "good");
        schema.getField("fruitsInBasket").schema().getElementType().getField("name").schema().addProp("qualityZ",
                "good");

        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schema, true);

        assertEquals("good", schemaWithoutRefTypes.getField("firstname").schema().getProp("qualityX"));
        assertEquals("good", schemaWithoutRefTypes.getField("answer").schema().getProp("qualityY"));
        assertEquals("good", schemaWithoutRefTypes
                .getField("fruitsInBasket")
                .schema()
                .getElementType()
                .getField("name")
                .schema()
                .getProp("qualityZ"));
    }

    @Test
    public void testDereferencingArray() throws IOException {
        Schema schema = readSchemaFromResources("person.avsc");
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schema);
        assertNotEquals(schema, schemaWithoutRefTypes);
    }

    @Test
    public void testDereferencingoneLevelComplex() throws IOException {
        Schema schema = readSchemaFromResources("oneLevelComplex.avsc");
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schema);
        assertEquals(schema, schemaWithoutRefTypes);
    }

    @Test
    public void testDereferencingMultiLevelComplex() throws IOException {
        Schema schema = readSchemaFromResources("multiLevelComplex.avsc");
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schema);
        assertNotEquals(schema, schemaWithoutRefTypes);
    }

    @Test
    public void testDereferencingUnionOfComplex() throws IOException {
        Schema schema = readSchemaFromResources("unionOfComplex.avsc");
        Schema schemaWithoutRefTypes = AvroUtils.dereferencing(schema);
        assertNotEquals(schema, schemaWithoutRefTypes);
    }

    @Test
    public void testCleanSchemaSimplePrimitive() {
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
    public void testCleanSchemaEnum() {
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
    public void testCleanSchemaFixed() {
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
    public void testCleanSchemaMap() {
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
    public void testCleanSchemaArray() {
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
    public void testCleanSchemaRecord() {
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

    private Schema readSchemaFromResources(String resourceName) throws IOException {
        return new Schema.Parser().parse(getClass().getResourceAsStream(resourceName));
    }
}
