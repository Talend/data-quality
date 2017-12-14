package org.talend.dataquality.statistics.semantic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
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
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;
import org.talend.dataquality.semantic.statistics.SemanticAnalyzer;
import org.talend.dataquality.semantic.statistics.SemanticType;
import org.talend.dataquality.statistics.cardinality.CardinalityAnalyzer;
import org.talend.dataquality.statistics.frequency.DataTypeFrequencyAnalyzer;
import org.talend.dataquality.statistics.frequency.pattern.CompositePatternFrequencyAnalyzer;
import org.talend.dataquality.statistics.numeric.quantile.QuantileAnalyzer;
import org.talend.dataquality.statistics.numeric.summary.SummaryAnalyzer;
import org.talend.dataquality.statistics.quality.DataTypeQualityAnalyzer;
import org.talend.dataquality.statistics.text.TextLengthAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeEnum;
import org.talend.dataquality.statistics.type.DataTypeOccurences;

public class AnalyzerPerformanceTest {

    private static Logger log = LoggerFactory.getLogger(AnalyzerPerformanceTest.class);

    private static DictionarySnapshot dictionarySnapshot;

    private static final List<String[]> records_card_exceptions = getRecords("Card_Exceptions_Preparation.csv");

    private final DataTypeEnum[] types_card_exceptions = new DataTypeEnum[] { //
            DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, //
            DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.INTEGER, DataTypeEnum.TIME, //
            DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, //
            DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DATE, DataTypeEnum.STRING,//
    };

    @BeforeClass
    public static void setupBuilder() throws URISyntaxException {
        dictionarySnapshot = new StandardDictionarySnapshotProvider().get();
    }

    private Analyzer<Result> setupBaselineAnalyzers(DataTypeEnum[] types) {
        // Analysis.QUALITY, Analysis.CARDINALITY, Analysis.TYPE, Analysis.FREQUENCY, Analysis.PATTERNS,
        // Analysis.SEMANTIC
        return Analyzers.with(//
                new DataTypeQualityAnalyzer(types), //
                new CardinalityAnalyzer(), //
                new DataTypeAnalyzer(), //
                new DataTypeFrequencyAnalyzer(), //
                new CompositePatternFrequencyAnalyzer(types), //
                new SemanticAnalyzer(dictionarySnapshot) //
        );
    }

    private Analyzer<Result> setupAdvancedAnalyzers() {
        // Analysis.LENGTH, Analysis.QUANTILES, Analysis.SUMMARY, Analysis.HISTOGRAM
        return Analyzers.with(//
                new TextLengthAnalyzer(), //
                new QuantileAnalyzer(types_card_exceptions), //
                new SummaryAnalyzer(types_card_exceptions) //
        );
    }

    @Test
    public void testBaselineAnalysis() {
        Analyzer<Result> analyzers = setupBaselineAnalyzers(types_card_exceptions);

        String[] firstRecord = records_card_exceptions.get(0);
        analyzers.analyze(firstRecord);
        final ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        final long cpuBefore = mxBean.getCurrentThreadCpuTime();
        for (String[] record : records_card_exceptions) {
            analyzers.analyze(record);
        }
        final List<Analyzers.Result> result = analyzers.getResult();
        final long cpuAfter = mxBean.getCurrentThreadCpuTime();
        log.info("baseline analysis took " + (cpuAfter - cpuBefore) + " CPU time.");
        assertTrue("baseline analysis took " + (cpuAfter - cpuBefore) + " CPU time, which is slower than expected.",
                (cpuAfter - cpuBefore) < 1.5e10);

        assertEquals(types_card_exceptions.length, result.size());

        // Composite result assertions (there should be a DataType and a SemanticType)
        for (Analyzers.Result columnResult : result) {
            assertNotNull(columnResult.get(DataTypeOccurences.class));
            assertNotNull(columnResult.get(SemanticType.class));
        }
        // Data type assertions
        for (int i = 0; i < types_card_exceptions.length; i++) {
            assertEquals("Unexpected DataType on column " + i, types_card_exceptions[i],
                    result.get(i).get(DataTypeOccurences.class).getSuggestedType());
        }

        // Semantic types assertions
        String[] expectedCategories = new String[] { "", //
                SemanticCategoryEnum.US_STATE_CODE.getId(), //
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
                "", //
                "", //
                "", //
                "", //
                "", //
                "" //
        };
        for (int i = 0; i < expectedCategories.length; i++) {
            assertEquals("Unexpected SemanticType on column " + i, expectedCategories[i],
                    result.get(i).get(SemanticType.class).getSuggestedCategory());
        }
    }

    @Test
    @Ignore
    public void testAdvancedAnalysis() {

        Analyzer<Result> analyzers = setupAdvancedAnalyzers();

        final ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        final long cpuBefore = mxBean.getCurrentThreadCpuTime();
        for (String[] record : records_card_exceptions) {
            analyzers.analyze(record);
        }
        final List<Analyzers.Result> result = analyzers.getResult();
        final long cpuAfter = mxBean.getCurrentThreadCpuTime();
        log.info("advanced analysis took " + (cpuAfter - cpuBefore) + " CPU time.");
        assertTrue("advanced analysis took " + (cpuAfter - cpuBefore) + " CPU time, which is slower than expected.",
                (cpuAfter - cpuBefore) < 7e8);
    }

    private static List<String[]> getRecords(String path) {
        List<String[]> records = new ArrayList<String[]>();
        try {
            Reader reader = new FileReader(AnalyzerPerformanceTest.class.getResource(path).getPath());
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
