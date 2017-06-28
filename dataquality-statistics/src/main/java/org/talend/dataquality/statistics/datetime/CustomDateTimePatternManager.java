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

import java.util.*;

import org.apache.commons.lang.StringUtils;

/**
 * Customized date time pattern manager.
 * 
 * @author mzhao
 *
 */
public final class CustomDateTimePatternManager {

    private static final Locale DEFAULT_LOCALE = Locale.US;

    private static final String PATTERN_WITH_ERA = "yyyy-MM-dd G"; //$NON-NLS-1$

    public static boolean isDate(String value, List<String> customPatterns) {
        // guess Locale by value and pattern with era.
        Locale locale = guessLocaleByEra(value, customPatterns);
        return isDate(value, customPatterns, locale);
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

    private static boolean isMatchCustomPatterns(String value, List<String> customPatterns, Locale locale) {
        for (String pattern : customPatterns) {
            if (SystemDateTimePatternManager.isMatchDateTimePattern(value, pattern, locale)) {
                return true;
            }
        }
        return false;
    }

    // for junit only
    static Set<String> replaceByDateTimePattern(String value, String customPattern) {
        return replaceByDateTimePattern(value, customPattern, DEFAULT_LOCALE);
    }

    static Set<String> replaceByDateTimePattern(String value, String customPattern, Locale locale) {
        return replaceByDateTimePattern(value, Collections.singletonList(customPattern), locale);
    }

    public static Set<String> replaceByDateTimePattern(String value, List<String> customPatterns) {
        return replaceByDateTimePattern(value, customPatterns, DEFAULT_LOCALE);
    }

    public static Set<String> replaceByDateTimePattern(String value, List<String> customPatterns, Locale locale) {
        Set<String> resultPatternSet = new HashSet<String>();
        for (String customPattern : customPatterns) {
            if (SystemDateTimePatternManager.isMatchDateTimePattern(value, customPattern, locale)) {
                resultPatternSet.add(customPattern);
            }
        }
        // otherwise, replace with system date pattern manager.
        resultPatternSet.addAll(systemPatternReplace(value));
        return resultPatternSet;
    }

    private static Set<String> systemPatternReplace(String value) {
        Set<String> resultPatternSet = new HashSet<String>();
        resultPatternSet.addAll(SystemDateTimePatternManager.datePatternReplace(value));
        if (resultPatternSet.isEmpty()) {
            resultPatternSet.addAll(SystemDateTimePatternManager.timePatternReplace(value));
        }
        return resultPatternSet;
    }

    /**
     * 
     * Guess Locale by value and pattern,only pattern "yyyy-MM-dd G" can be extracted Locale.
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
     * @param customPatterns date patterns
     * @return
     */
    private static Locale guessLocaleByEra(String value, List<String> customPatterns) {
        if (StringUtils.isEmpty(value) || customPatterns.isEmpty() || !customPatterns.contains(PATTERN_WITH_ERA)) {
            return DEFAULT_LOCALE;
        }
        for (LocaleEraEnum localEra : LocaleEraEnum.values()) {
            if (StringUtils.endsWithAny(value, localEra.getEras())) {
                return localEra.getLocale();
            }
        }
        return DEFAULT_LOCALE;

    }

    private enum LocaleEraEnum {

        ISO(Locale.US, new String[] { "AD", "BC" }), //$NON-NLS-1$ //$NON-NLS-2$
        JAPANESE(Locale.JAPANESE, new String[] { "明治", "平成", "昭和", "大正" }), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        HIJRI(new Locale("ar"), new String[] { "هـ" }), //$NON-NLS-1$ //$NON-NLS-2$
        MINGUO(Locale.TRADITIONAL_CHINESE, new String[] { "民國", "民國前" }), //$NON-NLS-1$//$NON-NLS-2$
        THAI_BUDDHIST(new Locale("th"), new String[] { "พ.ศ.", "ปีก่อนคริสต์กาลที่" }); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$

        private Locale locale;

        private String[] eras;

        LocaleEraEnum(Locale locale, String[] eras) {
            this.locale = locale;
            this.eras = eras;
        }

        public Locale getLocale() {
            return locale;
        }

        public String[] getEras() {
            return eras;
        }
    }

}
