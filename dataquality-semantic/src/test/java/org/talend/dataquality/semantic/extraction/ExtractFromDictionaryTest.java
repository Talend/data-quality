package org.talend.dataquality.semantic.extraction;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;

@RunWith(MockitoJUnitRunner.class)
public class ExtractFromDictionaryTest {

    private DictionarySnapshot snapshot = new StandardDictionarySnapshotProvider().get();

    private DQCategory category = CategoryRegistryManager.getInstance()
            .getCategoryMetadataByName(SemanticCategoryEnum.COUNTRY.getId());

    @Mock
    private DictionarySearcher mockSearcher;

    @Mock
    private LuceneIndex mockIndex;

    @Mock
    private DictionarySnapshot mockSnapshot;

    @Before
    public void setUp() {
        Map<String, DQCategory> metadata = Collections.singletonMap(category.getId(), category);

        when(mockIndex.getSearcher()).thenReturn(mockSearcher);
        when(mockSnapshot.getSharedDataDict()).thenReturn(mockIndex);
        when(mockSnapshot.getCustomDataDict()).thenReturn(Mockito.mock(Index.class));
        when(mockSnapshot.getKeyword()).thenReturn(Mockito.mock(Index.class));
        when(mockSnapshot.getMetadata()).thenReturn(metadata);
    }

    @Test
    public void matchStartsWithSeparator() {
        TokenizedString input = new TokenizedString("My phone number is +33612549863.");
        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("+33612549863"));
        String expected = "+33612549863";
        assertEquals(expected, dict.getMatches(input).get(0).getExactMatch());
    }

    @Test
    public void matchStartsWithFirstSeparator() {
        TokenizedString input = new TokenizedString("+33612549863 is my phone number.");
        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("+33612549863"));
        String expected = "+33612549863";
        assertEquals(expected, dict.getMatches(input).get(0).getExactMatch());
    }

    @Test
    public void matchStartsWithSeparatorNotPresentInField() {
        TokenizedString input = new TokenizedString("My phone number is 33612549863.");
        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("+33612549863"));
        String expected = "33612549863";
        assertEquals(expected, dict.getMatches(input).get(0).getExactMatch());
    }

    @Test
    public void matchEndsWithSeparator() {
        TokenizedString input = new TokenizedString("The Music International Museum (Phoenix))) is the best.");
        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Music International Museum (Phoenix)"));
        String expected = "Music International Museum (Phoenix)";
        assertEquals(expected, dict.getMatches(input).get(0).getExactMatch());
    }

    @Test
    public void matchEndsWithSeparatorNotPresentInField() {
        TokenizedString input = new TokenizedString("The Music International Museum (Phoenix is the best.");
        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Music International Museum (Phoenix)"));
        String expected = "Music International Museum (Phoenix";
        assertEquals(expected, dict.getMatches(input).get(0).getExactMatch());
    }

    @Test
    public void matchEndsWithLastSeparator() {
        TokenizedString input = new TokenizedString("My favorite museum is the Music International Museum (Phoenix)");
        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Music International Museum (Phoenix)"));
        String expected = "Music International Museum (Phoenix)";
        assertEquals(expected, dict.getMatches(input).get(0).getExactMatch());
    }

    @Test
    public void basicMatch() {
        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, category);
        TokenizedString input = new TokenizedString("Manchester United States");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPartDict(input, 1, 2, "United States"));
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
        List<MatchedPart> expected = Arrays.asList(new MatchedPartDict(input, 1, 2, "United States"),
                new MatchedPartDict(input, 3, 3, "Somalia"));
        assertEquals(expected, efd.getMatches(input));
    }

    @Test
    public void matchAfterNoExactMatch() {
        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, category);
        TokenizedString input = new TokenizedString("Emirates United Arabia, Somalia, SO, Africa, AFR");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPartDict(input, 3, 3, "Somalia"));
        assertEquals(expected, efd.getMatches(input));
    }

    @Test
    public void matchWithAccentInDictionary() {
        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, category);
        // input does not contain accent, but should match "Brésil" with accent
        TokenizedString input = new TokenizedString("Neymar vient de BRESIL. Messi vient d'une autre planète.");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPartDict(input, 3, 3, "Brésil"));
        List<MatchedPart> actual = efd.getMatches(input);
        assertEquals(expected, actual);
        assertEquals("BRESIL", actual.get(0).getExactMatch());
    }

    @Test
    public void matchWithAccentFromInput() {
        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, category);
        // input contains à with accent, the spelling is incorrect, but it should still match "Brazil" without accent in
        // dico
        TokenizedString input = new TokenizedString("Neymar is from Bràzil. Messi is from another planet.");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPartDict(input, 3, 3, "Brazil"));
        List<MatchedPart> actual = efd.getMatches(input);
        assertEquals(expected, actual);
        assertEquals("Bràzil", actual.get(0).getExactMatch());
    }

    @Test
    public void matchWithApostrophe() {
        DQCategory firstname = CategoryRegistryManager.getInstance()
                .getCategoryMetadataByName(SemanticCategoryEnum.FIRST_NAME.getId());

        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, firstname);
        // input contains à with accent, the spelling is incorrect, but it should still match "Brazil" without accent in
        // dico
        TokenizedString input = new TokenizedString("Susan's");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPartDict(input, 0, 0, "Susan"));
        List<MatchedPart> actual = efd.getMatches(input);
        assertEquals(expected, actual);
        assertEquals("Susan", actual.get(0).getExactMatch());
    }

    @Test
    public void matchWithApostropheInTheMiddle() {
        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, category);
        // input contains à with accent, the spelling is incorrect, but it should still match "Brazil" without accent in
        // dico
        TokenizedString input = new TokenizedString("lac d'Angola");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPartDict(input, 1, 1, "Angola"));
        List<MatchedPart> actual = efd.getMatches(input);
        assertEquals(expected, actual);
        assertEquals("Angola", actual.get(0).getExactMatch());
    }

    @Test
    public void matchWithApostropheAtTheEnd() {
        ExtractFromDictionary efd = new ExtractFromDictionary(snapshot, category);
        // input contains à with accent, the spelling is incorrect, but it should still match "Brazil" without accent in
        // dico
        TokenizedString input = new TokenizedString("Angola'");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPartDict(input, 0, 0, "Angola"));
        List<MatchedPart> actual = efd.getMatches(input);
        assertEquals(expected, actual);
        assertEquals("Angola", actual.get(0).getExactMatch());
    }
}