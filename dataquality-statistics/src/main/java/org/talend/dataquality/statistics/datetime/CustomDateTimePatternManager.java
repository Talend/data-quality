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

import org.apache.commons.lang3.tuple.Pair;

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
 * 
 * @author mzhao
 *
 */
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
        resultPatternSet.addAll(systemPatternReplace(value).getLeft().keySet());
        return resultPatternSet;
    }

    public static Pair<Map<String, Locale>, Map<Pattern, String>> replaceByDateTimePatternWithGroup(String value) {
        Pair<Map<String, Locale>, Map<Pattern, String>> resultPatternSet = systemPatternReplace(value);

        return resultPatternSet;
    }

    private static Pair<Map<String, Locale>, Map<Pattern, String>> systemPatternReplace(String value) {
        Pair<Map<String, Locale>, Map<Pattern, String>> resultPatternSet = SystemDateTimePatternManager
                .datePatternReplaceWithGroup(value);
        if (resultPatternSet.getRight().isEmpty()) {
            resultPatternSet = SystemDateTimePatternManager.timePatternReplaceWithGroup(value);
        }
        return resultPatternSet;
    }
}
