package org.talend.dataquality.statistics.datetime.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The class is used to list standard locale-specific datetime patterns proposed by JVM.
 */
public class StandardDatetimeFormatLister {

    private final static ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.of(1999, 3, 22, 5, 6, 7, 888, ZoneId.of("Europe/Paris"));

    private static final boolean PRINT_DETAILED_RESULTS = true;

    private static Locale[] localeArray = new Locale[] { //
            Locale.US, //
            Locale.FRANCE, //
            Locale.GERMANY, //
            Locale.UK, //
            Locale.JAPAN, //
    };

    private final static FormatStyle[] FORMAT_STYLES = new FormatStyle[] { FormatStyle.SHORT, FormatStyle.MEDIUM,
            FormatStyle.LONG, FormatStyle.FULL };

    private List<LocaledPattern> processBaseDateTimePatternsByLocales() {
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
        return dateTimePatterns;
    }

    private void getFormatByStyle(FormatStyle dateStyle, FormatStyle timeStyle, boolean isDateRequired, boolean isTimeRequired,
            Locale locale, boolean keepLongMonthAndSpecificChars) {
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(//
                isDateRequired ? dateStyle : null, isTimeRequired ? timeStyle : null, IsoChronology.INSTANCE, locale);//
        LocaledPattern lp = new LocaledPattern(pattern, locale, dateStyle.name(), isTimeRequired);
        String formattedDateTime = ZONED_DATE_TIME.format(DateTimeFormatter.ofPattern(pattern, lp.localeSet.iterator().next()));
        System.out.format("%-40s\t%s\n", lp, formattedDateTime);
    }

    public static void main(String[] args) {
        StandardDatetimeFormatLister appli = new StandardDatetimeFormatLister();
        appli.processBaseDateTimePatternsByLocales();
    }

}
