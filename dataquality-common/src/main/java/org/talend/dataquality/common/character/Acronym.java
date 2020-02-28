package org.talend.dataquality.common.character;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

public class Acronym {

    private AcronymSeparator separator;

    private AcronymContraction contraction;

    private String delimiterPattern;

    private Acronym(AcronymContraction contraction, AcronymSeparator separator, String delimiterPattern) {
        this.contraction = contraction;
        this.separator = separator;
        this.delimiterPattern = delimiterPattern;
    }

    public AcronymSeparator getSeparator() {
        return separator;
    }

    public AcronymContraction getContraction() {
        return contraction;
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

        sb.append(contraction.apply(tokens.get(0)));
        if (separator != AcronymSeparator.AS_IS) {
            for (int i = 1; i < tokens.size(); i++) {
                String chars = contraction.apply(tokens.get(i));
                if (!chars.isEmpty()) {
                    sb.append(separator.value).append(contraction.apply(tokens.get(i)));
                }
            }
        } else {
            List<String> separators = tokenizedString.getSeparators();
            int nextSeparator = tokenizedString.isStartingWithSeparator() ? 1 : 0;
            for (int i = 1; i < tokens.size(); i++) {
                String chars = contraction.apply(tokens.get(i));
                if (!chars.isEmpty()) {
                    sb.append(separators.get(nextSeparator).trim()).append(contraction.apply(tokens.get(i)));
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

        private AcronymContraction contraction;

        private String delimiterPattern;

        private AcronymBuilder() {
        }

        public AcronymBuilder withContraction(AcronymContraction contraction) {
            this.contraction = contraction;
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
            return new Acronym(contraction, separator, delimiterPattern);
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

    public enum AcronymContraction {

        FIRST_LETTERS_IGNORE_NUMERIC(false, StringHandler::firstCharIgnoreNumeric),
        FIRST_UPPER_CASE_LETTERS_IGNORE_NUMERIC(false, StringHandler::firstUpperOrSpecialIgnoreNumeric),
        ALL_UPPER_CASE_LETTERS_IGNORE_NUMERIC(false, StringHandler::allUpperAndSpecialIgnoreNumeric),
        FIRST_LETTERS_KEEP_NUMERIC(true, StringHandler::firstCharKeepNumeric),
        FIRST_UPPER_CASE_LETTERS_KEEP_NUMERIC(true, StringHandler::firstUpperOrSpecialKeepNumeric),
        ALL_UPPER_CASE_LETTERS_KEEP_NUMERIC(true, StringHandler::allUpperAndSpecialKeepNumeric);

        private final boolean keepDigits;

        private final Function<String, String> function;

        AcronymContraction(boolean keepDigits, Function<String, String> function) {
            this.keepDigits = keepDigits;
            this.function = function;
        }

        public boolean keepsDigits() {
            return keepDigits;
        }

        public String apply(String str) {
            return function.apply(str);
        }
    }
}
