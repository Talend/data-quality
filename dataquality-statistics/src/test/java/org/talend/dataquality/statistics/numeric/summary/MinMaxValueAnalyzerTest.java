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
package org.talend.dataquality.statistics.numeric.summary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.statistics.type.DataTypeEnum;

public class MinMaxValueAnalyzerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinMaxValueAnalyzerTest.class);

    // cf jdk.nashorn.internal.objects.Global.Infinity
    private static final double Infinity = 1.0D / 0.0;

    private static final double DELTA = 1E-8;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnalyzeDoubleIntegerMixed() {

        String[][] test2Cols_Double_Int = new String[][] { { "20", "20" }, { "1.0", "1.0" }, { "3", "3" }, { "4.5", "4.5" },
                { "8.0", "8" } };
        SummaryAnalyzer analyzer = new SummaryAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER });
        for (String[] values : test2Cols_Double_Int) {
            analyzer.analyze(values);
        }
        // for the double column: "20", "1.0", "3", "4.5", "8.0"
        assertEquals(1.0, analyzer.getResult().get(0).getMin(), 0);
        assertEquals(20, analyzer.getResult().get(0).getMax(), 0);// "20" is also valid as a double
        // for the integer column: "20", "1.0", "3", "4.5", "8"
        assertEquals(3, analyzer.getResult().get(1).getMin(), 0);// "1.0" is not valid in a integer column
        assertEquals(20, analyzer.getResult().get(1).getMax(), 0);

    }

    @Test
    public void testAnalyzeStr() {

        String[][] test2Cols_Double_Str = new String[][] { { "a str", "a" }, { "1.0", "b" }, { "3", "c" }, { "4.5", "4.5" },
                { "8.0", "8.0" } };
        SummaryAnalyzer analyzer = new SummaryAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE, DataTypeEnum.STRING });
        for (String[] values : test2Cols_Double_Str) {
            analyzer.analyze(values);
        }
        // for the double type column with one string value: "a str", "1.0", "3", "4.5", "8.0"
        assertEquals(1.0, analyzer.getResult().get(0).getMin(), 0);
        assertEquals(8, analyzer.getResult().get(0).getMax(), 0);
        // for the string type column with double values: "a", "b", "c", "4.5", "8.0"
        assertTrue(Double.isNaN(analyzer.getResult().get(1).getMin()));
        assertTrue(Double.isNaN(analyzer.getResult().get(1).getMax()));

    }

    @Test
    public void testAnalyzeEmpty() {

        String[][] test2Cols_Empty_StrSpace = new String[][] { { "", "" }, { "", "" } };
        SummaryAnalyzer analyzer = new SummaryAnalyzer(new DataTypeEnum[] { DataTypeEnum.EMPTY, DataTypeEnum.STRING });
        for (String[] values : test2Cols_Empty_StrSpace) {
            analyzer.analyze(values);
        }
        // for the EMPTY type column: "", ""
        assertTrue(Double.isNaN(analyzer.getResult().get(0).getMin()));
        assertTrue(Double.isNaN(analyzer.getResult().get(0).getMax()));
        // for the STRING type column: "", ""
        assertTrue(Double.isNaN(analyzer.getResult().get(1).getMin()));
        assertTrue(Double.isNaN(analyzer.getResult().get(1).getMax()));

        // for issue: https://jira.talendforge.org/browse/TDQ-10863
        String[] testMixedCol = new String[] { "22", "21", "18", "", "23", "25", "26", "26.5" };
        analyzer = new SummaryAnalyzer(new DataTypeEnum[] { DataTypeEnum.INTEGER });
        for (String value : testMixedCol) {
            analyzer.analyze(value);
        }
        assertEquals(18, analyzer.getResult().get(0).getMin(), 0);
        assertEquals(26, analyzer.getResult().get(0).getMax(), 0);

    }

    @Test
    public void testMixedNumberFormats() {

        final String[][] testers = new String[][] {
                //
                { "1,1", "3.333,33", "5,555", "1E308", "3.8", "1E-3" }, //
                { "2.2", "4,444.44", "6.666", "1E309", "38%", "2E-2%" },//
        };

        SummaryAnalyzer analyzer = new SummaryAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE, });

        for (String[] values : testers) {
            analyzer.analyze(values);
        }
        assertMinAndMax(analyzer.getResult().get(0), 1.1, 2.2);
        assertMinAndMax(analyzer.getResult().get(1), 3333.33, 4444.44);
        assertMinAndMax(analyzer.getResult().get(2), 6.666, 5555);
        assertMinAndMax(analyzer.getResult().get(3), 1E308, Infinity);
        assertMinAndMax(analyzer.getResult().get(4), 0.38, 3.8);
        assertMinAndMax(analyzer.getResult().get(5), 0.0002, 0.001);
    }

    private void assertMinAndMax(SummaryStatistics summaryStatistics, double min, double max) {
        assertEquals(min, summaryStatistics.getMin(), DELTA);
        assertEquals(max, summaryStatistics.getMax(), DELTA);
    }
}
