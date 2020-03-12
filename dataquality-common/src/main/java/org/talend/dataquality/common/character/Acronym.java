package org.talend.dataquality.common.character;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String transform(String str) {
        StringBuilder sb = new StringBuilder();
        TokenizedString tokenizedString = new TokenizedString(str, delimiterPattern);
        List<String> tokens = tokenizedString.getTokens();

        if (tokens.size() == 0) {
            return StringUtils.EMPTY;
        }

        int start = 0;
        String firstApplied;
        do {
            firstApplied = abbrevMode.apply(tokens.get(start));
            start++;
        } while (firstApplied.isEmpty() && start < tokens.size());

        sb.append(firstApplied);
        if (separator != AcronymSeparator.KEEP_SPECIAL_CHARS) {
            for (int i = start; i < tokens.size(); i++) {
                String chars = abbrevMode.apply(tokens.get(i));
                if (!chars.isEmpty()) {
                    sb.append(separator.value).append(abbrevMode.apply(tokens.get(i)));
                }
            }
        } else {
            Pattern specialCharPattern = Pattern.compile(separator.getValue());
            List<String> separators = tokenizedString.getSeparators();
            int nextSeparator = tokenizedString.isStartingWithSeparator() ? 1 : 0;
            for (int i = 1; i < tokens.size(); i++) {
                String chars = abbrevMode.apply(tokens.get(i));
                if (!chars.isEmpty()) {

                    sb.append(getSpecialChars(separators.get(nextSeparator), specialCharPattern)).append(
                            abbrevMode.apply(tokens.get(i)));
                }
                nextSeparator++;
            }
        }

        if (sb.length() > 0 && separator == AcronymSeparator.PERIOD) {
            sb.append(separator.getValue());
        }

        return sb.toString();
    }

    private String getSpecialChars(String separator, Pattern specialCharPattern) {
        Matcher matcher = specialCharPattern.matcher(separator);
        return matcher.find() ? matcher.group() : "";
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
        KEEP_SPECIAL_CHARS("[#$%&()\\-/=@_|~]");

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
}
