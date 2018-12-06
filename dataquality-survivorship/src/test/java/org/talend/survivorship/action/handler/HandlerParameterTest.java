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
package org.talend.survivorship.action.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.talend.survivorship.action.ISurvivorshipAction;
import org.talend.survivorship.model.Attribute;
import org.talend.survivorship.model.Column;
import org.talend.survivorship.model.DataSet;
import org.talend.survivorship.model.Record;
import org.talend.survivorship.model.RuleDefinition;
import org.talend.survivorship.model.SubDataSet;

public class HandlerParameterTest {

    /**
     * Test method for
     * {@link org.talend.survivorship.action.handler.HandlerParameter#removeFromConflictList(java.lang.Integer, java.lang.String)}
     * .
     */
    @Test
    public void testRemoveFromConflictList() {
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
        // add some value into conflictList and test removeFromConflictList
        HandlerParameter handlerParameter = new HandlerParameter(dataset, refColumn, tarColumn, ruleName, columnIndexMap,
                fillColumn, functionParameter);
        handlerParameter.removeFromConflictList(0, "city1");
        handlerParameter.removeFromConflictList(1, "city2");
        Assert.assertEquals("The size of conflict list should be 1", 1, conflictList.get(0).size());
        Assert.assertEquals("The size of conflict list should be 1", 1, conflictList.get(1).size());
        String conflictColumnForFirstRecored = conflictList.get(0).iterator().next();
        String conflictColumnForSecondRecored = conflictList.get(1).iterator().next();
        Assert.assertEquals("The conflict column in first record should be city2", "city2", conflictColumnForFirstRecored);
        Assert.assertEquals("The conflict column in second record should be city1", "city1", conflictColumnForSecondRecored);

    }

    /**
     * Test method for
     * {@link org.talend.survivorship.action.handler.HandlerParameter#removeFromConflictList(java.lang.Integer, java.lang.String)}
     * .
     */
    @Test
    public void testRemoveFromConflictListForSubDataSet() {
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
        // add some value into conflictList and test removeFromConflictList
        HandlerParameter handlerParameter = new HandlerParameter(new SubDataSet(dataset, null), refColumn, tarColumn, ruleName,
                columnIndexMap, fillColumn, functionParameter);
        handlerParameter.removeFromConflictList(0, "city1");
        handlerParameter.removeFromConflictList(1, "city2");
        Assert.assertEquals("The size of conflict list should be 1", 1, conflictList.get(0).size());
        Assert.assertEquals("The size of conflict list should be 1", 1, conflictList.get(1).size());
        String conflictColumnForFirstRecored = conflictList.get(0).iterator().next();
        String conflictColumnForSecondRecored = conflictList.get(1).iterator().next();
        Assert.assertEquals("The conflict column in first record should be city2", "city2", conflictColumnForFirstRecored);
        Assert.assertEquals("The conflict column in second record should be city1", "city1", conflictColumnForSecondRecored);

    }

}
