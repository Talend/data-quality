// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.statistics.frequency.pattern;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.statistics.frequency.AbstractFrequencyAnalyzer;
import org.talend.dataquality.statistics.quality.DataTypeQualityAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeEnum;

public class CompositePatternFrequencyAnalyzerTest {

    AbstractFrequencyAnalyzer<PatternFrequencyStatistics> patternFreqAnalyzer = null;

    @Before
    public void setUp() throws Exception {
        patternFreqAnalyzer = new CompositePatternFrequencyAnalyzer();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAsciiAndAsiaChars() {
        CompositePatternFrequencyAnalyzer analyzer = new CompositePatternFrequencyAnalyzer();

        Set<String> patternString1 = analyzer.getValuePatternSet("abcd1234ィゥェ中国");
        Assert.assertEquals(Collections.singleton("aaaa9999KKKCC"), patternString1);

        Set<String> patternString4 = analyzer.getValuePatternSet("2008-01-01");
        Assert.assertEquals(new HashSet<String>(Arrays.asList(new String[] { "yyyy-MM-dd" })), patternString4);

        Set<String> patternString5 = analyzer.getValuePatternSet("2008-1月-01");
        Assert.assertEquals(Collections.singleton("9999-9C-99"), patternString5);

    }

    @Test
    public void testAnalyze() {
        String[] data = new String[] { "John", "", "123Code", "111", "Zhao", "2015-08-20", "2012-02-12", "12/2/99", "Hois",
                "2001年" };
        for (String value : data) {
            patternFreqAnalyzer.analyze(value);
        }
        Map<String, Long> freqTable = patternFreqAnalyzer.getResult().get(0).getTopK(10);
        Iterator<Entry<String, Long>> entrySet = freqTable.entrySet().iterator();
        int idx = 0;
        boolean isAtLeastOneAsssert = false;
        while (entrySet.hasNext()) {
            Entry<String, Long> e = entrySet.next();
            if (idx == 0) {
                Assert.assertEquals("Aaaa", e.getKey());
                Assert.assertEquals(3, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            } else if (idx == 1) {
                Assert.assertEquals("yyyy-MM-dd", e.getKey());
                Assert.assertEquals(2, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            if (e.getKey().equals("999Aaaa")) {
                Assert.assertEquals(1, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            idx++;
        }
        Assert.assertTrue(isAtLeastOneAsssert);
    }

    @Test
    public void testAnalyzerTwoColumns() {

        String[][] data = new String[][] { { "John", "filx" }, { "", "a" }, { "123Code", "3649273" }, { "111", "100" },
                { "Zhao", "silL" }, { "2015-08-20", "2015-08-21" }, { "2012-02-12", "2022-9-12" }, { "12/2/99", "12/2/99" },
                { "Hois", "*^2lii" }, { "2001年", "4445-" } };
        for (String[] value : data) {
            patternFreqAnalyzer.analyze(value);
        }
        Map<String, Long> freqTable = patternFreqAnalyzer.getResult().get(0).getTopK(10);
        Map<String, Long> freqTable2 = patternFreqAnalyzer.getResult().get(1).getTopK(10);
        Iterator<Entry<String, Long>> entrySet = freqTable.entrySet().iterator();
        Iterator<Entry<String, Long>> entrySet2 = freqTable2.entrySet().iterator();
        int idx = 0;
        boolean isAtLeastOneAsssert = false;
        while (entrySet.hasNext()) {
            Entry<String, Long> e = entrySet.next();
            if (idx == 0) {
                Assert.assertEquals("Aaaa", e.getKey());
                Assert.assertEquals(3, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            } else if (idx == 1) {
                Assert.assertEquals("yyyy-MM-dd", e.getKey());
                Assert.assertEquals(2, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            if (e.getKey().equals("999Aaaa")) {
                Assert.assertEquals(1, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            idx++;
        }
        Assert.assertTrue(isAtLeastOneAsssert);

        isAtLeastOneAsssert = false;
        while (entrySet2.hasNext()) {
            Entry<String, Long> e = entrySet2.next();
            if (idx == 0) {
                Assert.assertEquals("yyyy-M-d", e.getKey());
                Assert.assertEquals(2, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            if (e.getKey().equals("9999999")) {
                Assert.assertEquals(1, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            if (e.getKey().equals("a")) {
                Assert.assertEquals(1, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            if (e.getKey().equals("d/M/yy")) {
                Assert.assertEquals(1, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            idx++;
        }
        Assert.assertTrue(isAtLeastOneAsssert);

    }

    @Test
    public void testCustomDateTypeQualityAnalyzer() {

        // Add value quality analyzer to have list of valid date. some date matches patterns from the file, some matches
        // them in memory user set.
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(DataTypeEnum.DATE);
        qualityAnalyzer.addCustomDateTimePattern("M/d/yy a H:m");
        qualityAnalyzer.init();
        // 2-8-15 15:57 is not at date with pattern available,"2012-02-12" is a date match pattern from file, the others
        // match pattern set ad-hoc
        final String[] data = new String[] { "11/19/07 AM 2:54", "7/6/09 PM 16:46", "2/8/15 PM 15:57", "2*8*15 15:57",
                "2012-02-12" };
        for (String value : data) {
            qualityAnalyzer.analyze(value);
        }
        qualityAnalyzer.end();
        assertTrue(qualityAnalyzer.getResult().size() > 0);
        Assert.assertEquals(5, qualityAnalyzer.getResult().get(0).getCount(), 0); // Count
        Assert.assertEquals(4, qualityAnalyzer.getResult().get(0).getValidCount()); // Valid Count
        // Invalid values
        Assert.assertTrue(qualityAnalyzer.getResult().get(0).getInvalidValues().size() == 1);
        Assert.assertEquals("2*8*15 15:57", qualityAnalyzer.getResult().get(0).getInvalidValues().toArray()[0]);

        // Add new customized pattern , create new quality analyzer , check again dates should be valid given customized
        // pattern and the pattern in file.
        // patterns provided.
        DataTypeQualityAnalyzer qualityAnalyzer2 = new DataTypeQualityAnalyzer(DataTypeEnum.DATE);
        qualityAnalyzer2.addCustomDateTimePattern("M*d*yy H:m");
        qualityAnalyzer2.init();
        for (String value : data) {
            qualityAnalyzer2.analyze(value);
        }
        qualityAnalyzer2.end();
        Assert.assertEquals(5, qualityAnalyzer2.getResult().get(0).getCount()); // Count
        // Valid Count , only "2012-02-12" and "2-8-15 15:57" match.
        Assert.assertEquals(2, qualityAnalyzer2.getResult().get(0).getValidCount());
        Assert.assertTrue(qualityAnalyzer2.getResult().get(0).getInvalidValues().size() == 3);

        Set<String> resultSet = qualityAnalyzer2.getResult().get(0).getInvalidValues();
        Assert.assertTrue(resultSet.contains("11/19/07 AM 2:54"));
        Assert.assertTrue(resultSet.contains("7/6/09 PM 16:46"));
        Assert.assertTrue(resultSet.contains("2/8/15 PM 15:57"));

    }

}
