package org.talend.dataquality.semantic.extraction;

import static junit.framework.TestCase.assertTrue;

import java.util.Arrays;

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
        MatchedPart expectedMatch = new MatchedPart(input, Arrays.asList(0, 1, 2, 3, 4, 5));
        assertTrue(efd.getMatches(input).contains(expectedMatch));
    }

    @Test
    public void frPhone() {

        DQCategory category = CategoryRegistryManager.getInstance()
                .getCategoryMetadataByName(SemanticCategoryEnum.FR_PHONE.getId());
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("My phone is 0102030405.");
        MatchedPart expectedMatch = new MatchedPart(input, Arrays.asList(3));
        assertTrue(efd.getMatches(input).contains(expectedMatch));
    }

    @Test
    public void without() {
        DQCategory category = prepCategory("abc");
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("My efdss abc dfdfs abcd.");
        MatchedPart expectedMatch = new MatchedPart(input, Arrays.asList(2));
        assertTrue(efd.getMatches(input).contains(expectedMatch));
    }

    @Test
    public void withLitteralDollar() {
        DQCategory category = prepCategory("abc\\$");
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("My phone is abc$.");
        MatchedPart expectedMatch = new MatchedPart(input, Arrays.asList(3));
        assertTrue(efd.getMatches(input).contains(expectedMatch));
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