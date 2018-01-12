package org.talend.dataquality.semantic.broadcast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;
import org.junit.Test;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.common.inference.Analyzers.Result;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.CustomDictionaryHolder;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;
import org.talend.dataquality.semantic.statistics.SemanticQualityAnalyzer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TdqCategoriesFactoryTest extends CategoryRegistryManagerAbstract {

    @Test
    public void testCreateFullTdqCategories() throws IOException {
        Collection<DQCategory> expectedCategories = CategoryRegistryManager.getInstance().listCategories(false);
        TdqCategories cats = TdqCategoriesFactory.createFullTdqCategories();

        Map<String, DQCategory> meta = cats.getCategoryMetadata().getMetadata();
        assertEquals("Unexpected metadata size!", 75, meta.values().size());

        for (DQCategory value : expectedCategories) {
            assertTrue("This category is not found in metadata: " + value, meta.values().contains(value));
        }
    }

    @Test
    public void testCreateDefaultTdqCategories() throws IOException {
        TdqCategories cats = TdqCategoriesFactory.createSharedTdqCategories();
        assertEquals("Unexpected metadata size!", 75, cats.getCategoryMetadata().getMetadata().size());
        assertEquals("Unexpected document size in shared data dict!", 45829, cats.getDictionary().getDocumentList().size());
        assertEquals("Unexpected document size in custom data dict!", 0, cats.getCustomDictionary().getDocumentList().size());
        assertEquals("Unexpected document size in shared keyword!", 0, cats.getKeyword().getDocumentList().size());
        assertEquals("Unexpected regex classifier size!", 47, cats.getRegex().getRegexClassifier().getClassifiers().size());
    }

    private final List<String[]> TEST_RECORDS_TAGADA = new ArrayList<String[]>() {

        private static final long serialVersionUID = 1L;

        {
            add(new String[] { "1", "Williams", "John", "40", "10/09/1940", "false" });
            add(new String[] { "2", "Bowie", "David", "67", "01/08/1947", "true" });
            add(new String[] { "3", "Cruise", "Tom", "55", "03/07/1962", "FR" });
        }
    };

    @Test
    public void testCreateTdqCategoriesWithModifiedCategories() throws IOException {
        CustomDictionaryHolder holder = CategoryRegistryManager.getInstance().getCustomDictionaryHolder();

        DQCategory answerCategory = holder.getMetadata().get(SemanticCategoryEnum.COUNTRY_CODE_ISO2.getTechnicalId());
        DQCategory categoryClone = SerializationUtils.clone(answerCategory); // make a clone instead of modifying the shared
                                                                             // category metadata
        categoryClone.setModified(true);
        holder.updateCategory(categoryClone);

        DQDocument newDoc = new DQDocument();
        newDoc.setCategory(categoryClone);
        newDoc.setId("the_doc_id");
        newDoc.setValues(new HashSet<>(Arrays.asList("true", "false")));
        holder.addDataDictDocuments(Collections.singletonList(newDoc));

        TdqCategories tdqCategories = TdqCategoriesFactory.createFullTdqCategories();

        final List<String> EXPECTED_CATEGORIES = Arrays.asList(new String[] { "", SemanticCategoryEnum.LAST_NAME.name(),
                SemanticCategoryEnum.FIRST_NAME.name(), "", "", SemanticCategoryEnum.COUNTRY_CODE_ISO2.name() });

        SemanticQualityAnalyzer semanticQualityAnalyzer = new SemanticQualityAnalyzer(tdqCategories.asDictionarySnapshot(),
                EXPECTED_CATEGORIES.toArray(new String[EXPECTED_CATEGORIES.size()]));

        Analyzer<Result> analyzer = Analyzers.with(semanticQualityAnalyzer);
        analyzer.init();
        for (String[] record : TEST_RECORDS_TAGADA) {
            analyzer.analyze(record);
        }
        analyzer.end();

        Result result = analyzer.getResult().get(5); // result for the last column

        if (result.exist(ValueQualityStatistics.class)) {
            final ValueQualityStatistics valueQualityStats = result.get(ValueQualityStatistics.class);
            assertEquals("Unexpected valid count!", 3L, valueQualityStats.getValidCount());
            assertEquals("Unexpected invalid count!", 0L, valueQualityStats.getInvalidCount());
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
