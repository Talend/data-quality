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
package org.talend.dataquality.datamasking.functions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class DateVarianceTest {

    private DateVariance dv = null;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //$NON-NLS-1$

    private Date input = null;

    @Before
    public void setUp() throws Exception {
        input = sdf.parse("29-02-1992"); //$NON-NLS-1$
        dv = new DateVariance();
        dv.setRandom(new Random(42L));
    }

    @Test
    public void testGoodCase1() throws ParseException {
        dv.parse("31", false); //$NON-NLS-1$
        for (int index = 0; index < 20; index++) {
            String output = sdf.format(dv.generateMaskedRow(input));
            boolean result = checkResult(output, "29-01-1992", "31-03-1992"); //$NON-NLS-1$ //$NON-NLS-2$
            Assert.assertTrue("result date should between 29-01-1992 and 31-03-1992 but it is " + output, result); //$NON-NLS-1$
        }
    }

    @Test
    public void testGoodCase2() throws ParseException {
        dv.parse("-31", false); //$NON-NLS-1$
        for (int index = 0; index < 20; index++) {
            String output = sdf.format(dv.generateMaskedRow(input));
            boolean result = checkResult(output, "29-01-1992", "31-03-1992"); //$NON-NLS-1$ //$NON-NLS-2$
            Assert.assertTrue("result date should between 29-01-1992 and 31-03-1992 but it is " + output, result); //$NON-NLS-1$
        }
    }

    @Test
    public void testGoodCase3() throws ParseException {
        dv.parse("1", false); //$NON-NLS-1$
        for (int index = 0; index < 20; index++) {
            String output = sdf.format(dv.generateMaskedRow(input));
            boolean result = checkResult(output, "28-01-1992", "01-03-1992"); //$NON-NLS-1$ //$NON-NLS-2$
            Assert.assertTrue("result date should between 28-01-1992 and 01-03-1992 but it is " + output, result); //$NON-NLS-1$
        }
    }

    @Test
    public void testGoodCase4() throws ParseException {
        dv.parse("1,3", false); //$NON-NLS-1$
        for (int index = 0; index < 20; index++) {
            String output = sdf.format(dv.generateMaskedRow(input));
            boolean result = checkResult(output, "29-01-1992", "31-03-1992"); //$NON-NLS-1$ //$NON-NLS-2$
            Assert.assertTrue("result date should between 29-01-1992 and 31-03-1992 but it is " + output, result); //$NON-NLS-1$
        }
    }

    @Test
    public void testDummyGood() throws ParseException {
        dv.parse("0", false); //$NON-NLS-1$
        for (int index = 0; index < 20; index++) {
            String output = sdf.format(dv.generateMaskedRow(input));
            boolean result = checkResult(output, "29-01-1992", "31-03-1992"); //$NON-NLS-1$ //$NON-NLS-2$
            Assert.assertTrue("result date should between 29-01-1992 and 31-03-1992 but it is " + output, result); //$NON-NLS-1$
        }
    }

    @Test
    public void testNullParameter() throws ParseException {
        dv.parse(null, false); // $NON-NLS-1$
        for (int index = 0; index < 20; index++) {
            String output = sdf.format(dv.generateMaskedRow(input));
            boolean result = checkResult(output, "29-01-1992", "31-03-1992"); //$NON-NLS-1$ //$NON-NLS-2$
            Assert.assertTrue("result date should between 29-01-1992 and 31-03-1992 but it is " + output, result); //$NON-NLS-1$
        }
    }

    @Test
    public void testBad() throws ParseException {
        dv.parse("j", false); //$NON-NLS-1$
        for (int index = 0; index < 20; index++) {
            String output = sdf.format(dv.generateMaskedRow(input));
            boolean result = checkResult(output, "29-01-1992", "31-03-1992"); //$NON-NLS-1$ //$NON-NLS-2$
            Assert.assertTrue("result date should between 29-01-1992 and 31-03-1992 but it is " + output, result); //$NON-NLS-1$
        }
    }

    /**
     * DOC talend Comment method "checkResult".
     * 
     * @param output
     * @return
     * @throws ParseException
     */
    private boolean checkResult(String output, String min, String max) throws ParseException {
        long currentTime = sdf.parse(output).getTime();
        long minTime = sdf.parse(min).getTime();
        long maxTime = sdf.parse(max).getTime();
        return minTime <= currentTime && currentTime <= maxTime;
    }
}
