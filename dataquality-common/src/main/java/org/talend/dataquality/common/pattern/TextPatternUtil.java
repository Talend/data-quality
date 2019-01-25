package org.talend.dataquality.common.pattern;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.talend.daikon.pattern.character.CharPattern;

/**
 * Helper class for text pattern recognition
 */
public class TextPatternUtil {

    public static final int PROLONGED_SOUND_MARK = 12540;

    private TextPatternUtil() {
        // no need any implement
    }

    /**
     * find text pattern for a given string
     *
     * @param stringToRecognize the input string
     * @return the text pattern
     */
    public static String findPattern(String stringToRecognize) {
        StringBuilder sb = new StringBuilder();
        boolean skipNextOne = false;
        for (int i = 0; i < stringToRecognize.length(); i++) {
            if (skipNextOne) {
                skipNextOne = false;
                continue;
            }
            Integer codePoint = stringToRecognize.codePointAt(i);
            if (isValidProlongedSoundMark(codePoint, sb)) {
                sb.append(sb.charAt(sb.length() - 1));
            } else {
                sb.append(Character.toChars(findReplaceCodePoint(codePoint)));
            }
            if (Character.isHighSurrogate(stringToRecognize.charAt(i))) {
                // if it is HighSurrogate thene we need to skip next one
                skipNextOne = true;
            }

        }
        return sb.toString();
    }

    private static boolean isValidProlongedSoundMark(Integer codePoint, StringBuilder sb) {
        return codePoint == PROLONGED_SOUND_MARK && sb.length() > 0
                && (sb.charAt(sb.length() - 1) == 'K' || sb.charAt(sb.length() - 1) == 'H');
    }

    private static Integer findReplaceCodePoint(Integer codePoint) {
        for (CharPattern charPattern : CharPattern.values()) {
            if (charPattern.contains(codePoint)) {
                return (int) charPattern.getReplaceChar();
            }
        }
        return codePoint;
    }

    /**
     * Replaces a character by a character in the same pattern (according to class CharPattern)
     * If the character is not present in any pattern, then it is kept at it is
     * 
     * @param codePointToReplace
     * @param random
     * @return
     */
    public static Integer replaceCharacter(Integer codePointToReplace, Random random) {
        for (CharPattern charPattern : CharPattern.values()) {
            if (charPattern.contains(codePointToReplace)) {
                int length = charPattern.getCodePointSize();
                int position = random.nextInt(length);
                return charPattern.getCodePointAt(position);
            }
        }
        return codePointToReplace;
    }

    public static List<Integer> replaceStringCodePoints(String stringToReplace, Random random) {
        List<Integer> codePoints = new ArrayList<>();
        long numberCodePoints = stringToReplace.codePoints().count();
        for (int i = 0; i < numberCodePoints; i++) {
            Integer codePoint = stringToReplace.codePointAt(i);
            codePoints.add(replaceCharacter(codePoint, random));
        }
        return codePoints;
    }

    /**
     * Replaces a pattern character by a character in this pattern (according to class CharPattern)
     * If the character is not present in any pattern, then it is kept at it is
     * 
     * @param codePointToReplace
     * @param random
     * @return
     */
    public static char[] replacePatternCharacter(Integer codePointToReplace, Random random) {
        for (CharPattern charPattern : CharPattern.values()) {
            if (charPattern.getReplaceChar().charValue() == codePointToReplace) {
                int length = charPattern.getCodePointSize();
                int position = random.nextInt(length);
                return Character.toChars(charPattern.getCodePointAt(position));
            }
        }
        return Character.toChars(codePointToReplace);
    }

    public static Set<CharPattern> getCharPatterns(List<Integer> codePoints, List<Integer> filteredCodepoints) {
        Set<CharPattern> charPatternSet = new LinkedHashSet<>();
        for (Integer codepoint : codePoints) {
            CharPattern charPattern = getCharPattern(codepoint);
            if (charPattern != null) {
                charPatternSet.add(charPattern);
                filteredCodepoints.add(codepoint);
            }
        }
        return charPatternSet;
    }

    public static CharPattern getCharPattern(Integer codepoint) {
        for (CharPattern charPattern : CharPattern.values())
            if (charPattern.contains(codepoint)) {
                return charPattern;
            }
        return null;
    }

}
