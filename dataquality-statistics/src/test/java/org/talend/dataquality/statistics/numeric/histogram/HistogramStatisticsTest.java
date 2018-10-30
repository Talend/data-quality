package org.talend.dataquality.statistics.numeric.histogram;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.statistics.exception.DQStatisticsRuntimeException;

public class HistogramStatisticsTest {

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
}
