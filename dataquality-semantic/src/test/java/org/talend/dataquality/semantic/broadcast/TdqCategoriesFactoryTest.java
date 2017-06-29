package org.talend.dataquality.semantic.broadcast;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;
import org.junit.Test;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.model.DQCategory;

import static org.junit.Assert.*;

public class TdqCategoriesFactoryTest {

    @Test
    public void testCreateTdqCategories() throws IOException {
        Collection<DQCategory> expectedCategories = CategoryRegistryManager.getInstance().listCategories(false);
        TdqCategories cats = TdqCategoriesFactory.createTdqCategories();

        Map<String, DQCategory> meta = cats.getCategoryMetadata().getMetadata();
        assertEquals("Unexpected metadata size!", 75, meta.values().size());

        for (DQCategory value : expectedCategories) {
            assertTrue("This category is not found in metadata: " + value, meta.values().contains(value));
        }
    }

    @Test
    public void testCreateTdqCategoriesWithSpecifiedDictionaryCategory() throws IOException {
        TdqCategories cats = TdqCategoriesFactory.createTdqCategories(
                new HashSet<String>(Arrays.asList(new String[] { SemanticCategoryEnum.STREET_TYPE.name() })));

        Map<String, DQCategory> meta = cats.getCategoryMetadata().getMetadata();
        assertEquals("Unexpected metadata size!", 1, meta.values().size());
        assertTrue("Unexpected category found in metadata",
                meta.keySet().contains(SemanticCategoryEnum.STREET_TYPE.getTechnicalId()));

        Directory ramDir = cats.getDictionary().asDirectory();
        DirectoryReader reader = DirectoryReader.open(ramDir);
        Bits liveDocs = MultiFields.getLiveDocs(reader);
        assertEquals("Unexpected document count!", 18, reader.maxDoc());
        for (int i = 0; i < reader.maxDoc(); i++) {
            if (liveDocs != null && !liveDocs.get(i)) {
                continue;
            }
            Document doc = reader.document(i);
            String category = doc.getField(DictionarySearcher.F_CATID).stringValue();
            assertEquals("Unexpected Category!", SemanticCategoryEnum.STREET_TYPE.getTechnicalId(), category);
        }
    }

    @Test
    public void testCreateTdqCategoriesWithSpecifiedRegexCategory() throws IOException {
        TdqCategories cats = TdqCategoriesFactory
                .createTdqCategories(new HashSet<>(Arrays.asList(new String[] { SemanticCategoryEnum.EMAIL.name() })));

        Map<String, DQCategory> meta = cats.getCategoryMetadata().getMetadata();
        assertEquals("Unexpected metadata size!", 1, meta.values().size());
        assertTrue("Unexpected category found in metadata", meta.keySet().contains(SemanticCategoryEnum.EMAIL.getTechnicalId()));

        Directory ramDir = cats.getDictionary().asDirectory();
        DirectoryReader reader = DirectoryReader.open(ramDir);
        assertEquals("Unexpected document count!", 0, reader.maxDoc());

        UserDefinedClassifier udc = cats.getRegex().getRegexClassifier();
        assertEquals("Unexpected classifier count!", 1, udc.getClassifiers().size());
        assertEquals("Unexpected classifier name!", "EMAIL", udc.getClassifiers().iterator().next().getName());
    }

    @Test
    public void testSerializable() throws Exception {
        TdqCategories baseValue = TdqCategoriesFactory
                .createTdqCategories(new HashSet<>(Arrays.asList(new String[] { SemanticCategoryEnum.EMAIL.name() })));

        ObjectMapper mapper = new ObjectMapper();
        try {
            String stringVersion = mapper.writeValueAsString(baseValue);
            mapper.readValue(stringVersion, TdqCategories.class);
        } catch (JsonProcessingException jsonProcessingException) {
            fail("Cannot serialize " + TdqCategories.class + " exception was: " + jsonProcessingException);
        }
    }
}
