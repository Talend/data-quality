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

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.statistics.type.DataTypeEnum;

public class VarianceValueAnalyzerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnalyzeStringArray() {
        SummaryAnalyzer analyzer = new SummaryAnalyzer(new DataTypeEnum[] { DataTypeEnum.DOUBLE });
        // 1. assert case of all double values.
        String[] pureDouble = new String[] { "20", "0.3", "3", "4.5", "8" };
        analyzer.init();
        for (String strValue : pureDouble) {
            analyzer.analyze(strValue);
        }
        analyzer.end();
        assertEquals(59.2, analyzer.getResult().get(0).getVariance(), 0.055);

        // 2. assert variance is small give same data.
        pureDouble = new String[] { "8", "8", "8", "8", "8" };
        analyzer.init();
        for (String strValue : pureDouble) {
            analyzer.analyze(strValue);
        }
        analyzer.end();
        assertEquals(0, analyzer.getResult().get(0).getVariance(), 0.055);

        // 3. assert with values contain a str
        analyzer.init();
        String[] strValues = new String[] { "20", "0.3", "3", "4.5", "8", "a str" };
        for (String strValue : strValues) {
            analyzer.analyze(strValue);
        }
        analyzer.end();
        assertEquals(59.2, analyzer.getResult().get(0).getVariance(), 0.055);

    }

}
