package org.talend.dataquality.semantic.index;

import org.junit.Test;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DictionarySearcherTest {

    @Test
    public void dontFindOvercompletePhrase() throws URISyntaxException {
        final URI ddPath = CategoryRegistryManager.getInstance().getDictionaryURI();
        final LuceneIndex dataDictIndex = new LuceneIndex(ddPath, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
        String input = "United States of America";
        List<String> listDocs = dataDictIndex.getSearcher()
                .searchPhraseInSemanticCategory(SemanticCategoryEnum.COUNTRY.getTechnicalId(), input);
        assertFalse(listDocs.contains("United States"));
    }

    @Test
    public void findIncompletePhrase() throws URISyntaxException {
        final URI ddPath = CategoryRegistryManager.getInstance().getDictionaryURI();
        final LuceneIndex dataDictIndex = new LuceneIndex(ddPath, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
        String input = "United";
        List<String> listDocs = dataDictIndex.getSearcher()
                .searchPhraseInSemanticCategory(SemanticCategoryEnum.COUNTRY.getTechnicalId(), input);
        assertTrue(listDocs.contains("United States"));
    }

    @Test
    public void findPhraseWithAccent() throws URISyntaxException {
        final URI ddPath = CategoryRegistryManager.getInstance().getDictionaryURI();
        final LuceneIndex dataDictIndex = new LuceneIndex(ddPath, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
        String input = "Corée du Nord";
        List<String> listDocs = dataDictIndex.getSearcher()
                .searchPhraseInSemanticCategory(SemanticCategoryEnum.COUNTRY.getTechnicalId(), input);
        assertTrue(listDocs.contains(input));
    }
}