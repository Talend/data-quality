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
package org.talend.dataquality.statistics.type;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * created by talend on 2015-07-28 Detailled comment.
 */
public class DataTypeAnalyzerTest extends DataTypeStatiticsTestBase {

    public DataTypeAnalyzer createDataTypeanalyzer() {
        DataTypeAnalyzer analyzer = new DataTypeAnalyzer();
        analyzer.init();
        return analyzer;
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testEmptyRecords() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        analyzer.analyze();
        assertEquals(0, analyzer.getResult().size());
        analyzer.analyze(null);
        assertEquals(0, analyzer.getResult().size());
        analyzer.analyze("");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.STRING, analyzer.getResult().get(0).getSuggestedType());
    }

    @Test
    public void testAnalysisResize() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        analyzer.analyze("aaaa");
        assertEquals(1, analyzer.getResult().size());
        analyzer.analyze("aaaa", "bbbb");
        assertEquals(2, analyzer.getResult().size());
    }

    @Test
    public void testString() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        // One string
        analyzer.analyze("aaaa");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.STRING, analyzer.getResult().get(0).getSuggestedType());
        // Two strings
        analyzer.analyze("bbbb");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.STRING, analyzer.getResult().get(0).getSuggestedType());
        // One integer
        analyzer.analyze("2");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.STRING, analyzer.getResult().get(0).getSuggestedType());
    }

    @Test
    public void testInteger() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        // One integer
        analyzer.analyze("0");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.INTEGER, analyzer.getResult().get(0).getSuggestedType());
        // Two integers
        analyzer.analyze("1");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.INTEGER, analyzer.getResult().get(0).getSuggestedType());
        // One string
        analyzer.analyze("aaaaa");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.INTEGER, analyzer.getResult().get(0).getSuggestedType());
    }

    @Test
    @Ignore
    public void testIncorrectCharDetection() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        // One character
        analyzer.analyze("M");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.STRING, analyzer.getResult().get(0).getSuggestedType());
        // Two characters
        analyzer.analyze("M");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.STRING, analyzer.getResult().get(0).getSuggestedType());
        // The new value should invalidate previous assumptions about CHAR value
        // (no longer a CHAR).
        analyzer.analyze("Mme");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.STRING, analyzer.getResult().get(0).getSuggestedType());
    }

    @Test
    public void testBoolean() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        // One boolean
        analyzer.analyze("true");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.BOOLEAN, analyzer.getResult().get(0).getSuggestedType());
        // Two booleans
        analyzer.analyze("false");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.BOOLEAN, analyzer.getResult().get(0).getSuggestedType());
        // One string
        analyzer.analyze("aaaaa");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.BOOLEAN, analyzer.getResult().get(0).getSuggestedType());
    }

    @Test
    public void testMixedDoubleInteger() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        String[] toTestMoreDouble = { "1.2", "3.4E-10", "1" };
        for (String string : toTestMoreDouble) {
            analyzer.analyze(string);
        }
        assertEquals(DataTypeEnum.DOUBLE, analyzer.getResult().get(0).getSuggestedType());

        String[] toTestMoreInteger = { "1.2", "3.4E-10", "1", "3", "6", "80" };
        for (String string : toTestMoreInteger) {
            analyzer.analyze(string);
        }
        assertEquals(DataTypeEnum.DOUBLE, analyzer.getResult().get(0).getSuggestedType());

    }

    @Test
    public void testBigDouble_TDQ11763() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        analyzer.analyze("true", "265" + '\u00A0' + "435" + '\u2007' + "000" + '\u202F' + "000");
        assertEquals(2, analyzer.getResult().size());
        assertEquals(DataTypeEnum.BOOLEAN, analyzer.getResult().get(0).getSuggestedType());
        assertEquals(DataTypeEnum.DOUBLE, analyzer.getResult().get(1).getSuggestedType());
    }

    @Test
    public void testMultipleColumns() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        analyzer.analyze("true", "aaaa");
        analyzer.analyze("true", "bbbb");
        assertEquals(2, analyzer.getResult().size());
        assertEquals(DataTypeEnum.BOOLEAN, analyzer.getResult().get(0).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, analyzer.getResult().get(1).getSuggestedType());
    }

    @Test
    public void testInferDataTypes() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        final List<String[]> records = getRecords(DataTypeStatiticsTestBase.class.getResourceAsStream("employee_100.csv"));
        for (String[] record : records) {
            analyzer.analyze(record);
        }
        final List<DataTypeOccurences> result = analyzer.getResult();
        assertEquals(18, result.size());
        assertEquals(DataTypeEnum.INTEGER, result.get(0).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(1).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(2).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(3).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(4).getSuggestedType());
        assertEquals(DataTypeEnum.INTEGER, result.get(5).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(6).getSuggestedType());
        assertEquals(DataTypeEnum.INTEGER, result.get(7).getSuggestedType());
        assertEquals(DataTypeEnum.INTEGER, result.get(8).getSuggestedType());
        assertEquals(DataTypeEnum.DATE, result.get(9).getSuggestedType());
        assertEquals(DataTypeEnum.DATE, result.get(10).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(11).getSuggestedType());
        assertEquals(DataTypeEnum.DOUBLE, result.get(12).getSuggestedType());
        assertEquals(DataTypeEnum.INTEGER, result.get(13).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(14).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(15).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(16).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(17).getSuggestedType());
    }

    @Test
    public void testDateColumn() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        analyzer.analyze("10-Oct-2015");
        analyzer.analyze("11-Oct-2015");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataTypeEnum.DATE, analyzer.getResult().get(0).getSuggestedType());
    }

    /**
     * Test the order of column index to see whether it is same after the inferring type done comparing the before or
     * not.
     */
    @Test
    public void testInferTypesColumnIndexOrder() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        final List<String[]> records = getRecords(
                DataTypeStatiticsTestBase.class.getResourceAsStream("customers_100_bug_TDQ10380.csv"));
        for (String[] record : records) {
            analyzer.analyze(record);
        }
        final List<DataTypeOccurences> result = analyzer.getResult();
        assertEquals(DataTypeEnum.INTEGER, result.get(0).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(1).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(2).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(3).getSuggestedType());
        assertEquals(DataTypeEnum.DATE, result.get(4).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(5).getSuggestedType());
        assertEquals(DataTypeEnum.DATE, result.get(6).getSuggestedType());
        assertEquals(DataTypeEnum.DOUBLE, result.get(7).getSuggestedType());
        assertEquals(DataTypeEnum.DOUBLE, result.get(8).getSuggestedType());
    }

    @Test
    public void testGetDataTypeWithRatio() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        // the integer ratio is about 0.57 in this case
        String[] records = new String[] { "1", "2", "3", "4", "1.0", "2.0", "3.0" };
        for (String record : records) {
            analyzer.analyze(record);
        }
        analyzer.end();
        final List<DataTypeOccurences> result = analyzer.getResult();
        // the ratio 0.57 exceeds the default integer threshold 0.5, return INTEGER
        assertEquals(DataTypeEnum.DOUBLE, result.get(0).getSuggestedType());
    }

    @Test
    public void testGetDataTypeWithRatioEmpty() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        String[] records = new String[] { "", "", "", "", "1.0", "2.0", "3.0" };
        for (String record : records) {
            analyzer.analyze(record);
        }
        analyzer.end();
        final List<DataTypeOccurences> result = analyzer.getResult();
        assertEquals(DataTypeEnum.DOUBLE, result.get(0).getSuggestedType());
    }

    @Test
    public void testGetDataTypeWithRatioEmpty2() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        String[] records = new String[] { "1", "2", "", "", "1.0", "2.0" };
        for (String record : records) {
            analyzer.analyze(record);
        }
        analyzer.end();
        final List<DataTypeOccurences> result = analyzer.getResult();
        assertEquals(DataTypeEnum.DOUBLE, result.get(0).getSuggestedType());
    }

    @Test
    public void testGetDataTypeWithOnlyInteger() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        String[] records = new String[] { "1", "2", "" };
        for (String record : records) {
            analyzer.analyze(record);
        }
        analyzer.end();
        final List<DataTypeOccurences> result = analyzer.getResult();
        assertEquals(DataTypeEnum.INTEGER, result.get(0).getSuggestedType());
    }
}
