package org.talend.dataquality.statistics.cardinality;

import com.clearspring.analytics.stream.cardinality.CardinalityMergeException;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.clearspring.analytics.stream.cardinality.HyperLogLog;

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

    @Test(expected = CardinalityMergeException.class)
    public void testDifferentHLLMerge() throws CardinalityMergeException {

        CardinalityHLLStatistics cardStat = new CardinalityHLLStatistics();
        cardStat.setHyperLogLog(new HyperLogLog(28));
        for (int i = 0; i < 1000; i++) {
            cardHLLStats.incrementCount();
            cardStat.incrementCount();
            String str = RandomStringUtils.randomAscii(2);
            cardHLLStats.getHyperLogLog().offer(str);
            cardStat.add(str);
        }

        cardHLLStats.merge(cardStat);
        Assert.assertEquals(cardStat.getDistinctCount(), cardHLLStats.getDistinctCount());

    }

    @Test(expected = ClassCastException.class)
    public void testDifferentTypeMerge() throws CardinalityMergeException {
        AbstractCardinalityStatistics stat1 = new CardinalityStatistics();
        stat1.merge(cardHLLStats);
    }

    @Test
    public void testPossibleMerge() throws CardinalityMergeException {
        CardinalityHLLStatistics otherCardHLLStat = new CardinalityHLLStatistics();
        otherCardHLLStat.setHyperLogLog(new HyperLogLog(20));
        for (int i = 0; i < 1000; i++) {
            cardHLLStats.incrementCount();
            otherCardHLLStat.incrementCount();
            String str = RandomStringUtils.randomAscii(2);
            cardHLLStats.getHyperLogLog().offer(str);
            otherCardHLLStat.getHyperLogLog().offer(str);
        }
        cardHLLStats.merge(otherCardHLLStat);
        Assert.assertEquals(cardHLLStats.getDistinctCount(), otherCardHLLStat.getDistinctCount());
    }

}