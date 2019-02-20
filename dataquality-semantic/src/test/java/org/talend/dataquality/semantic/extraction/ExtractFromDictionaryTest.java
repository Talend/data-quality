package org.talend.dataquality.semantic.extraction;

import static junit.framework.TestCase.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;

public class ExtractFromDictionaryTest {

    private DictionarySnapshot snapshot = new StandardDictionarySnapshotProvider().get();

    private DQCategory category = CategoryRegistryManager.getInstance()
            .getCategoryMetadataByName(SemanticCategoryEnum.COUNTRY.getId());

    @Test
    public void basicMatch() {
        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, category);
        TokenizedString input = new TokenizedString("Manchester United States");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPart(input, 1, 2));
        assertEquals(expected, efd.getMatches(input));
    }

    @Test
    public void noExactMatch() {
        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, category);
        TokenizedString input = new TokenizedString("Manchester United Sates");
        List<MatchedPart> expected = new ArrayList<>();
        assertEquals(expected, efd.getMatches(input));
    }

    @Test
    public void matchAfterMultiTokenMatch() {
        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, category);
        TokenizedString input = new TokenizedString("The United States, Somalia, AFR");
        List<MatchedPart> expected = Arrays.asList(new MatchedPart(input, 1, 2), new MatchedPart(input, 4, 4));
        assertEquals(expected, efd.getMatches(input));
    }

    @Test
    public void matchAfterNoExactMatch() {
        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, category);
        TokenizedString input = new TokenizedString("Emirates United Arabia, Somalia, SO, Africa, AFR");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPart(input, 4, 4));
        assertEquals(expected, efd.getMatches(input));
    }
}