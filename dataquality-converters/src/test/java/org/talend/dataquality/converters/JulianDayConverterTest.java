// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.converters;

import static org.junit.Assert.assertEquals;

import java.time.chrono.HijrahChronology;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.MinguoChronology;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.JulianFields;

import org.junit.Test;


/**
 * DOC qiongli class global comment. Detailled comment
 */
public class JulianDayConverterTest {

    private String calendarISO = "2017-05-18"; //$NON-NLS-1$

    private String calendarJapa = "0029-05-18"; //$NON-NLS-1$

    private String calendarMingGuo = "0106-05-18"; //$NON-NLS-1$

    private String calendarHijr = "1438-08-22"; //$NON-NLS-1$

    private String calendarThaiBudd = "2560-05-18"; //$NON-NLS-1$

    private String julianDay = "2457892"; //$NON-NLS-1$

    private String modiJulianDay = "57891"; //$NON-NLS-1$

    private String rataDie = "736467"; //$NON-NLS-1$

    private String epochDay = "17304"; //$NON-NLS-1$


    @Test
    public void testJulianToCalendar() {
        JulianDayConverter jc = new JulianDayConverter(JulianFields.JULIAN_DAY, IsoChronology.INSTANCE);
        assertEquals(calendarISO, jc.convert(julianDay));

        jc = new JulianDayConverter(JulianFields.JULIAN_DAY, JapaneseChronology.INSTANCE);
        assertEquals(calendarJapa, jc.convert(julianDay));

        jc = new JulianDayConverter(JulianFields.JULIAN_DAY, HijrahChronology.INSTANCE);
        assertEquals(calendarHijr, jc.convert(julianDay));

        jc = new JulianDayConverter(JulianFields.JULIAN_DAY, MinguoChronology.INSTANCE);
        assertEquals(calendarMingGuo, jc.convert(julianDay));

        jc = new JulianDayConverter(JulianFields.JULIAN_DAY, ThaiBuddhistChronology.INSTANCE);
        assertEquals(calendarThaiBudd, jc.convert(julianDay));

    }

    @Test
    public void testModifyJulianToCalendar() {
        JulianDayConverter jc = new JulianDayConverter(JulianFields.MODIFIED_JULIAN_DAY, IsoChronology.INSTANCE);
        assertEquals(calendarISO, jc.convert(modiJulianDay));

        jc = new JulianDayConverter(JulianFields.MODIFIED_JULIAN_DAY, JapaneseChronology.INSTANCE);
        assertEquals(calendarJapa, jc.convert(modiJulianDay));

        jc = new JulianDayConverter(JulianFields.MODIFIED_JULIAN_DAY, HijrahChronology.INSTANCE);
        assertEquals(calendarHijr, jc.convert(modiJulianDay));

        jc = new JulianDayConverter(JulianFields.MODIFIED_JULIAN_DAY, MinguoChronology.INSTANCE);
        assertEquals(calendarMingGuo, jc.convert(modiJulianDay));

        jc = new JulianDayConverter(JulianFields.MODIFIED_JULIAN_DAY, ThaiBuddhistChronology.INSTANCE);
        assertEquals(calendarThaiBudd, jc.convert(modiJulianDay));
    }

    @Test
    public void testRataDieToCalendar() {
        JulianDayConverter jc = new JulianDayConverter(JulianFields.RATA_DIE, IsoChronology.INSTANCE);
        assertEquals(calendarISO, jc.convert(rataDie));

        jc = new JulianDayConverter(JulianFields.RATA_DIE, JapaneseChronology.INSTANCE);
        assertEquals(calendarJapa, jc.convert(rataDie));

        jc = new JulianDayConverter(JulianFields.RATA_DIE, HijrahChronology.INSTANCE);
        assertEquals(calendarHijr, jc.convert(rataDie));

        jc = new JulianDayConverter(JulianFields.RATA_DIE, MinguoChronology.INSTANCE);
        assertEquals(calendarMingGuo, jc.convert(rataDie));

        jc = new JulianDayConverter(JulianFields.RATA_DIE, ThaiBuddhistChronology.INSTANCE);
        assertEquals(calendarThaiBudd, jc.convert(rataDie));
    }

    @Test
    public void testEpchoToCalendar() {
        JulianDayConverter jc = new JulianDayConverter(ChronoField.EPOCH_DAY, IsoChronology.INSTANCE);
        assertEquals(calendarISO, jc.convert(epochDay));

        jc = new JulianDayConverter(ChronoField.EPOCH_DAY, JapaneseChronology.INSTANCE);
        assertEquals(calendarJapa, jc.convert(epochDay));

        jc = new JulianDayConverter(ChronoField.EPOCH_DAY, HijrahChronology.INSTANCE);
        assertEquals(calendarHijr, jc.convert(epochDay));

        jc = new JulianDayConverter(ChronoField.EPOCH_DAY, MinguoChronology.INSTANCE);
        assertEquals(calendarMingGuo, jc.convert(epochDay));

        jc = new JulianDayConverter(ChronoField.EPOCH_DAY, ThaiBuddhistChronology.INSTANCE);
        assertEquals(calendarThaiBudd, jc.convert(epochDay));
    }

    @Test
    public void testCalendarISOToNumber() {
        JulianDayConverter jc = new JulianDayConverter(IsoChronology.INSTANCE, ChronoField.EPOCH_DAY);
        assertEquals(epochDay, jc.convert(calendarISO));

        jc = new JulianDayConverter(IsoChronology.INSTANCE, JulianFields.JULIAN_DAY);
        assertEquals(julianDay, jc.convert(calendarISO));

        jc = new JulianDayConverter(IsoChronology.INSTANCE, JulianFields.MODIFIED_JULIAN_DAY);
        assertEquals(modiJulianDay, jc.convert(calendarISO));

        jc = new JulianDayConverter(IsoChronology.INSTANCE, JulianFields.RATA_DIE);
        assertEquals(rataDie, jc.convert(calendarISO));

    }

    @Test
    public void testCalendarJapaToNumber() {
        JulianDayConverter jc = new JulianDayConverter(JapaneseChronology.INSTANCE, ChronoField.EPOCH_DAY);
        assertEquals(epochDay, jc.convert(calendarJapa));

        jc = new JulianDayConverter(JapaneseChronology.INSTANCE, JulianFields.JULIAN_DAY);
        assertEquals(julianDay, jc.convert(calendarJapa));

        jc = new JulianDayConverter(JapaneseChronology.INSTANCE, JulianFields.MODIFIED_JULIAN_DAY);
        assertEquals(modiJulianDay, jc.convert(calendarJapa));

        jc = new JulianDayConverter(JapaneseChronology.INSTANCE, JulianFields.RATA_DIE);
        assertEquals(rataDie, jc.convert(calendarJapa));
    }

    @Test
    public void testCalendarMingGuoToNumeber() {
        JulianDayConverter jc = new JulianDayConverter(MinguoChronology.INSTANCE, ChronoField.EPOCH_DAY);
        assertEquals(epochDay, jc.convert(calendarMingGuo));

        jc = new JulianDayConverter(MinguoChronology.INSTANCE, JulianFields.JULIAN_DAY);
        assertEquals(julianDay, jc.convert(calendarMingGuo));

        jc = new JulianDayConverter(MinguoChronology.INSTANCE, JulianFields.MODIFIED_JULIAN_DAY);
        assertEquals(modiJulianDay, jc.convert(calendarMingGuo));

        jc = new JulianDayConverter(MinguoChronology.INSTANCE, JulianFields.RATA_DIE);
        assertEquals(rataDie, jc.convert(calendarMingGuo));
    }

    @Test
    public void testCalendarHijrToNumeber() {
        JulianDayConverter jc = new JulianDayConverter(HijrahChronology.INSTANCE, ChronoField.EPOCH_DAY);
        assertEquals(epochDay, jc.convert(calendarHijr));

        jc = new JulianDayConverter(HijrahChronology.INSTANCE, JulianFields.JULIAN_DAY);
        assertEquals(julianDay, jc.convert(calendarHijr));

        jc = new JulianDayConverter(HijrahChronology.INSTANCE, JulianFields.MODIFIED_JULIAN_DAY);
        assertEquals(modiJulianDay, jc.convert(calendarHijr));

        jc = new JulianDayConverter(HijrahChronology.INSTANCE, JulianFields.RATA_DIE);
        assertEquals(rataDie, jc.convert(calendarHijr));
    }

    @Test
    public void testCalendarThaiBuddToNumber() {
        JulianDayConverter jc = new JulianDayConverter(ThaiBuddhistChronology.INSTANCE, ChronoField.EPOCH_DAY);
        assertEquals(epochDay, jc.convert(calendarThaiBudd));

        jc = new JulianDayConverter(ThaiBuddhistChronology.INSTANCE, JulianFields.JULIAN_DAY);
        assertEquals(julianDay, jc.convert(calendarThaiBudd));

        jc = new JulianDayConverter(ThaiBuddhistChronology.INSTANCE, JulianFields.MODIFIED_JULIAN_DAY);
        assertEquals(modiJulianDay, jc.convert(calendarThaiBudd));

        jc = new JulianDayConverter(ThaiBuddhistChronology.INSTANCE, JulianFields.RATA_DIE);
        assertEquals(rataDie, jc.convert(calendarThaiBudd));
    }

    @Test
    public void testJulianToOtherNumbers() {
        JulianDayConverter jc = new JulianDayConverter(JulianFields.JULIAN_DAY, JulianFields.MODIFIED_JULIAN_DAY);
        assertEquals(modiJulianDay, jc.convert(julianDay));

        jc = new JulianDayConverter(JulianFields.JULIAN_DAY, JulianFields.RATA_DIE);
        assertEquals(rataDie, jc.convert(julianDay));

        jc = new JulianDayConverter(JulianFields.JULIAN_DAY, ChronoField.EPOCH_DAY);
        assertEquals(epochDay, jc.convert(julianDay));
    }

    @Test
    public void testModifyJulianToOtherNumbers() {
        JulianDayConverter jc = new JulianDayConverter(JulianFields.MODIFIED_JULIAN_DAY, JulianFields.JULIAN_DAY);
        assertEquals(julianDay, jc.convert(modiJulianDay));

        jc = new JulianDayConverter(JulianFields.MODIFIED_JULIAN_DAY, JulianFields.RATA_DIE);
        assertEquals(rataDie, jc.convert(modiJulianDay));

        jc = new JulianDayConverter(JulianFields.MODIFIED_JULIAN_DAY, ChronoField.EPOCH_DAY);
        assertEquals(epochDay, jc.convert(modiJulianDay));
    }

    @Test
    public void testRataDieToOtherNumbers() {
        JulianDayConverter jc = new JulianDayConverter(JulianFields.RATA_DIE, JulianFields.JULIAN_DAY);
        assertEquals(julianDay, jc.convert(rataDie));

        jc = new JulianDayConverter(JulianFields.RATA_DIE, JulianFields.MODIFIED_JULIAN_DAY);
        assertEquals(modiJulianDay, jc.convert(rataDie));

        jc = new JulianDayConverter(JulianFields.RATA_DIE, ChronoField.EPOCH_DAY);
        assertEquals(epochDay, jc.convert(rataDie));
    }

    @Test
    public void testEpochToOtherNumbers() {
        JulianDayConverter jc = new JulianDayConverter(ChronoField.EPOCH_DAY, JulianFields.JULIAN_DAY);
        assertEquals(julianDay, jc.convert(epochDay));

        jc = new JulianDayConverter(ChronoField.EPOCH_DAY, JulianFields.MODIFIED_JULIAN_DAY);
        assertEquals(modiJulianDay, jc.convert(epochDay));

        jc = new JulianDayConverter(ChronoField.EPOCH_DAY, JulianFields.RATA_DIE);
        assertEquals(rataDie, jc.convert(epochDay));
    }

    @Test
    public void testInvalidConvert() {

        JulianDayConverter jc = new JulianDayConverter(JulianFields.JULIAN_DAY, IsoChronology.INSTANCE);
        assertEquals(null, jc.convert(null));

        jc = new JulianDayConverter(JulianFields.MODIFIED_JULIAN_DAY, IsoChronology.INSTANCE);
        assertEquals("", jc.convert("")); //$NON-NLS-1$ //$NON-NLS-2$

        jc = new JulianDayConverter(IsoChronology.INSTANCE, JulianFields.JULIAN_DAY);
        assertEquals("abc", jc.convert("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        
        jc = new JulianDayConverter(IsoChronology.INSTANCE, null);
        assertEquals("abc", jc.convert("abc")); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
