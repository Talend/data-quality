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
package org.talend.survivorship.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.talend.survivorship.model.Column;
import org.talend.survivorship.model.DataSet;

/**
 * Create by zshen Number servive test
 */
public class NumberServiceTest {

    /**
     * Test method for {@link org.talend.survivorship.services.NumberService#init()}.
     */
    @Test
    public void testInit() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        NumberService ns = new NumberService(dataSet);
        ns.putAttributeValues("id1"); //$NON-NLS-1$
        ns.putAttributeValues("id2"); //$NON-NLS-1$
        ns.putAttributeValues("id3"); //$NON-NLS-1$
        ns.putAttributeValues("id4"); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 4", 4, ns.largestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 4", 4, ns.smallestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 4", 4, ns.secondLargestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 4", 4, ns.secondSmallestValueMap.size()); //$NON-NLS-1$
        ns.init();
        Assert.assertEquals("longestValueMap size should be 0", 0, ns.largestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 0", 0, ns.smallestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 0", 0, ns.secondLargestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 0", 0, ns.secondSmallestValueMap.size()); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.survivorship.services.NumberService#putAttributeValues(java.lang.String)}.
     */
    @Test
    public void testPutAttributeValues() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        NumberService ns = new NumberService(dataSet);
        ns.putAttributeValues("id1"); //$NON-NLS-1$
        ns.putAttributeValues("id2"); //$NON-NLS-1$
        ns.putAttributeValues("id3"); //$NON-NLS-1$
        ns.putAttributeValues("id4"); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 4", 4, ns.largestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 4", 4, ns.smallestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 4", 4, ns.secondLargestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 4", 4, ns.secondSmallestValueMap.size()); //$NON-NLS-1$

        Assert.assertEquals("The result of firstName column should be 6", 6, ns.largestValueMap.get("id1")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 1", 1, ns.largestValueMap.get("id2")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 6", 6, ns.largestValueMap.get("id3")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 4", 4, ns.largestValueMap.get("id4")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 5", 5, ns.secondLargestValueMap.get("id1")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 1", 1, ns.secondLargestValueMap.get("id2")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 5", 5, ns.secondLargestValueMap.get("id3")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 3", 3, ns.secondLargestValueMap.get("id4")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 1", 1, ns.smallestValueMap.get("id1")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 1", 1, ns.smallestValueMap.get("id2")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 1", 1, ns.smallestValueMap.get("id3")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 3", 3, ns.smallestValueMap.get("id4")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 2", 2, ns.secondSmallestValueMap.get("id1")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 1", 1, ns.secondSmallestValueMap.get("id2")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 2", 2, ns.secondSmallestValueMap.get("id3")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 4", 4, ns.secondSmallestValueMap.get("id4")); //$NON-NLS-1$ //$NON-NLS-2$

        ns.init();
    }

    /**
     * Test method for {@link org.talend.survivorship.services.NumberService#isLargestValue(java.lang.Object, java.lang.String)}.
     */
    @Test
    public void testIsLargestValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        NumberService ns = new NumberService(dataSet);
        ns.putAttributeValues("id1"); //$NON-NLS-1$
        ns.putAttributeValues("id2"); //$NON-NLS-1$
        ns.putAttributeValues("id3"); //$NON-NLS-1$
        ns.putAttributeValues("id4"); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 4", 4, ns.largestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 4", 4, ns.smallestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 4", 4, ns.secondLargestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 4", 4, ns.secondSmallestValueMap.size()); //$NON-NLS-1$

        Assert.assertEquals("The result of firstName column should be 6", 6, ns.largestValueMap.get("id1")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 1", 1, ns.largestValueMap.get("id2")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 6", 6, ns.largestValueMap.get("id3")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 4", 4, ns.largestValueMap.get("id4")); //$NON-NLS-1$ //$NON-NLS-2$

        ns.init();
    }

    /**
     * Test method for {@link org.talend.survivorship.services.NumberService#isSmallestValue(java.lang.Object, java.lang.String)}.
     */
    @Test
    public void testIsSmallestValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        NumberService ns = new NumberService(dataSet);
        ns.putAttributeValues("id1"); //$NON-NLS-1$
        ns.putAttributeValues("id2"); //$NON-NLS-1$
        ns.putAttributeValues("id3"); //$NON-NLS-1$
        ns.putAttributeValues("id4"); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 4", 4, ns.largestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 4", 4, ns.smallestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 4", 4, ns.secondLargestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 4", 4, ns.secondSmallestValueMap.size()); //$NON-NLS-1$

        Assert.assertEquals("The result of firstName column should be 1", 1, ns.smallestValueMap.get("id1")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 1", 1, ns.smallestValueMap.get("id2")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 1", 1, ns.smallestValueMap.get("id3")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 3", 3, ns.smallestValueMap.get("id4")); //$NON-NLS-1$ //$NON-NLS-2$

        ns.init();
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.services.NumberService#isSecondLargestValue(java.lang.Object, java.lang.String)}.
     */
    @Test
    public void testIsSecondLargestValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        NumberService ns = new NumberService(dataSet);
        ns.putAttributeValues("id1"); //$NON-NLS-1$
        ns.putAttributeValues("id2"); //$NON-NLS-1$
        ns.putAttributeValues("id3"); //$NON-NLS-1$
        ns.putAttributeValues("id4"); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 4", 4, ns.largestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 4", 4, ns.smallestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 4", 4, ns.secondLargestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 4", 4, ns.secondSmallestValueMap.size()); //$NON-NLS-1$

        Assert.assertEquals("The result of firstName column should be 5", 5, ns.secondLargestValueMap.get("id1")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 1", 1, ns.secondLargestValueMap.get("id2")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 5", 5, ns.secondLargestValueMap.get("id3")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 3", 3, ns.secondLargestValueMap.get("id4")); //$NON-NLS-1$ //$NON-NLS-2$

        ns.init();
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.services.NumberService#isSecondSmallestValue(java.lang.Object, java.lang.String)}.
     */
    @Test
    public void testIsSecondSmallestValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        NumberService ns = new NumberService(dataSet);
        ns.putAttributeValues("id1"); //$NON-NLS-1$
        ns.putAttributeValues("id2"); //$NON-NLS-1$
        ns.putAttributeValues("id3"); //$NON-NLS-1$
        ns.putAttributeValues("id4"); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 4", 4, ns.largestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 4", 4, ns.smallestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 4", 4, ns.secondLargestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 4", 4, ns.secondSmallestValueMap.size()); //$NON-NLS-1$

        Assert.assertEquals("The result of firstName column should be 2", 2, ns.secondSmallestValueMap.get("id1")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 1", 1, ns.secondSmallestValueMap.get("id2")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 2", 2, ns.secondSmallestValueMap.get("id3")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of firstName column should be 4", 4, ns.secondSmallestValueMap.get("id4")); //$NON-NLS-1$ //$NON-NLS-2$

        ns.init();
    }

    /**
     * DOC zshen Comment method "generateColumnList".
     * 
     * @return
     */
    private List<Column> generateColumnList() {
        List<Column> columnList = new ArrayList<>();
        // thrid input data insert after min
        Column col1 = new Column("id1", "Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnList.add(col1);
        // only same one input data
        col1 = new Column("id2", "Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnList.add(col1);
        // thrid input data insert before max
        col1 = new Column("id3", "Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnList.add(col1);
        // thrid input data same with max or min
        col1 = new Column("id4", "Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnList.add(col1);
        return columnList;
    }

    /**
     * Create by zshen generate input data
     * 
     * @return array of input data
     */
    private Object[][] generateInputData() {

        return new Object[][] { { 5, 1, 3, 3 }, { 3, 1, 5, 4 }, { 1, 1, 4, 4 }, { 2, 1, 1, 3 }, { 4, 1, 6, 3 }, { 6, 1, 2, 4 } };
    }

}
