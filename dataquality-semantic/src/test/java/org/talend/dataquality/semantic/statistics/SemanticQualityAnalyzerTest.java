package org.talend.dataquality.semantic.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.common.inference.Analyzers.Result;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.recognizer.CategoryFrequency;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;

public class SemanticQualityAnalyzerTest {

    private static CategoryRecognizerBuilder builder;

    private static final List<String[]> RECORDS_CRM_CUST = getRecords("crm_cust.csv");

    private static final List<String[]> RECORDS_CREDIT_CARDS = getRecords("credit_card_number_samples.csv");

    private static final List<String[]> RECORDS_PHONES = getRecords("phone_number.csv");

    public static final CategoryRegistryManager crm = CategoryRegistryManager.getInstance();

    private final String[] EXPECTED_CATEGORIES_DICT = new String[] { //
            "", //
            "CIVILITY", //
            "FIRST_NAME", //
            "LAST_NAME", //
            "COUNTRY_CODE_ISO3", //
            "ADDRESS_LINE", //
            "FR_POSTAL_CODE", //
            "CITY", //
            "", //
            "EMAIL", //
            "", //
            "", //
    };

    private static final long[][] EXPECTED_VALIDITY_COUNT_DICT = new long[][] { //
            new long[] { 1000, 0, 0 }, //
            new long[] { 1000, 0, 0 }, //
            new long[] { 1000, 0, 0 }, //
            new long[] { 1000, 0, 0 }, //
            new long[] { 990, 10, 0 }, //
            new long[] { 1000, 0, 0 }, //
            new long[] { 996, 4, 0 }, //
            new long[] { 1000, 0, 0 }, //
            new long[] { 518, 0, 482 }, //
            new long[] { 996, 4, 0 }, //
            new long[] { 1000, 0, 0 }, //
            new long[] { 1000, 0, 0 }, //
    };

    private final String[] EXPECTED_CATEGORIES_REGEX = new String[] { //
            "", //
            "VISA_CARD", //
            "DATA_URL", //
    };

    private static final long[][] EXPECTED_VALIDITY_COUNT_REGEX_FOR_DISCOVERY = new long[][] { //
            new long[] { 30, 0, 0 }, //
            new long[] { 20, 10, 0 }, //
            new long[] { 30, 0, 0 }, //
    };

    private static final long[][] EXPECTED_VALIDITY_COUNT_REGEX_FOR_VALIDATION = new long[][] { //
            new long[] { 30, 0, 0 }, //
            new long[] { 20, 10, 0 }, //
            new long[] { 19, 11, 0 }, //
    };

    private static final long[][] EXPECTED_VALIDITY_COUNT_PHONE = new long[][] { //
            new long[] { 11, 0, 0 } };

    @BeforeClass
    public static void setupBuilder() throws URISyntaxException {
        final URI ddPath = SemanticQualityAnalyzerTest.class.getResource(CategoryRecognizerBuilder.DEFAULT_DD_PATH).toURI();
        final URI kwPath = SemanticQualityAnalyzerTest.class.getResource(CategoryRecognizerBuilder.DEFAULT_KW_PATH).toURI();
        builder = CategoryRecognizerBuilder.newBuilder() //
                .ddPath(ddPath) //
                .kwPath(kwPath) //
                .lucene();
    }

    @Test
    public void testSemanticQualityAnalyzerWithDictionaryCategory() {
        testAnalysis(RECORDS_CRM_CUST, EXPECTED_CATEGORIES_DICT, EXPECTED_VALIDITY_COUNT_DICT, EXPECTED_VALIDITY_COUNT_DICT);
    }

    @Test
    public void testSemanticQualityAnalyzerWithRegexCategory() {
        testAnalysis(RECORDS_CREDIT_CARDS, EXPECTED_CATEGORIES_REGEX, EXPECTED_VALIDITY_COUNT_REGEX_FOR_DISCOVERY,
                EXPECTED_VALIDITY_COUNT_REGEX_FOR_VALIDATION);
    }

    @Test
    public void testSemanticQualityAnalyzerWithPhoneCategory() {
        testAnalysis(RECORDS_PHONES, new String[] { "PHONE" }, EXPECTED_VALIDITY_COUNT_PHONE, EXPECTED_VALIDITY_COUNT_PHONE);
    }

    public void testAnalysis(List<String[]> records, String[] expectedCategories, long[][] expectedValidityCountForDiscovery,
            long[][] expectedValidityCountForValidation) {
        Analyzer<Result> analyzers = Analyzers.with(//
                new SemanticAnalyzer(builder), //
                new SemanticQualityAnalyzer(builder, expectedCategories)//
        );

        for (String[] record : records) {
            analyzers.analyze(record);
        }
        final List<Analyzers.Result> result = analyzers.getResult();

        assertEquals(expectedCategories.length, result.size());

        // Composite result assertions (there should be a DataType and a SemanticType)
        for (Analyzers.Result columnResult : result) {
            assertNotNull(columnResult.get(SemanticType.class));
            assertNotNull(columnResult.get(ValueQualityStatistics.class));
        }

        // Semantic types assertions
        for (int i = 0; i < expectedCategories.length; i++) {
            final SemanticType stats = result.get(i).get(SemanticType.class);
            // System.out.println("\"" + stats.getSuggestedCategory() + "\", //");
            assertEquals("Unexpected SemanticType on column " + i, expectedCategories[i],
                    result.get(i).get(SemanticType.class).getSuggestedCategory());
            for (CategoryFrequency cf : stats.getCategoryToCount().keySet()) {
                if (expectedCategories[i].equals(cf.getCategoryId())) {
                    // System.out.println(stats.getCategoryToCount().get(cf));
                    DQCategory cat = crm.getCategoryMetadataByName(cf.getCategoryId());
                    if (cat != null && cat.getCompleteness()) {
                        assertEquals("Unexpected SemanticType occurence on column " + i, expectedValidityCountForDiscovery[i][0],
                                cf.getCount());
                    }
                }
            }
        }

        // Semantic validation assertions
        for (int i = 0; i < expectedCategories.length; i++) {
            final ValueQualityStatistics stats = result.get(i).get(ValueQualityStatistics.class);
            // System.out.println("new long[] {" + stats.getValidCount() + ", " + stats.getInvalidCount() + ", "
            // + stats.getEmptyCount() + "}, //");
            assertEquals("Unexpected valid count on column " + i, expectedValidityCountForValidation[i][0],
                    stats.getValidCount());
            assertEquals("Unexpected invalid count on column " + i, expectedValidityCountForValidation[i][1],
                    stats.getInvalidCount());
            assertEquals("Unexpected empty count on column " + i, expectedValidityCountForValidation[i][2],
                    stats.getEmptyCount());
            assertEquals("Unexpected unknown count on column " + i, 0, stats.getUnknownCount());
        }
    }

    private static List<String[]> getRecords(String path) {
        List<String[]> records = new ArrayList<String[]>();
        try {
            Reader reader = new FileReader(SemanticQualityAnalyzerTest.class.getResource(path).getPath());
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
