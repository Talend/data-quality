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

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StatisticsFrequencyTest {

    private DataTypeFrequencyStatistics stats;

    @Before
    public void setUp() throws Exception {
        stats = new DataTypeFrequencyStatistics();
        for (int i = 1; i <= 10; i++) {
            for (int j = 0; j < i; j++) {
                stats.add(Integer.toString(i));
            }
        }
    }

    @Test
    public void getTopK() {
        // when
        Map<String, Long> results = stats.getTopK(3);

        // expect
        Assert.assertEquals(3, results.keySet().size());
        Assert.assertEquals(Long.valueOf(10), results.get("10"));
        Assert.assertEquals(Long.valueOf(9), results.get("9"));
        Assert.assertEquals(Long.valueOf(8), results.get("8"));
    }
}
