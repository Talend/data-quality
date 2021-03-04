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
package org.talend.dataquality.statistics.numeric.histogram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.statistics.type.DataTypeEnum;

public class HistogramAnalyzerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistogramAnalyzerTest.class);

    private HistogramAnalyzer createAnalyzer(DataTypeEnum[] types, HistogramParameter histogramParameter) {
        return new HistogramAnalyzer(types, histogramParameter);
    }

    @Test
    public void testResizeWithInvalidValues() throws Exception {
        String[][] data = { { "aaaa" }, { "5" } };
        HistogramParameter histogramParameter = new HistogramParameter();
        histogramParameter.setDefaultParameters(0, 5, 1);
        HistogramAnalyzer analyzer = createAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE }, histogramParameter);
        for (String[] d : data) {
            analyzer.analyze(d);
        }
        Map<Range, Long> histogram = analyzer.getResult().get(0).getHistogram();
        for (Entry<Range, Long> entry : histogram.entrySet()) {
            final Range range = entry.getKey();
            Assert.assertEquals(0, range.getLower(), 0.00);
            Assert.assertEquals(5, range.getUpper(), 0.00);
        }
    }

    @Test
    public void testAnalyzeStringArray() {
        String[] data = { "0", "2", "2.5", "4", "6", "7", "8", "9", "10" };
        HistogramParameter histogramParameter = new HistogramParameter();
        histogramParameter.setDefaultParameters(0, 10, 4);
        HistogramAnalyzer analyzer = createAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE }, histogramParameter);
        for (String d : data) {
            analyzer.analyze(d);
        }

        Map<Range, Long> histogram = analyzer.getResult().get(0).getHistogram();

        Iterator<Entry<Range, Long>> entrySet = histogram.entrySet().iterator();
        int idx = 0;
        while (entrySet.hasNext()) {
            Entry<Range, Long> entry = entrySet.next();
            Range r = entry.getKey();
            if (idx == 0) {
                Assert.assertEquals(0, r.getLower(), 0.00);
                Assert.assertEquals(2.5, r.getUpper(), 0.00);
                Assert.assertEquals(2, entry.getValue(), 0);
            }
            if (idx == 1) {
                Assert.assertEquals(2.5, r.getLower(), 0.00);
                Assert.assertEquals(5, r.getUpper(), 0.00);
                Assert.assertEquals(2, entry.getValue(), 0);
            }
            if (idx == 2) {
                Assert.assertEquals(5, r.getLower(), 0.00);
                Assert.assertEquals(7.5, r.getUpper(), 0.00);
                Assert.assertEquals(2, entry.getValue(), 0);
            }
            if (idx == 3) {
                Assert.assertEquals(7.5, r.getLower(), 0.00);
                Assert.assertEquals(10, r.getUpper(), 0.00);
                Assert.assertEquals(3, entry.getValue(), 0);
            }
            idx++;
        }
    }

    @Test
    public void testAnalyzeExtended() {
        String[] data = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        HistogramParameter histogramParameter = new HistogramParameter();
        histogramParameter.setDefaultParameters(2, 8, 3);
        HistogramAnalyzer analyzer = createAnalyzer(new DataTypeEnum[] { DataTypeEnum.INTEGER }, histogramParameter);
        for (String d : data) {
            analyzer.analyze(d);
        }

        HistogramStatistics histogramStatistics = analyzer.getResult().get(0);
        Map<Range, Long> histogram = histogramStatistics.getHistogram();

        Iterator<Entry<Range, Long>> entrySet = histogram.entrySet().iterator();
        int idx = 0;
        while (entrySet.hasNext()) {
            Entry<Range, Long> entry = entrySet.next();
            Range r = entry.getKey();
            if (idx == 0) {
                LOGGER.debug(r.getLower() + " to " + r.getUpper() + ", count:" + entry.getValue());
                Assert.assertEquals(2, r.getLower(), 0.00);
                Assert.assertEquals(4, r.getUpper(), 0.00);
                Assert.assertEquals(2, entry.getValue(), 0);
            }
            if (idx == 1) {
                LOGGER.debug(r.getLower() + " to " + r.getUpper() + ", count:" + entry.getValue());
                Assert.assertEquals(4, r.getLower(), 0.00);
                Assert.assertEquals(6, r.getUpper(), 0.00);
                Assert.assertEquals(2, entry.getValue(), 0);
            }
            if (idx == 2) {
                LOGGER.debug(r.getLower() + " to " + r.getUpper() + ", count:" + entry.getValue());
                Assert.assertEquals(6, r.getLower(), 0.00);
                Assert.assertEquals(8, r.getUpper(), 0.00);
                Assert.assertEquals(3, entry.getValue(), 0);
            }

            idx++;
        }
        // Assert the value out of range
        Assert.assertFalse(histogramStatistics.isComplete());
        Assert.assertEquals(1, histogramStatistics.getCountBelowMin(), 0);
        Assert.assertEquals(2, histogramStatistics.getCountAboveMax(), 0);
    }

    @Test
    public void testAnalyzeNegative() {
        String[] data = { "-2", "-4", "-6", "-7", "8", "9", "5", "1" };
        HistogramParameter histogramParameter = new HistogramParameter();
        histogramParameter.setDefaultParameters(-4, 8, 3);
        HistogramAnalyzer analyzer = createAnalyzer(new DataTypeEnum[] { DataTypeEnum.INTEGER }, histogramParameter);
        for (String d : data) {
            analyzer.analyze(d);
        }
        Map<Range, Long> histogram = analyzer.getResult().get(0).getHistogram();
        histogram.forEach(new BiConsumer<Range, Long>() {

            @Override
            public void accept(Range t, Long u) {
                LOGGER.debug(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                if (t.getLower() == -4.0) {
                    Assert.assertEquals(2, u, 0.0);
                }
                if (t.getLower() == 0.0) {
                    Assert.assertEquals(1, u, 0.0);
                }
                if (t.getLower() == 4.0) {
                    Assert.assertEquals(2, u, 0.0);
                }
            }

        });
    }

    @Test
    public void testAnalyzeFranction() {
        String[] data = { "-0.0001", "-0.00004", "-0.00006", "-0.00007", "8", "7", "9", "5", "1" };
        HistogramParameter histogramParameter = new HistogramParameter();
        histogramParameter.setDefaultParameters(-0.004, 9, 3);
        HistogramAnalyzer analyzer = createAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE }, histogramParameter);
        for (String d : data) {
            analyzer.analyze(d);
        }
        Map<Range, Long> histogram = analyzer.getResult().get(0).getHistogram();
        histogram.forEach(new BiConsumer<Range, Long>() {

            @Override
            public void accept(Range t, Long u) {
                if (t.getLower() == -0.004) {
                    Assert.assertEquals(5, u, 0.0);
                    LOGGER.debug(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (Math.round(t.getLower() * 1000.0) / 1000.0 == 2.997) {
                    Assert.assertEquals(1, u, 0.0);
                    LOGGER.debug(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (Math.round(t.getLower() * 1000.0) / 1000.0 == 5.999) {
                    Assert.assertEquals(3, u, 0.0);
                    LOGGER.debug(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                }
            }

        });
    }

    @Test
    public void testHistogramWithColumnParameters() {
        String[][] data = new String[][] { { "1", "1", "one" }, { "2", "2", "2" }, { "3", "3", "3" }, { "4", "4", "4" },
                { "5", "5", "5" }, { "6", "6", "6" }, { "7", "7", "7" }, { "8", "8", "8" }, { "9", "9", "9" },
                { "10", "10", "10" } };
        HistogramParameter histogramParameter = new HistogramParameter();
        HistogramColumnParameter column1Param = new HistogramColumnParameter();
        column1Param.setParameters(2, 8, 3);
        histogramParameter.putColumnParameter(0, column1Param);
        HistogramColumnParameter column2Param = new HistogramColumnParameter();
        column2Param.setParameters(0, 9, 4);
        histogramParameter.putColumnParameter(1, column2Param);
        HistogramAnalyzer analyzer =
                createAnalyzer(new DataTypeEnum[] { DataTypeEnum.INTEGER, DataTypeEnum.INTEGER, DataTypeEnum.STRING },
                        histogramParameter);
        for (String[] d : data) {
            analyzer.analyze(d);
        }
        Map<Range, Long> col1Histogram = analyzer.getResult().get(0).getHistogram();
        Map<Range, Long> col2Histogram = analyzer.getResult().get(1).getHistogram();
        col1Histogram.forEach(new BiConsumer<Range, Long>() {

            @Override
            public void accept(Range t, Long u) {
                if (t.getLower() == 2) {
                    Assert.assertEquals(2, u, 0.0);
                    Assert.assertEquals(4, t.getUpper(), 0.0);
                    LOGGER.debug(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (t.getLower() == 4) {
                    Assert.assertEquals(2, u, 0.0);
                    Assert.assertEquals(6, t.getUpper(), 0.0);
                    LOGGER.debug(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (t.getLower() == 6) {
                    Assert.assertEquals(3, u, 0.0);
                    Assert.assertEquals(8, t.getUpper(), 0.0);
                    LOGGER.debug(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
            }

        });
        col2Histogram.forEach(new BiConsumer<Range, Long>() {

            @Override
            public void accept(Range t, Long u) {
                if (t.getLower() == 0) {
                    Assert.assertEquals(2, u, 0.0);
                    Assert.assertEquals(2.25, t.getUpper(), 0.0);
                    LOGGER.debug(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (t.getLower() == 2.25) {
                    Assert.assertEquals(2, u, 0.0);
                    Assert.assertEquals(4.5, t.getUpper(), 0.0);
                    LOGGER.debug(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (t.getLower() == 4.5) {
                    Assert.assertEquals(2, u, 0.0);
                    Assert.assertEquals(6.75, t.getUpper(), 0.0);
                    LOGGER.debug(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (t.getLower() == 6.75) {
                    Assert.assertEquals(3, u, 0.0);
                    Assert.assertEquals(9, t.getUpper(), 0.0);
                    LOGGER.debug(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
            }

        });
    }

    @Test
    public void testMultipleRandomHistograms() {
        final int nbLoop = 10;
        for (int i = 0; i < nbLoop; i++) {
            testHistogramWithRandom(3, 60, -1300 + 10 * i, 196 + 11 * i, 3435 + 10 * i, i * 37, 3 + i * 73);
        }
    }

    private void testHistogramWithRandom(int minNbBins, int maxNbBins, int minValue, int maxMinValue, int maxValue,
            int minNbValue, int maxNbValue) {
        // number of bins
        int numBins = ThreadLocalRandom.current().nextInt(minNbBins, maxNbBins);
        // min value,max value
        double min = ThreadLocalRandom.current().nextDouble(minValue, maxMinValue);
        double max = ThreadLocalRandom.current().nextDouble(maxMinValue + 1, maxValue);
        double step = (max - min) / numBins;
        // value list
        List<Double> values = new ArrayList<Double>();
        // histograms
        Map<Range, Long> histograms = new TreeMap<Range, Long>();
        double current = min;
        for (int i = 1; i <= numBins; i++) {
            // generate values for each bin ( range of 5 to 10)
            long countInBin = ThreadLocalRandom.current().nextLong(minNbValue, maxNbValue);
            double next = current + step;
            if (i == numBins) {
                next = max;
            }
            Range currentRange = new Range(current, next);
            for (int j = 0; j < countInBin; j++) {
                double rValue = ThreadLocalRandom.current().nextDouble(current, next);
                values.add(rValue);
            }
            if (1 == i || numBins == i) {
                // increment count since min / max is included
                countInBin++;
            }
            histograms.put(currentRange, countInBin);
            // Go to next bin
            current = next;
        }

        // Add min and max into value list
        values.add(min);
        values.add(max);
        LOGGER.debug("numBins: " + numBins + ", min: " + min + ", max:" + max);
        // analyze histogram
        HistogramParameter histogramParameter = new HistogramParameter();
        HistogramColumnParameter columnParam = new HistogramColumnParameter();
        columnParam.setParameters(min, max, numBins);
        histogramParameter.putColumnParameter(0, columnParam);
        HistogramAnalyzer analyzer = createAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE }, histogramParameter);
        for (Double d : values) {
            analyzer.analyze(d.toString());
        }
        Map<Range, Long> histogramFromAnalyzer = analyzer.getResult().get(0).getHistogram();

        // do assertions
        int binIdx = 0;
        for (Entry<Range, Long> histEntry : histograms.entrySet()) {
            @SuppressWarnings("unchecked")
            Entry<Range, Long> histEntryOfAnalyzer =
                    (Entry<Range, Long>) histogramFromAnalyzer.entrySet().toArray()[binIdx];
            Assert.assertEquals(histEntry.getKey().getLower(), histEntryOfAnalyzer.getKey().getLower(), 0.001);
            Assert.assertEquals(histEntry.getKey().getUpper(), histEntryOfAnalyzer.getKey().getUpper(), 0.001);
            Assert.assertEquals(histEntry.getValue(), histEntryOfAnalyzer.getValue());
            binIdx++;
        }

    }
}
