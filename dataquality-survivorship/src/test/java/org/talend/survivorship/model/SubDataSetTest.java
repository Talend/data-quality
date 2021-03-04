// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class SubDataSetTest {

    /**
     * Test method for {@link org.talend.survivorship.model.SubDataSet#getValueAfterFiled(int, java.lang.String)}.
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
        List<HashSet<String>> conflictList = dataset.getConflictList();
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("city1"); //$NON-NLS-1$
        hashSet.add("city2"); //$NON-NLS-1$
        conflictList.add(hashSet);

        hashSet = new HashSet<>();
        hashSet.add("city1"); //$NON-NLS-1$
        hashSet.add("city2"); //$NON-NLS-1$
        conflictList.add(hashSet);

        SubDataSet subDataSet = new SubDataSet(dataset, null);
        Object valueAfterFiled = subDataSet.getValueAfterFiled(-1, "city1"); //$NON-NLS-1$
        Assert.assertEquals("The value of first row on city1 column is value1", "value1", valueAfterFiled); //$NON-NLS-1$ //$NON-NLS-2$
        valueAfterFiled = subDataSet.getValueAfterFiled(0, "city2"); //$NON-NLS-1$
        Assert.assertEquals("The value of second row on city2 column is value4", "value4", valueAfterFiled); //$NON-NLS-1$ //$NON-NLS-2$
        valueAfterFiled = subDataSet.getValueAfterFiled(-2, "city2"); //$NON-NLS-1$
        Assert.assertEquals("The value of -1th row on city2 column is null", null, valueAfterFiled); //$NON-NLS-1$
        valueAfterFiled = subDataSet.getValueAfterFiled(1, "city2"); //$NON-NLS-1$
        Assert.assertEquals("The value of third row on city2 column is null", null, valueAfterFiled); //$NON-NLS-1$
    }

}
