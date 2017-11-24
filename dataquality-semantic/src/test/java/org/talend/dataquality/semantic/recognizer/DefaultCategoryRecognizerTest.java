package org.talend.dataquality.semantic.recognizer;

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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.DictionaryUtils;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.index.ClassPathDirectory;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder.DEFAULT_DD_PATH;

public class DefaultCategoryRecognizerTest extends CategoryRegistryManagerAbstract {

    private CategoryRecognizer recognizer;

    private CategoryRecognizerBuilder builder;

    private static final String US_STATE_PHONE_FR_COMMUNE = "US_STATE_PHONE_FR_COMMUNE";

    private static final String US_STATE_PHONE = "US_STATE_PHONE";

    /**
     * BUILD THE FOLLOWING TREE:
     *
     * US_STATE_PHONE_FR_COMMUNE contains FR_COMMUNE and US_STATE_PHONE
     * US_STATE_PHONE contains US_STATE and PHONE
     * PHONE contains FR_PHONE, DE_PHONE, UK_PHONE, US_PHONE
     * 
     * @throws IOException
     */
    @Before
    public void init() throws IOException {

        builder = CategoryRecognizerBuilder.newBuilder();
        builder.tenantID("t_default_category");

        Map<String, DQCategory> metadata = builder.getCategoryMetadata();
        String randomId1 = "RANDOM_ID_1";
        String randomId2 = "RANDOM_ID_2";

        DQCategory northAmericaAndPhoneCategory = new DQCategory(randomId1);
        northAmericaAndPhoneCategory.setChildren(Arrays.asList(new DQCategory(SemanticCategoryEnum.PHONE.getTechnicalId()),
                new DQCategory(SemanticCategoryEnum.US_STATE.getTechnicalId())));
        northAmericaAndPhoneCategory.setName(US_STATE_PHONE);
        northAmericaAndPhoneCategory.setLabel("North American state and Phone");
        northAmericaAndPhoneCategory.setType(CategoryType.COMPOUND);

        DQCategory northAmericaPhoneAndFRCommuneCategory = new DQCategory(randomId2);
        northAmericaPhoneAndFRCommuneCategory.setChildren(
                Arrays.asList(new DQCategory(SemanticCategoryEnum.FR_COMMUNE.getTechnicalId()), new DQCategory(randomId1)));
        northAmericaPhoneAndFRCommuneCategory.setName(US_STATE_PHONE_FR_COMMUNE);
        northAmericaPhoneAndFRCommuneCategory.setLabel("North American state and Phone and fr communes");
        northAmericaPhoneAndFRCommuneCategory.setType(CategoryType.COMPOUND);

        metadata.put(randomId1, northAmericaAndPhoneCategory);
        metadata.put(randomId2, northAmericaPhoneAndFRCommuneCategory);
        metadata.get(SemanticCategoryEnum.PHONE.getTechnicalId()).setParents(Arrays.asList(northAmericaAndPhoneCategory));
        metadata.get(SemanticCategoryEnum.US_STATE.getTechnicalId()).setParents(Arrays.asList(northAmericaAndPhoneCategory));
        metadata.get(SemanticCategoryEnum.FR_COMMUNE.getTechnicalId())
                .setParents(Arrays.asList(northAmericaPhoneAndFRCommuneCategory));
        metadata.get(randomId1).setParents(Arrays.asList(northAmericaPhoneAndFRCommuneCategory));

        recognizer = builder.lucene().build();
    }

    public void initTenantIndex(boolean addExistingValues) throws IOException, URISyntaxException {
        builder = CategoryRecognizerBuilder.newBuilder();
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
        recognizer = builder.metadata(metadata).ddCustomDirectory(ramDirectory).lucene().build();
    }

    /**
     * check the order with the following priorities
     * "count" first, "level" next and "SemanticCategoryEnum.java ordinal" last
     * 
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void discoveryOrderForCompoundCategories() throws IOException, URISyntaxException {
        recognizer.process("alabama");
        recognizer.process("1-844-224-4468");
        recognizer.process("berulle");
        checkResults(US_STATE_PHONE_FR_COMMUNE, US_STATE_PHONE, SemanticCategoryEnum.FR_COMMUNE.getId(),
                SemanticCategoryEnum.US_PHONE.getId(), SemanticCategoryEnum.US_STATE.getId(), SemanticCategoryEnum.PHONE.getId());
    }

    @Test
    public void discoveryOrderWith2Phones() throws IOException, URISyntaxException {
        recognizer.process("alabama");
        recognizer.process("0675982547");
        recognizer.process("1-844-224-4468");
        recognizer.process("berulle");
        checkResults(US_STATE_PHONE_FR_COMMUNE, US_STATE_PHONE, SemanticCategoryEnum.PHONE.getId(),
                SemanticCategoryEnum.FR_COMMUNE.getId(), SemanticCategoryEnum.FR_PHONE.getId(),
                SemanticCategoryEnum.US_PHONE.getId(), SemanticCategoryEnum.US_STATE.getId(),
                SemanticCategoryEnum.DE_PHONE.getId());
    }

    @Test
    public void discoveryOrderWith2FRCommunes() throws IOException, URISyntaxException {
        recognizer.process("alabama");
        recognizer.process("la bernardiere");
        recognizer.process("berulle");
        checkResults(US_STATE_PHONE_FR_COMMUNE, SemanticCategoryEnum.FR_COMMUNE.getId(), SemanticCategoryEnum.US_STATE.getId(),
                US_STATE_PHONE);
    }

    @Test
    public void discoveryWithTenantIndex() throws IOException, URISyntaxException {
        recognizer.process("CDG");
        checkResults(SemanticCategoryEnum.AIRPORT_CODE.getId());
        recognizer.process("AAAA");
        checkResults(StringUtils.EMPTY);

        initTenantIndex(true);
        recognizer.process("CDG");
        checkResults(SemanticCategoryEnum.AIRPORT_CODE.getId());
        recognizer.process("AAAA");
        checkResults(SemanticCategoryEnum.AIRPORT_CODE.getId());

        initTenantIndex(false);
        recognizer.process("CDG");
        checkResults(StringUtils.EMPTY);
        recognizer.process("AAAA");
        checkResults(SemanticCategoryEnum.AIRPORT_CODE.getId());
    }

    @Test
    public void discoveryWithTenantIndexAndDeletedCategory() throws IOException, URISyntaxException {
        recognizer.process("damien");
        checkResults(SemanticCategoryEnum.FIRST_NAME.getId());

        builder.getCategoryMetadata().get(SemanticCategoryEnum.FIRST_NAME.getTechnicalId()).setDeleted(true);
        recognizer = builder.lucene().build();
        recognizer.process("damien");
        checkResults(StringUtils.EMPTY);
    }

    public void checkResults(String... expectedCategories) {
        Collection<CategoryFrequency> result = recognizer.getResult();
        CategoryFrequency[] resultArray = result.toArray(new CategoryFrequency[result.size()]);
        assertEquals(expectedCategories.length, resultArray.length);
        for (int i = 0; i < resultArray.length; i++) {
            assertEquals(expectedCategories[i], resultArray[i].getCategoryId());
        }
        recognizer.reset();
    }

}