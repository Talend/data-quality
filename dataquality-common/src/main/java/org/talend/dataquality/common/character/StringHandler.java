package org.talend.dataquality.common.character;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public final class StringHandler {

    public static final Set<Character> SPECIAL_CHARACTERS = new HashSet<>(Arrays.asList('&', '/'));

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
