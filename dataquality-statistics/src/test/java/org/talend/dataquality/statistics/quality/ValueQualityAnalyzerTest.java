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
package org.talend.dataquality.statistics.quality;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.talend.dataquality.statistics.type.DataTypeEnum.STRING;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.common.inference.QualityAnalyzer;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class ValueQualityAnalyzerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValueQualityAnalyzerTest.class);

    public static List<String[]> getRecords(InputStream inputStream, String separator) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null.");
        }
        try {
            List<String[]> records = new ArrayList<>();
            final List<String> lines = IOUtils.readLines(inputStream);
            for (String line : lines) {
                String[] record = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, separator);
                records.add(record);
            }
            return records;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Silent ignore
                e.printStackTrace();
            }
        }

    }

    public static List<String[]> getRecords(InputStream inputStream) {
        return getRecords(inputStream, ";");
    }

    @Test
    public void testValueQualityAnalyzerWithoutSemanticQuality() {

        DataTypeQualityAnalyzer dataTypeQualityAnalyzer =
                new DataTypeQualityAnalyzer(DataTypeEnum.INTEGER, STRING, STRING, STRING, DataTypeEnum.DATE, STRING,
                        DataTypeEnum.DATE, DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE);

        QualityAnalyzer analyzer = Mockito.mock(QualityAnalyzer.class);
        when(analyzer.getTypes()).thenReturn(new String[] { "UNKNOWN", "UNKNOWN", "UNKNOWN", "UNKNOWN", "UNKNOWN",
                "UNKNOWN", "UNKNOWN", "UNKNOWN", "UNKNOWN" });
        ValueQualityAnalyzer valueQualityAnalyzer = new ValueQualityAnalyzer(dataTypeQualityAnalyzer, analyzer);
        valueQualityAnalyzer.init();

        final List<String[]> records = getRecords(this.getClass().getResourceAsStream("../data/customers_100.csv"));

        for (String[] record : records) {
            valueQualityAnalyzer.analyze(record);
        }

        for (int i = 0; i < dataTypeQualityAnalyzer.getResult().size(); i++) {
            ValueQualityStatistics dataTypeQualityResult = dataTypeQualityAnalyzer.getResult().get(i);
            ValueQualityStatistics aggregatedResult = valueQualityAnalyzer.getResult().get(i);
            assertEquals("unexpected ValidCount on Column " + i, dataTypeQualityResult.getValidCount(),
                    aggregatedResult.getValidCount());
            assertEquals("unexpected InvalidCount on Column " + i, dataTypeQualityResult.getInvalidCount(),
                    aggregatedResult.getInvalidCount());
            assertEquals("unexpected EmptyCount on Column " + i, dataTypeQualityResult.getEmptyCount(),
                    aggregatedResult.getEmptyCount());
        }

        try {
            valueQualityAnalyzer.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Test
    public void valueQualityResultWithoutSemantic() {
        ValueQualityStatistics expectedResult = new ValueQualityStatistics();
        expectedResult.setInvalidCount(1);
        expectedResult.setValidCount(234);
        expectedResult.setUnknownCount(345);

        List<ValueQualityStatistics> expectedResults = new ArrayList<>();
        expectedResults.add(expectedResult);

        DataTypeQualityAnalyzer dataTypeQualityAnalyzer = Mockito.mock(DataTypeQualityAnalyzer.class);
        when(dataTypeQualityAnalyzer.getResult()).thenReturn(expectedResults);

        ValueQualityAnalyzer valueQualityAnalyzer = new ValueQualityAnalyzer(dataTypeQualityAnalyzer, null);

        List<ValueQualityStatistics> results = valueQualityAnalyzer.getResult();
        assertEquals(expectedResults.size(), results.size());

        ValueQualityStatistics statistics = results.get(0);
        assertEquals(1, statistics.getInvalidCount());
        assertEquals(234, statistics.getValidCount());
        assertEquals(345, statistics.getUnknownCount());

    }

    @Test
    public void valueQualityResultWithSemantic() {
        ValueQualityStatistics expectedDataTypeResult = new ValueQualityStatistics();
        expectedDataTypeResult.setInvalidCount(1);
        expectedDataTypeResult.setValidCount(234);
        expectedDataTypeResult.setUnknownCount(345);

        ValueQualityStatistics expectedDataTypeResultTwo = new ValueQualityStatistics();
        expectedDataTypeResultTwo.setInvalidCount(2);
        expectedDataTypeResultTwo.setValidCount(456);
        expectedDataTypeResultTwo.setUnknownCount(789);

        List<ValueQualityStatistics> expectedDataTypeResults = new ArrayList<>();
        expectedDataTypeResults.add(expectedDataTypeResult);
        expectedDataTypeResults.add(expectedDataTypeResultTwo);

        DataTypeQualityAnalyzer dataTypeQualityAnalyzer = Mockito.mock(DataTypeQualityAnalyzer.class);
        when(dataTypeQualityAnalyzer.getResult()).thenReturn(expectedDataTypeResults);

        ValueQualityStatistics expectedSemanticResult = new ValueQualityStatistics();
        expectedSemanticResult.setInvalidCount(14);
        expectedSemanticResult.setValidCount(7683);
        expectedSemanticResult.setUnknownCount(9874);

        List<ValueQualityStatistics> expectedSemanticResults = new ArrayList<>();
        expectedSemanticResults.add(expectedSemanticResult);

        QualityAnalyzer analyzer = Mockito.mock(QualityAnalyzer.class);
        when(analyzer.getTypes()).thenReturn(new String[] { "FR COMMUNE", "UNKNOWN" });
        when(analyzer.getResult()).thenReturn(expectedSemanticResults);

        ValueQualityAnalyzer valueQualityAnalyzer = new ValueQualityAnalyzer(dataTypeQualityAnalyzer, analyzer);

        List<ValueQualityStatistics> results = valueQualityAnalyzer.getResult();
        assertEquals(2, results.size());

        ValueQualityStatistics semanticStatistics = results.get(0);
        assertEquals(14, semanticStatistics.getInvalidCount());
        assertEquals(7683, semanticStatistics.getValidCount());
        assertEquals(9874, semanticStatistics.getUnknownCount());

        ValueQualityStatistics dataTypeStatistics = results.get(1);
        assertEquals(2, dataTypeStatistics.getInvalidCount());
        assertEquals(456, dataTypeStatistics.getValidCount());
        assertEquals(789, dataTypeStatistics.getUnknownCount());
    }

    @Test
    public void testClose() throws Exception {
        DataTypeQualityAnalyzer dataTypeQualityAnalyzer = Mockito.mock(DataTypeQualityAnalyzer.class);
        QualityAnalyzer analyzer = Mockito.mock(QualityAnalyzer.class);
        ValueQualityAnalyzer valueQualityAnalyzer = new ValueQualityAnalyzer(dataTypeQualityAnalyzer, analyzer);

        valueQualityAnalyzer.close();

        verify(dataTypeQualityAnalyzer, times(1)).close();
        verify(analyzer, times(1)).close();
    }
}
