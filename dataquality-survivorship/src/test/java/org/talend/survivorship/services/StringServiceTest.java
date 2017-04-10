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
 * Create by zshen test for String Service
 */
public class StringServiceTest {

    /**
     * Test method for {@link org.talend.survivorship.services.StringService#putAttributeValues(java.lang.String, boolean)}.
     */
    @Test
    public void testPutAttributeValues() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        StringService ss = new StringService(dataSet);
        ss.putAttributeValues("firstName", false); //$NON-NLS-1$
        ss.putAttributeValues("lastName", false); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 2", 2, ss.longestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 2", 2, ss.secondLongestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 2", 2, ss.secondShortestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 2", 2, ss.shortestValueMap.size()); //$NON-NLS-1$

        Assert.assertEquals("The result of firstName column should be 1", 1, ss.longestValueMap.get("firstName").size()); //$NON-NLS-1$ //$NON-NLS-2$
        // there is not 6 because of some data is duplicated
        Assert.assertEquals("The result of lastName column should be 3", 3, ss.longestValueMap.get("lastName").size()); //$NON-NLS-1$ //$NON-NLS-2$

        Assert.assertEquals("The second LongestValueMap of firstName column should be 1", 1, //$NON-NLS-1$
                ss.secondLongestValueMap.get("firstName").size()); //$NON-NLS-1$
        // because all of input data keep same length so that secondLongestValueMap is empty all of result keep in longestValueMap
        Assert.assertEquals("The second LongestValueMap of lastName column should be 0", 0, //$NON-NLS-1$
                ss.secondLongestValueMap.get("lastName").size()); //$NON-NLS-1$

        Assert.assertEquals("The second ShortestValueMap of firstName column should be 3", 3, //$NON-NLS-1$
                ss.secondShortestValueMap.get("firstName") //$NON-NLS-1$
                        .size());
        Assert.assertEquals("The second ShortestValueMap of lastName column should be 0", 0, //$NON-NLS-1$
                ss.secondShortestValueMap.get("lastName") //$NON-NLS-1$
                        .size());

        Assert.assertEquals("The shortestValueMap of firstName column should be 1", 1, ss.shortestValueMap.get("firstName") //$NON-NLS-1$ //$NON-NLS-2$
                .size());
        Assert.assertEquals("The shortestValueMap of lastName column should be 3", 3, ss.shortestValueMap.get("lastName") //$NON-NLS-1$ //$NON-NLS-2$
                .size());

        ss.init();
    }

    /**
     * Test method for {@link org.talend.survivorship.services.StringService#init()}.
     */
    @Test
    public void testInit() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        StringService ss = new StringService(dataSet);
        ss.putAttributeValues("firstName", false); //$NON-NLS-1$
        ss.putAttributeValues("lastName", false); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 2", 2, ss.longestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 2", 2, ss.secondLongestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 2", 2, ss.secondShortestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 2", 2, ss.shortestValueMap.size()); //$NON-NLS-1$
        ss.init();
        Assert.assertEquals("longestValueMap size should be 0", 0, ss.longestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 0", 0, ss.secondLongestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 0", 0, ss.secondShortestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 0", 0, ss.shortestValueMap.size()); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.services.StringService#isLongestValue(java.lang.Object, java.lang.String, boolean)}.
     */
    @Test
    public void testIsLongestValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        StringService ss = new StringService(dataSet);
        ss.putAttributeValues("firstName", false); //$NON-NLS-1$
        ss.putAttributeValues("lastName", false); //$NON-NLS-1$
        Assert.assertEquals("Brianna should be longest value on the column firstName", true, //$NON-NLS-1$
                ss.isLongestValue("Brianna", "firstName", false)); //$NON-NLS-1$
        Assert.assertEquals("cook should be longest value on the column lastName", true, //$NON-NLS-1$
                ss.isLongestValue("cook", "lastName", false)); //$NON-NLS-1$
        Assert.assertEquals("1111 should not be longest value on the column lastName", false, //$NON-NLS-1$
                ss.isLongestValue("1111", "lastName", false)); //$NON-NLS-1$
        ss.init();
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.services.StringService#isSecondLongestValue(java.lang.Object, java.lang.String, boolean)}.
     */
    @Test
    public void testIsSecondLongestValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        StringService ss = new StringService(dataSet);
        ss.putAttributeValues("firstName", false); //$NON-NLS-1$
        ss.putAttributeValues("lastName", false); //$NON-NLS-1$
        Assert.assertEquals("Ashley should be second longtest value on the column firstName", true, //$NON-NLS-1$
                ss.isSecondLongestValue("Ashley", "firstName", false)); //$NON-NLS-1$
        Assert.assertEquals("cook should be second longtest value on the column lastName", true, //$NON-NLS-1$
                ss.isSecondLongestValue("cook", "lastName", false)); //$NON-NLS-1$
        Assert.assertEquals("1111 should not be second longtest value on the column lastName", false, //$NON-NLS-1$
                ss.isSecondLongestValue("1111", "lastName", false)); //$NON-NLS-1$
        ss.init();
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.services.StringService#isShortestValue(java.lang.Object, java.lang.String, boolean)}.
     */
    @Test
    public void testIsShortestValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        StringService ss = new StringService(dataSet);
        ss.putAttributeValues("firstName", false); //$NON-NLS-1$
        ss.putAttributeValues("lastName", false); //$NON-NLS-1$
        Assert.assertEquals("Eric should be shortest value on the column firstName", true, //$NON-NLS-1$
                ss.isShortestValue("Eric", "firstName", false)); //$NON-NLS-1$
        Assert.assertEquals("cook should be shortest value on the column lastName", true, //$NON-NLS-1$
                ss.isShortestValue("cook", "lastName", false)); //$NON-NLS-1$
        Assert.assertEquals("1111 should not be shortest value on the column lastName", false, //$NON-NLS-1$
                ss.isShortestValue("1111", "lastName", false)); //$NON-NLS-1$
        ss.init();
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.services.StringService#isSecondShortestValue(java.lang.Object, java.lang.String, boolean)}.
     */
    @Test
    public void testIsSecondShortestValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        StringService ss = new StringService(dataSet);
        ss.putAttributeValues("firstName", false); //$NON-NLS-1$
        ss.putAttributeValues("lastName", false); //$NON-NLS-1$
        Assert.assertEquals("Faith should be second shortest value on the column firstName", true, //$NON-NLS-1$
                ss.isSecondShortestValue("Faith", "firstName", false)); //$NON-NLS-1$
        Assert.assertEquals("cook should be second shortest value on the column lastName", true, //$NON-NLS-1$
                ss.isSecondShortestValue("cook", "lastName", false)); //$NON-NLS-1$
        Assert.assertEquals("1111 should not be second shortest value on the column lastName", false, //$NON-NLS-1$
                ss.isSecondShortestValue("1111", "lastName", false)); //$NON-NLS-1$
        ss.init();
    }

    /**
     * create by zshen generate column list
     * 
     * @return list of columns
     */
    private List<Column> generateColumnList() {
        List<Column> columnList = new ArrayList<>();
        Column col1 = new Column("firstName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnList.add(col1);
        col1 = new Column("lastName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnList.add(col1);
        return columnList;
    }

    /**
     * Create by zshen generate input data
     * 
     * @return arrays of input data
     */
    private Object[][] generateInputData() {

        return new Object[][] { { "Ashley", "cook" }, { "Brianna", "bell" }, { "Chloe", "cook" }, { "David", "bell" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                { "Eric", "cook" }, { "Faith", "adam" } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

}
