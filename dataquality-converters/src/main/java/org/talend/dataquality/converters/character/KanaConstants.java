package org.talend.dataquality.converters.character;

import java.util.HashMap;
import java.util.Map;

/**
 * Constants of Japanese Kana in unicode table.
 */
public class KanaConstants {

    /**
     * Halfwidth
     */
    public static final char HALFWIDTH_VOICED_MARK = '\uff9e'; // ﾞ HALFWIDTH KATAKANA VOICED SOUND MARK (dakuten)

    public static final char HALFWIDTH_ASPIRATED_MARK = '\uff9f'; // ﾟ HALFWIDTH KATAKANA SEMI-VOICED SOUND MARK (handakuten)

    public static final char HALFWIDTH_KATAKANA_START = '\uff61'; // ｡ HALFWIDTH IDEOGRAPHIC FULL STOP

    public static final char HALFWIDTH_KATAKANA_END = '\uff9f'; // ﾟ HALFWIDTH KATAKANA SEMI-VOICED SOUND MARK

    /**
     * Fullwidth
     */
    public static final char FULLWIDTH_KATAKANA_START = '\u30a1'; // ァ KATAKANA LETTER SMALL A

    public static final char FULLWIDTH_KATAKANA_END = '\u30fe'; // ヾ KATAKANA VOICED ITERATION MARK

    /**
     *
     * @param ch
     * @return true if the input character is a halfwidth katakana
     */
    public static boolean isHalfwidthKatakana(char ch) {
        return (ch >= HALFWIDTH_KATAKANA_START || ch <= HALFWIDTH_KATAKANA_END);
    }

    /**
     *
     * @param ch
     * @return true if the input character is a fullwidth katakana
     */
    public static boolean isFullwidthKatakana(char ch) {
        return (ch >= FULLWIDTH_KATAKANA_START || ch <= FULLWIDTH_KATAKANA_END);
    }

    public static final Map<Character, Character> MAPPING_HALF_TO_FULL_KATAKANA;
    static {
        MAPPING_HALF_TO_FULL_KATAKANA = new HashMap<>();
        MAPPING_HALF_TO_FULL_KATAKANA.put('｡', '。');
        MAPPING_HALF_TO_FULL_KATAKANA.put('｢', '「');
        MAPPING_HALF_TO_FULL_KATAKANA.put('｣', '」');
        MAPPING_HALF_TO_FULL_KATAKANA.put('､', '、');
        MAPPING_HALF_TO_FULL_KATAKANA.put('･', '・');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｧ', 'ァ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｨ', 'ィ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｩ', 'ゥ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｪ', 'ェ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｫ', 'ォ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｬ', 'ャ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｭ', 'ュ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｮ', 'ョ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｯ', 'ッ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｰ', 'ー');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｱ', 'ア');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｲ', 'イ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｳ', 'ウ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｴ', 'エ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｵ', 'オ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｶ', 'カ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｷ', 'キ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｸ', 'ク');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｹ', 'ケ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｺ', 'コ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｻ', 'サ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｼ', 'シ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｽ', 'ス');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｾ', 'セ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｿ', 'ソ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾀ', 'タ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾁ', 'チ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾂ', 'ツ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾃ', 'テ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾄ', 'ト');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾅ', 'ナ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾆ', 'ニ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾇ', 'ヌ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾈ', 'ネ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾉ', 'ノ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾊ', 'ハ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾋ', 'ヒ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾌ', 'フ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾍ', 'ヘ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾎ', 'ホ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾏ', 'マ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾐ', 'ミ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾑ', 'ム');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾒ', 'メ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾓ', 'モ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾔ', 'ヤ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾕ', 'ユ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾖ', 'ヨ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾗ', 'ラ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾘ', 'リ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾙ', 'ル');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾚ', 'レ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾛ', 'ロ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾜ', 'ワ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ｦ', 'ヲ');
        MAPPING_HALF_TO_FULL_KATAKANA.put('ﾝ', 'ン');
    }

    public static final Map<Character, Character> MAPPING_FULL_TO_HALF_KATAKANA;
    static {
        MAPPING_FULL_TO_HALF_KATAKANA = new HashMap<>();
        MAPPING_FULL_TO_HALF_KATAKANA.put('。', '｡');
        MAPPING_FULL_TO_HALF_KATAKANA.put('「', '｢');
        MAPPING_FULL_TO_HALF_KATAKANA.put('」', '｣');
        MAPPING_FULL_TO_HALF_KATAKANA.put('、', '､');
        MAPPING_FULL_TO_HALF_KATAKANA.put('・', '･');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ァ', 'ｧ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ィ', 'ｨ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ゥ', 'ｩ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ェ', 'ｪ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ォ', 'ｫ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ャ', 'ｬ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ュ', 'ｭ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ョ', 'ｮ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ッ', 'ｯ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ー', 'ｰ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ア', 'ｱ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('イ', 'ｲ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ウ', 'ｳ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ヴ', 'ｳ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('エ', 'ｴ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('オ', 'ｵ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('カ', 'ｶ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ガ', 'ｶ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('キ', 'ｷ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ギ', 'ｷ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ク', 'ｸ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('グ', 'ｸ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ケ', 'ｹ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ゲ', 'ｹ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('コ', 'ｺ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ゴ', 'ｺ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('サ', 'ｻ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ザ', 'ｻ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('シ', 'ｼ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ジ', 'ｼ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ス', 'ｽ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ズ', 'ｽ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('セ', 'ｾ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ゼ', 'ｾ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ソ', 'ｿ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ゾ', 'ｿ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('タ', 'ﾀ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ダ', 'ﾀ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('チ', 'ﾁ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ヂ', 'ﾁ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ツ', 'ﾂ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ヅ', 'ﾂ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('テ', 'ﾃ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('デ', 'ﾃ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ト', 'ﾄ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ド', 'ﾄ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ナ', 'ﾅ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ニ', 'ﾆ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ヌ', 'ﾇ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ネ', 'ﾈ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ノ', 'ﾉ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ハ', 'ﾊ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('バ', 'ﾊ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('パ', 'ﾊ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ヒ', 'ﾋ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ビ', 'ﾋ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ピ', 'ﾋ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('フ', 'ﾌ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ブ', 'ﾌ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('プ', 'ﾌ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ヘ', 'ﾍ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ベ', 'ﾍ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ペ', 'ﾍ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ホ', 'ﾎ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ボ', 'ﾎ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ポ', 'ﾎ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('マ', 'ﾏ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ミ', 'ﾐ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ム', 'ﾑ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('メ', 'ﾒ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('モ', 'ﾓ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ヤ', 'ﾔ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ユ', 'ﾕ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ヨ', 'ﾖ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ラ', 'ﾗ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('リ', 'ﾘ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ル', 'ﾙ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('レ', 'ﾚ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ロ', 'ﾛ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ワ', 'ﾜ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ヮ', 'ﾜ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ヰ', 'ｲ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ヱ', 'ｴ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ヲ', 'ｦ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('ン', 'ﾝ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('゛', 'ﾞ');
        MAPPING_FULL_TO_HALF_KATAKANA.put('゜', 'ﾟ');
    }

    public static final Map<Character, Character> MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES;
    static {
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES = new HashMap<>();
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ヴ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ガ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ギ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('グ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ゲ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ゴ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ザ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ジ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ズ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ゼ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ゾ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ダ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ヂ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ヅ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('デ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ド', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('バ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ビ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ブ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ベ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ボ', HALFWIDTH_VOICED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('パ', HALFWIDTH_ASPIRATED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ピ', HALFWIDTH_ASPIRATED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('プ', HALFWIDTH_ASPIRATED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ペ', HALFWIDTH_ASPIRATED_MARK);
        MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.put('ポ', HALFWIDTH_ASPIRATED_MARK);
    }

}
