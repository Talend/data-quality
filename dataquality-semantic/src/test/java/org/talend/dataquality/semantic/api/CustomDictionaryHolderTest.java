package org.talend.dataquality.semantic.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.talend.dataquality.semantic.TestUtils.mockWithTenant;
import static org.talend.dataquality.semantic.api.CustomDictionaryHolder.TALEND;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.internal.CustomMetadataIndexAccess;
import org.talend.dataquality.semantic.api.internal.CustomRegexClassifierAccess;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.filter.impl.CharSequenceFilter;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQFilter;
import org.talend.dataquality.semantic.model.DQRegEx;

@PrepareForTest({ CustomDictionaryHolder.class, CategoryRegistryManager.class })
public class CustomDictionaryHolderTest extends CategoryRegistryManagerAbstract {

    @InjectMocks
    private CustomDictionaryHolder holder;

    private CustomMetadataIndexAccess customRepublishMetadataIndexAccess;

    private CustomRegexClassifierAccess customRepublishRegexClassifierAccess;

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
    public void createRegexCategory() throws IOException {
        holder.createCategory(createDQRegexCategory("1"));
        Set<ISubCategory> filteredSet = holder.getRegexClassifier().getClassifiers().stream()
                .filter(classifier -> classifier.getName().equals("RegExCategoryName")).collect(Collectors.toSet());
        assertEquals(1, filteredSet.size());
    }

    @Test
    public void republishRegex() {
        initRepublishMocks();
        holder.republishCategory(createDQRegexCategory("newCat"));
        verify(customRepublishMetadataIndexAccess, times(0)).insertOrUpdateCategory(any(DQCategory.class));
        verify(customRepublishMetadataIndexAccess, times(1)).createCategory(any(DQCategory.class));
        verify(customRepublishRegexClassifierAccess, times(1)).insertOrUpdateRegex(any(ISubCategory.class));
        verify(customRepublishMetadataIndexAccess, times(1)).commitChanges();
    }

    @Test
    public void republishCompound() {
        initRepublishMocks();
        holder.republishCategory(createCompoundCategory("1", false));
        verify(customRepublishMetadataIndexAccess, times(0)).insertOrUpdateCategory(any(DQCategory.class));
        verify(customRepublishMetadataIndexAccess, times(1)).createCategory(any(DQCategory.class));
        verify(customRepublishRegexClassifierAccess, times(0)).insertOrUpdateRegex(any(ISubCategory.class));
        verify(customRepublishMetadataIndexAccess, times(1)).commitChanges();
    }

    @Test
    public void republishExistingCompound() {
        initRepublishMocks();
        DQCategory category = createCompoundCategory("compoundCategory", true);
        holder.republishCategory(category);
        verify(customRepublishMetadataIndexAccess, times(1)).insertOrUpdateCategory(any(DQCategory.class));
        verify(customRepublishMetadataIndexAccess, times(0)).createCategory(any(DQCategory.class));
        verify(customRepublishRegexClassifierAccess, times(0)).insertOrUpdateRegex(any(ISubCategory.class));
        verify(customRepublishMetadataIndexAccess, times(1)).commitChanges();
        assert (category.getModified());
    }

    @Test
    public void republishExistingUnmodifiedCompound() {
        initRepublishMocks();
        DQCategory category = createCompoundCategory("compoundCategory", false);
        holder.republishCategory(category);
        verify(customRepublishMetadataIndexAccess, times(1)).insertOrUpdateCategory(any(DQCategory.class));
        verify(customRepublishMetadataIndexAccess, times(0)).createCategory(any(DQCategory.class));
        verify(customRepublishRegexClassifierAccess, times(0)).insertOrUpdateRegex(any(ISubCategory.class));
        verify(customRepublishMetadataIndexAccess, times(1)).commitChanges();
        assert (category.getModified()); // Not a Talend category, so must have modified to true
    }

    @Test
    public void republishTalendDict() {
        initRepublishMocks();
        DQCategory category = createTalendDictCategory("dictCategory");
        holder.republishCategory(category);
        verify(customRepublishMetadataIndexAccess, times(1)).insertOrUpdateCategory(any(DQCategory.class));
        verify(customRepublishMetadataIndexAccess, times(0)).createCategory(any(DQCategory.class));
        verify(customRepublishRegexClassifierAccess, times(0)).insertOrUpdateRegex(any(ISubCategory.class));
        verify(customRepublishMetadataIndexAccess, times(1)).commitChanges();
        assert (!category.getModified()); // Talend category, so must have modified to false
    }

    private void initRepublishMocks() {
        PowerMockito.mockStatic(CategoryRegistryManager.class);
        CategoryRegistryManager crm = mock(CategoryRegistryManager.class);
        when(CategoryRegistryManager.getInstance()).thenReturn(crm);
        when(crm.getSharedCategoryMetadata()).thenReturn(mockMap());

        customRepublishMetadataIndexAccess = PowerMockito.mock(CustomMetadataIndexAccess.class);
        customRepublishRegexClassifierAccess = PowerMockito.mock(CustomRegexClassifierAccess.class);
        try {
            PowerMockito.whenNew(CustomMetadataIndexAccess.class).withArguments(any(FSDirectory.class))
                    .thenReturn(customRepublishMetadataIndexAccess);
            PowerMockito.whenNew(CustomRegexClassifierAccess.class).withArguments(anyString())
                    .thenReturn(customRepublishRegexClassifierAccess);
        } catch (Exception e) {
            e.printStackTrace();
        }
        doNothing().when(customRepublishMetadataIndexAccess).insertOrUpdateCategory(any(DQCategory.class));
        doNothing().when(customRepublishMetadataIndexAccess).createCategory(any(DQCategory.class));
        doNothing().when(customRepublishRegexClassifierAccess).insertOrUpdateRegex(any(ISubCategory.class));
        doNothing().when(customRepublishMetadataIndexAccess).commitChanges();
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
}
