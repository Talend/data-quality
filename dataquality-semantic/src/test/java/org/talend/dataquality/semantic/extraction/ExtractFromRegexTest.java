package org.talend.dataquality.semantic.extraction;

import static junit.framework.TestCase.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQRegEx;
import org.talend.dataquality.semantic.model.DQValidator;
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

        DQCategory category = prepCategory(SemanticCategoryEnum.IBAN);
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("DE89 3704 0044 0532 0130 00, Bee, Aerospace Engineer");
        MatchedPart expectedMatch = new MatchedPart(input, Arrays.asList(0, 1, 2, 3, 4, 5));
        assertTrue(efd.getMatches(input).contains(expectedMatch));
    }

    @Test
    public void frPhone() {

        DQCategory category = prepCategory(SemanticCategoryEnum.FR_PHONE);
        ExtractFromRegex efd = new ExtractFromRegex(dictionarySnapshot, category);
        TokenizedString input = new TokenizedString("My phone is 0102030405.");
        MatchedPart expectedMatch = new MatchedPart(input, Arrays.asList(3));
        assertTrue(efd.getMatches(input).contains(expectedMatch));
    }

    private DQCategory prepCategory(SemanticCategoryEnum semanticCategoryEnum) {
        DQCategory category = CategoryRegistryManager.getInstance().getCategoryMetadataByName(semanticCategoryEnum.getId());

        DQRegEx dqRegEx = new DQRegEx();
        DQValidator dqValidator = new DQValidator();
        dqValidator.setPatternString(
                dictionarySnapshot.getRegexClassifier().getPatternStringByCategoryId(semanticCategoryEnum.getTechnicalId()));
        dqRegEx.setValidator(dqValidator);
        category.setRegEx(dqRegEx);
        return category;
    }
}