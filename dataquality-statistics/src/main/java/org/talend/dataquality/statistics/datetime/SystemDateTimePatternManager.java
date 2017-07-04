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
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date and time patterns manager with system default definitions.
 * 
 * @author mzhao
 */
public class SystemDateTimePatternManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemDateTimePatternManager.class);

    private static Locale DEFAULT_LOCALE = Locale.US;

    private static Locale SYSTEM_LOCALE = Locale.getDefault();

    private static List<Map<Pattern, String>> DATE_PATTERN_GROUP_LIST = new ArrayList<Map<Pattern, String>>();

    private static List<Map<Pattern, String>> TIME_PATTERN_GROUP_LIST = new ArrayList<Map<Pattern, String>>();

    private static Map<String, DateTimeFormatter> dateTimeFormatterCache = new HashMap<String, DateTimeFormatter>();

    private static final String PATTERN_SUFFIX_ERA = " G"; //$NON-NLS-1$

    static {
        try {
            // Load date patterns
            loadPatterns("DateRegexesGrouped.txt", DATE_PATTERN_GROUP_LIST);
            // Load time patterns
            loadPatterns("TimeRegexes.txt", TIME_PATTERN_GROUP_LIST);
        } catch (IOException e) {
            LOGGER.error("Unable to get date patterns.", e);
        }

    }

    private static void loadPatterns(String patternFileName, List<Map<Pattern, String>> patternParsers) throws IOException {
        InputStream stream = SystemDateTimePatternManager.class.getResourceAsStream(patternFileName);
        try {
            List<String> lines = IOUtils.readLines(stream, "UTF-8");
            Map<Pattern, String> currentGroupMap = new LinkedHashMap<Pattern, String>();
            patternParsers.add(currentGroupMap);
            for (String line : lines) {
                if (!"".equals(line.trim())) { // Not empty
                    if (line.startsWith("--")) { // group separator
                        currentGroupMap = new LinkedHashMap<Pattern, String>();
                        patternParsers.add(currentGroupMap);
                    } else {
                        String[] lineArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, "\t");
                        String format = lineArray[0];
                        Pattern pattern = Pattern.compile(lineArray[1]);
                        currentGroupMap.put(pattern, format);
                    }
                }
            }
            stream.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Whether the given string value is a date or not.
     * 
     * @param value
     * @return true if the value is a date.
     */
    public static boolean isDate(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        // The length of date strings must not be less than 6, and must not exceed 64.
        if (value.length() < 6 || value.length() > 64) {
            return false;
        }
        return isDateTime(DATE_PATTERN_GROUP_LIST, value);
    }

    /**
     * Check if the value passed is a time or not.
     * 
     * @param value
     * @return true if the value is type "Time", false otherwise.
     */
    public static boolean isTime(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        // The length of date strings must not be less than 4, and must not exceed 24.
        if (value.length() < 4 || value.length() > 24) {
            return false;
        }
        return isDateTime(TIME_PATTERN_GROUP_LIST, value);
    }

    private static boolean isDateTime(List<Map<Pattern, String>> patternGroupList, String value) {
        if (StringUtils.isNotEmpty(value)) {
            // at least 3 digit
            boolean hasEnoughDigits = false;
            int digitCount = 0;
            for (int i = 0; i < value.length(); i++) {
                char ch = value.charAt(i);
                if (ch >= '0' && ch <= '9') {
                    digitCount++;
                    if (digitCount > 2) {
                        hasEnoughDigits = true;
                        break;
                    }
                }
            }
            if (!hasEnoughDigits) {
                return false;
            }

            // Check the value with a list of regex patterns
            for (Map<Pattern, String> patternMap : patternGroupList) {
                for (Entry<Pattern, String> entry : patternMap.entrySet()) {
                    Pattern parser = entry.getKey();
                    try {
                        if (parser.matcher(value).find()) {
                            String dateFormat = entry.getValue();
                            if (isMatchDateTimePattern(value, dateFormat, SYSTEM_LOCALE)) {
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }
        return false;
    }

    /**
     * Replace the value with date pattern string.
     * 
     * @param value
     * @return date pattern string.
     */
    public static Set<String> datePatternReplace(String value) {
        return dateTimePatternReplace(DATE_PATTERN_GROUP_LIST, value);
    }

    /**
     * Replace the value with time pattern string.
     * 
     * @param value
     * @return
     */
    public static Set<String> timePatternReplace(String value) {
        return dateTimePatternReplace(TIME_PATTERN_GROUP_LIST, value);
    }

    private static Set<String> dateTimePatternReplace(List<Map<Pattern, String>> patternGroupList, String value) {
        if (StringUtils.isEmpty(value)) {
            return Collections.singleton(StringUtils.EMPTY);
        }
        HashSet<String> resultSet = new HashSet<>();
        for (Map<Pattern, String> patternMap : patternGroupList) {
            for (Entry<Pattern, String> entry : patternMap.entrySet()) {
                Pattern parser = entry.getKey();
                if (parser.matcher(value).find()) {
                    resultSet.add(entry.getValue());
                }
            }
            if (!resultSet.isEmpty()) {
                return resultSet;
            }
        }
        return resultSet;
    }

    private static DateTimeFormatter getDateTimeFormatterByPattern(String customPattern, Locale locale) {
        String localeStr = locale.toString();
        DateTimeFormatter formatter = dateTimeFormatterCache.get(customPattern + localeStr);
        if (formatter == null) {
            try {
                formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(customPattern)
                        .toFormatter(locale);
                // TDQ-13936 add Chronology for specified Locale.
                if (customPattern != null && customPattern.endsWith(PATTERN_SUFFIX_ERA)) {
                    formatter = ChronologyParameterManager.getDateTimeFormatterWithChronology(formatter, locale);
                }
            } catch (IllegalArgumentException e) {
                return null;
            }
            dateTimeFormatterCache.put(customPattern + localeStr, formatter);
        }
        return formatter;
    }

    private static boolean validateWithDateTimeFormatter(String value, DateTimeFormatter formatter) {
        if (formatter != null) {
            try {
                final TemporalAccessor temporal = formatter.parse(value);
                if (temporal != null && (temporal.query(TemporalQueries.localDate()) != null
                        || temporal.query(TemporalQueries.localTime()) != null)) {
                    return true;
                }
            } catch (DateTimeParseException e) {
                return false;
            }
        }
        return false;
    }

    static boolean isMatchDateTimePattern(String value, String pattern, Locale locale) {
        if (pattern == null) {
            return false;
        }

        // firstly, try with user-defined locale
        Locale correctLocale = locale;
        // TDQ-13936 Guess locale by the end of value when the pattern is "yyyy-MM-dd G"
        if (pattern.endsWith(PATTERN_SUFFIX_ERA)) {
            Locale guessLocale = ChronologyParameterManager.guessLocaleByEra(value);
            if (!DEFAULT_LOCALE.equals(guessLocale)) {
                correctLocale = guessLocale;
            }
        }
        final DateTimeFormatter formatter = getDateTimeFormatterByPattern(pattern, correctLocale);
        if (validateWithDateTimeFormatter(value, formatter)) {
            return true;
        } else {
            if (!DEFAULT_LOCALE.equals(correctLocale)) {
                // try with LOCALE_US if user defined locale is not US
                final DateTimeFormatter formatterUS = getDateTimeFormatterByPattern(pattern, Locale.US);
                if (validateWithDateTimeFormatter(value, formatterUS)) {
                    return true;
                }
            }
        }
        return false;
    }
}
