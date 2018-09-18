package org.talend.dataquality.statistics.frequency.recognition;

import org.junit.Assert;
import org.junit.Test;

public class PatternMatcherTest {

    @Test
    public void matcherPatternDateDayFirst() {
        Assert.assertTrue(PatternMatcher.matchCharDatePattern("08/08/2018", "dd/MM/yyyy"));
    }

    @Test
    public void matcherPatternDateMonthFirst() {
        String input = "08/08/2018";
        Assert.assertFalse(PatternMatcher.matchCharDatePattern(input, "99/99/9999"));
        Assert.assertFalse(PatternMatcher.matchCharDatePattern(input, "yyyy/MM/dd"));
        Assert.assertTrue(PatternMatcher.matchCharDatePattern(input, "MM/dd/yyyy"));
    }

    @Test
    public void matcherPatternNotDate() {
        Assert.assertTrue(PatternMatcher.matchCharDatePattern("32/13/2018", "99/99/9999"));
        Assert.assertFalse(PatternMatcher.matchCharDatePattern("32/13/2018", "dd/MM/yyyy"));
    }

    @Test
    public void matcherPatternName() {
        Assert.assertTrue(PatternMatcher.matchCharDatePattern("Toronto", "Aaaaaaa"));
    }

    @Test
    public void matcherPatternEmpty() {
        Assert.assertTrue(PatternMatcher.matchCharDatePattern("", ""));
        Assert.assertFalse(PatternMatcher.matchCharDatePattern("", "9"));

    }

    @Test
    public void matcherPatternNull() {
        Assert.assertFalse(PatternMatcher.matchCharDatePattern(null, "whatEver"));
    }

    @Test
    public void matcherPatternNameWithNumber() {
        Assert.assertTrue(PatternMatcher.matchCharDatePattern("Toronto1234", "Aaaaaaa9999"));
    }

    @Test
    public void matcherPatternWordNotSensitive() {
        Assert.assertTrue(PatternMatcher.matchWordPattern("Toronto1234", "[alnum]", false));
    }

    @Test
    public void matcherPatternWordSensitive() {
        Assert.assertTrue(PatternMatcher.matchWordPattern("Toronto1234", "[Word][number]"));
    }

    @Test
    public void matcherPatternEmail() {
        Assert.assertTrue(PatternMatcher.matchWordPattern("user.lastname@talend.com", "[word].[word]@[word].[word]", false));
    }

    @Test
    public void matcherPatternSensitive() {
        Assert.assertTrue(PatternMatcher.matchWordPattern("user.Lastname", "[word].[Word]"));
    }

}
