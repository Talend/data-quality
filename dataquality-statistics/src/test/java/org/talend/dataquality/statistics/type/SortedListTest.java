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
package org.talend.dataquality.statistics.type;

import org.junit.Assert;
import org.junit.Test;

public class SortedListTest {

    @Test
    public void testIncrement() {
        SortedList<String> sortedList = new SortedList<>();
        sortedList.add("value1");
        sortedList.add("value2");
        sortedList.add("value3");
        Assert.assertEquals(sortedList.get(0), "value1");
        sortedList.increment(2);
        Assert.assertEquals(sortedList.get(0), "value3");
        sortedList.increment(2);
        sortedList.increment(1);
        Assert.assertEquals(sortedList.get(0), "value1");
        Assert.assertEquals(sortedList.get(1), "value3");
        Assert.assertEquals(sortedList.get(2), "value2");
    }
}