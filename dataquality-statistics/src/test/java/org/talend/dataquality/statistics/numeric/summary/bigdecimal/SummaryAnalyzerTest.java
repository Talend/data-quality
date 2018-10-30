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
import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.number.BigDecimalParser;
import org.talend.dataquality.statistics.numeric.summary.bigdecimal.SummaryAnalyzerBigDecimal;
import org.talend.dataquality.statistics.numeric.summary.bigdecimal.SummaryStatisticsBigDecimal;
import org.talend.dataquality.statistics.type.DataTypeEnum;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SummaryAnalyzerTest {

    private SummaryAnalyzerBigDecimal analyzer;

    @Before
    public void setup() {
        DataTypeEnum[] types = { DataTypeEnum.DOUBLE };
        analyzer = new SummaryAnalyzerBigDecimal(types);
    }

    @Test
    public void doubleAnalyze() {
        List<String> list = Arrays.asList("6", "5", "4", "3", "2", "1");

        list.forEach(v -> analyzer.analyze(v));
        final List<SummaryStatisticsBigDecimal> result = analyzer.getResult();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMin()).isCloseTo(BigDecimalParser.toBigDecimal("1.0"), Offset.offset(BigDecimal.ZERO));
        assertThat(result.get(0).getMax()).isCloseTo(BigDecimalParser.toBigDecimal("6.0"), Offset.offset(BigDecimal.ZERO));
        assertThat(result.get(0).getMean()).isCloseTo(BigDecimalParser.toBigDecimal("3.5"), Offset.offset(BigDecimal.ZERO));
        assertThat(result.get(0).getVariance()).isCloseTo(BigDecimalParser.toBigDecimal("3.5"), Offset.offset(BigDecimal.ZERO));
        assertThat(result.get(0).getSum()).isCloseTo(BigDecimalParser.toBigDecimal("21.0"), Offset.offset(BigDecimal.ZERO));
    }

    @Test
    public void doubleAnalyzeLimit() {
        List<String> list = Arrays.asList("1E21564654", "-1E21564654", "200.2", "-200.2", "abc");

        list.forEach(v -> analyzer.analyze(v));
        final List<SummaryStatisticsBigDecimal> result = analyzer.getResult();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMin()).isCloseTo(BigDecimalParser.toBigDecimal("-1E21564654"),
                Offset.offset(BigDecimal.ZERO));
        assertThat(result.get(0).getMax()).isCloseTo(BigDecimalParser.toBigDecimal("1E21564654"), Offset.offset(BigDecimal.ZERO));
        assertThat(result.get(0).getMean()).isCloseTo(BigDecimalParser.toBigDecimal("0"), Offset.offset(BigDecimal.ZERO));
        // Variance is too big to be tested
        assertThat(result.get(0).getSum()).isCloseTo(BigDecimalParser.toBigDecimal("0"), Offset.offset(BigDecimal.ZERO));
    }
}