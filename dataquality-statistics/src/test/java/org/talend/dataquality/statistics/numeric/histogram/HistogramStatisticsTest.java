package org.talend.dataquality.statistics.numeric.histogram;

import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.statistics.exception.DQStatisticsRuntimeException;

public class HistogramStatisticsTest {

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

    @Test
    public void testSetParameters1() {
        HistogramStatistics hs = new HistogramStatistics();
        try {
            // exception case 1 : min = max
            hs.setParameters(100d, 100d, 1);
        } catch (DQStatisticsRuntimeException e) {
            Assert.assertEquals("max must be greater than min", e.getMessage());
        }
    }

    @Test
    public void testSetParameters2() {
        HistogramStatistics hs = new HistogramStatistics();
        try {
            // exception case 2 : min > max
            hs.setParameters(100d, 101d, 1);
        } catch (DQStatisticsRuntimeException e) {
            Assert.assertEquals("max must be greater than min", e.getMessage());
        }
    }

    @Test
    public void testSetParameters3() {
        HistogramStatistics hs = new HistogramStatistics();
        try {
            // exception case 3 : numBins = 0
            hs.setParameters(100d, 99d, 0);
        } catch (DQStatisticsRuntimeException e) {
            Assert.assertEquals("invalid numBins value :0 , numBins must be a none zero integer", e.getMessage());
        }
    }

    @Test
    public void testSetParameters4() {
        HistogramStatistics hs = new HistogramStatistics();
        try {
            // exception case 4 : numBins < 0
            hs.setParameters(100d, 99d, -100);
        } catch (DQStatisticsRuntimeException e) {
            Assert.assertEquals("invalid numBins value :-100 , numBins must be a none zero integer", e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSetParameters5() {
        HistogramStatistics hs = new HistogramStatistics();
        // Normal case 5 : numBins < 0
        hs.setParameters(100d, 98d, 1);

        Map<Range, Long> histogram = hs.getHistogram();
        for (Range r : histogram.keySet()) {
            Assert.assertEquals("min value:98d", 98.0, r.getLower(), 0.1);
            Assert.assertEquals("Max value is 100d", 100.0, r.getUpper(), 0.1);
        }
        for (Long lo : histogram.values()) {
            Assert.assertEquals("value is 0", 0.0, lo, 0.1);
        }

    }

    @Test
    public void testAdd1() {
        HistogramStatistics hs = new HistogramStatistics();
        hs.setParameters(100d, 98d, 2);

        // The added  data is smaller than min
        hs.add(1d);
        Assert.assertEquals("1", 1, hs.getCountBelowMin(), 0.1);

        // The added  data is bigger than max
        hs.add(101d);
        Assert.assertEquals("1", 1, hs.getCountAboveMax(), 0.1);
    }

    @Test
    public void testAdd2() {
        HistogramStatistics hs = new HistogramStatistics();
        hs.setParameters(100d, 98d, 2);

        //The added data is smaller than min, and bigger than max
        hs.add(99d);
        Assert.assertEquals("0", 0, hs.getCountBelowMin(), 0.1);
        Assert.assertEquals("0", 0, hs.getCountAboveMax(), 0.1);
    }
}
