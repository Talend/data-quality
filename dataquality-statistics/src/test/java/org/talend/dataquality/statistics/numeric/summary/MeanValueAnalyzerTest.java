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

import org.assertj.core.data.Offset;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.statistics.type.DataTypeEnum;

import static org.assertj.core.api.Assertions.assertThat;

public class MeanValueAnalyzerTest {

    private SummaryAnalyzer analyzer;

    @Before
    public void setup() {
        analyzer = new SummaryAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE });
    }

    @Test
    public void analyzeWithOnlyDouble() {
        String[] values = new String[] { "20", "0.3", "3", "4.5", "8" };
        for (String value : values) {
            analyzer.analyze(value);
        }
        assertThat(analyzer.getResult().get(0).getMean()).isCloseTo(7.16, Offset.offset(0.001));
    }

    @Test
    public void analyzeWithOneDigit() {
        String[] values = new String[] { "10" };
        for (String value : values) {
            analyzer.analyze(value);
        }
        assertThat(analyzer.getResult().get(0).getMean()).isCloseTo(10, Offset.offset(0.));
    }

    @Test
    public void analyzeWithInvalidStringValueIgnored() {
        String[] values = new String[] { "20", "a str", "3", "4.5", "8" };
        for (String value : values) {
            analyzer.analyze(value);
        }
        assertThat(analyzer.getResult().get(0).getMean()).isCloseTo(8.875, Offset.offset(0.));
    }

    @Test
    public void onlyInvalidValueCreateInvalidResult() {
        String[] values = new String[] { "" };
        for (String value : values) {
            analyzer.analyze(value);
        }
        assertThat(analyzer.getResult().get(0).getMean()).isNaN();
    }

}
