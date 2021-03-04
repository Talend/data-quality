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
package org.talend.dataquality.statistics.cardinality;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CardinalityAnalyzerTest {

    private CardinalityAnalyzer analyzer = new CardinalityAnalyzer();

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnalyze() {
        // 1. based on a small size.
        String[] data = new String[] { "0", "1", "2", "3", "16", "17", "18", "19", "19" };
        analyzer.init();
        for (String col : data) {
            analyzer.analyze(col);
        }
        Assert.assertEquals(8, analyzer.getResult().get(0).getDistinctCount(), 0);
        Assert.assertEquals(1, analyzer.getResult().get(0).getDuplicateCount(), 0);

        // 2. with an empty
        data = new String[] { "" };
        analyzer.init();
        for (String col : data) {
            analyzer.analyze(col);
        }
        Assert.assertEquals(1, analyzer.getResult().get(0).getDistinctCount(), 0);
        Assert.assertEquals(0, analyzer.getResult().get(0).getDuplicateCount(), 0);

        analyzer.init();
        Assert.assertTrue(analyzer.getResult().size() == 0);
        // 3. test with 2 columns
        String[][] twoColumnsData = new String[][] { { "0", "1" }, { "1", "2" }, { "2", "3" }, { "3", "4" },
                { "16", "17" }, { "17", "17" }, { "18", "18" }, { "19", "19" }, { "19", "19" }, { "20", "19" } };
        for (String[] record : twoColumnsData) {
            analyzer.analyze(record);
        }
        Assert.assertEquals(9, analyzer.getResult().get(0).getDistinctCount(), 0);
        Assert.assertEquals(1, analyzer.getResult().get(0).getDuplicateCount(), 0);
        Assert.assertEquals(7, analyzer.getResult().get(1).getDistinctCount(), 0);
        Assert.assertEquals(3, analyzer.getResult().get(1).getDuplicateCount(), 0);

    }

}
