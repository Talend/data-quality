package org.talend.dataquality.common.character;

public final class StringChecker {

    public static boolean containsLowerAndUpperCase(String str) {
        boolean hasLowerCase = false;
        boolean hasUpperCase = false;

        int cpCount = str.codePointCount(0, str.length());
        for (int i = 0; i < cpCount; i++) {
            int codePointPos = str.offsetByCodePoints(0, i);
            int codePoint = str.codePointAt(codePointPos);
            if (Character.isUpperCase(codePoint)) {
                if (hasLowerCase)
                    return true;
                hasUpperCase = true;
            } else if (Character.isLowerCase(codePoint)) {
                if (hasUpperCase)
                    return true;
                hasLowerCase = true;
            }
        }
        return false;
    }

    public static boolean isTitleCase(String str, String delimiters) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        for (String stringPart : str.split(delimiters)) {
            if (stringPart.isEmpty()) {
                continue;
            }
            int ind = 0;
            int cpCount = stringPart.codePointCount(0, stringPart.length());
            int codePoint = stringPart.codePointAt(stringPart.offsetByCodePoints(0, ind++));
            while (ind < cpCount && !Character.isLetter(codePoint)) {
                codePoint = stringPart.codePointAt(stringPart.offsetByCodePoints(0, ind));
                ind++;
            }

            if (ind < cpCount && Character.isLowerCase(codePoint)) {
                return false;
            }
            ind++;
            while (ind < cpCount) {
                codePoint = stringPart.codePointAt(stringPart.offsetByCodePoints(0, ind));
                if (Character.isUpperCase(codePoint)) {
                    return false;
                }
                ind++;
            }
        }
        return true;
    }

    public static boolean containsUpperCase(String str) {
        int cpCount = str.codePointCount(0, str.length());
        for (int i = 0; i < cpCount; i++) {
            int codePointPos = str.offsetByCodePoints(0, i);
            int codePoint = str.codePointAt(codePointPos);
            if (Character.isUpperCase(codePoint)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsLowerCase(String str) {
        int cpCount = str.codePointCount(0, str.length());
        for (int i = 0; i < cpCount; i++) {
            int codePointPos = str.offsetByCodePoints(0, i);
            int codePoint = str.codePointAt(codePointPos);
            if (Character.isLowerCase(codePoint)) {
                return true;
            }
        }
        return false;
    }
}
