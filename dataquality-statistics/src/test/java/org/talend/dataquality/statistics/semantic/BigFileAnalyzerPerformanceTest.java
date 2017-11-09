package org.talend.dataquality.statistics.semantic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.common.inference.Analyzers.Result;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.dataquality.semantic.statistics.SemanticAnalyzer;
import org.talend.dataquality.semantic.statistics.SemanticType;
import org.talend.dataquality.statistics.type.DataTypeAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeEnum;
import org.talend.dataquality.statistics.type.DataTypeOccurences;

public class BigFileAnalyzerPerformanceTest {

    private static Logger log = LoggerFactory.getLogger(BigFileAnalyzerPerformanceTest.class);

    private static CategoryRecognizerBuilder builder;

    private static final List<String[]> RECORDS_BIG_FILE = getRecords("big_file.csv");

    private static final DataTypeEnum[] EXPECTED_DATA_TYPE = new DataTypeEnum[] { //
            DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.DOUBLE,
            DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.INTEGER,
            DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.INTEGER,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DATE,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, };

    // Semantic types assertions
    private static final String[] EXPECTED_SEMANTIC_DOMAIN = new String[] { //
            "FR_POSTAL_CODE", "", "", "", "", "FR_POSTAL_CODE", "", "", "", "COUNTRY", // 10
            "", "", "", "", "", "", "", "", "", "", // 20
            "", "", "", "", "", "", "", "", "", "", // 30
            "", "", "", "", "", "", "", "", "", "", // 40
            "", "", "", "", "", "", "", "", "", "", // 50
            "", "", "", "", "", "", "", "", "", "", // 60
            "", "", "", "", "", "", "", "", "", "", // 70
            "", "", "", "", "", "", "", "", "", "", // 80
            "", "", "", "", "", "", "", "", "", "", // 90
            "", "", "", "", "", "", "", "", "", "", // 100
            "", "", "", "", "", "", "", "", "", "", // 110
            "", "", "", "", "", "", "", "", "", "", // 120
            "", "", "", "", "", "", "", "", "", "", // 130
            "", "", "", "", "", "", "", "", "", "", // 140
            "", "", "", "", "", "", "", "", "", "", // 150
            "", "", "", "", "", "", "", "", "", "", // 160
            "", "", "", "", "", "", "", "", "", "", // 170
            "", "", "", "", "", "", "", "", "", "", // 180
            "", "", "", "", "", "", "", "", "", "", // 190
            "", "", "", "", "", "", "", "", "", "", // 200
            "", "", "", "", "", "", "", "", "", "", // 210
            "", "", "", "", "", "", "", "", "", "", // 220
            "", "", "", "", "", "", "", "", "", "", // 230
            "", "", "", "", "", "", "", "", "", "", // 240
            "", "", "", "", "", "", "", "", "", "", // 250
            "", "", "", "", "", "", "", "", "", "", // 260
            "", "", "", "", "", "", "", "", "", "", // 270
            "", "", "", "", "", "", "", "", "", "", // 280
            "", "", "", "", "", "", "", "", "", };

    @BeforeClass
    public static void setupBuilder() throws URISyntaxException {
        builder = CategoryRecognizerBuilder.newBuilder().lucene();
    }

    private Analyzer<Result> setupBaselineAnalyzers(DataTypeEnum[] types) {
        // Analysis.QUALITY, Analysis.CARDINALITY, Analysis.TYPE, Analysis.FREQUENCY, Analysis.PATTERNS,
        // Analysis.SEMANTIC
        return Analyzers.with(//
                // new DataTypeQualityAnalyzer(types), //
                // new CardinalityAnalyzer(), //
                new DataTypeAnalyzer(), //
                // new DataTypeFrequencyAnalyzer(), //
                // new CompositePatternFrequencyAnalyzer(types), //
                new SemanticAnalyzer(builder) //
        );
    }

    @Test
    @Ignore
    public void testBaselineAnalysis() {
        Analyzer<Result> analyzers = setupBaselineAnalyzers(EXPECTED_DATA_TYPE);

        String[] firstRecord = RECORDS_BIG_FILE.get(0);
        analyzers.analyze(firstRecord);
        final long begin = System.currentTimeMillis();

        for (int i = 0; i < RECORDS_BIG_FILE.size(); i++) {
            if ((i + 1) % 1000 == 0) {
                System.out.println(i + 1);
            }
            final String[] record = RECORDS_BIG_FILE.get(i);
            analyzers.analyze(record);
        }
        final List<Analyzers.Result> result = analyzers.getResult();
        final long end = System.currentTimeMillis();
        log.info("The analyses took " + (end - begin) + " ms.");

        // Composite result assertions (there should be a DataType and a SemanticType)
        for (Analyzers.Result columnResult : result) {
            assertNotNull(columnResult.get(DataTypeOccurences.class));
            assertNotNull(columnResult.get(SemanticType.class));
        }

        assertEquals(EXPECTED_DATA_TYPE.length, result.size());
        // Data type assertions
        for (int i = 0; i < result.size(); i++) {
            // System.out.println("DataTypeEnum." + result.get(i).get(DataTypeOccurences.class).getSuggestedType() + ", ");
            assertEquals("Unexpected DataType on column " + i, EXPECTED_DATA_TYPE[i],
                    result.get(i).get(DataTypeOccurences.class).getSuggestedType());
        }

        assertEquals(EXPECTED_SEMANTIC_DOMAIN.length, result.size());
        for (int i = 0; i < result.size(); i++) {
            System.out.print("\"" + result.get(i).get(SemanticType.class).getSuggestedCategory() + "\", ");
            if ((i + 1) % 10 == 0) {
                System.out.println("// " + (i + 1));
            }
            assertEquals("Unexpected SemanticType on column " + i, EXPECTED_SEMANTIC_DOMAIN[i],
                    result.get(i).get(SemanticType.class).getSuggestedCategory());
        }
    }

    private static List<String[]> getRecords(String path) {
        List<String[]> records = new ArrayList<String[]>();
        try {
            Reader reader = new FileReader(BigFileAnalyzerPerformanceTest.class.getResource(path).getPath());
            CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader();
            Iterable<CSVRecord> csvRecords = csvFormat.parse(reader);

            for (CSVRecord csvRecord : csvRecords) {
                String[] values = new String[csvRecord.size()];
                for (int i = 0; i < csvRecord.size(); i++) {
                    values[i] = csvRecord.get(i);
                }
                records.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}
