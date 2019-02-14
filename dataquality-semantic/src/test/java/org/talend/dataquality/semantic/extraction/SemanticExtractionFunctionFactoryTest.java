package org.talend.dataquality.semantic.extraction;

import org.junit.Test;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class SemanticExtractionFunctionFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void createExtractionFunctionWithInvalidName() {
        List<String> categoryNames = Arrays.asList(SemanticCategoryEnum.COUNTRY.getId(), SemanticCategoryEnum.FIRST_NAME.getId(),
                "sdgfkljdfsogmjdfsgkjggjdfklghmksjdsqskbgs");

        SemanticExtractionFunctionFactory.createFieldExtractionFunction(categoryNames);
    }

    @Test
    public void createFunctionWithSemanticDictionaries() {
        List<String> categoryNames = Arrays.asList(SemanticCategoryEnum.COUNTRY.getId(), SemanticCategoryEnum.FIRST_NAME.getId(),
                SemanticCategoryEnum.FR_COMMUNE.getId());

        FieldExtractionFunction fun = SemanticExtractionFunctionFactory.createFieldExtractionFunction(categoryNames);
        assertNotNull(fun);
    }
}
