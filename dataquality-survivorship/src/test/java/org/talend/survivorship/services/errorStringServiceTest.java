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
 * 
 * Add @Test before testPutAttributeValues() firstly
 * 
 * 1.Try to execute whole the junit you will find out
 * testPutAttributeValues will failed.But that is not a main issue.
 * 
 * 2.Add parameter in the launch configation filter only testPutAttributeValues() execute.
 * then the test case will passed.
 * 
 * Fouce on the output console
 * When the test case failed first input data is "Eric"
 * When test case sucessful first input data is others.
 * 
 * 
 */
public class errorStringServiceTest {

    /**
     * Test method for {@link org.talend.survivorship.services.StringService#putAttributeValues(java.lang.String, boolean)}.
     */
    @Test
    public void testPutAttributeValues() {
        List<Column> columnList = generateColumnList();
        DataSet dataSet = new DataSet(columnList);
        dataSet.initData(generateInputData());
        ErrorStringService ss = new ErrorStringService(dataSet);
        ss.putAttributeValues("firstName", false); //$NON-NLS-1$

        Assert.assertEquals("The second ShortestValueMap of firstName column should be 3", 3, //$NON-NLS-1$
                ss.secondShortestValueMap.get("firstName") //$NON-NLS-1$
                        .size());

    }

    /**
     * DOC zshen Comment method "generateColumnList".
     * 
     * @return
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
     * DOC zshen Comment method "generateInputData".
     * 
     * @return
     */
    private Object[][] generateInputData() {

        return new Object[][] { { "Ashley", "cook" }, { "Brianna", "bell" }, { "Chloe", "cook" }, { "David", "bell" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                { "Eric", "cook" }, { "Faith", "adam" } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

}
