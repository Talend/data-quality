package org.talend.dataquality.semantic.extraction;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
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

    @Test
    public void susans() {
        TokenizedString susan = new TokenizedString("Susan's");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Susan"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Susan";
        assertEquals(expected, dict.getMatches(susan).get(0).getExactMatch());
    }

    @Test
    public void dArtagnan() {
        TokenizedString artagnan = new TokenizedString("d'Artagnan");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("d'Artagnan"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "d'Artagnan";
        assertEquals(expected, dict.getMatches(artagnan).get(0).getExactMatch());
    }

    @Test
    public void lacDAnnecy() {
        TokenizedString annecy = new TokenizedString("lac d'Annecy");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Annecy"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Annecy";
        assertEquals(expected, dict.getMatches(annecy).get(0).getExactMatch());
    }

    @Test
    public void aujourdHUi() {
        TokenizedString aujourdhui = new TokenizedString("aujourd'hui");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("aujourd'hui"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "aujourd'hui";
        assertEquals(expected, dict.getMatches(aujourdhui).get(0).getExactMatch());
    }

    @Test
    public void rockNRoll_2apostrophes() {
        TokenizedString rock = new TokenizedString("Rock 'n' Roll");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Rock 'n' Roll"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Rock 'n' Roll";
        assertEquals(expected, dict.getMatches(rock).get(0).getExactMatch());
    }

    @Test
    public void rockNRoll_1apostrophe() {
        TokenizedString rock = new TokenizedString("Rock 'n Roll");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Rock 'n' Roll"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        assertEquals(0, dict.getMatches(rock).size());
    }

    @Test
    public void rockNRoll_WithoutApostrophe() {
        TokenizedString rock = new TokenizedString("Rock n Roll");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Rock 'n' Roll"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        assertEquals(0, dict.getMatches(rock).size());
    }

    @Test
    public void dunvilleSThreeCrown() {
        TokenizedString dunville = new TokenizedString("Dunville's Three Crown");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Dunville's Three Crown"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Dunville's Three Crown";
        assertEquals(expected, dict.getMatches(dunville).get(0).getExactMatch());
    }

    @Test
    public void dunville() {
        TokenizedString dunville = new TokenizedString("Dunville's Three Crown");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Dunville"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Dunville";
        assertEquals(expected, dict.getMatches(dunville).get(0).getExactMatch());
    }

    @Test
    public void usa() {
        TokenizedString usa = new TokenizedString("U.S.A");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("U.S.A"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "U.S.A";
        assertEquals(expected, dict.getMatches(usa).get(0).getExactMatch());
    }

    @Test
    public void usaWithFinalPoint() {
        TokenizedString usa = new TokenizedString("U.S.A.");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("U.S.A."));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "U.S.A.";
        assertEquals(expected, dict.getMatches(usa).get(0).getExactMatch());
    }

    @Test
    public void iLiveInTheUSA() {
        TokenizedString usa = new TokenizedString("i live in the U.S.A.");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("U.S.A"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "U.S.A";
        assertEquals(expected, dict.getMatches(usa).get(0).getExactMatch());
    }

    @Ignore
    public void usaWithoutPoint() {
        TokenizedString usa = new TokenizedString("U.S.A");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("USA"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "U.S.A";
        assertEquals(expected, dict.getMatches(usa).get(0).getExactMatch());
    }

    @Ignore
    public void usaGlued() {
        TokenizedString usa = new TokenizedString("USA");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("U.S.A"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "USA";
        assertEquals(expected, dict.getMatches(usa).get(0).getExactMatch());
    }

    @Test
    public void google() {
        TokenizedString google = new TokenizedString("www.google.fr");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Google"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "google";
        assertEquals(expected, dict.getMatches(google).get(0).getExactMatch());
    }

    @Test
    public void googleInc() {
        TokenizedString google = new TokenizedString("www.google.fr");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Google Inc."));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        assertEquals(0, dict.getMatches(google).size());
    }

    @Test
    public void amazonFRFullSlash() {
        TokenizedString amazon = new TokenizedString("https://aws.amazon.com/fr/");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("FR"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "fr";
        assertEquals(expected, dict.getMatches(amazon).get(0).getExactMatch());
    }

    @Test
    public void amazonFR() {
        TokenizedString amazon = new TokenizedString("https://aws.amazon.com/fr");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("FR"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "fr";
        assertEquals(expected, dict.getMatches(amazon).get(0).getExactMatch());
    }

    @Test
    public void amazonFRHttp() {
        TokenizedString amazon = new TokenizedString("https://aws.amazon.com/fr/");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("http"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        assertEquals(0, dict.getMatches(amazon).size());
    }

    @Test
    public void amazonFRHttps() {
        TokenizedString amazon = new TokenizedString("https://aws.amazon.com/fr/");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("https"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "https";
        assertEquals(expected, dict.getMatches(amazon).get(0).getExactMatch());
    }

    @Test
    public void whaaaat() {
        TokenizedString what = new TokenizedString("//What a comment!");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("what"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "What";
        assertEquals(expected, dict.getMatches(what).get(0).getExactMatch());
    }

    @Test
    public void backslash() {
        TokenizedString user = new TokenizedString("c:\\Users\\Public");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("users"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Users";
        assertEquals(expected, dict.getMatches(user).get(0).getExactMatch());
    }

    @Test
    public void afterBackslash() {
        TokenizedString afterSlash = new TokenizedString("c:\\Users\\Public");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("public"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Public";
        assertEquals(expected, dict.getMatches(afterSlash).get(0).getExactMatch());
    }

    @Test
    public void exclamation() {
        TokenizedString exclamation = new TokenizedString("That's great!!!");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("great"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "great";
        assertEquals(expected, dict.getMatches(exclamation).get(0).getExactMatch());
    }

    @Test
    public void hell() {
        TokenizedString hell = new TokenizedString("He told me \"Go to hell\"");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("hell"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "hell";
        assertEquals(expected, dict.getMatches(hell).get(0).getExactMatch());
    }

    @Test
    public void goToHell() {
        TokenizedString hell = new TokenizedString("He told me \"Go to hell\"");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("go"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Go";
        assertEquals(expected, dict.getMatches(hell).get(0).getExactMatch());
    }

    @Test
    public void parentheses() {
        TokenizedString parentheses = new TokenizedString(
                "Parentheses may be nested (generally with one set (such as this) inside another set)");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("generally"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "generally";
        assertEquals(expected, dict.getMatches(parentheses).get(0).getExactMatch());
    }

    @Test
    public void thisParentheses() {
        TokenizedString parentheses = new TokenizedString(
                "Parentheses may be nested (generally with one set (such as this) inside another set)");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("this"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "this";
        assertEquals(expected, dict.getMatches(parentheses).get(0).getExactMatch());
    }

    @Test
    public void future() {
        TokenizedString future = new TokenizedString("the future of psionics [see definition] is in doubt");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("see"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "see";
        assertEquals(expected, dict.getMatches(future).get(0).getExactMatch());
    }

    @Test
    public void definition() {
        TokenizedString definition = new TokenizedString("the future of psionics [see definition] is in doubt");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("definition"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "definition";
        assertEquals(expected, dict.getMatches(definition).get(0).getExactMatch());
    }

    @Test
    public void theGoat() {
        TokenizedString goat = new TokenizedString("Select your animal {goat, sheep, cow, horse} and follow me");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("goat"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "goat";
        assertEquals(expected, dict.getMatches(goat).get(0).getExactMatch());
    }

    @Test
    public void horse() {
        TokenizedString horse = new TokenizedString("Select your animal {goat, sheep, cow, horse} and follow me");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("horse"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "horse";
        assertEquals(expected, dict.getMatches(horse).get(0).getExactMatch());
    }

    @Test
    public void flower() {
        TokenizedString flower = new TokenizedString("<What an unusual flower>");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("flower"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "flower";
        assertEquals(expected, dict.getMatches(flower).get(0).getExactMatch());
    }

    @Test
    public void whatFlower() {
        TokenizedString flower = new TokenizedString("<What an unusual flower>");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("What"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "What";
        assertEquals(expected, dict.getMatches(flower).get(0).getExactMatch());
    }

    @Test
    public void pork() {
        TokenizedString pork = new TokenizedString("#BalanceTonPorc");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("balanceTonPorc"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "BalanceTonPorc";
        assertEquals(expected, dict.getMatches(pork).get(0).getExactMatch());
    }

    @Test
    public void chelsea() {
        TokenizedString chelsea = new TokenizedString("Chel$ea");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Chel$ea"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Chel$ea";
        assertEquals(expected, dict.getMatches(chelsea).get(0).getExactMatch());
    }

    @Test
    public void moneyMoneyMoney() {
        TokenizedString chelsea = new TokenizedString("30$");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("30"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "30";
        assertEquals(expected, dict.getMatches(chelsea).get(0).getExactMatch());
    }

    @Test
    public void percentage() {
        TokenizedString percentage = new TokenizedString("set PATH=c:\\;%PATH%");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("PATH"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "PATH";
        assertEquals(expected, dict.getMatches(percentage).get(0).getExactMatch());
    }

    @Test
    public void ampersandTest() {
        TokenizedString ampersand = new TokenizedString("http://www.example.com/login.php?username=test&password=blank");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("test"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "test";
        assertEquals(expected, dict.getMatches(ampersand).get(0).getExactMatch());
    }

    @Test
    public void ampersandPassword() {
        TokenizedString ampersand = new TokenizedString("http://www.example.com/login.php?username=test&password=blank");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("password"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "password";
        assertEquals(expected, dict.getMatches(ampersand).get(0).getExactMatch());
    }

    @Test
    public void equalPassword() {
        TokenizedString equal = new TokenizedString("http://www.example.com/login.php?username=test&password=blank");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("password"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "password";
        assertEquals(expected, dict.getMatches(equal).get(0).getExactMatch());
    }

    @Test
    public void equalBlank() {
        TokenizedString blank = new TokenizedString("http://www.example.com/login.php?username=test&password=blank");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("blank"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "blank";
        assertEquals(expected, dict.getMatches(blank).get(0).getExactMatch());
    }

    @Test
    public void star() {
        TokenizedString star = new TokenizedString("/*This is a comment*/");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("this"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "This";
        assertEquals(expected, dict.getMatches(star).get(0).getExactMatch());
    }

    @Test
    public void starFinish() {
        TokenizedString star = new TokenizedString("/*This is a comment*/");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("comment"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "comment";
        assertEquals(expected, dict.getMatches(star).get(0).getExactMatch());
    }

    @Test
    public void gregoire() {
        TokenizedString gregoire = new TokenizedString("Toi + Moi");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Toi + Moi"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Toi + Moi";
        assertEquals(expected, dict.getMatches(gregoire).get(0).getExactMatch());
    }

    @Test
    public void phoneNumber() {
        TokenizedString gregoire = new TokenizedString("+3312345678");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("3312345678"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "3312345678";
        assertEquals(expected, dict.getMatches(gregoire).get(0).getExactMatch());
    }

    @Test
    public void jeanPierre() {
        TokenizedString jeanPierre = new TokenizedString("Jean-Pierre");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Jean-Pierre"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Jean-Pierre";
        assertEquals(expected, dict.getMatches(jeanPierre).get(0).getExactMatch());
    }

    @Test
    public void jeanPierreAMoitie() {
        TokenizedString jeanPierre = new TokenizedString("Jean-Pierre");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Jean"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Jean";
        assertEquals(expected, dict.getMatches(jeanPierre).get(0).getExactMatch());
    }

    @Test
    public void jeanPierreTwoResults() {
        TokenizedString jeanPierre = new TokenizedString("Jean-Pierre");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Arrays.asList("Jean", "Jean-Pierre"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Jean-Pierre";
        assertEquals(expected, dict.getMatches(jeanPierre).get(0).getExactMatch());
    }

    @Test
    public void jp() {
        TokenizedString jeanPierre = new TokenizedString("J.-P.");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("J.-P."));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "J.-P.";
        assertEquals(expected, dict.getMatches(jeanPierre).get(0).getExactMatch());
    }

    @Ignore
    public void allInOne() {
        TokenizedString jeanPierre = new TokenizedString("Jean-Pierre");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Arrays.asList("Jean", "Pierre"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Jean, Pierre";
        assertEquals(expected, dict.getMatches(jeanPierre).get(0).getExactMatch());
    }

    @Test
    public void starWars() {
        TokenizedString star = new TokenizedString("Star Wars Episode VI: Return of the Jedi");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("VI"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "VI";
        assertEquals(expected, dict.getMatches(star).get(0).getExactMatch());
    }

    @Test
    public void dented() {
        TokenizedString star = new TokenizedString(":Dented text by the means of a colon.");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Dented"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "Dented";
        assertEquals(expected, dict.getMatches(star).get(0).getExactMatch());
    }

    @Test
    public void myWife() {
        TokenizedString wife = new TokenizedString("My wife would like tea; I would prefer coffee.");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("tea"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "tea";
        assertEquals(expected, dict.getMatches(wife).get(0).getExactMatch());
    }

    @Test
    public void form() {
        TokenizedString form = new TokenizedString("Is it good in form? style? meaning?");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("form"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "form";
        assertEquals(expected, dict.getMatches(form).get(0).getExactMatch());
    }

    @Test
    public void meaning() {
        TokenizedString form = new TokenizedString("Is it good in form? style? meaning?");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("meaning"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "meaning";
        assertEquals(expected, dict.getMatches(form).get(0).getExactMatch());
    }

    @Test
    public void talend() {
        TokenizedString talend = new TokenizedString("toto@talend.com");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("Talend"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "talend";
        assertEquals(expected, dict.getMatches(talend).get(0).getExactMatch());
    }

    @Test
    public void toto() {
        TokenizedString talend = new TokenizedString("toto@talend.com");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("toto"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "toto";
        assertEquals(expected, dict.getMatches(talend).get(0).getExactMatch());
    }

    @Test
    public void dadJoke() {
        TokenizedString toto = new TokenizedString("'@toto: make an effort");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("toto"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "toto";
        assertEquals(expected, dict.getMatches(toto).get(0).getExactMatch());
    }

    @Test
    public void loser() {
        TokenizedString loser = new TokenizedString("loser^^");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("loser"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "loser";
        assertEquals(expected, dict.getMatches(loser).get(0).getExactMatch());
    }

    @Test
    public void underscore_first() {
        TokenizedString first = new TokenizedString("firstname_lastname@talend.com");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("firstname"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "firstname";
        assertEquals(expected, dict.getMatches(first).get(0).getExactMatch());
    }

    @Test
    public void underscore_last() {
        TokenizedString first = new TokenizedString("firstname_lastname@talend.com");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("lastname"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "lastname";
        assertEquals(expected, dict.getMatches(first).get(0).getExactMatch());
    }

    @Test
    public void pipeLog() {
        TokenizedString pipe = new TokenizedString("grep -i 'blair' filename.log|more");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("log"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "log";
        assertEquals(expected, dict.getMatches(pipe).get(0).getExactMatch());
    }

    @Test
    public void pipeMore() {
        TokenizedString pipe = new TokenizedString("grep -i 'blair' filename.log|more");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("more"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "more";
        assertEquals(expected, dict.getMatches(pipe).get(0).getExactMatch());
    }

    @Test
    public void tilde() {
        TokenizedString tilde = new TokenizedString("12~15");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("12"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "12";
        assertEquals(expected, dict.getMatches(tilde).get(0).getExactMatch());
    }

    @Test
    public void urlWithTilde() {
        TokenizedString tilde = new TokenizedString("https://suif.stanford.edu/~courses/cs243/");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString()))
                .thenReturn(Collections.singletonList("courses"));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "courses";
        assertEquals(expected, dict.getMatches(tilde).get(0).getExactMatch());
    }

    @Test
    public void azer_1apostrophe() {
        TokenizedString tilde = new TokenizedString("azer'");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString())).thenReturn(Arrays
                .asList("azer'", "azer''", "azer'''", "azer''''", "az'er'", "azer.", "azer..", "azer...", "azer....", "az.er."));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "azer'";
        assertEquals(expected, dict.getMatches(tilde).get(0).getExactMatch());
    }

    @Test
    public void azer_1point() {
        TokenizedString tilde = new TokenizedString("azer.");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString())).thenReturn(Arrays
                .asList("azer'", "azer''", "azer'''", "azer''''", "az'er'", "azer.", "azer..", "azer...", "azer....", "az.er."));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "azer.";
        assertEquals(expected, dict.getMatches(tilde).get(0).getExactMatch());
    }

    @Test
    public void azer_middlePoint() {
        TokenizedString tilde = new TokenizedString("az.er.");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString())).thenReturn(Arrays
                .asList("azer'", "azer''", "azer'''", "azer''''", "az'er'", "azer.", "azer..", "azer...", "azer....", "az.er."));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "az.er.";
        assertEquals(expected, dict.getMatches(tilde).get(0).getExactMatch());
    }

    @Test
    public void azer_2points() {
        TokenizedString tilde = new TokenizedString("azer..");
        when(mockSearcher.searchPhraseInSemanticCategory(Matchers.anyString(), Matchers.anyString())).thenReturn(Arrays
                .asList("azer'", "azer''", "azer'''", "azer''''", "az'er'", "azer.", "azer..", "azer...", "azer....", "az.er."));

        ExtractFromDictionary dict = new ExtractFromDictionary(mockSnapshot, category);

        String expected = "azer..";
        assertEquals(expected, dict.getMatches(tilde).get(0).getExactMatch());
    }
}