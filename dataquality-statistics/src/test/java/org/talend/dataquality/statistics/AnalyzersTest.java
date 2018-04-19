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
package org.talend.dataquality.statistics;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.common.inference.Analyzers.Result;
import org.talend.dataquality.statistics.cardinality.CardinalityAnalyzer;
import org.talend.dataquality.statistics.cardinality.CardinalityStatistics;
import org.talend.dataquality.statistics.frequency.AbstractFrequencyStatistics;
import org.talend.dataquality.statistics.type.DataTypeAnalyzer;

public class AnalyzersTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnalyze() {
        Analyzer<Result> analyzer = Analyzers.with(new CardinalityAnalyzer(), new DataTypeAnalyzer());
        String[] data = new String[] { "0", "1", "2", "3", "16", "17", "18", "19", "19" };
        for (String r : data) {
            analyzer.analyze(r);
        }
        Assert.assertEquals(8, analyzer.getResult().get(0).get(CardinalityStatistics.class).getDistinctCount(), 0);
        Assert.assertEquals(1, analyzer.getResult().get(0).get(CardinalityStatistics.class).getDuplicateCount(), 0);
        Assert.assertFalse(analyzer.getResult().get(0).exist(AbstractFrequencyStatistics.class));
    }
}
