package org.talend.dataquality.statistics.numeric.histogram;

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
    public void testSetParameters() {
        HistogramStatistics hs = new HistogramStatistics();
        // normal case
        hs.setParameters(100d, 0d, 1);
        try {
            // exception case 1 : min = max
            hs.setParameters(100d, 100d, 1);
        } catch (DQStatisticsRuntimeException e) {
            Assert.assertEquals("max must be greater than min", e.getMessage());
        }
        try {
            // exception case 2 : min > max
            hs.setParameters(100d, 101d, 1);
        } catch (DQStatisticsRuntimeException e) {
            Assert.assertEquals("max must be greater than min", e.getMessage());
        }
        try {
            // exception case 3 : numBins = 0
            hs.setParameters(100d, 99d, 0);
        } catch (DQStatisticsRuntimeException e) {
            Assert.assertEquals("invalid numBins value :0 , numBins must be a none zero integer", e.getMessage());
        }
        try {
            // exception case 4 : numBins < 0
            hs.setParameters(100d, 99d, -100);
        } catch (DQStatisticsRuntimeException e) {
            Assert.assertEquals("invalid numBins value :-100 , numBins must be a none zero integer", e.getMessage());
        }
    }

    @Test
    public void testAdd() {
        // TODO implement it later
    }

    @Test
    public void testGetHistogram() {
        // TODO implement it later
    }

    @Test
    public void testIsComplete() {
        // TODO implement it later
    }

    @Test
    public void testGetCountBelowMin() {
        // TODO implement it later
    }

    @Test
    public void testGetCountAboveMax() {
        // TODO implement it later
    }

}
