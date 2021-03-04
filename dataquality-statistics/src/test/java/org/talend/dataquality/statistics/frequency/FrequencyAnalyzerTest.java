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
package org.talend.dataquality.statistics.frequency;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FrequencyAnalyzerTest {

    private DataTypeFrequencyAnalyzer fta = null;

    @Before
    public void setUp() throws Exception {
        fta = new DataTypeFrequencyAnalyzer();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnalyze() {
        String[] data = new String[] { "a", "b", "a", "b", "a", "c", "1", "2", "3" };
        fta.init();
        for (String col : data) {
            fta.analyze(col);
        }
        Map<String, Long> tableFrequency = fta.getResult().get(0).getTopK(3);
        Iterator<Entry<String, Long>> entrySet = tableFrequency.entrySet().iterator();
        int idx = 0;
        while (entrySet.hasNext()) {
            Entry<String, Long> e = entrySet.next();
            if (idx == 0) {
                Assert.assertEquals("a", e.getKey());
                Assert.assertEquals(3, e.getValue(), 0);
            } else if (idx == 1) {
                Assert.assertEquals("b", e.getKey());
                Assert.assertEquals(2, e.getValue(), 0);
            }
            idx++;
        }
    }
}
