package org.talend.dataquality.statistics.cardinality;

import com.clearspring.analytics.stream.cardinality.HyperLogLog;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by afournier on 31/03/17.
 */
public class CardinalityHLLStatisticsTest {

    private static CardinalityHLLStatistics cardHLLStats;

    @Before
    public void setUp() throws Exception {
        cardHLLStats = new CardinalityHLLStatistics();
        cardHLLStats.setHyperLogLog(new HyperLogLog(20));
    }

    @Test
    public void testUnpossibleMerge() {
        CardinalityStatistics cardStat = new CardinalityStatistics();
        for (int i = 0; i < 1000; i++) {
            cardHLLStats.incrementCount();
            cardStat.incrementCount();
            String str = RandomStringUtils.randomAscii(2);
            cardHLLStats.getHyperLogLog().offer(str);
            cardStat.add(str);
        }

        Assert.assertEquals(false, cardHLLStats.merge(cardStat));
    }

    @Test
    public void testPossibleMerge() {
        CardinalityHLLStatistics otherCardHLLStat = new CardinalityHLLStatistics();
        otherCardHLLStat.setHyperLogLog(new HyperLogLog(20));
        for (int i = 0; i < 1000; i++) {
            cardHLLStats.incrementCount();
            otherCardHLLStat.incrementCount();
            String str = RandomStringUtils.randomAscii(2);
            cardHLLStats.getHyperLogLog().offer(str);
            otherCardHLLStat.getHyperLogLog().offer(str);
        }

        Assert.assertEquals(true, cardHLLStats.merge(otherCardHLLStat));
        // The merge should not have changed the cardinality because every element was the same for both in the loop.
        Assert.assertEquals(cardHLLStats.getDistinctCount(), otherCardHLLStat.getDistinctCount());
    }

}