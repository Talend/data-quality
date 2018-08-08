package org.talend.dataquality.common.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Helper class for text pattern recognition
 */
public class TextPatternUtil {

    /**
     * beginIntervals contains the begin index of each intervals
     * it must be sorted and we should not have any intersections between the intervals
     */
    private static final List<Integer> BEGIN_INTERVALS = new ArrayList<>();

    /**
     * for each begin index, we have the end index and the replacement character
     */
    private static final Map<Integer, Pair<Integer, Character>> INTERVAL_REPLACEMENT_MAP = new HashMap<>();

    /**
     * avoid manual instantiation
     */
    private TextPatternUtil() {
    }

    static {
        loadIntervals();
    }

    private static void loadIntervals() {
        buildInterval(0, 0, ' '); // for the characters not found
        buildInterval('0', '9', '9');
        buildInterval('A', 'Z', 'A');
        buildInterval('À', 'Ö', 'A');
        buildInterval('Ø', 'Þ', 'A');
        buildInterval('a', 'z', 'a');
        buildInterval('ß', 'ö', 'a');
        buildInterval('ø', 'ÿ', 'a');

        buildInterval('h', 0x3041, 0x3043, 0x3045, 0x3047, 0x3049, 0x3063, 0x3083, // HIRAGANA SMALL
                0x3085, 0x3087, 0x308E, 0x3095, 0x3096);

        buildInterval('H', 0x3042, 0x3044, 0x3046, 0x3048, 0x3084, 0x3086); // HIRAGANA
        buildInterval(0x304A, 0x3062, 'H'); // HIRAGANA
        buildInterval(0x3064, 0x3082, 'H'); // HIRAGANA
        buildInterval(0x3088, 0x308D, 'H'); // HIRAGANA
        buildInterval(0x308F, 0x3094, 'H'); // HIRAGANA
        buildInterval(0x3099, 0x309F, 'H'); // HIRAGANA

        buildInterval(0x30A1, 0x30FA, 'K'); // KATAKANA
        buildInterval(0xFF66, 0xFF9F, 'K'); // KATAKANA

        buildInterval(0x4e00, 0x9faf, 'C'); // KANJI

        buildInterval(0xAC00, 0xD7AF, 'G'); // HANGUL SYLLABLES

        Collections.sort(BEGIN_INTERVALS); // important for the dichotomic search
    }

    private static void buildInterval(int begin, int end, char replaceCharacter) {
        BEGIN_INTERVALS.add(begin);
        INTERVAL_REPLACEMENT_MAP.put(begin, Pair.of(end, replaceCharacter));
    }

    private static void buildInterval(char replaceCharacter, Integer... charactersList) {
        for (int character : charactersList)
            buildInterval(character, character, replaceCharacter);
    }

    /**
     * find text pattern for a given string
     * 
     * @param stringToRecognize the input string
     * @return the text pattern
     */
    public static String findPattern(String stringToRecognize) {
        StringBuilder sb = new StringBuilder();
        int n = stringToRecognize.length();
        for (int i = 0; i < n; i++) {
            char c = stringToRecognize.charAt(i);
            int index = Collections.binarySearch(BEGIN_INTERVALS, (int) c); // dichotomic search
            if (index < 0) // if the character is not found, it can be in an interval
                index = -index - 2;
            Pair<Integer, Character> pair = INTERVAL_REPLACEMENT_MAP.get(BEGIN_INTERVALS.get(index));
            if (pair != null && pair.getLeft() >= (int) c) { // c was found
                sb.append(pair.getRight());
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
