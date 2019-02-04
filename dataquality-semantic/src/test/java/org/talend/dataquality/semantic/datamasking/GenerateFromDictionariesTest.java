package org.talend.dataquality.semantic.datamasking;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class GenerateFromDictionariesTest {

    private DictionarySearcher mockSearcher;

    private LuceneIndex mockIndex;

    private UserDefinedClassifier mockUserClassifier;

    private DictionarySnapshot dictionarySnapshot;

    private Map<String, DQCategory> metadata;

    private GenerateFromDictionaries gfd;

    @Before
    public void setUp() {

        mockSearcher = Mockito.mock(DictionarySearcher.class);
        mockIndex = Mockito.mock(LuceneIndex.class);
        mockUserClassifier = Mockito.mock(UserDefinedClassifier.class);
        when(mockIndex.getSearcher()).thenReturn(mockSearcher);
        when(mockSearcher.listDocumentsByCategoryId("CAT1", 0, Integer.MAX_VALUE)).thenReturn(getDocuments(9999));

        metadata = createMetadata();
        dictionarySnapshot = Mockito.mock(DictionarySnapshot.class);
        when(dictionarySnapshot.getSharedDataDict()).thenReturn(mockIndex);
        when(dictionarySnapshot.getCustomDataDict()).thenReturn(Mockito.mock(Index.class));
        when(dictionarySnapshot.getKeyword()).thenReturn(Mockito.mock(Index.class));
        when(dictionarySnapshot.getRegexClassifier()).thenReturn(mockUserClassifier);
        when(dictionarySnapshot.getMetadata()).thenReturn(metadata);

        gfd = new GenerateFromDictionaries();
        gfd.setDictionarySnapshot(dictionarySnapshot);
    }

    @Test
    public void consistentMasking() {
        gfd.setSeed("aSeed");
        gfd.parse("CAT1", true, new Random(1234));
        gfd.setMaskingMode(FunctionMode.CONSISTENT);
        String result1 = gfd.generateMaskedRow("value1");
        String result2 = gfd.generateMaskedRow("value1");
        assertEquals("value7703", result1);
        assertEquals(result2, result1);
    }

    private List<Document> getDocuments(int number) {
        List<Document> documents = new ArrayList<>();

        Document doc = new Document();
        for (int i = 0; i < number; i++)
            doc.add(new StringField(DictionarySearcher.F_RAW, "value" + i, Field.Store.YES));

        documents.add(doc);

        return documents;
    }

    private Map<String, DQCategory> createMetadata() {
        Map<String, DQCategory> metadata = new HashMap<>();
        DQCategory cat1 = new DQCategory();
        cat1.setId("1");
        cat1.setName("CAT1");
        cat1.setType(CategoryType.DICT);

        DQCategory cat2 = new DQCategory();
        cat2.setId("2");
        cat2.setName("REGEX2");
        cat2.setType(CategoryType.REGEX);

        metadata.put("CAT1", cat1);
        metadata.put("REGEX2", cat2);
        return metadata;
    }
}
