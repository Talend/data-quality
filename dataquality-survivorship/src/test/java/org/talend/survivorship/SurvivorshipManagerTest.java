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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.talend.survivorship.model.Attribute;
import org.talend.survivorship.model.Record;
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
     * no conflict case
     */
    @Test
    public void testRunSessionNoConflictCase() {
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
     * 
     * generate conflict by most common rule and resolve conflict by most recent rule
     * recent date should be 08-08-2000 rather than 04-04-2000
     */

    @Test
    public void testRunSessionMostCommon2MostRecent() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME_CONFLICT);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv"));
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
     * 
     * For city1 column, after most common rule generate conflict beijing and shanghai then use Longest rule resolve conflict.
     * Rusult is shanghai
     */

    @Test
    public void testRunSessionMostCommon2Longest() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME_CONFLICT_FRE_LONG);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT_FRE_LONG) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv"));
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        Assert.assertEquals("The size of conflictsOfSurvivor should be 1", 1, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Assert.assertTrue("The column of conflict should be birthday", conflictsOfSurvivor.contains("city1")); //$NON-NLS-1$ //$NON-NLS-2$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        Assert.assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$ 
        Object cityObj = survivorMap.get("city1"); //$NON-NLS-1$
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
     * firstName column used rule most common then get conflict between Tony and Lili.
     * Then use Longest to resolve conflict but them can't.
     * Then use most recent rule on birthday column to resolve conflict between 04-04-2000 and 06-06-2000
     * we get final result 06-06-2000 and mapping to fistName column the result should be Tony.
     * Because of the birthday of Tony is 06-06-2000.
     * Note that Ignore blank has been check on this case
     */

    @Test
    public void testRunSessionMostCommon2Longest2MostRecent() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME_CONFLICT_FRE_LONG_RECENT);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT_FRE_LONG_RECENT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv"));
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
     * Because of there are two empty value so that we get value from firstName column.
     * Then do most common rule we get Green=2 |Tony=2| null is ignore aotomatic.It is conflict.
     * Because value "green" is The constant so that we ignore it.
     * Final we get rusult "Tony"
     */

    @Test
    public void testRunSessionOtherColumn2MostCommon2Constant() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME_CONFLICT_FRE_NULL_CONSTANT);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT_FRE_NULL_CONTSTANT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv"));
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
     * @case5 city1 column use Longest get survived value hebeihebei.
     * birthday column use most Recent get survived value 08-08-2000.
     * Althougth there are two values are 08-08-2000 but they are same so that no generate conflict
     */

    @Test
    public void testRunSessionTwoNoConflictColumnGetOneSameSurvivedValue() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME_CONFLICT_TWO_TARGET_ONE_COLUMN);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT_TWO_TARGET_ONE_COLUMN) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv"));
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

    /**
     * Test method for {@link org.talend.survivorship.SurvivorshipManager#runSession(java.lang.String[][])}.
     * 
     * @case6 most frequent->longest->keep one of duplicates only
     * 
     * now both survived values are beijing after implememt code there should keep one value and it should be shanghai
     * Because we will use most common to generate conflict between beijing=2 and shanghai=2.
     * And use Longest to resolve conflict get final result shanghai.
     * Both city1 and city2 values are "shanghai" it is duplicte .
     * So that we just keep one of them.
     */
    @Test
    public void testRunSessionMostCommon2Longest2keepOneOfDuplicte() {

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME_CONFLICT_TWO_TARGET_SAME_VALUE);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT_TWO_TARGET_SAME_RESULT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv"));
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        Assert.assertEquals("The size of conflictsOfSurvivor should be 2", 2, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        Assert.assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$ 
        Assert.assertTrue("The size of SurvivorMap should be 2", survivorMap.size() == 2); //$NON-NLS-1$
        Object city1Obj = survivorMap.get("city1"); //$NON-NLS-1$
        Assert.assertTrue("The city1Obj should not be null", city1Obj != null); //$NON-NLS-1$ 
        String resultDate = city1Obj.toString();
        Assert.assertEquals("The resultDate should be shanghai", "shanghai", //$NON-NLS-1$ //$NON-NLS-2$
                resultDate);
        Object city2Obj = survivorMap.get("city2"); //$NON-NLS-1$
        Assert.assertTrue("The city1Obj should not be null", city2Obj != null); //$NON-NLS-1$ 
        resultDate = city2Obj.toString();
        Assert.assertEquals("The resultDate should be empty", "", //$NON-NLS-1$ //$NON-NLS-2$
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

        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME_CONFLICT_FRE_LONG_RECENT);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT_FRE_LONG_RECENT) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv"));
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        Assert.assertEquals("The size of conflictsOfSurvivor should be 1", 1, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Assert.assertTrue("The column of conflict should be firstName", conflictsOfSurvivor.contains("firstName")); //$NON-NLS-1$ //$NON-NLS-2$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        Assert.assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$ 
        Object firstNameObj = survivorMap.get("firstName"); //$NON-NLS-1$
        Assert.assertTrue("The firstNameObj should not be null", firstNameObj != null); //$NON-NLS-1$ 
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
                SampleData.PKG_NAME_CONFLICT_FRE_LONG_RECENT_WITHOUT_IGNORE_BLANK);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT_FRE_LONG_RECENT_NO_IGNORE_BLANK) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv"));
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        Assert.assertEquals("The size of conflictsOfSurvivor should be 0", 0, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        Assert.assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$ 
        Object firstNameObj = survivorMap.get("firstName"); //$NON-NLS-1$
        Assert.assertTrue("The firstNameObj should not be null", firstNameObj != null); //$NON-NLS-1$ 
        String resultDate = firstNameObj.toString();
        Assert.assertEquals("The resultDate should be \" \"", " ", resultDate);
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
                SampleData.PKG_NAME_CONFLICT_TWO_TARGET_SAME_RESULT_REFERENCE_COLUMN);

        for (String str : SampleData.COLUMNS_CONFLICT.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS_CONFLICT.get(str));
        }
        for (RuleDefinition element : SampleData.RULES_CONFLICT_TWO_TARGET_SAME_RESULT_REFERENCE_COLUMN) {
            manager.addRuleDefinition(element);
        }
        manager.initKnowledgeBase();
        manager.runSession(getTableValue("/org.talend.survivorship.conflict/conflicts.csv"));
        // 5. Retrieve results
        HashSet<String> conflictsOfSurvivor = manager.getConflictsOfSurvivor();
        Assert.assertEquals("The size of conflictsOfSurvivor should be 1", 1, conflictsOfSurvivor.size()); //$NON-NLS-1$
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        Assert.assertTrue("The SurvivorMap should not be null", survivorMap != null); //$NON-NLS-1$ 
        Object city1NameObj = survivorMap.get("city1"); //$NON-NLS-1$
        Assert.assertTrue("The city1NameObj should not be null", city1NameObj != null); //$NON-NLS-1$ 
        String resultDate = city1NameObj.toString();
        Assert.assertEquals("The resultDate should be xian", "xian", resultDate);
        Object city2NameObj = survivorMap.get("city2"); //$NON-NLS-1$
        Assert.assertTrue("The city2NameObj should not be null", city2NameObj != null); //$NON-NLS-1$ 
        resultDate = city2NameObj.toString();
        Assert.assertEquals("The resultDate should be xian", "xian", resultDate);
    }

    /**
     * DOC zshen Comment method "assertResultIsFirstConflictedValue".
     */
    private void assertResultIsFirstConflictedValue() {
        Map<String, Object> survivorMap = manager.getSurvivorMap();
        manager.getDataSet().getRecordList().get(1).getAttribute("firstName").getValue();
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
            break;
        }
        int index = 0;
        for (Record record : manager.getDataSet().getRecordList()) {
            Attribute currentAttribute = record.getAttribute(survivedColumnName);
            if (currentAttribute == null) {
                continue;
            }
            String currentValue = currentAttribute.getValue().toString();
            // survivedValue should be same with currentValue
            if (survivedColumnValue.equals(currentValue)) {
                Assert.assertTrue("first value should be " + currentValue, currentValue.equals(survivedColumnValue));
                break;
                // survivedValue should not be same with currentValue
            } else if (manager.getConflictList().get(index) != null) {
                Assert.assertFalse("first value should not be " + currentValue, currentValue.equals(survivedColumnValue));
                break;
            }
            index++;
        }

    }

    protected Object[][] getTableValue(String file) {

        String pathString = "";
        try {
            pathString = this.getClass().getResource(file).toURI().getPath();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        Object[][] result = new Object[10][9];

        try {
            br = new BufferedReader(new FileReader(pathString));
            int index = 0;
            while ((line = br.readLine()) != null) {
                List<Object> row = new ArrayList<Object>();
                Object[] items = line.split(cvsSplitBy);
                int y = 0;
                for (Object readArray : items) {
                    if (readArray.toString().equals("null")) { //$NON-NLS-1$
                        readArray = null;
                    }

                    if (y == 5 && readArray != null) {
                        result[index][5] = Integer.getInteger(readArray.toString());
                    } else if (y == 6 && readArray != null) {
                        result[index][6] = SampleData.stringToDate(readArray.toString(), "dd-MM-yyyy");
                    } else if (y == 8 && readArray != null) {
                        result[index][8] = Integer.parseInt(readArray.toString());
                    } else {
                        result[index][y] = readArray;
                    }
                    y++;
                }
                index++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }
}
