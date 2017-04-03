package org.talend.dataquality.statistics.cardinality;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.clearspring.analytics.stream.cardinality.HyperLogLog;

/**
 * Created by afournier on 31/03/17.
 */
public class CardinalityStatisticsTest {

    private static CardinalityStatistics cardStats;

    @Before
    public void setUp() throws Exception {
        cardStats = new CardinalityStatistics();
    }

    @Test
    public void testUnpossibleMerge() {
        CardinalityHLLStatistics cardHLL = new CardinalityHLLStatistics();
        cardHLL.setHyperLogLog(new HyperLogLog(20));

        for (int i = 0; i < 1000; i++) {
            cardStats.incrementCount();
            cardHLL.incrementCount();
            String str = RandomStringUtils.randomAscii(2);
            cardStats.add(str);
            cardHLL.getHyperLogLog().offer(str);
        }

        Assert.assertEquals(false, cardStats.merge(cardHLL));
    }

    @Test
    public void testPossibleMerge() {
        CardinalityStatistics otherCardStat = new CardinalityStatistics();
        for (int i = 0; i < 1000; i++) {
            cardStats.incrementCount();
            otherCardStat.incrementCount();
            String str = RandomStringUtils.randomAscii(2);
            cardStats.add(str);
            otherCardStat.add(str);
        }

        Assert.assertEquals(true, cardStats.merge(otherCardStat));
        // The merge should not have changed the cardinality because every element was the same for both in the loop.
        Assert.assertEquals(cardStats.getDistinctCount(), otherCardStat.getDistinctCount());
    }
}