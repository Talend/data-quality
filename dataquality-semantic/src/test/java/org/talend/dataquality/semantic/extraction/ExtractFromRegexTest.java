package org.talend.dataquality.semantic.extraction;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedCategory;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedRegexValidator;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;

public class ExtractFromRegexTest extends CategoryRegistryManagerAbstract {

    private DictionarySnapshot dictionarySnapshot;

    @Before
    public void init() {
        dictionarySnapshot = new StandardDictionarySnapshotProvider().get();
    }

    @Test
    public void ibanMatch() {
        DQCategory category = CategoryRegistryManager.getInstance().getCategoryMetadataByName(SemanticCategoryEnum.IBAN.getId());
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("DE89 3704 0044 0532 0130 00, Bee, Aerospace Engineer");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 0, 27);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch.getExactMatch() + " in " + list, list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    @Test
    public void ibanMatchAlone() {
        DQCategory category = CategoryRegistryManager.getInstance().getCategoryMetadataByName(SemanticCategoryEnum.IBAN.getId());
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("DE89 3704 0044 0532 0130 00");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 0, 27);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch.getExactMatch() + " in " + list, list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    @Test
    public void frPhone() {
        DQCategory category = CategoryRegistryManager.getInstance()
                .getCategoryMetadataByName(SemanticCategoryEnum.FR_PHONE.getId());
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("My phone is 0102030405.");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 12, 22);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch.getExactMatch() + " in " + list, list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    @Test
    public void without() {
        DQCategory category = prepCategory("abc");
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("My efdss abc dfdfs abcd.");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 9, 12);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch.getExactMatch() + " in " + list, list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    @Test
    public void withLitteralDollar() {
        DQCategory category = prepCategory("abc\\$");
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("My phone is abc$.");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 12, 16);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch.getExactMatch() + " in " + list, list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    @Test
    public void withBackslashAndLitteralDollar() {
        DQCategory category = prepCategory("abc\\\\\\$");
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("My phone is abc\\$.");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 12, 17);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch.getExactMatch() + " in " + list, list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    @Test
    public void withBackslashAndDollar() {
        DQCategory category = prepCategory("abc\\\\$");
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("My phone is abc\\.");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 12, 16);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch.getExactMatch() + " in " + list, list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    @Test
    public void frPhoneWithSpaces() {
        DQCategory category = CategoryRegistryManager.getInstance()
                .getCategoryMetadataByName(SemanticCategoryEnum.FR_PHONE.getId());
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("My phone is 01 02 03 04 05.");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 12, 26);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch.getExactMatch() + " in " + list, list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    @Test
    public void frPhoneWithPoints() {
        DQCategory category = CategoryRegistryManager.getInstance()
                .getCategoryMetadataByName(SemanticCategoryEnum.FR_PHONE.getId());
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("My phone is 01.02.03.04.05.");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 12, 26);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch.getExactMatch() + " in " + list, list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    @Test
    public void ukSSN() {
        DQCategory category = CategoryRegistryManager.getInstance()
                .getCategoryMetadataByName(SemanticCategoryEnum.UK_SSN.getId());
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString(
                "file://localhost/etc/fstab F-9748 0033739-55.67 67 OK 18 57 04 C fabien.bouquignaud@solypse.com");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 51, 64);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch.getExactMatch() + " in " + list, list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    @Test
    public void multipleFrPhone() {
        DQCategory category = CategoryRegistryManager.getInstance()
                .getCategoryMetadataByName(SemanticCategoryEnum.FR_PHONE.getId());
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString(
                "My phone is 0102030405. Your phone is 0203040506, isn't it? My SSN is 123010112312374.");
        MatchedPart expectedMatch1 = new MatchedPartRegex(input, 12, 22);
        MatchedPart expectedMatch2 = new MatchedPartRegex(input, 38, 48);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch1.getExactMatch() + " in " + list, list.contains(expectedMatch1));
        assertTrue("Expected : " + expectedMatch2.getExactMatch() + " in " + list, list.contains(expectedMatch2));
        assertEquals(2, list.size());
    }

    @Test
    public void frPostalCode() {
        DQCategory category = CategoryRegistryManager.getInstance()
                .getCategoryMetadataByName(SemanticCategoryEnum.FR_POSTAL_CODE.getId());
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString(
                "SW 24 48 37 A F-54444 file:///home/User/2ndFile.html 0033880533496 munjuluris@aetna.com");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 14, 21);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch.getExactMatch() + " in " + list, list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    @Test
    public void matchEndingWithSeparator() {
        DQCategory category = prepCategory("[0-9]{3}\\+");

        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("+33 7.65.1434.22 123+");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 17, 21);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch.getExactMatch() + " in " + list, list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    @Test
    public void matchStartingWithSeparator() {
        DQCategory category = CategoryRegistryManager.getInstance()
                .getCategoryMetadataByName(SemanticCategoryEnum.FR_PHONE.getId());
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("+33 7.65.1434.22 ZL228849 file:///c:/WINDOWS/clock.avi swann@redkeet.com");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 0, 16);
        List<MatchedPart> list = efd.getMatches(input);
        assertTrue("Expected : " + expectedMatch.getExactMatch() + " in " + list, list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    @Test
    public void fieldStartingWithSeparator() {
        DQCategory category = CategoryRegistryManager.getInstance()
                .getCategoryMetadataByName(SemanticCategoryEnum.FR_PHONE.getId());
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString(" My phone is 0102030405.");
        MatchedPart expectedMatch = new MatchedPartRegex(input, 13, 23);
        List<MatchedPart> list = efd.getMatches(input);
        Assert.assertTrue(list.contains(expectedMatch));
        assertEquals(1, list.size());
    }

    private DQCategory prepCategory(String regex) {

        String id = "this is the Id"; //$NON-NLS-1$
        UserDefinedCategory cat = new UserDefinedCategory(id);
        cat.setId(id);
        UserDefinedRegexValidator userDefinedRegexValidator = new UserDefinedRegexValidator();
        userDefinedRegexValidator.setPatternString(regex);
        cat.setValidator(userDefinedRegexValidator);
        dictionarySnapshot.getRegexClassifier().addSubCategory(cat);

        DQCategory dqCategory = new DQCategory();
        dqCategory.setId(id);
        return dqCategory;
    }

}