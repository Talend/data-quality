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
package org.talend.dataquality.statistics.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

import org.junit.Test;

public class SystemDateTimePatternManagerTest {

    @Test
    public void datesWithJapaneseChronologyWithNewReiwaEraInJapanese() {
        // ignored if the Reiwa era is not supported in the current JVM
        assumeTrue(ChronologyParameterManager.IS_REIWA_ERA_SUPPORTED);
        assertTrue(SystemDateTimePatternManager.isDate("0017-02-28 令和")); //$NON-NLS-1$
    }

    @Test
    public void datesWithJapaneseChronologyWithHeiseiEraInJapanese() {
        // ignored if the Reiwa era is not supported in the current JVM
        assumeTrue(ChronologyParameterManager.IS_REIWA_ERA_SUPPORTED);
        assertTrue(SystemDateTimePatternManager.isDate("0017-02-28 平成")); //$NON-NLS-1$
    }

    @Test
    public void datesWithMingoChronology() {
        assertTrue(SystemDateTimePatternManager.isDate("0017-02-28 民國")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isDate("0017-02-28 民國前")); //$NON-NLS-1$
    }

    @Test
    public void datesWithHijrahChronology() {
        assertTrue(SystemDateTimePatternManager.isDate("1345-02-28 هـ")); //$NON-NLS-1$
    }

    @Test
    public void testTDC_4108() {
        // already supported
        assertTrue(SystemDateTimePatternManager.isDate("2020-04-29T08:49:29")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isDate("2020-04-29 08:49:29")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isDate("2020-04-29T08:49:29.000Z")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isDate("2020-04-29 08:49:29.000Z")); //$NON-NLS-1$
        // newly supported
        assertTrue(SystemDateTimePatternManager.isDate("2020-04-29T08:49:29Z")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isDate("2020-04-29 08:49:29Z")); //$NON-NLS-1$
    }

    @Test
    public void validateUnusualTimeZones() {
        // Kiribati
        assertTrue(SystemDateTimePatternManager.isDate("2020-04-29T08:49:29+14:00")); //$NON-NLS-1$
        // Mumbai
        assertTrue(SystemDateTimePatternManager.isDate("2020-04-29T08:49:29+05:30")); //$NON-NLS-1$
    }

    @Test
    public void validateDatesWithOffsetTimeZones() {
        assertTrue(SystemDateTimePatternManager.isDate("2020-04-29T08:49:29+0200")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isDate("2020-04-29T08:49:29+01")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isDate("2020-04-29 08:49:29+0200")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isDate("2020-04-29 08:49:29+01")); //$NON-NLS-1$
    }

    @Test
    public void validateTimesWithOffsetTimeZones() {
        assertTrue(SystemDateTimePatternManager.isTime("08:49:29+02:00")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isTime("08:49:29+0200")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isTime("08:49:29+01")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isTime("8.49.29+02:00")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isTime("8.49.29+0200")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isTime("8.49.29+01")); //$NON-NLS-1$
    }

    @Test
    public void datesWithThaiBuddhistChronology() {
        assertTrue(SystemDateTimePatternManager.isDate("1345-02-28 พ.ศ.")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isDate("1345-02-28 ปีก่อนคริสต์กาลที่")); //$NON-NLS-1$
    }

    @Test
    public void BadMonthName_TDQ13347() {
        assertFalse(SystemDateTimePatternManager.isDate("01 TOTO 2015")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isDate("01 JANUARY 2015")); //$NON-NLS-1$
    }

    @Test
    public void testgetDateTimeFormatterByPattern() {
        DateTimeFormatter dateTimeFormatterByPattern =
                SystemDateTimePatternManager.getDateTimeFormatterByPattern("dd/MM/yyyy", Locale.ENGLISH);
        assertFalse(dateTimeFormatterByPattern == null);
        assertTrue(dateTimeFormatterByPattern.getResolverStyle() == ResolverStyle.STRICT);
        assertEquals("17/08/2015", dateTimeFormatterByPattern.format(LocalDate.of(2015, 8, 17)));
        dateTimeFormatterByPattern =
                SystemDateTimePatternManager.getDateTimeFormatterByPattern("yyyy-MM-dd G", Locale.US);
        assertFalse(dateTimeFormatterByPattern == null);
        assertEquals("2015-08-17 AD", dateTimeFormatterByPattern.format(LocalDate.of(2015, 8, 17)));
        dateTimeFormatterByPattern = SystemDateTimePatternManager.getDateTimeFormatterByPattern("yyyy-MM-dd G", null);
        assertTrue(dateTimeFormatterByPattern == null);
    }

}
