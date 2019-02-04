package org.talend.dataquality.semantic.utils;

public class RegexUtils {

    private RegexUtils() {
    }

    /**
     * Keep only one '$'at the end and remove other no need character '$'
     *
     * @param oldPatternString
     * @return pattern string after removal
     */
    public static String removeStartingAndEndingAnchors(String oldPatternString) {
        String patternStr = oldPatternString;
        while (patternStr.startsWith("^")) {
            patternStr = patternStr.substring(1);
        }
        // ends with dollar sign as end-of-line anchor but not a literal dollar
        while (patternStr.endsWith("$") && !patternStr.replace("\\\\", "").endsWith("\\$")) {
            patternStr = patternStr.substring(0, patternStr.length() - 1);
        }
        return patternStr;
    }
}
