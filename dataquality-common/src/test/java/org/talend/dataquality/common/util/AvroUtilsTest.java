package org.talend.dataquality.common.util;

import com.clearspring.analytics.util.Lists;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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
    public void testCreateRecordSemanticSchemaQualityForPrimitiveSchemas() {
        try {
            String path = AvroUtils.class.getResource("sample/primitive").getPath();
            File primitiveFolder = new File(path);
            for (final File fileEntry : Objects.requireNonNull(primitiveFolder.listFiles())) {
                DataFileReader<GenericRecord> avro = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
                Schema schema = avro.getSchema();
                Schema validationSchema = AvroUtils.createRecordSemanticSchema(schema, QualityType.VALIDATION);
                Schema discoverySchema = AvroUtils.createRecordSemanticSchema(schema, QualityType.DISCOVERY);
                List<Schema> schemas = getAllSubSchemas(schema);
                List<Schema> validationSchemas = getAllSubSchemas(validationSchema);
                List<Schema> discoverySchemas = getAllSubSchemas(discoverySchema);
                assertEquals((schemas.size() - 1) * 2 + 1, validationSchemas.size());
                assertEquals((schemas.size() - 1) * 3 + 1, discoverySchemas.size());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateRecordSemanticSchemaQualityForComplexSchemas() {
        try {
            String path = AvroUtils.class.getResource("sample/complex").getPath();
            File primitiveFolder = new File(path);
            for (final File fileEntry : Objects.requireNonNull(primitiveFolder.listFiles())) {
                DataFileReader<GenericRecord> avro = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
                Schema schema = avro.getSchema();
                Schema validationSchema = AvroUtils.createRecordSemanticSchema(schema, QualityType.VALIDATION);
                Schema discoverySchema = AvroUtils.createRecordSemanticSchema(schema, QualityType.DISCOVERY);
                List<Schema> schemas = getAllSubSchemas(schema);
                List<Schema> validationSchemas = getAllSubSchemas(validationSchema);
                List<Schema> discoverySchemas = getAllSubSchemas(discoverySchema);
                Set<String> nonePrimitiveNames = schemas
                        .stream()
                        .filter(s -> !AvroUtils.isPrimitiveType(s.getType(), true))
                        .map(Schema::getName)
                        .collect(Collectors.toSet());
                Set<String> nonePrimitiveNamesValidation = validationSchemas
                        .stream()
                        .filter(s -> !AvroUtils.isPrimitiveType(s.getType()))
                        .map(Schema::getName)
                        .collect(Collectors.toSet());
                Set<String> nonePrimitiveNamesDiscovery = discoverySchemas
                        .stream()
                        .filter(s -> !AvroUtils.isPrimitiveType(s.getType()))
                        .map(Schema::getName)
                        .collect(Collectors.toSet());
                assert (nonePrimitiveNamesValidation.containsAll(nonePrimitiveNames));
                assert (nonePrimitiveNamesDiscovery.containsAll(nonePrimitiveNames));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateRecordSemanticSchemaQualityForComplexStructures() {
        try {
            String path = AvroUtils.class.getResource("sample/structure").getPath();
            File primitiveFolder = new File(path);
            for (final File fileEntry : Objects.requireNonNull(primitiveFolder.listFiles())) {
                DataFileReader<GenericRecord> avro = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
                Schema schema = avro.getSchema();
                Schema validationSchema = AvroUtils.createRecordSemanticSchema(schema, QualityType.VALIDATION);
                Schema discoverySchema = AvroUtils.createRecordSemanticSchema(schema, QualityType.DISCOVERY);
                List<Schema> schemas = getAllSubSchemas(schema);
                List<Schema> validationSchemas = getAllSubSchemas(validationSchema);
                List<Schema> discoverySchemas = getAllSubSchemas(discoverySchema);
                Set<String> nonePrimitiveNames = schemas
                        .stream()
                        .filter(s -> !AvroUtils.isPrimitiveType(s.getType(), true))
                        .map(Schema::getName)
                        .collect(Collectors.toSet());
                Set<String> nonePrimitiveNamesValidation = validationSchemas
                        .stream()
                        .filter(s -> !AvroUtils.isPrimitiveType(s.getType()))
                        .map(Schema::getName)
                        .collect(Collectors.toSet());
                Set<String> nonePrimitiveNamesDiscovery = discoverySchemas
                        .stream()
                        .filter(s -> !AvroUtils.isPrimitiveType(s.getType()))
                        .map(Schema::getName)
                        .collect(Collectors.toSet());
                assert (nonePrimitiveNamesValidation.containsAll(nonePrimitiveNames));
                assert (nonePrimitiveNamesDiscovery.containsAll(nonePrimitiveNames));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Schema> getAllSubSchemas(Schema sourceSchema) {
        List<Schema> subSchemas = Lists.newArrayList();
        switch (sourceSchema.getType()) {
        case RECORD:
            subSchemas = sourceSchema
                    .getFields()
                    .stream()
                    .flatMap(field -> getAllSubSchemas(field.schema()).stream())
                    .collect(Collectors.toList());
            subSchemas.add(sourceSchema);
            return subSchemas;
        case ARRAY:
            subSchemas = getAllSubSchemas(sourceSchema.getElementType());
            subSchemas.add(sourceSchema);
            return subSchemas;
        case MAP:
            subSchemas = getAllSubSchemas(sourceSchema.getValueType());
            subSchemas.add(sourceSchema);
            return subSchemas;
        case UNION:
            for (Schema unionSchema : sourceSchema.getTypes()) {
                subSchemas.addAll(Objects.requireNonNull(getAllSubSchemas(unionSchema)));
            }
            subSchemas.add(sourceSchema);
            return subSchemas;
        case ENUM:
        case FIXED:
        case STRING:
        case BYTES:
        case INT:
        case LONG:
        case FLOAT:
        case DOUBLE:
        case BOOLEAN:
        case NULL:
            subSchemas.add(sourceSchema);
            return subSchemas;
        }

        return null;
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
}
