package org.talend.dataquality.statistics.type;

import static org.talend.dataquality.common.util.AvroUtils.SEM_DISCOVERY_DATATYPE_SCHEMA;
import static org.talend.dataquality.common.util.AvroUtils.SEM_DISCOVERY_SCHEMA;
import static org.talend.dataquality.common.util.AvroUtils.itemId;
import static org.talend.dataquality.statistics.datetime.SystemDateTimePatternManager.isDate;

import java.util.*;
import java.util.stream.Stream;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.talend.dataquality.common.inference.AvroAnalyzer;
import org.talend.dataquality.common.util.LFUCache;

public class AvroDataTypeAnalyzer implements AvroAnalyzer {

    public static final String DATA_TYPE_AGGREGATE = "dataTypeAggregate";

    public static final String DATA_TYPE = "dataType";

    public static String TOTAL = "total";

    private final Map<String, SortedList> frequentDatePatterns = new HashMap<>();

    private final Map<String, LFUCache> knownDataTypeCaches = new HashMap<>();

    private final Map<String, DataTypeOccurences> dataTypeResults = new HashMap<>();

    private Schema semanticSchema;

    private Schema recordDataTypeSchema;

    @Override
    public void init() {
        frequentDatePatterns.clear();
        knownDataTypeCaches.clear();
    }

    @Override
    public void init(Schema schema) {
        this.semanticSchema = schema; // TODO create Data Type Schema
        this.recordDataTypeSchema = schema;
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

        final GenericRecord resultRecord = new GenericData.Record(recordDataTypeSchema);
        analyzeRecord("", record, resultRecord, semanticSchema);
        return resultRecord;
    }

    private void analyzeRecord(String id, IndexedRecord record, GenericRecord resultRecord, Schema semanticSchema) {
        final Schema schema = record.getSchema();

        for (Schema.Field field : schema.getFields()) {
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
                resultArray.add(analyzeItem(itemId, obj, itemSchema.getElementType(), resultSchema.getElementType(),
                        fieldSemanticSchema.getElementType()));
            }
            return new GenericData.Array(resultSchema, resultArray);

        case MAP:
            final Map<String, Object> itemMap = (Map) item;
            final Map<String, Object> resultMap = new HashMap<>();
            for (Map.Entry<String, Object> itemValue : itemMap.entrySet()) {
                resultMap.put(itemValue.getKey(), analyzeItem(itemId, itemValue.getValue(), itemSchema.getValueType(),
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
                    .orElse(SEM_DISCOVERY_SCHEMA);
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
            final GenericRecord semRecord = new GenericData.Record(SEM_DISCOVERY_DATATYPE_SCHEMA);
            semRecord.put(DATA_TYPE, analyzeLeafValue(itemId, item));
            return semRecord;

        case NULL:
            // No information in semantic schema
            final GenericRecord nullSemRecord = new GenericData.Record(SEM_DISCOVERY_DATATYPE_SCHEMA);
            nullSemRecord.put(DATA_TYPE, analyzeLeafValue(itemId, item));
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
        if (recordDataTypeSchema == null) {
            return null;
        }

        for (Schema.Field field : recordDataTypeSchema.getFields()) {
            updateDatatype(field.schema(), field.name());
        }
        return recordDataTypeSchema;
    }

    private void updateDatatype(Schema schema, String fieldName) {
        switch (schema.getType()) {
        case RECORD:
            for (Schema.Field field : schema.getFields()) {
                updateDatatype(field.schema(), itemId(fieldName, field.name()));
            }
            break;

        case UNION:
            if (dataTypeResults.containsKey(fieldName)) {
                try {
                    schema.addProp(DATA_TYPE, dataTypeResults.get(fieldName).getTypeFrequencies());
                } catch (AvroRuntimeException e) {
                    System.out.println("Failed to add prop to field " + fieldName + ".");
                }
            }
            for (Schema unionSchema : schema.getTypes()) {
                updateDatatype(unionSchema, itemId(fieldName, unionSchema.getName()));
            }
            break;

        case ARRAY:
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
        case NULL:
            if (dataTypeResults.containsKey(fieldName)) {

                List<Map<String, Object>> res = new ArrayList<>();

                dataTypeResults.get(fieldName).getTypeFrequencies().forEach((key, value) -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put(DATA_TYPE, key);
                    result.put(TOTAL, value);

                    res.add(result);
                });

                try {
                    schema.addProp(DATA_TYPE_AGGREGATE, res);
                } catch (AvroRuntimeException e) {
                    System.out.println("Failed to add prop to referenced type " + fieldName
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
