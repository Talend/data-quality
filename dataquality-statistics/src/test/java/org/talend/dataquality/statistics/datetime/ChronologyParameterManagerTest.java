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

import static org.junit.Assert.assertEquals;

import java.time.chrono.HijrahChronology;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.MinguoChronology;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import org.junit.Test;

public class ChronologyParameterManagerTest {

    @Test
    public void testGuessLocaleByEra() {
        assertEquals(Locale.US, ChronologyParameterManager.guessLocaleByEra("2017-05-18 AD")); //$NON-NLS-1$
        assertEquals(Locale.US, ChronologyParameterManager.guessLocaleByEra("")); //$NON-NLS-1$
        assertEquals(Locale.US, ChronologyParameterManager.guessLocaleByEra("2017-05-18 ABC")); //$NON-NLS-1$
        assertEquals(Locale.US, ChronologyParameterManager.guessLocaleByEra("2017-05-18")); //$NON-NLS-1$
        assertEquals(Locale.TRADITIONAL_CHINESE, ChronologyParameterManager.guessLocaleByEra("0106-05-18 民國")); //$NON-NLS-1$
        assertEquals(Locale.TRADITIONAL_CHINESE, ChronologyParameterManager.guessLocaleByEra("6625-11-12 民國前")); //$NON-NLS-1$
        assertEquals(Locale.JAPANESE, ChronologyParameterManager.guessLocaleByEra("0006-01-01 明治")); //$NON-NLS-1$
        assertEquals(Locale.JAPANESE, ChronologyParameterManager.guessLocaleByEra("0029-05-18 平成")); //$NON-NLS-1$
        assertEquals(Locale.JAPANESE, ChronologyParameterManager.guessLocaleByEra("0045-01-01 昭和")); //$NON-NLS-1$
        assertEquals(Locale.JAPANESE, ChronologyParameterManager.guessLocaleByEra("0014-05-30 大正")); //$NON-NLS-1$
        assertEquals(new Locale("ar"), ChronologyParameterManager.guessLocaleByEra("1438-08-22 هـ")); //$NON-NLS-1$//$NON-NLS-2$
        assertEquals(new Locale("th"), ChronologyParameterManager.guessLocaleByEra("4171-11-12 ปีก่อนคริสต์กาลที่")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new Locale("th"), ChronologyParameterManager.guessLocaleByEra("2560-05-18 พ.ศ.")); //$NON-NLS-1$ //$NON-NLS-2$

    }

    @Test
    public void testgetDateTimeFormatterWithChronology() {
        String pattern = "yyyy-MM-dd G"; //$NON-NLS-1$
        Locale locale = Locale.JAPANESE;
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(pattern)
                .toFormatter(locale);
        DateTimeFormatter dateTimeFormatter = ChronologyParameterManager.getDateTimeFormatterWithChronology(formatter, locale);
        assertEquals(JapaneseChronology.INSTANCE, dateTimeFormatter.getChronology());

        locale = Locale.TAIWAN;
        dateTimeFormatter = ChronologyParameterManager.getDateTimeFormatterWithChronology(formatter, locale);
        assertEquals(MinguoChronology.INSTANCE, dateTimeFormatter.getChronology());

        locale = new Locale("ar"); //$NON-NLS-1$
        dateTimeFormatter = ChronologyParameterManager.getDateTimeFormatterWithChronology(formatter, locale);
        assertEquals(HijrahChronology.INSTANCE, dateTimeFormatter.getChronology());

        locale = new Locale("th"); //$NON-NLS-1$
        dateTimeFormatter = ChronologyParameterManager.getDateTimeFormatterWithChronology(formatter, locale);
        assertEquals(ThaiBuddhistChronology.INSTANCE, dateTimeFormatter.getChronology());
    }

}
