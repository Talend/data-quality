package org.talend.dataquality.semantic.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.custom.UDCategorySerDeser;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;

public class CategoryRegistryManagerTest {

    @Test
    public void testGetRegexClassifier() throws IOException, URISyntaxException {
        final String path = "target/test_crm";
        CategoryRegistryManager.setLocalRegistryPath(path);
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
        } finally {
            FileUtils.deleteDirectory(new File(path));
            crm.reset();
        }
    }

}
