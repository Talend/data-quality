package org.talend.dataquality.semantic.statistics;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.common.inference.Analyzers.Result;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.CustomDictionaryHolder;
import org.talend.dataquality.semantic.api.DictionaryUtils;
import org.talend.dataquality.semantic.broadcast.TdqCategories;
import org.talend.dataquality.semantic.broadcast.TdqCategoriesFactory;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.index.ClassPathDirectory;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.recognizer.CategoryFrequency;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder.DEFAULT_DD_PATH;

public class SemanticQualityAnalyzerTest extends CategoryRegistryManagerAbstract {

    public static final CategoryRegistryManager crm = CategoryRegistryManager.getInstance();

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

    private static CategoryRecognizerBuilder builder;

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

    @Before
    public void setupBuilder() throws URISyntaxException {
        builder = CategoryRecognizerBuilder.newBuilder().lucene();
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

    @Test
    public void testMultiTenantIndex() throws IOException, URISyntaxException {
        long[][] expectedCount = new long[][] { new long[] { 1, 0, 0 } };
        initTenantIndex(true);
        testAnalysis(Collections.singletonList(new String[] { "CDG" }),
                new String[] { SemanticCategoryEnum.AIRPORT_CODE.getId() }, expectedCount, expectedCount);
        testAnalysis(Collections.singletonList(new String[] { "AAAA" }),
                new String[] { SemanticCategoryEnum.AIRPORT_CODE.getId() }, expectedCount, expectedCount);
    }

    @Test
    public void testMultiTenantIndexWithoutExistingValues() throws IOException, URISyntaxException {
        long[][] expectedCount = new long[][] { new long[] { 1, 0, 0 } };
        initTenantIndex(false);
        testAnalysis(Collections.singletonList(new String[] { "CDG" }), new String[] { StringUtils.EMPTY }, expectedCount,
                expectedCount);
        testAnalysis(Collections.singletonList(new String[] { "AAAA" }),
                new String[] { SemanticCategoryEnum.AIRPORT_CODE.getId() }, expectedCount, expectedCount);
    }

    @Test
    public void testMultiTenantIndexWithDeletedCategory() throws IOException, URISyntaxException {
        long[][] expectedCount = new long[][] { new long[] { 1, 0, 0 } };
        testAnalysis(Collections.singletonList(new String[] { "Berulle" }),
                new String[] { SemanticCategoryEnum.FR_COMMUNE.getId() }, expectedCount, expectedCount);

        TdqCategories tdqCategories = TdqCategoriesFactory.createTdqCategories();
        builder = CategoryRecognizerBuilder.newBuilder().lucene()//
                .metadata(tdqCategories.getCategoryMetadata().getMetadata())//
                .ddDirectory(tdqCategories.getDictionary().asDirectory())//
                .kwDirectory(tdqCategories.getKeyword().asDirectory()) //
                .regexClassifier(tdqCategories.getRegex().getRegexClassifier());
        CustomDictionaryHolder holder = CategoryRegistryManager.getInstance().getCustomDictionaryHolder();

        DQCategory category = holder.getCategoryMetadataById(SemanticCategoryEnum.FR_COMMUNE.getTechnicalId());
        holder.deleteCategory(category);
        testAnalysis(Collections.singletonList(new String[] { "Berulle" }), new String[] { StringUtils.EMPTY }, expectedCount,
                expectedCount);

    }

    @Test
    public void testCompoundCategoryWithSharedChildrenAndCustomChildren() throws IOException, URISyntaxException {
        long[][] expectedCount = new long[][] { new long[] { 2, 0, 0 } };
        initCompound();
        initTenantIndex(false);
        testAnalysis(Arrays.asList(new String[] { "AAAA" }, new String[] { "Lagunillas" }), new String[] { "COMPOUND_NAME" },
                expectedCount, expectedCount);
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

    public void initTenantIndex(boolean addExistingValues) throws IOException, URISyntaxException {

        Map<String, DQCategory> metadata = builder.getCategoryMetadata();

        metadata.get(SemanticCategoryEnum.AIRPORT_CODE.getTechnicalId()).setModified(true);

        IndexSearcher sharedLuceneDocumentSearcher = new IndexSearcher(DirectoryReader
                .open(ClassPathDirectory.open(CategoryRecognizerBuilder.class.getResource(DEFAULT_DD_PATH).toURI())));

        Directory ramDirectory = new RAMDirectory();
        IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer(CharArraySet.EMPTY_SET));
        IndexWriter writer = new IndexWriter(ramDirectory, writerConfig);

        final Term searchTerm = new Term(DictionarySearcher.F_CATID, SemanticCategoryEnum.AIRPORT_CODE.getTechnicalId());

        if (addExistingValues)
            for (ScoreDoc d : sharedLuceneDocumentSearcher.search(new TermQuery(searchTerm), Integer.MAX_VALUE).scoreDocs) {
                Document doc = sharedLuceneDocumentSearcher.getIndexReader().document(d.doc);
                for (String value : doc.getValues(DictionarySearcher.F_RAW)) {
                    List<String> tokens = DictionarySearcher.getTokensFromAnalyzer(value);
                    doc.add(new StringField(DictionarySearcher.F_SYNTERM, StringUtils.join(tokens, ' '), Field.Store.NO));
                }
                writer.addDocument(doc);
            }

        writer.addDocument(DictionaryUtils.generateDocument("ID_DOCUMENT", SemanticCategoryEnum.AIRPORT_CODE.getTechnicalId(),
                SemanticCategoryEnum.AIRPORT_CODE.getId(), new HashSet<String>(Arrays.asList("AAAA", "BBBB"))));
        writer.addDocument(DictionaryUtils.generateDocument("ID_DOCUMENT2", SemanticCategoryEnum.AIRPORT_CODE.getTechnicalId(),
                SemanticCategoryEnum.AIRPORT_CODE.getId(), new HashSet<String>(Arrays.asList("CCCC", "DDDD"))));

        writer.commit();
        writer.close();
        builder.ddCustomDirectory(ramDirectory).lucene().build();

    }

    public void initCompound() throws IOException, URISyntaxException {
        Map<String, DQCategory> metadata = builder.getCategoryMetadata();
        DQCategory compoundCategory = new DQCategory("COMPOUND_ID");
        compoundCategory.setName("COMPOUND_NAME");
        compoundCategory.setLabel("COMPOUND_LABEL");
        compoundCategory.setCompleteness(true);
        compoundCategory.setModified(true);
        compoundCategory.setType(CategoryType.COMPOUND);
        List<DQCategory> children = new ArrayList<>();
        children.add(metadata.get(SemanticCategoryEnum.AIRPORT_CODE.getTechnicalId()));
        children.add(metadata.get(SemanticCategoryEnum.AIRPORT.getTechnicalId()));
        compoundCategory.setChildren(children);
        metadata.put("COMPOUND_ID", compoundCategory);
        metadata.get(SemanticCategoryEnum.AIRPORT_CODE.getTechnicalId()).setParents(Collections.singletonList(compoundCategory));
        metadata.get(SemanticCategoryEnum.AIRPORT.getTechnicalId()).setParents(Collections.singletonList(compoundCategory));

    }

}
