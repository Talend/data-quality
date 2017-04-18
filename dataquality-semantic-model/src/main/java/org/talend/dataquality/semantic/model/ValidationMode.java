package org.talend.dataquality.semantic.model;

public enum ValidationMode {

    SIMPLIFIED("simplified"),
    EXACT("exact"),
    EXACT_IGNORE_CASE_AND_ACCENT("exactIgnoreCaseAndAccent");

    private String technicalName;

    private ValidationMode(String technicalName) {
        this.technicalName = technicalName;
    }

    public String getTechnicalName() {
        return technicalName;
    }
}
