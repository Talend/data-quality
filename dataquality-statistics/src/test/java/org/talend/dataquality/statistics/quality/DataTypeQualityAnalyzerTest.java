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
package org.talend.dataquality.statistics.quality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class DataTypeQualityAnalyzerTest {

    private DataTypeQualityAnalyzer analyzer;

    private static void populateAnalyzeData(DataTypeQualityAnalyzer qualityAnalyzer) {
        qualityAnalyzer.analyze("1", "");
        qualityAnalyzer.analyze("2", "a");
        qualityAnalyzer.analyze("3", "2.0");
        qualityAnalyzer.analyze("str", "0.1");
        qualityAnalyzer.analyze("another str", "");
    }

    private static void populateAnalyzerNoneString(DataTypeQualityAnalyzer qualityAnalyzer) {
        qualityAnalyzer.analyze("1.0");
        qualityAnalyzer.analyze("0.02");
        qualityAnalyzer.analyze("2.88888888888888888888888");
        qualityAnalyzer.analyze("3");
        qualityAnalyzer.analyze("5538297118");
        qualityAnalyzer.analyze("str");
    }

    private static void populateAnalyzerHalfA(DataTypeQualityAnalyzer qualityAnalyzer) {
        qualityAnalyzer.analyze("1.0");
        qualityAnalyzer.analyze("0.02");
        qualityAnalyzer.analyze("2.88888888888888888888888");
    }

    private static void populateAnalyzerHalfB(DataTypeQualityAnalyzer qualityAnalyzer) {
        qualityAnalyzer.analyze("3");
        qualityAnalyzer.analyze("5538297118");
        qualityAnalyzer.analyze("str");
    }

    private static void populateAnalyzerWithNumbers(DataTypeQualityAnalyzer qualityAnalyzer) {
        qualityAnalyzer.analyze("1.0");
        qualityAnalyzer.analyze("0.02");
        qualityAnalyzer.analyze("2.88888888888888888888888");
        qualityAnalyzer.analyze("3.0");
        qualityAnalyzer.analyze("5538297118");
    }

    @Before
    public void setUp() {
        analyzer = new DataTypeQualityAnalyzer(DataTypeEnum.INTEGER);
        analyzer.init();
    }

    @After
    public void tearDown() {
        analyzer.end();
    }

    @Test
    public void testEmpty() throws Exception {
        // One empty record
        analyzer.analyze("");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(1, analyzer.getResult().get(0).getEmptyCount());
        assertEquals(0, analyzer.getResult().get(0).getInvalidCount());
        assertEquals(0, analyzer.getResult().get(0).getValidCount());
        // One new empty record
        analyzer.analyze("");
        assertEquals(2, analyzer.getResult().get(0).getEmptyCount());
        assertEquals(0, analyzer.getResult().get(0).getInvalidCount());
        assertEquals(0, analyzer.getResult().get(0).getValidCount());
    }

    @Test
    public void testValid() throws Exception {
        // One valid record (type is integer)
        analyzer.analyze("1");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(0, analyzer.getResult().get(0).getEmptyCount());
        assertEquals(0, analyzer.getResult().get(0).getInvalidCount());
        assertEquals(1, analyzer.getResult().get(0).getValidCount());
        // One new valid record (type is still integer)
        analyzer.analyze("2");
        assertEquals(0, analyzer.getResult().get(0).getEmptyCount());
        assertEquals(0, analyzer.getResult().get(0).getInvalidCount());
        assertEquals(2, analyzer.getResult().get(0).getValidCount());
    }

    @Test
    public void testInvalid() throws Exception {
        // One invalid record (type is integer)
        analyzer.analyze("aaaa");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(0, analyzer.getResult().get(0).getEmptyCount());
        assertEquals(1, analyzer.getResult().get(0).getInvalidCount());
        assertEquals(0, analyzer.getResult().get(0).getValidCount());
        // One new invalid record (type is integer)
        analyzer.analyze("bbbb");
        assertEquals(0, analyzer.getResult().get(0).getEmptyCount());
        assertEquals(2, analyzer.getResult().get(0).getInvalidCount());
        assertEquals(0, analyzer.getResult().get(0).getValidCount());
    }

    @Test
    public void testInvalidValues() {
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(DataTypeEnum.STRING);
        qualityAnalyzer.analyze("1");
        qualityAnalyzer.analyze("2");
        qualityAnalyzer.analyze("3");
        qualityAnalyzer.analyze("str");
        qualityAnalyzer.analyze("another str");
        // Valid and invalid
        assertEquals(0, qualityAnalyzer.getResult().get(0).getInvalidCount());
        assertEquals(5, qualityAnalyzer.getResult().get(0).getValidCount());
        // Invalid values
        Set<String> invalidValues = qualityAnalyzer.getResult().get(0).getInvalidValues();
        assertEquals(0, invalidValues.size());
    }

    @Test
    public void testTwoColumnsInvalid() {
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE);
        populateAnalyzeData(qualityAnalyzer);
        // --- Assert column 0
        ValueQualityStatistics valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(2, valueQuality.getInvalidCount());
        assertEquals(3, valueQuality.getValidCount());
        // Invalid values
        Set<String> invalidValues = valueQuality.getInvalidValues();
        assertTrue(invalidValues.contains("str"));
        assertTrue(invalidValues.contains("another str"));
        // ---Assert column 1
        valueQuality = qualityAnalyzer.getResult().get(1);
        // Valid , Empty, and invalid
        assertEquals(1, valueQuality.getInvalidCount());
        assertEquals(2, valueQuality.getValidCount());
        assertEquals(2, valueQuality.getEmptyCount());
        // Invalid values
        invalidValues = valueQuality.getInvalidValues();
        assertEquals(1, invalidValues.size());
        assertTrue(invalidValues.contains("a"));

    }

    @Test
    public void testTwoColumnsValidAndEmpty() throws Exception {
        // ---Assert when user set string type
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(DataTypeEnum.STRING, DataTypeEnum.STRING);
        populateAnalyzeData(qualityAnalyzer);
        ValueQualityStatistics valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(5, valueQuality.getValidCount());
        // Invalid values
        Set<String> invalidValues = valueQuality.getInvalidValues();
        assertEquals(0, invalidValues.size());

        // --- test when user set actual type to string
        valueQuality = qualityAnalyzer.getResult().get(1);
        // Valid , Empty, and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(3, valueQuality.getValidCount());
        assertEquals(2, valueQuality.getEmptyCount());
        // Invalid values
        invalidValues = valueQuality.getInvalidValues();
        assertEquals(0, invalidValues.size());
    }

    @Test
    public void testValidIntegers() {
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(DataTypeEnum.INTEGER);
        qualityAnalyzer.analyze("1");
        qualityAnalyzer.analyze("2");
        qualityAnalyzer.analyze("3");
        qualityAnalyzer.analyze("5538297118");

        // Valid and invalid
        ValueQualityStatistics valueQuality = qualityAnalyzer.getResult().get(0);
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(4, valueQuality.getValidCount());
        // Invalid values
        Set<String> invalidValues = valueQuality.getInvalidValues();
        assertEquals(0, invalidValues.size());
    }

    @Test
    public void testValidDoubles() {
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(DataTypeEnum.DOUBLE);

        String[] inputValues = new String[] { "5538297118", "1045.35", "1,045.35", "1,045", "1,045,350", "2.68435E+17",
                "268 435 000 000 000 000", "265" + '\u00A0' + "435" + '\u2007' + "000" + '\u202F' + "000" };

        for (String str : inputValues) {
            qualityAnalyzer.analyze(str);
        }
        // Valid and invalid
        ValueQualityStatistics valueQuality = qualityAnalyzer.getResult().get(0);
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(8, valueQuality.getValidCount());
        // Invalid values
        Set<String> invalidValues = valueQuality.getInvalidValues();
        assertEquals(0, invalidValues.size());
    }

    @Test
    public void testNoneStrings_double() {
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(DataTypeEnum.DOUBLE);
        populateAnalyzerNoneString(qualityAnalyzer);
        ValueQualityStatistics valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(1, valueQuality.getInvalidCount());
        assertEquals(5, valueQuality.getValidCount());
        // Invalid values
        Set<String> invalidValues = valueQuality.getInvalidValues();
        assertEquals(1, invalidValues.size());
        assertTrue(invalidValues.contains("str"));
    }

    @Test
    public void testNoneStrings_string() throws Exception {
        // ---- second pass when user set the user defined type as string---
        // Actual type
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(DataTypeEnum.STRING);
        populateAnalyzerNoneString(qualityAnalyzer);
        ValueQualityStatistics valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(6, valueQuality.getValidCount());
        // Invalid values
        Set<String> invalidValues = valueQuality.getInvalidValues();
        assertEquals(0, invalidValues.size());
    }

    @Test
    public void testNoneStrings_integer() {
        // ---- third pass when user set the user defined type as Integer---
        // Actual type
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(DataTypeEnum.INTEGER);
        populateAnalyzerNoneString(qualityAnalyzer);
        ValueQualityStatistics valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(4, valueQuality.getInvalidCount());
        assertEquals(2, valueQuality.getValidCount());
        // Invalid values
        Set<String> invalidValues = valueQuality.getInvalidValues();
        assertEquals(4, invalidValues.size());
        assertTrue(invalidValues.contains("1.0"));
        assertTrue(invalidValues.contains("0.02"));
        assertTrue(invalidValues.contains("2.88888888888888888888888"));
        assertTrue(invalidValues.contains("str"));
    }

    @Test
    public void testNumbers_double() {
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(DataTypeEnum.DOUBLE);
        populateAnalyzerWithNumbers(qualityAnalyzer);
        ValueQualityStatistics valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(5, valueQuality.getValidCount());
        // Invalid values
        Set<String> invalidValues = valueQuality.getInvalidValues();
        assertEquals(0, invalidValues.size());
    }

    @Test
    public void testNumbers_string() {
        // ---- send pass when user set the user defined type ---
        // Actual type
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(DataTypeEnum.STRING);
        populateAnalyzerWithNumbers(qualityAnalyzer);
        ValueQualityStatistics valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(5, valueQuality.getValidCount());
        // Invalid values
        Set<String> invalidValues = valueQuality.getInvalidValues();
        assertEquals(0, invalidValues.size());
    }

    @Test
    public void testEmptyOver() {
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(DataTypeEnum.STRING);
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze("1");
        qualityAnalyzer.analyze("a str");
        ValueQualityStatistics valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(2, valueQuality.getValidCount());
        assertEquals(4, valueQuality.getEmptyCount());
        // Invalid values
        Set<String> invalidValues = valueQuality.getInvalidValues();
        assertEquals(0, invalidValues.size());
    }

    @Test
    public void testEmptyAll() {
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(DataTypeEnum.STRING);
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze(null);
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze(" ");
        qualityAnalyzer.analyze("  ");
        ValueQualityStatistics valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(0, valueQuality.getValidCount());
        assertEquals(6, valueQuality.getEmptyCount());
        // Invalid values
        Set<String> invalidValues = valueQuality.getInvalidValues();
        assertEquals(0, invalidValues.size());
    }

    @Test
    public void testInvalidCount() {// for issue TDQ_10380
        DataTypeQualityAnalyzer qualityAnalyzer = new DataTypeQualityAnalyzer(
                new DataTypeEnum[] { DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                        DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE });

        final List<String[]> records = getRecords(this.getClass().getResourceAsStream("../data/customers_100.csv"));

        for (String[] record : records) {
            qualityAnalyzer.analyze(record);
        }

        ValueQualityStatistics result = qualityAnalyzer.getResult().get(7);
        assertEquals(6, result.getInvalidCount());
        assertEquals(4, result.getEmptyCount());
        assertEquals(90, result.getValidCount());
        assertEquals(100, result.getCount());
    }

    public static List<String[]> getRecords(InputStream inputStream, String separator) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null.");
        }
        try {
            List<String[]> records = new ArrayList<String[]>();
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
}
