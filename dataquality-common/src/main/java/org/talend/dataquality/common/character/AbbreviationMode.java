package org.talend.dataquality.common.character;

import java.util.function.Function;

public enum AbbreviationMode {

    FIRST_LETTERS_IGNORE_NUMERIC(false, false, StringHandler::firstCharIgnoreNumeric),
    FIRST_UPPER_CASE_LETTERS_IGNORE_NUMERIC(false, true, StringHandler::firstUpperIgnoreNumeric),
    ALL_UPPER_CASE_LETTERS_IGNORE_NUMERIC(false, true, StringHandler::allUpperIgnoreNumeric),
    FIRST_LETTERS_KEEP_NUMERIC(true, false, StringHandler::firstCharKeepNumeric),
    FIRST_UPPER_CASE_LETTERS_KEEP_NUMERIC(true, true, StringHandler::firstUpperKeepNumeric),
    ALL_UPPER_CASE_LETTERS_KEEP_NUMERIC(true, true, StringHandler::allUpperKeepNumeric);

    private final boolean keepDigits;

    private final boolean isUpperCaseMode;

    private final Function<String, String> function;

    AbbreviationMode(boolean keepDigits, boolean isUpperCaseMode, Function<String, String> function) {
        this.keepDigits = keepDigits;
        this.isUpperCaseMode = isUpperCaseMode;
        this.function = function;
    }

    public boolean keepsDigits() {
        return keepDigits;
    }

    public boolean isUpperCaseMode() {
        return isUpperCaseMode;
    }

    public String apply(String str) {
        return function.apply(str);
    }
}