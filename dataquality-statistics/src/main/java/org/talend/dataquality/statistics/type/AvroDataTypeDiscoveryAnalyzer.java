package org.talend.dataquality.statistics.type;

import static java.util.stream.Collectors.toList;
import static org.apache.avro.Schema.Type.RECORD;
import static org.talend.dataquality.common.util.AvroUtils.copySchema;
import static org.talend.dataquality.common.util.AvroUtils.createRecordSemanticSchema;
import static org.talend.dataquality.common.util.AvroUtils.itemId;
import static org.talend.dataquality.statistics.datetime.SystemDateTimePatternManager.isDate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.talend.dataquality.common.inference.AvroAnalyzer;
import org.talend.dataquality.common.util.LFUCache;

import avro.shaded.com.google.common.collect.Maps;

/**
 *
 */
public class AvroDataTypeDiscoveryAnalyzer implements AvroAnalyzer {

    public static final String DATA_TYPE_AGGREGATE = "talend.component.dqType";

    public static final String MATCHINGS_FIELD = "matchings";

    public static final String FORCED_FIELD = "forced";

    public static final String DATA_TYPE_FIELD = "dataType";

    public static String TOTAL_FIELD = "total";

    private static final String DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA_JSON =
            "{\"type\": \"record\"," + "\"name\": \"discovery_metadata\", \"namespace\": \"org.talend.dataquality\","
                    + "\"fields\":[{ \"type\":\"string\", \"name\":\"dataType\"}]}";

    public static final Schema DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA =
            new Schema.Parser().parse(DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA_JSON);

    private final Map<String, SortedList> frequentDatePatterns = new HashMap<>();

    private final Map<String, LFUCache> knownDataTypeCaches = new HashMap<>();

    private final Map<String, DataTypeOccurences> dataTypeResults = new HashMap<>();

    private Schema inputSemanticSchema;

    private Schema outputSemanticSchema;

    private Schema outputRecordSemanticSchema;

    static final Comparator<Map.Entry<DataTypeEnum, Long>> entryComparator = (t0, t1) -> {
        int dataTypeEnumComparaison = DataTypeEnum.dataTypeEnumComparator.compare(t0.getKey(), t1.getKey());
        return dataTypeEnumComparaison != 0 ? dataTypeEnumComparaison : t0.getValue().compareTo(t1.getValue());
    };

    @Override
    public void init() {
        frequentDatePatterns.clear();
        knownDataTypeCaches.clear();
    }

    @Override
    public void init(Schema semanticSchema) {
        this.inputSemanticSchema = dereferencing(semanticSchema); // TODO create Data Type Schema
        this.outputSemanticSchema = copySchema(this.inputSemanticSchema);
        this.outputRecordSemanticSchema =
                createRecordSemanticSchema(this.inputSemanticSchema, DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA);
    }

    private Stream getNamedTypes(Schema schema) {
        return schema.getFields().stream().flatMap(field -> {
            Schema fieldSchema = field.schema();
            switch (fieldSchema.getType()) {
            case RECORD:
                return Stream.of(getNamedTypes(fieldSchema), fieldSchema.getFullName());
            case ARRAY:
                if (fieldSchema.getElementType().getType() == RECORD) {
                    return Stream.of(getNamedTypes(fieldSchema.getElementType()));
                } else {
                    return Stream.empty();
                }
            case MAP:
                if (fieldSchema.getValueType().getType() == RECORD) {
                    return Stream.of(getNamedTypes(fieldSchema.getValueType()));
                } else {
                    return Stream.empty();
                }
            case UNION:
                return fieldSchema.getTypes().stream().flatMap(unionSchema -> {
                    switch (unionSchema.getType()) {
                    case RECORD:
                        return Stream.of(getNamedTypes(unionSchema), unionSchema.getFullName());
                    case ARRAY:
                        if (unionSchema.getElementType().getType() == RECORD) {
                            return Stream.of(getNamedTypes(unionSchema.getElementType()));
                        } else {
                            return null;
                        }
                    case MAP:
                        if (unionSchema.getValueType().getType() == RECORD) {
                            return Stream.of(getNamedTypes(unionSchema.getValueType()));
                        } else {
                            return null;
                        }
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

    private List<String> flattenStream(Stream stream) {
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

    private Schema dereferencing(Schema schema) {
        Schema dereferencedSchema = schema;
        Stream names = getNamedTypes(schema);

        List<String> flattenNames = flattenStream(names);

        int beforeDistinct = flattenNames.size();
        flattenNames = flattenNames.stream().distinct().collect(toList());

        if (!flattenNames.isEmpty() && beforeDistinct != flattenNames.size()) {
            Map<String, Integer> namespaces = new HashMap<>();
            for (String name : flattenNames) {
                namespaces.put(name, 0);
            }
            return buildDereferencedSchema(schema, namespaces);
        }
        return dereferencedSchema;
    }

    private Schema buildDereferencedSchema(Schema schema, Map<String, Integer> namespaces) {

        final SchemaBuilder.RecordBuilder<Schema> qualityRecordBuilder =
                SchemaBuilder.record(schema.getName()).namespace(schema.getNamespace());
        final SchemaBuilder.FieldAssembler<Schema> fieldAssembler = qualityRecordBuilder.fields();

        for (Schema.Field field : schema.getFields()) {
            Schema fieldSchema = field.schema();
            switch (fieldSchema.getType()) {
            case RECORD:
                fieldAssembler.name(field.name()).type(buildDereferencedSchema(field.schema(), namespaces)).noDefault();
                break;
            case ARRAY:
                if (fieldSchema.getElementType().getType() == Schema.Type.STRING) {
                    fieldAssembler.name(field.name()).type(fieldSchema).noDefault();
                } else {
                    fieldAssembler
                            .name(field.name())
                            .type(buildDereferencedSchema(fieldSchema.getElementType(), namespaces))
                            .noDefault();
                }
                break;
            case UNION:

                List<Schema> fullChild = fieldSchema.getTypes().stream().map(unionSchema -> {
                    String fullName = unionSchema.getFullName();
                    if (namespaces.containsKey(fullName)) {
                        List<Schema.Field> cloneFields = new ArrayList<>();

                        for (Schema.Field f : unionSchema.getFields()) {
                            Schema.Field _field = new Schema.Field(f.name(), f.schema(), f.doc(), f.defaultValue());
                            cloneFields.add(_field);
                        }

                        Schema newSchema = Schema
                                .createRecord(unionSchema.getName(), unionSchema.getDoc(),
                                        unionSchema.getNamespace() + "." + namespaces.get(fullName),
                                        unionSchema.isError(), cloneFields);
                        namespaces.put(fullName, namespaces.get(fullName) + 1);
                        return newSchema;

                    } else {
                        return unionSchema;
                    }
                }).collect(Collectors.toList());
                fieldAssembler.name(field.name()).type(Schema.createUnion(fullChild)).noDefault();
                break;
            case MAP:
            case ENUM:
            case FIXED:
            case STRING:
            case BYTES:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case BOOLEAN:
                fieldAssembler.name(field.name()).type(field.schema()).noDefault();
                break;
            case NULL:
                break;
            }
        }

        return fieldAssembler.endRecord();
    }

    @Override
    public boolean analyze(IndexedRecord record) {
        analyzeRecord(record);
        return true;
    }

    @Override
    public Stream<IndexedRecord> analyze(Stream<IndexedRecord> records) {
        return records.map(this::analyzeRecord);
    }

    private IndexedRecord analyzeRecord(IndexedRecord record) {
        if (record == null) {
            return null;
        }

        final GenericRecord resultRecord = new GenericData.Record(outputRecordSemanticSchema);
        analyzeRecord("", record, resultRecord, inputSemanticSchema);
        return resultRecord;
    }

    private void analyzeRecord(String id, IndexedRecord record, GenericRecord resultRecord, Schema semanticSchema) {

        for (Schema.Field field : record.getSchema().getFields()) {
            final String itemId = itemId(id, field.name());
            final Optional<Schema> maybeFieldResultSchema =
                    Optional.ofNullable(resultRecord.getSchema().getField(field.name())).map(Schema.Field::schema);
            final Optional<Schema> maybeFieldSemanticSchema =
                    Optional.ofNullable(semanticSchema.getField(field.name())).map(Schema.Field::schema);

            if (maybeFieldResultSchema.isPresent())
                if (maybeFieldSemanticSchema.isPresent()) {
                    final Object semRecord = analyzeItem(itemId, record.get(field.pos()), field.schema(),
                            maybeFieldResultSchema.get(), maybeFieldSemanticSchema.get());
                    resultRecord.put(field.name(), semRecord);
                } else {
                    System.out.println(field.name() + " field is missing from semantic schema.");
                }
            else {
                System.out.println(field.name() + " field is missing from result record schema.");
            }
        }

    }

    private Object analyzeItem(String itemId, Object item, Schema itemSchema, Schema resultSchema,
            Schema fieldSemanticSchema) {

        switch (itemSchema.getType()) {
        case RECORD:
            final GenericRecord resultRecord = new GenericData.Record(resultSchema);
            analyzeRecord(itemId, (GenericRecord) item, resultRecord, fieldSemanticSchema);
            return resultRecord;

        case ARRAY:
            final List resultArray = new ArrayList();
            for (Object obj : (List) item) {
                resultArray
                        .add(analyzeItem(itemId, obj, itemSchema.getElementType(), resultSchema.getElementType(),
                                fieldSemanticSchema.getElementType()));
            }
            return new GenericData.Array(resultSchema, resultArray);

        case MAP:
            final Map<String, Object> itemMap = (Map) item;
            final Map<String, Object> resultMap = new HashMap<>();
            for (Map.Entry<String, Object> itemValue : itemMap.entrySet()) {
                resultMap
                        .put(itemValue.getKey(), analyzeItem(itemId, itemValue.getValue(), itemSchema.getValueType(),
                                resultSchema.getValueType(), fieldSemanticSchema.getValueType()));
            }
            return resultMap;

        case UNION:
            final int typeIdx = new GenericData().resolveUnion(itemSchema, item);
            final List<Schema> unionSchemas = itemSchema.getTypes();
            final Schema realItemSchema = unionSchemas.get(typeIdx);
            final Schema realResultSchema = resultSchema
                    .getTypes()
                    .stream()
                    .filter((type) -> type.getName().equals(realItemSchema.getName()))
                    .findFirst()
                    .orElse(DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA);
            final Schema realSemanticSchema = fieldSemanticSchema.getTypes().get(typeIdx);

            return analyzeItem(itemId(itemId, realItemSchema.getName()), item, realItemSchema, realResultSchema,
                    realSemanticSchema);

        case ENUM:
        case FIXED:
        case STRING:
        case BYTES:
        case INT:
        case LONG:
        case FLOAT:
        case DOUBLE:
        case BOOLEAN:
            final GenericRecord semRecord = new GenericData.Record(DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA);
            semRecord.put(DATA_TYPE_FIELD, analyzeLeafValue(itemId, item));
            return semRecord;

        case NULL:
            // No information in semantic schema
            final GenericRecord nullSemRecord = new GenericData.Record(DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA);
            nullSemRecord.put(DATA_TYPE_FIELD, analyzeLeafValue(itemId, item));
            return nullSemRecord;

        default:
            throw new IllegalStateException("Unexpected value: " + itemSchema.getType());
        }
    }

    private Object analyzeLeafValue(String itemId, Object value) {

        DataTypeEnum type = DataTypeEnum.STRING;// STRING means we didn't find any native data types

        if (!frequentDatePatterns.containsKey(itemId))
            frequentDatePatterns.put(itemId, new SortedList());

        if (dataTypeResults.get(itemId) == null) {
            dataTypeResults.put(itemId, new DataTypeOccurences());
        }
        DataTypeOccurences dataType = dataTypeResults.get(itemId);

        LFUCache<String, DataTypeEnum> knownDataTypeCache = knownDataTypeCaches.get(value);
        if (knownDataTypeCache == null)
            knownDataTypeCache = new LFUCache<>();
        final DataTypeEnum knownDataType = knownDataTypeCache.get(value);

        if (knownDataType != null) {
            dataType.increment(knownDataType);
            type = knownDataType;
        } else {
            if (value != null) {
                type = TypeInferenceUtils.getNativeDataType(value.toString());
                if (DataTypeEnum.STRING.equals(type) && isDate(value.toString(), frequentDatePatterns.get(itemId)))
                    type = DataTypeEnum.DATE;
                knownDataTypeCache.put(itemId, type);
                dataType.increment(type);
            }
        }
        return type;
    }

    @Override
    public Schema getResult() {
        if (outputSemanticSchema == null) {
            return null;
        }

        for (Schema.Field field : outputSemanticSchema.getFields()) {
            updateDatatype(field.schema(), field.name());
        }
        return outputSemanticSchema;
    }

    /**
     * Merge the matchings and the new discovered data types into the input semantic schema.
     * Matchings will be updated for all existing dataTypeAggregates, if the dataType is forced, it won't be updated.
     * 
     * @param schema
     * @param fieldName
     */
    private void updateDatatype(Schema schema, String fieldName) {
        switch (schema.getType()) {
        case RECORD:
            for (Schema.Field field : schema.getFields()) {
                updateDatatype(field.schema(), itemId(fieldName, field.name()));
            }
            break;

        case ARRAY:
            updateDatatype(schema.getElementType(), fieldName);
            break;

        case MAP:
            updateDatatype(schema.getValueType(), fieldName);
            break;

        case UNION:
            if (dataTypeResults.containsKey(fieldName)) {
                try {
                    schema.addProp(DATA_TYPE_FIELD, dataTypeResults.get(fieldName).getTypeFrequencies());
                } catch (AvroRuntimeException e) {
                    System.out.println("Failed to add prop to field " + fieldName + ".");
                }
            }
            for (Schema unionSchema : schema.getTypes()) {
                updateDatatype(unionSchema, itemId(fieldName, unionSchema.getName()));
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
            if (dataTypeResults.containsKey(fieldName)) {

                Map<String, Object> aggregate = Maps.newHashMap();

                dataTypeResults
                        .get(fieldName)
                        .getTypeFrequencies()
                        .entrySet()
                        .stream()
                        .filter(entry -> !entry.getKey().equals(DataTypeEnum.EMPTY))
                        .max(entryComparator)
                        .map(entry -> {
                            aggregate.put(FORCED_FIELD, false);
                            aggregate.put(DATA_TYPE_FIELD, entry.getKey());
                            return null;
                        });

                List<Map<String, Object>> matchings = new ArrayList<>();
                dataTypeResults.get(fieldName).getTypeFrequencies().forEach((key, value) -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put(DATA_TYPE_FIELD, key);
                    result.put(TOTAL_FIELD, value);
                    matchings.add(result);
                });
                aggregate.put(MATCHINGS_FIELD, matchings);

                try {
                    schema.addProp(DATA_TYPE_AGGREGATE, aggregate);
                } catch (AvroRuntimeException e) {
                    System.out
                            .println("Failed to add prop to referenced type " + fieldName
                                    + ". The analyzer is not supporting schema with referenced types.");
                }
            }
            break;
        }
    }

    @Override
    public List<Schema> getResults() {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
