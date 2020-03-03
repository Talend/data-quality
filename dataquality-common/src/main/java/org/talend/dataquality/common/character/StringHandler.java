package org.talend.dataquality.common.character;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public final class StringHandler {

    public static final Set<Character> SPECIAL_CHARACTERS = new HashSet<>(Arrays.asList('&', '/'));

    /**
     * Convert the string to title case.
     *
     * @param str the string to convert.
     * @param delimiters the delimiter pattern to split the string into words.
     * @return the string with every word separated by {@param delimiters} in title case.
     */
    public static String toTitleCase(String str, String delimiters) {

        if (StringUtils.isBlank(str)) {
            return StringUtils.EMPTY;
        }

        if (StringUtils.length(str) == 1) {
            return str.toUpperCase();
        }

        StringBuilder resultPlaceHolder = new StringBuilder(str.length());

        Stream.of(str.split(delimiters)).forEach(stringPart -> {
            if (stringPart.length() > 1) {
                int firstCodePoint = stringPart.codePointAt(0);
                String firstChars = new String(Character.toChars(firstCodePoint));
                int offset = stringPart.offsetByCodePoints(0, 1);
                resultPlaceHolder.append(firstChars.toUpperCase()).append(stringPart.substring(offset).toLowerCase());
            } else
                resultPlaceHolder.append(stringPart.toUpperCase());

            resultPlaceHolder.append(" ");
        });

        return StringUtils.trim(resultPlaceHolder.toString());
    }

    /**
     * Takes the first char of the string, ignoring numbers.
     *
     * @param str the input string.
     * @return the first char of the string.
     */
    public static String firstCharIgnoreNumeric(String str) {

        if (str == null || "".equals(str)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        int cpCount = str.codePointCount(0, str.length());
        for (int i = 0; i < cpCount; i++) {
            int codePointPos = str.offsetByCodePoints(0, i);
            int codePoint = str.codePointAt(codePointPos);
            if (!Character.isDigit(codePoint)) {
                return new String(Character.toChars(codePoint));
            }
        }
        return "";
    }

    /**
     * Takes the first char of the string only if it is in upper case or among the supported
     * {@link #SPECIAL_CHARACTERS}.
     *
     * @param str the input string.
     * @return the first character of the string.
     */
    public static String firstUpperOrSpecialIgnoreNumeric(String str) {

        if (str == null || "".equals(str)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        int cpCount = str.codePointCount(0, str.length());
        for (int i = 0; i < cpCount; i++) {
            int codePointPos = str.offsetByCodePoints(0, i);
            int codePoint = str.codePointAt(codePointPos);
            if (Character.isDigit(codePoint)) {
                continue;
            }
            if (Character.isUpperCase(codePoint) || SPECIAL_CHARACTERS.contains(str.charAt(codePointPos))) {
                return new String(Character.toChars(codePoint));
            } else {
                break;
            }
        }
        return "";
    }

    /**
     * Take all the upper case letters and supported {@link #SPECIAL_CHARACTERS} in the string.
     *
     * @param str the input string.
     * @return all the upper cases letters and {@link #SPECIAL_CHARACTERS} in the string.
     */
    public static String allUpperAndSpecialIgnoreNumeric(String str) {

        if (str == null || "".equals(str)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();
        int cpCount = str.codePointCount(0, str.length());
        for (int i = 0; i < cpCount; i++) {
            int codePointPos = str.offsetByCodePoints(0, i);
            int codePoint = str.codePointAt(codePointPos);
            if (Character.isUpperCase(codePoint) || SPECIAL_CHARACTERS.contains(str.charAt(codePointPos))) {
                sb.append(Character.toChars(codePoint));
            }
        }
        return sb.toString();
    }

    /**
     * Take the first character of the string.
     * If the first character is a digit, also keep all the directly following digits if any.
     * If the first character is followed by a number, this number is kept.
     * If the string starts with a number, the first non-numeric character is not kept.
     *
     * @param str the input string.
     * @return the starting character (and the following number if any) or number of the string.
     */
    public static String firstCharKeepNumeric(String str) {

        if (str == null || "".equals(str)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();
        int cpCount = str.codePointCount(0, str.length());
        int firstCodePoint = str.codePointAt(str.offsetByCodePoints(0, 0));

        sb.append(Character.toChars(firstCodePoint));

        for (int i = 1; i < cpCount; i++) {
            int codePointPos = str.offsetByCodePoints(0, i);
            int codePoint = str.codePointAt(codePointPos);
            if (Character.isDigit(codePoint)) {
                sb.append(Character.toChars(codePoint));
            } else {
                break;
            }
        }

        return sb.toString();
    }

    /**
     * Take the first uppercase or supported {@link #SPECIAL_CHARACTERS} of the string.
     * If the first character is a digit, also keep all the directly following digits if any.
     * If the first character is followed by a number, this number is kept.
     * If the string starts with a number, the first non-numeric character is not kept.
     *
     * @param str the input string.
     * @return the starting uppercase or supported {@link #SPECIAL_CHARACTERS} (and the following number if any) or
     * number of the string.
     */
    public static String firstUpperOrSpecialKeepNumeric(String str) {

        if (str == null || "".equals(str)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();
        int cpCount = str.codePointCount(0, str.length());
        int firstCodePoint = str.codePointAt(str.offsetByCodePoints(0, 0));

        if (Character.isDigit(firstCodePoint) || Character.isUpperCase(firstCodePoint)
                || SPECIAL_CHARACTERS.contains(str.charAt(0))) {
            sb.append(Character.toChars(firstCodePoint));
            for (int i = 1; i < cpCount; i++) {
                int codePointPos = str.offsetByCodePoints(0, i);
                int codePoint = str.codePointAt(codePointPos);
                if (Character.isDigit(codePoint)) {
                    sb.append(Character.toChars(codePoint));
                } else {
                    break;
                }
            }
        }

        return sb.toString();
    }

    /**
     * Take all the uppercase letters, supported {@link #SPECIAL_CHARACTERS} and numbers of the string.
     *
     * @param str the input string.
     * @return all the uppercase letters, supported {@link #SPECIAL_CHARACTERS} and numbers of the string.
     */
    public static String allUpperAndSpecialKeepNumeric(String str) {

        if (str == null || "".equals(str)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();
        int cpCount = str.codePointCount(0, str.length());
        for (int i = 0; i < cpCount; i++) {
            int codePointPos = str.offsetByCodePoints(0, i);
            int codePoint = str.codePointAt(codePointPos);
            if (Character.isDigit(codePoint) || Character.isUpperCase(codePoint)
                    || SPECIAL_CHARACTERS.contains(str.charAt(codePointPos))) {
                sb.append(Character.toChars(codePoint));
            }
        }

        return sb.toString();
    }
}
