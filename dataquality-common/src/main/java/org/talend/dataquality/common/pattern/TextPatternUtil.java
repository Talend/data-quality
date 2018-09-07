package org.talend.dataquality.common.pattern;

import java.util.Random;

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
        int n = stringToRecognize.length();
        for (int i = 0; i < n; i++) {
            char c = stringToRecognize.charAt(i);
            sb.append(findReplaceCharacter(c));
        }
        return sb.toString();
    }

    private static char findReplaceCharacter(char c) {
        for (CharPatternToRegexEnum charPatternToRegexEnum : CharPatternToRegexEnum.values()) {
            if (charPatternToRegexEnum.contains(c))
                return charPatternToRegexEnum.getReplaceChar();
        }
        return c;
    }

    /**
     * Replaces a character by a character in the same pattern (according to class CharPatternToRegexEnum)
     * If the character is not present in any pattern, then it is kept at it is
     * @param toBeReplaced
     * @param random
     * @return
     */
    public static char replaceCharacter(char toBeReplaced, Random random) {
        for (CharPatternToRegexEnum charPatternToRegexEnum : CharPatternToRegexEnum.values()) {
            if (charPatternToRegexEnum.contains(toBeReplaced)) {
                int length = charPatternToRegexEnum.getSize();
                return charPatternToRegexEnum.getCharacter(random.nextInt(length));
            }
        }
        return toBeReplaced;
    }

}
