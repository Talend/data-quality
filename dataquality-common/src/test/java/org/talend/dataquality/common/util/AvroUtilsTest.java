package org.talend.dataquality.common.util;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        Map props = AvroUtils.extractProperties(null, "");
        assertTrue(props.size() == 0);

        props = AvroUtils.extractProperties(Schema.create(Schema.Type.STRING), "");
        assertTrue(props.size() == 0);
    }

    @Test
    public void testExtractSimpleRecord() {
        Schema schema = createSimpleRecordSchema();
        Map props = AvroUtils.extractProperties(schema, "quality");

        assertEquals(2, props.size());
        assertEquals("not so bad", props.get("firstname"));
        assertEquals("not so bad", props.get("lastname"));
    }

    @Test
    public void testExtractComplexRecord() {
        Schema schema = createComplexRecordSchema();
        Map props = AvroUtils.extractProperties(schema, "quality");

        assertEquals(4, props.size());
        assertEquals("not so bad", props.get("emails"));
        assertEquals("not so bad", props.get("location.country"));
        assertEquals("not so bad", props.get("age.string"));
        assertEquals("not so bad", props.get("age.int"));
    }
}
