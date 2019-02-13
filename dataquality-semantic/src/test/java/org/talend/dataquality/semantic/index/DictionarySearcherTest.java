package org.talend.dataquality.semantic.index;

import org.junit.Test;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DictionarySearcherTest {

    @Test
    public void findPhraseInCategory() throws URISyntaxException, IOException {
        final URI ddPath = CategoryRegistryManager.getInstance().getDictionaryURI();
        final LuceneIndex dataDictIndex = new LuceneIndex(ddPath, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
        List<String> tokens = Arrays.asList("Clermont", "Ferrand", "Slip");
        List<String> phrase = new ArrayList<>();
        List<String> listDocs = new ArrayList<>();
        for (String token : tokens) {
            phrase.add(token);
            listDocs.addAll(dataDictIndex.getSearcher()
                    .searchPhraseInSemanticCategory(SemanticCategoryEnum.FR_COMMUNE.getTechnicalId(), phrase));
        }
        //System.out.println(listDocs.size());
    }
}