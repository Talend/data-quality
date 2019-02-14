package org.talend.dataquality.semantic.extraction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class FieldExtractionFunctionTest {

    @Mock
    private ExtractFromSemanticType dict1;

    @Mock
    private ExtractFromSemanticType dict2;

    @Mock
    private ExtractFromSemanticType dict3;

    private String input;

    private MatchedPart match1, match1bis, match2, match2bis, match3, match3bis;

    @Before
    public void setUp() {
        Mockito.when(dict1.getCategoryName()).thenReturn("Football Club");
        Mockito.when(dict2.getCategoryName()).thenReturn("Country");
        Mockito.when(dict3.getCategoryName()).thenReturn("City");

        input = "Manchester United States Of America, Paris-Saint Germain";
        TokenizedString tokenizedInput = new TokenizedString(input);

        match1 = new MatchedPart(tokenizedInput, Arrays.asList(0, 1));
        match1bis = new MatchedPart(tokenizedInput, Arrays.asList(5, 6, 7));
        match2 = new MatchedPart(tokenizedInput, Arrays.asList(1, 2, 3, 4));
        match2bis = new MatchedPart(tokenizedInput, Arrays.asList(1, 2));
        match3 = new MatchedPart(tokenizedInput, Collections.singletonList(0));
        match3bis = new MatchedPart(tokenizedInput, Collections.singletonList(5));
    }

    @Test
    public void noConflicts() {
        Mockito.when(dict2.getMatches(Matchers.any())).thenReturn(Collections.singletonList(match2));
        Mockito.when(dict3.getMatches(Matchers.any())).thenReturn(Arrays.asList(match3, match3bis));

        FieldExtractionFunction function = new FieldExtractionFunction(Arrays.asList(dict2, dict3));

        Map<String, List<String>> expectedMatches = new HashMap<>();
        expectedMatches.put("Country", Collections.singletonList("United States Of America"));
        expectedMatches.put("City", Arrays.asList("Manchester", "Paris"));

        assertEquals(expectedMatches, function.extractFieldParts(input));
    }

    @Test
    public void conflictWithDifferentSizes() {
        Mockito.when(dict1.getMatches(Matchers.any())).thenReturn(Arrays.asList(match1, match1bis));
        Mockito.when(dict2.getMatches(Matchers.any())).thenReturn(Collections.singletonList(match2));

        FieldExtractionFunction function = new FieldExtractionFunction(Arrays.asList(dict1, dict2));

        Map<String, List<String>> expectedMatches = new HashMap<>();
        expectedMatches.put("Football Club", Collections.singletonList("Paris-Saint Germain"));
        expectedMatches.put("Country", Collections.singletonList("United States Of America"));

        assertEquals(expectedMatches, function.extractFieldParts(input));
    }

    @Test
    public void doubleConflict() {
        Mockito.when(dict1.getMatches(Matchers.any())).thenReturn(Arrays.asList(match1, match1bis));
        Mockito.when(dict2.getMatches(Matchers.any())).thenReturn(Collections.singletonList(match2));
        Mockito.when(dict3.getMatches(Matchers.any())).thenReturn(Collections.singletonList(match3));

        FieldExtractionFunction function = new FieldExtractionFunction(Arrays.asList(dict1, dict2, dict3));

        Map<String, List<String>> expectedMatches = new HashMap<>();
        expectedMatches.put("Football Club", Collections.singletonList("Paris-Saint Germain"));
        expectedMatches.put("Country", Collections.singletonList("United States Of America"));
        expectedMatches.put("City", Collections.singletonList("Manchester"));

        assertEquals(expectedMatches, function.extractFieldParts(input));
    }

    @Test
    public void conflictWithSameSizes() {
        Mockito.when(dict1.getMatches(Matchers.any())).thenReturn(Arrays.asList(match1, match1bis));
        Mockito.when(dict2.getMatches(Matchers.any())).thenReturn(Collections.singletonList(match2bis));
        Mockito.when(dict3.getMatches(Matchers.any())).thenReturn(Arrays.asList(match3, match3bis));

        FieldExtractionFunction function = new FieldExtractionFunction(Arrays.asList(dict2, dict1, dict3));

        Map<String, List<String>> expectedMatches = new HashMap<>();
        expectedMatches.put("Football Club", Collections.singletonList("Paris-Saint Germain"));
        expectedMatches.put("Country", Collections.singletonList("United States"));
        expectedMatches.put("City", Collections.singletonList("Manchester"));

        assertEquals(expectedMatches, function.extractFieldParts(input));
    }

    @Test
    public void noMatchFor1Category() {
        ExtractFromSemanticType dict = Mockito.mock(ExtractFromDictionary.class);
        Mockito.when(dict.getCategoryName()).thenReturn("NoMatch");
        Mockito.when(dict.getMatches(Matchers.any())).thenReturn(new ArrayList<>());
        Mockito.when(dict1.getMatches(Matchers.any())).thenReturn(Collections.singletonList(match1));
        Mockito.when(dict3.getMatches(Matchers.any())).thenReturn(Collections.singletonList(match3bis));

        FieldExtractionFunction function = new FieldExtractionFunction(Arrays.asList(dict, dict1, dict3));

        Map<String, List<String>> expectedMatches = new HashMap<>();
        expectedMatches.put("NoMatch", Collections.emptyList());
        expectedMatches.put("Football Club", Collections.singletonList("Manchester United"));
        expectedMatches.put("City", Collections.singletonList("Paris"));

        assertEquals(expectedMatches, function.extractFieldParts(input));
    }

    @Test
    public void emptyCategoryAfterConflicts() {
        Mockito.when(dict1.getMatches(Matchers.any())).thenReturn(Arrays.asList(match1, match1bis));
        Mockito.when(dict2.getMatches(Matchers.any())).thenReturn(Collections.singletonList(match2bis));
        Mockito.when(dict3.getMatches(Matchers.any())).thenReturn(Arrays.asList(match3, match3bis));

        FieldExtractionFunction function = new FieldExtractionFunction(Arrays.asList(dict1, dict2, dict3));

        Map<String, List<String>> expectedMatches = new HashMap<>();
        expectedMatches.put("Football Club", Arrays.asList("Manchester United", "Paris-Saint Germain"));
        expectedMatches.put("Country", Collections.emptyList());
        expectedMatches.put("City", Collections.emptyList());

        assertEquals(expectedMatches, function.extractFieldParts(input));
    }

    @Test
    public void notMocked() {
        DictionarySnapshot snapshot = new StandardDictionarySnapshotProvider().get();

        CategoryRegistryManager crm = CategoryRegistryManager.getInstance();

        ExtractFromDictionary efd_country = new ExtractFromDictionary(snapshot,
                crm.getCategoryMetadataByName(SemanticCategoryEnum.COUNTRY.getId()));
        ExtractFromDictionary efd_commune = new ExtractFromDictionary(snapshot,
                crm.getCategoryMetadataByName(SemanticCategoryEnum.FR_COMMUNE.getId()));
        ExtractFromDictionary efd_animal = new ExtractFromDictionary(snapshot,
                crm.getCategoryMetadataByName(SemanticCategoryEnum.ANIMAL.getId()));

        FieldExtractionFunction function = new FieldExtractionFunction(Arrays.asList(efd_country, efd_commune, efd_animal));

        String field = "Bear, Polar bear, Teddy Bear, Canada, the United States, clermont ferrand.";

        Map<String, List<String>> expectedMatches = new HashMap<>();
        expectedMatches.put("COUNTRY", Arrays.asList("Canada", "United States"));
        expectedMatches.put("FR_COMMUNE", Collections.singletonList("clermont ferrand"));
        expectedMatches.put("ANIMAL", Arrays.asList("Bear", "bear", "Bear"));

        assertEquals(expectedMatches, function.extractFieldParts(field));
    }
}
