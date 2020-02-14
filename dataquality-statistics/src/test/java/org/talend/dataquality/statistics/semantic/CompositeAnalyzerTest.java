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
package org.talend.dataquality.statistics.semantic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.statistics.cardinality.CardinalityAnalyzer;
import org.talend.dataquality.statistics.cardinality.CardinalityStatistics;
import org.talend.dataquality.statistics.type.DataTypeAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeEnum;
import org.talend.dataquality.statistics.type.DataTypeOccurences;

public class CompositeAnalyzerTest extends SemanticStatisticsTestBase {

    Analyzer<Analyzers.Result> analyzer = null;

    @BeforeClass
    public static void before() {
    }

    @Before
    public void setUp() {
        analyzer = Analyzers.with(new DataTypeAnalyzer(), new CardinalityAnalyzer());
    }

    @After
    public void tearDown() {
        analyzer.end();
    }

    @Test
    public void testDataTypeAndSemantic() {
        final List<String[]> records =
                getRecords(SemanticStatisticsTestBase.class.getResourceAsStream("employee_100.csv"));
        for (String[] record : records) {
            analyzer.analyze(record);
        }
        final List<Analyzers.Result> result = analyzer.getResult();
        assertEquals(18, result.size());
        // Composite result assertions (there should be a DataType and a SemanticType)
        for (Analyzers.Result columnResult : result) {
            assertNotNull(columnResult.get(DataTypeOccurences.class));
            assertNotNull(columnResult.get(CardinalityStatistics.class));
        }
        // Data type assertions
        assertEquals(DataTypeEnum.INTEGER, result.get(0).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(1).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(2).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(3).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(4).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.INTEGER, result.get(5).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(6).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.INTEGER, result.get(7).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.INTEGER, result.get(8).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.DATE, result.get(9).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.DATE, result.get(10).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(11).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.DOUBLE, result.get(12).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.INTEGER, result.get(13).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(14).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(15).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(16).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(17).get(DataTypeOccurences.class).getSuggestedType());

        assertEquals(100, result.get(0).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(100, result.get(1).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(86, result.get(2).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(80, result.get(3).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(71, result.get(4).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(18, result.get(5).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(18, result.get(6).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(25, result.get(7).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(12, result.get(8).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(51, result.get(9).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(7, result.get(10).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(1, result.get(11).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(38, result.get(12).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(19, result.get(13).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(5, result.get(14).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(3, result.get(15).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(2, result.get(16).get(CardinalityStatistics.class).getDistinctCount());
        assertEquals(5, result.get(17).get(CardinalityStatistics.class).getDistinctCount());

        for (int i = 0; i < 18; i++) {
            assertEquals(100, result.get(i).get(CardinalityStatistics.class).getCount());
        }
        /*
        // Semantic types assertions
        String[] expectedCategories = new String[] { "", //
                "", //
                SemanticCategoryEnum.FIRST_NAME.getId(), //
                SemanticCategoryEnum.FIRST_NAME.getId(), //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                SemanticCategoryEnum.GENDER.getId(), //
                "" //
        };
        for (int i = 0; i < expectedCategories.length; i++) {
            assertEquals(expectedCategories[i], result.get(i).get(SemanticType.class).getSuggestedCategory());
        }*/
    }
}
