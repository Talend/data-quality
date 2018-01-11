package org.talend.dataquality.semantic.recognizer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;

public class DefaultCategoryRecognizerTest extends CategoryRegistryManagerAbstract {

    private CategoryRecognizer recognizer;

    DictionarySnapshot dictionarySnapshot;

    private static final String US_STATE_PHONE_FR_COMMUNE = "US_STATE_PHONE_FR_COMMUNE";

    private static final String US_STATE_PHONE = "US_STATE_PHONE";

    /**
     * BUILD THE FOLLOWING TREE:
     * <p>
     * PHONE contains FR_PHONE, DE_PHONE, UK_PHONE, US_PHONE
     * US_STATE_PHONE contains US_STATE and PHONE
     * US_STATE_PHONE_FR_COMMUNE contains FR_COMMUNE and US_STATE_PHONE
     *
     * @throws IOException
     */
    public void init() throws IOException {

        Map<String, DQCategory> metadata = CategoryRegistryManager.getInstance().getCustomDictionaryHolder().getMetadata();
        String randomId1 = "RANDOM_ID_1";
        String randomId2 = "RANDOM_ID_2";

        DQCategory northAmericaAndPhoneCategory = new DQCategory(randomId1);
        northAmericaAndPhoneCategory.setChildren(Arrays.asList(new DQCategory(SemanticCategoryEnum.PHONE.getTechnicalId()),
                new DQCategory(SemanticCategoryEnum.US_STATE.getTechnicalId())));
        northAmericaAndPhoneCategory.setName(US_STATE_PHONE);
        northAmericaAndPhoneCategory.setLabel("North American state and Phone");
        northAmericaAndPhoneCategory.setType(CategoryType.COMPOUND);
        northAmericaAndPhoneCategory.setCompleteness(true);

        DQCategory northAmericaPhoneAndFRCommuneCategory = new DQCategory(randomId2);
        northAmericaPhoneAndFRCommuneCategory.setChildren(
                Arrays.asList(new DQCategory(SemanticCategoryEnum.FR_COMMUNE.getTechnicalId()), new DQCategory(randomId1)));
        northAmericaPhoneAndFRCommuneCategory.setName(US_STATE_PHONE_FR_COMMUNE);
        northAmericaPhoneAndFRCommuneCategory.setLabel("North American state and Phone and fr communes");
        northAmericaPhoneAndFRCommuneCategory.setType(CategoryType.COMPOUND);
        northAmericaPhoneAndFRCommuneCategory.setCompleteness(true);

        if (northAmericaAndPhoneCategory.getParents() == null)
            northAmericaAndPhoneCategory.setParents(new ArrayList<>());
        northAmericaAndPhoneCategory.getParents().add(northAmericaPhoneAndFRCommuneCategory);

        CategoryRegistryManager.getInstance().getCustomDictionaryHolder().createCategory(northAmericaAndPhoneCategory);
        CategoryRegistryManager.getInstance().getCustomDictionaryHolder().createCategory(northAmericaPhoneAndFRCommuneCategory);

        DQCategory phoneCategory = metadata.get(SemanticCategoryEnum.PHONE.getTechnicalId());
        if (phoneCategory.getParents() == null)
            phoneCategory.setParents(new ArrayList<>());
        phoneCategory.getParents().add(northAmericaAndPhoneCategory);
        CategoryRegistryManager.getInstance().getCustomDictionaryHolder().updateCategory(phoneCategory);

        DQCategory usStateCategory = metadata.get(SemanticCategoryEnum.US_STATE.getTechnicalId());
        if (usStateCategory.getParents() == null)
            usStateCategory.setParents(new ArrayList<>());

        usStateCategory.getParents().add(northAmericaAndPhoneCategory);
        CategoryRegistryManager.getInstance().getCustomDictionaryHolder().updateCategory(usStateCategory);

        DQCategory frCommuneCategory = metadata.get(SemanticCategoryEnum.FR_COMMUNE.getTechnicalId());
        if (frCommuneCategory.getParents() == null)
            frCommuneCategory.setParents(new ArrayList<>());
        frCommuneCategory.getParents().add(northAmericaPhoneAndFRCommuneCategory);
        CategoryRegistryManager.getInstance().getCustomDictionaryHolder().updateCategory(frCommuneCategory);

        dictionarySnapshot = new StandardDictionarySnapshotProvider().get();
        recognizer = new DefaultCategoryRecognizer(dictionarySnapshot);

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
        recognizer = new DefaultCategoryRecognizer(dictionarySnapshot);

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
        init();
        recognizer.process("alabama");
        recognizer.process("1-844-224-4468");
        recognizer.process("berulle");
        checkResults(US_STATE_PHONE_FR_COMMUNE, US_STATE_PHONE, SemanticCategoryEnum.FR_COMMUNE.getId(),
                SemanticCategoryEnum.US_PHONE.getId(), SemanticCategoryEnum.US_STATE.getId(), SemanticCategoryEnum.PHONE.getId(),
                SemanticCategoryEnum.NA_STATE.getId());
    }

    @Test
    public void discoveryOrderWith2Phones() throws IOException, URISyntaxException {
        init();
        recognizer.process("alabama");
        recognizer.process("0675982547");
        recognizer.process("1-844-224-4468");
        recognizer.process("berulle");
        checkResults(US_STATE_PHONE_FR_COMMUNE, US_STATE_PHONE, SemanticCategoryEnum.PHONE.getId(),
                SemanticCategoryEnum.FR_COMMUNE.getId(), SemanticCategoryEnum.FR_PHONE.getId(),
                SemanticCategoryEnum.US_PHONE.getId(), SemanticCategoryEnum.US_STATE.getId(),
                SemanticCategoryEnum.DE_PHONE.getId(), SemanticCategoryEnum.NA_STATE.getId());
    }

    @Test
    public void discoveryOrderWith2FRCommunes() throws IOException, URISyntaxException {
        init();
        recognizer.process("alabama");
        recognizer.process("la bernardiere");
        recognizer.process("berulle");
        checkResults(US_STATE_PHONE_FR_COMMUNE, SemanticCategoryEnum.FR_COMMUNE.getId(), SemanticCategoryEnum.US_STATE.getId(),
                US_STATE_PHONE, SemanticCategoryEnum.NA_STATE.getId());
    }

    @Test
    public void discoveryWithTenantIndex() throws IOException, URISyntaxException {
        init();
        recognizer.process("CDG");
        checkResults(SemanticCategoryEnum.AIRPORT_CODE.getId());
        recognizer.process("AAAA");
        checkResults(StringUtils.EMPTY);

        initTenantIndex(false);
        recognizer.process("CDG");
        checkResults(SemanticCategoryEnum.AIRPORT_CODE.getId());
        recognizer.process("AAAA");
        checkResults(SemanticCategoryEnum.AIRPORT_CODE.getId());

        initTenantIndex(true);

        recognizer.process("CDG");
        checkResults(StringUtils.EMPTY);
        recognizer.process("AAAA");
        checkResults(SemanticCategoryEnum.AIRPORT_CODE.getId());
    }

    @Test
    public void discoveryWithTenantIndexFirstName() throws IOException, URISyntaxException {
        init();
        recognizer.process("damien");
        checkResults(SemanticCategoryEnum.FIRST_NAME.getId());
    }

    @Test
    public void discoveryWithTenantIndexAndDeletedCategory() throws IOException, URISyntaxException {
        init();
        DQCategory dqCategory = CategoryRegistryManager.getInstance()
                .getCategoryMetadataById(SemanticCategoryEnum.FIRST_NAME.getTechnicalId());
        CategoryRegistryManager.getInstance().getCustomDictionaryHolder().deleteCategory(dqCategory);

        dictionarySnapshot = new StandardDictionarySnapshotProvider().get();
        recognizer = new DefaultCategoryRecognizer(dictionarySnapshot);

        recognizer.process("damien");
        checkResults(StringUtils.EMPTY);
    }

    public void checkResults(String... expectedCategories) throws IOException {
        Collection<CategoryFrequency> result = recognizer.getResult();
        CategoryFrequency[] resultArray = result.toArray(new CategoryFrequency[result.size()]);
        assertEquals(expectedCategories.length, resultArray.length);
        for (int i = 0; i < resultArray.length; i++) {
            assertEquals(expectedCategories[i], resultArray[i].getCategoryId());
        }
        dictionarySnapshot = new StandardDictionarySnapshotProvider().get();
        recognizer = new DefaultCategoryRecognizer(dictionarySnapshot);
    }

}