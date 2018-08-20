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
package org.talend.dataquality.statistics.datetime;

import org.talend.dataquality.statistics.type.SortedList;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Customized date time pattern manager.
 * NOT USED ANYMORE
 * USE SystemDateTimePatternManager
 * 
 * @author mzhao
 *
 */
@Deprecated
public final class CustomDateTimePatternManager {

    private static final Locale DEFAULT_LOCALE = Locale.US;

    public static boolean isDate(String value, List<String> customPatterns) {
        return isDate(value, customPatterns, DEFAULT_LOCALE);
    }

    public static boolean isDate(String value, List<String> customPatterns, Locale locale) {
        // use custom patterns first
        if (isMatchCustomPatterns(value, customPatterns, locale)) {
            return true;
        }
        // validate using system pattern manager
        return SystemDateTimePatternManager.isDate(value);
    }

    public static boolean isTime(String value, List<String> customPatterns) {
        return isTime(value, customPatterns, DEFAULT_LOCALE);
    }

    public static boolean isTime(String value, List<String> customPatterns, Locale locale) {
        // use custom patterns first
        if (isMatchCustomPatterns(value, customPatterns, locale)) {
            return true;
        }
        // validate using system pattern manager
        return SystemDateTimePatternManager.isTime(value);
    }

    public static boolean isMatchCustomPatterns(String value, List<String> customPatterns, Locale locale) {
        return customPatterns.stream()
                .filter(pattern -> SystemDateTimePatternManager.isMatchDateTimePattern(value, pattern, locale)).findAny()
                .isPresent();
    }

    // for junit only
    static Set<String> replaceByDateTimePattern(String value, String customPattern) {
        return replaceByDateTimePattern(value, customPattern, DEFAULT_LOCALE);
    }

    static Set<String> replaceByDateTimePattern(String value, String customPattern, Locale locale) {
        return replaceByDateTimePattern(value, Collections.singletonList(customPattern), locale);
    }

    public static Set<String> replaceByDateTimePattern(String value, List<String> customPatterns) {
        return replaceByDateTimePattern(value, customPatterns,
                customPattern -> SystemDateTimePatternManager.isMatchDateTimePattern(value, customPattern));
    }

    public static Set<String> replaceByDateTimePattern(String value, List<String> customPatterns, Locale locale) {
        return replaceByDateTimePattern(value, customPatterns,
                customPattern -> SystemDateTimePatternManager.isMatchDateTimePattern(value, customPattern, locale));
    }

    private static Set<String> replaceByDateTimePattern(String value, List<String> customPatterns,
            Predicate<String> isMatchDateTimePattern) {
        Set<String> resultPatternSet = new HashSet<>();
        for (String customPattern : customPatterns) {
            if (isMatchDateTimePattern.test(customPattern)) {
                resultPatternSet.add(customPattern);
            }
        }
        // otherwise, replace with system date pattern manager.
        resultPatternSet.addAll(getPatterns(value, new SortedList<>()).keySet());
        return resultPatternSet;
    }

    /**
     * Find the patterns for a given value
     * @param value
     * @param frequentDatePatternsCache
     * @return the list of found patterns AND the group with the pattern and the regex for the cache
     */
    public static Map<String, Locale> getPatterns(String value, SortedList<Map<Pattern, String>> frequentDatePatternsCache) {
        Map<String, Locale> resultPatternSet = SystemDateTimePatternManager.getDatePatterns(value, frequentDatePatternsCache);
        if (resultPatternSet.isEmpty()) {
            resultPatternSet = SystemDateTimePatternManager.getTimePatterns(value);
        }
        return resultPatternSet;
    }
}
