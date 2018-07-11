package org.talend.dataquality.jp.transliteration;

public enum TransliterateType {
    HIRAGANA("Hiragana"),
    KATAKANA_READING("Katakana-reading"),
    KATAKANA_PRONUNCIATION("Katakana-pronunciation"),
    HEPBURN("Hepburn-romanization"),
    KUNREI_SHIKI("Kunrei-shiki-romanization"),
    NIHON_SHIKI("Nihon-shiki-romanization");

    private final String typeName;

    public String getTypeName() {
        return typeName;
    }

    TransliterateType(String typeName) {
        this.typeName = typeName;
    }
}