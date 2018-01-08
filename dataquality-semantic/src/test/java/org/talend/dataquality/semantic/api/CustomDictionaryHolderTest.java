package org.talend.dataquality.semantic.api;

import static org.junit.Assert.assertEquals;
import static org.talend.dataquality.semantic.TestUtils.mockWithTenant;

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
import org.talend.dataquality.semantic.model.DQCategory;
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
        initializeCDH(testName.getMethodName());
    }

    @Test
    public void insertOrUpdateRegexCategory() {

        holder.insertOrUpdateRegexCategory(createDQRegexCategory());

        Set<ISubCategory> filteredSet = holder.getRegexClassifier().getClassifiers().stream()
                .filter(classifier -> classifier.getName().equals("RegExCategoryName")).collect(Collectors.toSet());

        assertEquals(1, filteredSet.size());
    }

    private void initializeCDH(String tenantID) {
        mockWithTenant(tenantID);
        holder = new CustomDictionaryHolder(tenantID);
    }

    private DQCategory createDQRegexCategory() {
        DQCategory category = new DQCategory();
        category.setId("1");
        category.setLabel("RegExCategoryLabel");
        category.setName("RegExCategoryName");

        DQRegEx regEx = new DQRegEx();

        DQFilter filter = new DQFilter();
        filter.setFilterParam("filterParam");
        filter.setFilterType(CharSequenceFilter.CharSequenceFilterType.MUST_CONTAIN.toString());
        regEx.setFilter(filter);

        category.setRegEx(regEx);

        return category;
    }
}
