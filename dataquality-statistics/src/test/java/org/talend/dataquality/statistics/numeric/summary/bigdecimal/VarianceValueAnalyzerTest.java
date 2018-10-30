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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.statistics.numeric.summary.bigdecimal.SummaryAnalyzerBigDecimal;
import org.talend.dataquality.statistics.type.DataTypeEnum;

import java.math.BigDecimal;

public class VarianceValueAnalyzerTest {

    private SummaryAnalyzerBigDecimal analyzer;

    @Before
    public void setup() {
        analyzer = new SummaryAnalyzerBigDecimal(new DataTypeEnum[] { DataTypeEnum.DOUBLE });
    }

    @Test
    public void analyzeWithOnlyDouble() {
        String[] values = new String[] { "20", "0.3", "3", "4.5", "8" };
        for (String strValue : values) {
            analyzer.analyze(strValue);
        }
        assertThat(analyzer.getResult().get(0).getVariance()).isCloseTo(BigDecimal.valueOf(59.2),
                Offset.offset(BigDecimal.valueOf(0.055)));
    }

    @Test
    public void varianceNullWithSameValues() {
        String[] values = new String[] { "8", "8", "8", "8", "8" };
        for (String strValue : values) {
            analyzer.analyze(strValue);
        }
        assertThat(analyzer.getResult().get(0).getVariance()).isCloseTo(BigDecimal.valueOf(0),
                Offset.offset(BigDecimal.valueOf(0.055)));
    }

    @Test
    public void varianceIgnoreInvalidData() {
        String[] values = new String[] { "20", "0.3", "3", "4.5", "8", "a str" };
        for (String strValue : values) {
            analyzer.analyze(strValue);
        }
        assertThat(analyzer.getResult().get(0).getVariance()).isCloseTo(BigDecimal.valueOf(59.2),
                Offset.offset(BigDecimal.valueOf(0.055)));

    }

}
