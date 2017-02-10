package org.talend.dataquality.semantic.recognizer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import org.talend.dataquality.semantic.classifier.custom.UDCategorySerDeser;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;

public class InvalidRegexCategorizerTest {

    @Test
    public void testReadJsonFileWithInvalidRegex() throws IOException, URISyntaxException {
        URI uri = InvalidRegexCategorizerTest.class.getResource("invalid_regex_categorizer.json").toURI();
        try {
            UserDefinedClassifier userDefinedClassifier = UDCategorySerDeser.readJsonFile(uri);
            assertNotNull(userDefinedClassifier);
            int nbCat = userDefinedClassifier.getClassifiers().size();
            assertEquals("Expected to read at least 0 category but only get " + nbCat, 1, nbCat); //$NON-NLS-1$
            assertFalse("Any data should be invalid when the pattern is invalid",
                    userDefinedClassifier.validCategory("AZERTY", "CATEGORY_WITH_INVALID_REGEX"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
