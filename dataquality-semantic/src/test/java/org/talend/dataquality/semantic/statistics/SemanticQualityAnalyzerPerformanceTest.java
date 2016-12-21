package org.talend.dataquality.semantic.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.common.inference.Analyzers.Result;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.semantic.index.utils.DictionaryGenerationSpec;
import org.talend.dataquality.semantic.index.utils.SemanticDictionaryGenerator;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;

import com.talend.csv.CSVWriter;

public class SemanticQualityAnalyzerPerformanceTest {

    private static CategoryRecognizerBuilder builder;

    private static int RECORD_LINES_NUMBER = 500000;

    private static final String BIG_FILE_PATH = "src/test/resources/org/talend/dataquality/semantic/statistics/validation_big_file.csv";

    private static final List<String[]> RECORDS_CRM_CUST = getRecords("validation_big_file.csv");

    private static final DictionaryGenerationSpec[] EXPECTED_CATEGORIES_DICT = new DictionaryGenerationSpec[] { //
            DictionaryGenerationSpec.AIRPORT_CODE, //
            DictionaryGenerationSpec.CIVILITY, //
            DictionaryGenerationSpec.CONTINENT, //
            DictionaryGenerationSpec.COUNTRY, //
            DictionaryGenerationSpec.COUNTRY_CODE_ISO3, //
            DictionaryGenerationSpec.MONTH, //
            DictionaryGenerationSpec.US_COUNTY, //
            DictionaryGenerationSpec.FR_COMMUNE, //
            DictionaryGenerationSpec.FR_DEPARTEMENT, //
            DictionaryGenerationSpec.LANGUAGE //
    };

    private static final long[][] EXPECTED_VALIDITY_COUNT_DICT = new long[][] { //
            new long[] { 9944, 0, 0 }, //
            new long[] { 9944, 0, 0 }, //
            new long[] { 9944, 0, 0 }, //
            new long[] { 9944, 0, 0 }, //
            new long[] { 9944, 0, 0 }, //
            new long[] { 9944, 0, 0 }, //
            new long[] { 9943, 1, 0 }, //
            new long[] { 9943, 0, 0 }, //
            new long[] { 9943, 0, 0 }, //
            new long[] { 9943, 0, 0 }, //
    };

    @BeforeClass
    public static void setupBuilder() throws URISyntaxException {
        final URI ddPath = SemanticQualityAnalyzerPerformanceTest.class.getResource(CategoryRecognizerBuilder.DEFAULT_DD_PATH)
                .toURI();
        final URI kwPath = SemanticQualityAnalyzerPerformanceTest.class.getResource(CategoryRecognizerBuilder.DEFAULT_KW_PATH)
                .toURI();
        builder = CategoryRecognizerBuilder.newBuilder() //
                .ddPath(ddPath) //
                .kwPath(kwPath) //
                .lucene();
    }

    @Test
    @Ignore
    public void testSemanticQualityAnalyzerWithDictionaryCategory() {
        String[] a = new String[EXPECTED_CATEGORIES_DICT.length];
        for (int i = 0; i < EXPECTED_CATEGORIES_DICT.length; i++) {
            a[i] = EXPECTED_CATEGORIES_DICT[i].getCategoryName();
        }
        testAnalysis(RECORDS_CRM_CUST, a, EXPECTED_VALIDITY_COUNT_DICT);
    }

    public void testAnalysis(List<String[]> records, String[] expectedCategories, long[][] expectedValidityCount) {
        Analyzer<Result> analyzers = Analyzers.with(//
                new SemanticQualityAnalyzer(builder, expectedCategories)//
        );

        long time = System.currentTimeMillis();
        for (String[] record : records) {
            analyzers.analyze(record);
        }
        final List<Result> result = analyzers.getResult();
        System.out.println("Result = " + (System.currentTimeMillis() - time) + " ms");

        assertEquals(expectedCategories.length, result.size());

        // Composite result assertions (there should be a DataType and a SemanticType)
        for (Result columnResult : result) {
            assertNotNull(columnResult.get(ValueQualityStatistics.class));
        }

        // Semantic validation assertions
        for (int i = 0; i < expectedCategories.length; i++) {
            final ValueQualityStatistics stats = result.get(i).get(ValueQualityStatistics.class);
            // System.out.println("new long[] {" + stats.getValidCount() + ", " + stats.getInvalidCount() + ", "
            // + stats.getEmptyCount() + "}, //");
            assertEquals("Unexpected valid count on column " + i, expectedValidityCount[i][0], stats.getValidCount());
            assertEquals("Unexpected invalid count on column " + i, expectedValidityCount[i][1], stats.getInvalidCount());
            assertEquals("Unexpected empty count on column " + i, expectedValidityCount[i][2], stats.getEmptyCount());
            assertEquals("Unexpected unknown count on column " + i, 0, stats.getUnknownCount());
        }
    }

    private static List<String[]> getRecords(String path) {
        List<String[]> records = new ArrayList<String[]>();
        try {
            Reader reader = new FileReader(SemanticQualityAnalyzerPerformanceTest.class.getResource(path).getPath());
            CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(';');
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

    // To generate a bigger validation_big_file.csv if necessary
    public static void main(String[] args) {

        try {
            final String resourcePath = SemanticDictionaryGenerator.class.getResource(".").getFile();
            final String projectRoot = new File(resourcePath).getParentFile().getParentFile().getParentFile().getParentFile()
                    .getParentFile().getParentFile().getParentFile().getParentFile().getPath() + File.separator;
            File f = new File(projectRoot + BIG_FILE_PATH);
            CSVWriter writer = new CSVWriter(new FileWriter(f));
            writer.setSeparator(';');
            List<String[]> records = new ArrayList<>();
            Random randomGenerator = new Random();
            for (int i = 0; i < RECORD_LINES_NUMBER; i++) {
                records.add(new String[EXPECTED_CATEGORIES_DICT.length]);
            }

            for (int j = 0; j < EXPECTED_CATEGORIES_DICT.length; j++) {
                List<String> file = getFile(EXPECTED_CATEGORIES_DICT[j]);
                for (int i = 0; i < RECORD_LINES_NUMBER; i++) {
                    records.get(i)[j] = file.get(randomGenerator.nextInt(file.size()));
                }
            }
            writer.writeAll(records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getFile(DictionaryGenerationSpec spec) throws IOException {

        Reader reader = new FileReader(
                SemanticQualityAnalyzerPerformanceTest.class.getResource("../index/utils/" + spec.getSourceFile()).getPath());
        CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(spec.getCsvConfig().getDelimiter());
        if (spec.getCsvConfig().isWithHeader()) {
            csvFormat = csvFormat.withFirstRecordAsHeader();
        }

        // collect values
        Iterable<CSVRecord> records = csvFormat.parse(reader);
        List<Set<String>> valueSetList = SemanticDictionaryGenerator.getDictionaryForCategory(records, spec);

        List<String> result = new ArrayList<>();
        for (Set<String> valueSet : valueSetList) {
            if (valueSet.iterator().hasNext())
                result.add(valueSet.iterator().next());
        }
        return result;
    }

}
