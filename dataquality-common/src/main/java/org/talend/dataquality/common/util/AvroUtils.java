package org.talend.dataquality.common.util;

import static java.util.stream.Collectors.toList;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Methods for Avro analyzers.
 */
public class AvroUtils {

    /**
     * From a record schema, create a value level metadata schema replacing primitive types by a value level metadata
     * schema.
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
        return Pair
                .of(StreamSupport.stream(dateAvroReader.spliterator(), false).map(c -> (IndexedRecord) c),
                        dateAvroReader.getSchema());
    }

    public static Schema dereferencing(Schema schema) {
        Schema dereferencedSchema = schema;
        Stream names = getNamedTypes(schema);

        List<String> flattenNames = flattenStream(names).stream().distinct().collect(toList());

        if (!flattenNames.isEmpty()) {
            Map<String, String> namespaces = new HashMap<>();
            for (String name : flattenNames) {
                namespaces.put(name, "a");
            }
            return buildDereferencedSchema(schema, namespaces);
        }
        return dereferencedSchema;
    }

    private static Stream getNamedTypes(Schema schema) {
        return schema.getFields().stream().flatMap(field -> {
            Schema fieldSchema = field.schema();
            switch (fieldSchema.getType()) {
            case RECORD:
                return Stream.of(getNamedTypes(fieldSchema), fieldSchema.getFullName());
            case ARRAY:
                return Stream
                        .of(getNamedTypes(fieldSchema.getElementType()), fieldSchema.getElementType().getFullName());
            case MAP:
                return Stream.of(getNamedTypes(fieldSchema.getValueType()));
            case UNION:
                return fieldSchema.getTypes().stream().flatMap(unionSchema -> {
                    switch (unionSchema.getType()) {
                    case RECORD:
                        return Stream.of(getNamedTypes(unionSchema), unionSchema.getFullName());
                    case ARRAY:
                        return Stream.of(getNamedTypes(unionSchema.getElementType()));
                    case MAP:
                        return Stream.of(getNamedTypes(unionSchema.getValueType()));
                    case FIXED:
                        return Stream.of(unionSchema.getFullName());
                    default:
                        return null;
                    }
                });
            case FIXED:
                return Stream.of(fieldSchema.getFullName());
            default:
                return null;
            }
        });
    }

    private static List<String> flattenStream(Stream stream) {
        List<String> flattenNames = new ArrayList<>();
        stream.forEach(obj -> {
            if (obj instanceof String) {
                flattenNames.add((String) obj);
            } else {
                flattenStream((Stream) obj);
            }
        });
        return flattenNames;
    }

    private static Schema buildDereferencedSchema(Schema schema, Map<String, String> namespaces) {

        String namespace = schema.getNamespace();
        if (namespaces.containsKey(schema.getFullName())) {
            namespace = namespace + "." + namespaces.get(schema.getFullName());
            namespaces.put(schema.getFullName(), nextNamespaceSuffix(namespaces.get(schema.getFullName())));
        }
        final SchemaBuilder.RecordBuilder<Schema> qualityRecordBuilder =
                SchemaBuilder.record(schema.getName()).namespace(namespace);
        final SchemaBuilder.FieldAssembler<Schema> fieldAssembler = qualityRecordBuilder.fields();

        for (Schema.Field field : schema.getFields()) {
            Schema fieldSchema = field.schema();
            switch (fieldSchema.getType()) {
            case RECORD:
                Schema sRecord = buildDereferencedSchema(field.schema(), namespaces);
                System.out.println(sRecord);
                fieldAssembler.name(field.name()).type(sRecord).noDefault();
                break;
            case ARRAY:
                Schema sArray = Schema.createArray(buildDereferencedSchema(fieldSchema.getElementType(), namespaces));
                System.out.println(sArray);
                fieldAssembler
                        .name(field.name())
                        .type(sArray)
                        .noDefault();

                break;
            case UNION:
                List<Schema> fullChild = fieldSchema.getTypes().stream().map(unionSchema -> {
                    switch (unionSchema.getType()) {
                    case RECORD:
                        return buildDereferencedSchema(unionSchema, namespaces);
                    case ARRAY:
                        return Schema.createArray(buildDereferencedSchema(unionSchema.getElementType(), namespaces));
                    case MAP:
                        return Schema.createMap(buildDereferencedSchema(unionSchema.getValueType(), namespaces));
                    default:
                        return unionSchema;
                    }
                }).collect(Collectors.toList());

                Schema sUnion = Schema.createUnion(fullChild);
                System.out.println(sUnion);
                fieldAssembler.name(field.name()).type(sUnion).noDefault();
                break;
            case MAP:
                System.out.println("CREATE MAP");
                Schema sMap = Schema.createMap(buildDereferencedSchema(fieldSchema.getValueType(), namespaces));
                System.out.println(sMap);
                fieldAssembler
                        .name(field.name())
                        .type(sMap)
                        .noDefault();
                System.out.println("END MAP");
            case ENUM:
            case FIXED:
            case STRING:
            case BYTES:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case BOOLEAN:
                fieldAssembler.name(field.name()).type((new Schema.Parser()).parse(field.schema().toString())).noDefault();
                break;
            case NULL:
                break;
            }
        }

        return fieldAssembler.endRecord();
    }

    private static String nextNamespaceSuffix(String suffix) {
        return encode(decode(suffix) + 1);
    }

    private static long decode(String input) {
        long value = 0;
        int i = 0;
        for (char c : input.toCharArray()) {
            value += (c - 'a' + 1) * Math.pow(26, i);
            i++;
        }
        return value;
    }

    private static String encode(long value) {

        StringBuilder output = new StringBuilder("");

        long divide = value;
        long remaining;

        do {
            divide = divide / 26;
            remaining = value % 26;
            if (remaining == 0)
                remaining = 26;
            output.append((char) ('a' + remaining - 1));
        } while (divide != 0);

        return output.toString();
    }
}
