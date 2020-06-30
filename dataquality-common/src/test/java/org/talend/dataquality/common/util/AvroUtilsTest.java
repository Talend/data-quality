package org.talend.dataquality.common.util;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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

        assertEquals("{\"type\":\"record\",\"name\":\"Person\",\"namespace\":\"experiment.sample\",\"fields\":[{\"name\":\"firstName\",\"type\":\"string\"},{\"name\":\"midleName\",\"type\":[\"null\",\"string\"]},{\"name\":\"lastName\",\"type\":\"string\"},{\"name\":\"homeAddress\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"Address\",\"namespace\":\"experiment.sample.a\",\"fields\":[{\"name\":\"line\",\"type\":\"string\"},{\"name\":\"postalCode\",\"type\":\"string\"},{\"name\":\"city\",\"type\":\"string\"}]}]},{\"name\":\"companyAddress\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"Address\",\"namespace\":\"experiment.sample.b\",\"fields\":[{\"name\":\"line\",\"type\":\"string\"},{\"name\":\"postalCode\",\"type\":\"string\"},{\"name\":\"city\",\"type\":\"string\"}]}]}]}", schemaWithoutRefTypes.toString());
    }
}
