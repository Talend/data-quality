package org.talend.dataquality.common.util;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.apache.avro.Schema.Type.BOOLEAN;
import static org.apache.avro.Schema.Type.BYTES;
import static org.apache.avro.Schema.Type.DOUBLE;
import static org.apache.avro.Schema.Type.ENUM;
import static org.apache.avro.Schema.Type.FIXED;
import static org.apache.avro.Schema.Type.FLOAT;
import static org.apache.avro.Schema.Type.INT;
import static org.apache.avro.Schema.Type.LONG;
import static org.apache.avro.Schema.Type.NULL;
import static org.apache.avro.Schema.Type.STRING;

/**
 * Methods for Avro analyzers.
 */
public class AvroUtils {

    /**
     * From a record schema, create a value level metadata schema replacing primitive types by a value level metadata schema.
     *
     * @param sourceSchema Record schema
     * @return Semantic schema
     */
    public static Schema createRecordSemanticSchema(Schema sourceSchema, Schema valueLevelMetadataSchema) {
        final Schema semanticSchema = createSemanticSchemaForRecord(sourceSchema, valueLevelMetadataSchema);
        return semanticSchema;
    }

    private static Schema createSemanticSchemaForRecord(Schema recordSchema, Schema valueLevelMetadataSchema) {
        final SchemaBuilder.RecordBuilder<Schema> semanticRecordBuilder =
                SchemaBuilder.record(recordSchema.getName()).namespace(recordSchema.getNamespace());
        final SchemaBuilder.FieldAssembler<Schema> fieldAssembler = semanticRecordBuilder.fields();

        for (Schema.Field field : recordSchema.getFields()) {
            fieldAssembler
                    .name(field.name())
                    .type(createSemanticSchema(field.schema(), valueLevelMetadataSchema))
                    .noDefault();
        }

        return fieldAssembler.endRecord();
    }

    private static Schema createSemanticSchema(Schema sourceSchema, Schema valueLevelMetadataSchema) {
        switch (sourceSchema.getType()) {
        case RECORD:
            return createSemanticSchemaForRecord(sourceSchema, valueLevelMetadataSchema);

        case ARRAY:
            return Schema.createArray(createSemanticSchema(sourceSchema.getElementType(), valueLevelMetadataSchema));

        case MAP:
            return Schema.createMap(createSemanticSchema(sourceSchema.getValueType(), valueLevelMetadataSchema));

        case UNION:
            final Set<Schema> unionSchemas = new HashSet<>();
            for (Schema unionSchema : sourceSchema.getTypes()) {
                unionSchemas.add(createSemanticSchema(unionSchema, valueLevelMetadataSchema));
            }
            return Schema.createUnion(new ArrayList<>(unionSchemas));

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
            return valueLevelMetadataSchema;
        }

        return null;
    }

    /**
     * Make a copy of a schema.
     *
     * @param sourceSchema Schema to copy
     * @return New schema
     */
    public static Schema copySchema(Schema sourceSchema) {
        return new Schema.Parser().parse(sourceSchema.toString());
    }

    public static boolean isPrimitiveType(Schema.Type type) {
        return isPrimitiveType(type, false);
    }

    /**
     * Returns true if the type is a primitive type or an enum/fixed type.
     *
     * @param type Schema type
     * @param enumFixedIncluded Include or not ENUM and FIXED in the check
     * @return True if primitive
     */
    public static boolean isPrimitiveType(Schema.Type type, boolean enumFixedIncluded) {
        return type == STRING || type == BYTES || type == INT || type == LONG || type == FLOAT || type == DOUBLE
                || type == BOOLEAN || type == NULL || (enumFixedIncluded && (type == ENUM || type == FIXED));
    }

    public static String itemId(String prefix, String itemId) {
        if (StringUtils.isEmpty(prefix)) {
            return itemId;
        }

        return prefix + "." + itemId;
    }

    /**
     * Extract a given property from a schema. This property can be present at anly level in the schema.
     *
     * @param schema Schema with the property
     * @param propName Name of the property to extract
     * @return Map with the property values (key: a name built with field name)
     */
    public static Map<String, Object> extractProperties(Schema schema, String propName) {
        final Map<String, Object> props = new HashMap<>();

        if (schema != null && StringUtils.isNoneEmpty(propName)) {
            extractProperties(schema, propName, props, "");
        }

        return props;
    }

    public static void extractProperties(Schema schema, String propName, Map<String, Object> props, String prefix) {
        switch (schema.getType()) {
        case RECORD:
            for (Schema.Field field : schema.getFields()) {
                extractProperties(field.schema(), propName, props, itemId(prefix, field.name()));
            }
            break;

        case ARRAY:
            extractProperties(schema.getElementType(), propName, props, prefix);
            break;

        case MAP:
            extractProperties(schema.getValueType(), propName, props, prefix);
            break;

        case UNION:
            for (Schema unionSchema : schema.getTypes()) {
                if (isPrimitiveType(unionSchema.getType())) {
                    extractProperties(unionSchema, propName, props, itemId(prefix, unionSchema.getName()));
                } else {
                    extractProperties(unionSchema, propName, props, prefix);
                }
            }
            break;

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
            if (schema.getObjectProp(propName) != null) {
                props.put(prefix, schema.getObjectProp(propName));
            }
            break;
        }
    }

    public static Pair<Stream<IndexedRecord>, Schema> streamAvroFile(File file) throws IOException {
        DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(file, new GenericDatumReader<>());
        return Pair.of(StreamSupport.stream(dateAvroReader.spliterator(), false).map(c -> (IndexedRecord) c),
                dateAvroReader.getSchema());
    }
}
