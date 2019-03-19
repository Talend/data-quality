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

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

import org.junit.Test;

public class SystemDateTimePatternManagerTest {

    @Test
    public void BadMonthName_TDQ13347() {
        assertFalse(SystemDateTimePatternManager.isDate("01 TOTO 2015")); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isDate("01 JANUARY 2015")); //$NON-NLS-1$
    }

    @Test
    public void testIsMatchDateTimePatternInvalid() {
        String pattern = "yyyy-MM-dd"; //$NON-NLS-1$
        assertFalse(SystemDateTimePatternManager.isMatchDateTimePattern("2017-02-29", pattern, Locale.CHINESE)); //$NON-NLS-1$
        assertFalse(SystemDateTimePatternManager.isMatchDateTimePattern("2017-04-31", pattern, Locale.US)); //$NON-NLS-1$
        assertFalse(SystemDateTimePatternManager.isMatchDateTimePattern("31/11/2017", pattern, Locale.US)); //$NON-NLS-1$
        assertFalse(SystemDateTimePatternManager.isMatchDateTimePattern("2015/01/32", pattern, Locale.US)); //$NON-NLS-1$
        // the pattern is not adapt this value. should be "2011-05-02";
        assertFalse(SystemDateTimePatternManager.isMatchDateTimePattern("2011-5-02", pattern, Locale.CHINESE)); //$NON-NLS-1$
    }

    @Test
    public void testIsMatchDateTimePatternValid() {
        String pattern = "yyyy-MM-dd"; //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isMatchDateTimePattern("2016-02-29", pattern, Locale.CHINESE)); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isMatchDateTimePattern("2017-05-02", pattern, Locale.CHINESE)); //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isMatchDateTimePattern("2017-11-30", pattern, Locale.CHINESE)); //$NON-NLS-1$
        pattern = "dd/MM/yyy"; //$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isMatchDateTimePattern("30/01/2017", pattern, Locale.US)); //$NON-NLS-1$
        pattern = "yyyy-MM-dd G"; //$NON-NLS-1$
        assertFalse(SystemDateTimePatternManager.isMatchDateTimePattern("2017-02-15 AD", pattern, Locale.CHINESE)); //$NON-NLS-1$
        assertFalse(SystemDateTimePatternManager.isMatchDateTimePattern("4714-11-12 BC", pattern, Locale.CHINESE)); //$NON-NLS-1$
        pattern = "MMMM d, y GG";//$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isMatchDateTimePattern("March 15, 44 BC", pattern, Locale.US)); //$NON-NLS-1$
        pattern = "MMMM d, u";//$NON-NLS-1$
        assertTrue(SystemDateTimePatternManager.isMatchDateTimePattern("March 15, -43", pattern, Locale.US)); //$NON-NLS-1$
    }

    @Test
    public void testgetDateTimeFormatterByPattern() {
        DateTimeFormatter dateTimeFormatterByPattern = SystemDateTimePatternManager.getDateTimeFormatterByPattern("dd/MM/yyyy", //$NON-NLS-1$
                Locale.ENGLISH);
        assertFalse(dateTimeFormatterByPattern == null);
        assertTrue(dateTimeFormatterByPattern.getResolverStyle() == ResolverStyle.STRICT);
        assertEquals("17/08/2015", dateTimeFormatterByPattern.format(LocalDate.of(2015, 8, 17))); //$NON-NLS-1$
        dateTimeFormatterByPattern = SystemDateTimePatternManager.getDateTimeFormatterByPattern("yyyy-MM-dd G", Locale.US); //$NON-NLS-1$
        assertFalse(dateTimeFormatterByPattern == null);
        assertEquals("2015-08-17 AD", dateTimeFormatterByPattern.format(LocalDate.of(2015, 8, 17))); //$NON-NLS-1$
        dateTimeFormatterByPattern = SystemDateTimePatternManager.getDateTimeFormatterByPattern("yyyy-MM-dd G", null); //$NON-NLS-1$
        assertTrue(dateTimeFormatterByPattern == null);
    }

    @Test
    public void testIsDateString() {
        boolean isDate = SystemDateTimePatternManager.isDate("2013-02-14 13:40:51.123"); //$NON-NLS-1$
        assertTrue(isDate);
        isDate = SystemDateTimePatternManager.isDate("2013-12-04 13:40:51.123"); //$NON-NLS-1$
        assertTrue(isDate);
        isDate = SystemDateTimePatternManager.isDate("2013-13-04 13:40:51.123"); //$NON-NLS-1$
        assertFalse(isDate);
        isDate = SystemDateTimePatternManager.isDate("2013-12-04 25:40:51.123"); //$NON-NLS-1$
        assertFalse(isDate);
        isDate = SystemDateTimePatternManager.isDate("2013-12-04 13:40:61.123"); //$NON-NLS-1$
        assertFalse(isDate);
        isDate = SystemDateTimePatternManager.isDate("2013-12-04 13:60:51.123"); //$NON-NLS-1$
        assertFalse(isDate);
    }

}
