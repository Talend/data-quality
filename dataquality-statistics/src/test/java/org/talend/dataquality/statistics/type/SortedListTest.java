package org.talend.dataquality.statistics.type;// ============================================================================

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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SortedListTest {

    SortedList<String> sortedList = new SortedList<>();

    @Before
    public void before() {
        sortedList.clear();
    }

    @Test
    public void testIncrement() {
        sortedList.addNewValue("value1");
        sortedList.addNewValue("value2");
        sortedList.addNewValue("value3");
        Assert.assertEquals(sortedList.get(0).left, "value1");
        sortedList.increment(2);
        Assert.assertEquals(sortedList.get(0).left, "value3");
        sortedList.increment(2);
        sortedList.increment(1);
        Assert.assertEquals(sortedList.get(0).left, "value1");
    }
}