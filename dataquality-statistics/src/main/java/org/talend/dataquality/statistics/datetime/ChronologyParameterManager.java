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

import java.time.chrono.HijrahChronology;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.MinguoChronology;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Manage some Chronology parameters.
 */
public class ChronologyParameterManager {

    private static final Locale DEFAULT_LOCALE = Locale.US;

    private static Map<String, Locale> localeEraMap = null;

    private ChronologyParameterManager() {

    }

    /**
     * 
     * Guess Locale by date value.only support date format "yyyy-MM-dd G".
     * 
     * <pre>
     * guessLocaleByEra("2017-06-26",  Arrays.asList("yyyy-MM-dd G"))  = DEFAULT_LOCALE
     * guessLocaleByEra("0006-01-01 明治",  Arrays.asList("yyyy-MM-dd G"))  = Locale.JAPANESE
     * guessLocaleByEra("0106-05-18 民國",  Arrays.asList("yyyy-MM-dd G"))  = Locale.TAIWAN
     * guessLocaleByEra("1438-08-22 هـ",  Arrays.asList("yyyy-MM-dd G"))  = new Locale("ar")
     * guessLocaleByEra("04171-11-12 ปีก่อนคริสต์กาลที่",  Arrays.asList("yyyy-MM-dd G"))  = new Locale("th")
     * </pre>
     * 
     * @param value a String of date like as "0106-05-18 民國"
     * @return
     */
    public static Locale guessLocaleByEra(String value) {
        if (StringUtils.isEmpty(value)) {
            return DEFAULT_LOCALE;
        }
        int indexOf = value.indexOf(' ');
        if (indexOf != -1) {
            initMapIfNeeded();
            // extract the era from the end of value.
            String era = value.substring(indexOf + 1, value.length());
            Locale locale = localeEraMap.get(era);
            if (locale != null) {
                return locale;
            }
        }
        return DEFAULT_LOCALE;

    }

    /**
     * 
     * initialize localeEraMap once.
     */
    private static void initMapIfNeeded() {
        if (localeEraMap == null) {
            localeEraMap = new HashMap<>();
            Locale localeTH = new Locale("th"); //$NON-NLS-1$
            localeEraMap.put("AD", Locale.US); //$NON-NLS-1$
            localeEraMap.put("BC", Locale.US); //$NON-NLS-1$
            localeEraMap.put("明治", Locale.JAPANESE); //$NON-NLS-1$
            localeEraMap.put("平成", Locale.JAPANESE); //$NON-NLS-1$
            localeEraMap.put("昭和", Locale.JAPANESE); //$NON-NLS-1$
            localeEraMap.put("大正", Locale.JAPANESE); //$NON-NLS-1$
            localeEraMap.put("هـ", new Locale("ar")); //$NON-NLS-1$//$NON-NLS-2$
            localeEraMap.put("民國", Locale.TRADITIONAL_CHINESE); //$NON-NLS-1$
            localeEraMap.put("民國前", Locale.TRADITIONAL_CHINESE); //$NON-NLS-1$
            localeEraMap.put("พ.ศ.", localeTH); //$NON-NLS-1$
            localeEraMap.put("ปีก่อนคริสต์กาลที่", localeTH); //$NON-NLS-1$
        }
    }

    /**
     * 
     * Set Chronology for DateTimeFormatter by Locale.
     * 
     * @param formatter
     * @param locale
     * @return
     */
    public static DateTimeFormatter getDateTimeFormatterWithChronology(String pattern, Locale locale) {

        DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(pattern)
                .toFormatter(locale).withResolverStyle(ResolverStyle.STRICT);
        if (Locale.JAPANESE.equals(locale)) {
            return formatter.withChronology(JapaneseChronology.INSTANCE);
        } else if (Locale.TRADITIONAL_CHINESE.equals(locale)) {
            return formatter.withChronology(MinguoChronology.INSTANCE);
        } else if ("ar".equals(locale.getLanguage())) { //$NON-NLS-1$
            return formatter.withChronology(HijrahChronology.INSTANCE);
        } else if ("th".equals(locale.getLanguage())) { //$NON-NLS-1$
            return formatter.withChronology(ThaiBuddhistChronology.INSTANCE).withLocale(locale);
        }
        return formatter;

    }
}
