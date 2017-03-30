// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SAps
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.converters;

import static org.junit.Assert.*;

import java.time.temporal.ChronoUnit;

import org.junit.Test;

/**
 * Test for class {@link DurationConverter}.
 * <p>
 * Created by msjian on 2017-02-27
 */
public class DurationConverterTest {

    private double delta = 1;

    // 1 year = 365 days = 12 months (!= 12 * 30)
    // 1 month = 30 days
    long year = 1;

    long month = 12;

    long week = 52;

    long day = 365;

    long hour = 8760;// 365 * 24;

    long minute = 525600;// 365 * 24 * 60;

    long second = 31536000;// 365 * 24 * 60 * 60;

    long millisecond = 31536000000L;// (365 * 24 * 60 * 60 * 1000);

    @Test
    public void testConvertZero() {
        long zero = 0;
        assertEquals(zero,
                new DurationConverter(DurationConverter.DEFAULT_FROM_UNIT, DurationConverter.DEFAULT_TO_UNIT).convert(zero));
    }

    @Test
    public void testConvertMaxValue() {
        long max = Long.MAX_VALUE;
        assertEquals(max, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.MONTHS).convert(max));
        assertEquals(max, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.YEARS).convert(max));
    }

    @Test
    public void testConvertMinValue() {
        long min = Long.MIN_VALUE;
        assertEquals(min, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.YEARS).convert(min));
        assertEquals(min, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.MONTHS).convert(min));
    }

    @Test
    public void testConvertDefault() {
        long day = 1;
        long hour = 24;
        assertEquals(hour, new DurationConverter().convert(day));
    }

    @Test
    public void testConvertYEARS() {
        assertEquals(year, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.YEARS).convert(year));
        assertEquals(month, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.MONTHS).convert(year));
        assertEquals(week, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.WEEKS).convert(year), delta);
        assertEquals(day, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.DAYS).convert(year));
        assertEquals(hour, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.HOURS).convert(year));
        assertEquals(minute, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.MINUTES).convert(year));
        assertEquals(second, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.SECONDS).convert(year));
        assertEquals(millisecond, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.MILLIS).convert(year));
    }

    @Test
    public void testConvertMONTHS() {
        assertEquals(year, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.YEARS).convert(month));
        assertEquals(month, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.MONTHS).convert(month));
        assertEquals(week, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.WEEKS).convert(month));
        assertEquals(day, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.DAYS).convert(month));
        assertEquals(hour, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.HOURS).convert(month));
        assertEquals(minute, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.MINUTES).convert(month));
        assertEquals(second, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.SECONDS).convert(month));
        assertEquals(millisecond, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.MILLIS).convert(month));
    }

    @Test
    public void testConvertWEEKS() {
        assertEquals(year, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.YEARS).convert(week));
        assertEquals(month, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.MONTHS).convert(week));
        assertEquals(week, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.WEEKS).convert(week));
        assertEquals(day, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.DAYS).convert(week));
        assertEquals(hour, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.HOURS).convert(week));
        assertEquals(minute, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.MINUTES).convert(week));
        assertEquals(second, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.SECONDS).convert(week));
        assertEquals(millisecond, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.MILLIS).convert(week));
    }

    @Test
    public void testConvertHOURS() {
        assertEquals(year, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.YEARS).convert(hour));
        assertEquals(month, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.MONTHS).convert(hour));
        assertEquals(week, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.WEEKS).convert(hour), delta);
        assertEquals(day, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.DAYS).convert(hour));
        assertEquals(hour, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.HOURS).convert(hour));
        assertEquals(minute, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.MINUTES).convert(hour));
        assertEquals(second, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.SECONDS).convert(hour));
        assertEquals(millisecond, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.MILLIS).convert(hour));
    }

    @Test
    public void testConvertMINUTES() {
        assertEquals(year, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.YEARS).convert(minute));
        assertEquals(month, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.MONTHS).convert(minute));
        assertEquals(week, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.WEEKS).convert(minute), delta);
        assertEquals(day, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.DAYS).convert(minute));
        assertEquals(hour, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.HOURS).convert(minute));
        assertEquals(minute, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.MINUTES).convert(minute));
        assertEquals(second, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.SECONDS).convert(minute));
        assertEquals(millisecond, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.MILLIS).convert(minute));
    }

    @Test
    public void testConvertSECONDS() {
        assertEquals(year, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.YEARS).convert(second));
        assertEquals(month, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.MONTHS).convert(second));
        assertEquals(week, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.WEEKS).convert(second), delta);
        assertEquals(day, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.DAYS).convert(second));
        assertEquals(hour, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.HOURS).convert(second));
        assertEquals(minute, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.MINUTES).convert(second));
        assertEquals(second, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.SECONDS).convert(second));
        assertEquals(millisecond, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.MILLIS).convert(second));
    }

    @Test
    public void testConvertMILLIS() {
        assertEquals(year, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.YEARS).convert(millisecond));
        assertEquals(month, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.MONTHS).convert(millisecond));
        assertEquals(week, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.WEEKS).convert(millisecond), delta);
        assertEquals(day, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.DAYS).convert(millisecond));
        assertEquals(hour, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.HOURS).convert(millisecond));
        assertEquals(minute, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.MINUTES).convert(millisecond));
        assertEquals(second, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.SECONDS).convert(millisecond));
        assertEquals(millisecond, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.MILLIS).convert(millisecond));
    }
}
