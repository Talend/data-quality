package org.talend.dataquality.matchmerge.mfb;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

public class MatchResultTest {

    @Test
    public void testGetMatchingWeightRecordResultWithSameScoreAndThreshold() {
        MatchResult matchingResult = new MatchResult(1);
        matchingResult.setThreshold(0, 0.8f);
        matchingResult.setScore(0, AttributeMatcherType.EXACT, 0.8d, "recordId1", "value1", "recordId2", "value2");
        Assert.assertTrue(matchingResult.isMatch());

        matchingResult.setThreshold(0, 0.799f);
        matchingResult.setScore(0, AttributeMatcherType.EXACT, 0.799d, "recordId1", "value1", "recordId2", "value2");
        Assert.assertTrue(matchingResult.isMatch());

        matchingResult.setThreshold(0, 0.799f);
        matchingResult.setScore(0, AttributeMatcherType.EXACT, 0.798d, "recordId1", "value1", "recordId2", "value2");
        Assert.assertFalse(matchingResult.isMatch());

        matchingResult.setThreshold(0, 0.798f);
        matchingResult.setScore(0, AttributeMatcherType.EXACT, 0.799d, "recordId1", "value1", "recordId2", "value2");
        Assert.assertTrue(matchingResult.isMatch());
    }
}
