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
package org.talend.dataquality.libraries.converters;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.HijrahChronology;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.MinguoChronology;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

/**
 * Test for class {@link ConvertDateUtils}.
 * 
 * @author msjian
 * @version 2017.02.08
 */
public class ConvertDateUtilsTest {

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
        assertEquals(HijrahStr, ConvertDateUtils.convertDate(IsoStr, IsoChronology.INSTANCE, HijrahChronology.INSTANCE));
        assertEquals(JapaneseStr, ConvertDateUtils.convertDate(IsoStr, IsoChronology.INSTANCE, JapaneseChronology.INSTANCE));
        assertEquals(MinguoStr, ConvertDateUtils.convertDate(IsoStr, IsoChronology.INSTANCE, MinguoChronology.INSTANCE));
        assertEquals(ThaiBuddhistStr,
                ConvertDateUtils.convertDate(IsoStr, IsoChronology.INSTANCE, ThaiBuddhistChronology.INSTANCE));

        assertEquals(IsoStr2,
                ConvertDateUtils.convertDate(IsoStr, pattern, pattern2, IsoChronology.INSTANCE, IsoChronology.INSTANCE));
        assertEquals(IsoStr3,
                ConvertDateUtils.convertDate(IsoStr, pattern, pattern3, IsoChronology.INSTANCE, IsoChronology.INSTANCE));
        assertEquals(IsoStr4,
                ConvertDateUtils.convertDate(IsoStr, pattern, pattern4, IsoChronology.INSTANCE, IsoChronology.INSTANCE));
        assertEquals(IsoStr5,
                ConvertDateUtils.convertDate(IsoStr, pattern, pattern5, IsoChronology.INSTANCE, IsoChronology.INSTANCE));

        assertEquals(HijrahStr1,
                ConvertDateUtils.convertDate(IsoStr, pattern, pattern1, IsoChronology.INSTANCE, HijrahChronology.INSTANCE));
        assertEquals(HijrahStr1,
                ConvertDateUtils.convertDate(IsoStr1, pattern1, pattern1, IsoChronology.INSTANCE, HijrahChronology.INSTANCE));
        assertEquals(HijrahStr,
                ConvertDateUtils.convertDate(IsoStr1, pattern1, pattern, IsoChronology.INSTANCE, HijrahChronology.INSTANCE));
        assertEquals(HijrahStr5,
                ConvertDateUtils.convertDate(IsoStr1, pattern1, pattern5, IsoChronology.INSTANCE, HijrahChronology.INSTANCE));

        assertEquals(JapaneseStr1,
                ConvertDateUtils.convertDate(IsoStr, pattern, pattern1, IsoChronology.INSTANCE, JapaneseChronology.INSTANCE));
        assertEquals(JapaneseStr1,
                ConvertDateUtils.convertDate(IsoStr1, pattern1, pattern1, IsoChronology.INSTANCE, JapaneseChronology.INSTANCE));
        assertEquals(JapaneseStr,
                ConvertDateUtils.convertDate(IsoStr1, pattern1, pattern, IsoChronology.INSTANCE, JapaneseChronology.INSTANCE));

        assertEquals(MinguoStr1,
                ConvertDateUtils.convertDate(IsoStr, pattern, pattern1, IsoChronology.INSTANCE, MinguoChronology.INSTANCE));
        assertEquals(MinguoStr1,
                ConvertDateUtils.convertDate(IsoStr1, pattern1, pattern1, IsoChronology.INSTANCE, MinguoChronology.INSTANCE));
        assertEquals(MinguoStr,
                ConvertDateUtils.convertDate(IsoStr1, pattern1, pattern, IsoChronology.INSTANCE, MinguoChronology.INSTANCE));

        assertEquals(ThaiBuddhistStr1,
                ConvertDateUtils.convertDate(IsoStr, pattern, pattern1, IsoChronology.INSTANCE, ThaiBuddhistChronology.INSTANCE));
        assertEquals(ThaiBuddhistStr1, ConvertDateUtils.convertDate(IsoStr1, pattern1, pattern1, IsoChronology.INSTANCE,
                ThaiBuddhistChronology.INSTANCE));
        assertEquals(ThaiBuddhistStr,
                ConvertDateUtils.convertDate(IsoStr1, pattern1, pattern, IsoChronology.INSTANCE, ThaiBuddhistChronology.INSTANCE));
    }

    @Test
    public void TestConvertHijrahDateTo() {
        assertEquals(IsoStr, ConvertDateUtils.convertDate(HijrahStr, HijrahChronology.INSTANCE, IsoChronology.INSTANCE));
        assertEquals(JapaneseStr, ConvertDateUtils.convertDate(HijrahStr, HijrahChronology.INSTANCE, JapaneseChronology.INSTANCE));
        assertEquals(MinguoStr, ConvertDateUtils.convertDate(HijrahStr, HijrahChronology.INSTANCE, MinguoChronology.INSTANCE));
        assertEquals(ThaiBuddhistStr,
                ConvertDateUtils.convertDate(HijrahStr, HijrahChronology.INSTANCE, ThaiBuddhistChronology.INSTANCE));

        assertEquals(JapaneseStr1, ConvertDateUtils.convertDate(HijrahStr, pattern, pattern1, HijrahChronology.INSTANCE,
                JapaneseChronology.INSTANCE));
        assertEquals(MinguoStr,
                ConvertDateUtils.convertDate(HijrahStr2, pattern6, pattern, HijrahChronology.INSTANCE, MinguoChronology.INSTANCE));
        assertEquals(ThaiBuddhistStr5, ConvertDateUtils.convertDate(HijrahStr, pattern, pattern5, HijrahChronology.INSTANCE,
                ThaiBuddhistChronology.INSTANCE));

    }

    @Test
    public void TestConvertJapaneseDateTo() {
        assertEquals(IsoStr, ConvertDateUtils.convertDate(JapaneseStr, JapaneseChronology.INSTANCE, IsoChronology.INSTANCE));
        assertEquals(HijrahStr, ConvertDateUtils.convertDate(JapaneseStr, JapaneseChronology.INSTANCE, HijrahChronology.INSTANCE));
        assertEquals(MinguoStr, ConvertDateUtils.convertDate(JapaneseStr, JapaneseChronology.INSTANCE, MinguoChronology.INSTANCE));
        assertEquals(ThaiBuddhistStr,
                ConvertDateUtils.convertDate(JapaneseStr, JapaneseChronology.INSTANCE, ThaiBuddhistChronology.INSTANCE));

        assertEquals(MinguoStr5, ConvertDateUtils.convertDate(JapaneseStr4, pattern4, pattern5, JapaneseChronology.INSTANCE,
                MinguoChronology.INSTANCE));
        assertEquals(MinguoStr5, ConvertDateUtils.convertDate(JapaneseStr, pattern, pattern5, JapaneseChronology.INSTANCE,
                MinguoChronology.INSTANCE));
    }

    @Test
    public void TestConvertMinguoDateTo() {
        assertEquals(IsoStr, ConvertDateUtils.convertDate(MinguoStr, MinguoChronology.INSTANCE, IsoChronology.INSTANCE));
        assertEquals(HijrahStr, ConvertDateUtils.convertDate(MinguoStr, MinguoChronology.INSTANCE, HijrahChronology.INSTANCE));
        assertEquals(JapaneseStr, ConvertDateUtils.convertDate(MinguoStr, MinguoChronology.INSTANCE, JapaneseChronology.INSTANCE));
        assertEquals(ThaiBuddhistStr,
                ConvertDateUtils.convertDate(MinguoStr, MinguoChronology.INSTANCE, ThaiBuddhistChronology.INSTANCE));

        assertEquals(JapaneseStr5, ConvertDateUtils.convertDate(MinguoStr, pattern, pattern5, MinguoChronology.INSTANCE,
                JapaneseChronology.INSTANCE));
    }

    @Test
    public void TestConvertThaiBuddhistDateTo() {
        assertEquals(IsoStr,
                ConvertDateUtils.convertDate(ThaiBuddhistStr, ThaiBuddhistChronology.INSTANCE, IsoChronology.INSTANCE));
        assertEquals(HijrahStr,
                ConvertDateUtils.convertDate(ThaiBuddhistStr, ThaiBuddhistChronology.INSTANCE, HijrahChronology.INSTANCE));
        assertEquals(JapaneseStr,
                ConvertDateUtils.convertDate(ThaiBuddhistStr, ThaiBuddhistChronology.INSTANCE, JapaneseChronology.INSTANCE));
        assertEquals(MinguoStr,
                ConvertDateUtils.convertDate(ThaiBuddhistStr, ThaiBuddhistChronology.INSTANCE, MinguoChronology.INSTANCE));

        assertEquals(IsoStr5, ConvertDateUtils.convertDate(ThaiBuddhistStr, pattern, pattern5, ThaiBuddhistChronology.INSTANCE,
                IsoChronology.INSTANCE));
    }

    @Test
    public void TestSpecialCases() {
        // test when the input is blank
        assertEquals("", ConvertDateUtils.convertDate("", HijrahChronology.INSTANCE, JapaneseChronology.INSTANCE)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(" ", ConvertDateUtils.convertDate(" ", IsoChronology.INSTANCE, HijrahChronology.INSTANCE)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(null, ConvertDateUtils.convertDate(null, HijrahChronology.INSTANCE, ThaiBuddhistChronology.INSTANCE));

        // test when the input is not a date
        assertEquals("aa", ConvertDateUtils.convertDate("aa", HijrahChronology.INSTANCE, HijrahChronology.INSTANCE)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", ConvertDateUtils.convertDate( //$NON-NLS-1$
                "aa", pattern1, pattern, HijrahChronology.INSTANCE, HijrahChronology.INSTANCE)); //$NON-NLS-1$ 

        // test when the pattern is null
        assertEquals(IsoStr5, ConvertDateUtils.convertDate(ThaiBuddhistStr, null, pattern5, ThaiBuddhistChronology.INSTANCE,
                IsoChronology.INSTANCE));
        assertEquals(IsoStr, ConvertDateUtils.convertDate(ThaiBuddhistStr, pattern, null, ThaiBuddhistChronology.INSTANCE,
                IsoChronology.INSTANCE));
        assertEquals(IsoStr, ConvertDateUtils.convertDate(ThaiBuddhistStr, null, null, ThaiBuddhistChronology.INSTANCE,
                IsoChronology.INSTANCE));
    }

    @Test
    public void TestParseStringToDate() {
        // convert an ISO-based date to a date in another chronology
        LocalDate date = LocalDate.of(2011, Month.AUGUST, 19);// LocalDate.from(jdate)
        // JapaneseDate jdate = JapaneseDate.from(date);
        // HijrahDate hdate = HijrahDate.from(date);
        // MinguoDate mdate = MinguoDate.from(date);
        // ThaiBuddhistDate tdate = ThaiBuddhistDate.from(date);

        LocalDate parseDateString = ConvertDateUtils.parseStringToDate("20110819", DateTimeFormatter.BASIC_ISO_DATE, null);// '20110819' //$NON-NLS-1$
        assertEquals(date, parseDateString);
        assertEquals("20110819", ConvertDateUtils.formatDateToString(parseDateString, null, DateTimeFormatter.BASIC_ISO_DATE)); //$NON-NLS-1$

        LocalDate parseDateString1 = ConvertDateUtils.parseStringToDate("2011-08-19", DateTimeFormatter.ISO_LOCAL_DATE, null); //$NON-NLS-1$
        assertEquals(date, parseDateString1);
        assertEquals("2011-08-19", ConvertDateUtils.formatDateToString(parseDateString1, null)); //$NON-NLS-1$

        LocalDate parseDateString2 = ConvertDateUtils
                .parseStringToDate("2011 08 19", DateTimeFormatter.ofPattern(pattern6), null); //$NON-NLS-1$ 
        assertEquals(date, parseDateString2);
        assertEquals("2011 08 19", ConvertDateUtils.formatDateToString(parseDateString2, null, pattern6)); //$NON-NLS-1$ 

        LocalDate parseDateString3 = ConvertDateUtils.parseStringToDate(
                "20110819", DateTimeFormatter.BASIC_ISO_DATE, JapaneseChronology.INSTANCE);// '20110819' //$NON-NLS-1$
        assertEquals(date, parseDateString3);
        assertEquals("0023 08 19", ConvertDateUtils.formatDateToString(parseDateString3, JapaneseChronology.INSTANCE, pattern6)); //$NON-NLS-1$ 

        LocalDate parseDateString4 = ConvertDateUtils.parseStringToDate("0023-08-19", pattern, JapaneseChronology.INSTANCE); //$NON-NLS-1$ 
        assertEquals(date, parseDateString4);
        assertEquals("0023-08-19", ConvertDateUtils.formatDateToString(parseDateString4, JapaneseChronology.INSTANCE, pattern)); //$NON-NLS-1$ 

        LocalDate parseDateString5 = ConvertDateUtils.parseStringToDate("2011 08 19", DateTimeFormatter.ofPattern(pattern6), //$NON-NLS-1$ 
                JapaneseChronology.INSTANCE);
        assertEquals(date, parseDateString5);
        assertEquals("0023/08/19", ConvertDateUtils.formatDateToString(parseDateString5, JapaneseChronology.INSTANCE, pattern1)); //$NON-NLS-1$ 
    }
}
