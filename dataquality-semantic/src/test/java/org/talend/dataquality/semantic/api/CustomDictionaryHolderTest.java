package org.talend.dataquality.semantic.api;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.filter.impl.CharSequenceFilter;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQFilter;
import org.talend.dataquality.semantic.model.DQRegEx;

public class CustomDictionaryHolderTest {

    private CustomDictionaryHolder holder;

    @Before
    public void setUp() {
        holder = new CustomDictionaryHolder("aTenantID");
    }

    @Test
    public void insertOrUpdateRegexCategory() {

        holder.insertOrUpdateRegexCategory(createDQRegexCategory());

        Set<ISubCategory> filteredSet = holder.getRegexClassifier().getClassifiers().stream()
                .filter(classifier -> classifier.getName().equals("RegExCategoryName")).collect(Collectors.toSet());

        assertEquals(1, filteredSet.size());
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
