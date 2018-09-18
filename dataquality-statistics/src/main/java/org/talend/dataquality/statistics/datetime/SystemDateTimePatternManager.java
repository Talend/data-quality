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
import java.text.DateFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.statistics.type.SortedList;

/**
 * Date and time patterns manager with system default definitions.
 * 
 * @author mzhao
 */
public class SystemDateTimePatternManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemDateTimePatternManager.class);

    private static final List<Map<Pattern, String>> DATE_PATTERN_GROUP_LIST = new ArrayList<>();

    private static final String MONTHS = "MONTHS";

    private static final String SHORT_MONTHS = "SHORT_MONTHS";

    private static final String WEEKDAYS = "WEEKDAYS";

    private static final String SHORT_WEEKDAYS = "SHORT_WEEKDAYS";

    private static final String AM_PM = "AM_PM";

    private static final String ERAS = "ERAS";

    /**
     * give for a worg group, the list of words and their locales
     * The word groups available are MONTHS, SHORT_MONTHS, WEEKDAYS, SHORT_WEEKDAYS, AM_PM and ERAS
     * For example, "MONTHS" -> < february -> [en] ; février -> [fr] >
     */
    private static final Map<String, Map<String, Set<Locale>>> WORD_GROUPS_TO_LANGUAGES_DATES_WORDS = new HashMap<>();

    /**
     * give for each patterns, the list of word groups to check.
     * The word groups available are MONTHS, SHORT_MONTHS, WEEKDAYS, SHORT_WEEKDAYS, AM_PM and ERAS
     * For example the pattern "EEEE, MMMM d, yyyy h:mm:ss a z" is associated to the list ["MONTHS"; "WEEKDAYS"; "AM_PM"]
     *
     */
    private static final Map<String, List<String>> PATTERN_TO_WORD_GROUPS = new HashMap<>();

    private static final List<Map<Pattern, String>> TIME_PATTERN_GROUP_LIST = new ArrayList<>();

    private static final Map<String, DateTimeFormatter> dateTimeFormatterCache = new HashMap<>();

    private static final String PATTERN_SUFFIX_ERA = "G"; //$NON-NLS-1$

    private static final Pattern PATTERN_FILTER_DATE//
            = Pattern.compile("[ \\-]\\d|\\d[./+W\\u5E74]\\d|^\\d{8}$"); // '\u5E74' stands for '年'

    private static final Set<Locale> LOCALES = getDistinctLanguagesLocales();

    static {
        loadLanguagesDatesWords();
        // Load date patterns
        loadPatterns("DateRegexesGrouped.txt", DATE_PATTERN_GROUP_LIST);
        // Load time patterns
        loadPatterns("TimeRegexes.txt", TIME_PATTERN_GROUP_LIST);

    }

    private static void loadLanguagesDatesWords() {
        for (Locale locale : getDistinctLanguagesLocales()) {
            final DateFormatSymbols dfs = new DateFormatSymbols(locale);
            buildWordsToLocales(MONTHS, Arrays.asList(dfs.getMonths()), locale);
            buildWordsToLocales(SHORT_MONTHS, Arrays.asList(dfs.getShortMonths()), locale);
            buildWordsToLocales(WEEKDAYS, Arrays.asList(dfs.getWeekdays()), locale);
            buildWordsToLocales(SHORT_WEEKDAYS, Arrays.asList(dfs.getShortWeekdays()), locale);
            buildWordsToLocales(AM_PM, Arrays.asList(dfs.getAmPmStrings()), locale);
            buildWordsToLocales(ERAS, Arrays.asList(dfs.getEras()), locale);
        }
    }

    private static void buildWordsToLocales(final String wordGroup, final List<String> languagesWords, Locale currentLocale) {
        Map<String, Set<Locale>> languagesDatesWords = WORD_GROUPS_TO_LANGUAGES_DATES_WORDS.get(wordGroup);
        if (languagesDatesWords == null) {
            languagesDatesWords = new HashMap<>();
            WORD_GROUPS_TO_LANGUAGES_DATES_WORDS.put(wordGroup, languagesDatesWords);
        }
        for (String languageWord : languagesWords) {
            if (StringUtils.isNotEmpty(languageWord)) {
                String lowerCaseLanguageWord = languageWord.toLowerCase();
                Set<Locale> locales = languagesDatesWords.get(lowerCaseLanguageWord);
                if (locales == null) {
                    locales = new HashSet<>();
                    languagesDatesWords.put(lowerCaseLanguageWord, locales);
                }
                locales.add(currentLocale);
            }
        }
    }

    private static Set<Locale> getDistinctLanguagesLocales() {
        Set<Locale> locales = new LinkedHashSet<>();
        // we add these specific languages first because they are the most frequent.
        for (String lang : new String[] { "en", "fr", "de", "it", "es", "ja", "zh" }) {
            locales.add(Locale.forLanguageTag(lang));
        }
        for (Locale locale : DateFormat.getAvailableLocales()) {
            if (StringUtils.isNotEmpty(locale.getLanguage())) {
                locales.add(Locale.forLanguageTag(locale.getLanguage()));
            }
        }
        return locales;
    }

    private static void loadPatterns(String patternFileName, List<Map<Pattern, String>> patternParsers) {
        InputStream stream = SystemDateTimePatternManager.class.getResourceAsStream(patternFileName);
        try {
            List<String> lines = IOUtils.readLines(stream, "UTF-8");
            Map<Pattern, String> currentGroupMap = new LinkedHashMap<>();
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
                        loadPatternToLanguagesDatesWords(format);
                    }
                }
            }
            stream.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static void loadPatternToLanguagesDatesWords(String format) {
        List<String> languagesWordsList = new ArrayList<>();
        if (format.contains("MMMM"))
            languagesWordsList.add(MONTHS);
        else if (format.contains("MMM"))
            languagesWordsList.add(SHORT_MONTHS);
        if (format.contains("EEEE"))
            languagesWordsList.add(WEEKDAYS);
        else if (format.contains("EEE"))
            languagesWordsList.add(SHORT_WEEKDAYS);
        if (format.contains("G"))
            languagesWordsList.add(ERAS);
        if (format.contains("a"))
            languagesWordsList.add(AM_PM);
        PATTERN_TO_WORD_GROUPS.put(format, languagesWordsList);
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
    private static Optional<Pair<Pattern, DateTimeFormatter>> findOneDatePattern(String value) {
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
     * 
     * @param value
     * @return true is the value valids the preconditions
     */
    private static boolean checkDatesPreconditions(String value) {
        return (StringUtils.isNotEmpty(value) && value.length() >= 6 && value.length() <= 64
                && PATTERN_FILTER_DATE.matcher(value).find() && checkEnoughDigits(value));
    }

    /**
     * The value must have at least 3 digits
     * 
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
            boolean isFoundRegex = false;
            for (Entry<Pattern, String> entry : patternMap.entrySet()) {
                Matcher matcher = entry.getKey().matcher(value);
                if (matcher.find()) {
                    isFoundRegex = true;
                    Optional<DateTimeFormatter> dateTimeFormatter = validateWithPatternInAnyLocale(value, entry.getValue(),
                            matcher);
                    if (dateTimeFormatter.isPresent())
                        return Optional.of(Pair.of(entry.getKey(), dateTimeFormatter.get()));
                }

            }
            if (isFoundRegex)
                break;
        }
        return Optional.empty();
    }

    /**
     * Replace the value with date pattern string.
     * 
     * @param value
     * @return date pattern string.
     */
    @Deprecated
    public static Set<String> datePatternReplace(String value) {
        return getDateTimePatterns(DATE_PATTERN_GROUP_LIST, value, new SortedList<>()).keySet();
    }

    /**
     * Replace the value with date pattern string.
     *
     * @param value
     * @param frequentDatePatternsCache
     * @return the list of found patterns AND the group with the pattern and the regex for the cache
     */
    public static Map<String, Locale> getDatePatterns(String value, SortedList<Map<Pattern, String>> frequentDatePatternsCache) {
        return getDateTimePatterns(DATE_PATTERN_GROUP_LIST, value, frequentDatePatternsCache);
    }

    /**
     * Replace the value with time pattern string.
     * 
     * @param value
     * @return
     */
    @Deprecated
    public static Map<String, Locale> timePatternReplace(String value) {
        return getDateTimePatterns(TIME_PATTERN_GROUP_LIST, value, new SortedList<>());
    }

    /**
     * Replace the value with time pattern string.
     *
     * @param value
     * @return
     */
    public static Map<String, Locale> getTimePatterns(String value) {
        return getDateTimePatterns(TIME_PATTERN_GROUP_LIST, value, new SortedList<>());
    }

    private static Map<String, Locale> getDateTimePatterns(List<Map<Pattern, String>> patternGroupList, String value,
            SortedList<Map<Pattern, String>> frequentDatePatternsCache) {
        if (StringUtils.isEmpty(value)) {
            return Collections.singletonMap(StringUtils.EMPTY, null);
        }
        Optional<Map<String, Locale>> foundFrequentDatePatterns = findValueInFrequentDatePatternsCache(value,
                frequentDatePatternsCache);
        if (foundFrequentDatePatterns.isPresent())
            return foundFrequentDatePatterns.get();
        Map<String, Locale> resultMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(patternGroupList)) {
            for (Map<Pattern, String> patternMap : patternGroupList) {
                if (isFoundRegex(value, patternMap, resultMap)) {
                    frequentDatePatternsCache.addNewValue(patternMap);
                    return resultMap;
                }
            }
        }
        return resultMap;
    }

    private static Optional<Map<String, Locale>> findValueInFrequentDatePatternsCache(String value,
            SortedList<Map<Pattern, String>> frequentDatePatternsCache) {
        if (CollectionUtils.isNotEmpty(frequentDatePatternsCache)) {
            Map<String, Locale> resultMap = new HashMap<>();
            for (int j = 0; j < frequentDatePatternsCache.size(); j++) {
                Map<Pattern, String> cachedPattern = frequentDatePatternsCache.get(j).getLeft();
                if (isFoundRegex(value, cachedPattern, resultMap)) {
                    frequentDatePatternsCache.increment(j);
                    return Optional.of(resultMap);
                }
            }
        }
        return Optional.empty();
    }

    private static boolean isFoundRegex(String value, Map<Pattern, String> groupPattern, Map<String, Locale> resultMap) {
        boolean isFoundRegex = false;
        for (Entry<Pattern, String> entry : groupPattern.entrySet()) {
            Matcher matcher = entry.getKey().matcher(value);
            if (matcher.find()) {
                isFoundRegex = true;
                validateWithPatternInAnyLocale(value, entry.getValue(), matcher)
                        .ifPresent(opt -> resultMap.put(entry.getValue(), opt.getLocale()));
            }
        }
        return isFoundRegex;
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

    private static boolean isMatchDateTimePattern(String value, DateTimeFormatter formatter) {
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

    public static boolean isDate(String value, SortedList<Pair<Pattern, DateTimeFormatter>> orderedPatterns) {
        for (int j = 0; j < orderedPatterns.size(); j++) {
            Pair<Pattern, DateTimeFormatter> cachedPattern = orderedPatterns.get(j).getLeft();
            if (cachedPattern.getLeft().matcher(value).find() && isMatchDateTimePattern(value, cachedPattern.getRight())) {
                orderedPatterns.increment(j);
                return true;
            }
        }

        Optional<Pair<Pattern, DateTimeFormatter>> foundPattern = findOneDatePattern(value);
        foundPattern.ifPresent(pattern -> orderedPatterns.addNewValue(pattern));
        return foundPattern.isPresent();
    }

    public static Set<String> getDatePatterns() {
        Set<String> patterns = new HashSet<>();
        for (Map<Pattern, String> datePatternGroup : DATE_PATTERN_GROUP_LIST)
            for (String pattern : datePatternGroup.values())
                patterns.add(pattern);
        return patterns;
    }

    @Deprecated
    public static boolean isMatchDateTimePattern(String value, String pattern, Locale locale) {
        return findDateTimeFormatter(value, pattern, locale).isPresent();
    }

    @Deprecated
    public static boolean isMatchDateTimePattern(String value, String pattern) {
        return findDateTimeFormatter(value, pattern, LOCALES).isPresent();
    }

    private static Optional<DateTimeFormatter> findDateTimeFormatter(String value, String pattern, Set<Locale> locales) {
        for (Locale locale : locales) {
            final Optional<DateTimeFormatter> dateTimeFormatter = findDateTimeFormatter(value, pattern, locale);
            if (dateTimeFormatter.isPresent())
                return dateTimeFormatter;
        }
        return Optional.empty();
    }

    /**
     * validate a pattern with all existing locales
     * @param value to validate
     * @param pattern to use (for example dd/MM/yy)
     * @param matcher the regex matcher
     * @return the date time format if found
     */
    private static Optional<DateTimeFormatter> validateWithPatternInAnyLocale(String value, String pattern, Matcher matcher) {
        List<String> wordGroups = PATTERN_TO_WORD_GROUPS.get(pattern);
        if (CollectionUtils.isNotEmpty(wordGroups)) {
            if (matcher.groupCount() == wordGroups.size()) {
                List<Map<String, Set<Locale>>> languagesDatesWords = wordGroups.stream()
                        .map(wordGroup -> WORD_GROUPS_TO_LANGUAGES_DATES_WORDS.get(wordGroup)).collect(Collectors.toList());
                Set<Locale> locales = findLocales(languagesDatesWords, matcher);
                if (CollectionUtils.isNotEmpty(locales)) {
                    return findDateTimeFormatter(value, pattern, locales);
                }
            }
        } else {
            return findDateTimeFormatter(value, pattern);
        }
        return Optional.empty();
    }

    /**
     * find the set of locales from a pattern matcher and the list of words
     * @param wordToLocalList
     * @param matcher
     * @return the set of locales
     */
    private static Set<Locale> findLocales(List<Map<String, Set<Locale>>> wordToLocalList, Matcher matcher) {
        int groupIndex = 1;
        Set<Locale> locales = new HashSet<>();
        //we iterate over all matcher groups
        while (groupIndex <= matcher.groupCount()) {
            Set<Locale> tmpLocales = null;
            String group = matcher.group(groupIndex++).toLowerCase();
            // for each group, we search for the right map of words
            for (Map<String, Set<Locale>> wordToLocal : wordToLocalList) {
                tmpLocales = wordToLocal.get(group);
                if (tmpLocales != null) { //found
                    if (CollectionUtils.isEmpty(locales))
                        locales = tmpLocales;
                    else
                        locales.retainAll(tmpLocales); //we do the intersection between the sets "locales" and "tmpLocales"
                    break; //we don't have to iterate anymore
                }
            }
            if (CollectionUtils.isEmpty(tmpLocales) || CollectionUtils.isEmpty(locales)) //group not found
                return Collections.emptySet();
        }
        return locales;
    }

    /**
     * find the date time formatter is the pattern matches with the specified local
     * @param value
     * @param pattern
     * @param locale
     * @return the date time formatter
     */
    private static Optional<DateTimeFormatter> findDateTimeFormatter(String value, String pattern, Locale locale) {
        DateTimeFormatter dateTimeFormatterByPattern = getDateTimeFormatterByPattern(pattern, locale);
        if (isMatchDateTimePattern(value, dateTimeFormatterByPattern))
            return Optional.of(dateTimeFormatterByPattern);
        return Optional.empty();
    }

    private static Optional<DateTimeFormatter> findDateTimeFormatter(String value, String pattern) {
        return findDateTimeFormatter(value, pattern, Locale.getDefault());
    }
}
