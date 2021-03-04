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
package org.talend.dataquality.statistics.numeric.quantile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.statistics.numeric.summary.SummaryAnalyzer;
import org.talend.dataquality.statistics.quality.ValueQualityAnalyzerTest;
import org.talend.dataquality.statistics.type.DataTypeEnum;

public class QuantileAnalyzerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnalyzeStringArray() {
        String[] data = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        QuantileAnalyzer analyzer = new QuantileAnalyzer(new DataTypeEnum[] { DataTypeEnum.INTEGER });
        TDigestAnalyzer tanalyzer = new TDigestAnalyzer(new DataTypeEnum[] { DataTypeEnum.INTEGER });
        analyzer.init();
        tanalyzer.init();
        for (String value : data) {
            analyzer.analyze(value);
            tanalyzer.analyze(value);
        }
        analyzer.end();
        tanalyzer.end();
        Assert.assertEquals(5, analyzer.getResult().get(0).getMedian(), 0);
        Assert.assertEquals(2.5, analyzer.getResult().get(0).getLowerQuartile(), 0);
        Assert.assertEquals(7.5, analyzer.getResult().get(0).getUpperQuartile(), 0);
        Assert.assertEquals(3, analyzer.getResult().get(0).getQuantile(0.3), 0);

        Assert.assertEquals(4.5, tanalyzer.getResult().get(0).getMedian(), 0.0);
        Assert.assertEquals(2.75, tanalyzer.getResult().get(0).getLowerQuartile(), 0.0);
        Assert.assertEquals(6.25, tanalyzer.getResult().get(0).getUpperQuartile(), 0);
        Assert.assertEquals(2.3, tanalyzer.getResult().get(0).getQuantile(0.3), 0.001);

        data = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
                "17", "18", "19", "20", "21" };
        analyzer.init();
        tanalyzer.init();
        for (String value : data) {
            analyzer.analyze(value);
            tanalyzer.analyze(value);
        }
        analyzer.end();
        tanalyzer.end();
        Assert.assertEquals(11, analyzer.getResult().get(0).getMedian(), 0.0);
        Assert.assertEquals(5.5, analyzer.getResult().get(0).getLowerQuartile(), 0.0);
        Assert.assertEquals(16.5, analyzer.getResult().get(0).getUpperQuartile(), 0);
        Assert.assertEquals(6.6, analyzer.getResult().get(0).getQuantile(0.3), 0.001);

        Assert.assertEquals(10.5, tanalyzer.getResult().get(0).getMedian(), 0.0);
        Assert.assertEquals(5.75, tanalyzer.getResult().get(0).getLowerQuartile(), 0.0);
        Assert.assertEquals(15.25, tanalyzer.getResult().get(0).getUpperQuartile(), 0);
        Assert.assertEquals(6.7, tanalyzer.getResult().get(0).getQuantile(0.3), 0.001);

        data = new String[] { "1", "9", "3", "4", "6", "7", "5", "8", "2" };
        analyzer.init();
        tanalyzer.init();
        for (String value : data) {
            analyzer.analyze(value);
            tanalyzer.analyze(value);
        }
        analyzer.end();
        tanalyzer.end();
        Assert.assertEquals(5, analyzer.getResult().get(0).getMedian(), 0);
        Assert.assertEquals(2.5, analyzer.getResult().get(0).getLowerQuartile(), 0);
        Assert.assertEquals(7.5, analyzer.getResult().get(0).getUpperQuartile(), 0);
        Assert.assertEquals(3, analyzer.getResult().get(0).getQuantile(0.3), 0);

        Assert.assertEquals(4.5, tanalyzer.getResult().get(0).getMedian(), 0.0);
        Assert.assertEquals(2.75, tanalyzer.getResult().get(0).getLowerQuartile(), 0.0);
        Assert.assertEquals(6.25, tanalyzer.getResult().get(0).getUpperQuartile(), 0);
        Assert.assertEquals(2.3, tanalyzer.getResult().get(0).getQuantile(0.3), 0.001);

        data = new String[] { "0", "0", "0", "0", "0", "0", "0", "0", "0" };
        analyzer.init();
        tanalyzer.init();
        for (String value : data) {
            analyzer.analyze(value);
            tanalyzer.analyze(value);
        }
        analyzer.end();
        tanalyzer.end();
        Assert.assertEquals(0, analyzer.getResult().get(0).getMedian(), 0);
        Assert.assertEquals(0, analyzer.getResult().get(0).getLowerQuartile(), 0);
        Assert.assertEquals(0, analyzer.getResult().get(0).getUpperQuartile(), 0);
        Assert.assertEquals(0, analyzer.getResult().get(0).getQuantile(0.3), 0);

        Assert.assertEquals(0, tanalyzer.getResult().get(0).getMedian(), 0);
        Assert.assertEquals(0, tanalyzer.getResult().get(0).getLowerQuartile(), 0);
        Assert.assertEquals(0, tanalyzer.getResult().get(0).getUpperQuartile(), 0);
        Assert.assertEquals(0, tanalyzer.getResult().get(0).getQuantile(0.3), 0);

        data = new String[] { "-1", "-1", "1", "2", "3", "4", "5", "6", "7" };
        analyzer.init();
        tanalyzer.init();
        for (String value : data) {
            analyzer.analyze(value);
            tanalyzer.analyze(value);
        }
        analyzer.end();
        tanalyzer.end();
        Assert.assertEquals(3, analyzer.getResult().get(0).getMedian(), 0);
        Assert.assertEquals(0, analyzer.getResult().get(0).getLowerQuartile(), 0);
        Assert.assertEquals(5.5, analyzer.getResult().get(0).getUpperQuartile(), 0);
        Assert.assertEquals(1, analyzer.getResult().get(0).getQuantile(0.3), 0);

        Assert.assertEquals(2.5, tanalyzer.getResult().get(0).getMedian(), 0);
        Assert.assertEquals(0.5, tanalyzer.getResult().get(0).getLowerQuartile(), 0);
        Assert.assertEquals(4.25, tanalyzer.getResult().get(0).getUpperQuartile(), 0);
        Assert.assertEquals(-0.4, tanalyzer.getResult().get(0).getQuantile(0.3), 0.001);

        data = new String[] {};
        analyzer.init();
        for (String value : data) {
            analyzer.analyze(value);
        }
        analyzer.end();
        Assert.assertEquals(0, analyzer.getResult().size(), 0);
    }

    @Test
    public void testQuantileOfFile() throws IOException {// test for double data, TDQ-10789, TDP-394
        final List<String[]> records = ValueQualityAnalyzerTest
                .getRecords(this.getClass().getResourceAsStream("../../data/t-shirt_100.csv"), ",");
        QuantileAnalyzer analyzer = new QuantileAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE });
        TDigestAnalyzer tanalyzer = new TDigestAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE });
        SummaryAnalyzer summaryAnalyzer = new SummaryAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE });
        tanalyzer.init();
        analyzer.init();
        summaryAnalyzer.init();
        List<Analyzer<?>> analyzers = new ArrayList<Analyzer<?>>();
        analyzers.add(summaryAnalyzer);
        analyzers.add(tanalyzer);
        analyzers.add(analyzer);
        records.forEach(r -> analyze(analyzers, r[7]));
        tanalyzer.end();
        analyzer.end();
        summaryAnalyzer.end();
        Assert.assertEquals(23.9, tanalyzer.getResult().get(0).getUpperQuartile(), 0);
        Assert.assertEquals(16.7, tanalyzer.getResult().get(0).getLowerQuartile(), 0);
        Assert.assertEquals(23.9, analyzer.getResult().get(0).getUpperQuartile(), 0);
        Assert.assertEquals(16.7, analyzer.getResult().get(0).getLowerQuartile(), 0);
        Assert.assertEquals(16.7, summaryAnalyzer.getResult().get(0).getMin(), 0);
        Assert.assertEquals(32, summaryAnalyzer.getResult().get(0).getMax(), 0);

    }

    private Object analyze(List<Analyzer<?>> analyzers, String value) {
        for (Analyzer<?> ana : analyzers) {
            ana.analyze(value);
        }
        return null;
    }

}
