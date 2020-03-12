package org.talend.dataquality.common.character;

import java.util.List;
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

        StringBuilder sb = new StringBuilder(firstApplied);

        if (separator == AcronymSeparator.KEEP_SPECIAL_CHARS) {
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
        } else {
            for (int i = start; i < tokens.size(); i++) {
                String chars = abbrevMode.apply(tokens.get(i));
                if (!chars.isEmpty()) {
                    sb.append(separator.getValue()).append(abbrevMode.apply(tokens.get(i)));
                }
            }

            if (sb.length() > 0 && AcronymSeparator.PERIOD == separator) {
                sb.append(separator.getValue());
            }
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
}
