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
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
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
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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

    private static final String PATTERN_SUFFIX_ERA = "G"; //$NON-NLS-1$

    private static final Pattern PATTERN_FILTER_DATE = Pattern.compile("[ \\-]\\d|\\d[./+W\\u5E74]\\d|^\\d{8}$"); //'\u5E74' stands for '年'

    private static final List<Locale> LOCALES = getDistinctLanguagesLocales();

    static {
        // Load date patterns
        loadPatterns("DateRegexesGrouped.txt", DATE_PATTERN_GROUP_LIST);
        // Load time patterns
        loadPatterns("TimeRegexes.txt", TIME_PATTERN_GROUP_LIST);

    }

    private static List<Locale> getDistinctLanguagesLocales() {
        Set<String> languages = new HashSet<>();
        List<Locale> locales = new ArrayList<>();
        for (Locale locale : DateFormat.getAvailableLocales()) {
            if (!languages.contains(locale.getLanguage())) {
                locales.add(locale);
                languages.add(locale.getLanguage());
            }
        }
        return locales;
    }

    private static void loadPatterns(String patternFileName, List<Map<Pattern, String>> patternParsers) {
        InputStream stream = SystemDateTimePatternManager.class.getResourceAsStream(patternFileName);
        try {
            List<String> lines = IOUtils.readLines(stream, "UTF-8");
            Map<Pattern, String> currentGroupMap = new LinkedHashMap<Pattern, String>();
            patternParsers.add(currentGroupMap);
            for (String line : lines) {
                if (!"".equals(line.trim())) { // Not empty
                    if (line.startsWith("--")) { // group separator
                        currentGroupMap = new LinkedHashMap<>();
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
        if (checkDatesPreconditions(value))
            return findOneDateTimePattern(DATE_PATTERN_GROUP_LIST, value).isPresent();
        return false;
    }

    /**
     * Whether the given string value is a date or not and the pattern associated
     *
     * @param value to check
     * @return the pair pattern, regex if it's a regex, null otherwise
     */
    public static Optional<Pair<Pattern, DateTimeFormatter>> findOneDatePattern(String value) {
        if (checkDatesPreconditions(value))
            return findOneDateTimePattern(DATE_PATTERN_GROUP_LIST, value);
        return Optional.empty();
    }

    /**
     * Check if the value passed is a time or not.
     * 
     * @param value
     * @return true if the value is type "Time", false otherwise.
     */
    public static boolean isTime(String value) {
        // The length of date strings must not be less than 4, and must not exceed 24.
        return StringUtils.isNotEmpty(value) && value.length() >= 4 && value.length() <= 24 && checkEnoughDigits(value)
                && findOneDateTimePattern(TIME_PATTERN_GROUP_LIST, value).isPresent();
    }

    /**
     * Not empty
     * The length of date strings must not be less than 6, and must not exceed 64.
     * TDQ-14894: Improve Date discovery by listing the separators
     * @param value
     * @return true is the value valids the preconditions
     */
    private static boolean checkDatesPreconditions(String value) {
        return (StringUtils.isNotEmpty(value) && value.length() >= 6 && value.length() <= 64
                && PATTERN_FILTER_DATE.matcher(value).find() && checkEnoughDigits(value));
    }

    /**
     * The value must have at least 3 digits
     * @param value
     * @return true is the value contains at least 3 digits
     */
    private static boolean checkEnoughDigits(String value) {
        int digitCount = 0;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch >= '0' && ch <= '9') {
                digitCount++;
                if (digitCount > 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Optional<Pair<Pattern, DateTimeFormatter>> findOneDateTimePattern(List<Map<Pattern, String>> patternGroupList,
            String value) {
        // Check the value with a list of regex patterns
        for (Map<Pattern, String> patternMap : patternGroupList) {
            for (Entry<Pattern, String> entry : patternMap.entrySet()) {
                Pattern parser = entry.getKey();
                if (parser.matcher(value).find()) {
                    Optional<DateTimeFormatter> dateTimeFormatter = getDateTimeFormatter(value, entry.getValue());
                    return dateTimeFormatter.isPresent() ? Optional.of(Pair.of(parser, dateTimeFormatter.get()))
                            : Optional.empty();
                }
            }
        }
        return Optional.empty();
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

    /**
     * 
     * @param customPattern A date pattern such as "yyyy-MM-dd G","dd/MM/uuuu"
     * @param locale
     * @return Return a DateTimeFormatter.
     */
    public static DateTimeFormatter getDateTimeFormatterByPattern(String customPattern, Locale locale) {
        if (locale == null || StringUtils.isEmpty(customPattern)) {
            return null;
        }
        String localeStr = locale.toString();
        DateTimeFormatter formatter = dateTimeFormatterCache.get(customPattern + localeStr);
        if (formatter == null) {
            try {
                // TDQ-13936 add Chronology for specified Locale.
                if (customPattern.contains(PATTERN_SUFFIX_ERA)) {
                    formatter = ChronologyParameterManager.getDateTimeFormatterWithChronology(customPattern, locale);
                } else {
                    // TDQ-14421 use ResolverStyle.STRICT to validate a date. such as "2017-02-29" should be
                    // invalid.STRICT model for pattern without G,should replace 'y' with 'u'.see Java DOC.
                    String customPatternStrict = customPattern.replace('y', 'u');
                    formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(customPatternStrict)
                            .toFormatter(locale).withResolverStyle(ResolverStyle.STRICT);
                }
            } catch (IllegalArgumentException e) {
                LOGGER.debug(e.getMessage(), e);
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

    public static boolean isMatchDateTimePattern(String value, String pattern, Locale locale) {
        return validateWithDateTimeFormatter(value, getDateTimeFormatterByPattern(pattern, locale));
    }

    public static boolean isMatchDateTimePattern(String value, DateTimeFormatter dateTimeFormatter) {
        return validateWithDateTimeFormatter(value, dateTimeFormatter);
    }

    public static boolean isMatchDateTimePattern(String value, String pattern) {
        return getDateTimeFormatter(value, pattern).isPresent();
    }

    public static Optional<DateTimeFormatter> getDateTimeFormatter(String value, String pattern) {
        DateTimeFormatter dateTimeFormatterByPattern;
        int i = 0;

        do {
            dateTimeFormatterByPattern = getDateTimeFormatterByPattern(pattern, LOCALES.get(i++));
        } while (i < LOCALES.size() && !validateWithDateTimeFormatter(value, dateTimeFormatterByPattern));

        return i < LOCALES.size() ? Optional.of(dateTimeFormatterByPattern) : Optional.empty();
    }
}
