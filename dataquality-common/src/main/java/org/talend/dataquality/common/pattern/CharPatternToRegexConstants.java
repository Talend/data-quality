package org.talend.dataquality.common.pattern;

public class CharPatternToRegexConstants {

    static final String DIGIT = "[\\u0030-\\u0039]";

    static final String LOWER_LATIN = "[\\u0061-\\u007a|\\u00DF-\\u00F6|\\u00F8-\\u00FF]";

    static final String UPPER_LATIN = "[\\u0041-\\u005A|\\u00C0-\\u00D6|\\u00D8-\\u00DE]";

    static final String FULLWIDTH_DIGIT = "[\\uFF10-\\uFF19]";

    static final String FULLWIDTH_LOWER_LATIN = "[\\uFF41-\\uFF5A]";

    static final String FULLWIDTH_UPPER_LATIN = "[\\uFF21-\\uFF3A]";

    static final String LOWER_HIRAGANA = "[\\u3041|\\u3043|\\u3045|\\u3047|\\u3049|\\u3063|\\u3083|\\u3085|\\u3087|\\u308E|\\u3095|\\u3096]";

    static final String UPPER_HIRAGANA = "[\\u3042|\\u3044|\\u3046|\\u3048|\\u304A-\\u3062|\\u3064-\\u3082|\\u3084|\\u3086|\\u3088-\\u308D|\\u308F-\\u3094]";

    static final String LOWER_KATAKANA = "[\\u30A1|\\u30A3|\\u30A5|\\u30A7|\\u30A9|\\u30C3|\\u30E3|\\u30E5|\\u30E7|\\u30EE|\\u30F5|\\u30F6" // FullWidth
            + "|\\u31F0-\\u31FF" // Phonetic extension
            + "|\\uFF67-\\uFF6F]"; // HalfWidth

    static final String UPPER_KATAKANA = "[\\u30A2|\\u30A4|\\u30A6|\\u30A8|\\u30AA-\\u30C2|\\u30C4-\\u30E2|\\u30E4|\\u30E6|\\u30E8-\\u30ED|\\u30EF-\\u30F4|\\u30F7-\\u30FA" // FullWidth
            + "|\\uFF66|\\uFF71-\\uFF9D]"; // HalfWidth

    static final String KANJI = "[\\u4E00-\\u9FBF]";

    static final String HANGUL = "[\\uAC00-\\uD7AF]";

}
