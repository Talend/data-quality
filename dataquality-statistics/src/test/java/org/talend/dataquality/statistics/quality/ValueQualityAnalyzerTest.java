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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.dataquality.semantic.statistics.SemanticQualityAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class ValueQualityAnalyzerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValueQualityAnalyzerTest.class);

    private static final String LOCAL_CATEGORY_REGISTRY_PATH = "target/test_registry";

    @BeforeClass
    public static void setCategoryRegistryPath() {
        CategoryRegistryManager.setLocalRegistryPath(LOCAL_CATEGORY_REGISTRY_PATH);
    }

    @AfterClass
    public static void tearDown() throws IOException {
        CategoryRegistryManager.reset();
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

    private CategoryRecognizerBuilder createCategoryRecognizerBuilder() throws URISyntaxException {
        final URI ddPath = this.getClass().getResource(CategoryRecognizerBuilder.DEFAULT_DD_PATH).toURI();
        final URI kwPath = this.getClass().getResource(CategoryRecognizerBuilder.DEFAULT_KW_PATH).toURI();
        final CategoryRecognizerBuilder builder = CategoryRecognizerBuilder.newBuilder() //
                .ddPath(ddPath) //
                .kwPath(kwPath) //
                .lucene();
        return builder;
    }

    @Test
    public void testValueQualityAnalyzerWithoutSemanticQuality() throws URISyntaxException {

        DataTypeQualityAnalyzer dataTypeQualityAnalyzer = new DataTypeQualityAnalyzer(
                new DataTypeEnum[] { DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                        DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE });
        String[] semanticTypes = new String[] { SemanticCategoryEnum.UNKNOWN.name(), SemanticCategoryEnum.UNKNOWN.name(),
                SemanticCategoryEnum.UNKNOWN.name(), SemanticCategoryEnum.UNKNOWN.name(), SemanticCategoryEnum.UNKNOWN.name(),
                SemanticCategoryEnum.UNKNOWN.name(), SemanticCategoryEnum.UNKNOWN.name(), SemanticCategoryEnum.UNKNOWN.name(),
                SemanticCategoryEnum.UNKNOWN.name() };
        SemanticQualityAnalyzer semanticQualityAnalyzer = new SemanticQualityAnalyzer(createCategoryRecognizerBuilder(),
                semanticTypes);

        ValueQualityAnalyzer valueQualityAnalyzer = new ValueQualityAnalyzer(dataTypeQualityAnalyzer, semanticQualityAnalyzer);
        valueQualityAnalyzer.init();

        final List<String[]> records = getRecords(this.getClass().getResourceAsStream("../data/customers_100.csv"));

        for (String[] record : records) {
            valueQualityAnalyzer.analyze(record);
        }

        for (int i = 0; i < semanticTypes.length; i++) {
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
    public void testValueQualityAnalyzerWithSemanticQuality() throws URISyntaxException {

        final List<String[]> records = new ArrayList<String[]>() {

            private static final long serialVersionUID = 1L;

            {
                add(new String[] { "1", "UT", "Bonn" });
                add(new String[] { "2", "MN", "Suresnes" });
                add(new String[] { "3", "MO", "Beijing" });
                add(new String[] { "4", "", "Washington" });
                add(new String[] { "5", "IL", "Tokyo" });
                add(new String[] { "6", "ORZ", "" });
                add(new String[] { "7", " ", "CityA" });
                add(new String[] { "8", "LOL", "CityB" });
            }
        };

        final int[] EXPECTED_VALID_COUNT = { 8, 4, 7 };
        final int[] EXPECTED_EMPTY_COUNT = { 0, 2, 1 };
        final int[] EXPECTED_INVALID_COUNT = { 0, 2, 0 };
        final int[] EXPECTED_UNKNOWN_COUNT = { 0, 0, 0 };
        final List<Set<String>> EXPECTED_INVALID_VALUES = new ArrayList<Set<String>>() {

            private static final long serialVersionUID = 1L;

            {
                add(new HashSet<String>());
                add(new HashSet<String>() {

                    private static final long serialVersionUID = 1L;

                    {
                        add("LOL");
                        add("ORZ");
                    }
                });
                add(new HashSet<String>());
            }
        };
        final List<Set<String>> EXPECTED_UNKNOWN_VALUES = new ArrayList<Set<String>>() {

            private static final long serialVersionUID = 1L;

            {
                add(new HashSet<String>());
                add(new HashSet<String>());
                add(new HashSet<String>());
            }
        };

        final DataTypeQualityAnalyzer dataTypeQualityAnalyzer = new DataTypeQualityAnalyzer(
                new DataTypeEnum[] { DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING });

        final String[] semanticTypes = new String[] { SemanticCategoryEnum.UNKNOWN.name(),
                SemanticCategoryEnum.US_STATE_CODE.name(), SemanticCategoryEnum.CITY.name() };

        final SemanticQualityAnalyzer semanticQualityAnalyzer = new SemanticQualityAnalyzer(createCategoryRecognizerBuilder(),
                semanticTypes);

        final ValueQualityAnalyzer valueQualityAnalyzer = new ValueQualityAnalyzer(dataTypeQualityAnalyzer,
                semanticQualityAnalyzer);
        valueQualityAnalyzer.init();

        for (String[] record : records) {
            valueQualityAnalyzer.analyze(record);
        }

        for (int i = 0; i < EXPECTED_INVALID_VALUES.size(); i++) {
            ValueQualityStatistics aggregatedResult = valueQualityAnalyzer.getResult().get(i);
            assertEquals("unexpected ValidCount on Column " + i, EXPECTED_VALID_COUNT[i], aggregatedResult.getValidCount());
            assertEquals("unexpected EmptyCount on Column " + i, EXPECTED_EMPTY_COUNT[i], aggregatedResult.getEmptyCount());
            assertEquals("unexpected InvalidCount on Column " + i, EXPECTED_INVALID_COUNT[i], aggregatedResult.getInvalidCount());
            assertEquals("unexpected InvalidValues on Column " + i, EXPECTED_INVALID_VALUES.get(i),
                    aggregatedResult.getInvalidValues());
            assertEquals("unexpected UnknownCount on Column " + i, EXPECTED_UNKNOWN_COUNT[i], aggregatedResult.getUnknownCount());
            assertEquals("unexpected UnknownValues on Column " + i, EXPECTED_UNKNOWN_VALUES.get(i),
                    aggregatedResult.getUnknownValues());
        }

        try {
            valueQualityAnalyzer.close();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
