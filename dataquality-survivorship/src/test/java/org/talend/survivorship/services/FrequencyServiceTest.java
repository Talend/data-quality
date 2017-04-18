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
 * Create by zshen Frequency service test
 */
public class FrequencyServiceTest {

    /**
     * Test method for {@link org.talend.survivorship.services.FrequencyService#init()}.
     */
    @Test
    public void testInit() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        FrequencyService fs = new FrequencyService(dataSet);
        fs.putAttributeValues("firstName", false); //$NON-NLS-1$
        fs.putAttributeValues("lastName", false); //$NON-NLS-1$
        Assert.assertEquals("frequencyMaps size should be 2", 2, fs.frequencyMaps.size()); //$NON-NLS-1$
        Assert.assertEquals("maxOccurence size should be 2", 2, fs.maxOccurence.size()); //$NON-NLS-1$
        Assert.assertEquals("secondMaxOccurence size should be 2", 2, fs.secondMaxOccurence.size()); //$NON-NLS-1$
        fs.init();
        Assert.assertEquals("frequencyMaps size should be 0", 0, fs.frequencyMaps.size()); //$NON-NLS-1$
        Assert.assertEquals("maxOccurence size should be 0", 0, fs.maxOccurence.size()); //$NON-NLS-1$
        Assert.assertEquals("secondMaxOccurence size should be 0", 0, fs.secondMaxOccurence.size()); //$NON-NLS-1$
    }

    /**
     * Create by zshen generate input data
     * 
     * @return arrays of input data
     */
    private Object[][] generateInputData() {

        return new Object[][] { { "Ashley", "cook" }, { "Brianna", "bell" }, { "Chloe", "cook" }, { "David", "bell" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                { "Eric", "cook" }, { "Faith ", "adams" }, }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    /**
     * create by zshen generate column list
     * 
     * @return a list of columns
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
     * Test method for {@link org.talend.survivorship.services.FrequencyService#putAttributeValues(java.lang.String, boolean)}.
     */
    @Test
    public void testPutAttributeValues() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        FrequencyService fs = new FrequencyService(dataSet);
        fs.putAttributeValues("firstName", false); //$NON-NLS-1$
        fs.putAttributeValues("lastName", false); //$NON-NLS-1$
        Assert.assertEquals("frequencyMaps size should be 2", 2, fs.frequencyMaps.size()); //$NON-NLS-1$
        Assert.assertEquals("maxOccurence size should be 2", 2, fs.maxOccurence.size()); //$NON-NLS-1$
        Assert.assertEquals("secondMaxOccurence size should be 2", 2, fs.secondMaxOccurence.size()); //$NON-NLS-1$
        Assert.assertEquals("The result of firstName column should be 6", 6, fs.frequencyMaps.get("firstName").size()); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The result of lastName column should be 3", 3, fs.frequencyMaps.get("lastName").size()); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The maxOccurence of firstName column should be 1", 1, fs.maxOccurence.get("firstName").intValue()); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The maxOccurence of lastName column should be 3", 3, fs.maxOccurence.get("lastName").intValue()); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("The secondMaxOccurence of firstName column should be 0", 0, fs.secondMaxOccurence.get("firstName") //$NON-NLS-1$ //$NON-NLS-2$
                .intValue());
        Assert.assertEquals("The secondMaxOccurence of lastName column should be 2", 2, fs.secondMaxOccurence.get("lastName") //$NON-NLS-1$ //$NON-NLS-2$
                .intValue());
        fs.init();
    }

    /**
     * Test method for {@link org.talend.survivorship.services.FrequencyService#getMostCommonValue(java.lang.String, boolean)}.
     */
    @Test
    public void testGetMostCommonValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        FrequencyService fs = new FrequencyService(dataSet);
        fs.putAttributeValues("firstName", false); //$NON-NLS-1$
        fs.putAttributeValues("lastName", false); //$NON-NLS-1$
        Assert.assertEquals("the size of most common value on firstName column should be 6", 6, //$NON-NLS-1$
                fs.getMostCommonValue("firstName", false).size()); //$NON-NLS-1$
        Assert.assertEquals("the size of most common value on lastName column should be 1", 1, //$NON-NLS-1$
                fs.getMostCommonValue("lastName", false).size()); //$NON-NLS-1$
        Assert.assertEquals("most common value on lastName column should be cook", "cook", //$NON-NLS-1$//$NON-NLS-2$
                fs.getMostCommonValue("lastName", false).toArray()[0].toString()); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.services.FrequencyService#getSecondMostCommonValue(java.lang.String, boolean)}.
     */
    @Test
    public void testGetSecondMostCommonValue() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        FrequencyService fs = new FrequencyService(dataSet);
        fs.putAttributeValues("firstName", false); //$NON-NLS-1$
        fs.putAttributeValues("lastName", false); //$NON-NLS-1$
        Assert.assertEquals("the size of most common value on firstName column should be 0", 0, //$NON-NLS-1$
                fs.getSecondMostCommonValue("firstName", false).size()); //$NON-NLS-1$
        Assert.assertEquals("the size of most common value on lastName column should be 1", 1, //$NON-NLS-1$
                fs.getSecondMostCommonValue("lastName", false).size()); //$NON-NLS-1$
        Assert.assertEquals("most common value on lastName column should be bell", "bell", //$NON-NLS-1$//$NON-NLS-2$
                fs.getSecondMostCommonValue("lastName", false).toArray()[0].toString()); //$NON-NLS-1$
    }

}
