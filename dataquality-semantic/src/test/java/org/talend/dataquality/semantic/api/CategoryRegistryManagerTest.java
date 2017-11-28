package org.talend.dataquality.semantic.api;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.custom.UDCategorySerDeser;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CategoryRegistryManagerTest extends CategoryRegistryManagerAbstract {

    @Test
    public void testGetRegexClassifier() throws IOException, URISyntaxException {
        CategoryRegistryManager crm = CategoryRegistryManager.getInstance();
        try {

            final UserDefinedClassifier udc = crm.getRegexClassifier(false);
            final Set<ISubCategory> classifiers = udc.getClassifiers();
            assertEquals("Unexpected size of classifiers", 47, classifiers.size());

            // put less categories back into the file
            final Set<ISubCategory> lessClassifiers = new HashSet<>();
            for (ISubCategory classifier : classifiers) {
                if (!"EMAIL".equals(classifier.getName())) {
                    lessClassifiers.add(classifier);
                }
            }

            UserDefinedClassifier newUdc = new UserDefinedClassifier();
            newUdc.setClassifiers(lessClassifiers);

            final UDCategorySerDeser udcWriter = new UDCategorySerDeser();
            udcWriter.writeToJsonFile(newUdc, new FileOutputStream(new File(crm.getRegexURI())));

            // getRegexClassifier without reloading
            final UserDefinedClassifier notTrulyReloaded = crm.getRegexClassifier(false);
            assertEquals("Unexpected size of classifiers", 47, notTrulyReloaded.getClassifiers().size());

            // reload and assert
            final UserDefinedClassifier trulyReloaded = crm.getRegexClassifier(true);
            assertEquals("Unexpected size of classifiers", 46, trulyReloaded.getClassifiers().size());

        } catch (Exception e) {
            fail("Failed due to exception: " + e.getMessage());
        }
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
    public void testGetLuncenIndex() {
        String categoryName = "COUNTRY";
        LuceneIndex lnceneIndex = CategoryRegistryManager.getInstance().getLuceneIndex(categoryName);
        Assert.assertNotNull(lnceneIndex);
    }

}
