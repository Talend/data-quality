package org.talend.dataquality.statistics.numeric.histogram;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RangeTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSetLowerwithOnedecimal() {
        Range r = new Range(0.0, 0.0);
        r.setLower(10.0);
        Assert.assertEquals(10.0, r.getLower(), 1);
    }

    @Test
    public void testSetLowerwithMoredecimal() {
        Range r = new Range(0.0, 10.0);
        r.setLower(100.00000);
        Assert.assertEquals(100.00000, r.getLower(), 5);
    }

    @Test
    public void testSetLowerwithMin() {
        Range r = new Range(0.0, 0.0);
        r.setLower(Double.MIN_VALUE);
        Assert.assertEquals(4.9E-324, r.getLower(), 6);
    }

    @Test
    public void testSetLowerwithMax() {
        Range r = new Range(0.0, 0.0);
        r.setLower(Double.MAX_VALUE);
        Assert.assertEquals(1.7976931348623157E308, r.getLower(), 20);
    }

    @Test
    public void testSetLowerwithPositiveInfinity() {
        Range r = new Range(0.0, 0.0);
        r.setLower(Double.POSITIVE_INFINITY);
        Assert.assertEquals(Double.POSITIVE_INFINITY, r.getLower(), 1000);
    }

    @Test
    public void testSetLowerwithNegativeInfinity() {
        Range r = new Range(0.0, 0.0);
        r.setLower(Double.NEGATIVE_INFINITY);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, r.getLower(), 1000);
    }

    @Test
    public void testSetUpperWithOneDecimal() {
        Range r = new Range(0.0, 10.0);
        r.setUpper(109.8);
        Assert.assertEquals(109.8, r.getUpper(), 1);
    }

    @Test
    public void testSetUpperWithMoreDecimal() {
        Range r = new Range(0.0, 10.0);
        r.setUpper(-0.856789);
        Assert.assertEquals(-0.856789, r.getUpper(), 6);
    }

    @Test
    public void testSetUpperWithPositiveInfinity() {
        Range r = new Range(0.0, 10.0);
        r.setUpper(Double.POSITIVE_INFINITY);
        Assert.assertEquals(Double.POSITIVE_INFINITY, r.getUpper(), 2000);
    }

    @Test
    public void testSetUpperWithNegativeInfinity() {
        Range r = new Range(0.0, 10.0);
        r.setUpper(Double.NEGATIVE_INFINITY);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, r.getUpper(), 2000);
    }

    @Test
    public void testSetUpperWithMax() {
        Range r = new Range(0.0, 10.0);
        r.setUpper(Double.MAX_VALUE);
        Assert.assertEquals(1.7976931348623157E308, r.getUpper(), 20);
    }

    @Test
    public void testSetUpperWithMin() {
        Range r = new Range(0.0, 10.0);
        r.setUpper(Double.MIN_VALUE);
        Assert.assertEquals(4.9E-324, r.getUpper(), 20);
    }

    @Test
    public void testCompareTo() {
        Range r = new Range(8.0, 9.0);
        Range r1 = new Range(1.0, 9.0);
        if (r.getLower() > r1.getLower()) {
            r.compareTo(r1);
        }
    }

    public void testCompareTo1() {
        Range r = new Range(8.0, 9.0);
        Range r1 = new Range(1.0, 9.0);
        if (r.getLower() < r1.getLower()) {
            r.compareTo(r1);
        }
    }

    @Test
    public void testEqualsObject() {
        Range r = new Range(10.0, 10.0);
        Range r1 = new Range(10.0, 10.0);
        Assert.assertTrue(r.equals(r1));
    }

    @Test
    public void testEqualsObject2WithNull() {
        Range r = new Range(10.0, 10.0);
        Range r1 = null;
        Assert.assertFalse(r.equals(r1));
    }

    @Test
    public void testEqualsObjectWithTwodifferentData() {
        Range r = new Range(10.0, 10.0);
        Range r1 = new Range(0.0, 0.0);
        Assert.assertFalse(r.equals(r1));
    }

    @Test
    public void testEqualsObjectWithonediferentData() {
        Range r = new Range(10.0, 10.0);
        Range r1 = new Range(0.0, 10.0);
        Assert.assertFalse(r.equals(r1));
    }
}
