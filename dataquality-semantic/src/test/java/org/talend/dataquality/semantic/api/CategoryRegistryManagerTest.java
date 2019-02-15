package org.talend.dataquality.semantic.api;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;

public class CategoryRegistryManagerTest extends CategoryRegistryManagerAbstract {

    @Test
    public void testRegexClassifierCount() {

        CategoryRegistryManager crm = CategoryRegistryManager.getInstance();
        UserDefinedClassifier userDefinedClassifier = crm.getCustomDictionaryHolder().getRegexClassifier();

        // getRegexClassifier before deletion
        assertEquals("Unexpected size of classifiers", 42, userDefinedClassifier.getClassifiers().size());
    }

    @Test
    public void testGetRegexClassifierMultiAccessWay() {
        CategoryRegistryManager crm = CategoryRegistryManager.getInstance();
        Set<ISubCategory> direct = crm.getRegexClassifier().getClassifiers();
        Set<ISubCategory> byCustomDictionaryHolder = crm.getCustomDictionaryHolder().getRegexClassifier().getClassifiers();

        assertEquals(direct, byCustomDictionaryHolder);
    }

    @Test
    public void testGetRegexClassifierTalend() {
        // GIVEN
        CategoryRegistryManager crm = CategoryRegistryManager.getInstance();
        UserDefinedClassifier userDefinedClassifier = crm.getCustomDictionaryHolder().getRegexClassifier();

        DQCategory regexCategory = crm.getCategoryMetadataById(SemanticCategoryEnum.EMAIL.getTechnicalId());

        int beforeSize = userDefinedClassifier.getClassifiers().size();

        // WHEN
        crm.getCustomDictionaryHolder().deleteCategory(regexCategory);
        crm.reloadCategoriesFromRegistry();

        userDefinedClassifier = crm.getCustomDictionaryHolder().getRegexClassifier();

        // THEN
        assertEquals(beforeSize - 1, userDefinedClassifier.getClassifiers().size());
    }

    @Test
    public void testFindMostSimilarValue() {
        String result = CategoryRegistryManager.getInstance().findMostSimilarValue("Franc", "COUNTRY", 0.8);
        assertEquals("France", result);
    }

    @Test
    public void testFindMostSimilarValue_ValueIsNull() {
        String result = CategoryRegistryManager.getInstance().findMostSimilarValue(null, "COUNTRY", 0.8);
        assertEquals(null, result);
    }

    @Test
    public void testFindMostSimilarValue_CategoryIsNull() {
        String result = CategoryRegistryManager.getInstance().findMostSimilarValue("the input", null, 0.8);
        assertEquals("the input", result);
    }

    @Test
    public void testFindMostSimilarValue_ValueNotExist() {
        String result = CategoryRegistryManager.getInstance().findMostSimilarValue("Fran", "COUNTRY", 0.8);
        assertEquals("Fran", result);
    }

    @Test
    public void testFindMostSimilarValue_CategoryNotExist() {
        String result = CategoryRegistryManager.getInstance().findMostSimilarValue("Fran", "NonExistingCategory", 0.8);
        assertEquals("Fran", result);
    }

    @Test
    public void testFindMostSimilarValueWithCustomDataDict() throws IOException {
        CustomDictionaryHolder holder = CategoryRegistryManager.getInstance().getCustomDictionaryHolder();

        DQCategory answerCategory = holder.getMetadata().get(SemanticCategoryEnum.ANSWER.getTechnicalId());
        DQCategory categoryClone = SerializationUtils.clone(answerCategory); // make a clone instead of modifying the shared
                                                                             // category metadata
        categoryClone.setModified(true);
        holder.updateCategory(categoryClone);

        DQDocument newDoc = new DQDocument();
        newDoc.setCategory(categoryClone);
        newDoc.setId("the_doc_id");
        newDoc.setValues(new HashSet<>(Arrays.asList("true", "false")));
        holder.addDataDictDocuments(Collections.singletonList(newDoc));

        String result = CategoryRegistryManager.getInstance().findMostSimilarValue("TRUEL", SemanticCategoryEnum.ANSWER.name(),
                0.8);
        assertEquals("true", result);
    }

    @Test
    public void testGetLuceneIndex() {
        String categoryName = "COUNTRY";
        LuceneIndex luceneIndex = CategoryRegistryManager.getInstance().getLuceneIndex(categoryName);
        Assert.assertNotNull(luceneIndex);
    }

}
