package org.talend.dataquality.datamasking.generic;

/**
 *
 */
public class FieldDefinition {

    public enum FieldDefinitionType {
        DATEPATTERN("DATEPATTERN"),
        INTERVAL("INTERVAL"),
        ENUMERATION("ENUMERATION"),
        ENUMERATION_FROM_FILE("ENUMERATION_FROM_FILE");

        private String componentValue;

        FieldDefinitionType(String componentValue) {
            this.componentValue = componentValue;
        }

        public String getComponentValue() {
            return componentValue;
        }

        public static FieldDefinitionType getTypeByComponentValue(String typeName) {
            for (FieldDefinitionType v : FieldDefinitionType.values()) {
                if (v.getComponentValue().equals(typeName)) {
                    return v;
                }
            }
            throw new IllegalArgumentException("Unknown FieldDefinitionType: " + typeName);
        }
    }

    private FieldDefinitionType type;

    private String value;

    private Long min;

    private Long max;

    public FieldDefinition(String type, String value, Long min, Long max) {
        this.type = FieldDefinitionType.getTypeByComponentValue(type);
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public FieldDefinitionType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Long getMin() {
        return min;
    }

    public Long getMax() {
        return max;
    }

}
