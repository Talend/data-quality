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

import org.junit.Assert;
import org.junit.Test;

public class DataSetTest {

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
        Map<String, Integer> columnIndexMap = new HashMap<>();
        columnIndexMap.put("city1", 0); //$NON-NLS-1$
        columnIndexMap.put("city2", 1); //$NON-NLS-1$

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
        conflictRowNum = new ArrayList<>();
        conflictRowNum.add(0);
        dataset.getConflictDataMap().get().put("city1", conflictRowNum); //$NON-NLS-1$
        Map<String, Integer> columnIndexMap = new HashMap<>();
        columnIndexMap.put("city1", 0); //$NON-NLS-1$
        columnIndexMap.put("city2", 1); //$NON-NLS-1$
        List<HashSet<String>> conflictList = dataset.getConflictList();
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("city1"); //$NON-NLS-1$
        hashSet.add("city2"); //$NON-NLS-1$
        conflictList.add(hashSet);

        hashSet = new HashSet<>();
        hashSet.add("city1"); //$NON-NLS-1$
        hashSet.add("city2"); //$NON-NLS-1$
        conflictList.add(hashSet);

        HashMap<String, Object> survivorMap = dataset.getSurvivorMap();
        survivorMap.put("city1", "value1"); //$NON-NLS-1$ //$NON-NLS-2$
        survivorMap.put("city2", "value2"); //$NON-NLS-1$ //$NON-NLS-2$

        SurvivedResult survivedResult1 = new SurvivedResult(0, "city1"); //$NON-NLS-1$
        survivedResult1.setResolved(true);
        SurvivedResult survivedResult2 = new SurvivedResult(0, "city2"); //$NON-NLS-1$
        dataset.arrangeConflictCol("city1", survivedResult1); //$NON-NLS-1$
        dataset.arrangeConflictCol("city2", survivedResult2); //$NON-NLS-1$

        Assert.assertEquals("The size of conflict list should be 0", 0, conflictList.get(0).size()); //$NON-NLS-1$
        Assert.assertEquals("The size of conflict list should be 2", 2, conflictList.get(1).size()); //$NON-NLS-1$
        Iterator<String> iterator = conflictList.get(1).iterator();
        String conflictColumnForFirstRecored = iterator.next();
        String conflictColumnForSecondRecored = iterator.next();
        Assert.assertEquals("The first conflict column in second record should be city1", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                conflictColumnForFirstRecored);
        Assert.assertEquals("The second conflict column in second record should be city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$
                conflictColumnForSecondRecored);

        dataset.getConflictDataMap().get().clear();
        // there is not any NPE
        dataset.arrangeConflictCol("city2", survivedResult2); //$NON-NLS-1$

    }

}
