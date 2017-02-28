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

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.talend.survivorship.model.RuleDefinition;
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
        for (RuleDefinition element : SampleData.RULES) {
            manager.addRuleDefinition(element);
        }
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#initKnowledgeBase()}.
     */
    @Test
    public void testInitKnowledgeBase() {
        manager.initKnowledgeBase();
        KnowledgeBase base = manager.getKnowledgeBase();
        assertNotNull("Model is null", base.getFactType(SampleData.PKG_NAME, "RecordIn")); //$NON-NLS-1$ //$NON-NLS-2$

        assertNotNull(base.getRule(SampleData.PKG_NAME, SampleData.RULES[0].getRuleName()));
        assertNotNull(base.getProcess(SampleData.PKG_NAME + ".SurvivorFlow")); //$NON-NLS-1$

    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     */
    @Test
    public void testRunSession() { // FIXME change this method name which is not informative about the test!
        manager.initKnowledgeBase();
        manager.runSession(SampleData.SAMPLE_INPUT);

        Map<String, Object> survivors = manager.getSurvivorMap();
        for (String col : SampleData.COLUMNS.keySet()) {
            assertEquals(SampleData.EXPECTED_SURVIVOR.get(col), survivors.get(col));
        }
        assertTrue("conflicts are not the same as expected.", //$NON-NLS-1$
                manager.getConflictsOfSurvivor().equals(SampleData.EXPECTED_CONFLICT_OF_SURVIVOR));

        // Run the same test for a second time
        manager.runSession(SampleData.SAMPLE_INPUT);

        Map<String, Object> survivors2 = manager.getSurvivorMap();
        for (String col : SampleData.COLUMNS.keySet()) {
            assertEquals(SampleData.EXPECTED_SURVIVOR.get(col), survivors2.get(col));
        }
        assertTrue("conflicts are not the same as expected.", //$NON-NLS-1$
                manager.getConflictsOfSurvivor().equals(SampleData.EXPECTED_CONFLICT_OF_SURVIVOR));
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case1 most frequent->most recent and with null
     */

    @Test
    public void testRunSessionCase1() { // FIXME change this method name which is not informative about the test!

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME_CONFLICT);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(SampleData.SAMPLE_INPUT_CONFLICT);
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        Assert.assertEquals("The size of conflictsOfSurvivor should be 1", 1, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Assert.assertTrue("The column of conflict should be birthday", conflictsOfSurvivor.contains("birthday")); //$NON-NLS-1$ //$NON-NLS-2$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        Assert.assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$ 
        Object birthdayObj = survivorMap.get("birthday"); //$NON-NLS-1$
        Assert.assertTrue("The birthdayObj should not be null", birthdayObj != null); //$NON-NLS-1$ 
        Date resultDate = (Date) birthdayObj;

        // FIXME why this assertion? When I run it, I get 04-04-2000
        Assert.assertEquals("The resultDate should be 08-08-2000", "08-08-2000", //$NON-NLS-1$ //$NON-NLS-2$
                SampleData.dateToString(resultDate, "dd-MM-yyyy")); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case2 most frequent->longest and with null
     */

    @Test
    public void testRunSessionCase2() { // FIXME change this method name which is not informative about the test!

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME_CONFLICT_FRE_LONG);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT_FRE_LONG) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(SampleData.SAMPLE_INPUT_CONFLICT);
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        Assert.assertEquals("The size of conflictsOfSurvivor should be 1", 1, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Assert.assertTrue("The column of conflict should be birthday", conflictsOfSurvivor.contains("city")); //$NON-NLS-1$ //$NON-NLS-2$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        Assert.assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$ 
        Object cityObj = survivorMap.get("city"); //$NON-NLS-1$
        Assert.assertTrue("The birthdayObj should not be null", cityObj != null); //$NON-NLS-1$ 
        String resultStr = (String) cityObj;
        // FIXME why shangai is expected? I get beijing!
        Assert.assertEquals("The resultStr should be shanghai", "shanghai", //$NON-NLS-1$ //$NON-NLS-2$
                resultStr);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case3 most frequent->longest->recent and with null
     */

    @Test
    public void testRunSessionCase3() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME_CONFLICT_FRE_LONG_RECENT);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT_FRE_LONG_RECENT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(SampleData.SAMPLE_INPUT_CONFLICT);
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        Assert.assertEquals("The size of conflictsOfSurvivor should be 1", 1, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Assert.assertTrue("The column of conflict should be birthday", conflictsOfSurvivor.contains("firstName")); //$NON-NLS-1$ //$NON-NLS-2$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        Assert.assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$ 
        Object firstNameObj = survivorMap.get("firstName"); //$NON-NLS-1$
        Assert.assertTrue("The firstNameObj should not be null", firstNameObj != null); //$NON-NLS-1$ 
        String resultStr = (String) firstNameObj;
        // FIXME why is Tony expected? I get Lili
        Assert.assertEquals("The resultStr should be Tony", "Tony", //$NON-NLS-1$ //$NON-NLS-2$
                resultStr);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case4 most frequent->null->constant
     * 
     * the constant is Green
     * other column is firstName column
     */

    @Test
    public void testRunSessionCase4() { // FIXME change this method name which is not informative about the test!

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME_CONFLICT_FRE_NULL_CONSTANT);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT_FRE_NULL_CONTSTANT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(SampleData.SAMPLE_INPUT_CONFLICT);
        // 5. Retrieve results
        // HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        //        Assert.assertEquals("The size of conflictsOfSurvivor should be 1", 1, conflictsOfSurvivor.size()); //$NON-NLS-1$
        //        Assert.assertTrue("The column of conflict should be lastName", conflictsOfSurvivor.contains("lastName")); //$NON-NLS-1$ //$NON-NLS-2$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        Assert.assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$ 
        Object lastNameObj = survivorMap.get("lastName"); //$NON-NLS-1$
        Assert.assertTrue("The lastNameObj should not be null", lastNameObj != null); //$NON-NLS-1$ 
        String resultStr = (String) lastNameObj;
        // FIXME why is Tony expected? I get Green
        Assert.assertEquals("The resultStr should be Tony", "Tony", //$NON-NLS-1$ //$NON-NLS-2$
                resultStr);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case5 most frequent->null->frequent
     * 
     * other column is lastName column
     */

    @Test
    public void testRunSessionCase5() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME_CONFLICT_TWO_TARGET_ONE_COLUMN);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT_TWO_TARGET_ONE_COLUMN) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(SampleData.SAMPLE_INPUT_CONFLICT);
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        Assert.assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        Assert.assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$ 
        Assert.assertTrue("The size of SurvivorMap should be 1", survivorMap.size() == 1); //$NON-NLS-1$
        Object birthdayObj = survivorMap.get("birthday"); //$NON-NLS-1$
        Assert.assertTrue("The birthdayNameObj should not be null", birthdayObj != null); //$NON-NLS-1$ 
        String resultDate = SampleData.dateToString((Date) birthdayObj, "dd-MM-yyyy"); //$NON-NLS-1$
        Assert.assertEquals("The resultDate should be 08-08-2000", "08-08-2000", //$NON-NLS-1$ //$NON-NLS-2$
                resultDate);
    }

}
