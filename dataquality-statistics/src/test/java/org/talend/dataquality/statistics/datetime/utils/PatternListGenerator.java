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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.talend.dataquality.statistics.datetime.SystemDateTimePatternManager;

/**
 * The class is used for generating regexes and unit tests for all date patterns supported in datetime discovery.
 */
public class PatternListGenerator {

    private static Set<LocaledPattern> knownLocaledPatternList = new LinkedHashSet<LocaledPattern>();

    private static Set<String> knownPatternList = new LinkedHashSet<String>();

    private final static ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.of(1999, 3, 22, 5, 6, 7, 888, ZoneId.of("Europe/Paris"));

    private final static FormatStyle[] FORMAT_STYLES = new FormatStyle[] { FormatStyle.SHORT, FormatStyle.MEDIUM,
            FormatStyle.LONG, FormatStyle.FULL };

    private static final boolean PRINT_DETAILED_RESULTS = false;

    private static StringBuilder dateSampleFileTextBuilder = new StringBuilder();

    private static StringBuilder datePatternFileTextBuilder = new StringBuilder();

    private static StringBuilder dateRegexFileTextBuilder = new StringBuilder();

    private static StringBuilder timeSampleFileTextBuilder = new StringBuilder();

    private static StringBuilder timePatternFileTextBuilder = new StringBuilder();

    private static StringBuilder timeRegexFileTextBuilder = new StringBuilder();

    /**
     * General locales to support. For each of them, we generate:
     * 1. SHORT style date + SHORT style SHORT
     * 2. MEDIUM style date + MEDIUM style time
     * 3. LONG style date + LONG style time
     * 4. FULL style date + FULL style time
     */
    private static Locale[] localeArray = new Locale[] { Locale.US, //
            Locale.FRANCE, //
            Locale.GERMANY, //
            Locale.UK, //
            Locale.ITALY, //
            Locale.CANADA, Locale.CANADA_FRENCH, //
            Locale.JAPAN, //
            Locale.CHINA, //
    };

    /**
     * Important locales with which we need not only support their own pattern, but also the following combination in addition:
     * 1. SHORT style date + MEDIUM style time
     * 2. MEDIUM style date + SHORT style time
     * 3. all date patterns in OTHER_COMMON_PATTERNS_NEED_COMBINATION combined with the SHORT and MEDIUM style time of the
     * following locales.
     */
    private static Locale[] primaryLocaleArray = new Locale[] { Locale.US, //
            Locale.FRANCE, //
            Locale.GERMANY, //
            Locale.UK, //
            Locale.JAPAN, //
    };

    /**
     * Add important date pattern here which need to be combined with SHORT and MEDIUM style time of the primary locales.
     */
    private static List<LocaledPattern> OTHER_COMMON_PATTERNS_NEED_COMBINATION = new ArrayList<LocaledPattern>() {

        private static final long serialVersionUID = 1L;
        // NOTE: do not use patterns containing only one "y" for year part.
        {
            add(new LocaledPattern("dd/MM/yyyy", Locale.US, "OTHER", false));
            add(new LocaledPattern("d/M/yyyy", Locale.US, "OTHER", false));
            add(new LocaledPattern("MM/dd/yyyy", Locale.US, "OTHER", false));
            add(new LocaledPattern("M/d/yyyy", Locale.US, "OTHER", false));
            add(new LocaledPattern("MM-dd-yy", Locale.US, "OTHER", false));
            add(new LocaledPattern("M-d-yy", Locale.US, "OTHER", false));
            add(new LocaledPattern("MM-dd-yyyy", Locale.US, "OTHER", false));
            add(new LocaledPattern("M-d-yyyy", Locale.US, "OTHER", false));
            add(new LocaledPattern("yyyy-MM-dd", Locale.US, "OTHER", false));
            add(new LocaledPattern("yyyy-M-d", Locale.US, "OTHER", false));
            add(new LocaledPattern("MM/dd/yy", Locale.US, "OTHER", false));
            add(new LocaledPattern("M/d/yy", Locale.US, "OTHER", false));
        }
    };

    /**
     * Add other single patterns here which do not need to be combined with SHORT and MEDIUM style time of the primary locales.
     */
    private static List<LocaledPattern> OTHER_COMMON_PATTERNS = new ArrayList<LocaledPattern>() {

        private static final long serialVersionUID = 1L;
        // NOTE: do not use patterns containing only one "y" for year part.
        {
            add(new LocaledPattern("MMM d yyyy", Locale.US, "OTHER", false));// Jan 18 2012
            add(new LocaledPattern("MMM.dd.yyyy", Locale.US, "OTHER", false));// Jan.02.2010
            add(new LocaledPattern("MMMM d yyyy", Locale.US, "OTHER", false));// January 18 2012
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss.S", Locale.US, "OTHER", true));// 2013-2-14 13:40:51.1
            add(new LocaledPattern("d/MMM/yyyy H:mm:ss Z", Locale.US, "OTHER", true));// 14/Feb/2013 13:40:51 +0100
            add(new LocaledPattern("dd-MMM-yy hh.mm.ss.nnnnnnnnn a", //
                    Locale.UK, "OTHER", true));// 18-Nov-86 01.00.00.000000000 AM
            add(new LocaledPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.US, "OTHER", true));// default format of java.util.Date
            add(new LocaledPattern("dd/MMM/yy h:mm a", Locale.US, "OTHER", true)); // data time pattern from jira
            add(new LocaledPattern("yyyy/M/d", Locale.US, "OTHER", false)); // TDQ-13539
        }
    };

    private static List<LocaledPattern> processBaseDateTimePatternsByLocales() {

        // Set<String> dateTimePatternsList = new LinkedHashSet<String>();
        List<LocaledPattern> dateTimePatterns = new ArrayList<LocaledPattern>();

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

        dateTimePatterns.removeAll(knownPatternList);
        // return new ArrayList<String>(dateTimePatterns);
        return dateTimePatterns;

    }

    private static List<LocaledPattern> processBaseTimePatternsByLocales() {
        List<LocaledPattern> timePatterns = new ArrayList<LocaledPattern>();
        for (FormatStyle style : FORMAT_STYLES) {
            if (PRINT_DETAILED_RESULTS) {
                System.out.println("--------------------Time Style: " + style + "-----------------------");
            }
            for (Locale locale : localeArray) {
                getFormatByStyle(style, style, false, true, locale, true); // Time Only
            }
        }
        return timePatterns;
    }

    private static void getFormatByStyle(FormatStyle dateStyle, FormatStyle timeStyle, boolean isDateRequired,
            boolean isTimeRequired, Locale locale, boolean keepLongMonthAndSpecificChars) {
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(//
                isDateRequired ? dateStyle : null, isTimeRequired ? timeStyle : null, IsoChronology.INSTANCE, locale);//

        // ignore patterns with long month for additional languages
        if (!keepLongMonthAndSpecificChars
                && (pattern.contains("MMMM") || pattern.contains("MMM") || pattern.contains(" a") || pattern.contains("'"))) {
            return;
        }

        if (!pattern.contains("yy") && pattern.contains("y")) {// only one "y" to represent year part
            return;
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
            for (String lang : Locale.getISOLanguages()) {
                getFormatByStyle(style, style, true, false, new Locale(lang), false);// Date Only
            }
        }
        for (FormatStyle style : FORMAT_STYLES) {
            if (PRINT_DETAILED_RESULTS) {
                System.out.println("--------------------DateTime Style: " + style + "-----------------------");
            }
            for (String lang : Locale.getISOLanguages()) {
                getFormatByStyle(style, style, true, true, new Locale(lang), false);// DateTime
            }
        }
    }

    private static void processISOAndRFCDateTimePatternList() {

        List<LocaledPattern> patternList = new ArrayList<LocaledPattern>();

        // 1. BASIC_ISO_DATE
        patternList.add(new LocaledPattern("yyyyMMddZ", Locale.US, "BASIC_ISO_DATE", false));
        patternList.add(new LocaledPattern("yyyyMMdd", Locale.US, "BASIC_ISO_DATE", false));
        // 2. ISO_DATE
        patternList.add(new LocaledPattern("yyyy-MM-dd G", Locale.US, "ISO_DATE", false));// 2017-05-27 AD
        patternList.add(new LocaledPattern("yyyy-MM-ddXXX", Locale.US, "ISO_DATE", false));
        patternList.add(new LocaledPattern("yyyy-MM-dd", Locale.US, "ISO_DATE", false));
        // 3. ISO_DATE_TIME
        patternList.add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'['VV']'", Locale.US, "ISO_DATE_TIME", true));
        patternList.add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US, "ISO_DATE_TIME", true));
        patternList.add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US, "ISO_DATE_TIME", true));
        // 4. ISO_INSTANT
        patternList.add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US, "ISO_INSTANT", true));
        // 5. ISO_LOCAL_DATE
        patternList.add(new LocaledPattern("yyyy-MM-dd", Locale.US, "ISO_LOCAL_DATE", false));
        // 6. ISO_LOCAL_DATE_TIME
        patternList.add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US, "ISO_LOCAL_DATE_TIME", true));
        patternList.add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US, "ISO_LOCAL_DATE_TIME", true));// 1970-01-01T00:32:43
        // 7. ISO_OFFSET_DATE
        patternList.add(new LocaledPattern("yyyy-MM-ddXXX", Locale.US, "ISO_OFFSET_DATE", false));
        // 8. ISO_OFFSET_DATE_TIME
        patternList.add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US, "ISO_OFFSET_DATE_TIME", true));
        patternList.add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US, "ISO_OFFSET_DATE_TIME", true));
        // 9. ISO_ORDINAL_DATE
        patternList.add(new LocaledPattern("yyyy-DDDXXX", Locale.US, "ISO", false));
        // 10. ISO_WEEK_BASED_DATE
        patternList.add(new LocaledPattern("YYYY'W'wc", Locale.US, "ISO", false)); // compact form
        patternList.add(new LocaledPattern("YYYY-'W'w-c", Locale.US, "ISO", false)); // extended form
        // 11. ISO_ZONED_DATE_TIME
        patternList.add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'", Locale.US, "ISO_ZONED_DATE_TIME", true));
        patternList.add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'", Locale.US, "ISO_ZONED_DATE_TIME", true));
        // 12. RFC_1123_DATE_TIME
        patternList.add(new LocaledPattern("EEE, d MMM yyyy HH:mm:ss Z", Locale.US, "RFC1123_WITH_DAY", true));
        patternList.add(new LocaledPattern("d MMM yyyy HH:mm:ss Z", Locale.US, "RFC1123", true));

        for (LocaledPattern lp : patternList) {
            addLocaledPattern(lp);
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
            String formattedDateTime = ZONED_DATE_TIME.format(formatter);
            return formattedDateTime;
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
                LocaledPattern combinedShortLP = new LocaledPattern(lp.pattern + " " + patternShort, locale,
                        FormatStyle.SHORT.name(), true);
                addLocaledPattern(combinedShortLP);

                String patternMedium = DateTimeFormatterBuilder.getLocalizedDateTimePattern(//
                        null, FormatStyle.MEDIUM, IsoChronology.INSTANCE, locale);//
                LocaledPattern combinedMediumLP = new LocaledPattern(lp.pattern + " " + patternMedium, locale,
                        FormatStyle.MEDIUM.name(), true);
                addLocaledPattern(combinedMediumLP);

            }

        }

        for (LocaledPattern lp : OTHER_COMMON_PATTERNS) {
            addLocaledPattern(lp);
        }

        // 3. ISO and RFC DateTimePatterns
        processISOAndRFCDateTimePatternList();
        // knownPatternList.addAll(isoPatternList);
        int isoPatternCount = knownLocaledPatternList.size() - currentLocaledPatternSize;
        if (PRINT_DETAILED_RESULTS) {
            System.out.println("#DateTimePattern(ISO&RFC) = " + isoPatternCount + "\n");
        }
        currentLocaledPatternSize = knownLocaledPatternList.size();

        // 4. Additional Localized DateTimePatterns (java8 DateTimeFormatterBuilder)
        processAdditionalDateTimePatternsByLocales();
        // knownPatternList.addAll(additionalPatternList);
        int additionalPatternCount = knownLocaledPatternList.size() - currentLocaledPatternSize;
        if (PRINT_DETAILED_RESULTS) {
            System.out.println("#additionalPatternList = " + additionalPatternCount + "\n");
        }
        currentLocaledPatternSize = knownLocaledPatternList.size();

        if (PRINT_DETAILED_RESULTS) {
            System.out.println("#Total = " + knownLocaledPatternList.size() + //
                    " (#baseDatePatterns = " + basePatternCount + //
                    ", #isoPatterns = " + isoPatternCount + //
                    ", #additionalPatterns = " + additionalPatternCount + ")\n");//
        }

        // table header
        dateSampleFileTextBuilder.append("Sample\tPattern\tLocale\tFormatStyle\tIsWithTime\n");

        RegexGenerator regexGenerator = new RegexGenerator();
        for (LocaledPattern lp : knownLocaledPatternList) {

            datePatternFileTextBuilder.append(lp).append("\n");

            String regex = regexGenerator.convertPatternToRegex(lp.pattern);
            dateRegexFileTextBuilder.append(lp.getPattern()).append("\t^").append(regex).append("$\n");
            dateSampleFileTextBuilder.append(ZONED_DATE_TIME.format(DateTimeFormatter.ofPattern(lp.getPattern(), lp.getLocale())))
                    .append("\t").append(lp.getPattern())//
                    .append("\t").append(lp.getLocale())//
                    .append("\t").append(lp.getFormatStyle())//
                    .append("\t").append(lp.isWithTime()).append("\n");
        }

        // Date Formats
        String path = SystemDateTimePatternManager.class.getResource("DateFormats.txt").getFile()
                .replace("target" + File.separator + "classes", "src" + File.separator + "main" + File.separator + "resources");
        IOUtils.write(datePatternFileTextBuilder.toString(), new FileOutputStream(new File(path)));

        // Date Regexes
        path = SystemDateTimePatternManager.class.getResource("DateRegexes.txt").getFile()
                .replace("target" + File.separator + "classes", "src" + File.separator + "main" + File.separator + "resources");
        IOUtils.write(dateRegexFileTextBuilder.toString(), new FileOutputStream(new File(path)));

        // Date Samples
        path = SystemDateTimePatternManager.class.getResource("DateSampleTable.txt").getFile()
                .replace("target" + File.separator + "classes", "src" + File.separator + "test" + File.separator + "resources");
        IOUtils.write(dateSampleFileTextBuilder.toString(), new FileOutputStream(new File(path)));

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
        if (PRINT_DETAILED_RESULTS) {
            System.out.println("\n#Total = " + knownLocaledPatternList.size() + //
                    " (#baseDatePatterns = " + basePatternCount + ")\n");//
        }

        // table header
        timeSampleFileTextBuilder.append("Sample\tPattern\tLocale\tFormatStyle\tIsWithTime\n");
        RegexGenerator regexGenerator = new RegexGenerator();
        for (LocaledPattern lp : knownLocaledPatternList) {

            timePatternFileTextBuilder.append(lp).append("\n");

            String regex = regexGenerator.convertPatternToRegex(lp.pattern);
            timeRegexFileTextBuilder.append(lp.getPattern()).append("\t^").append(regex).append("$\n");

            timeSampleFileTextBuilder.append(ZONED_DATE_TIME.format(DateTimeFormatter.ofPattern(lp.getPattern(), lp.getLocale())))
                    .append("\t").append(lp.getPattern())//
                    .append("\t").append(lp.getLocale())//
                    .append("\t").append(lp.getFormatStyle())//
                    .append("\t").append(lp.isWithTime()).append("\n");
        }

        // Time Formats
        String path = SystemDateTimePatternManager.class.getResource("TimeFormats.txt").getFile()
                .replace("target" + File.separator + "classes", "src" + File.separator + "main" + File.separator + "resources");
        IOUtils.write(timePatternFileTextBuilder.toString(), new FileOutputStream(new File(path)));

        // Time Regexes
        path = SystemDateTimePatternManager.class.getResource("TimeRegexes.txt").getFile()
                .replace("target" + File.separator + "classes", "src" + File.separator + "main" + File.separator + "resources");
        IOUtils.write(timeRegexFileTextBuilder.toString(), new FileOutputStream(new File(path)));

        // Time Samples
        path = SystemDateTimePatternManager.class.getResource("TimeSampleTable.txt").getFile()
                .replace("target" + File.separator + "classes", "src" + File.separator + "test" + File.separator + "resources");
        IOUtils.write(timeSampleFileTextBuilder.toString(), new FileOutputStream(new File(path)));
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
