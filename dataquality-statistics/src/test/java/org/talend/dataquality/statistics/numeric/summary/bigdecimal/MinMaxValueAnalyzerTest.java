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
package org.talend.dataquality.statistics.numeric.summary.bigdecimal;

import org.assertj.core.data.Offset;
import org.junit.Test;
import org.talend.daikon.number.BigDecimalParser;
import org.talend.dataquality.statistics.numeric.summary.bigdecimal.SummaryAnalyzerBigDecimal;
import org.talend.dataquality.statistics.numeric.summary.bigdecimal.SummaryStatisticsBigDecimal;
import org.talend.dataquality.statistics.type.DataTypeEnum;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class MinMaxValueAnalyzerTest {

    @Test
    public void testAnalyzeDoubleIntegerMixed() {

        String[][] test2Cols_Double_Int = new String[][] { { "20", "20" }, { "1.0", "1.0" }, { "3", "3" }, { "4.5", "4.5" },
                { "8.0", "8" } };
        SummaryAnalyzerBigDecimal analyzer = new SummaryAnalyzerBigDecimal(
                new DataTypeEnum[] { DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER });
        for (String[] values : test2Cols_Double_Int) {
            analyzer.analyze(values);
        }
        // for the double column: "20", "1.0", "3", "4.5", "8.0"
        assertThat(analyzer.getResult().get(0).getMin()).isCloseTo(BigDecimal.valueOf(1.0), Offset.offset(BigDecimal.ZERO));
        assertThat(analyzer.getResult().get(0).getMax()).isCloseTo(BigDecimal.valueOf(20), Offset.offset(BigDecimal.ZERO));// "20" is also valid as a double
        // for the integer column: "20", "1.0", "3", "4.5", "8"

        assertThat(analyzer.getResult().get(1).getMin()).isCloseTo(BigDecimal.valueOf(3), Offset.offset(BigDecimal.ZERO));// "1.0" is not valid in a integer column
        assertThat(analyzer.getResult().get(1).getMax()).isCloseTo(BigDecimal.valueOf(20), Offset.offset(BigDecimal.ZERO));
    }

    @Test
    public void testAnalyzeStr() {

        String[][] test2Cols_Double_Str = new String[][] { { "a str", "a" }, { "1.0", "b" }, { "3", "c" }, { "4.5", "4.5" },
                { "8.0", "8.0" } };
        SummaryAnalyzerBigDecimal analyzer = new SummaryAnalyzerBigDecimal(
                new DataTypeEnum[] { DataTypeEnum.DOUBLE, DataTypeEnum.STRING });
        for (String[] values : test2Cols_Double_Str) {
            analyzer.analyze(values);
        }
        // for the double type column with one string value: "a str", "1.0", "3", "4.5", "8.0"
        assertThat(analyzer.getResult().get(0).isValid()).isTrue();
        assertThat(analyzer.getResult().get(0).getMin()).isEqualTo(BigDecimal.valueOf(1.0));
        assertThat(analyzer.getResult().get(0).getMax()).isCloseTo(BigDecimal.valueOf(8), Offset.offset(BigDecimal.ZERO));
        // for the string type column with double values: "a", "b", "c", "4.5", "8.0"
        assertThat(analyzer.getResult().get(1).isValid()).isFalse();

    }

    @Test
    public void testAnalyzeEmpty() {

        String[][] test2Cols_Empty_StrSpace = new String[][] { { "", "" }, { "", "" } };
        SummaryAnalyzerBigDecimal analyzer = new SummaryAnalyzerBigDecimal(
                new DataTypeEnum[] { DataTypeEnum.EMPTY, DataTypeEnum.STRING });
        for (String[] values : test2Cols_Empty_StrSpace) {
            analyzer.analyze(values);
        }
        // for the EMPTY type column: "", ""
        assertThat(analyzer.getResult().get(0).isValid()).isFalse();
        // for the STRING type column: "", ""
        assertThat(analyzer.getResult().get(1).isValid()).isFalse();

        // for issue: https://jira.talendforge.org/browse/TDQ-10863
        String[] testMixedCol = new String[] { "22", "21", "18", "", "23", "25", "26", "26.5" };
        analyzer = new SummaryAnalyzerBigDecimal(new DataTypeEnum[] { DataTypeEnum.INTEGER });
        for (String value : testMixedCol) {
            analyzer.analyze(value);
        }
        assertThat(analyzer.getResult().get(0).getMin()).isCloseTo(BigDecimal.valueOf(18), Offset.offset(BigDecimal.ZERO));
        assertThat(analyzer.getResult().get(0).getMax()).isCloseTo(BigDecimal.valueOf(26), Offset.offset(BigDecimal.ZERO));

    }

    @Test
    public void testMixedNumberFormats() {

        final String[][] testers = new String[][] {
                //
                { "1,1", "3.333,33", "5,555", "1E308", "3.8", "1E-3" }, //
                { "2.2", "4,444.44", "6.666", "1E309", "38%", "2E-2%" },//
        };

        SummaryAnalyzerBigDecimal analyzer = new SummaryAnalyzerBigDecimal(new DataTypeEnum[] { DataTypeEnum.DOUBLE,
                DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE, });

        for (String[] values : testers) {
            analyzer.analyze(values);
        }
        assertMinAndMax(analyzer.getResult().get(0), BigDecimal.valueOf(1.1), BigDecimal.valueOf(2.2));
        assertMinAndMax(analyzer.getResult().get(1), BigDecimal.valueOf(3333.33), BigDecimal.valueOf(4444.44));
        assertMinAndMax(analyzer.getResult().get(2), BigDecimal.valueOf(6.666), BigDecimal.valueOf(5555));
        assertMinAndMax(analyzer.getResult().get(3), BigDecimal.valueOf(1E308), BigDecimalParser.toBigDecimal("1E309"));
        assertMinAndMax(analyzer.getResult().get(4), BigDecimal.valueOf(0.38), BigDecimal.valueOf(3.8));
        assertMinAndMax(analyzer.getResult().get(5), BigDecimal.valueOf(0.0002), BigDecimal.valueOf(0.001));
    }

    private void assertMinAndMax(SummaryStatisticsBigDecimal summaryStatistics, BigDecimal min, BigDecimal max) {
        assertThat(summaryStatistics.getMin()).isCloseTo(min, Offset.offset(BigDecimal.ZERO));
        assertThat(summaryStatistics.getMax()).isCloseTo(max, Offset.offset(BigDecimal.ZERO));
    }
}
