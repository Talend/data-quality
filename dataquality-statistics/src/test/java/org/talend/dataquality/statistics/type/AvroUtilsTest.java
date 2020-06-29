package org.talend.dataquality.statistics.type;

import com.clearspring.analytics.util.Lists;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;
import org.talend.dataquality.common.inference.AvroQualityAnalyzer;
import org.talend.dataquality.common.util.AvroUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class AvroUtilsTest {

    @Test
    public void testCreateRecordSemanticSchemaQualityForPrimitiveSchemas() {
        try {
            String path = AvroUtilsTest.class.getResource("../sample/primitive").getPath();
            File primitiveFolder = new File(path);
            for (final File fileEntry : Objects.requireNonNull(primitiveFolder.listFiles())) {
                DataFileReader<GenericRecord> avro = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
                Schema schema = avro.getSchema();
                Schema discoverySchema = AvroUtils.createRecordSemanticSchema(schema,
                        AvroDataTypeDiscoveryAnalyzer.DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA);
                Schema qualitySchema =
                        AvroUtils.createRecordSemanticSchema(schema, AvroQualityAnalyzer.QUALITY_VALUE_LEVEL_SCHEMA);
                List<Schema> schemas = getAllSubSchemas(schema);
                List<Schema> discoverySchemas = getAllSubSchemas(discoverySchema);
                List<Schema> qualitySchemas = getAllSubSchemas(qualitySchema);
                assertEquals((schemas.size() - 1) * 2 + 1, discoverySchemas.size());
                assertEquals((schemas.size() - 1) * 2 + 1, qualitySchemas.size());
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
            String path = AvroUtilsTest.class.getResource("../sample/complex").getPath();
            File primitiveFolder = new File(path);
            for (final File fileEntry : Objects.requireNonNull(primitiveFolder.listFiles())) {
                DataFileReader<GenericRecord> avro = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
                Schema schema = avro.getSchema();
                Schema validationSchema =
                        AvroUtils.createRecordSemanticSchema(schema, AvroQualityAnalyzer.QUALITY_VALUE_LEVEL_SCHEMA);
                Schema discoverySchema = AvroUtils.createRecordSemanticSchema(schema,
                        AvroDataTypeDiscoveryAnalyzer.DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA);
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
            String path = AvroUtilsTest.class.getResource("../sample/structure").getPath();
            File primitiveFolder = new File(path);
            for (final File fileEntry : Objects.requireNonNull(primitiveFolder.listFiles())) {
                DataFileReader<GenericRecord> avro = new DataFileReader<>(fileEntry, new GenericDatumReader<>());
                Schema schema = avro.getSchema();
                Schema validationSchema =
                        AvroUtils.createRecordSemanticSchema(schema, AvroQualityAnalyzer.QUALITY_VALUE_LEVEL_SCHEMA);
                Schema discoverySchema = AvroUtils.createRecordSemanticSchema(schema,
                        AvroDataTypeDiscoveryAnalyzer.DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA);
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

}
