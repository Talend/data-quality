// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.semantic.statistics;

import static org.junit.Assert.assertEquals;
import static org.talend.dataquality.semantic.TestUtils.mockWithTenant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.common.inference.Analyzers.Result;
import org.talend.dataquality.common.inference.Metadata;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.CustomDictionaryHolder;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;
import org.talend.dataquality.semantic.model.DQRegEx;
import org.talend.dataquality.semantic.model.DQValidator;
import org.talend.dataquality.semantic.model.MainCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TenancyContextHolder.class })
public class SemanticAnalyzerTest extends CategoryRegistryManagerAbstract {

    private final List<String[]> TEST_RECORDS = new ArrayList<String[]>() {

        private static final long serialVersionUID = 1L;

        {
            add(new String[] { "veau" });
            add(new String[] { "United States" });
            add(new String[] { "France" });
        }
    };

    private final List<String[]> TEST_RECORDS_TAGADA = new ArrayList<String[]>() {

        private static final long serialVersionUID = 1L;

        {
            add(new String[] { "1", "Williams", "John", "40", "10/09/1940", "false" });
            add(new String[] { "2", "Bowie", "David", "67", "01/08/1947", "true" });
        }
    };

    private final List<String> EXPECTED_CATEGORY_TAGADA = Arrays.asList(
            new String[] { "", SemanticCategoryEnum.LAST_NAME.name(), SemanticCategoryEnum.FIRST_NAME.name(), "", "", "" });

    private final List<String[]> TEST_RECORDS_CITY_METADATA = new ArrayList<String[]>() {

        private static final long serialVersionUID = 1L;

        {
            add(new String[] { "1", "Washington" });
            add(new String[] { "2", "Washington" });
            add(new String[] { "3", "Washington" });
            add(new String[] { "4", "Washington" });
            add(new String[] { "5", "Washington" });
            add(new String[] { "6", "Washington" });
            add(new String[] { "7", "Washington" });
            add(new String[] { "8", "Washington" });
            add(new String[] { "9", "Washington" });
            add(new String[] { "10", "Washington" });
            add(new String[] { "11", "Washington" });
            add(new String[] { "12", "Washington" });
            add(new String[] { "13", "Washington" });
            add(new String[] { "14", "Washington" });
            add(new String[] { "15", "Washington" });
            add(new String[] { "16", "Washington" });
            add(new String[] { "17", "Washington" });
            add(new String[] { "18", "New York" });
            add(new String[] { "19", "+33688052266" });
            add(new String[] { "20", "+33688052266" });
        }
    };

    private final List<String> EXPECTED_FR_COMMUNE_CATEGORY_METADATA = Arrays
            .asList(new String[] { "", SemanticCategoryEnum.LAST_NAME.name() });

    private final List<String[]> TEST_RECORDS_PHONE_METADATA = new ArrayList<String[]>() {

        private static final long serialVersionUID = 1L;

        {
            add(new String[] { "08 25 01 20 11" });
            add(new String[] { "+33123456789" });
            add(new String[] { "+1 (555) 457-2154" });
            add(new String[] { "+509 7845 2156" });
        }
    };

    private final List<String> EXPECTED_PHONE_CATEGORY_METADATA = Arrays
            .asList(new String[] { SemanticCategoryEnum.PHONE.name() });

    private DictionarySnapshot dictionarySnapshot;

    @Test
    public void testTagada() {
        mockWithTenant("testTagada");
        testSemanticAnalyzer(TEST_RECORDS_TAGADA, null, EXPECTED_CATEGORY_TAGADA);
    }

    @Test
    public void testTagadaWithCustomMetadata() {
        mockWithTenant("t_custom_meta");
        CustomDictionaryHolder holder = CategoryRegistryManager.getInstance().getCustomDictionaryHolder();

        DQCategory firstNameCat = holder.getMetadata().get(SemanticCategoryEnum.FIRST_NAME.getTechnicalId());
        firstNameCat.setDeleted(true);
        holder.updateCategory(firstNameCat);
        dictionarySnapshot = new StandardDictionarySnapshotProvider().get();

        final List<String> EXPECTED_CATEGORIES = Arrays.asList(
                new String[] { "", SemanticCategoryEnum.LAST_NAME.name(), SemanticCategoryEnum.LAST_NAME.name(), "", "", "" });

        testSemanticAnalyzer(TEST_RECORDS_TAGADA, null, EXPECTED_CATEGORIES);
    }

    @Test
    public void testTagadaWithCustomDataDict() throws IOException {
        mockWithTenant("testTagadaWithCustomDataDict");
        CustomDictionaryHolder holder = CategoryRegistryManager.getInstance().getCustomDictionaryHolder();

        DQCategory answerCategory = holder.getMetadata().get(SemanticCategoryEnum.ANSWER.getTechnicalId());

        holder.updateCategory(answerCategory);

        DQDocument newDoc = new DQDocument();
        newDoc.setCategory(answerCategory);
        newDoc.setId("the_doc_id");
        newDoc.setValues(new HashSet<>(Arrays.asList("true", "false")));
        holder.addDataDictDocuments(Collections.singletonList(newDoc));
        dictionarySnapshot = new StandardDictionarySnapshotProvider().get();

        final List<String> EXPECTED_CATEGORIES = Arrays.asList(new String[] { "", SemanticCategoryEnum.LAST_NAME.name(),
                SemanticCategoryEnum.FIRST_NAME.name(), "", "", SemanticCategoryEnum.ANSWER.name() });

        testSemanticAnalyzer(TEST_RECORDS_TAGADA, null, EXPECTED_CATEGORIES);

    }

    @Test
    public void testTagadaWithCustomRegex() {
        mockWithTenant("t_custom_re");
        CustomDictionaryHolder holder = CategoryRegistryManager.getInstance().getCustomDictionaryHolder();
        DQValidator dqValidator = new DQValidator();
        dqValidator.setPatternString("^(true|false)$");
        DQRegEx dqRegEx = new DQRegEx();
        dqRegEx.setMainCategory(MainCategory.Alpha);
        dqRegEx.setValidator(dqValidator);
        DQCategory dqCat = new DQCategory("the_id");
        dqCat.setName("the_name");
        dqCat.setLabel("the_label");
        dqCat.setDescription("the_description");
        dqCat.setRegEx(dqRegEx);
        dqCat.setType(CategoryType.REGEX);
        dqCat.setCompleteness(Boolean.TRUE);
        dqCat.setModified(Boolean.TRUE);
        holder.updateCategory(dqCat);
        dictionarySnapshot = new StandardDictionarySnapshotProvider().get();

        final List<String> EXPECTED_CATEGORIES = Arrays.asList(new String[] { "", SemanticCategoryEnum.LAST_NAME.name(),
                SemanticCategoryEnum.FIRST_NAME.name(), "", "", "the_name" });

        testSemanticAnalyzer(TEST_RECORDS_TAGADA, null, EXPECTED_CATEGORIES);

    }

    @Test
    public void firstNameToFRCommune() {
        // 85% last name
        // 90% city
        // and column name is city
        mockWithTenant("firstNameToFRCommune");
        testSemanticAnalyzer(TEST_RECORDS_CITY_METADATA, Arrays.asList("", "Last Name"), EXPECTED_FR_COMMUNE_CATEGORY_METADATA);
    }

    @Test
    public void metadataLastNameWithPhoneNumber() {
        mockWithTenant("metadataLastNameWithPhoneNumber");
        testSemanticAnalyzer(TEST_RECORDS_PHONE_METADATA, Arrays.asList("Last Name"), EXPECTED_PHONE_CATEGORY_METADATA);
    }

    @Test
    public void semanticTypeNameFuzzyMatching() { // TDQ-14062: Fuzzy matching on the semantic type name
        mockWithTenant("semanticTypeNameFuzzyMatching");
        // 1. test levenshtein
        testSemanticAnalyzer(TEST_RECORDS_CITY_METADATA, Arrays.asList("", "Lost Names"), EXPECTED_FR_COMMUNE_CATEGORY_METADATA);
        // 2. test tokenization with any order
        testSemanticAnalyzer(TEST_RECORDS_CITY_METADATA, Arrays.asList("", "Name Last"), EXPECTED_FR_COMMUNE_CATEGORY_METADATA);
        // 3. test fingerprint
        testSemanticAnalyzer(TEST_RECORDS_CITY_METADATA, Arrays.asList("", "läst namê"), EXPECTED_FR_COMMUNE_CATEGORY_METADATA);
    }

    @Test
    public void testSetLimit() {

        mockWithTenant("testSetLimit");
        dictionarySnapshot = new StandardDictionarySnapshotProvider().get();
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(dictionarySnapshot);

        semanticAnalyzer.setLimit(0);

        assertEquals("Unexpected Category.", SemanticCategoryEnum.COUNTRY.getId(), getSuggestedCategories(semanticAnalyzer));

        semanticAnalyzer.setLimit(1);
        assertEquals("Unexpected Category.", SemanticCategoryEnum.ANIMAL.getId(), getSuggestedCategories(semanticAnalyzer));

        semanticAnalyzer.setLimit(3);
        assertEquals("Unexpected Category.", SemanticCategoryEnum.COUNTRY.getId(), getSuggestedCategories(semanticAnalyzer));
    }

    private String getSuggestedCategories(SemanticAnalyzer semanticAnalyzer) {
        Analyzer<Result> analyzer = Analyzers.with(semanticAnalyzer);
        analyzer.init();
        for (String[] record : TEST_RECORDS) {
            analyzer.analyze(record);
        }
        analyzer.end();
        Result result = analyzer.getResult().get(0);

        if (result.exist(SemanticType.class)) {
            final SemanticType semanticType = result.get(SemanticType.class);
            final String suggestedCategory = semanticType.getSuggestedCategory();
            return suggestedCategory;
        }
        return null;
    }

    @Test
    public void testRefreshIndex_TDQ14562() {
        final String tenantID = "t_tdq14562";
        mockWithTenant(tenantID);
        CustomDictionaryHolder holder = CategoryRegistryManager.getInstance().getCustomDictionaryHolder();

        // Run the analysis for a first time
        final List<String> EXPECTED_CATEGORIES_BEFORE_MODIF = Arrays.asList(
                new String[] { "", SemanticCategoryEnum.LAST_NAME.name(), SemanticCategoryEnum.FIRST_NAME.name(), "", "", "" });

        testSemanticAnalyzer(TEST_RECORDS_TAGADA, null, EXPECTED_CATEGORIES_BEFORE_MODIF);

        // Create a new category
        DQValidator dqValidator = new DQValidator();
        dqValidator.setPatternString("^(true|false)$");
        DQRegEx dqRegEx = new DQRegEx();
        dqRegEx.setMainCategory(MainCategory.Alpha);
        dqRegEx.setValidator(dqValidator);
        DQCategory dqCat = new DQCategory("the_id");
        dqCat.setName("the_name");
        dqCat.setLabel("the_label");
        dqCat.setDescription("the_description");
        dqCat.setRegEx(dqRegEx);
        dqCat.setType(CategoryType.REGEX);
        dqCat.setCompleteness(Boolean.TRUE);
        dqCat.setModified(Boolean.TRUE);
        holder.createCategory(dqCat);
        // Run the analysis for a second time

        // after fixing the issue, the expected category of last column must be "the_name" instead of ""
        final List<String> EXPECTED_CATEGORIES_AFTER_MODIF = Arrays.asList(new String[] { "",
                SemanticCategoryEnum.LAST_NAME.name(), SemanticCategoryEnum.FIRST_NAME.name(), "", "", "the_name" });

        testSemanticAnalyzer(TEST_RECORDS_TAGADA, null, EXPECTED_CATEGORIES_AFTER_MODIF);

    }

    private void testSemanticAnalyzer(List<String[]> testRecords, List<String> testMetadata, List<String> expectedCategories) {
        dictionarySnapshot = new StandardDictionarySnapshotProvider().get();
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(dictionarySnapshot);

        Analyzer<Result> analyzer = Analyzers.with(semanticAnalyzer);

        analyzer.init();
        if (testMetadata != null) {
            semanticAnalyzer.setMetadata(Metadata.HEADER_NAME, testMetadata);
        }

        for (String[] record : testRecords) {
            analyzer.analyze(record);
        }
        analyzer.end();

        List<Result> results = analyzer.getResult();
        for (int i = 0; i < expectedCategories.size(); i++) {
            Result result = results.get(i);

            if (result.exist(SemanticType.class)) {
                final SemanticType semanticType = result.get(SemanticType.class);
                final String suggestedCategory = semanticType.getSuggestedCategory();
                assertEquals("Unexpected Category.", expectedCategories.get(i), suggestedCategory);
            }
        }
    }

}
