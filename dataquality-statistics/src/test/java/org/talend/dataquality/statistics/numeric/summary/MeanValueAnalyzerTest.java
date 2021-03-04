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
package org.talend.dataquality.statistics.numeric.summary;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.statistics.type.DataTypeEnum;

public class MeanValueAnalyzerTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnalyze() {
        // 1. test with values of all double.
        String[] dValues = new String[] { "20", "0.3", "3", "4.5", "8" };
        SummaryAnalyzer meanAnalyzer = new SummaryAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE });
        meanAnalyzer.init();
        for (String strValue : dValues) {
            meanAnalyzer.analyze(strValue);
        }
        meanAnalyzer.end();
        Assert.assertEquals(7.16, meanAnalyzer.getResult().get(0).getMean(), 0.001);

        // 2. assert with only one digits
        dValues = new String[] { "10" };
        meanAnalyzer.init();
        for (String strValue : dValues) {
            meanAnalyzer.analyze(strValue);
        }
        meanAnalyzer.end();
        Assert.assertEquals(10, meanAnalyzer.getResult().get(0).getMean(), 0);

        // 3. assert with values contain a str
        meanAnalyzer.init();
        dValues = new String[] { "20", "a str", "3", "4.5", "8" };
        for (String strValue : dValues) {
            meanAnalyzer.analyze(strValue);
        }
        meanAnalyzer.end();
        Assert.assertEquals(8.875, meanAnalyzer.getResult().get(0).getMean(), 0);

        // 4. assert with empty
        meanAnalyzer.init();
        dValues = new String[] { "" };
        for (String strValue : dValues) {
            meanAnalyzer.analyze(strValue);
        }
        meanAnalyzer.end();
        Assert.assertTrue(Double.isNaN(meanAnalyzer.getResult().get(0).getMean()));

    }

}
