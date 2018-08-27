package org.talend.dataquality.statistics.frequency.pattern;

import org.talend.dataquality.common.regex.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum TypoUnicodePattern {
    // @formatter:off
    WORD(
            "[Word]",
            "["
                    + ArabicLetters.range
                    + Constants.JAPANESE_SYLLABARY
                    + LatinLetters.range
                    + LatinLettersSmall.range
                    + GreekLetters.range
                    + GreekLettersSmall.range
                    + "]+"),
    LOWER_WORD(
            "[word]",
            "["
                    + ArabicLetters.range
                    + Constants.JAPANESE_SYLLABARY
                    + LatinLetters.range
                    + LatinLettersSmall.range
                    + GreekLetters.range
                    + GreekLettersSmall.range
                    + "]+",
            "["
                    + ArabicLetters.range
                    + Constants.JAPANESE_SYLLABARY
                    + LatinLettersSmall.range
                    + GreekLettersSmall.range
                    + "]+"),
    UPPER_WORD(
            "[WORD]",
            "["
                    + ArabicLetters.range
                    + Constants.JAPANESE_SYLLABARY
                    + LatinLetters.range
                    + LatinLettersSmall.range
                    + GreekLetters.range
                    + GreekLettersSmall.range
                    + "]+",
            "["
                    + ArabicLetters.range
                    + Constants.JAPANESE_SYLLABARY
                    + LatinLetters.range
                    + GreekLetters.range
                    + "]+"),
    LOWER_CHAR(
            "[char]",
            "["
                    + ArabicLetters.range
                    + Constants.JAPANESE_SYLLABARY
                    + LatinLetters.range
                    + LatinLettersSmall.range
                    + GreekLetters.range
                    + GreekLettersSmall.range
                    + "]",
            "["
                    + ArabicLetters.range
                    + Constants.JAPANESE_SYLLABARY
                    + LatinLettersSmall.range
                    + GreekLettersSmall.range
                    + "]"),
    UPPER_CHAR(
            "[Char]",
            "["
                    + ArabicLetters.range
                    + Constants.JAPANESE_SYLLABARY
                    + LatinLetters.range
                    + LatinLettersSmall.range
                    + GreekLetters.range
                    + GreekLettersSmall.range
                    + "]",
            "["
                    + ArabicLetters.range
                    + Constants.JAPANESE_SYLLABARY
                    + LatinLetters.range
                    + GreekLetters.range
                    + "]"),
    NUMBER("[number]",
            "["
                    + LatinAsciiDigits.range
                    + "]+"),
    DIGIT("[digit]",
            "["
                    + LatinAsciiDigits.range
                    + "]"),
    ALPHANUMERIC(
            "[alnum]",
            "["
                    + ArabicLetters.range
                    + Constants.JAPANESE_SYLLABARY
                    + LatinLetters.range
                    + LatinLettersSmall.range
                    + LatinAsciiDigits.range
                    + GreekLetters.range
                    + GreekLettersSmall.range
                    + "]+"),
    IDEOGRAM("[Ideogram]",
            "["
                    + Constants.IDEOGRAMS
                    + "]"),
    IDEOGRAM_SEQUENCE("[IdeogramSeq]",
            "["
                    + Constants.IDEOGRAMS
                    + "]+"),
    ALPHANUMERIC_CJK("[alnum(CJK)]", 
            "[" 
                    + Constants.IDEOGRAMS 
                    + LatinAsciiDigits.range 
                    + "]+");
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

        private static final String JAPANESE_SYLLABARY =
                Hiragana.range + HiraganaSmall.range + Katakana.range + KatakanaSmall.range;

        private static final String IDEOGRAMS = Hangul.range + Kanji.range;
    }
}
