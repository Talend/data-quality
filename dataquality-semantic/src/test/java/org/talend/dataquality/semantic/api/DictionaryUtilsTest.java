package org.talend.dataquality.semantic.api;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.junit.Test;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.ValidationMode;

public class DictionaryUtilsTest {

    @Test
    public void categoryFromAndToDocument() {
        DQCategory category = new DQCategory();
        category.setId("id");
        category.setName("name");
        category.setLabel("label");
        category.setValidationMode(ValidationMode.EXACT_IGNORE_CASE_AND_ACCENT);
        category.setType(CategoryType.DICT);
        category.setCompleteness(true);
        category.setDescription("description");
        List<DQCategory> children = new ArrayList<>();
        DQCategory child = new DQCategory();
        child.setId("child");
        children.add(child);
        category.setChildren(children);

        Document doc = DictionaryUtils.categoryToDocument(category);

        DQCategory categoryRes = DictionaryUtils.categoryFromDocument(doc);

        assertTrue(category.getId().equals(categoryRes.getId()));
        assertTrue(category.getName().equals(categoryRes.getName()));
        assertTrue(category.getLabel().equals(categoryRes.getLabel()));
        assertTrue(category.getType().equals(categoryRes.getType()));
        assertTrue(category.getCompleteness().equals(categoryRes.getCompleteness()));
        assertTrue(category.getDescription().equals(categoryRes.getDescription()));
        assertTrue(category.getValidationMode().equals(categoryRes.getValidationMode()));
        for (int i = 0; i < category.getChildren().size(); i++)
            assertTrue(category.getChildren().get(i).getId().equals(categoryRes.getChildren().get(i).getId()));
    }
}
