package org.talend.dataquality.common.pattern;

class CharPatternToRegexConstants {

    static final String DIGIT = "([\\x{30}-\\x{39}])";

    static final String LOWER_LATIN = "([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}])";

    static final String UPPER_LATIN = "([\\x{41}-\\x{5A}]|[\\x{C0}-\\x{D6}]|[\\x{D8}-\\x{DE}])";

    static final String FULLWIDTH_DIGIT = "([\\x{FF10}-\\x{FF19}])";

    static final String FULLWIDTH_LOWER_LATIN = "([\\x{FF41}-\\x{FF5A}])";

    static final String FULLWIDTH_UPPER_LATIN = "([\\x{FF21}-\\x{FF3A}])";

    static final String LOWER_HIRAGANA = "(\\x{3041}|\\x{3043}|\\x{3045}|\\x{3047}|\\x{3049}|\\x{3063}|\\x{3083}|\\x{3085}|\\x{3087}|\\x{308E}|\\x{3095}|\\x{3096})";

    static final String UPPER_HIRAGANA = "(\\x{3042}|\\x{3044}|\\x{3046}|\\x{3048}|[\\x{304A}-\\x{3062}]|[\\x{3064}-\\x{3082}]|\\x{3084}|\\x{3086}|[\\x{3088}-\\x{308D}]|[\\x{308F}-\\x{3094}])";

    static final String LOWER_KATAKANA = "(\\x{30A1}|\\x{30A3}|\\x{30A5}|\\x{30A7}|\\x{30A9}|\\x{30C3}|\\x{30E3}|\\x{30E5}|\\x{30E7}|\\x{30EE}|\\x{30F5}|\\x{30F6}" // FullWidth
            + "|[\\x{31F0}-\\x{31FF}]" // Phonetic extension
            + "|[\\x{FF67}-\\x{FF6F}])"; // HalfWidth

    static final String UPPER_KATAKANA = "(\\x{30A2}|\\x{30A4}|\\x{30A6}|\\x{30A8}|[\\x{30AA}-\\x{30C2}]|[\\x{30C4}-\\x{30E2}]|\\x{30E4}|\\x{30E6}|[\\x{30E8}-\\x{30ED}]|[\\x{30EF}-\\x{30F4}]|[\\x{30F7}-\\x{30FA}]" // FullWidth
            + "|\\x{FF66}|[\\x{FF71}-\\x{FF9D}])"; // HalfWidth

    static final String KANJI = "([\\x{4E00}-\\x{9FEF}]" + "|[\\x{3400}-\\x{4DB5}]" + // Extension A
            "|[\\x{20000}-\\x{2A6D6}]" + // Extension B
            "|[\\x{2A700}-\\x{2B734}]" + // Extension C
            "|[\\x{2B740}-\\x{2B81D}]" + // Extension D
            "|[\\x{2B820}-\\x{2CEA1}]" + // Extension E
            "|[\\x{2CEB0}-\\x{2EBE0}]" + // Extension F
            "|[\\x{F900}-\\x{FA6D}]|[\\x{FA70}-\\x{FAD9}]" + // Compatibility Ideograph
            "|[\\x{2F800}-\\x{2FA1D}]" + // Compatibility Ideograph Supplement
            "|[\\x{2F00}-\\x{2FD5}]" + // KangXi Radicals
            "|[\\x{2E80}-\\x{2E99}]|[\\x{2E9B}-\\x{2EF3}]" + // Radical Supplement
            "|\\x{3005}|\\x{3007}|[\\x{3021}-\\x{3029}]|[\\x{3038}-\\x{303B}]" + // Symbol and punctuation added for TDQ-11343
            ")";

    static final String HANGUL = "([\\x{AC00}-\\x{D7AF}])";

}
