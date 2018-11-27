// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.survivorship.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.survivorship.action.ISurvivorshipAction;
import org.talend.survivorship.action.handler.FunctionParameter;

public class DataSetTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.talend.survivorship.model.DataSet#getValueAfterFiled(int, java.lang.String)}.
     */
    @Test
    public void testGetValueAfterFiled() {
        Column col1 = new Column("city1", "String"); //$NON-NLS-1$ //$NON-NLS-2$
        Column col2 = new Column("city2", "String"); //$NON-NLS-1$ //$NON-NLS-2$
        List<Column> columns = new ArrayList<>();
        columns.add(col1);
        columns.add(col2);
        DataSet dataset = new DataSet(columns);
        // first record
        Record record1 = new Record();
        record1.setId(1);
        Attribute att1 = new Attribute(record1, col1, "value1"); //$NON-NLS-1$
        Attribute att2 = new Attribute(record1, col2, "value2"); //$NON-NLS-1$
        record1.putAttribute("city1", att1); //$NON-NLS-1$
        record1.putAttribute("city2", att2); //$NON-NLS-1$
        col1.putAttribute(record1, att1);
        col2.putAttribute(record1, att2);
        dataset.getRecordList().add(record1);
        // second record
        record1 = new Record();
        record1.setId(2);
        att1 = new Attribute(record1, col1, "value3"); //$NON-NLS-1$
        att2 = new Attribute(record1, col2, "value4"); //$NON-NLS-1$
        record1.putAttribute("city1", att1); //$NON-NLS-1$
        record1.putAttribute("city2", att2); //$NON-NLS-1$
        col1.putAttribute(record1, att1);
        col2.putAttribute(record1, att2);
        dataset.getRecordList().add(record1);
        List<Integer> conflictRowNum = new ArrayList<>();
        conflictRowNum.add(0);
        dataset.getConflictDataMap().get().put("city2", conflictRowNum); //$NON-NLS-1$
        ISurvivorshipAction action = RuleDefinition.Function.MostCommon.getAction();
        Column refColumn = col1;
        Column tarColumn = col2;
        String ruleName = "rule1"; //$NON-NLS-1$
        String expression = null;
        boolean isIgnoreBlank = false;
        String fillColumn = "city2"; //$NON-NLS-1$
        boolean isDealDup = false;
        Map<String, Integer> columnIndexMap = new HashMap<>();
        columnIndexMap.put("city1", 0); //$NON-NLS-1$
        columnIndexMap.put("city2", 1); //$NON-NLS-1$
        FunctionParameter functionParameter = new FunctionParameter(action, expression, isIgnoreBlank, isDealDup);
        List<HashSet<String>> conflictList = dataset.getConflictList();
        HashSet hashSet = new HashSet();
        hashSet.add("city1"); //$NON-NLS-1$
        hashSet.add("city2"); //$NON-NLS-1$
        conflictList.add(hashSet);

        hashSet = new HashSet();
        hashSet.add("city1"); //$NON-NLS-1$
        hashSet.add("city2"); //$NON-NLS-1$
        conflictList.add(hashSet);

        Object valueAfterFiled = dataset.getValueAfterFiled(-1, "city1"); //$NON-NLS-1$
        Assert.assertEquals("The value of first row on city1 column is value1", "value1", valueAfterFiled); //$NON-NLS-1$ //$NON-NLS-2$
        valueAfterFiled = dataset.getValueAfterFiled(0, "city2"); //$NON-NLS-1$
        Assert.assertEquals("The value of second row on city2 column is value4", "value4", valueAfterFiled); //$NON-NLS-1$ //$NON-NLS-2$
        valueAfterFiled = dataset.getValueAfterFiled(-2, "city2"); //$NON-NLS-1$
        Assert.assertEquals("The value of -1th row on city2 column is null", null, valueAfterFiled); //$NON-NLS-1$
        valueAfterFiled = dataset.getValueAfterFiled(1, "city2"); //$NON-NLS-1$
        Assert.assertEquals("The value of third row on city2 column is null", null, valueAfterFiled); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.survivorship.model.DataSet#arrangeConflictCol(String, SurvivedResult)}.
     */
    @Test
    public void testArrangeConflictCol() throws Exception {
        Column col1 = new Column("city1", "String");
        Column col2 = new Column("city2", "String");
        List<Column> columns = new ArrayList<>();
        columns.add(col1);
        columns.add(col2);
        DataSet dataset = new DataSet(columns);
        // first record
        Record record1 = new Record();
        record1.setId(1);
        Attribute att1 = new Attribute(record1, col1, "value1");
        Attribute att2 = new Attribute(record1, col2, "value2");
        record1.putAttribute("city1", att1);
        record1.putAttribute("city2", att2);
        col1.putAttribute(record1, att1);
        col2.putAttribute(record1, att2);
        dataset.getRecordList().add(record1);
        // second record
        record1 = new Record();
        record1.setId(2);
        att1 = new Attribute(record1, col1, "value3");
        att2 = new Attribute(record1, col2, "value4");
        record1.putAttribute("city1", att1);
        record1.putAttribute("city2", att2);
        col1.putAttribute(record1, att1);
        col2.putAttribute(record1, att2);
        dataset.getRecordList().add(record1);
        List<Integer> conflictRowNum = new ArrayList<>();
        conflictRowNum.add(0);
        dataset.getConflictDataMap().get().put("city2", conflictRowNum);
        conflictRowNum = new ArrayList<>();
        conflictRowNum.add(0);
        dataset.getConflictDataMap().get().put("city1", conflictRowNum);
        ISurvivorshipAction action = RuleDefinition.Function.MostCommon.getAction();
        Column refColumn = col1;
        Column tarColumn = col2;
        String ruleName = "rule1";
        String expression = null;
        boolean isIgnoreBlank = false;
        String fillColumn = "city2";
        boolean isDealDup = false;
        Map<String, Integer> columnIndexMap = new HashMap<>();
        columnIndexMap.put("city1", 0);
        columnIndexMap.put("city2", 1);
        FunctionParameter functionParameter = new FunctionParameter(action, expression, isIgnoreBlank, isDealDup);
        List<HashSet<String>> conflictList = dataset.getConflictList();
        HashSet hashSet = new HashSet();
        hashSet.add("city1");
        hashSet.add("city2");
        conflictList.add(hashSet);

        hashSet = new HashSet();
        hashSet.add("city1");
        hashSet.add("city2");
        conflictList.add(hashSet);

        HashMap<String, Object> survivorMap = dataset.getSurvivorMap();
        survivorMap.put("city1", "value1");
        survivorMap.put("city2", "value2");

        SurvivedResult survivedResult1 = new SurvivedResult(0, "city1");
        survivedResult1.setResolved(true);
        SurvivedResult survivedResult2 = new SurvivedResult(0, "city2");
        dataset.arrangeConflictCol("city1", survivedResult1);
        dataset.arrangeConflictCol("city2", survivedResult2);

        Assert.assertEquals("The size of conflict list should be 0", 0, conflictList.get(0).size());
        Assert.assertEquals("The size of conflict list should be 2", 2, conflictList.get(1).size());
        Iterator<String> iterator = conflictList.get(1).iterator();
        String conflictColumnForFirstRecored = iterator.next();
        String conflictColumnForSecondRecored = iterator.next();
        Assert.assertEquals("The first conflict column in second record should be city1", "city1", conflictColumnForFirstRecored);
        Assert.assertEquals("The second conflict column in second record should be city2", "city2",
                conflictColumnForSecondRecored);

        dataset.getConflictDataMap().get().clear();
        // there is not any NPE
        dataset.arrangeConflictCol("city2", survivedResult2);

    }

}
