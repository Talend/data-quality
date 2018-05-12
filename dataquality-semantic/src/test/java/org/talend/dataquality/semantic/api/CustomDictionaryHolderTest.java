package org.talend.dataquality.semantic.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.talend.dataquality.semantic.TestUtils.mockWithTenant;
import static org.talend.dataquality.semantic.api.CategoryRegistryManager.DICTIONARY_SUBFOLDER_NAME;
import static org.talend.dataquality.semantic.api.CategoryRegistryManager.METADATA_SUBFOLDER_NAME;
import static org.talend.dataquality.semantic.api.CategoryRegistryManager.PRODUCTION_FOLDER_NAME;
import static org.talend.dataquality.semantic.api.CategoryRegistryManager.REGEX_CATEGORIZER_FILE_NAME;
import static org.talend.dataquality.semantic.api.CategoryRegistryManager.REGEX_SUBFOLDER_NAME;
import static org.talend.dataquality.semantic.api.CategoryRegistryManager.REPUBLISH_FOLDER_NAME;
import static org.talend.dataquality.semantic.api.CustomDictionaryHolder.TALEND;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.internal.CustomDocumentIndexAccess;
import org.talend.dataquality.semantic.api.internal.CustomMetadataIndexAccess;
import org.talend.dataquality.semantic.api.internal.CustomRegexClassifierAccess;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.filter.impl.CharSequenceFilter;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;
import org.talend.dataquality.semantic.model.DQFilter;
import org.talend.dataquality.semantic.model.DQRegEx;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CustomDictionaryHolder.class, CategoryRegistryManager.class })
public class CustomDictionaryHolderTest extends CategoryRegistryManagerAbstract {

    private CustomDictionaryHolder holder;

    private CustomMetadataIndexAccess customMetadataIndexAccess;

    private CustomRegexClassifierAccess customRegexClassifierAccess;

    private CustomDocumentIndexAccess customDataDictIndexAccess;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initializeCDH(this.getClass().getSimpleName() + "_" + testName.getMethodName());
    }

    private void initializeCDH(String tenantID) {
        mockWithTenant(tenantID);
        CategoryRegistryManager.setUsingLocalCategoryRegistry(true);
        holder = new CustomDictionaryHolder(tenantID);
    }

    @Test
    public void createRegexCategory() {
        holder.createCategory(createDQRegexCategory("1"));
        Set<ISubCategory> filteredSet = holder.getRegexClassifier().getClassifiers().stream()
                .filter(classifier -> classifier.getName().equals("RegExCategoryName")).collect(Collectors.toSet());
        assertEquals(1, filteredSet.size());
    }

    @Test
    public void republishRegex() throws Exception {
        initRepublishMocks();
        holder.republishCategories(Arrays.asList(createDQRegexCategory("newCat")));
        verify(customMetadataIndexAccess, times(0)).insertOrUpdateCategory(any(DQCategory.class));
        verify(customMetadataIndexAccess, times(1)).createCategory(any(DQCategory.class));
        verify(customRegexClassifierAccess, times(1)).insertOrUpdateRegex(any(ISubCategory.class));
        verify(customMetadataIndexAccess, times(1)).commitChanges();
    }

    @Test
    public void republishCompound() throws Exception {
        initRepublishMocks();
        holder.republishCategories(Arrays.asList(createCompoundCategory("1", false)));
        verify(customMetadataIndexAccess, times(0)).insertOrUpdateCategory(any(DQCategory.class));
        verify(customMetadataIndexAccess, times(1)).createCategory(any(DQCategory.class));
        verify(customRegexClassifierAccess, times(0)).insertOrUpdateRegex(any(ISubCategory.class));
        verify(customMetadataIndexAccess, times(1)).commitChanges();
    }

    @Test
    public void republishExistingCompound() throws Exception {
        initRepublishMocks();
        DQCategory category = createCompoundCategory("compoundCategory", true);
        holder.republishCategories(Arrays.asList(category));
        verify(customMetadataIndexAccess, times(1)).insertOrUpdateCategory(any(DQCategory.class));
        verify(customMetadataIndexAccess, times(0)).createCategory(any(DQCategory.class));
        verify(customRegexClassifierAccess, times(0)).insertOrUpdateRegex(any(ISubCategory.class));
        verify(customMetadataIndexAccess, times(1)).commitChanges();
        Assert.assertTrue(category.getModified());
    }

    @Test
    public void republishExistingUnmodifiedCompound() throws Exception {
        initRepublishMocks();
        DQCategory category = createCompoundCategory("compoundCategory", false);
        holder.republishCategories(Arrays.asList(category));
        verify(customMetadataIndexAccess, times(1)).insertOrUpdateCategory(any(DQCategory.class));
        verify(customMetadataIndexAccess, times(0)).createCategory(any(DQCategory.class));
        verify(customRegexClassifierAccess, times(0)).insertOrUpdateRegex(any(ISubCategory.class));
        verify(customMetadataIndexAccess, times(1)).commitChanges();
        Assert.assertTrue(category.getModified()); // Not a Talend category, so must have modified to true
    }

    @Test
    public void republishTalendDict() throws Exception {
        initRepublishMocks();
        DQCategory category = createTalendDictCategory("dictCategory");
        holder.republishCategories(Arrays.asList(category));
        verify(customMetadataIndexAccess, times(1)).insertOrUpdateCategory(any(DQCategory.class));
        verify(customMetadataIndexAccess, times(0)).createCategory(any(DQCategory.class));
        verify(customRegexClassifierAccess, times(0)).insertOrUpdateRegex(any(ISubCategory.class));
        verify(customMetadataIndexAccess, times(1)).commitChanges();
        Assert.assertTrue(!category.getModified()); // Talend category, so must have modified to false
    }

    @Test
    public void republishDataDictDocuments() throws Exception {
        initRepublishMocks();
        DQDocument document = createDQDocument("dictCategory");
        holder.republishDataDictDocuments(Arrays.asList(document));
        verify(customDataDictIndexAccess, times(1)).createDocument(any(List.class));
        Assert.assertTrue(
                new File("target/test_crm/CustomDictionaryHolderTest_republishDataDictDocuments/republish/dictionary").exists());
    }

    @Test
    public void publishDirectory() throws Exception {
        initRepublishMocks();

        String path = CategoryRegistryManager.getLocalRegistryPath() + File.separator + holder.getTenantID() + File.separator;
        new File(path).delete();
        String stagingPath = path + REPUBLISH_FOLDER_NAME;
        String prodPath = path + PRODUCTION_FOLDER_NAME;
        String backupPath = prodPath + ".old";

        String metadataStagingPath = stagingPath + File.separator + METADATA_SUBFOLDER_NAME;
        String dicoStagingPath = stagingPath + File.separator + DICTIONARY_SUBFOLDER_NAME;
        String regexStagingPath = stagingPath + File.separator + REGEX_SUBFOLDER_NAME + File.separator
                + REGEX_CATEGORIZER_FILE_NAME;

        createRepos(stagingPath, prodPath, metadataStagingPath, dicoStagingPath, regexStagingPath);

        holder.publishDirectory();

        verify(customMetadataIndexAccess, times(1)).copyStagingContent(anyString());
        verify(customDataDictIndexAccess, times(1)).copyStagingContent(anyString());
        verify(customRegexClassifierAccess, times(1)).copyStagingContent(anyString());

        Assert.assertTrue(!new File(stagingPath).exists());
        Assert.assertTrue(new File(prodPath).exists());
        Assert.assertTrue(!new File(backupPath).exists());
    }

    @Test
    public void publishDirectoryDoNothing() throws Exception {
        initRepublishMocks();

        String path = CategoryRegistryManager.getLocalRegistryPath() + File.separator + holder.getTenantID() + File.separator;
        new File(path).delete();
        String stagingPath = path + REPUBLISH_FOLDER_NAME;
        String prodPath = path + PRODUCTION_FOLDER_NAME;
        String backupPath = prodPath + ".old";

        String metadataStagingPath = stagingPath + File.separator + METADATA_SUBFOLDER_NAME;
        String dicoStagingPath = stagingPath + File.separator + DICTIONARY_SUBFOLDER_NAME;
        String regexStagingPath = stagingPath + File.separator + REGEX_SUBFOLDER_NAME + File.separator
                + REGEX_CATEGORIZER_FILE_NAME;

        createRepos(stagingPath, prodPath, backupPath, metadataStagingPath, dicoStagingPath, regexStagingPath);

        holder.publishDirectory();

        verify(customMetadataIndexAccess, times(0)).copyStagingContent(anyString());
        verify(customDataDictIndexAccess, times(0)).copyStagingContent(anyString());
        verify(customRegexClassifierAccess, times(0)).copyStagingContent(anyString());

        Assert.assertTrue(new File(stagingPath).exists());
        Assert.assertTrue(new File(prodPath).exists());
        Assert.assertTrue(new File(backupPath).exists());
    }

    @Test
    public void publishDirectoryWithOnlyStaging() throws Exception {
        initRepublishMocks();

        String path = CategoryRegistryManager.getLocalRegistryPath() + File.separator + holder.getTenantID() + File.separator;
        new File(path).delete();
        String stagingPath = path + REPUBLISH_FOLDER_NAME;
        String prodPath = path + PRODUCTION_FOLDER_NAME;
        String backupPath = prodPath + ".old";

        String metadataStagingPath = stagingPath + File.separator + METADATA_SUBFOLDER_NAME;
        String dicoStagingPath = stagingPath + File.separator + DICTIONARY_SUBFOLDER_NAME;
        String regexStagingPath = stagingPath + File.separator + REGEX_SUBFOLDER_NAME + File.separator
                + REGEX_CATEGORIZER_FILE_NAME;

        createRepos(stagingPath, metadataStagingPath, dicoStagingPath, regexStagingPath);

        holder.publishDirectory();

        verify(customMetadataIndexAccess, times(0)).copyStagingContent(anyString());
        verify(customDataDictIndexAccess, times(0)).copyStagingContent(anyString());
        verify(customRegexClassifierAccess, times(0)).copyStagingContent(anyString());

        Assert.assertTrue(!new File(stagingPath).exists());
        Assert.assertTrue(new File(prodPath).exists());
        Assert.assertTrue(!new File(backupPath).exists());
    }

    @Test
    public void publishDirectoryWithoutStaging() throws Exception {
        initRepublishMocks();

        String path = CategoryRegistryManager.getLocalRegistryPath() + File.separator + holder.getTenantID() + File.separator;
        new File(path).delete();
        String stagingPath = path + REPUBLISH_FOLDER_NAME;
        String prodPath = path + PRODUCTION_FOLDER_NAME;
        String backupPath = prodPath + ".old";

        createRepos(prodPath);

        holder.publishDirectory();

        Assert.assertTrue(!new File(stagingPath).exists());
        Assert.assertTrue(new File(prodPath).exists());
        Assert.assertTrue(!new File(backupPath).exists());
        verify(customMetadataIndexAccess, times(0)).copyStagingContent(anyString());
        verify(customDataDictIndexAccess, times(0)).copyStagingContent(anyString());
        verify(customRegexClassifierAccess, times(0)).copyStagingContent(anyString());
    }

    @Test
    public void publishDirectoryFailure() throws Exception {
        initRepublishMocksForCrash();

        String path = CategoryRegistryManager.getLocalRegistryPath() + File.separator + holder.getTenantID() + File.separator;
        new File(path).delete();
        String stagingPath = path + REPUBLISH_FOLDER_NAME;
        String prodPath = path + PRODUCTION_FOLDER_NAME;
        String backupPath = prodPath + ".old";

        String metadataStagingPath = stagingPath + File.separator + METADATA_SUBFOLDER_NAME;
        String dicoStagingPath = stagingPath + File.separator + DICTIONARY_SUBFOLDER_NAME;
        String regexStagingPath = stagingPath + File.separator + REGEX_SUBFOLDER_NAME + File.separator
                + REGEX_CATEGORIZER_FILE_NAME;

        createRepos(stagingPath, prodPath, metadataStagingPath, dicoStagingPath, regexStagingPath);

        holder.publishDirectory();

        Assert.assertTrue(!new File(stagingPath).exists());
        Assert.assertTrue(new File(prodPath).exists());
        Assert.assertTrue(!new File(backupPath).exists());
        verify(customMetadataIndexAccess, times(1)).copyStagingContent(anyString());
        verify(customDataDictIndexAccess, times(0)).copyStagingContent(anyString());
        verify(customRegexClassifierAccess, times(0)).copyStagingContent(anyString());
    }

    @Test
    public void closeRepublishDictionaryAccess() throws Exception {
        initRepublishMocks();
        holder.republishCategories(Arrays.asList(createTalendDictCategory("CAT_ID")));
        holder.republishCategories(Arrays.asList(createDQRegexCategory("CAT_ID")));
        holder.republishDataDictDocuments(Arrays.asList(createDQDocument("CAT_ID")));
        holder.closeRepublishDictionaryAccess();

        verify(customMetadataIndexAccess, times(1)).close();
        verify(customDataDictIndexAccess, times(1)).close();
    }

    private void createRepos(String... paths) {
        for (String path : paths) {
            new File(path).mkdirs();
        }
    }

    private void initRepublishMocks() throws Exception {
        PowerMockito.mockStatic(CategoryRegistryManager.class);
        CategoryRegistryManager crm = mock(CategoryRegistryManager.class);
        when(CategoryRegistryManager.getInstance()).thenReturn(crm);
        when(CategoryRegistryManager.getLocalRegistryPath()).thenReturn("target/test_crm");
        when(crm.getSharedCategoryMetadata()).thenReturn(mockMap());

        customMetadataIndexAccess = PowerMockito.mock(CustomMetadataIndexAccess.class);
        customRegexClassifierAccess = PowerMockito.mock(CustomRegexClassifierAccess.class);
        customDataDictIndexAccess = PowerMockito.mock(CustomDocumentIndexAccess.class);

        PowerMockito.whenNew(CustomMetadataIndexAccess.class).withArguments(any(FSDirectory.class))
                .thenReturn(customMetadataIndexAccess);
        PowerMockito.whenNew(CustomRegexClassifierAccess.class).withArguments(anyString())
                .thenReturn(customRegexClassifierAccess);
        PowerMockito.whenNew(CustomDocumentIndexAccess.class).withArguments(any(Directory.class))
                .thenReturn(customDataDictIndexAccess);

        doNothing().when(customMetadataIndexAccess).insertOrUpdateCategory(any(DQCategory.class));
        doNothing().when(customMetadataIndexAccess).createCategory(any(DQCategory.class));
        doNothing().when(customRegexClassifierAccess).insertOrUpdateRegex(any(ISubCategory.class));
        doNothing().when(customMetadataIndexAccess).commitChanges();
        doNothing().when(customMetadataIndexAccess).copyStagingContent(anyString());
        doNothing().when(customDataDictIndexAccess).copyStagingContent(anyString());
    }

    private void initRepublishMocksForCrash() throws Exception {
        PowerMockito.mockStatic(CategoryRegistryManager.class);
        CategoryRegistryManager crm = mock(CategoryRegistryManager.class);
        when(CategoryRegistryManager.getInstance()).thenReturn(crm);
        when(CategoryRegistryManager.getLocalRegistryPath()).thenReturn("target/test_crm");
        when(crm.getSharedCategoryMetadata()).thenReturn(mockMap());

        customMetadataIndexAccess = PowerMockito.mock(CustomMetadataIndexAccess.class);
        customRegexClassifierAccess = PowerMockito.mock(CustomRegexClassifierAccess.class);
        customDataDictIndexAccess = PowerMockito.mock(CustomDocumentIndexAccess.class);

        PowerMockito.whenNew(CustomMetadataIndexAccess.class).withArguments(any(FSDirectory.class))
                .thenReturn(customMetadataIndexAccess);
        PowerMockito.whenNew(CustomRegexClassifierAccess.class).withArguments(anyString())
                .thenReturn(customRegexClassifierAccess);
        PowerMockito.whenNew(CustomDocumentIndexAccess.class).withArguments(any(Directory.class))
                .thenReturn(customDataDictIndexAccess);

        doNothing().when(customMetadataIndexAccess).insertOrUpdateCategory(any(DQCategory.class));
        doNothing().when(customMetadataIndexAccess).createCategory(any(DQCategory.class));
        doNothing().when(customRegexClassifierAccess).insertOrUpdateRegex(any(ISubCategory.class));
        doNothing().when(customMetadataIndexAccess).commitChanges();
        doThrow(IOException.class).when(customMetadataIndexAccess).copyStagingContent(anyString());
        doNothing().when(customDataDictIndexAccess).copyStagingContent(anyString());
    }

    private Map<String, DQCategory> mockMap() {
        Map<String, DQCategory> map = new HashMap<>();
        map.put("dictCategory", createTalendDictCategory("dictCategory"));
        map.put("regexCategory", createDQRegexCategory("regexCategory"));
        map.put("compoundCategory", createCompoundCategory("compoundCategory", false));
        return map;
    }

    private DQCategory createTalendDictCategory(String categoryId) {
        DQCategory category = new DQCategory(categoryId);
        category.setLabel("dictCategoryLabel");
        category.setName("dictCategoryName");
        category.setType(CategoryType.DICT);
        category.setCompleteness(false);
        category.setModified(false);
        category.setLastModifier(TALEND);
        return category;
    }

    private DQCategory createCompoundCategory(String categoryId, boolean isModified) {
        DQCategory category = new DQCategory(categoryId);
        category.setLabel("compoundCategoryLabel");
        category.setName("compoundCategoryName");
        category.setType(CategoryType.COMPOUND);
        category.setCompleteness(true);
        category.setLastModifier(holder.getTenantID());
        category.setModified(isModified);
        DQCategory child1 = new DQCategory("child1");
        DQCategory child2 = new DQCategory("child2");
        List<DQCategory> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        category.setChildren(children);
        return category;
    }

    private DQCategory createDQRegexCategory(String categoryId) {
        DQCategory category = new DQCategory(categoryId);
        category.setLabel("RegExCategoryLabel");
        category.setName("RegExCategoryName");
        category.setType(CategoryType.REGEX);
        category.setCompleteness(true);

        DQRegEx regEx = new DQRegEx();

        DQFilter filter = new DQFilter();
        filter.setFilterParam("filterParam");
        filter.setFilterType(CharSequenceFilter.CharSequenceFilterType.MUST_CONTAIN.toString());
        regEx.setFilter(filter);

        category.setRegEx(regEx);

        return category;
    }

    private DQDocument createDQDocument(String categoryId) {
        DQDocument document = new DQDocument();
        document.setCategory(createTalendDictCategory(categoryId));
        document.setId("ID");
        document.setValues(Collections.singleton("value1"));

        return document;
    }
}
