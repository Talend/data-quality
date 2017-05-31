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
package org.talend.dataquality.statistics.datetime.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generate regex from java Datetime pattern
 */
public class RegexGenerator {

    Map<String, String> PATTERN_REGEX_MAP = new LinkedHashMap<String, String>() {

        private static final long serialVersionUID = 1L;

        {

            put("[", "\\[");
            put("]", "\\]");
            put("-", "\\-");
            put(".", "\\.");
            put("/", "\\/");

            // regex with numbers
            put("XXX", "[+-](0[0-9]|1[0-2]):00");
            put("Z", "[+-](0[0-9]|1[0-2])00");
            put("z", "[A-Z]{2,5}");
            put("W", "[1-7]");
            put("w", "([1-4]?[0-9]|5[0-2])");
            put("DDD", "[0-9]{1,3}");

            put("HH", "(0[0-9]|1[0-9]|2[0-3])");
            put("H", "(0?[0-9]|1[0-9]|2[0-3])");
            put("hh", "(0[1-9]|1[0-2])"); // in 12-hour format time, 00 o'clock is written as 12h AM.
            put("h", "(0?[1-9]|1[0-2])");
            put("mm", "([0-5][0-9])");
            put("m", "([0-5]?[0-9])");
            put("ss", "([0-5][0-9])");
            put("s", "([0-5]?[0-9])");
            put("SSS", "([0-9]{3})");
            put("S", "[0-9]");
            put("nnnnnnnnn", "[0-9]{9}");

            // replace the 'a' char by AM|PM
            put("a", "\\p{L}{2}");

            put("yyyy", "[0-9]{4}");
            put("yy", "[0-9]{2}");
            put("y", "[0-9]{2,4}"); // TO CHECK
            put("MMMM", "(\\p{L}{3,10})");
            put("MMM", "(([A-Z]\\p{L}{2})|([a-z]\\p{L}{2,3}\\.?))");// this line must be after the replacement of 'a'
            put("MM", "(0[1-9]|1[0-2])");
            put("M", "([1-9]|1[0-2])");
            put("dd", "(0[1-9]|[1-2][0-9]|3[0-1])");
            put("d", "([1-9]|[1-2][0-9]|3[0-1])");

            // can use \\p{L} starting from here
            put("EEEE", "(\\p{L}{3,10})");// TO CHECK
            put("EEE", "(\\p{L}{3,5})");

            put("VV", "\\p{L}{4,10}\\/\\p{L}{4,15}");

            // replace the 'G' char by AD|BC
            put("G", "(AD|BC)");

            put("'", "");
        }
    };

    /**
     * replace literal parts of the datetime pattern by symbols to keep them from being converted, and recover them after
     * conversion.
     */
    public String convertPatternToRegex(String pattern) {
        String regex = pattern;
        regex = regex.replace("' h '", "*****");
        regex = regex.replace("Uhr", "===");
        regex = regex.replace("'Z'", "___");
        regex = regex.replace("'W'", "+++");
        regex = regex.replace("''", "!!");
        for (String key : PATTERN_REGEX_MAP.keySet()) {
            regex = regex.replace(key, PATTERN_REGEX_MAP.get(key));
        }
        regex = regex.replace("===", "Uhr");
        regex = regex.replace("___", "Z");
        regex = regex.replace("+++", "W");
        regex = regex.replace("!!", "'");
        regex = regex.replace("*****", " h ");

        return regex;
    }
}
