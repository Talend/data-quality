package org.talend.dataquality.statistics.quality;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.talend.dataquality.common.inference.AvroQualityAnalyzer;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.common.util.LFUCache;
import org.talend.dataquality.statistics.type.DataTypeEnum;
import org.talend.dataquality.statistics.type.SortedList;
import org.talend.dataquality.statistics.type.TypeInferenceUtils;

import java.util.*;
import java.util.stream.Stream;

import static org.talend.dataquality.common.util.AvroUtils.copySchema;
import static org.talend.dataquality.common.util.AvroUtils.createRecordSemanticSchema;
import static org.talend.dataquality.common.util.AvroUtils.itemId;
import static org.talend.dataquality.statistics.datetime.SystemDateTimePatternManager.isDate;

/**
 * Data type quality analyzer for Avro records.
 *
 * How to use it:
 * <ul>
 *     <li>create a new instance</li>
 *     <li>initialize the instance with a schema containing the data type (see {@link #init(Schema)})</li>
 *     <li>analyze records (see {@link #analyze(Stream<IndexedRecord>)}) as many times as needed: it returns a list of
 *         records with the result of the analysis</li>
 *     <li>finally, get the global result (see {@link #getResult()})</li>
 * </ul>
 */
public class AvroDataTypeQualityAnalyzer extends AvroQualityAnalyzer {

    private static final long serialVersionUID = 6687921563928212180L;

    private final Map<String, LFUCache> knownDataTypeCaches = new HashMap<>();

    private final Map<String, SortedList> frequentDatePatterns = new HashMap<>();

    public AvroDataTypeQualityAnalyzer(boolean isStoreInvalidValues) {
        this.isStoreInvalidValues = isStoreInvalidValues;
    }

    public AvroDataTypeQualityAnalyzer() {
        this(true);
    }

    @Override
    public void init() {
        frequentDatePatterns.clear();
        qualityResults.clear();
        knownDataTypeCaches.clear();
    }

    @Override
    public void init(Schema semanticSchema) {
        init();
        initResultSchema(semanticSchema);
    }

    private void initResultSchema(Schema semanticSchema) {
        this.inputSemanticSchema = semanticSchema;
        this.outputSemanticSchema = copySchema(this.inputSemanticSchema);
        this.outputRecordSemanticSchema =
                createRecordSemanticSchema(this.inputSemanticSchema, QUALITY_VALUE_LEVEL_SCHEMA);
    }

    private void analyzeRecord(String id, IndexedRecord record, GenericRecord resultRecord,
            Schema recordSemanticSchema) {
        final Schema schema = record.getSchema();

        for (Schema.Field field : schema.getFields()) {
            final String itemId = itemId(id, field.name());
            final Optional<Schema> maybeFieldResultSchema =
                    Optional.ofNullable(resultRecord.getSchema().getField(field.name())).map(Schema.Field::schema);
            final Optional<Schema> maybeFieldSemanticSchema =
                    Optional.ofNullable(recordSemanticSchema.getField(field.name())).map(Schema.Field::schema);

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
            Schema semanticSchema) {

        switch (itemSchema.getType()) {
        case RECORD:
            final GenericRecord resultRecord = new GenericData.Record(resultSchema);
            analyzeRecord(itemId, (GenericRecord) item, resultRecord, semanticSchema);
            return resultRecord;

        case ARRAY:
            final List resultArray = new ArrayList();
            for (Object obj : (List) item) {
                resultArray.add(analyzeItem(itemId, obj, itemSchema.getElementType(), resultSchema.getElementType(),
                        semanticSchema.getElementType()));
            }
            return new GenericData.Array(resultSchema, resultArray);

        case MAP:
            final Map<String, Object> itemMap = (Map) item;
            final Map<String, Object> resultMap = new HashMap<>();
            for (Map.Entry<String, Object> itemValue : itemMap.entrySet()) {
                resultMap.put(itemValue.getKey(), analyzeItem(itemId, itemValue.getValue(), itemSchema.getValueType(),
                        resultSchema.getValueType(), semanticSchema.getValueType()));
            }
            return resultMap;

        case UNION:
            //TODO: doit-on faire la différence entre les différents types pour une union ? => ICI NON
            final int typeIdx = new GenericData().resolveUnion(itemSchema, item);
            final List<Schema> unionSchemas = itemSchema.getTypes();
            final Schema realItemSchema = unionSchemas.get(typeIdx);
            final Schema realResultSchema = resultSchema
                    .getTypes()
                    .stream()
                    .filter((type) -> type.getName().equals(realItemSchema.getName()))
                    .findFirst()
                    .orElse(QUALITY_VALUE_LEVEL_SCHEMA);
            final Schema realSemanticSchema = semanticSchema.getTypes().get(typeIdx);

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
            final Optional<Map> maybeProps = Optional.ofNullable((Map) semanticSchema.getObjectProp(DQTYPE_PROP_NAME));
            final GenericRecord semRecord = new GenericData.Record(QUALITY_VALUE_LEVEL_SCHEMA);
            String semanticType =
                    maybeProps.map(props -> props.get(DQTYPE_DATA_TYPE_FIELD_NAME).toString()).orElse(null);
            semRecord.put(VALIDITY_FIELD_NAME, analyzeLeafValue(itemId, item, semanticType));
            return semRecord;

        case NULL:
            // No information in semantic schema
            final GenericRecord nullSemRecord = new GenericData.Record(QUALITY_VALUE_LEVEL_SCHEMA);
            nullSemRecord.put(VALIDITY_FIELD_NAME, analyzeLeafValue(itemId, item, null));
            return nullSemRecord;

        default:
            throw new IllegalStateException("Unexpected value: " + itemSchema.getType());
        }
    }

    private int analyzeLeafValue(String id, Object objValue, String semanticType) {
        final LFUCache<String, Boolean> knownDataTypeCache = getOrCreate(id, knownDataTypeCaches, LFUCache.class);
        final String value = objValue == null ? "" : objValue.toString();
        final Boolean knownDataType = knownDataTypeCache.get(value);
        final ValueQualityStatistics valueQuality = getOrCreate(id, qualityResults, ValueQualityStatistics.class);

        if (!frequentDatePatterns.containsKey(id))
            frequentDatePatterns.put(id, new SortedList());

        if (knownDataType != null) {
            if (knownDataType) {
                valueQuality.incrementValid();
                return VALID_VALUE;
            } else {
                valueQuality.incrementInvalid();
                processInvalidValue(valueQuality, value);
                return INVALID_VALUE;
            }
        } else {
            if (TypeInferenceUtils.isEmpty(value)) {
                valueQuality.incrementEmpty();
                return EMPTY_VALUE;
            } else if (semanticType == null) {
                // None empty values for field without dqType are considered valid.
                valueQuality.incrementValid();
                return VALID_VALUE;
            } else if (DataTypeEnum.DATE == DataTypeEnum.valueOf(semanticType)
                    && isDate(value, frequentDatePatterns.get(id))) {
                valueQuality.incrementValid();
                knownDataTypeCache.put(value, Boolean.TRUE);
                return VALID_VALUE;
            } else if (TypeInferenceUtils.isValid(DataTypeEnum.valueOf(semanticType), value)) {
                valueQuality.incrementValid();
                knownDataTypeCache.put(value, Boolean.TRUE);
                return VALID_VALUE;
            } else {
                valueQuality.incrementInvalid();
                processInvalidValue(valueQuality, value);
                knownDataTypeCache.put(value, Boolean.FALSE);
                return INVALID_VALUE;
            }
        }
    }

    private void processInvalidValue(ValueQualityStatistics valueQuality, String invalidValue) {
        if (isStoreInvalidValues) {
            valueQuality.appendInvalidValue(invalidValue);
        }
    }

    @Override
    public boolean analyze(IndexedRecord record) {
        analyzeRecord(record);
        return true;
    }

    @Override
    public Stream<IndexedRecord> analyze(Stream<IndexedRecord> records) {
        return records.sequential().map(this::analyzeRecord);
    }

    private IndexedRecord analyzeRecord(IndexedRecord record) {
        if (record == null) {
            return null;
        }

        if (this.inputSemanticSchema == null) {
            initResultSchema(record.getSchema());
        }

        final GenericRecord resultRecord = new GenericData.Record(outputRecordSemanticSchema);
        analyzeRecord("", record, resultRecord, inputSemanticSchema);

        return resultRecord;
    }
}
