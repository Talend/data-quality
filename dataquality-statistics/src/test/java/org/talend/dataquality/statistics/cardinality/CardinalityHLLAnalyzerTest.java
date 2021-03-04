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

import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CardinalityHLLAnalyzerTest {

    private CardinalityHLLAnalyzer distinctHLLAna = null;

    @Before
    public void setUp() throws Exception {
        distinctHLLAna = new CardinalityHLLAnalyzer();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnalyze() {
        // 1. based on a small size.
        String[] data = new String[] { "0", "1", "2", "3", "16", "17", "18", "19", "19" };
        distinctHLLAna.init();
        for (String col : data) {
            distinctHLLAna.analyze(col);
        }
        Assert.assertEquals(8, distinctHLLAna.getResult().get(0).getDistinctCount(), 0);
        Assert.assertEquals(1, distinctHLLAna.getResult().get(0).getDuplicateCount(), 0);

        // 2. based a on large size, the error less than 0.1
        int size = 10000000;
        distinctHLLAna.init();
        for (int i = 0; i < size; i++) {
            distinctHLLAna.analyze(streamElement(i));
        }
        long estimate = distinctHLLAna.getResult().get(0).getDistinctCount();
        double err = Math.abs(estimate - size) / (double) size;
        Assert.assertTrue(err < .1);

        // 3. with an empty
        data = new String[] { "" };
        distinctHLLAna.init();
        for (String col : data) {
            distinctHLLAna.analyze(col);
        }
        Assert.assertEquals(1, distinctHLLAna.getResult().get(0).getDistinctCount(), 0);
        Assert.assertEquals(0, distinctHLLAna.getResult().get(0).getDuplicateCount(), 0);

        distinctHLLAna.init();
        Assert.assertTrue(distinctHLLAna.getResult().size() == 0);

    }

    protected static String streamElement(int i) {
        return Long.toHexString(prng.nextLong());
        // return se++;
    }

    private static Random prng = new Random();
}
