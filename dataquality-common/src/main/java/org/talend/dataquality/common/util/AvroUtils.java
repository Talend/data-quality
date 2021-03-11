package org.talend.dataquality.common.util;

import static org.apache.avro.Schema.Type.BOOLEAN;
import static org.apache.avro.Schema.Type.BYTES;
import static org.apache.avro.Schema.Type.DOUBLE;
import static org.apache.avro.Schema.Type.ENUM;
import static org.apache.avro.Schema.Type.FIXED;
import static org.apache.avro.Schema.Type.FLOAT;
import static org.apache.avro.Schema.Type.INT;
import static org.apache.avro.Schema.Type.LONG;
import static org.apache.avro.Schema.Type.NULL;
import static org.apache.avro.Schema.Type.RECORD;
import static org.apache.avro.Schema.Type.STRING;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Methods for Avro analyzers.
 */
public class AvroUtils {

    /**
     * Apply a {@link Schema} on a {@link IndexedRecord} given in parameters.
     *
     * @param record to have the schema applied on
     * @param schema to be applied
     * @return the new {@link IndexedRecord} with the new {@link Schema}
     */
    public static IndexedRecord applySchema(IndexedRecord record, Schema schema) {
        try {
            GenericDatumWriter<IndexedRecord> writer = new GenericDatumWriter<>(record.getSchema());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            writer.write(record, encoder);
            encoder.flush();

            DatumReader<IndexedRecord> reader = new GenericDatumReader<>(schema);
            Decoder decoder = DecoderFactory.get().binaryDecoder(outputStream.toByteArray(), null);
            return reader.read(null, decoder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Replace 'null' unions representation with a simplified version. At the time of writing, 'Daikon' doesn't
     * support the more advanced representation.
     * <p>
     * <p>
     *
     * <pre>
     *  "type": [
     *      {
     *          "type": "string",
     *          "dqType": "STRING"
     *      },
     *      {
     *          "type": "null",
     *          "dqType": "STRING"
     *      }
     * ]
     * </pre>
     * </p>
     * <p>
     * This method transforms this 'null' union representation in a simpler format:
     * </p>
     * <p>
     *
     * <pre>
     *  "type": [
     *      {
     *          "type": "string",
     *          "dqType": "STRING"
     *      },
     *      "null"
     * ]
     * </pre>
     * </p>
     *
     * @param s the {@link Schema} to transform
     * @return the transformed {@link Schema}
     */
    public static Schema replaceNullUnion(Schema s) {
        return replaceNullUnion(s, Collections.emptyList());
    }

    public static Schema replaceNullUnion(Schema s, List<String> propsToAvoid) {
        if (s == null) {
            return null;
        }
        switch (s.getType()) {
        case UNION:
            List<Schema> replacedUnionTypes = new ArrayList<>();
            for (Schema unionType : s.getTypes()) {
                if (unionType.getType() == Schema.Type.NULL) {
                    replacedUnionTypes.add(Schema.create(Schema.Type.NULL));
                } else {
                    Schema unionTypeSchema = replaceNullUnion(unionType, propsToAvoid);
                    replacedUnionTypes.add(unionTypeSchema);
                }
            }
            Schema unionSchema = Schema.createUnion(replacedUnionTypes);
            addPropsToSchema(unionSchema, s.getObjectProps(), propsToAvoid);
            return unionSchema;
        case RECORD:
            List<Schema.Field> fields = new ArrayList<>();
            for (Schema.Field outField : s.getFields()) {
                Schema fieldSchema = replaceNullUnion(outField.schema(), propsToAvoid);
                addPropsToSchema(fieldSchema, outField.schema().getObjectProps(), propsToAvoid);
                fields.add(new Schema.Field(outField.name(), fieldSchema, outField.doc(), outField.defaultVal()));
            }
            Schema recordSchema = Schema.createRecord(s.getName(), s.getDoc(), s.getNamespace(), s.isError(), fields);
            addPropsToSchema(recordSchema, s.getObjectProps(), propsToAvoid);
            return recordSchema;
        case ARRAY:
            Schema arraySchema = Schema.createArray(replaceNullUnion(s.getElementType(), propsToAvoid));
            addPropsToSchema(arraySchema, s.getObjectProps(), propsToAvoid);
            return arraySchema;
        case MAP:
            Schema mapSchema = Schema.createMap(replaceNullUnion(s.getValueType(), propsToAvoid));
            addPropsToSchema(mapSchema, s.getObjectProps(), propsToAvoid);
            return mapSchema;
        default:
            return s;
        }
    }

    /**
     * From a record schema, create a value level metadata schema replacing primitive types by a value level metadata
     * schema.
     *
     * @param sourceSchema Record schema
     * @return Semantic schema
     */
    public static Schema createRecordSemanticSchema(Schema sourceSchema, Schema valueLevelMetadataSchema) {
        return createSemanticSchemaForRecord(sourceSchema, valueLevelMetadataSchema);
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
     * @return <code>true</code> if primitive, <code>false</code> otherwise.
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
     * Extract all the available properties from a schema.
     *
     * @param schema to be extracted
     * @return all available properties; The key of the map represents the path to the field in the schema, the value is another Map of key/value properties.
     */
    public static Map<String, Object> extractAllProperties(Schema schema) {
        return extractProperties(schema, null);
    }

    /**
     * Extract a given property from a schema. This property can be present at any level in the schema.
     * if propName is <code>null</code>, it will extract all the available properties from the schema.
     *
     * @param schema Schema with the property
     * @param propName Name of the property to extract
     * @return Map with the property values (key: a name built with field name)
     */
    public static Map<String, Object> extractProperties(Schema schema, String propName) {
        Objects.requireNonNull(schema, "Input schema should not be null.");
        final Map<String, Object> props = new HashMap<>();
        extractProperties(schema, propName, props, "");
        return props;
    }

    private static void extractProperties(Schema schema, String propName, Map<String, Object> props, String prefix) {
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
                extractProperties(unionSchema, propName, props, itemId(prefix, unionSchema.getName()));
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
            if (propName != null) {
                if (schema.getObjectProp(propName) != null) {
                    props.put(prefix, schema.getObjectProp(propName));
                }
            } else {
                Map<String, Object> fieldProps = schema.getObjectProps();
                if (!fieldProps.isEmpty()) {
                    props.put(prefix, fieldProps);
                }
            }
            break;
        }
    }

    /**
     * Add the properties map to the given schema. The properties can be extracted using {@code ExtractAllProperties}.
     * The key of the map represents the path to the field in the schema, the value is another Map of key/value properties.
     *
     * @param schema to be enriched
     * @param props the properties map
     */
    public static void addAllProperties(Schema schema, Map<String, Object> props) {
        addProperties(schema, null, props);
    }

    /**
     * Add a property to a schema.
     *
     * @param schema to be enriched
     * @param propName is the name of the property
     * @param props a map that has the key represents the path to the field and the value is the value of the property
     */
    public static void addProperties(Schema schema, String propName, Map<String, Object> props) {
        Objects.requireNonNull(schema, "Input schema should not be null.");
        addProperties(schema, propName, props, "");
    }

    private static void addProperties(Schema schema, String propName, Map<String, Object> props, String prefix) {
        switch (schema.getType()) {
        case RECORD:
            for (Schema.Field field : schema.getFields()) {
                addProperties(field.schema(), propName, props, itemId(prefix, field.name()));
            }
            break;
        case ARRAY:
            addProperties(schema.getElementType(), propName, props, prefix);
            break;
        case MAP:
            addProperties(schema.getValueType(), propName, props, prefix);
            break;
        case UNION:
            for (Schema unionSchema : schema.getTypes()) {
                addProperties(unionSchema, propName, props, itemId(prefix, unionSchema.getName()));
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
            if (props.containsKey(prefix)) {
                if (propName != null) {
                    schema.addProp(propName, props.get(prefix));
                } else if (props.get(prefix) instanceof Map) {
                    for (Map.Entry<String, Object> entry : ((Map<String, Object>) props.get(prefix)).entrySet()) {
                        schema.addProp(entry.getKey(), entry.getValue());
                    }
                }
            }
            break;
        }
    }

    public static Pair<Stream<IndexedRecord>, Schema> streamAvroFile(File file) throws IOException {
        DataFileReader<GenericRecord> dateAvroReader = new DataFileReader<>(file, new GenericDatumReader<>());
        return Pair.of(StreamSupport.stream(dateAvroReader.spliterator(), false).map(c -> c),
                dateAvroReader.getSchema());
    }

    public static Schema dereferencing(Schema schema, boolean keepProps) {
        Stream names = getNamedTypes(schema);
        List<String> flattenNames = flattenStream(names);
        Set<String> distinctFlattenNames = new HashSet<>(flattenNames);

        if (distinctFlattenNames.size() != flattenNames.size()) {
            Map<String, String> namespaces = new HashMap<>();
            for (String name : distinctFlattenNames) {
                namespaces.put(name, "a"); //referenced namespaces will be suffixed by alphabet a, then b, then c, etc...
            }
            Schema dereferencedSchema = buildDereferencedSchema(schema, namespaces);
            if (keepProps) {
                Map<String, Object> props = extractAllProperties(schema);
                addAllProperties(dereferencedSchema, props);
            }
            return dereferencedSchema;
        }
        return schema;
    }

    public static Schema dereferencing(Schema schema) {
        return dereferencing(schema, false);
    }

    private static Stream getNamedTypes(Schema schema) {
        if (schema.getType() != RECORD)
            return Stream.empty();

        return schema.getFields().stream().flatMap(field -> {
            Schema fieldSchema = field.schema();
            switch (fieldSchema.getType()) {
            case RECORD:
                List recordNames = (List) getNamedTypes(fieldSchema).collect(Collectors.toCollection(ArrayList::new));
                recordNames.add(fieldSchema.getFullName());
                return recordNames.stream();
            case ARRAY:
                Stream arrayStream = getNamedTypes(fieldSchema.getElementType());
                if (arrayStream != null) {
                    List arrayNames = (List) arrayStream.collect(Collectors.toCollection(ArrayList::new));
                    arrayNames.add(fieldSchema.getElementType().getFullName());
                    return arrayNames.stream();
                } else {
                    return Stream.empty();
                }

            case MAP:
                Stream namedTypes = getNamedTypes(fieldSchema.getValueType());
                List mapNames = (List) namedTypes.collect(Collectors.toCollection(ArrayList::new));
                mapNames.add(fieldSchema.getValueType().getFullName());
                return mapNames.stream();
            case UNION:
                return fieldSchema.getTypes().stream().flatMap(unionSchema -> {
                    switch (unionSchema.getType()) {
                    case RECORD:
                        List unionRecordStream =
                                (List) getNamedTypes(unionSchema).collect(Collectors.toCollection(ArrayList::new));
                        unionRecordStream.add(unionSchema.getFullName());
                        return unionRecordStream.stream();
                    case ARRAY:
                        List unionArrayStream = (List) getNamedTypes(unionSchema.getElementType())
                                .collect(Collectors.toCollection(ArrayList::new));
                        if (unionSchema.getElementType().getType() == RECORD) {
                            unionArrayStream.add(unionSchema.getElementType().getFullName());
                        }
                        return unionArrayStream.stream();
                    case MAP:
                        List unionMapStream = (List) getNamedTypes(unionSchema.getValueType())
                                .collect(Collectors.toCollection(ArrayList::new));
                        unionMapStream.add(unionSchema.getValueType().getFullName());
                        return unionMapStream.stream();
                    case FIXED:
                        return Stream.of(fieldSchema.getFullName());
                    default:
                        return Stream.empty();
                    }
                });

            case ENUM:
                return Stream.of(fieldSchema.getFullName());
            default:
                return Stream.empty();
            }
        });
    }

    private static List<String> flattenStream(Stream stream) {
        List<String> flattenNames = new ArrayList<>();
        if (stream == null)
            return flattenNames;

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
                fieldAssembler.name(field.name()).type(buildDereferencedSchema(field.schema(), namespaces)).noDefault();
                break;
            case ARRAY:
                fieldAssembler
                        .name(field.name())
                        .type(Schema.createArray(dereferenceLeaf(fieldSchema.getElementType(), namespaces)))
                        .noDefault();
                break;
            case UNION:
                List<Schema> fullChild = fieldSchema.getTypes().stream().map(unionSchema -> {
                    switch (unionSchema.getType()) {
                    case RECORD:
                        return buildDereferencedSchema(unionSchema, namespaces);
                    case ARRAY:
                        Schema unionArraySchema;
                        if (unionSchema.getElementType().getType() != RECORD) {
                            unionArraySchema = dereferenceLeaf(unionSchema.getElementType(), namespaces);
                        } else {
                            unionArraySchema = buildDereferencedSchema(unionSchema.getElementType(), namespaces);
                        }
                        return Schema.createArray(unionArraySchema);
                    case MAP:
                        Schema unionMapSchema;
                        if (unionSchema.getValueType().getType() != RECORD) {
                            unionMapSchema = dereferenceLeaf(unionSchema.getValueType(), namespaces);
                        } else {
                            unionMapSchema = buildDereferencedSchema(unionSchema.getValueType(), namespaces);
                        }
                        return Schema.createMap(unionMapSchema);
                    default:
                        return unionSchema;
                    }
                }).collect(Collectors.toList());

                fieldAssembler.name(field.name()).type(Schema.createUnion(fullChild)).noDefault();
                break;
            case MAP:
                Schema mapSchema;
                if (fieldSchema.getValueType().getType() != RECORD) {
                    mapSchema = Schema.createMap(dereferenceLeaf(fieldSchema.getValueType(), namespaces));

                } else {
                    mapSchema = Schema.createMap(buildDereferencedSchema(fieldSchema.getValueType(), namespaces));
                }
                fieldAssembler.name(field.name()).type(mapSchema).noDefault();
                break;
            case ENUM:
                String enumNamespace = fieldSchema.getNamespace();
                if (namespaces.containsKey(fieldSchema.getFullName())) {
                    enumNamespace = enumNamespace + "." + namespaces.get(fieldSchema.getFullName());
                    namespaces.put(fieldSchema.getFullName(),
                            nextNamespaceSuffix(namespaces.get(fieldSchema.getFullName())));
                }
                fieldAssembler
                        .name(field.name())
                        .type(Schema.createEnum(field.name(), field.doc(), enumNamespace,
                                field.schema().getEnumSymbols()))
                        .noDefault();
                break;
            case FIXED:
            case STRING:
            case BYTES:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case BOOLEAN:
            case NULL:
                fieldAssembler.name(field.name()).type(field.schema()).noDefault();
                break;
            }
        }

        return fieldAssembler.endRecord();
    }

    private static Schema dereferenceLeaf(Schema fieldSchema, Map<String, String> namespaces) {
        switch (fieldSchema.getType()) {
        case RECORD:
            return buildDereferencedSchema(fieldSchema, namespaces);
        case ARRAY:
            return Schema.createArray(dereferenceLeaf(fieldSchema.getElementType(), namespaces));
        case ENUM:
            String enumNamespace = fieldSchema.getNamespace();
            if (namespaces.containsKey(fieldSchema.getFullName())) {
                enumNamespace = enumNamespace + "." + namespaces.get(fieldSchema.getFullName());
                namespaces.put(fieldSchema.getFullName(),
                        nextNamespaceSuffix(namespaces.get(fieldSchema.getFullName())));
            }
            return Schema.createEnum(fieldSchema.getName(), fieldSchema.getDoc(), enumNamespace,
                    fieldSchema.getEnumSymbols());
        }
        return fieldSchema;
    }

    private static String nextNamespaceSuffix(String suffix) {
        return encode(decode(suffix) + 1);
    }

    private static long decode(String input) {
        long value = 0;
        long multiplier = 1;
        for (char c : input.toCharArray()) {
            value += (c - 'a') * multiplier;
            multiplier *= 26;
        }
        return value;
    }

    private static String encode(long value) {
        StringBuilder output = new StringBuilder();
        do {
            long divide = value / 26;
            long remaining = value % 26;
            output.append((char) ('a' + remaining));
            value = divide;
        } while (value != 0);
        return output.toString();
    }

    /**
     * Remove properties for Avro Schema
     *
     * @param schema       schema to modify
     * @param propsToAvoid list of props names to remove
     * @return the schema without props
     */
    public static Schema cleanSchema(Schema schema, List<String> propsToAvoid) {
        if (schema != null) {
            switch (schema.getType()) {
            case RECORD:
                List<Schema.Field> fields = new ArrayList<>();
                for (Schema.Field field : schema.getFields()) {
                    Schema fieldSchema = cleanSchema(field.schema(), propsToAvoid);
                    fields.add(new Schema.Field(field.name(), fieldSchema, field.doc(), field.defaultVal()));
                }
                Schema recordSchema = Schema.createRecord(schema.getName(), schema.getDoc(), schema.getNamespace(),
                        schema.isError(), fields);
                addPropsToSchema(recordSchema, schema.getObjectProps(), propsToAvoid);
                return recordSchema;
            case MAP:
                Schema mapSchema = Schema.createMap(cleanSchema(schema.getValueType(), propsToAvoid));
                addPropsToSchema(mapSchema, schema.getObjectProps(), propsToAvoid);
                return mapSchema;
            case ARRAY:
                Schema arraySchema = Schema.createArray(cleanSchema(schema.getElementType(), propsToAvoid));
                addPropsToSchema(arraySchema, schema.getObjectProps(), propsToAvoid);
                return arraySchema;
            case UNION:
                List<Schema> types = new ArrayList<>();
                for (Schema unionType : schema.getTypes()) {
                    Schema unionTypeSchema = cleanSchema(unionType, propsToAvoid);
                    types.add(unionTypeSchema);
                }
                Schema unionSchema = Schema.createUnion(types);
                addPropsToSchema(unionSchema, schema.getObjectProps(), propsToAvoid);
                return unionSchema;
            case ENUM:
                Schema enumSchema = Schema.createEnum(schema.getName(), schema.getDoc(), schema.getNamespace(),
                        schema.getEnumSymbols());
                addPropsToSchema(enumSchema, schema.getObjectProps(), propsToAvoid);
                return enumSchema;
            case FIXED:
                Schema fixedSchema = Schema.createFixed(schema.getName(), schema.getDoc(), schema.getNamespace(),
                        schema.getFixedSize());
                addPropsToSchema(fixedSchema, schema.getObjectProps(), propsToAvoid);
                return fixedSchema;
            case INT:
            case LONG:
            case FLOAT:
            case BYTES:
            case STRING:
            case BOOLEAN:
            case DOUBLE:
            case NULL:
                Schema primitiveSchema = Schema.create(schema.getType());
                addPropsToSchema(primitiveSchema, schema.getObjectProps(), propsToAvoid);
                return primitiveSchema;

            }
        }
        return null;
    }

    private static void addPropsToSchema(Schema destinationSchema, Map<String, Object> propsToCopy,
            List<String> propsToAvoid) {
        propsToCopy.forEach((k, v) -> {
            if (!propsToAvoid.contains(k))
                destinationSchema.addProp(k, v);
        });
    }
}
