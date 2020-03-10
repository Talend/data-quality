package org.talend.dataquality.common.character;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

public class Acronym {

    private AcronymSeparator separator;

    private AbbreviationMode abbrevMode;

    private String delimiterPattern;

    private Acronym(AbbreviationMode abbrevMode, AcronymSeparator separator, String delimiterPattern) {
        this.abbrevMode = abbrevMode;
        this.separator = separator;
        this.delimiterPattern = delimiterPattern;
    }

    public AcronymSeparator getSeparator() {
        return separator;
    }

    public AbbreviationMode getAbbrevMode() {
        return abbrevMode;
    }

    public String getDelimiterPattern() {
        return delimiterPattern;
    }

    public String transform(String str) {
        StringBuilder sb = new StringBuilder();
        TokenizedString tokenizedString = new TokenizedString(str, delimiterPattern);
        List<String> tokens = tokenizedString.getTokens();

        if (tokens.size() == 0) {
            return StringUtils.EMPTY;
        }

        sb.append(abbrevMode.apply(tokens.get(0)));
        if (separator != AcronymSeparator.AS_IS) {
            for (int i = 1; i < tokens.size(); i++) {
                String chars = abbrevMode.apply(tokens.get(i));
                if (!chars.isEmpty()) {
                    sb.append(separator.value).append(abbrevMode.apply(tokens.get(i)));
                }
            }
        } else {
            List<String> separators = tokenizedString.getSeparators();
            int nextSeparator = tokenizedString.isStartingWithSeparator() ? 1 : 0;
            for (int i = 1; i < tokens.size(); i++) {
                String chars = abbrevMode.apply(tokens.get(i));
                if (!chars.isEmpty()) {
                    sb.append(separators.get(nextSeparator).trim()).append(abbrevMode.apply(tokens.get(i)));
                }
                nextSeparator++;
            }
        }

        if (sb.length() > 0 && separator == AcronymSeparator.PERIOD) {
            sb.append(separator.getValue());
        }

        return sb.toString();
    }

    public static AcronymBuilder newBuilder() {
        return new AcronymBuilder();
    }

    public static class AcronymBuilder {

        private AcronymSeparator separator;

        private AbbreviationMode abbrevMode;

        private String delimiterPattern;

        private AcronymBuilder() {
        }

        public AcronymBuilder withAbbreviationMode(AbbreviationMode abbrevMode) {
            this.abbrevMode = abbrevMode;
            return this;
        }

        public AcronymBuilder withSeparator(AcronymSeparator separator) {
            this.separator = separator;
            return this;
        }

        public AcronymBuilder withDelimiters(String delimiterPattern) {
            this.delimiterPattern = delimiterPattern;
            return this;
        }

        public Acronym build() {
            return new Acronym(abbrevMode, separator, delimiterPattern);
        }
    }

    public enum AcronymSeparator {

        NONE(""),
        DASH("-"),
        SPACE(" "),
        PERIOD("."),
        AS_IS(null);

        private String value;

        AcronymSeparator(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum AbbreviationMode {

        FIRST_LETTERS_IGNORE_NUMERIC(false, false, StringHandler::firstCharIgnoreNumeric),
        FIRST_UPPER_CASE_LETTERS_IGNORE_NUMERIC(false, true, StringHandler::firstUpperOrSpecialIgnoreNumeric),
        ALL_UPPER_CASE_LETTERS_IGNORE_NUMERIC(false, true, StringHandler::allUpperAndSpecialIgnoreNumeric),
        FIRST_LETTERS_KEEP_NUMERIC(true, false, StringHandler::firstCharKeepNumeric),
        FIRST_UPPER_CASE_LETTERS_KEEP_NUMERIC(true, true, StringHandler::firstUpperOrSpecialKeepNumeric),
        ALL_UPPER_CASE_LETTERS_KEEP_NUMERIC(true, true, StringHandler::allUpperAndSpecialKeepNumeric);

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
}
