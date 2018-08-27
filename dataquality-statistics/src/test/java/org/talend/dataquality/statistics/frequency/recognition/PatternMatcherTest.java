package org.talend.dataquality.statistics.frequency.recognition;

import org.junit.Assert;
import org.junit.Test;

public class PatternMatcherTest {

    @Test
    public void matcherPatternDateDayFirst() {
        Assert.assertTrue(PatternMatcher.matchCharacterPattern("08/08/2018", "dd/MM/yyyy"));
    }

    @Test
    public void matcherPatternDateMonthFirst() {
        Assert.assertTrue(PatternMatcher.matchCharacterPattern("08/08/2018", "MM/dd/yyyy"));
    }

    @Test
    public void matcherPatternDate() {
        Assert.assertFalse(PatternMatcher.matchCharacterPattern("08/08/2018", "yyyy/MM/dd"));
    }

    @Test
    public void matcherPatternName() {
        Assert.assertTrue(PatternMatcher.matchCharacterPattern("Toronto", "Aaaaaaa"));
    }

    @Test
    public void matcherPatternEmpty() {
        Assert.assertTrue(PatternMatcher.matchCharacterPattern("", ""));
    }

    @Test
    public void matcherPatternNull() {
        Assert.assertFalse(PatternMatcher.matchCharacterPattern(null, "whatEver"));
    }

    @Test
    public void matcherPatternNameWithNumber() {
        Assert.assertTrue(PatternMatcher.matchCharacterPattern("Toronto1234", "Aaaaaaa9999"));
    }

    @Test
    public void matcherPatternWordNotSensitive() {
        Assert.assertTrue(PatternMatcher.matchWordPattern("Toronto1234", "[alnum]"));
    }

    @Test
    public void matcherPatternWordSensitive() {
        Assert.assertTrue(PatternMatcher.matchWordPattern("Toronto1234", "[alnum]", true));
    }

    @Test
    public void matcherPatternEmail() {
        Assert.assertTrue(PatternMatcher.matchWordPattern("user.lastname@talend.com", "[word].[word]@[word].[word]"));
    }

    @Test
    public void matcherPatternSensitive() {
        Assert.assertTrue(PatternMatcher.matchWordPattern("user.Lastname", "[word].[Word]", true));
    }

}
