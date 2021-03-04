// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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

import static org.talend.dataquality.statistics.datetime.utils.AdditionalDateTimePatterns.ISO_RFC_DATETIME_PATTERNS;
import static org.talend.dataquality.statistics.datetime.utils.AdditionalDateTimePatterns.OFFSET_TIME_ZONE_PATTERNS;
import static org.talend.dataquality.statistics.datetime.utils.AdditionalDateTimePatterns.OTHER_COMMON_PATTERNS;
import static org.talend.dataquality.statistics.datetime.utils.AdditionalDateTimePatterns.OTHER_COMMON_PATTERNS_NEED_COMBINATION;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.talend.dataquality.statistics.datetime.SystemDateTimePatternManager;

/**
 * The class is used for generating regexes and unit tests for all date patterns supported in datetime discovery.
 */
public class PatternListGenerator {

    private static final Set<LocaledPattern> knownLocaledPatternList = new LinkedHashSet<LocaledPattern>();

    private static final Set<String> knownPatternList = new LinkedHashSet<String>();

    private final static ZonedDateTime ZONED_DATE_TIME =
            ZonedDateTime.of(1999, 3, 22, 5, 6, 7, 888, ZoneId.of("Europe/Paris"));

    private final static FormatStyle[] FORMAT_STYLES =
            new FormatStyle[] { FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL };

    private static final boolean PRINT_DETAILED_RESULTS = true;

    private static final StringBuilder dateSampleFileTextBuilder = new StringBuilder();

    private static final StringBuilder datePatternFileTextBuilder = new StringBuilder();

    private static final StringBuilder dateRegexFileTextBuilder = new StringBuilder();

    private static final StringBuilder timeSampleFileTextBuilder = new StringBuilder();

    private static final StringBuilder timePatternFileTextBuilder = new StringBuilder();

    private static final StringBuilder timeRegexFileTextBuilder = new StringBuilder();

    /**
     * General locales to support. For each of them, we generate:
     * 1. SHORT style date + SHORT style SHORT
     * 2. MEDIUM style date + MEDIUM style time
     * 3. LONG style date + LONG style time
     * 4. FULL style date + FULL style time
     */
    private static final Locale[] localeArray = new Locale[] { Locale.US, //
            Locale.FRANCE, //
            Locale.GERMANY, //
            Locale.UK, //
            Locale.ITALY, //
            Locale.CANADA, Locale.CANADA_FRENCH, //
            Locale.JAPAN, //
            Locale.CHINA, //
    };

    /**
     * Important locales with which we need not only support their own pattern, but also the following combination in
     * addition:
     * 1. SHORT style date + MEDIUM style time
     * 2. MEDIUM style date + SHORT style time
     * 3. all date patterns in OTHER_COMMON_PATTERNS_NEED_COMBINATION combined with the SHORT and MEDIUM style time of
     * the
     * following locales.
     */
    private static final Locale[] primaryLocaleArray = new Locale[] { Locale.US, //
            Locale.FRANCE, //
            Locale.GERMANY, //
            Locale.UK, //
            Locale.JAPAN, //
    };

    private static void processBaseDateTimePatternsByLocales() {

        for (FormatStyle style : FORMAT_STYLES) {
            if (PRINT_DETAILED_RESULTS) {
                System.out.println("--------------------Date Style: " + style + "-----------------------");
            }
            for (Locale locale : localeArray) {
                getFormatByStyle(style, style, true, false, locale, true);// Date Only
            }
        }
        for (FormatStyle style : FORMAT_STYLES) {
            if (PRINT_DETAILED_RESULTS) {
                System.out.println("--------------------DateTime Style: " + style + "-----------------------");
            }
            for (Locale locale : localeArray) {
                getFormatByStyle(style, style, true, true, locale, true); // Date & Time
            }
        }

        // include additional combinations
        for (Locale locale : primaryLocaleArray) {
            getFormatByStyle(FormatStyle.SHORT, FormatStyle.MEDIUM, true, true, locale, false);
            getFormatByStyle(FormatStyle.MEDIUM, FormatStyle.SHORT, true, true, locale, false);
        }

    }

    private static void processBaseTimePatternsByLocales() {
        for (FormatStyle style : FORMAT_STYLES) {
            if (PRINT_DETAILED_RESULTS) {
                System.out.println("--------------------Time Style: " + style + "-----------------------");
            }
            for (Locale locale : localeArray) {
                getFormatByStyle(style, style, false, true, locale, true); // Time Only
            }
        }
    }

    private static void getFormatByStyle(FormatStyle dateStyle, FormatStyle timeStyle, boolean isDateRequired,
            boolean isTimeRequired, Locale locale, boolean keepLongMonthAndSpecificChars) {
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(//
                isDateRequired ? dateStyle : null, isTimeRequired ? timeStyle : null, IsoChronology.INSTANCE, locale);//

        // ignore patterns with long month for additional languages
        if (!keepLongMonthAndSpecificChars && (pattern.contains("MMMM") || pattern.contains("MMM")
                || pattern.contains(" a") || pattern.contains("'"))) {
            return;
        }

        Set<String> localesInShortStyleWithYYYY = new HashSet<String>(
                Arrays.asList("el", "fi", "hr", "hu", "is", "mt", "pt", "ro", "sk", "sv", "th", "tr", "vi"));

        if (!pattern.contains("yy") && pattern.contains("y")) {// only one "y" to represent year part
            if (FormatStyle.SHORT.equals(dateStyle) && !localesInShortStyleWithYYYY.contains(locale.toLanguageTag())) {
                pattern = pattern.replace("y", "yy");
            } else {
                pattern = pattern.replace("y", "yyyy");
            }
        }

        if (pattern.contains("\u200F")) {
            pattern = pattern.replace("\u200F", "");
        }

        if (!knownPatternList.contains(pattern)) {

            LocaledPattern lp = new LocaledPattern(pattern, locale, dateStyle.name(), isTimeRequired);
            knownLocaledPatternList.add(lp);
            knownPatternList.add(pattern); // update list of pattern strings without locale
            if (PRINT_DETAILED_RESULTS) {
                System.out.println(lp);
            }
        } else {
            if (pattern.contains("MMMM") || pattern.contains("MMM")) {
                if (PRINT_DETAILED_RESULTS) {
                    System.out.print("!!!duplicated pattern with different locale!!! ");
                }
                LocaledPattern lp = new LocaledPattern(pattern, locale, dateStyle.name(), isTimeRequired);
                knownLocaledPatternList.add(lp);
                if (PRINT_DETAILED_RESULTS) {
                    System.out.println(lp);
                }
            }
        }
    }

    private static void processAdditionalDateTimePatternsByLocales() {

        for (FormatStyle style : FORMAT_STYLES) {
            if (PRINT_DETAILED_RESULTS) {
                System.out.println("--------------------Date Style: " + style + "-----------------------");
            }
            for (String lang : SystemDateTimePatternManager.getSupportedIsoLanguages()) {
                getFormatByStyle(style, style, true, false, new Locale(lang), false);// Date Only
            }
        }
        for (FormatStyle style : FORMAT_STYLES) {
            if (PRINT_DETAILED_RESULTS) {
                System.out.println("--------------------DateTime Style: " + style + "-----------------------");
            }
            for (String lang : SystemDateTimePatternManager.getSupportedIsoLanguages()) {
                getFormatByStyle(style, style, true, true, new Locale(lang), false);// DateTime
            }
        }
    }

    @SuppressWarnings("unused")
    private static void validateISOPattens(List<String> isoPatternList) {

        Set<String> formattedDateTimeSet = new HashSet<String>();
        for (String pattern : isoPatternList) {
            formattedDateTimeSet.add(getFormattedDateTime(pattern, Locale.US));
        }

        DateTimeFormatter[] formatters = new DateTimeFormatter[] { DateTimeFormatter.BASIC_ISO_DATE, // 1
                DateTimeFormatter.ISO_DATE, // 2
                DateTimeFormatter.ISO_DATE_TIME, // 3
                // DateTimeFormatter.ISO_TIME, //
                DateTimeFormatter.ISO_INSTANT, // 4
                DateTimeFormatter.ISO_LOCAL_DATE, // 5
                DateTimeFormatter.ISO_LOCAL_DATE_TIME, // 6
                // DateTimeFormatter.ISO_LOCAL_TIME, //
                DateTimeFormatter.ISO_OFFSET_DATE, // 7
                DateTimeFormatter.ISO_OFFSET_DATE_TIME, // 8
                // DateTimeFormatter.ISO_OFFSET_TIME, //
                DateTimeFormatter.ISO_ORDINAL_DATE, // 9
                DateTimeFormatter.ISO_WEEK_DATE, // 10
                DateTimeFormatter.ISO_ZONED_DATE_TIME, // 11
                DateTimeFormatter.RFC_1123_DATE_TIME, // 12
        };

        System.out.println("-------------Validate ISO PattenText-------------");
        for (int i = 0; i < formatters.length; i++) {

            System.out.print((i + 1) + "\t");
            try {
                String formattedDateTime = ZONED_DATE_TIME.format(formatters[i]);
                System.out.print(formattedDateTimeSet.contains(formattedDateTime) ? "YES\t" : "NO\t");
                System.out.println(formattedDateTime);
            } catch (Throwable t) {
                System.out.println(t.getMessage());
            }
        }

    }

    private static String getFormattedDateTime(String pattern, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
        try {
            return ZONED_DATE_TIME.format(formatter);
        } catch (Throwable t) {
            return t.getMessage();
        }
    }

    private static void generateDateFormats() throws IOException {
        int currentLocaledPatternSize = 0;
        knownLocaledPatternList.clear();
        knownPatternList.clear();
        // 1. Base Localized DateTimePatterns (java8 DateTimeFormatterBuilder)
        processBaseDateTimePatternsByLocales();
        int basePatternCount = knownLocaledPatternList.size() - currentLocaledPatternSize;
        if (PRINT_DETAILED_RESULTS) {
            System.out.println("#basePatterns = " + basePatternCount + "\n");
        }
        currentLocaledPatternSize = knownLocaledPatternList.size();

        // 2. Other common DateTime patterns
        for (LocaledPattern lp : OTHER_COMMON_PATTERNS_NEED_COMBINATION) {
            addLocaledPattern(lp);

            for (Locale locale : primaryLocaleArray) {

                String patternShort = DateTimeFormatterBuilder.getLocalizedDateTimePattern(//
                        null, FormatStyle.SHORT, IsoChronology.INSTANCE, locale);//
                LocaledPattern combinedShortLP =
                        new LocaledPattern(lp.pattern + " " + patternShort, locale, FormatStyle.SHORT.name(), true);
                addLocaledPattern(combinedShortLP);

                String patternMedium = DateTimeFormatterBuilder.getLocalizedDateTimePattern(//
                        null, FormatStyle.MEDIUM, IsoChronology.INSTANCE, locale);//
                LocaledPattern combinedMediumLP =
                        new LocaledPattern(lp.pattern + " " + patternMedium, locale, FormatStyle.MEDIUM.name(), true);
                addLocaledPattern(combinedMediumLP);

            }

        }

        for (LocaledPattern lp : OTHER_COMMON_PATTERNS) {
            addLocaledPattern(lp);
        }

        // 3. ISO and RFC DateTimePatterns
        ISO_RFC_DATETIME_PATTERNS.forEach(PatternListGenerator::addLocaledPattern);
        int isoPatternCount = knownLocaledPatternList.size() - currentLocaledPatternSize;
        if (PRINT_DETAILED_RESULTS) {
            System.out.println("#DateTimePattern(ISO&RFC) = " + isoPatternCount + "\n");
        }
        currentLocaledPatternSize = knownLocaledPatternList.size();

        // 4. Additional Localized DateTimePatterns (java8 DateTimeFormatterBuilder)
        processAdditionalDateTimePatternsByLocales();
        int additionalPatternCount = knownLocaledPatternList.size() - currentLocaledPatternSize;
        if (PRINT_DETAILED_RESULTS) {
            System.out.println("#additionalPatternList = " + additionalPatternCount + "\n");
        }

        if (PRINT_DETAILED_RESULTS) {
            System.out.println("#Total = " + knownLocaledPatternList.size() + //
                    " (#baseDatePatterns = " + basePatternCount + //
                    ", #isoPatterns = " + isoPatternCount + //
                    ", #additionalPatterns = " + additionalPatternCount + ")\n");//
        }

        writeDates();
    }

    private static void writeDates() throws IOException {
        // table header
        dateSampleFileTextBuilder.append("Sample\tPattern\tLocale\tFormatStyle\tIsWithTime\n");

        RegexGenerator regexGenerator = new RegexGenerator();
        for (LocaledPattern lp : knownLocaledPatternList) {

            datePatternFileTextBuilder.append(lp).append("\n");

            String regex = regexGenerator.convertPatternToRegex(lp.pattern);
            dateRegexFileTextBuilder.append(lp.getPattern()).append("\t^").append(regex).append("$\n");
            dateSampleFileTextBuilder
                    .append(ZONED_DATE_TIME.format(DateTimeFormatter.ofPattern(lp.getPattern(), lp.getLocale())))
                    .append("\t")
                    .append(lp.getPattern())//
                    .append("\t")
                    .append(lp.getLocale())//
                    .append("\t")
                    .append(lp.getFormatStyle())//
                    .append("\t")
                    .append(lp.isWithTime())
                    .append("\n");
        }

        writeResource("main", "DateFormats.txt", datePatternFileTextBuilder.toString());
        writeResource("main", "DateRegexes.txt", dateRegexFileTextBuilder.toString());
        writeResource("test", "DateSampleTable.txt", dateSampleFileTextBuilder.toString());

        // generate grouped Date Regexes
        FormatGroupGenerator.generateDateRegexGroups();
    }

    private static void addLocaledPattern(LocaledPattern lp) {
        if (!knownPatternList.contains(lp.pattern)) {
            knownLocaledPatternList.add(lp);
            knownPatternList.add(lp.getPattern());
            if (PRINT_DETAILED_RESULTS) {
                System.out.println(lp);
            }
        }
    }

    private static void generateTimeFormats() throws IOException {
        knownLocaledPatternList.clear();
        knownPatternList.clear();
        processBaseTimePatternsByLocales();
        int basePatternCount = knownLocaledPatternList.size();
        knownLocaledPatternList.addAll(OFFSET_TIME_ZONE_PATTERNS);
        int additionalPatternCount = OFFSET_TIME_ZONE_PATTERNS.size();
        if (PRINT_DETAILED_RESULTS) {
            System.out.println("--------------------Additional Time Patterns-----------------------");
            OFFSET_TIME_ZONE_PATTERNS.forEach(System.out::println);
            System.out.println("\n#Total = " + knownLocaledPatternList.size() + //
                    " (#baseTimePatterns = " + basePatternCount + ")\n" + " (#additionalTimePatterns = "
                    + additionalPatternCount);//
        }

        // table header
        timeSampleFileTextBuilder.append("Sample\tPattern\tLocale\tFormatStyle\tIsWithTime\n");
        RegexGenerator regexGenerator = new RegexGenerator();
        for (LocaledPattern lp : knownLocaledPatternList) {

            timePatternFileTextBuilder.append(lp).append("\n");

            String regex = regexGenerator.convertPatternToRegex(lp.pattern);
            timeRegexFileTextBuilder.append(lp.getPattern()).append("\t^").append(regex).append("$\n");

            timeSampleFileTextBuilder
                    .append(ZONED_DATE_TIME.format(DateTimeFormatter.ofPattern(lp.getPattern(), lp.getLocale())))
                    .append("\t")
                    .append(lp.getPattern())//
                    .append("\t")
                    .append(lp.getLocale())//
                    .append("\t")
                    .append(lp.getFormatStyle())//
                    .append("\t")
                    .append(lp.isWithTime())
                    .append("\n");
        }

        writeResource("main", "TimeFormats.txt", timePatternFileTextBuilder.toString());
        writeResource("main", "TimeRegexes.txt", timeRegexFileTextBuilder.toString());
        writeResource("test", "TimeSampleTable.txt", timeSampleFileTextBuilder.toString());
    }

    private static void writeResource(String scope, String resourceName, String resourceContent) throws IOException {
        String targetClassPath = "target" + File.separator + (scope.equals("main") ? "classes" : "test-classes");
        // Time Samples
        Path path = Paths.get(SystemDateTimePatternManager.class.getResource(resourceName).getFile().replace(
                targetClassPath, "src" + File.separator + scope + File.separator + "resources"));
        Files.write(path, resourceContent.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) throws IOException {

        generateDateFormats();

        generateTimeFormats();

    }

}

class LocaledPattern {

    String pattern;

    Locale locale;

    String formatStyle;

    boolean withTime;

    int groupId = 0;

    public LocaledPattern(String pattern, Locale locale, String formatStyle, boolean withTime) {
        this.pattern = pattern;
        this.locale = locale;
        this.formatStyle = formatStyle;
        this.withTime = withTime;
    }

    public String getPattern() {
        return pattern;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getFormatStyle() {
        return formatStyle;
    }

    public boolean isWithTime() {
        return withTime;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return locale + "\t" + pattern + (groupId == 0 ? "" : "\t" + groupId);

    }

}
