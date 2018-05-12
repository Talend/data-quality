package org.talend.dataquality.semantic.sampling;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CategoryInferenceManagerTest {

    private CategoryInferenceManager manager;

    @Before
    public void setUp() {
        manager = new CategoryInferenceManager();
    }

    @Test
    public void inferCategory() {
        Object[] records = { "record1", "record2" };
        manager.inferCategory(records);

        Assert.assertEquals(2, manager.getSemanticCategory().size());
    }
}