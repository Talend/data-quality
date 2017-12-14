package org.talend.dataquality.semantic.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.talend.dataquality.semantic.TestUtils.mockWithTenant;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.common.inference.Analyzers.Result;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.CustomDictionaryHolder;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;
import org.talend.dataquality.semantic.recognizer.CategoryFrequency;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TenancyContextHolder.class })
public class SemanticQualityAnalyzerTest extends CategoryRegistryManagerAbstract {

    private static final List<String[]> RECORDS_CRM_CUST = getRecords("crm_cust.csv");

    private static final List<String[]> RECORDS_CREDIT_CARDS = getRecords("credit_card_number_samples.csv");

    private static final List<String[]> RECORDS_PHONES = getRecords("phone_number.csv");

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

    private static DictionarySnapshot dictionarySnapshot;

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

    private final String[] EXPECTED_CATEGORIES_REGEX = new String[] { //
            "", //
            "VISA_CARD", //
            "DATA_URL", //
    };

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

    @Test
    public void testSemanticQualityAnalyzerWithDictionaryCategory() {
        mockWithTenant("testSemanticQualityAnalyzerWithDictionaryCategory");
        testAnalysis(RECORDS_CRM_CUST, EXPECTED_CATEGORIES_DICT, EXPECTED_VALIDITY_COUNT_DICT, EXPECTED_VALIDITY_COUNT_DICT);
    }

    @Test
    public void testSemanticQualityAnalyzerWithRegexCategory() {
        mockWithTenant("testSemanticQualityAnalyzerWithRegexCategory");
        testAnalysis(RECORDS_CREDIT_CARDS, EXPECTED_CATEGORIES_REGEX, EXPECTED_VALIDITY_COUNT_REGEX_FOR_DISCOVERY,
                EXPECTED_VALIDITY_COUNT_REGEX_FOR_VALIDATION);
    }

    @Test
    public void testSemanticQualityAnalyzerWithPhoneCategory() {
        mockWithTenant("testSemanticQualityAnalyzerWithPhoneCategory");
        testAnalysis(RECORDS_PHONES, new String[] { "PHONE" }, EXPECTED_VALIDITY_COUNT_PHONE, EXPECTED_VALIDITY_COUNT_PHONE);
    }

    @Test
    public void testMultiTenantIndex() throws IOException, URISyntaxException {
        mockWithTenant("testMultiTenantIndex");
        long[][] expectedCount = new long[][] { new long[] { 1, 0, 0 } };
        initTenantIndex(false);
        testAnalysis(Collections.singletonList(new String[] { "CDG" }),
                new String[] { SemanticCategoryEnum.AIRPORT_CODE.getId() }, expectedCount, expectedCount);
        testAnalysis(Collections.singletonList(new String[] { "AAAA" }),
                new String[] { SemanticCategoryEnum.AIRPORT_CODE.getId() }, expectedCount, expectedCount);
    }

    @Test
    public void testMultiTenantIndexWithoutExistingValues() throws IOException, URISyntaxException {
        mockWithTenant("testMultiTenantIndexWithoutExistingValues");
        long[][] expectedCount = new long[][] { new long[] { 1, 0, 0 } };
        initTenantIndex(true);
        testAnalysis(Collections.singletonList(new String[] { "CDG" }), new String[] { StringUtils.EMPTY }, expectedCount,
                expectedCount);
        testAnalysis(Collections.singletonList(new String[] { "AAAA" }),
                new String[] { SemanticCategoryEnum.AIRPORT_CODE.getId() }, expectedCount, expectedCount);
    }

    @Test
    public void testMultiTenantIndexWithDeletedCategory() throws IOException, URISyntaxException {
        mockWithTenant("testMultiTenantIndexWithDeletedCategory");
        long[][] expectedCount = new long[][] { new long[] { 1, 0, 0 } };
        testAnalysis(Collections.singletonList(new String[] { "Berulle" }),
                new String[] { SemanticCategoryEnum.FR_COMMUNE.getId() }, expectedCount, expectedCount);

        CustomDictionaryHolder holder = CategoryRegistryManager.getInstance().getCustomDictionaryHolder();
        DQCategory category = holder.getCategoryMetadataById(SemanticCategoryEnum.FR_COMMUNE.getTechnicalId());
        holder.deleteCategory(category);
        dictionarySnapshot = new StandardDictionarySnapshotProvider().get();
        testAnalysis(Collections.singletonList(new String[] { "Berulle" }), new String[] { StringUtils.EMPTY }, expectedCount,
                expectedCount);

    }

    @Test
    public void testCompoundCategoryWithSharedChildrenAndCustomChildren() throws IOException, URISyntaxException {
        mockWithTenant("testCompoundCategoryWithSharedChildrenAndCustomChildren");
        long[][] expectedCount = new long[][] { new long[] { 2, 0, 0 } };
        initCompound();
        initTenantIndex(true);
        testAnalysis(Arrays.asList(new String[] { "AAAA" }, new String[] { "Lagunillas" }), new String[] { "COMPOUND_NAME" },
                expectedCount, expectedCount);
    }

    public void testAnalysis(List<String[]> records, String[] expectedCategories, long[][] expectedValidityCountForDiscovery,
            long[][] expectedValidityCountForValidation) {
        dictionarySnapshot = new StandardDictionarySnapshotProvider().get();
        Analyzer<Result> analyzers = Analyzers.with(//
                new SemanticAnalyzer(dictionarySnapshot), //
                new SemanticQualityAnalyzer(dictionarySnapshot, expectedCategories)//
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
                    DQCategory cat = CategoryRegistryManager.getInstance().getCategoryMetadataByName(cf.getCategoryId());
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

    public void initTenantIndex(boolean removeCDG) throws IOException, URISyntaxException {

        Map<String, DQCategory> metadata = CategoryRegistryManager.getInstance().getCustomDictionaryHolder().getMetadata();

        DQCategory airportCategory = metadata.get(SemanticCategoryEnum.AIRPORT_CODE.getTechnicalId());

        if (removeCDG) {
            DQDocument cdgDoc = new DQDocument();
            cdgDoc.setId("5836fb7742b02a69874f8149");
            cdgDoc.setCategory(airportCategory);
            List<DQDocument> removeList = new ArrayList<>();
            removeList.add(cdgDoc);
            CategoryRegistryManager.getInstance().getCustomDictionaryHolder().deleteDataDictDocuments(removeList);
        }

        DQDocument doc1 = new DQDocument();
        doc1.setId("ID_DOCUMENT");
        doc1.setCategory(airportCategory);
        doc1.setValues(new HashSet<String>(Arrays.asList("AAAA", "BBBB")));
        DQDocument doc2 = new DQDocument();
        doc2.setId("ID_DOCUMENT2");
        doc2.setCategory(airportCategory);
        doc2.setValues(new HashSet<String>(Arrays.asList("CCCC", "DDDD")));

        List<DQDocument> addList = new ArrayList<>();
        addList.add(doc1);
        addList.add(doc2);
        CategoryRegistryManager.getInstance().getCustomDictionaryHolder().addDataDictDocuments(addList);

        dictionarySnapshot = new StandardDictionarySnapshotProvider().get();
    }

    public void initCompound() throws IOException, URISyntaxException {
        Map<String, DQCategory> metadata = CategoryRegistryManager.getInstance().getSharedCategoryMetadata();
        DQCategory compoundCategory = new DQCategory("COMPOUND_ID");
        compoundCategory.setName("COMPOUND_NAME");
        compoundCategory.setLabel("COMPOUND_LABEL");
        compoundCategory.setCompleteness(true);
        compoundCategory.setModified(true);
        compoundCategory.setType(CategoryType.COMPOUND);
        List<DQCategory> children = new ArrayList<>();
        DQCategory child1 = metadata.get(SemanticCategoryEnum.AIRPORT_CODE.getTechnicalId());
        child1.setParents(Collections.singletonList(compoundCategory));
        children.add(child1);
        DQCategory child2 = metadata.get(SemanticCategoryEnum.AIRPORT.getTechnicalId());
        child2.setParents(Collections.singletonList(compoundCategory));
        children.add(child2);
        compoundCategory.setChildren(children);

        CategoryRegistryManager.getInstance().getCustomDictionaryHolder().createCategory(compoundCategory);
        CategoryRegistryManager.getInstance().getCustomDictionaryHolder().updateCategory(child1);
        CategoryRegistryManager.getInstance().getCustomDictionaryHolder().updateCategory(child2);
    }

}
