package org.talend.dataquality.semantic.datamasking;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.datamasking.model.CategoryValues;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;
import org.talend.dataquality.semantic.validator.GenerateValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class GenerateFromCompoundTest {

    private DictionarySearcher mockSearcher;

    private LuceneIndex mockIndex;

    private UserDefinedClassifier mockUserClassifier;

    private GenerateFromCompound generate;

    private DictionarySnapshot dictionarySnapshot;

    private Map<String, DQCategory> metadata;

    @Before
    public void setUp() {

        mockSearcher = Mockito.mock(DictionarySearcher.class);
        mockIndex = Mockito.mock(LuceneIndex.class);
        mockUserClassifier = Mockito.mock(UserDefinedClassifier.class);
        when(mockIndex.getSearcher()).thenReturn(mockSearcher);
        when(mockSearcher.listDocumentsByCategoryId("CAT1")).thenReturn(getDocuments("1", 9999));

        metadata = createMetadata();
        dictionarySnapshot = Mockito.mock(DictionarySnapshot.class);
        when(dictionarySnapshot.getSharedDataDict()).thenReturn(mockIndex);
        when(dictionarySnapshot.getCustomDataDict()).thenReturn(Mockito.mock(Index.class));
        when(dictionarySnapshot.getKeyword()).thenReturn(Mockito.mock(Index.class));
        when(dictionarySnapshot.getRegexClassifier()).thenReturn(mockUserClassifier);
        when(dictionarySnapshot.getMetadata()).thenReturn(metadata);

        generate = new GenerateFromCompound();
        generate.setDictionarySnapshot(dictionarySnapshot);
        generate.setRandom(new Random(1234));
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

    @Test
    public void doGenerateMaskedFieldWithDistribution() {
        when(dictionarySnapshot.getDQCategoryByName("CAT1")).thenReturn(metadata.get("CAT1"));
        when(mockIndex.validCategories(eq("value1"), eq(metadata.get("CAT1")), eq(null))).thenReturn(true);

        generate.setCategoryValues(createCategoryValuesWithDict());
        generate.parse("1", true);
        String result = generate.doGenerateMaskedField("value1");
        assertEquals("value3", result);
    }

    @Test
    public void doGenerateMaskedFieldWithRegex() {
        when(dictionarySnapshot.getDQCategoryByName("REGEX2")).thenReturn(metadata.get("REGEX2"));
        when(mockUserClassifier.validCategories(eq("a"), eq(metadata.get("REGEX2")), eq(null))).thenReturn(true);

        generate.setCategoryValues(createCategoryValuesWithRegex());
        generate.parse("2", true);
        String result = generate.doGenerateMaskedField("a");
        assertEquals("y", result);
    }

    @Test
    public void doGenerateMaskedFieldWithCompoundDictAndRegex() {
        when(dictionarySnapshot.getDQCategoryByName("CAT1")).thenReturn(metadata.get("CAT1"));
        when(dictionarySnapshot.getDQCategoryByName("REGEX2")).thenReturn(metadata.get("REGEX2"));
        when(mockIndex.validCategories(eq("value1"), eq(metadata.get("CAT1")), eq(null))).thenReturn(true);
        when(mockUserClassifier.validCategories(eq("a"), eq(metadata.get("REGEX2")), eq(null))).thenReturn(true);

        generate.setCategoryValues(createCategoryValuesWithCompound());
        generate.parse("1", true);
        String result = generate.doGenerateMaskedField("a");
        assertEquals("y", result);
    }

    @Test
    public void repeatableMaskCompoundMultiMatchedRegex() { //TDQ-16673: Consistent repeatable SemanticType masking on Phone number
        DictionarySnapshot snapshotCompound = new StandardDictionarySnapshotProvider().get();
        final DQCategory dqCategory = snapshotCompound.getDQCategoryByName(SemanticCategoryEnum.PHONE.name());
        List types = GenerateValidator.initSemanticTypes(snapshotCompound, dqCategory, null);

        GenerateFromCompound generateFromCompound = new GenerateFromCompound();
        if (types.size() > 0) {
            generateFromCompound.setDictionarySnapshot(snapshotCompound);
            generateFromCompound.setCategoryValues(types);
        }
        generateFromCompound.setSeed("azer1!");
        generateFromCompound.setMaskingMode(FunctionMode.CONSISTENT);

        final String originalMaskedPhone = "0714-1438-85";
        final String expectedMaskedPhone = "089 61994681";
        String result1 = generateFromCompound.doGenerateMaskedField(originalMaskedPhone, FunctionMode.CONSISTENT);
        String result2 = generateFromCompound.doGenerateMaskedField(originalMaskedPhone, FunctionMode.CONSISTENT);
        assertEquals(expectedMaskedPhone, result1);
        assertEquals(expectedMaskedPhone, result2);
    }

    private List<CategoryValues> createCategoryValuesWithCompound() {
        List<CategoryValues> values = new ArrayList<>();
        values.addAll(createCategoryValuesWithDict());
        values.addAll(createCategoryValuesWithRegex());
        return values;
    }

    private List<CategoryValues> createCategoryValuesWithDict() {
        List<CategoryValues> values = new ArrayList<>();

        CategoryValues dict = new CategoryValues();
        dict.setCategoryId("1");
        dict.setName("CAT1");
        dict.setType(CategoryType.DICT);
        dict.setValue(Arrays.asList("value1", "value2", "value3"));

        values.add(dict);

        return values;
    }

    private List<CategoryValues> createCategoryValuesWithRegex() {
        List<CategoryValues> values = new ArrayList<>();

        CategoryValues regex = new CategoryValues();
        regex.setCategoryId("2");
        regex.setName("REGEX2");
        regex.setType(CategoryType.REGEX);
        regex.setValue("^[a-z]$");

        values.add(regex);

        return values;
    }

    private List<Document> getDocuments(String id, int number) {
        List<Document> documents = new ArrayList<>();

        Document doc = new Document();
        for (int i = 0; i < number; i++)
            doc.add(new StringField(DictionarySearcher.F_RAW, "value" + i, Field.Store.YES));

        documents.add(doc);

        return documents;
    }
}
