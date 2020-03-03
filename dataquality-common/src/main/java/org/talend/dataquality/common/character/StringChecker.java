package org.talend.dataquality.common.character;

public final class StringChecker {

    /**
     * Check if the string contains both lowercase and uppercase letters.
     * 
     * @param str the string to check.
     * @return true if the string has at least one lowercase letter and one uppercase letter, false otherwise.
     */
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

    /**
     * Check if the string is in Title case, i.e. if every word starts with 1 capital letter and the remaining is in
     * lowercase. the string is split using a delimiter pattern.
     * 
     * @param str the string to check.
     * @param delimiters the delimiter pattern to split the string.
     * @return true if every word is in title case, false otherwise or if the string is null or empty.
     */
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

    /**
     * Check if the string contains uppercase letters.
     *
     * @param str the string to check.
     * @return true if the string contains at least one uppercase letter, false otherwise.
     */
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

    /**
     * Check if the string contains lowercase letters.
     *
     * @param str the string to check.
     * @return true if the string contains at least one lowercase letter, false otherwise.
     */
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
