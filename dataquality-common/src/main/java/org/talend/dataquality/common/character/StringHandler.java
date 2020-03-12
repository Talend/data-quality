package org.talend.dataquality.common.character;

import java.util.stream.Stream;

public final class StringHandler {

    /**
     * Convert the string to title case.
     *
     * @param str the string to convert.
     * @param delimiters the delimiter pattern to split the string into words.
     * @return the string with every word separated by {@param delimiters} in title case.
     */
    public static String toTitleCase(String str, String delimiters) {

        if (str == null || str.isEmpty()) {
            return "";
        }

        if (str.length() == 1) {
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

        return resultPlaceHolder.toString().trim();
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
     * Takes the first char of the string only if it is in upper case
     *
     * @param str the input string.
     * @return the first character of the string.
     */
    public static String firstUpperIgnoreNumeric(String str) {

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
            if (Character.isUpperCase(codePoint)) {
                return new String(Character.toChars(codePoint));
            } else {
                break;
            }
        }
        return "";
    }

    /**
     * Take all the upper case letters in the string.
     *
     * @param str the input string.
     * @return all the upper cases letters in the string.
     */
    public static String allUpperIgnoreNumeric(String str) {

        if (str == null || "".equals(str)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();
        int cpCount = str.codePointCount(0, str.length());
        for (int i = 0; i < cpCount; i++) {
            int codePointPos = str.offsetByCodePoints(0, i);
            int codePoint = str.codePointAt(codePointPos);
            if (Character.isUpperCase(codePoint)) {
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
     * Take the first uppercase and/or numeric of the string.
     * If the first character is a digit, also keep all the directly following digits if any.
     * If the first character is followed by a number, this number is kept.
     * If the string starts with a number, the first non-numeric character is not kept.
     *
     * @param str the input string.
     * @return the starting uppercase (and the following number if any) or
     * number of the string.
     */
    public static String firstUpperKeepNumeric(String str) {

        if (str == null || "".equals(str)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();
        int cpCount = str.codePointCount(0, str.length());
        int firstCodePoint = str.codePointAt(str.offsetByCodePoints(0, 0));

        if (Character.isDigit(firstCodePoint) || Character.isUpperCase(firstCodePoint)) {
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
     * Take all the uppercase letters and numbers of the string.
     *
     * @param str the input string.
     * @return all the uppercase letters and numbers of the string.
     */
    public static String allUpperKeepNumeric(String str) {

        if (str == null || "".equals(str)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();
        int cpCount = str.codePointCount(0, str.length());
        for (int i = 0; i < cpCount; i++) {
            int codePointPos = str.offsetByCodePoints(0, i);
            int codePoint = str.codePointAt(codePointPos);
            if (Character.isDigit(codePoint) || Character.isUpperCase(codePoint)) {
                sb.append(Character.toChars(codePoint));
            }
        }

        return sb.toString();
    }
}
