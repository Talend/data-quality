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
public class CardinalityStatisticsTest {

    private static CardinalityStatistics cardStats;

    @Before
    public void setUp() throws Exception {
        cardStats = new CardinalityStatistics();
    }

    @Test(expected = ClassCastException.class)
    public void testDifferentTypeMerge() throws CardinalityMergeException {
        AbstractCardinalityStatistics stat1 = new CardinalityHLLStatistics();
        stat1.merge(cardStats);
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
        cardStats.merge(otherCardStat);
        Assert.assertEquals(cardStats.getDistinctCount(), otherCardStat.getDistinctCount());
    }
}