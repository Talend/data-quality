// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.statistics.type;

import static org.talend.dataquality.common.util.AvroUtils.cleanSchema;
import static org.talend.dataquality.common.util.AvroUtils.copySchema;
import static org.talend.dataquality.common.util.AvroUtils.createRecordSemanticSchema;
import static org.talend.dataquality.common.util.AvroUtils.dereferencing;
import static org.talend.dataquality.common.util.AvroUtils.itemId;
import static org.talend.dataquality.statistics.datetime.SystemDateTimePatternManager.isDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.talend.dataquality.common.inference.AvroAnalyzer;

import avro.shaded.com.google.common.collect.Maps;

/**
 * Data type quality analyzer for Avro records.
 *
 * How to use it:
 * <ul>
 * <li>create a new instance</li>
 * <li>initialize the instance with a schema containing the data type (see {@link #init(Schema)})</li>
 * <li>analyze records (see {@link #analyze(Stream<IndexedRecord>)}) as many times as needed</li>
 * <li>finally, get the global result (see {@link #getResult()})</li>
 * </ul>
 */
public class AvroDataTypeDiscoveryAnalyzer implements AvroAnalyzer {

    public static final String DATA_TYPE_AGGREGATE = "talend.component.dqType";

    public static final String MATCHINGS_FIELD = "matchings";

    public static final String DATA_TYPE_FIELD = "dataType";

    public static String TOTAL_FIELD = "total";

    private static final String DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA_JSON =
            "{\"type\": \"record\"," + "\"name\": \"discovery_metadata\", \"namespace\": \"org.talend.dataquality\","
                    + "\"fields\":[{ \"type\":\"string\", \"name\":\"dataType\"}]}";

    private static final List<LogicalType> DATE_RELATED_LOGICAL_TYPES =
            Arrays.asList(LogicalTypes.date(), LogicalTypes.timeMillis(), LogicalTypes.timeMicros(),
                    LogicalTypes.timestampMillis(), LogicalTypes.timestampMicros());

    public static final Schema DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA =
            new Schema.Parser().parse(DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA_JSON);

    private final Map<String, SortedList> frequentDatePatterns = new HashMap<>();

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
        dataTypeResults.clear();
    }

    @Override
    public void init(Schema semanticSchema) {
        init();
        Schema cleanSchema = cleanSchema(semanticSchema, Arrays.asList(DATA_TYPE_AGGREGATE));
        this.inputSemanticSchema = dereferencing(cleanSchema); // TODO create Data Type Schema
        this.outputSemanticSchema = copySchema(this.inputSemanticSchema);
        this.outputRecordSemanticSchema =
                createRecordSemanticSchema(this.inputSemanticSchema, DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA);
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

        final GenericRecord resultRecord = new GenericData.Record(outputRecordSemanticSchema);
        analyzeRecord("", record, resultRecord, inputSemanticSchema);
        return resultRecord;
    }

    private void analyzeRecord(String id, IndexedRecord record, GenericRecord resultRecord, Schema semanticSchema) {

        for (Schema.Field field : record.getSchema().getFields()) {
            final String itemId = itemId(id, field.name());
            final Schema.Field resultField = resultRecord.getSchema().getField(field.name());
            final Schema.Field semanticField = semanticSchema.getField(field.name());

            if (resultField != null)
                if (semanticField != null) {
                    final Object semRecord = analyzeItem(itemId, record.get(field.pos()), field.schema(),
                            resultField.schema(), semanticField.schema());
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
        case NULL:
            final GenericRecord semRecord = new GenericData.Record(DATA_TYPE_DISCOVERY_VALUE_LEVEL_SCHEMA);
            semRecord.put(DATA_TYPE_FIELD, analyzeLeafValue(itemId, item, itemSchema));
            return semRecord;

        default:
            throw new IllegalStateException("Unexpected value: " + itemSchema.getType());
        }
    }

    private DataTypeEnum analyzeLeafValue(String itemId, Object value, Schema itemSchema) {

        DataTypeEnum type;// STRING means we didn't find any native data types

        if (!frequentDatePatterns.containsKey(itemId))
            frequentDatePatterns.put(itemId, new SortedList());

        if (dataTypeResults.get(itemId) == null)
            dataTypeResults.put(itemId, new DataTypeOccurences());

        DataTypeOccurences dataType = dataTypeResults.get(itemId);

        if (value != null) {
            type = getNativeDataType(value, itemSchema);
            if ((DataTypeEnum.STRING.equals(type) && isDate(value.toString(), frequentDatePatterns.get(itemId)))
                    || isLogicalDate(itemSchema))
                type = DataTypeEnum.DATE;
        } else {
            type = DataTypeEnum.NULL;
        }

        dataType.increment(type);

        return type;
    }

    private DataTypeEnum getNativeDataType(Object itemValue, Schema itemSchema) {
        switch (itemSchema.getType()) {
        case BOOLEAN:
            return DataTypeEnum.BOOLEAN;
        case FLOAT:
        case DOUBLE:
            return DataTypeEnum.DOUBLE;
        case INT:
        case LONG:
            return DataTypeEnum.INTEGER;
        case NULL:
            return DataTypeEnum.NULL;
        default:
            return TypeInferenceUtils.getNativeDataType(itemValue.toString());
        }
    }

    private boolean isLogicalDate(Schema itemSchema) {
        LogicalType logicalType = LogicalTypes.fromSchemaIgnoreInvalid(itemSchema);
        return logicalType != null && DATE_RELATED_LOGICAL_TYPES.contains(logicalType);
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
     * Matchings will be updated for all existing dataTypeAggregates.
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
                    System.out.println("Failed to add prop to referenced type " + fieldName
                            + ". The analyzer is not supporting schema with referenced types.");
                }
            }
            break;
        }
    }

    @Override
    public List<Schema> getResults() {
        return Collections.singletonList(getResult());
    }

    @Override
    public void close() throws Exception {

    }
}
