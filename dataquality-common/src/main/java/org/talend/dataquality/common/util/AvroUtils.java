package org.talend.dataquality.common.util;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import static org.talend.dataquality.common.util.QualityType.DISCOVERY;

/**
 * Methods for Avro analyzers.
 */
public class AvroUtils {

    private static final String SEM_QUALITY_SCHEMA_DEF =
            "{ \"type\": \"record\"," + "    \"name\": \"quality_metadata\", \"namespace\": \"org.talend.dataquality\","
                    + "    \"fields\": [ { \"name\": \"validity\", \"type\": \"int\" } ] }";

    public static final Schema SEM_QUALITY_SCHEMA = new Schema.Parser().parse(SEM_QUALITY_SCHEMA_DEF);

    private static final String SEM_DISCOVERY_SCHEMA_DEF = "{\"type\": \"record\","
            + "\"name\": \"discovery_metadata\", \"namespace\": \"org.talend.dataquality\","
            + "\"fields\":[{ \"type\":\"string\", \"name\":\"matching\"}, { \"type\":\"int\", \"name\":\"total\"}]}";

    public static final Schema SEM_DISCOVERY_SCHEMA = new Schema.Parser().parse(SEM_DISCOVERY_SCHEMA_DEF);

    private static final String SEM_DISCOVERY_DATATYPE_SCHEMA_DEF =
            "{\"type\": \"record\"," + "\"name\": \"datatype_metadata\", \"namespace\": \"org.talend.dataquality\","
                    + "\"fields\":[{ \"type\":\"string\", \"name\":\"dataType\"}]}";

    public static final Schema SEM_DISCOVERY_DATATYPE_SCHEMA =
            new Schema.Parser().parse(SEM_DISCOVERY_DATATYPE_SCHEMA_DEF);

    /**
     * From a record schema, create a semantic schema replacing type by a record with information about the quality.
     *
     * @param sourceSchema Record schema
     * @return Semantic schema
     */
    public static Schema createRecordSemanticSchema(Schema sourceSchema, QualityType type) {
        final Schema semanticSchema = createSemanticSchemaForRecord(sourceSchema, type);
        return semanticSchema;
    }

    private static Schema createSemanticSchemaForRecord(Schema recordSchema, QualityType type) {
        final SchemaBuilder.RecordBuilder<Schema> semanticRecordBuilder =
                SchemaBuilder.record(recordSchema.getName()).namespace(recordSchema.getNamespace());
        final SchemaBuilder.FieldAssembler<Schema> fieldAssembler = semanticRecordBuilder.fields();

        for (Schema.Field field : recordSchema.getFields()) {
            fieldAssembler.name(field.name()).type(createSemanticSchema(field.schema(), type)).noDefault();
        }

        return fieldAssembler.endRecord();
    }

    private static Schema createSemanticSchema(Schema sourceSchema, QualityType type) {
        switch (sourceSchema.getType()) {
        case RECORD:
            return createSemanticSchemaForRecord(sourceSchema, type);

        case ARRAY:
            return Schema.createArray(createSemanticSchema(sourceSchema.getElementType(), type));

        case MAP:
            return Schema.createMap(createSemanticSchema(sourceSchema.getValueType(), type));

        case UNION:
            final Set<Schema> unionSchemas = new HashSet<>();
            for (Schema unionSchema : sourceSchema.getTypes()) {
                unionSchemas.add(createSemanticSchema(unionSchema, type));
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
            if (DISCOVERY.equals(type))
                return SEM_DISCOVERY_SCHEMA;
            else
                return SEM_QUALITY_SCHEMA;
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
        final SchemaBuilder.RecordBuilder<Schema> qualityRecordBuilder =
                SchemaBuilder.record(sourceSchema.getName()).namespace(sourceSchema.getNamespace());
        final SchemaBuilder.FieldAssembler<Schema> fieldAssembler = qualityRecordBuilder.fields();

        for (Schema.Field field : sourceSchema.getFields()) {
            fieldAssembler.name(field.name()).type(createSchemaCopy(field.schema())).noDefault();
        }

        return fieldAssembler.endRecord();
    }

    private static Schema createSchemaCopy(Schema sourceSchema) {
        switch (sourceSchema.getType()) {
        case RECORD:
            return copySchema(sourceSchema);

        case ARRAY:
            return Schema.createArray(createSchemaCopy(sourceSchema.getElementType()));

        case MAP:
            return Schema.createMap(createSchemaCopy(sourceSchema.getValueType()));

        case UNION:
            final List<Schema> types =
                    sourceSchema.getTypes().stream().map(type -> createSchemaCopy(type)).collect(Collectors.toList());
            return Schema.createUnion(types);

        case ENUM:
            return Schema.createEnum(sourceSchema.getName(), sourceSchema.getDoc(), sourceSchema.getNamespace(),
                    sourceSchema.getEnumSymbols());

        case FIXED:
            return Schema.createFixed(sourceSchema.getName(), sourceSchema.getDoc(), sourceSchema.getNamespace(),
                    sourceSchema.getFixedSize());

        case STRING:
        case BYTES:
        case INT:
        case LONG:
        case FLOAT:
        case DOUBLE:
        case BOOLEAN:
        case NULL:
            return Schema.create(sourceSchema.getType());
        }

        return null;
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
}
