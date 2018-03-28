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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.talend.survivorship.model.Attribute;
import org.talend.survivorship.model.Column;
import org.talend.survivorship.model.ConflictRuleDefinition;
import org.talend.survivorship.model.DataSet;
import org.talend.survivorship.model.Record;
import org.talend.survivorship.model.RuleDefinition;
import org.talend.survivorship.sample.SampleData;
import org.talend.survivorship.sample.SampleDataCheckOutputConflictColumn;
import org.talend.survivorship.sample.SampleDataConflict;
import org.talend.survivorship.sample.SampleDataConflictCheckRule;
import org.talend.survivorship.sample.SampleDataConflictExecuteRulesWithUIOrder;
import org.talend.survivorship.sample.SampleDataConflictFillEmpty;
import org.talend.survivorship.sample.SampleDataConflictMostCommon2Longest;
import org.talend.survivorship.sample.SampleDataConflictMostCommon2Longest2MostRecent;
import org.talend.survivorship.sample.SampleDataConflictMostCommon2Longest2keepOneOfDuplicte;
import org.talend.survivorship.sample.SampleDataConflictMostCommon2MostRecent;
import org.talend.survivorship.sample.SampleDataConflictMostCommon2OtherSurvivedValue;
import org.talend.survivorship.sample.SampleDataConflictMostCommonAndNoIgnoreBlank;
import org.talend.survivorship.sample.SampleDataConflictMutilGroupConflictDisError;
import org.talend.survivorship.sample.SampleDataConflictMutilGroupFillEmptyBy;
import org.talend.survivorship.sample.SampleDataConflictOtherColumn2MostCommon2Constant;
import org.talend.survivorship.sample.SampleDataConflictOtherColumn2MostCommon2ConstantEmptyDuplicate;
import org.talend.survivorship.sample.SampleDataConflictShortest2OtherColumnDuplicateSurvivedValue;
import org.talend.survivorship.sample.SampleDataConflictTwoNoConflictColumnGetOneSameSurvivedValue;
import org.talend.survivorship.sample.SampleDataRegexFunction;

/**
 * Create by sizhaoliu test for SurvivorshipManager
 */
public class SurvivorshipManagerTest {

    private SurvivorshipManager manager;

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
     * no conflict case
     */
    @Test
    public void testRunSessionNoConflictCase() {
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(SampleData.SAMPLE_INPUT);

        Map<String, Object> survivors = manager.getSurvivorMap();
        for (String col : SampleData.COLUMNS.keySet()) {
            assertEquals(SampleData.EXPECTED_SURVIVOR.get(col), survivors.get(col));
        }
        assertTrue("conflicts are not the same as expected.", //$NON-NLS-1$
                manager.getConflictsOfSurvivor().equals(SampleData.EXPECTED_CONFLICT_OF_SURVIVOR));
        manager.checkConflictRuleValid();
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
     * no conflict case
     */
    @Test
    public void testRunSessionRegex() {
        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleDataRegexFunction.PKG_NAME);

        for (String str : SampleDataRegexFunction.COLUMNS.keySet()) {
            manager.addColumn(str, SampleDataRegexFunction.COLUMNS.get(str));
        }
        for (RuleDefinition element : SampleDataRegexFunction.RULES) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(SampleDataRegexFunction.SAMPLE_INPUT);

        Map<String, Object> survivors = manager.getSurvivorMap();
        for (String col : SampleDataRegexFunction.COLUMNS.keySet()) {
            assertEquals(SampleDataRegexFunction.EXPECTED_SURVIVOR.get(col), survivors.get(col));
        }
        assertTrue("conflicts are not the same as expected.", //$NON-NLS-1$
                manager.getConflictsOfSurvivor().equals(SampleDataRegexFunction.EXPECTED_CONFLICT_OF_SURVIVOR));

        // Run the same test for a second time
        manager.runSession(SampleDataRegexFunction.SAMPLE_INPUT);

        Map<String, Object> survivors2 = manager.getSurvivorMap();
        for (String col : SampleDataRegexFunction.COLUMNS.keySet()) {
            assertEquals(SampleDataRegexFunction.EXPECTED_SURVIVOR.get(col), survivors2.get(col));
        }
        assertTrue("conflicts are not the same as expected.", //$NON-NLS-1$
                manager.getConflictsOfSurvivor().equals(SampleDataRegexFunction.EXPECTED_CONFLICT_OF_SURVIVOR));
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case1 most frequent->most recent and with null
     * 
     * generate conflict by most common rule and resolve conflict by most recent rule
     * recent date should be 08-08-2000 rather than 04-04-2000
     */

    @Test
    public void testRunSessionMostCommon2MostRecent() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleDataConflictMostCommon2MostRecent.PKG_NAME_CONFLICT);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("birthday")) { //$NON-NLS-1$
                for (ConflictRuleDefinition element : SampleDataConflictMostCommon2MostRecent.RULES_CONFLICT_RESOLVE) {
                    column.getConflictResolveList().add(element);
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictMostCommon2MostRecent.RULES_CONFLICT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object birthdayObj = survivorMap.get("birthday"); //$NON-NLS-1$
        assertTrue("The birthdayObj should not be null", birthdayObj != null); //$NON-NLS-1$
        Date resultDate = (Date) birthdayObj;

        // 08-08-2000 is we expect after implement code because we use most recent to resolve conflict
        assertEquals("The resultDate should be 08-08-2000", "08-08-2000", //$NON-NLS-1$ //$NON-NLS-2$
                SampleData.dateToString(resultDate, "dd-MM-yyyy")); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case most frequent->most recent(disable) and with null
     * 
     * generate conflict by most common rule and resolve conflict by most recent rule
     * recent date should be 08-08-2000 rather than 04-04-2000
     */

    @Test
    public void testRunSessionMostCommon2MostRecentDisable() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleDataConflictMostCommon2MostRecent.PKG_NAME_CONFLICT);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("birthday")) { //$NON-NLS-1$
                for (ConflictRuleDefinition element : SampleDataConflictMostCommon2MostRecent.RULES_CONFLICT_RESOLVE_DISABLE) {
                    column.getConflictResolveList().add(element);
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictMostCommon2MostRecent.RULES_CONFLICT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 1", 1, conflictsOfSurvivor.size()); //$NON-NLS-1$
        assertTrue("The column of conflict should be birthday", conflictsOfSurvivor.contains("birthday")); //$NON-NLS-1$ //$NON-NLS-2$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object birthdayObj = survivorMap.get("birthday"); //$NON-NLS-1$
        assertTrue("The birthdayObj should not be null", birthdayObj != null); //$NON-NLS-1$
        Date resultDate = (Date) birthdayObj;

        // 08-08-2000 is we expect after implement code because we use most recent to resolve conflict
        assertEquals("The resultDate should be 04-04-2000", "04-04-2000", //$NON-NLS-1$ //$NON-NLS-2$
                SampleData.dateToString(resultDate, "dd-MM-yyyy")); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case1 most common->fill empty->longest
     * 
     * generate conflict by most common rule and resolve conflict by longest rule
     * 
     */

    @Test
    public void testRunSessionMostCommon2LongestFillEmpty() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleDataConflictFillEmpty.PKG_NAME_CONFLICT);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("lastName")) { //$NON-NLS-1$
                for (ConflictRuleDefinition element : SampleDataConflictFillEmpty.RULES_CONFLICT_RESOLVE) {
                    column.getConflictResolveList().add(element);
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictFillEmpty.RULES_CONFLICT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object lastNameObj = survivorMap.get("lastName"); //$NON-NLS-1$
        assertTrue("The birthdayObj should not be null", lastNameObj != null); //$NON-NLS-1$
        String resultLastName = lastNameObj.toString();

        // shenze is we expect after implement code because we use longest to resolve conflict
        assertEquals("The resultLastName should be shenze", "shenze", //$NON-NLS-1$ //$NON-NLS-2$
                resultLastName);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case2 most frequent->longest and with null
     * 
     * For city1 column, after most common rule generate conflict beijing and shanghai then use Longest rule resolve
     * conflict.
     * Rusult is shanghai
     */
    @Test
    public void testRunSessionMostCommon2Longest() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleDataConflictMostCommon2Longest.PKG_NAME_CONFLICT_FRE_LONG);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("city1")) { //$NON-NLS-1$
                for (ConflictRuleDefinition element : SampleDataConflictMostCommon2Longest.RULES_CONFLICT_RESOLVE) {
                    column.getConflictResolveList().add(element);
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictMostCommon2Longest.RULES_CONFLICT_FRE_LONG) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object cityObj = survivorMap.get("city1"); //$NON-NLS-1$
        assertTrue("The birthdayObj should not be null", cityObj != null); //$NON-NLS-1$
        String resultStr = (String) cityObj;
        // Because we used longest rule to resolve conflict the frequency of shanghai is 2 and the frequency of beijing
        // is 2.
        // But length of beijing is 7 the length of shanghai is 8 so that we expect final result is shanghai
        assertEquals("The resultStr should be shanghai", "shanghai", //$NON-NLS-1$ //$NON-NLS-2$
                resultStr);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case3 most frequent->longest->recent and with null
     * firstName column used rule most common then get conflict between Tony and Lili.
     * Then use Longest to resolve conflict but them can't.
     * Then use most recent rule on birthday column to resolve conflict between 04-04-2000 and 06-06-2000
     * we get final result 06-06-2000 and mapping to fistName column the result should be Tony.
     * Because of the birthday of Tony is 06-06-2000.
     * Note that Ignore blank has been check on this case
     */
    @Test
    public void testRunSessionMostCommon2Longest2MostRecent() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH,
                SampleDataConflictMostCommon2Longest2MostRecent.PKG_NAME_CONFLICT_FRE_LONG_RECENT);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("firstName")) { //$NON-NLS-1$
                for (ConflictRuleDefinition element : SampleDataConflictMostCommon2Longest2MostRecent.RULES_CONFLICT_RESOLVE) {
                    column.getConflictResolveList().add(element);
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictMostCommon2Longest2MostRecent.RULES_CONFLICT_FRE_LONG_RECENT) {
            manager.addRuleDefinition(element);
        }

        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object firstNameObj = survivorMap.get("firstName"); //$NON-NLS-1$
        assertTrue("The firstNameObj should not be null", firstNameObj != null); //$NON-NLS-1$
        String resultStr = (String) firstNameObj;
        // There Tony and Lili is conflict.we use most recent on the birthday column so that we choose Tony.
        // Because of Tony birthday is 06-06-2000 but Lili birthday is 04-04-2000.
        // After implement resolve conflict code the final result should be Tony but it is Lili until now.
        // So that the assert will be failed it is noraml
        assertEquals("The resultStr should be Tony", "Tony", //$NON-NLS-1$ //$NON-NLS-2$
                resultStr);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case10 most frequent->other survived value
     *
     */
    @Test
    public void testRunSessionMostCommon2OtherSurvived() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH,
                SampleDataConflictMostCommon2OtherSurvivedValue.PKG_NAME_CONFLICT);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("city1")) { //$NON-NLS-1$
                for (ConflictRuleDefinition element : SampleDataConflictMostCommon2OtherSurvivedValue.RULES_CONFLICT_RESOLVE) {
                    column.getConflictResolveList().add(element);
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictMostCommon2OtherSurvivedValue.RULES_CONFLICT) {
            manager.addRuleDefinition(element);
        }

        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object city1 = survivorMap.get("city1"); //$NON-NLS-1$
        assertTrue("The firstNameObj should not be null", city1 != null); //$NON-NLS-1$
        String resultStr = (String) city1;
        // There Tony and Lili is conflict.we use most recent on the birthday column so that we choose Tony.
        // Because of Tony birthday is 06-06-2000 but Lili birthday is 04-04-2000.
        // After implement resolve conflict code the final result should be Tony but it is Lili until now.
        // So that the assert will be failed it is noraml
        assertEquals("The resultStr should be beijing", "beijing", //$NON-NLS-1$ //$NON-NLS-2$
                resultStr);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case4 most frequent->null->constant
     * 
     * the constant is Green
     * fill column is firstName column
     * Because of there are two empty value so that we get value from firstName column.
     * Then do most common rule we get Green=2 |Tony=2| null is ignore aotomatic.It is conflict.
     * Because value "green" is The constant so that we ignore it.
     * Final we get rusult "Tony"
     */
    @Test
    public void testRunSessionOtherColumn2MostCommon2Constant() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH,
                SampleDataConflictOtherColumn2MostCommon2Constant.PKG_NAME_CONFLICT_FRE_NULL_CONSTANT);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("lastName")) { //$NON-NLS-1$
                for (ConflictRuleDefinition element : SampleDataConflictOtherColumn2MostCommon2Constant.RULES_CONFLICT_RESOLVE) {
                    column.getConflictResolveList().add(element);
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictOtherColumn2MostCommon2Constant.RULES_CONFLICT_FRE_NULL_CONTSTANT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object lastNameObj = survivorMap.get("lastName"); //$NON-NLS-1$
        assertTrue("The lastNameObj should not be null", lastNameObj != null); //$NON-NLS-1$
        String resultStr = (String) lastNameObj;
        // conflicting between "" and Green after most common
        // after fill empty conflicting between "tony" "shenze" and "Green"
        // after exclud because of Green is constant so that we don't choose it.
        // after longest we get unique result shenze
        assertEquals("The resultStr should be shenze", "shenze", //$NON-NLS-1$ //$NON-NLS-2$
                resultStr);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case4 most frequent->null->constant->empty duplicate sur
     * 
     * 1.longest on firstName so that we get confilect Tony and Lili
     * Then find shortest on city2 column and get xian which mapping to firstName column value is Tony
     * Final we get firstName survived value is "Tony"
     * 
     * 2.the constant is Green
     * fill column is firstName column
     * Because of there are two empty value so that we get value from firstName column.
     * Then do most common rule we get Green=2 |Tony=2| null is ignore aotomatic.It is conflict.
     * Because value "green" is The constant so that we ignore it.
     * 
     * lastName survived value is empty
     */
    @Test
    public void testRunSessionOtherColumn2MostCommon2ConstantEmptyDuplicate() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH,
                SampleDataConflictOtherColumn2MostCommon2ConstantEmptyDuplicate.PKG_NAME);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("lastName") || column.getName().equals("firstName")) { //$NON-NLS-1$ //$NON-NLS-2$
                for (ConflictRuleDefinition element : SampleDataConflictOtherColumn2MostCommon2ConstantEmptyDuplicate.RULES_CONFLICT_RESOLVE) {
                    if (column.getName().equals(element.getTargetColumn())) {
                        column.getConflictResolveList().add(element);
                    }
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictOtherColumn2MostCommon2ConstantEmptyDuplicate.RULES_CONFLICT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        // HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        // Assert.assertEquals("The size of conflictsOfSurvivor should be 1", 1, conflictsOfSurvivor.size()); //$NON-NLS-1$
        // Assert.assertTrue("The column of conflict should be lastName", conflictsOfSurvivor.contains("lastName")); //$NON-NLS-1$
        // //$NON-NLS-2$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object lastNameObj = survivorMap.get("lastName"); //$NON-NLS-1$
        assertTrue("The lastNameObj should not be null", lastNameObj != null); //$NON-NLS-1$
        String resultStr2 = (String) lastNameObj;
        Object firstNameObj = survivorMap.get("firstName"); //$NON-NLS-1$
        assertTrue("The firstNameObj should not be null", firstNameObj != null); //$NON-NLS-1$
        String resultStr1 = (String) firstNameObj;
        // Green is our Constant value which will be setting by user after that.
        // In fact, Tony and Green is conflict after most common rule.
        // But Green is constant so that we don't choose it.
        // On my side result is Green too. need now code to implement it
        assertEquals("The resultStr should be Tony", "Tony", //$NON-NLS-1$ //$NON-NLS-2$
                resultStr1);
        assertEquals("The resultStr should be empty", "", //$NON-NLS-1$ //$NON-NLS-2$
                resultStr2);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case5 city1 column use Longest get survived value hebeihebei.
     * birthday column use most Recent get survived value 08-08-2000.
     * Althougth there are two values are 08-08-2000 but they are same so that no generate conflict
     */

    @Test
    public void testRunSessionTwoNoConflictColumnGetOneSameSurvivedValue() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH,
                SampleDataConflictTwoNoConflictColumnGetOneSameSurvivedValue.PKG_NAME_CONFLICT_TWO_TARGET_ONE_COLUMN);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleDataConflictTwoNoConflictColumnGetOneSameSurvivedValue.RULES_CONFLICT_TWO_TARGET_ONE_COLUMN) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        assertTrue("The size of SurvivorMap should be 1", survivorMap.size() == 1); //$NON-NLS-1$
        Object birthdayObj = survivorMap.get("birthday"); //$NON-NLS-1$
        assertTrue("The birthdayNameObj should not be null", birthdayObj != null); //$NON-NLS-1$
        String resultDate = SampleData.dateToString((Date) birthdayObj, "dd-MM-yyyy"); //$NON-NLS-1$
        assertEquals("The resultDate should be 08-08-2000", "08-08-2000", //$NON-NLS-1$ //$NON-NLS-2$
                resultDate);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case6 most frequent->longest->keep one of duplicates only
     * 
     * now both survived values are beijing. After implememt code there should keep one value and it should be shanghai
     * Because we will use most common to generate conflict between beijing=2 and shanghai=2.
     * And use Longest to resolve conflict get final result shanghai.
     * Both city1 and city2 values are "shanghai" it is duplicte .
     * So that we just keep one of them.
     */
    @Test
    public void testRunSessionMostCommon2Longest2keepOneOfDuplicte() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH,
                SampleDataConflictMostCommon2Longest2keepOneOfDuplicte.PKG_NAME_CONFLICT_TWO_TARGET_SAME_VALUE);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("city1") || column.getName().equals("city2")) { //$NON-NLS-1$ //$NON-NLS-2$
                for (ConflictRuleDefinition element : SampleDataConflictMostCommon2Longest2keepOneOfDuplicte.RULES_CONFLICT_RESOLVE) {
                    if (column.getName().equals(element.getTargetColumn())) {
                        column.getConflictResolveList().add(element);
                    }
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictMostCommon2Longest2keepOneOfDuplicte.RULES_CONFLICT_TWO_TARGET_SAME_RESULT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        assertTrue("The size of SurvivorMap should be 2", survivorMap.size() == 2); //$NON-NLS-1$
        Object city1Obj = survivorMap.get("city1"); //$NON-NLS-1$
        assertTrue("The city1Obj should not be null", city1Obj != null); //$NON-NLS-1$
        String resultDate = city1Obj.toString();
        assertEquals("The resultDate should be shanghai", "shanghai", //$NON-NLS-1$ //$NON-NLS-2$
                resultDate);
        Object city2Obj = survivorMap.get("city2"); //$NON-NLS-1$
        assertTrue("The city1Obj should not be null", city2Obj != null); //$NON-NLS-1$
        resultDate = city2Obj.toString();
        assertEquals("The resultDate should be beijing", "beijing", //$NON-NLS-1$ //$NON-NLS-2$
                resultDate);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case6 most frequent->shortest->keep one of duplicates only
     * 
     * now both survived values are beijing. After implememt code there should keep one value and it should be beijing
     * Because we will use most common to generate conflict between beijing=2 and shanghai=2.
     * And use Shortest to resolve conflict get final result beijing.
     * Both city1 and city2 values are "beijing" it is duplicte .
     * So that we just keep one of them and choose longest value in the city2 conflict values
     * city2 is "shanghai"
     */
    @Test
    public void testRunSessionMostCommon2Shortest2keepOneOfDuplicte() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH,
                SampleDataConflictMostCommon2Longest2keepOneOfDuplicte.PKG_NAME_CONFLICT_TWO_TARGET_SAME_VALUE);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("city1") || column.getName().equals("city2")) { //$NON-NLS-1$ //$NON-NLS-2$
                for (ConflictRuleDefinition element : SampleDataConflictMostCommon2Longest2keepOneOfDuplicte.RULES_CONFLICT_RESOLVE2) {
                    if (column.getName().equals(element.getTargetColumn())) {
                        column.getConflictResolveList().add(element);
                    }
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictMostCommon2Longest2keepOneOfDuplicte.RULES_CONFLICT_TWO_TARGET_SAME_RESULT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        assertTrue("The size of SurvivorMap should be 2", survivorMap.size() == 2); //$NON-NLS-1$
        Object city1Obj = survivorMap.get("city1"); //$NON-NLS-1$
        assertTrue("The city1Obj should not be null", city1Obj != null); //$NON-NLS-1$
        String resultDate = city1Obj.toString();
        assertEquals("The resultDate should be beijing", "beijing", //$NON-NLS-1$ //$NON-NLS-2$
                resultDate);
        Object city2Obj = survivorMap.get("city2"); //$NON-NLS-1$
        assertTrue("The city1Obj should not be null", city2Obj != null); //$NON-NLS-1$
        resultDate = city2Obj.toString();
        assertEquals("The resultDate should be shanghai", "shanghai", //$NON-NLS-1$ //$NON-NLS-2$
                resultDate);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case7 most frequent->first one
     * most common rule generate conflict then resolve by first one between conflict values
     */

    @Test
    public void testRunSessionMostCommonGetConflictThenDefauleRule() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH,
                SampleDataConflictMostCommon2Longest2MostRecent.PKG_NAME_CONFLICT_FRE_LONG_RECENT);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleDataConflictMostCommon2Longest2MostRecent.RULES_CONFLICT_FRE_LONG_RECENT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 1", 1, conflictsOfSurvivor.size()); //$NON-NLS-1$
        assertTrue("The column of conflict should be firstName", conflictsOfSurvivor.contains("firstName")); //$NON-NLS-1$ //$NON-NLS-2$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object firstNameObj = survivorMap.get("firstName"); //$NON-NLS-1$
        assertTrue("The firstNameObj should not be null", firstNameObj != null); //$NON-NLS-1$
        assertResultIsFirstConflictedValue();

    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case8 most frequent&&no check ignore blank no conflict
     * 
     * The number of blank are 3 so that survived value should be " "(one space character)
     */

    @Test
    public void testRunSessionMostCommonAndNoIgnoreBlank() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH,
                SampleDataConflictMostCommonAndNoIgnoreBlank.PKG_NAME_CONFLICT_FRE_LONG_RECENT_WITHOUT_IGNORE_BLANK);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleDataConflictMostCommonAndNoIgnoreBlank.RULES_CONFLICT_FRE_LONG_RECENT_NO_IGNORE_BLANK) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object firstNameObj = survivorMap.get("firstName"); //$NON-NLS-1$
        assertTrue("The firstNameObj should not be null", firstNameObj != null); //$NON-NLS-1$
        String resultDate = firstNameObj.toString();
        assertEquals("The resultDate should be \"   \"", "   ", resultDate); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case9 most frequent&&no check ignore blank no conflict
     * 
     * The reference column of city1 is city2
     * city1 use shortest rule get conflict between "xian" and "lasa"
     * city2 use shortest rule and no conflict final survived value is "xian"
     * Because of city2 is reference column of city1 so that we take survived value from city2 column.
     * After that both city1 and city2 keep same survived value which is "xian"
     * I think it is conflict with {@link SurvivorshipManagerTest#testRunSessionMostCommon2Longest2keepOneOfDuplicte()}
     * 
     */

    @Test
    public void testRunSessionShortest2OtherColumnDuplicateSurvivedValue() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH,
                SampleDataConflictShortest2OtherColumnDuplicateSurvivedValue.PKG_NAME_CONFLICT_TWO_TARGET_SAME_RESULT_REFERENCE_COLUMN);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("city1") || column.getName().equals("city2")) { //$NON-NLS-1$ //$NON-NLS-2$
                for (ConflictRuleDefinition element : SampleDataConflictShortest2OtherColumnDuplicateSurvivedValue.RULES_CONFLICT_RESOLVE) {
                    if (column.getName().equals(element.getTargetColumn())) {
                        column.getConflictResolveList().add(element);
                    }
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictShortest2OtherColumnDuplicateSurvivedValue.RULES_CONFLICT_TWO_TARGET_SAME_RESULT_REFERENCE_COLUMN) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 11, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object city1NameObj = survivorMap.get("city1"); //$NON-NLS-1$
        assertTrue("The city1NameObj should not be null", city1NameObj != null); //$NON-NLS-1$
        String resultDate = city1NameObj.toString();
        assertEquals("The resultDate should be lasa", "lasa", resultDate); //$NON-NLS-1$ //$NON-NLS-2$
        Object city2NameObj = survivorMap.get("city2"); //$NON-NLS-1$
        assertTrue("The city2NameObj should not be null", city2NameObj != null); //$NON-NLS-1$
        resultDate = city2NameObj.toString();
        assertEquals("The resultDate should be xian", "xian", resultDate); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case10 most common&&no check ignore blank fillEmptyby and most common again
     * 
     * 
     */

    @Test
    public void testRunSessionMoreThanOneGroupThenFillEmtpyBy() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleDataConflictMutilGroupFillEmptyBy.PKG_NAME_CONFLICT);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("firstName")) { //$NON-NLS-1$ 
                for (ConflictRuleDefinition element : SampleDataConflictMutilGroupFillEmptyBy.RULES_CONFLICT_RESOLVE) {
                    if (column.getName().equals(element.getTargetColumn())) {
                        column.getConflictResolveList().add(element);
                    }
                }
            }
            manager.getColumnList().add(column);
        }

        for (RuleDefinition element : SampleDataConflictMutilGroupFillEmptyBy.RULES_CONFLICT) {
            manager.addRuleDefinition(element);
        }
        // group 1
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/mutilGroupConflicts.csv", 3, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object firstNameObj = survivorMap.get("firstName"); //$NON-NLS-1$
        assertTrue("The city1NameObj should not be null", firstNameObj != null); //$NON-NLS-1$
        String resultDate = firstNameObj.toString();
        assertEquals("The resultDate should be Lili", "Lili", resultDate); //$NON-NLS-1$ //$NON-NLS-2$
        // group 2
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/mutilGroupConflicts.csv", 2, 9, 2)); //$NON-NLS-1$
        // 5. Retrieve results
        conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 1", 1, conflictsOfSurvivor.size()); //$NON-NLS-1$
        assertTrue("The column of conflict should be firstName", conflictsOfSurvivor.contains("firstName")); //$NON-NLS-1$ //$NON-NLS-2$
        survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        firstNameObj = survivorMap.get("firstName"); //$NON-NLS-1$
        assertTrue("The city1NameObj should not be null", firstNameObj != null); //$NON-NLS-1$
        resultDate = firstNameObj.toString();
        assertEquals("The resultDate should be Lili", "Lili", resultDate); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case11 check conflict column is normal
     * 
     * 
     */

    @Test
    public void testRunSessionMoreThanOneGroupCollictColumnDisNormal() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleDataConflictMutilGroupConflictDisError.PKG_NAME_CONFLICT);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("firstName") || column.getName().equals("lastName")) { //$NON-NLS-1$ //$NON-NLS-2$ 
                for (ConflictRuleDefinition element : SampleDataConflictMutilGroupConflictDisError.RULES_CONFLICT_RESOLVE) {
                    if (column.getName().equals(element.getTargetColumn())) {
                        column.getConflictResolveList().add(element);
                    }
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictMutilGroupConflictDisError.RULES_CONFLICT) {
            manager.addRuleDefinition(element);
        }
        // group 1
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/mutilGroupConflicts.csv", 3, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object firstNameObj = survivorMap.get("firstName"); //$NON-NLS-1$
        assertTrue("The city1NameObj should not be null", firstNameObj != null); //$NON-NLS-1$
        String resultDate = firstNameObj.toString();
        assertEquals("The resultDate should be Lili", "Lili", resultDate); //$NON-NLS-1$ //$NON-NLS-2$
        List<HashSet<String>> conflictList = manager.getConflictList();
        assertTrue("The conflictList should not be null", conflictList != null); //$NON-NLS-1$ 
        //        assertTrue("The second row exist a conflict data which column name should be firstName", //$NON-NLS-1$
        //                conflictList.get(1).contains("firstName")); //$NON-NLS-1$
        // group 2
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/mutilGroupConflicts.csv", 2, 9, 2)); //$NON-NLS-1$
        // 5. Retrieve results
        conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 1", 1, conflictsOfSurvivor.size()); //$NON-NLS-1$
        assertTrue("The column of conflict should be lastName", conflictsOfSurvivor.contains("lastName")); //$NON-NLS-1$ //$NON-NLS-2$
        survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        firstNameObj = survivorMap.get("firstName"); //$NON-NLS-1$
        assertTrue("The city1NameObj should not be null", firstNameObj != null); //$NON-NLS-1$
        resultDate = firstNameObj.toString();
        assertEquals("The resultDate should be Lili", "Lili", resultDate); //$NON-NLS-1$ //$NON-NLS-2$
        conflictList = manager.getConflictList();
        assertTrue("The conflictList should not be null", conflictList != null); //$NON-NLS-1$ 
        assertTrue("The second row exist a conflict data which column name should be lastName", //$NON-NLS-1$
                conflictList.get(1).contains("lastName")); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#checkConflictRuleValid()}.
     * check 1 mappingTo need firstName longest need to lastName
     */
    @Test
    public void testCheckConflictRuleValid() {
        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            for (ConflictRuleDefinition element : SampleDataConflictCheckRule.RULES_CONFLICT_RESOLVE) {
                if (column.getName().equals(element.getTargetColumn())) {
                    column.getConflictResolveList().add(element);
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictCheckRule.RULES) {
            manager.addRuleDefinition(element);
        }

        Map<String, List<String>> checkConflictRuleValid = manager.checkConflictRuleValid();
        assertTrue("firstName is not exist survived value so that it must be invalid value", //$NON-NLS-1$
                checkConflictRuleValid.containsKey("firstName")); //$NON-NLS-1$
        assertEquals("firstName is not exist survived value so that it must be invalid value", //$NON-NLS-1$
                "firstName does not contain any survived value", //$NON-NLS-1$
                checkConflictRuleValid.get("firstName").get(0)); //$NON-NLS-1$
        assertTrue("lastName is not exist survived value so that it must be invalid value", //$NON-NLS-1$
                checkConflictRuleValid.containsKey("lastName")); //$NON-NLS-1$
        assertEquals("lastName is not exist survived value so that it must be invalid value", //$NON-NLS-1$
                "lastName does not contain any survived value", //$NON-NLS-1$
                checkConflictRuleValid.get("lastName").get(0)); //$NON-NLS-1$

    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#checkConflictRuleValid()}.
     * 2 rules Circular dependency case
     */
    @Test
    public void testCheckConflictRuleValidWith2RulesCycDependency() {
        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            for (ConflictRuleDefinition element : SampleDataConflictCheckRule.RULES_CONFLICT_RESOLVE_CASE2) {
                if (column.getName().equals(element.getTargetColumn())) {
                    column.getConflictResolveList().add(element);
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictCheckRule.RULES) {
            manager.addRuleDefinition(element);
        }

        Map<String, List<String>> checkConflictRuleValid = manager.checkConflictRuleValid();
        assertTrue("city1 and city2 should be exist circular dependency", //$NON-NLS-1$
                checkConflictRuleValid.containsKey("city1")); //$NON-NLS-1$
        assertEquals("city2 cannot be survived as city1 because of circular dependency", //$NON-NLS-1$
                "city2 cannot be survived as city1 because of circular dependency", //$NON-NLS-1$
                checkConflictRuleValid.get("city1").get(0)); //$NON-NLS-1$
        assertTrue("city2 and city1 should be exist circular dependency", //$NON-NLS-1$
                checkConflictRuleValid.containsKey("city2")); //$NON-NLS-1$
        assertEquals("city1 cannot be survived as city2 because of circular dependency", //$NON-NLS-1$
                "city1 cannot be survived as city2 because of circular dependency", //$NON-NLS-1$
                checkConflictRuleValid.get("city2").get(0)); //$NON-NLS-1$

    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#checkConflictRuleValid()}.
     * 3 rules Circular dependency case
     */
    @Test
    public void testCheckConflictRuleValidWith3RulesCycDependency() {
        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            for (ConflictRuleDefinition element : SampleDataConflictCheckRule.RULES_CONFLICT_RESOLVE_CASE3) {
                if (column.getName().equals(element.getTargetColumn())) {
                    column.getConflictResolveList().add(element);
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictCheckRule.RULES_CASE3) {
            manager.addRuleDefinition(element);
        }
        Map<String, List<String>> checkConflictRuleValid = manager.checkConflictRuleValid();
        assertTrue("city1 and city2 should be exist circular dependency", //$NON-NLS-1$
                checkConflictRuleValid.containsKey("city1")); //$NON-NLS-1$
        assertTrue("city2 and id should be exist circular dependency", //$NON-NLS-1$
                checkConflictRuleValid.containsKey("city2")); //$NON-NLS-1$
        assertTrue("id and city1 should be exist circular dependency", //$NON-NLS-1$
                checkConflictRuleValid.containsKey("id")); //$NON-NLS-1$
        assertEquals("city2 cannot be survived as city1 because of circular dependency", //$NON-NLS-1$
                "city2 cannot be survived as city1 because of circular dependency", //$NON-NLS-1$
                checkConflictRuleValid.get("city1").get(0)); //$NON-NLS-1$
        assertEquals("id cannot be survived as city2 because of circular dependency", //$NON-NLS-1$
                "id cannot be survived as city2 because of circular dependency", //$NON-NLS-1$
                checkConflictRuleValid.get("city2").get(0)); // $NON- //$NON-NLS-1$
        assertEquals("city1 cannot be survived as id because of circular dependency", //$NON-NLS-1$
                "city1 cannot be survived as id because of circular dependency", //$NON-NLS-1$
                checkConflictRuleValid.get("id").get(0)); //$NON-NLS-1$

    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#checkConflictRuleValid()}.
     * 4 no Circular dependency case
     */
    @Test
    public void testCheckConflictRuleValidNormal() {
        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            for (ConflictRuleDefinition element : SampleDataConflictCheckRule.RULES_CONFLICT_RESOLVE_CASE4) {
                if (column.getName().equals(element.getTargetColumn())) {
                    column.getConflictResolveList().add(element);
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictCheckRule.RULES_CASE3) {
            manager.addRuleDefinition(element);
        }
        manager.dataset = new DataSet(manager.getColumnList());
        Map<String, List<String>> checkConflictRuleValid = manager.checkConflictRuleValid();
        assertEquals("All of rule should be valid so that the size of map should be 0", 0, checkConflictRuleValid.size()); //$NON-NLS-1$
        assertEquals("column oder size should be 9 because of there are 9 columns", 9, //$NON-NLS-1$
                manager.getDataSet().getColumnOrder().size());
        assertEquals("The first one should be firstName", "firstName", manager.getDataSet().getColumnOrder().get(0).getName()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The first one should be city1", "city1", manager.getDataSet().getColumnOrder().get(1).getName()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("The first one should be city2", "city2", manager.getDataSet().getColumnOrder().get(2).getName()); //$NON-NLS-1$ //$NON-NLS-2$

    }

    /**
     * test case for TDQ-14225. It shouldn't output the CONFLICT when the conflict is resolved.
     */
    @Test
    public void testRunSessionNoOutputConflictColumn() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH,
                SampleDataConflictMostCommon2Longest2MostRecent.PKG_NAME_CONFLICT_FRE_LONG_RECENT);

        for (String str : SampleDataCheckOutputConflictColumn.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataCheckOutputConflictColumn.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("firstName")) { //$NON-NLS-1$
                for (ConflictRuleDefinition element : SampleDataConflictMostCommon2Longest2MostRecent.RULES_CONFLICT_RESOLVE) {
                    if (element.getReferenceColumn().equals("firstName")) {
                        column.getConflictResolveList().add(element);
                    }
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictMostCommon2Longest2MostRecent.RULES_CONFLICT_FRE_LONG_RECENT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(SampleDataCheckOutputConflictColumn.SAMPLE_INPUT_1);
        // 5. Retrieve results
        List<HashSet<String>> conflictList = manager.getConflictList();
        assertEquals(4, conflictList.size());
        for (HashSet<String> set : conflictList) {
            assertTrue(set.isEmpty());
        }
    }

    /**
     * test case for TDQ-14225. only output the last time CONFLICT name
     */
    @Test
    public void testRunSessionOutputLastConflictColumn() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH,
                SampleDataConflictMostCommon2Longest2MostRecent.PKG_NAME_CONFLICT_FRE_LONG_RECENT);

        for (String str : SampleDataCheckOutputConflictColumn.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataCheckOutputConflictColumn.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("firstName")) { //$NON-NLS-1$
                for (ConflictRuleDefinition element : SampleDataConflictMostCommon2Longest2MostRecent.RULES_CONFLICT_RESOLVE) {
                    if (element.getReferenceColumn().equals("firstName")) {//$NON-NLS-1$
                        column.getConflictResolveList().add(element);
                    }
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictMostCommon2Longest2MostRecent.RULES_CONFLICT_FRE_LONG_RECENT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(SampleDataCheckOutputConflictColumn.SAMPLE_INPUT_2);

        List<HashSet<String>> conflictList = manager.getConflictList();
        assertTrue(conflictList.size() == 6);
        assertTrue(conflictList.get(0).isEmpty());
        assertTrue(conflictList.get(1).isEmpty());
        String expectConflictName = "[firstName]";
        assertEquals(expectConflictName, conflictList.get(2).toString());
        assertEquals(expectConflictName, conflictList.get(3).toString());
        assertEquals(expectConflictName, conflictList.get(4).toString());
        assertEquals(expectConflictName, conflictList.get(5).toString());
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case Execute rules with ui order(rather than as columns order)
     * 
     */

    @Test
    public void testRunSessionExecuteWithUIOrderRevmoveIsSecondOne() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleDataConflictExecuteRulesWithUIOrder.PKG_NAME_CONFLICT);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("city1") || column.getName().equals("city2")) { //$NON-NLS-1$ //$NON-NLS-2$
                for (ConflictRuleDefinition element : SampleDataConflictExecuteRulesWithUIOrder.RULES_CONFLICT_RESOLVE_SECOND_LONGEST_INVALID) {
                    if (column.getName().equals(element.getTargetColumn())) {
                        column.getConflictResolveList().add(element);
                    }
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictExecuteRulesWithUIOrder.RULES_CONFLICT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 5, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object city2 = survivorMap.get("city2"); //$NON-NLS-1$
        assertTrue("The chity2 should not be null", city2 != null); //$NON-NLS-1$
        String resultCity = (String) city2;

        // 08-08-2000 is we expect after implement code because we use most recent to resolve conflict
        assertEquals("The resultCity should be beijing", "beijing", //$NON-NLS-1$ //$NON-NLS-2$
                resultCity);
        Object city1 = survivorMap.get("city1"); //$NON-NLS-1$
        assertTrue("The chity1 should not be null", city1 != null); //$NON-NLS-1$
        resultCity = (String) city1;

        // 08-08-2000 is we expect after implement code because we use most recent to resolve conflict
        assertEquals("The resultCity should be shanghai", "shanghai", //$NON-NLS-1$ //$NON-NLS-2$
                resultCity);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case Execute rules with ui order(rather than as columns order)
     * 
     */

    @Test
    public void testRunSessionExecuteWithUIOrderRevmoveIsFirstOne() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleDataConflictExecuteRulesWithUIOrder.PKG_NAME_CONFLICT);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("city1") || column.getName().equals("city2")) { //$NON-NLS-1$ //$NON-NLS-2$
                for (ConflictRuleDefinition element : SampleDataConflictExecuteRulesWithUIOrder.RULES_CONFLICT_RESOLVE_REMOVE_DUPLICATE_INVALID) {
                    if (column.getName().equals(element.getTargetColumn())) {
                        column.getConflictResolveList().add(element);
                    }
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictExecuteRulesWithUIOrder.RULES_CONFLICT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 5, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object city2 = survivorMap.get("city2"); //$NON-NLS-1$
        assertTrue("The chity2 should not be null", city2 != null); //$NON-NLS-1$
        String resultCity = (String) city2;

        // 08-08-2000 is we expect after implement code because we use most recent to resolve conflict
        assertEquals("The chity2 should be shanghai", "shanghai", //$NON-NLS-1$ //$NON-NLS-2$
                resultCity);
        Object city1 = survivorMap.get("city1"); //$NON-NLS-1$
        assertTrue("The chity1 should not be null", city1 != null); //$NON-NLS-1$
        resultCity = (String) city1;

        // 08-08-2000 is we expect after implement code because we use most recent to resolve conflict
        assertEquals("The resultCity should be shanghai", "shanghai", //$NON-NLS-1$ //$NON-NLS-2$
                resultCity);
    }

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case Execute rules with ui order(rather than as columns order)
     * 
     */

    @Test
    public void testRunSessionExecuteWithUIOrderRevmoveFirstRuleIsInvalid() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleDataConflictExecuteRulesWithUIOrder.PKG_NAME_CONFLICT);

        for (String str : SampleDataConflict.COLUMNS_CONFLICT.keySet()) {
            Column column = new Column(str, SampleDataConflict.COLUMNS_CONFLICT.get(str));
            if (column.getName().equals("city1") || column.getName().equals("city2")) { //$NON-NLS-1$ //$NON-NLS-2$
                for (ConflictRuleDefinition element : SampleDataConflictExecuteRulesWithUIOrder.RULES_CONFLICT_RESOLVE_MATCH_REGEX_INVALID) {
                    if (column.getName().equals(element.getTargetColumn())) {
                        column.getConflictResolveList().add(element);
                    }
                }
            }
            manager.getColumnList().add(column);
        }
        for (RuleDefinition element : SampleDataConflictExecuteRulesWithUIOrder.RULES_CONFLICT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.checkConflictRuleValid();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv", 5, 9, 1)); //$NON-NLS-1$
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        assertEquals("The size of conflictsOfSurvivor should be 1", 1, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$
        Object city2 = survivorMap.get("city2"); //$NON-NLS-1$
        assertTrue("The chity2 should not be null", city2 != null); //$NON-NLS-1$
        String resultCity = (String) city2;

        // 08-08-2000 is we expect after implement code because we use most recent to resolve conflict
        assertEquals("The chity2 should be beijing", "beijing", //$NON-NLS-1$ //$NON-NLS-2$
                resultCity);
        Object city1 = survivorMap.get("city1"); //$NON-NLS-1$
        assertTrue("The chity1 should not be null", city1 != null); //$NON-NLS-1$
        resultCity = (String) city1;

        // 08-08-2000 is we expect after implement code because we use most recent to resolve conflict
        assertEquals("The resultCity should be shanghai", "shanghai", //$NON-NLS-1$ //$NON-NLS-2$
                resultCity);
    }

    /**
     * Create by zshen judge whether conflict value is right
     */
    private void assertResultIsFirstConflictedValue() {
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        manager.getDataSet().getRecordList().get(1).getAttribute("firstName").getValue(); //$NON-NLS-1$
        for (Set<String> ciflictValue : manager.getConflictList()) {
            if (ciflictValue.size() > 0) {
                ciflictValue.toArray()[0].toString();
            }
        }

        String survivedColumnValue = null;
        String survivedColumnName = null;
        for (String columnName : survivorMap.keySet()) {
            survivedColumnName = columnName;
            survivedColumnValue = survivorMap.get(columnName).toString();
            if (survivedColumnName != null && survivedColumnValue != null) {
                break;
            }
        }
        int index = 0;
        for (Record record : manager.getDataSet().getRecordList()) {
            Attribute currentAttribute = record.getAttribute(survivedColumnName);
            if (currentAttribute == null) {
                continue;
            }
            String currentValue = currentAttribute.getValue().toString();
            // survivedValue should be same with currentValue
            if (survivedColumnValue != null && survivedColumnValue.equals(currentValue)) {
                assertTrue("first value should be " + currentValue, currentValue.equals(survivedColumnValue)); //$NON-NLS-1$
                break;
                // survivedValue should not be same with currentValue
            } else if (manager.getConflictList().get(index) != null) {
                assertFalse("first value should not be " + currentValue, currentValue.equals(survivedColumnValue)); //$NON-NLS-1$
                break;
            }
            index++;
        }

    }

    /**
     * 
     * Get input data from special csv file
     * 
     * @param file the file full path
     * @return array of input data
     */
    protected Object[][] getTableValue(String file, int rowNum, int colNum, int groupNum) {

        String pathString = ""; //$NON-NLS-1$
        try {
            pathString = this.getClass().getResource(file).toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        BufferedReader br = null;
        String line = ""; //$NON-NLS-1$
        String cvsSplitBy = ","; //$NON-NLS-1$
        int gCount = 0;

        Object[][] result = new Object[rowNum][colNum];
        boolean start = false;
        try {
            br = new BufferedReader(new FileReader(pathString));
            int index = 0;
            while ((line = br.readLine()) != null) {
                Object[] items = line.split(cvsSplitBy);
                if (Integer.valueOf(items[colNum - 1].toString()) != 0) {
                    gCount += 1;
                    if (gCount > groupNum) {
                        break;
                    } else if (gCount < groupNum) {
                        continue;
                    }
                    start = true;
                }

                if (!start) {
                    continue;
                }
                int y = 0;
                for (Object readArray : items) {
                    if (readArray.toString().equals("null")) { //$NON-NLS-1$
                        readArray = null;
                    }

                    if (y == 5 && readArray != null) {
                        result[index][5] = Integer.getInteger(readArray.toString());
                    } else if (y == 6 && readArray != null) {
                        result[index][6] = SampleData.stringToDate(readArray.toString(), "dd-MM-yyyy"); //$NON-NLS-1$
                    } else if (y == 8 && readArray != null) {
                        result[index][8] = Integer.parseInt(readArray.toString());
                    } else {
                        result[index][y] = readArray;
                    }
                    y++;
                }
                index++;
                if (index >= rowNum) {
                    break;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                // no need to be implements
            }
        }

        return result;

    }
}
