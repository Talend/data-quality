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
        List<MatchedPart> expected = Collections.singletonList(new MatchedPartDict(input, 1, 2));
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
        List<MatchedPart> expected = Arrays.asList(new MatchedPartDict(input, 1, 2), new MatchedPartDict(input, 3, 3));
        assertEquals(expected, efd.getMatches(input));
    }

    @Test
    public void matchAfterNoExactMatch() {
        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, category);
        TokenizedString input = new TokenizedString("Emirates United Arabia, Somalia, SO, Africa, AFR");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPartDict(input, 3, 3));
        assertEquals(expected, efd.getMatches(input));
    }

    @Test
    public void matchWithAccentInDictionary() {
        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, category);
        // input does not contain accent, but should match "Brésil" with accent
        TokenizedString input = new TokenizedString("Neymar vient de BRESIL. Messi vient d'une autre planète.");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPartDict(input, 3, 3));
        List<MatchedPart> actual = efd.getMatches(input);
        assertEquals(expected, actual);
        assertEquals("BRESIL", actual.get(0).getExactMatch());
    }

    @Test
    public void matchWithAccentFromInput() {
        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, category);
        // input contains à with accent, the spelling is incorrect, but it should still match "Brazil" without accent in dico
        TokenizedString input = new TokenizedString("Neymar is from Bràzil. Messi is from another planet.");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPartDict(input, 3, 3));
        List<MatchedPart> actual = efd.getMatches(input);
        assertEquals(expected, actual);
        assertEquals("Bràzil", actual.get(0).getExactMatch());
    }
}