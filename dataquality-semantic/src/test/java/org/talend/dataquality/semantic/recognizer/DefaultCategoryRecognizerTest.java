package org.talend.dataquality.semantic.recognizer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;

public class DefaultCategoryRecognizerTest {

    private CategoryRecognizer recognizer;

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
        CategoryRecognizerBuilder builder = CategoryRecognizerBuilder.newBuilder();
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
        Collection<CategoryFrequency> result = recognizer.getResult();
        CategoryFrequency[] resultArray = result.toArray(new CategoryFrequency[result.size()]);
        List<String> expectedCategories = Arrays.asList(US_STATE_PHONE_FR_COMMUNE, US_STATE_PHONE,
                SemanticCategoryEnum.FR_COMMUNE.getId(), SemanticCategoryEnum.US_PHONE.getId(),
                SemanticCategoryEnum.US_STATE.getId(), SemanticCategoryEnum.PHONE.getId());
        assertEquals(expectedCategories.size(), resultArray.length);
        for (int i = 0; i < resultArray.length; i++) {
            assertEquals(expectedCategories.get(i), resultArray[i].getCategoryId());
        }
    }

    @Test
    public void discoveryOrderWith2Phones() throws IOException, URISyntaxException {
        recognizer.process("alabama");
        recognizer.process("0675982547");
        recognizer.process("1-844-224-4468");
        recognizer.process("berulle");
        Collection<CategoryFrequency> result = recognizer.getResult();
        CategoryFrequency[] resultArray = result.toArray(new CategoryFrequency[result.size()]);
        List<String> expectedCategories = Arrays.asList(US_STATE_PHONE_FR_COMMUNE, US_STATE_PHONE,
                SemanticCategoryEnum.PHONE.getId(), SemanticCategoryEnum.FR_COMMUNE.getId(),
                SemanticCategoryEnum.FR_PHONE.getId(), SemanticCategoryEnum.US_PHONE.getId(),
                SemanticCategoryEnum.US_STATE.getId(), SemanticCategoryEnum.DE_PHONE.getId());
        assertEquals(expectedCategories.size(), resultArray.length);
        for (int i = 0; i < resultArray.length; i++) {
            assertEquals(expectedCategories.get(i), resultArray[i].getCategoryId());

        }
    }

    @Test
    public void discoveryOrderWith2FRCommunes() throws IOException, URISyntaxException {
        recognizer.process("alabama");
        recognizer.process("la bernardiere");
        recognizer.process("berulle");
        Collection<CategoryFrequency> result = recognizer.getResult();
        CategoryFrequency[] resultArray = result.toArray(new CategoryFrequency[result.size()]);
        List<String> expectedCategories = Arrays.asList(US_STATE_PHONE_FR_COMMUNE, SemanticCategoryEnum.FR_COMMUNE.getId(),
                SemanticCategoryEnum.US_STATE.getId(), US_STATE_PHONE);
        assertEquals(expectedCategories.size(), resultArray.length);
        for (int i = 0; i < resultArray.length; i++) {
            assertEquals(expectedCategories.get(i), resultArray[i].getCategoryId());

        }
    }
}