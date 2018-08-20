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

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Date and time patterns manager with system default definitions.
 * 
 * @deprecated use {@link CustomDateTimePatternManager} instead for better performance
 */
public class DateTimePatternManager {

    private static final Locale DEFAULT_LOCALE = Locale.US;

    private static Map<DateTimeFormatter, String> DATE_FORMATTERS;

    private static Map<DateTimeFormatter, String> TIME_FORMATTERS;

    static {
        try {
            DATE_FORMATTERS = loadDateFormats("DateFormats.txt");
            TIME_FORMATTERS = loadDateFormats("TimeFormats.txt");
        } catch (IOException e) {
            System.err.println("Unable to get date patterns.");
        }

    }

    private static Map<DateTimeFormatter, String> loadDateFormats(String patternFileName) throws IOException {
        Map<DateTimeFormatter, String> parsers = new LinkedHashMap<DateTimeFormatter, String>();
        InputStream stream = DateTimePatternManager.class.getResourceAsStream(patternFileName);
        List<String> lines = IOUtils.readLines(stream);
        for (String line : lines) {
            if (!"".equals(line.trim())) {
                String[] localePatternText = line.trim().split("\t");
                parsers.put(DateTimeFormatter.ofPattern(localePatternText[1], getLocaleFromStr(localePatternText[0])),
                        localePatternText[1]);
            }
        }
        stream.close();
        return parsers;
    }

    private static Locale getLocaleFromStr(String localeStr) {
        if (localeStr != null) {
            String[] parts = localeStr.split("_");
            if (parts.length == 1) {
                return new Locale(parts[0]);
            } else if (parts.length == 2) {
                return new Locale(parts[0], parts[1]);
            }
        }
        // any other case
        return DEFAULT_LOCALE;
    }

    /**
     * Check if the given string value is a date or not.
     * 
     * @param value
     * @return true if the value is a date.
     */
    public static boolean isDate(String value) {
        return isDateTime(DATE_FORMATTERS, value);
    }

    /**
     * Whether the given string value is a date or not using the default jvm locale.
     *
     * @param value the value to check if it's a date.
     * @param customDatePatterns the list of custom date patterns.
     * @return true if the value is a date.
     */
    @Deprecated
    public static boolean isDate(String value, List<String> customDatePatterns) {
        return isDate(value, customDatePatterns, DEFAULT_LOCALE);
    }

    /**
     * Whether the given string value is a date or not.
     *
     * @param value the value to check if it's a date.
     * @param customDatePatterns the list of custom date patterns.
     * @param locale the locale to use.
     * @return true if the value is a date.
     */
    @Deprecated
    public static boolean isDate(String value, List<String> customDatePatterns, Locale locale) {
        for (String customPattern : customDatePatterns) {
            if (isMatchCustomPattern(value, customPattern, locale)) {
                return true;
            }
        }
        return isDate(value);
    }

    /**
     * Check if the given string value is a time or not.
     * 
     * @param value
     * @return true if the value is type "Time", false otherwise.
     */
    public static boolean isTime(String value) {
        return isDateTime(TIME_FORMATTERS, value);
    }

    /**
     * Whether the given string value is a date or not using the default jvm locale.
     *
     * @param value the value to check if it's a date.
     * @param customTimePatterns the list of custom date patterns.
     * @return true if the value is a date.
     */
    @Deprecated
    public static boolean isTime(String value, List<String> customTimePatterns) {
        return isTime(value, customTimePatterns, DEFAULT_LOCALE);
    }

    @Deprecated
    public static boolean isTime(String value, List<String> customTimePatterns, Locale locale) {
        for (String customPattern : customTimePatterns) {
            if (isMatchCustomPattern(value, customPattern, locale)) {
                return true;
            }
        }
        return isTime(value);
    }

    private static boolean isDateTime(Map<DateTimeFormatter, String> parsers, String value) {
        if (StringUtils.isNotEmpty(value)) {
            // 1. The length of date characters should not exceed 64.
            if (value.length() > 64) {
                return false;
            }
            // 2. Check it by list of patterns
            for (DateTimeFormatter formatter : parsers.keySet()) {
                try {
                    if (formatter.parse(value) != null) {
                        return true;
                    }
                } catch (DateTimeParseException e) {
                    // continue
                }
            }
        }
        return false;
    }

    private static String dateTimePatternReplace(Map<DateTimeFormatter, String> parsers, String value) {
        if (StringUtils.isEmpty(value)) {
            return StringUtils.EMPTY;
        }
        for (DateTimeFormatter formatter : parsers.keySet()) {
            try {
                if (formatter.parse(value) != null) {
                    return parsers.get(formatter);
                }
            } catch (DateTimeParseException e) {
                // continue
            }
        }
        return value;

    }

    private static boolean isMatchCustomPattern(String value, String customPattern, Locale locale) {
        if (customPattern == null) {
            return false;
        }
        try {// firstly, try with user-defined locale
            DateTimeFormatter.ofPattern(customPattern, locale).parse(value);
            return true;
        } catch (DateTimeParseException | IllegalArgumentException e) {
            if (!DEFAULT_LOCALE.equals(locale)) {
                try {// try with LOCALE_US if user defined locale is not US
                    DateTimeFormatter.ofPattern(customPattern, DEFAULT_LOCALE).parse(value);
                    return true;
                } catch (DateTimeParseException | IllegalArgumentException e1) {
                    // return false
                }
            }
        }
        return false;
    }

    @Deprecated
    public static String replaceByDateTimePattern(String value, String customPattern) {
        return replaceByDateTimePattern(value, customPattern, DEFAULT_LOCALE);
    }

    @Deprecated
    public static String replaceByDateTimePattern(String value, String customPattern, Locale locale) {
        return replaceByDateTimePattern(value, Collections.singletonList(customPattern), locale);
    }

    @Deprecated
    public static String replaceByDateTimePattern(String value, List<String> customPatterns) {
        return replaceByDateTimePattern(value, customPatterns, DEFAULT_LOCALE);
    }

    @Deprecated
    public static String replaceByDateTimePattern(String value, List<String> customPatterns, Locale locale) {
        for (String customPattern : customPatterns) {
            if (isMatchCustomPattern(value, customPattern, locale)) {
                return customPattern;
            }
        }
        // otherwise, replace with system date pattern manager.
        return systemPatternReplace(value);
    }

    private static String systemPatternReplace(String value) {
        String pattern = dateTimePatternReplace(DATE_FORMATTERS, value);
        if (pattern.equals(value)) {
            pattern = dateTimePatternReplace(TIME_FORMATTERS, value);
        }
        return pattern;
    }

}
