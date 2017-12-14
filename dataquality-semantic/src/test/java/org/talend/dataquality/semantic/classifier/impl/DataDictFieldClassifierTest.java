package org.talend.dataquality.semantic.classifier.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.ValidationMode;

public class DataDictFieldClassifierTest {

    private static final Map<String, Boolean[]> EXPECTED_VALIDATION_RESULTS_BEVERAGE = new LinkedHashMap<String, Boolean[]>() {

        private static final long serialVersionUID = 1L;

        {
            // expected validation results for 3 validation modes: SIMPLIFIED, EXACT_IGNORE_CASE_AND_ACCENT, EXACT
            put("Zundert", new Boolean[] { true, true, true }); // exact
            put("zundert", new Boolean[] { true, true, false });
            put(".zundert.", new Boolean[] { true, false, false });
            put("zun.dert", new Boolean[] { false, false, false });
            put("zun dert", new Boolean[] { false, false, false });
            put("zun#dert", new Boolean[] { false, false, false });

            put("Café Rica", new Boolean[] { true, true, true }); // exact
            put("cafe rica", new Boolean[] { true, true, false });
            put("cafe  rica!!!", new Boolean[] { true, false, false });

            put("5-hour Energy", new Boolean[] { true, true, true }); // exact
            put("5-HOUR ENERGY", new Boolean[] { true, true, false });
            put("5 hour Energy", new Boolean[] { true, false, false });
            put("5.hour Energy", new Boolean[] { true, false, false });
            put("5.hour  Energy", new Boolean[] { true, false, false });
            put("5hour Energy", new Boolean[] { false, false, false }); // ?

            put("P.i.n.k Vodka", new Boolean[] { true, true, true }); // exact
            put("P.i.n.k. Vodka", new Boolean[] { true, false, false });
            put("P.i.n.k-Vodka", new Boolean[] { true, false, false });
            put("P.i.n.k.Vodka", new Boolean[] { false, false, false });
            put("P.i.n.k-!#  Vodka", new Boolean[] { true, false, false });
            put("P i n k Vodka", new Boolean[] { false, false, false });
            put("Pink Vodka", new Boolean[] { false, false, false });
            put("P-i-n-k Vodka", new Boolean[] { false, false, false });
        }
    };

    private static final Map<String, Boolean[]> EXPECTED_VALIDATION_RESULTS_FR_COMMUNE = new LinkedHashMap<String, Boolean[]>() {

        private static final long serialVersionUID = 1L;

        {
            // expected validation results for 3 validation modes: SIMPLIFIED, EXACT_IGNORE_CASE_AND_ACCENT, EXACT
            put("Clermont-Ferrand", new Boolean[] { true, true, true }); // exact
            put("clermont-ferrand", new Boolean[] { true, true, false }); // exact
            put("Clermont Ferrand", new Boolean[] { true, false, false });
            put("Clermont.Ferrand", new Boolean[] { false, false, false });
        }
    };

    @Test
    public void testValidCategoriesBeverage() throws IOException {
        CategoryRegistryManager.setLocalRegistryPath("target/testValidCategoriesBeverage");
        Index ddClassifier = CategoryRegistryManager.getInstance().getCustomDictionaryHolder().getDictionarySnapshot()
                .getSharedDataDict();

        DQCategory category = CategoryRegistryManager.getInstance().getCategoryMetadataByName("BEVERAGE");
        category.setCompleteness(Boolean.TRUE);

        for (String input : EXPECTED_VALIDATION_RESULTS_BEVERAGE.keySet()) {
            category.setValidationMode(ValidationMode.SIMPLIFIED);
            assertEquals("Validating [" + input + "] in mode SIMPLIFIED", EXPECTED_VALIDATION_RESULTS_BEVERAGE.get(input)[0],
                    ddClassifier.validCategories(input, category, null));

            category.setValidationMode(ValidationMode.EXACT_IGNORE_CASE_AND_ACCENT);
            assertEquals("Validating [" + input + "] in mode EXACT_IGNORE", EXPECTED_VALIDATION_RESULTS_BEVERAGE.get(input)[1],
                    ddClassifier.validCategories(input, category, null));

            category.setValidationMode(ValidationMode.EXACT);
            assertEquals("Validating [" + input + "] in mode EXACT", EXPECTED_VALIDATION_RESULTS_BEVERAGE.get(input)[2],
                    ddClassifier.validCategories(input, category, null));
        }
        category.setCompleteness(Boolean.FALSE);
    }

    @Test
    public void testValidCategoriesWithFrCommune() throws IOException {
        CategoryRegistryManager.reset();
        CategoryRegistryManager.setUsingLocalCategoryRegistry(false);
        CategoryRegistryManager.setLocalRegistryPath("target/testValidCategoriesWithFrCommune");
        Index ddClassifier = CategoryRegistryManager.getInstance().getCustomDictionaryHolder().getDictionarySnapshot()
                .getSharedDataDict();

        DQCategory category = CategoryRegistryManager.getInstance().getCategoryMetadataByName("FR_COMMUNE");

        for (String input : EXPECTED_VALIDATION_RESULTS_FR_COMMUNE.keySet()) {
            category.setValidationMode(ValidationMode.SIMPLIFIED);
            assertEquals("Validating [" + input + "] in mode SIMPLIFIED", EXPECTED_VALIDATION_RESULTS_FR_COMMUNE.get(input)[0],
                    ddClassifier.validCategories(input, category, null));

            category.setValidationMode(ValidationMode.EXACT_IGNORE_CASE_AND_ACCENT);
            assertEquals("Validating [" + input + "] in mode EXACT_IGNORE", EXPECTED_VALIDATION_RESULTS_FR_COMMUNE.get(input)[1],
                    ddClassifier.validCategories(input, category, null));

            category.setValidationMode(ValidationMode.EXACT);
            assertEquals("Validating [" + input + "] in mode EXACT", EXPECTED_VALIDATION_RESULTS_FR_COMMUNE.get(input)[2],
                    ddClassifier.validCategories(input, category, null));
        }
    }
}
