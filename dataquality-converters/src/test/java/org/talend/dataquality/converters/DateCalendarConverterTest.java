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
package org.talend.dataquality.converters;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.Chronology;
import java.time.chrono.HijrahChronology;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.MinguoChronology;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link DateCalendarConverter}.
 * 
 * @author msjian
 * @version 2017.02.08
 */
public class DateCalendarConverterTest {

    private static final Logger LOGGER = Logger.getLogger(DateCalendarConverterTest.class);

    private static final String pattern = "yyyy-MM-dd"; //$NON-NLS-1$

    private static final String IsoStr = "1996-10-29"; //$NON-NLS-1$

    private static final String HijrahStr = "1417-06-16"; //$NON-NLS-1$

    private static final String JapaneseStr = "0008-10-29";//$NON-NLS-1$

    private static final String MinguoStr = "0085-10-29"; //$NON-NLS-1$

    private static final String ThaiBuddhistStr = "2539-10-29"; //$NON-NLS-1$

    private static final String pattern1 = "yyyy/MM/dd"; //$NON-NLS-1$

    private static final String IsoStr1 = "1996/10/29"; //$NON-NLS-1$

    private static final String HijrahStr1 = "1417/06/16"; //$NON-NLS-1$

    private static final String JapaneseStr1 = "0008/10/29";//$NON-NLS-1$

    private static final String MinguoStr1 = "0085/10/29"; //$NON-NLS-1$

    private static final String ThaiBuddhistStr1 = "2539/10/29"; //$NON-NLS-1$

    private static final String pattern2 = "yy/MM/dd"; //$NON-NLS-1$

    private static final String IsoStr2 = "96/10/29"; //$NON-NLS-1$

    private static final String pattern3 = "MM/dd/yyyy"; //$NON-NLS-1$

    private static final String IsoStr3 = "10/29/1996"; //$NON-NLS-1$

    private static final String pattern4 = "yyyyMMdd"; //$NON-NLS-1$

    private static final String IsoStr4 = "19961029"; //$NON-NLS-1$

    private static final String JapaneseStr4 = "00081029";//$NON-NLS-1$

    private static final String pattern5 = "M/d/yyyy GGGGG"; //$NON-NLS-1$

    private static final String IsoStr5 = "10/29/1996 A"; //$NON-NLS-1$

    private static final String HijrahStr5 = "6/16/1417 1"; //$NON-NLS-1$

    private static final String JapaneseStr5 = "10/29/0008 H";//$NON-NLS-1$

    private static final String MinguoStr5 = "10/29/0085 1"; //$NON-NLS-1$

    private static final String ThaiBuddhistStr5 = "10/29/2539 B.E."; //$NON-NLS-1$

    private static final String pattern6 = "yyyy MM dd"; //$NON-NLS-1$

    private static final String HijrahStr2 = "1417 06 16"; //$NON-NLS-1$

    @Test
    public void TestConvertIsoDateTo() {
        assertEquals(HijrahStr, new DateCalendarConverter(IsoStr, IsoChronology.INSTANCE, HijrahChronology.INSTANCE).convert());
        assertEquals(JapaneseStr,
                new DateCalendarConverter(IsoStr, IsoChronology.INSTANCE, JapaneseChronology.INSTANCE).convert());
        assertEquals(MinguoStr, new DateCalendarConverter(IsoStr, IsoChronology.INSTANCE, MinguoChronology.INSTANCE).convert());
        assertEquals(ThaiBuddhistStr,
                new DateCalendarConverter(IsoStr, IsoChronology.INSTANCE, ThaiBuddhistChronology.INSTANCE).convert());

        assertEquals(IsoStr2,
                new DateCalendarConverter(IsoStr, pattern, pattern2, IsoChronology.INSTANCE, IsoChronology.INSTANCE).convert());
        assertEquals(IsoStr3,
                new DateCalendarConverter(IsoStr, pattern, pattern3, IsoChronology.INSTANCE, IsoChronology.INSTANCE).convert());
        assertEquals(IsoStr4,
                new DateCalendarConverter(IsoStr, pattern, pattern4, IsoChronology.INSTANCE, IsoChronology.INSTANCE).convert());
        assertEquals(IsoStr5,
                new DateCalendarConverter(IsoStr, pattern, pattern5, IsoChronology.INSTANCE, IsoChronology.INSTANCE).convert());

        assertEquals(HijrahStr1, new DateCalendarConverter(IsoStr, pattern, pattern1, IsoChronology.INSTANCE,
                HijrahChronology.INSTANCE).convert());
        assertEquals(HijrahStr1, new DateCalendarConverter(IsoStr1, pattern1, pattern1, IsoChronology.INSTANCE,
                HijrahChronology.INSTANCE).convert());
        assertEquals(HijrahStr, new DateCalendarConverter(IsoStr1, pattern1, pattern, IsoChronology.INSTANCE,
                HijrahChronology.INSTANCE).convert());
        assertEquals(HijrahStr5, new DateCalendarConverter(IsoStr1, pattern1, pattern5, IsoChronology.INSTANCE,
                HijrahChronology.INSTANCE).convert());

        assertEquals(JapaneseStr1, new DateCalendarConverter(IsoStr, pattern, pattern1, IsoChronology.INSTANCE,
                JapaneseChronology.INSTANCE).convert());
        assertEquals(JapaneseStr1, new DateCalendarConverter(IsoStr1, pattern1, pattern1, IsoChronology.INSTANCE,
                JapaneseChronology.INSTANCE).convert());
        assertEquals(JapaneseStr, new DateCalendarConverter(IsoStr1, pattern1, pattern, IsoChronology.INSTANCE,
                JapaneseChronology.INSTANCE).convert());

        assertEquals(MinguoStr1, new DateCalendarConverter(IsoStr, pattern, pattern1, IsoChronology.INSTANCE,
                MinguoChronology.INSTANCE).convert());
        assertEquals(MinguoStr1, new DateCalendarConverter(IsoStr1, pattern1, pattern1, IsoChronology.INSTANCE,
                MinguoChronology.INSTANCE).convert());
        assertEquals(MinguoStr, new DateCalendarConverter(IsoStr1, pattern1, pattern, IsoChronology.INSTANCE,
                MinguoChronology.INSTANCE).convert());

        assertEquals(ThaiBuddhistStr1, new DateCalendarConverter(IsoStr, pattern, pattern1, IsoChronology.INSTANCE,
                ThaiBuddhistChronology.INSTANCE).convert());
        assertEquals(ThaiBuddhistStr1, new DateCalendarConverter(IsoStr1, pattern1, pattern1, IsoChronology.INSTANCE,
                ThaiBuddhistChronology.INSTANCE).convert());
        assertEquals(ThaiBuddhistStr, new DateCalendarConverter(IsoStr1, pattern1, pattern, IsoChronology.INSTANCE,
                ThaiBuddhistChronology.INSTANCE).convert());
    }

    @Test
    public void TestConvertHijrahDateTo() {
        assertEquals(IsoStr, new DateCalendarConverter(HijrahStr, HijrahChronology.INSTANCE, IsoChronology.INSTANCE).convert());
        assertEquals(JapaneseStr,
                new DateCalendarConverter(HijrahStr, HijrahChronology.INSTANCE, JapaneseChronology.INSTANCE).convert());
        assertEquals(MinguoStr,
                new DateCalendarConverter(HijrahStr, HijrahChronology.INSTANCE, MinguoChronology.INSTANCE).convert());
        assertEquals(ThaiBuddhistStr, new DateCalendarConverter(HijrahStr, HijrahChronology.INSTANCE,
                ThaiBuddhistChronology.INSTANCE).convert());

        assertEquals(JapaneseStr1, new DateCalendarConverter(HijrahStr, pattern, pattern1, HijrahChronology.INSTANCE,
                JapaneseChronology.INSTANCE).convert());
        assertEquals(MinguoStr, new DateCalendarConverter(HijrahStr2, pattern6, pattern, HijrahChronology.INSTANCE,
                MinguoChronology.INSTANCE).convert());
        assertEquals(ThaiBuddhistStr5, new DateCalendarConverter(HijrahStr, pattern, pattern5, HijrahChronology.INSTANCE,
                ThaiBuddhistChronology.INSTANCE).convert());
    }

    @Test
    public void TestConvertJapaneseDateTo() {
        assertEquals(IsoStr,
                new DateCalendarConverter(JapaneseStr, JapaneseChronology.INSTANCE, IsoChronology.INSTANCE).convert());
        assertEquals(HijrahStr,
                new DateCalendarConverter(JapaneseStr, JapaneseChronology.INSTANCE, HijrahChronology.INSTANCE).convert());
        assertEquals(MinguoStr,
                new DateCalendarConverter(JapaneseStr, JapaneseChronology.INSTANCE, MinguoChronology.INSTANCE).convert());
        assertEquals(ThaiBuddhistStr, new DateCalendarConverter(JapaneseStr, JapaneseChronology.INSTANCE,
                ThaiBuddhistChronology.INSTANCE).convert());

        assertEquals(MinguoStr5, new DateCalendarConverter(JapaneseStr4, pattern4, pattern5, JapaneseChronology.INSTANCE,
                MinguoChronology.INSTANCE).convert());
        assertEquals(MinguoStr5, new DateCalendarConverter(JapaneseStr, pattern, pattern5, JapaneseChronology.INSTANCE,
                MinguoChronology.INSTANCE).convert());
    }

    @Test
    public void TestConvertMinguoDateTo() {
        assertEquals(IsoStr, new DateCalendarConverter(MinguoStr, MinguoChronology.INSTANCE, IsoChronology.INSTANCE).convert());
        assertEquals(HijrahStr,
                new DateCalendarConverter(MinguoStr, MinguoChronology.INSTANCE, HijrahChronology.INSTANCE).convert());
        assertEquals(JapaneseStr,
                new DateCalendarConverter(MinguoStr, MinguoChronology.INSTANCE, JapaneseChronology.INSTANCE).convert());
        assertEquals(ThaiBuddhistStr, new DateCalendarConverter(MinguoStr, MinguoChronology.INSTANCE,
                ThaiBuddhistChronology.INSTANCE).convert());

        assertEquals(JapaneseStr5, new DateCalendarConverter(MinguoStr, pattern, pattern5, MinguoChronology.INSTANCE,
                JapaneseChronology.INSTANCE).convert());
    }

    @Test
    public void TestConvertThaiBuddhistDateTo() {
        assertEquals(IsoStr,
                new DateCalendarConverter(ThaiBuddhistStr, ThaiBuddhistChronology.INSTANCE, IsoChronology.INSTANCE).convert());
        assertEquals(HijrahStr, new DateCalendarConverter(ThaiBuddhistStr, ThaiBuddhistChronology.INSTANCE,
                HijrahChronology.INSTANCE).convert());
        assertEquals(JapaneseStr, new DateCalendarConverter(ThaiBuddhistStr, ThaiBuddhistChronology.INSTANCE,
                JapaneseChronology.INSTANCE).convert());
        assertEquals(MinguoStr, new DateCalendarConverter(ThaiBuddhistStr, ThaiBuddhistChronology.INSTANCE,
                MinguoChronology.INSTANCE).convert());

        assertEquals(IsoStr5, new DateCalendarConverter(ThaiBuddhistStr, pattern, pattern5, ThaiBuddhistChronology.INSTANCE,
                IsoChronology.INSTANCE).convert());
    }

    @Test
    public void TestSpecialCases() {
        // test when the input is blank
        assertEquals("", new DateCalendarConverter("", HijrahChronology.INSTANCE, JapaneseChronology.INSTANCE).convert()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(" ", new DateCalendarConverter(" ", IsoChronology.INSTANCE, HijrahChronology.INSTANCE).convert()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(null, new DateCalendarConverter(null, HijrahChronology.INSTANCE, ThaiBuddhistChronology.INSTANCE).convert());

        // test when the input is not a date
        assertEquals("aa", new DateCalendarConverter("aa", HijrahChronology.INSTANCE, HijrahChronology.INSTANCE).convert()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", new DateCalendarConverter( //$NON-NLS-1$
                "aa", pattern1, pattern, HijrahChronology.INSTANCE, HijrahChronology.INSTANCE).convert()); //$NON-NLS-1$ 

        // test when the pattern is null
        assertEquals(IsoStr5, new DateCalendarConverter(ThaiBuddhistStr, null, pattern5, ThaiBuddhistChronology.INSTANCE,
                IsoChronology.INSTANCE).convert());
        assertEquals(IsoStr, new DateCalendarConverter(ThaiBuddhistStr, pattern, null, ThaiBuddhistChronology.INSTANCE,
                IsoChronology.INSTANCE).convert());
        assertEquals(IsoStr, new DateCalendarConverter(ThaiBuddhistStr, null, null, ThaiBuddhistChronology.INSTANCE,
                IsoChronology.INSTANCE).convert());
    }

    @Test
    public void TestParseStringToDate() {
        // convert an ISO-based date to a date in another chronology
        LocalDate date = LocalDate.of(2011, Month.AUGUST, 19);// LocalDate.from(jdate)
        // JapaneseDate jdate = JapaneseDate.from(date);
        // HijrahDate hdate = HijrahDate.from(date);
        // MinguoDate mdate = MinguoDate.from(date);
        // ThaiBuddhistDate tdate = ThaiBuddhistDate.from(date);

        LocalDate parseDateString = new DateCalendarConverter().parseStringToDate(
                "20110819", DateTimeFormatter.BASIC_ISO_DATE, null);// '20110819' //$NON-NLS-1$
        assertEquals(date, parseDateString);
        assertEquals(
                "20110819", new DateCalendarConverter().formatDateToString(parseDateString, null, DateTimeFormatter.BASIC_ISO_DATE)); //$NON-NLS-1$

        LocalDate parseDateString1 = new DateCalendarConverter().parseStringToDate(
                "2011-08-19", DateTimeFormatter.ISO_LOCAL_DATE, null); //$NON-NLS-1$
        assertEquals(date, parseDateString1);
        assertEquals("2011-08-19", new DateCalendarConverter().formatDateToString(parseDateString1, null)); //$NON-NLS-1$

        LocalDate parseDateString2 = new DateCalendarConverter().parseStringToDate(
                "2011 08 19", DateTimeFormatter.ofPattern(pattern6), //$NON-NLS-1$
                null);
        assertEquals(date, parseDateString2);
        assertEquals("2011 08 19", new DateCalendarConverter().formatDateToString(parseDateString2, null, pattern6)); //$NON-NLS-1$ 

        LocalDate parseDateString3 = new DateCalendarConverter().parseStringToDate("20110819", DateTimeFormatter.BASIC_ISO_DATE, //$NON-NLS-1$
                JapaneseChronology.INSTANCE);// '20110819'
        assertEquals(date, parseDateString3);
        assertEquals(
                "0023 08 19", new DateCalendarConverter().formatDateToString(parseDateString3, JapaneseChronology.INSTANCE, pattern6)); //$NON-NLS-1$ 

        LocalDate parseDateString4 = new DateCalendarConverter().parseStringToDate(
                "0023-08-19", pattern, JapaneseChronology.INSTANCE); //$NON-NLS-1$ 
        assertEquals(date, parseDateString4);
        assertEquals(
                "0023-08-19", new DateCalendarConverter().formatDateToString(parseDateString4, JapaneseChronology.INSTANCE, pattern)); //$NON-NLS-1$ 

        LocalDate parseDateString5 = new DateCalendarConverter().parseStringToDate(
                "2011 08 19", DateTimeFormatter.ofPattern(pattern6), //$NON-NLS-1$ 
                JapaneseChronology.INSTANCE);
        assertEquals(date, parseDateString5);
        assertEquals(
                "0023/08/19", new DateCalendarConverter().formatDateToString(parseDateString5, JapaneseChronology.INSTANCE, pattern1)); //$NON-NLS-1$ 
    }

    /**
     * measure the execution time of the current implementation with 100 000 dates to convert.
     */
    @Test
    public void TestMeasureTheExecutionTime() {
        Chronology[] chronologys = { IsoChronology.INSTANCE, HijrahChronology.INSTANCE, JapaneseChronology.INSTANCE,
                MinguoChronology.INSTANCE, ThaiBuddhistChronology.INSTANCE };

        for (Chronology sourceChronology : chronologys) {
            for (Chronology targetChronology : chronologys) {

                InputStream dateStream = this.getClass().getResourceAsStream("dateList.txt"); //$NON-NLS-1$
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(dateStream, "UTF-8")); //$NON-NLS-1$ //for Hindi language Double-byte type
                } catch (UnsupportedEncodingException e1) {
                    LOGGER.error(e1, e1);
                    Assert.fail(e1.getMessage());
                }
                try {
                    long startTime = System.currentTimeMillis();
                    String line;
                    while ((line = br.readLine()) != null) {
                        new DateCalendarConverter(line, "dd-MM-yyyy", pattern, sourceChronology, //$NON-NLS-1$
                                targetChronology).convert();
                    }
                    long endTime = System.currentTimeMillis();
                    System.out.println("the execution time of " + sourceChronology.getId() + "-->" + targetChronology.getId() //$NON-NLS-1$ //$NON-NLS-2$
                            + " ： " + (endTime - startTime) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
                } catch (FileNotFoundException e) {
                    LOGGER.error(e, e);
                    Assert.fail(e.getMessage());
                } catch (IOException e) {
                    LOGGER.error(e, e);
                    Assert.fail(e.getMessage());
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        LOGGER.error(e, e);
                        Assert.fail(e.getMessage());
                    }
                }
            }
        }

    }
}
