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
import org.talend.survivorship.sample.SampleData;

/**
 * Create by zshen test for Time Service
 */
public class TimeServiceTest {

    /**
     * Test method for {@link org.talend.survivorship.services.TimeService#init()}.
     */
    @Test
    public void testInit() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        TimeService ts = new TimeService(dataSet);
        ts.putAttributeValues("date1"); //$NON-NLS-1$
        ts.putAttributeValues("date2"); //$NON-NLS-1$
        ts.putAttributeValues("date3"); //$NON-NLS-1$
        ts.putAttributeValues("date4"); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 4", 4, ts.earliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 4", 4, ts.latestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 4", 4, ts.secondEarliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 4", 4, ts.secondLatestValueMap.size()); //$NON-NLS-1$
        ts.init();
        Assert.assertEquals("longestValueMap size should be 0", 0, ts.earliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 0", 0, ts.latestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 0", 0, ts.secondEarliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 0", 0, ts.secondLatestValueMap.size()); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.survivorship.services.TimeService#putAttributeValues(java.lang.String)}.
     */
    @Test
    public void testPutAttributeValues() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        TimeService ts = new TimeService(dataSet);
        ts.putAttributeValues("date1"); //$NON-NLS-1$
        ts.putAttributeValues("date2"); //$NON-NLS-1$
        ts.putAttributeValues("date3"); //$NON-NLS-1$
        ts.putAttributeValues("date4"); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 4", 4, ts.earliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 4", 4, ts.latestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 4", 4, ts.secondEarliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 4", 4, ts.secondLatestValueMap.size()); //$NON-NLS-1$

        Assert.assertEquals("The result of firstName column should be 2000-06-06", "2000-06-06", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.latestValueMap.get("date1"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-01-01", "2000-01-01", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.latestValueMap.get("date2"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-06-06", "2000-06-06", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.latestValueMap.get("date3"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-04-04", "2000-04-04", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.latestValueMap.get("date4"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-05-05", "2000-05-05", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondLatestValueMap.get("date1"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-01-01", "2000-01-01", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondLatestValueMap.get("date2"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-05-05", "2000-05-05", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondLatestValueMap.get("date3"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-03-03", "2000-03-03", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondLatestValueMap.get("date4"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-01-01", "2000-01-01", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.earliestValueMap.get("date1"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-01-01", "2000-01-01", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.earliestValueMap.get("date2"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-01-01", "2000-01-01", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.earliestValueMap.get("date3"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-03-03", "2000-03-03", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.earliestValueMap.get("date4"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-02-02", "2000-02-02", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondEarliestValueMap.get("date1"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-01-01", "2000-01-01", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondEarliestValueMap.get("date2"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-02-02", "2000-02-02", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondEarliestValueMap.get("date3"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-04-04", "2000-04-04", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondEarliestValueMap.get("date4"), "yyyy-MM-dd"));

        ts.init();
    }

    /**
     * Test method for {@link org.talend.survivorship.services.TimeService#isLatestValue(java.lang.Object, java.lang.String)}.
     */
    @Test
    public void testIsLatestValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        TimeService ts = new TimeService(dataSet);
        ts.putAttributeValues("date1"); //$NON-NLS-1$
        ts.putAttributeValues("date2"); //$NON-NLS-1$
        ts.putAttributeValues("date3"); //$NON-NLS-1$
        ts.putAttributeValues("date4"); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 4", 4, ts.earliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 4", 4, ts.latestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 4", 4, ts.secondEarliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 4", 4, ts.secondLatestValueMap.size()); //$NON-NLS-1$

        Assert.assertEquals("The result of firstName column should be 2000-06-06", "2000-06-06", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.latestValueMap.get("date1"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-01-01", "2000-01-01", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.latestValueMap.get("date2"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-06-06", "2000-06-06", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.latestValueMap.get("date3"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-04-04", "2000-04-04", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.latestValueMap.get("date4"), "yyyy-MM-dd"));

        ts.init();
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.services.TimeService#isSecondEarliestValue(java.lang.Object, java.lang.String)}.
     */
    @Test
    public void testIsSecondEarliestValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        TimeService ts = new TimeService(dataSet);
        ts.putAttributeValues("date1"); //$NON-NLS-1$
        ts.putAttributeValues("date2"); //$NON-NLS-1$
        ts.putAttributeValues("date3"); //$NON-NLS-1$
        ts.putAttributeValues("date4"); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 4", 4, ts.earliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 4", 4, ts.latestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 4", 4, ts.secondEarliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 4", 4, ts.secondLatestValueMap.size()); //$NON-NLS-1$

        Assert.assertEquals("The result of firstName column should be 2000-02-02", "2000-02-02", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondEarliestValueMap.get("date1"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-01-01", "2000-01-01", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondEarliestValueMap.get("date2"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-02-02", "2000-02-02", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondEarliestValueMap.get("date3"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-04-04", "2000-04-04", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondEarliestValueMap.get("date4"), "yyyy-MM-dd"));

        ts.init();
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.services.TimeService#isSecondLatestValue(java.lang.Object, java.lang.String)}.
     */
    @Test
    public void testIsSecondLatestValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        TimeService ts = new TimeService(dataSet);
        ts.putAttributeValues("date1"); //$NON-NLS-1$
        ts.putAttributeValues("date2"); //$NON-NLS-1$
        ts.putAttributeValues("date3"); //$NON-NLS-1$
        ts.putAttributeValues("date4"); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 4", 4, ts.earliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 4", 4, ts.latestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 4", 4, ts.secondEarliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 4", 4, ts.secondLatestValueMap.size()); //$NON-NLS-1$

        Assert.assertEquals("The result of firstName column should be 2000-05-05", "2000-05-05", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondLatestValueMap.get("date1"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-01-01", "2000-01-01", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondLatestValueMap.get("date2"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-05-05", "2000-05-05", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondLatestValueMap.get("date3"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-03-03", "2000-03-03", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.secondLatestValueMap.get("date4"), "yyyy-MM-dd"));

        ts.init();
    }

    /**
     * Test method for {@link org.talend.survivorship.services.TimeService#isEarliestValue(java.lang.Object, java.lang.String)}.
     */
    @Test
    public void testIsEarliestValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        TimeService ts = new TimeService(dataSet);
        ts.putAttributeValues("date1"); //$NON-NLS-1$
        ts.putAttributeValues("date2"); //$NON-NLS-1$
        ts.putAttributeValues("date3"); //$NON-NLS-1$
        ts.putAttributeValues("date4"); //$NON-NLS-1$
        Assert.assertEquals("longestValueMap size should be 4", 4, ts.earliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondLongestValueMap size should be 4", 4, ts.latestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("secondShortestValueMap size should be 4", 4, ts.secondEarliestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("shortestValueMap size should be 4", 4, ts.secondLatestValueMap.size()); //$NON-NLS-1$
        Assert.assertEquals("The result of firstName column should be 2000-01-01", "2000-01-01", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.earliestValueMap.get("date1"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-01-01", "2000-01-01", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.earliestValueMap.get("date2"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-01-01", "2000-01-01", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.earliestValueMap.get("date3"), "yyyy-MM-dd"));
        Assert.assertEquals("The result of firstName column should be 2000-03-03", "2000-03-03", //$NON-NLS-1$//$NON-NLS-2$
                SampleData.dateToString(ts.earliestValueMap.get("date4"), "yyyy-MM-dd"));

        ts.init();
    }

    /**
     * Create by zshen generate column list
     * 
     * @return a list of columns
     */
    private List<Column> generateColumnList() {
        List<Column> columnList = new ArrayList<>();
        // thrid input data insert after min
        Column col1 = new Column("date1", "Date"); //$NON-NLS-1$ //$NON-NLS-2$
        columnList.add(col1);
        // only same one input data
        col1 = new Column("date2", "Date"); //$NON-NLS-1$ //$NON-NLS-2$
        columnList.add(col1);
        // thrid input data insert before max
        col1 = new Column("date3", "Date"); //$NON-NLS-1$ //$NON-NLS-2$
        columnList.add(col1);
        // thrid input data same with max or min
        col1 = new Column("date4", "Date"); //$NON-NLS-1$ //$NON-NLS-2$
        columnList.add(col1);
        return columnList;
    }

    /**
     * Create by zshen generate input data
     * 
     * @return array of input data
     */
    private Object[][] generateInputData() {

        return new Object[][] {
                { SampleData.stringToDate("2000-05-05", "yyyy-MM-dd"), SampleData.stringToDate("2000-01-01", "yyyy-MM-dd"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        SampleData.stringToDate("2000-03-03", "yyyy-MM-dd"), //$NON-NLS-1$//$NON-NLS-2$
                        SampleData.stringToDate("2000-03-03", "yyyy-MM-dd") }, //$NON-NLS-1$ //$NON-NLS-2$
                { SampleData.stringToDate("2000-03-03", "yyyy-MM-dd"), SampleData.stringToDate("2000-01-01", "yyyy-MM-dd"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        SampleData.stringToDate("2000-05-05", "yyyy-MM-dd"), //$NON-NLS-1$//$NON-NLS-2$
                        SampleData.stringToDate("2000-04-04", "yyyy-MM-dd") }, //$NON-NLS-1$ //$NON-NLS-2$
                { SampleData.stringToDate("2000-01-01", "yyyy-MM-dd"), SampleData.stringToDate("2000-01-01", "yyyy-MM-dd"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        SampleData.stringToDate("2000-04-04", "yyyy-MM-dd"), //$NON-NLS-1$//$NON-NLS-2$
                        SampleData.stringToDate("2000-04-04", "yyyy-MM-dd") }, //$NON-NLS-1$ //$NON-NLS-2$
                { SampleData.stringToDate("2000-02-02", "yyyy-MM-dd"), SampleData.stringToDate("2000-01-01", "yyyy-MM-dd"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        SampleData.stringToDate("2000-01-01", "yyyy-MM-dd"), //$NON-NLS-1$//$NON-NLS-2$
                        SampleData.stringToDate("2000-03-03", "yyyy-MM-dd") }, //$NON-NLS-1$ //$NON-NLS-2$
                { SampleData.stringToDate("2000-04-04", "yyyy-MM-dd"), SampleData.stringToDate("2000-01-01", "yyyy-MM-dd"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        SampleData.stringToDate("2000-06-06", "yyyy-MM-dd"), //$NON-NLS-1$//$NON-NLS-2$
                        SampleData.stringToDate("2000-03-03", "yyyy-MM-dd") }, //$NON-NLS-1$ //$NON-NLS-2$
                { SampleData.stringToDate("2000-06-06", "yyyy-MM-dd"), SampleData.stringToDate("2000-01-01", "yyyy-MM-dd"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        SampleData.stringToDate("2000-02-02", "yyyy-MM-dd"), //$NON-NLS-1$//$NON-NLS-2$
                        SampleData.stringToDate("2000-04-04", "yyyy-MM-dd") } }; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
