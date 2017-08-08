package org.talend.dataquality.datamasking.generic;

/**
 *
 */
public class FieldDefinition {

    public enum FieldDefinitionType {
        CONSTANT("Constant"),
        INTERVAL("Interval"),
        ENUMERATION("Enumeration");

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

    private Integer min;

    private Integer max;

    public FieldDefinition(String type, String value, Integer min, Integer max) {
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

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

}
