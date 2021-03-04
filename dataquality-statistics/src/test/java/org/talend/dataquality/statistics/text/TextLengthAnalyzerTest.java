// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.statistics.text;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TextLengthAnalyzerTest {

    TextLengthAnalyzer analyzer = new TextLengthAnalyzer();

    @Before
    public void setUp() throws Exception {
        analyzer.init();
    }

    @After
    public void tearDown() throws Exception {
        analyzer.end();
    }

    @Test
    public void testAnalyze() {
        String[] data = new String[] { "Brayan", "Ava", " ", "" };
        for (String value : data) {
            analyzer.analyze(value);
        }
        TextLengthStatistics stats = analyzer.getResult().get(0);
        // Min
        Assert.assertEquals(0, stats.getMinTextLength(), 0);
        Assert.assertEquals(3, stats.getMinTextLengthIgnoreBlank(), 0);
        // Max
        Assert.assertEquals(6, stats.getMaxTextLength(), 0);
        Assert.assertEquals(6, stats.getMaxTextLengthIgnoreBlank(), 0);
        // Avg
        Assert.assertEquals(2.5, stats.getAvgTextLength(), 0);
        Assert.assertEquals(4.5, stats.getAvgTextLengthIgnoreBlank(), 0);

    }

    @Test
    public void testAnalyzeWithNullValue() {
        String[] data = new String[] { "          ", "Brayan", "Ava", " ", null };
        for (String value : data) {
            analyzer.analyze(value);
        }
        TextLengthStatistics stats = analyzer.getResult().get(0);
        // Min
        Assert.assertEquals(1, stats.getMinTextLength(), 0);
        Assert.assertEquals(3, stats.getMinTextLengthIgnoreBlank(), 0);
        // Max
        Assert.assertEquals(10, stats.getMaxTextLength(), 0);
        Assert.assertEquals(6, stats.getMaxTextLengthIgnoreBlank(), 0);
        // Avg
        Assert.assertEquals(5, stats.getAvgTextLength(), 0);
        Assert.assertEquals(4.5, stats.getAvgTextLengthIgnoreBlank(), 0);

    }

    @Test
    public void testEmpties() {
        String[] data = new String[] { "  gmail.", "  " };
        for (String value : data) {
            analyzer.analyze(value);
        }
        TextLengthStatistics stats = analyzer.getResult().get(0);
        Assert.assertEquals(5, stats.getAvgTextLength(), 0);
        Assert.assertEquals(8, stats.getAvgTextLengthIgnoreBlank(), 0);
    }

    @Test
    public void testAnalyzeSurrogatePair() {
        String[] data = new String[] { "𠀀", "Avr", "ab", "我𠀀𠀐𠀑𠀒" };
        for (String value : data) {
            analyzer.analyze(value);
        }
        TextLengthStatistics stats = analyzer.getResult().get(0);
        // Min
        Assert.assertEquals(0, stats.getMinTextLength(), 1);
        // Max
        Assert.assertEquals(5, stats.getMaxTextLength(), 0);
        // Avg
        Assert.assertEquals(2.75, stats.getAvgTextLength(), 0);
    }

    @Test
    public void testAnalyzeOnlyNull() {
        String[] data = new String[] { null };
        for (String value : data) {
            analyzer.analyze(value);
        }

        TextLengthStatistics stats = analyzer.getResult().get(0);
        // Min
        Assert.assertEquals(0, stats.getMinTextLength(), 0);
        Assert.assertEquals(0, stats.getMinTextLengthIgnoreBlank(), 0);
        // Max
        Assert.assertEquals(0, stats.getMaxTextLength(), 0);
        Assert.assertEquals(0, stats.getMaxTextLengthIgnoreBlank(), 0);
        // Avg
        Assert.assertNull(stats.getAvgTextLength());
        Assert.assertEquals(0, stats.getAvgTextLengthIgnoreBlank(), 0);

        Assert.assertEquals(0, stats.getCount(), 0);
        Assert.assertEquals(0, stats.getCountIgnoreBlank(), 0);
    }

    @Test
    public void testNullString() {
        Assert.assertEquals(0, analyzer.getResult().size(), 0);
        Assert.assertTrue(analyzer.analyze(null));
        Assert.assertEquals(0, analyzer.getResult().size(), 0);
    }

    @Test
    public void testGetterSetter() {
        String[] data = new String[] { null };
        for (String value : data) {
            analyzer.analyze(value);
        }
        TextLengthStatistics stats = analyzer.getResult().get(0);

        // set general values
        stats.setMinTextLength(1);
        stats.setMinTextLengthIgnoreBlank(1);
        stats.setMaxTextLength(6);
        stats.setMaxTextLengthIgnoreBlank(5);
        stats.setSumTextLength(12);
        stats.setSumTextLengthIgnoreBlank(6);
        stats.setCount(3);
        stats.setCountIgnoreBlank(2);

        Assert.assertEquals(1, stats.getMinTextLength(), 0);
        Assert.assertEquals(1, stats.getMinTextLengthIgnoreBlank(), 0);
        Assert.assertEquals(6, stats.getMaxTextLength(), 0);
        Assert.assertEquals(5, stats.getMaxTextLengthIgnoreBlank(), 0);
        Assert.assertEquals(12, stats.getSumTextLength(), 0);
        Assert.assertEquals(6, stats.getSumTextLengthIgnoreBlank(), 0);
        Assert.assertEquals(3, stats.getCount(), 0);
        Assert.assertEquals(2, stats.getCountIgnoreBlank(), 0);
        Assert.assertEquals(4, stats.getAvgTextLength(), 0);
        Assert.assertEquals(3, stats.getAvgTextLengthIgnoreBlank(), 0);

        // set null values.
        stats.setMinTextLength(null);
        stats.setMinTextLengthIgnoreBlank(null);
        stats.setMaxTextLength(0);
        stats.setMaxTextLengthIgnoreBlank(null);
        stats.setSumTextLength(null);
        stats.setSumTextLengthIgnoreBlank(null);
        stats.setCount(0);
        stats.setCountIgnoreBlank(0);

        Assert.assertEquals(0, stats.getMinTextLength(), 0);
        Assert.assertEquals(0, stats.getAvgTextLengthIgnoreBlank(), 0);
        Assert.assertEquals(0, stats.getMaxTextLength(), 0);
        Assert.assertEquals(0, stats.getMaxTextLength(), 0);
        Assert.assertEquals(0, stats.getMaxTextLengthIgnoreBlank(), 0);
        Assert.assertNull(stats.getAvgTextLength());
        Assert.assertEquals(0, stats.getAvgTextLengthIgnoreBlank(), 0);
        Assert.assertEquals(0, stats.getCount(), 0);
        Assert.assertEquals(0, stats.getCountIgnoreBlank(), 0);

        // set bigger value
        stats.setSumTextLength(Integer.MAX_VALUE);
        stats.setSumTextLengthIgnoreBlank(Integer.MAX_VALUE);
        stats.setCount(100);
        stats.setCountIgnoreBlank(1000);
        Assert.assertEquals(21474836.47, stats.getAvgTextLength(), 0);
        Assert.assertEquals(2147483.647, stats.getAvgTextLengthIgnoreBlank(), 0);
        Assert.assertEquals(100, stats.getCount(), 0);
        Assert.assertEquals(1000, stats.getCountIgnoreBlank(), 0);
    }
}
