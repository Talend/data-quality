package org.talend.dataquality.common.inference;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.talend.dataquality.common.exception.DQCommonRuntimeException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.talend.dataquality.common.util.AvroUtils.itemId;

public abstract class AvroQualityAnalyzer implements AvroAnalyzer {

    private static final long serialVersionUID = 7878661383777406934L;

    public static final String GLOBAL_QUALITY_PROP_NAME = "talend.component.globalQuality";

    public static final String QUALITY_PROP_NAME = "talend.component.qualityAggregate";

    public static final String DQTYPE_PROP_NAME = "talend.component.dqType";

    public static final String DQTYPE_DATA_TYPE_FIELD_NAME = "dataType";

    public static final String DQTYPE_DQTYPE_FIELD_NAME = "dqType";

    public static final String VALIDITY_FIELD_NAME = "validity";

    private static final String QUALITY_VALUE_LEVEL_SCHEMA_DEF =
            "{ \"type\": \"record\"," + "    \"name\": \"quality_metadata\", \"namespace\": \"org.talend.dataquality\","
                    + "    \"fields\": [ { \"name\": \"validity\", \"type\": \"int\" } ] }";

    public static final Schema QUALITY_VALUE_LEVEL_SCHEMA = new Schema.Parser().parse(QUALITY_VALUE_LEVEL_SCHEMA_DEF);

    public static final int VALID_VALUE = 1;

    public static final int INVALID_VALUE = -1;

    public static final int EMPTY_VALUE = 0;

    protected boolean isStoreInvalidValues = true;

    protected final Map<String, ValueQualityStatistics> qualityResults = new HashMap<>();

    /** Semantic schema with data and qd types metadata. */
    protected Schema inputSemanticSchema;

    /** Semantic Schema with data and qd types metadata and quality metadata. */
    protected Schema outputSemanticSchema;

    /** Schema schema for value level metadata for the records containing the results of the analysis of each field. */
    protected Schema outputRecordSemanticSchema;

    protected <T> T getOrCreate(String key, Map<String, T> map, Class<T> itemClass) {
        T value = map.get(key);

        if (value == null) {
            try {
                value = itemClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new DQCommonRuntimeException("Unable to get create an instance of " + itemClass, e);
            }

            map.put(key, value);
        }

        return value;
    }

    @Override
    public Schema getResult() {
        if (inputSemanticSchema == null) {
            return null;
        }

        final ValueQualityStatistics stats = qualityResults.values().stream().reduce(new ValueQualityStatistics(),
                ValueQualityStatistics::mergeCounts);

        // Create a deep copy of the semantic schema (without "talend.component.dqType")
        // Update it with the information about the quality

        outputSemanticSchema.addProp(GLOBAL_QUALITY_PROP_NAME, stats.toMap());

        for (Schema.Field field : outputSemanticSchema.getFields()) {
            updateQuality(field.schema(), field.name());
        }

        return outputSemanticSchema;
    }

    private Map<String, Long> getStatMap(String resultKey) {
        if (qualityResults.containsKey(resultKey)) {
            return qualityResults.get(resultKey).toMap();
        }

        return new ValueQualityStatistics().toMap();
    }

    private Schema updateQuality(Schema sourceSchema, String prefix) {
        switch (sourceSchema.getType()) {
        case RECORD:
            for (Schema.Field field : sourceSchema.getFields()) {
                updateQuality(field.schema(), itemId(prefix, field.name()));
            }
            break;

        case ARRAY:
            updateQuality(sourceSchema.getElementType(), prefix);
            break;

        case MAP:
            updateQuality(sourceSchema.getValueType(), prefix);
            break;

        case UNION:
            if (qualityResults.containsKey(prefix)) {
                try {
                    sourceSchema.addProp(QUALITY_PROP_NAME, getStatMap(prefix));
                } catch (AvroRuntimeException e) {
                    System.out.println("Failed to add prop to field " + sourceSchema.getName() + ".");
                }
            }
            for (Schema unionSchema : sourceSchema.getTypes()) {
                updateQuality(unionSchema, itemId(prefix, unionSchema.getName()));
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
            try {
                sourceSchema.addProp(QUALITY_PROP_NAME, getStatMap(prefix));
            } catch (AvroRuntimeException e) {
                System.out.println("Failed to add prop to referenced type " + sourceSchema.getName()
                        + ". The analyzer is not supporting schema with referenced types.");
            }
            break;
        }

        return null;
    }

    @Override
    public List<Schema> getResults() {
        return Collections.singletonList(getResult());
    }

    @Override
    public void close() throws Exception {
        // Nothing to do
    }
}
