package org.talend.dataquality.statistics.frequency.pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum TypoUnicodePattern {
    // @formatter:off
    WORD(
            "[Word]",
            "["
                    + Constants.ALL_CHARS
                    + "]{2,}"),
    LOWER_WORD(
            "[word]",
            "["
                    + Constants.ALL_CHARS
                    + "]{2,}",
            "["
                    + Constants.LOWER_CHAR
                    + "]{2,}"),
    UPPER_WORD(
            "[WORD]",
            "["
                    + Constants.ARABIC
                    + "|"
                    + Constants.ALL_LETTERS
                    + "]{2,}",
            "["
                    + Constants.UPPER_CHAR
                    + "]{2,}"),
    LOWER_CHAR(
            "[char]",
            "["
                    + Constants.ALL_CHARS
                    + "]",
            "["
                    + Constants.LOWER_CHAR
                    + "]"),
    UPPER_CHAR(
            "[Char]",
            "["
                    + Constants.ALL_CHARS
                    + "]",
            "["
                    + Constants.UPPER_CHAR
                    + "]"),
    NUMBER("[number]",
            "["
                    + Constants.DIGITS
                    + "]{2,}"),
    DIGIT("[digit]",
            "["
                    + Constants.DIGITS
                    + "]"),
    ALPHANUMERIC(
            "[alnum]",
            "["
                    + Constants.DIGITS
                    + "|"
                    + Constants.ALL_CHARS
                    + "]{2,}"),
    IDEOGRAM("[Ideogram]",
            "["
                    + Constants.IDEOGRAMS
                    + "]"),
    IDEOGRAM_SEQUENCE("[IdeogramSeq]",
            "["
                    + Constants.IDEOGRAMS
                    + "]{2,}"),
    ALPHANUMERIC_CJK("[alnum(CJK)]",
            "["
                    + Constants.DIGITS
                    + "|"
                    + Constants.IDEOGRAMS
                    + "]{2,}");
    // @formatter:on
    private static final Map<String, TypoUnicodePattern> lookup = new HashMap<>();

    static {
        for (TypoUnicodePattern value : TypoUnicodePattern.values()) {
            lookup.put(value.getPattern(), value);
        }
    }

    public static Optional<TypoUnicodePattern> get(final String pattern) {
        return Optional.ofNullable(lookup.get(pattern));
    }

    private final String pattern;

    private final String caseInsensitive;

    private final String caseSensitive;

    TypoUnicodePattern(final String pattern, final String caseInsensitive, final String caseSensitive) {
        this.pattern = pattern;
        this.caseInsensitive = caseInsensitive;
        this.caseSensitive = caseSensitive;
    }

    TypoUnicodePattern(final String pattern, final String caseInsensitive) {
        this.pattern = pattern;
        this.caseInsensitive = caseInsensitive;
        this.caseSensitive = caseInsensitive;
    }

    public String getPattern() {
        return pattern;
    }

    public String getCaseInsensitive() {
        return caseInsensitive;
    }

    public String getCaseSensitive() {
        return caseSensitive;
    }

    private static class Constants {

        private static final String IDEOGRAMS = "\\p{InHangul_Jamo}|\\p{InHangul_Compatibility_Jamo}|\\p{InHangul_Syllables}|\\p{script=Han}";

        private static final String LOWER_LETTERS = "\\p{Ll}";
        private static final String UPPER_LETTERS = "\\p{Lu}";
        private static final String ALL_LETTERS = "\\p{L}";
        private static final String ARABIC = "\\p{InArabic}";
        private static final String DIGITS = "\\p{Nd}";


        private static final String ALL_CHARS = Constants.ARABIC + "|" + Constants.ALL_LETTERS
                + "&&[^"+ IDEOGRAMS +"]"; // Except ideograms
        private static final String UPPER_CHAR = ALL_CHARS
                + "&&[^"  // Except lower because only lower won't contain insensitive letters
                + Constants.LOWER_LETTERS
                + "]";
        private static final String LOWER_CHAR = ALL_CHARS
                + "&&[^"  // Except uppers because only lower won't contain insensitive letters
                + Constants.UPPER_LETTERS
                + "]";
    }
}
