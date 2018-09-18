package org.talend.dataquality.common.pattern;

import java.util.Random;

import org.talend.daikon.pattern.character.CharPattern;

/**
 * Helper class for text pattern recognition
 */
public class TextPatternUtil {

    /**
     * find text pattern for a given string
     *
     * @param stringToRecognize the input string
     * @return the text pattern
     */
    public static String findPattern(String stringToRecognize) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stringToRecognize.length(); i++) {
            Integer codePoint = stringToRecognize.codePointAt(i);
            sb.append(Character.toChars(findReplaceCodePoint(codePoint)));
            if (Character.isHighSurrogate(stringToRecognize.charAt(i)))
                i++;
        }
        return sb.toString();
    }

    private static Integer findReplaceCodePoint(Integer codePoint) {
        for (CharPattern charPattern : CharPattern.values()) {
            if (charPattern.contains(codePoint))
                return (int) charPattern.getReplaceChar();
        }
        return codePoint;
    }

    /**
     * Replaces a character by a character in the same pattern (according to class CharPattern)
     * If the character is not present in any pattern, then it is kept at it is
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

}
