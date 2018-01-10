package org.talend.dataquality.semantic.api;

import static org.junit.Assert.assertEquals;
import static org.talend.dataquality.semantic.TestUtils.mockWithTenant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.filter.impl.CharSequenceFilter;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;
import org.talend.dataquality.semantic.model.DQFilter;
import org.talend.dataquality.semantic.model.DQRegEx;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TenancyContextHolder.class })
public class CustomDictionaryHolderTest extends CategoryRegistryManagerAbstract {

    private CustomDictionaryHolder holder;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        initializeCDH(this.getClass().getSimpleName() + "_" + testName.getMethodName());
    }

    private void initializeCDH(String tenantID) {
        mockWithTenant(tenantID);
        CategoryRegistryManager.setUsingLocalCategoryRegistry(true);
        holder = new CustomDictionaryHolder(tenantID);
    }

    @Test
    public void createRegexCategory() {
        holder.createCategory(createDQRegexCategory());
        Set<ISubCategory> filteredSet = holder.getRegexClassifier().getClassifiers().stream()
                .filter(classifier -> classifier.getName().equals("RegExCategoryName")).collect(Collectors.toSet());
        assertEquals(1, filteredSet.size());
    }

    @Test
    public void republishRegex() throws IOException {
        holder.beforeRepublish();
        holder.republishCategory(createDQRegexCategory());
        holder.publishDirectory();
        assertEquals(1, holder.getRegexClassifier().getClassifiers().size());
    }

    @Test
    public void republishCompound() throws IOException {
        int initialSize = holder.getMetadata().size();
        holder.beforeRepublish();
        holder.republishCategory(createCompoundCategory("1", false));
        holder.publishDirectory();
        assertEquals(1, holder.getMetadata().size() - initialSize);
    }

    @Test
    public void republishExistingCompound() throws IOException {
        int initialSize = holder.getMetadata().size();
        holder.beforeRepublish();
        String categoryId = "58f9d2e8b45fc36367e8bc38";
        holder.republishCategory(createCompoundCategory(categoryId, true));
        holder.publishDirectory();
        assertEquals(holder.getMetadata().size(), initialSize);
        assert (holder.getCategoryMetadataById(categoryId).getModified());
    }

    @Test
    public void republishExistingUnmodifiedCompound() throws IOException {
        int initialSize = holder.getMetadata().size();
        holder.beforeRepublish();
        String categoryId = "58f9d2e8b45fc36367e8bc38";
        holder.republishCategory(createCompoundCategory(categoryId, false));
        holder.publishDirectory();
        assertEquals(holder.getMetadata().size(), initialSize);
        assert (!holder.getCategoryMetadataById(categoryId).getModified());
    }

    @Test
    public void republishDict() throws IOException {
        int initialSize = holder.getMetadata().size();
        DQCategory category = createDictCategory("1", true);
        List<DQDocument> documents = createDocuments(category);
        holder.beforeRepublish();
        holder.republishCategory(category);
        holder.republishDataDictDocuments(documents);
        holder.publishDirectory();

        assertEquals(1, holder.getMetadata().size() - initialSize);
        assertEquals(2, holder.getDictionaryCache().listDocuments("dictCategoryName", 0, 10).size());
    }

    @Test
    public void republishExistingDict() throws IOException {
        int initialSize = holder.getMetadata().size();

        DQCategory category = createDictCategory("5836fb7642b02a69874f77e3", true); // Airport code
        List<DQDocument> documents = createDocuments(category);
        holder.beforeRepublish();
        holder.republishCategory(category);
        holder.republishDataDictDocuments(documents);
        holder.publishDirectory();

        assertEquals(0, holder.getMetadata().size() - initialSize);
        assertEquals(2, holder.getDictionaryCache().listDocuments("dictCategoryName", 0, 10).size());
    }

    @Test
    public void republishExistingUnmodifiedDict() throws IOException {
        int initialSize = holder.getMetadata().size();

        DQCategory category = createDictCategory("5836fb7642b02a69874f77e3", false); // Airport code
        holder.beforeRepublish();
        holder.republishCategory(category);
        holder.publishDirectory();

        assertEquals(0, holder.getMetadata().size() - initialSize);
        assertEquals(7830, holder.getDictionaryCache().listDocuments("dictCategoryName", 0, 10000).size());
    }

    private List<DQDocument> createDocuments(DQCategory category) {
        List<DQDocument> documents = new ArrayList<>();
        DQDocument doc1 = new DQDocument();
        doc1.setId("doc1");
        doc1.setCategory(category);
        doc1.setValues(new HashSet<>(Arrays.asList("a", "b")));
        documents.add(doc1);
        DQDocument doc2 = new DQDocument();
        doc2.setId("doc2");
        doc2.setCategory(category);
        doc2.setValues(new HashSet<>(Arrays.asList("c", "d")));
        documents.add(doc2);
        return documents;
    }

    private DQCategory createDictCategory(String categoryId, boolean isModified) {
        DQCategory category = new DQCategory(categoryId);
        category.setLabel("dictCategoryLabel");
        category.setName("dictCategoryName");
        category.setType(CategoryType.DICT);
        category.setCompleteness(false);
        if (isModified) {
            category.setModified(true);
            category.setLastModifier(holder.getTenantID());
        }
        return category;
    }

    private DQCategory createCompoundCategory(String categoryId, boolean isModified) {
        DQCategory category = new DQCategory(categoryId);
        category.setLabel("compoundCategoryLabel");
        category.setName("compoundCategoryName");
        category.setType(CategoryType.COMPOUND);
        category.setCompleteness(true);
        if (isModified) {
            category.setModified(true);
            category.setLastModifier(holder.getTenantID());
        }
        DQCategory child1 = new DQCategory("child1");
        DQCategory child2 = new DQCategory("child2");
        List<DQCategory> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        category.setChildren(children);
        return category;
    }

    private DQCategory createDQRegexCategory() {
        DQCategory category = new DQCategory("1");
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
