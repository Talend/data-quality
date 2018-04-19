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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.time.temporal.ChronoUnit;

import org.junit.Test;

/**
 * Test for class {@link DurationConverter}.
 * <p>
 * Created by msjian on 2017-02-27
 */
public class DurationConverterTest {

    private double delta = 1.0E-1d;

    // 1 year = 365 days = 12 months (!= 12 * 30)
    // 1 month = 30 days
    private static double year = 1d;

    private static double month = 12.2d;

    private static double week = 52.1d;

    private static double day = 365d;

    private static double hour = 8760d;// 365 * 24;

    private static double minute = 525600d;// 365 * 24 * 60;

    private static double second = 31536000d;// 365 * 24 * 60 * 60;

    private static double millisecond = 31536000000d;// (365 * 24 * 60 * 60 * 1000);

    @Test
    public void testConvertMonths2Days() {
        assertEquals(1 * 365 + 1 * 30, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.DAYS).convert(13), delta);
        assertNotEquals(13 * 30, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.DAYS).convert(13), delta);

        assertEquals(5 * 7, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.DAYS).convert(5), delta);
        assertEquals(4.3, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.WEEKS).convert(1), delta);
    }

    @Test
    public void testConvertZero() {
        double zero = 0;
        assertEquals(zero,
                new DurationConverter(DurationConverter.DEFAULT_FROM_UNIT, DurationConverter.DEFAULT_TO_UNIT).convert(zero),
                delta);
    }

    @Test
    public void testConvertDoubleNan() {
        double nan = Double.NaN;
        assertEquals(nan,
                new DurationConverter(DurationConverter.DEFAULT_FROM_UNIT, DurationConverter.DEFAULT_TO_UNIT).convert(nan),
                delta);
    }

    @Test
    public void testConvertMaxValue() {
        double max = Double.MAX_VALUE;
        assertEquals(max, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.MONTHS).convert(max), delta);
        assertEquals(max, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.YEARS).convert(max), delta);
    }

    @Test
    public void testConvertMinValue() {
        double min = Double.MIN_VALUE;
        assertEquals(min, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.YEARS).convert(min), delta);
        assertEquals(min, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.MONTHS).convert(min), delta);
    }

    @Test
    public void testConvertDefault() {
        double day = 1;
        double hour = 24;
        assertEquals(hour, new DurationConverter().convert(day), delta);
        assertEquals(hour, new DurationConverter(ChronoUnit.DAYS, null).convert(day), delta);
        assertEquals(hour, new DurationConverter(null, ChronoUnit.HOURS).convert(day), delta);
    }

    @Test
    public void testConvertYEARS() {
        assertEquals(year, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.YEARS).convert(year), delta);
        assertEquals(month, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.MONTHS).convert(year), delta);
        assertEquals(week, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.WEEKS).convert(year), delta);
        assertEquals(day, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.DAYS).convert(year), delta);
        assertEquals(hour, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.HOURS).convert(year), delta);
        assertEquals(minute, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.MINUTES).convert(year), delta);
        assertEquals(second, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.SECONDS).convert(year), delta);
        assertEquals(millisecond, new DurationConverter(ChronoUnit.YEARS, ChronoUnit.MILLIS).convert(year), delta);
    }

    @Test
    public void testConvertMONTHS() {
        assertEquals(year, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.YEARS).convert(month), delta);
        assertEquals(month, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.MONTHS).convert(month), delta);
        assertEquals(52.3, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.WEEKS).convert(month), delta);
        assertEquals(day, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.DAYS).convert(month), delta);
        assertEquals(hour, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.HOURS).convert(month), delta);
        assertEquals(minute, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.MINUTES).convert(month), delta);
        assertEquals(second, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.SECONDS).convert(month), delta);
        assertEquals(millisecond, new DurationConverter(ChronoUnit.MONTHS, ChronoUnit.MILLIS).convert(month), delta);
    }

    @Test
    public void testConvertWEEKS() {
        assertEquals(year, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.YEARS).convert(week), delta);
        assertEquals(month, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.MONTHS).convert(week), delta);
        assertEquals(week, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.WEEKS).convert(week), delta);
        assertEquals(day, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.DAYS).convert(week), delta);
        assertEquals(hour, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.HOURS).convert(week), delta);
        assertEquals(minute, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.MINUTES).convert(week), delta);
        assertEquals(second, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.SECONDS).convert(week), delta);
        assertEquals(millisecond, new DurationConverter(ChronoUnit.WEEKS, ChronoUnit.MILLIS).convert(week), delta);
    }

    @Test
    public void testConvertDAYS() {
        assertEquals(year, new DurationConverter(ChronoUnit.DAYS, ChronoUnit.YEARS).convert(day), delta);
        assertEquals(month, new DurationConverter(ChronoUnit.DAYS, ChronoUnit.MONTHS).convert(day), delta);
        assertEquals(week, new DurationConverter(ChronoUnit.DAYS, ChronoUnit.WEEKS).convert(day), delta);
        assertEquals(day, new DurationConverter(ChronoUnit.DAYS, ChronoUnit.DAYS).convert(day), delta);
        assertEquals(hour, new DurationConverter(ChronoUnit.DAYS, ChronoUnit.HOURS).convert(day), delta);
        assertEquals(minute, new DurationConverter(ChronoUnit.DAYS, ChronoUnit.MINUTES).convert(day), delta);
        assertEquals(second, new DurationConverter(ChronoUnit.DAYS, ChronoUnit.SECONDS).convert(day), delta);
        assertEquals(millisecond, new DurationConverter(ChronoUnit.DAYS, ChronoUnit.MILLIS).convert(day), delta);

        assertEquals(3.9, new DurationConverter(ChronoUnit.DAYS, ChronoUnit.YEARS).convert(1440), delta);
    }

    @Test
    public void testConvertHOURS() {
        assertEquals(year, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.YEARS).convert(hour), delta);
        assertEquals(month, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.MONTHS).convert(hour), delta);
        assertEquals(week, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.WEEKS).convert(hour), delta);
        assertEquals(day, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.DAYS).convert(hour), delta);
        assertEquals(hour, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.HOURS).convert(hour), delta);
        assertEquals(minute, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.MINUTES).convert(hour), delta);
        assertEquals(second, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.SECONDS).convert(hour), delta);
        assertEquals(millisecond, new DurationConverter(ChronoUnit.HOURS, ChronoUnit.MILLIS).convert(hour), delta);
    }

    @Test
    public void testConvertMINUTES() {
        assertEquals(year, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.YEARS).convert(minute), delta);
        assertEquals(month, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.MONTHS).convert(minute), delta);
        assertEquals(week, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.WEEKS).convert(minute), delta);
        assertEquals(day, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.DAYS).convert(minute), delta);
        assertEquals(hour, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.HOURS).convert(minute), delta);
        assertEquals(minute, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.MINUTES).convert(minute), delta);
        assertEquals(second, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.SECONDS).convert(minute), delta);
        assertEquals(millisecond, new DurationConverter(ChronoUnit.MINUTES, ChronoUnit.MILLIS).convert(minute), delta);
    }

    @Test
    public void testConvertSECONDS() {
        assertEquals(year, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.YEARS).convert(second), delta);
        assertEquals(month, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.MONTHS).convert(second), delta);
        assertEquals(week, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.WEEKS).convert(second), delta);
        assertEquals(day, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.DAYS).convert(second), delta);
        assertEquals(hour, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.HOURS).convert(second), delta);
        assertEquals(minute, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.MINUTES).convert(second), delta);
        assertEquals(second, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.SECONDS).convert(second), delta);
        assertEquals(millisecond, new DurationConverter(ChronoUnit.SECONDS, ChronoUnit.MILLIS).convert(second), delta);
    }

    @Test
    public void testConvertMILLIS() {
        assertEquals(year, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.YEARS).convert(millisecond), delta);
        assertEquals(month, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.MONTHS).convert(millisecond), delta);
        assertEquals(week, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.WEEKS).convert(millisecond), delta);
        assertEquals(day, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.DAYS).convert(millisecond), delta);
        assertEquals(hour, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.HOURS).convert(millisecond), delta);
        assertEquals(minute, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.MINUTES).convert(millisecond), delta);
        assertEquals(second, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.SECONDS).convert(millisecond), delta);
        assertEquals(millisecond, new DurationConverter(ChronoUnit.MILLIS, ChronoUnit.MILLIS).convert(millisecond), delta);
    }
}
