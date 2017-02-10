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
package org.talend.survivorship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.talend.survivorship.sample.SampleData;

/**
 * DOC sizhaoliu class global comment. Detailled comment
 */
public class SurvivorshipManagerTest {

    private SurvivorshipManager manager;

    private String ruleRelativePath = "src/test/resources/" + SampleData.RULE_PATH; //$NON-NLS-1$

    /**
     * Setup SurvivorshipManager.
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME);

        for (String str : SampleData.COLUMNS.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS.get(str));
        }
        for (int i = 0; i < SampleData.RULES.length; i++) {
            manager.addRuleDefinition(SampleData.RULES[i]);
        }
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#initKnowledgeBase()}.
     */
    @Test
    public void testInitKnowledgeBase() {
        manager.initKnowledgeBase();
        KnowledgeBase base = manager.getKnowledgeBase();
        assertNotNull("Model is null", base.getFactType(SampleData.PKG_NAME, "RecordIn"));

        assertNotNull(base.getRule(SampleData.PKG_NAME, SampleData.RULES[0].getRuleName()));
        assertNotNull(base.getProcess(SampleData.PKG_NAME + ".SurvivorFlow"));

    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     */
    @Test
    public void testRunSession() {
        manager.initKnowledgeBase();
        manager.runSession(SampleData.SAMPLE_INPUT);

        Map<String, Object> survivors = manager.getSurvivorMap();
        for (String col : SampleData.COLUMNS.keySet()) {
            assertEquals(SampleData.EXPECTED_SURVIVOR.get(col), survivors.get(col));
        }
        assertTrue("conflicts are not the same as expected.",
                manager.getConflictsOfSurvivor().equals(SampleData.EXPECTED_CONFLICT_OF_SURVIVOR));

        // Run the same test for a second time
        manager.runSession(SampleData.SAMPLE_INPUT);

        Map<String, Object> survivors2 = manager.getSurvivorMap();
        for (String col : SampleData.COLUMNS.keySet()) {
            assertEquals(SampleData.EXPECTED_SURVIVOR.get(col), survivors2.get(col));
        }
        assertTrue("conflicts are not the same as expected.",
                manager.getConflictsOfSurvivor().equals(SampleData.EXPECTED_CONFLICT_OF_SURVIVOR));
    }
}
