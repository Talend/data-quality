// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.statistics.frequency.recognition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 * * Recognize ascii characters given predefined list of Ascii characters and its pattern mappings.
 * 
 * @since 6.1.0
 * @author jteuladedenantes
 */
public class GenericCharPatternRecognizer extends AbstractPatternRecognizer {

    private Pattern charsPattern = Pattern.compile("[a-z|A-Z|à-ÿ|À-ß]");

    /**
     * beginIntervals contains the begin index of each intervals
     * it must be sorted and we should not have any intersections between the intervals
     */
    private static final List<Integer> beginIntervals = new ArrayList<>();

    /**
     * for each begin index, we have the end index and the replacement character
     */
    private static final Map<Integer, Pair<Integer, Character>> beginToEndAndReplacementChar = new HashMap<>();

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

        buildInterval('h', 0x3041, 0x3043, 0x3045, 0x3047, 0x3049, 0x3063, 0x3083, 0x3085, 0x3087, 0x308E, 0x3095, 0x3096); //HIRAGANA SMALL

        buildInterval('H', 0x3042, 0x3044, 0x3046, 0x3048, 0x3084, 0x3086); //HIRAGANA
        buildInterval(0x304A, 0x3062, 'H'); //HIRAGANA
        buildInterval(0x3064, 0x3082, 'H'); //HIRAGANA
        buildInterval(0x3088, 0x308D, 'H'); //HIRAGANA
        buildInterval(0x308F, 0x3094, 'H'); //HIRAGANA
        buildInterval(0x3099, 0x309F, 'H'); //HIRAGANA

        buildInterval(0x30A1, 0x30FA, 'K'); //KATAKANA
        buildInterval(0xFF66, 0xFF9F, 'K'); //KATAKANA

        buildInterval(0x4e00, 0x9faf, 'C'); //KANJI

        buildInterval(0xAC00, 0xD7AF, 'G'); //HANGUL SYLLABLES

        Collections.sort(beginIntervals); // important for the dichotomic search
    }

    private static void buildInterval(int begin, int end, char replaceCharacter) {
        beginIntervals.add(begin);
        beginToEndAndReplacementChar.put(begin, Pair.of(end, replaceCharacter));
    }

    private static void buildInterval(char replaceCharacter, Integer... charactersList) {
        for (int character : charactersList)
            buildInterval(character, character, replaceCharacter);
    }

    @Override
    public RecognitionResult recognize(String stringToRecognize, DataTypeEnum type) {
        RecognitionResult result = new RecognitionResult();
        if (StringUtils.isEmpty(stringToRecognize)) {
            result.setResult(Collections.singleton(stringToRecognize), false);
            return result;
        }
        boolean isComplete = true;
        StringBuilder sb = new StringBuilder();
        int n = stringToRecognize.length();
        for (int i = 0; i < n; i++) {
            char c = stringToRecognize.charAt(i);
            int index = Collections.binarySearch(beginIntervals, (int) c); //dichotomic search
            if (index < 0) // if the character is not found, it can be in an interval
                index = -index - 2;
            Pair<Integer, Character> pair = beginToEndAndReplacementChar.get(beginIntervals.get(index));
            if (pair != null && pair.getLeft() >= (int) c) { //c was found
                sb.append(pair.getRight());
            } else {
                sb.append(c);
                isComplete = false;
            }
        }
        result.setResult(Collections.singleton(sb.toString()), isComplete);
        return result;
    }

    /**
     * Whether the patternString contains the predefined alpha character.
     *
     * @param patternString
     * @return
     */
    public boolean containsAlpha(String patternString) {
        return charsPattern.matcher(patternString).find();
    }

    @Override
    public Set<String> getValuePattern(String originalValue) {
        RecognitionResult result = recognize(originalValue);
        return result.getPatternStringSet();
    }

}
