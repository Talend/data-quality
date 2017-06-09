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
package org.talend.survivorship.action;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.talend.survivorship.model.Attribute;
import org.talend.survivorship.model.Column;
import org.talend.survivorship.model.DataSet;
import org.talend.survivorship.model.Record;

/**
 * Create by zshen Test for SurvivedValuePassAction
 */
public class SurviveAsActionTest {

    /**
     * Test method for
     * {@link org.talend.survivorship.action.SurviveAsAction#canHandle(org.talend.survivorship.action.ActionParameter)}
     * .
     */
    @Test
    public void testCheckCanHandle() {

        Column col1 = new Column("city1", "String");
        Column col2 = new Column("city2", "String");
        Column col3 = new Column("city3", "String");
        Column col4 = new Column("city4", "String");
        List<Column> columns = new ArrayList<>();
        columns.add(col1);
        columns.add(col2);
        columns.add(col3);
        columns.add(col4);
        DataSet dataset = new DataSet(columns);
        Record record1 = new Record();
        record1.setId(1);
        Attribute att1 = new Attribute(record1, col1, "value1");
        Attribute att2 = new Attribute(record1, col2, null);
        Attribute att3 = new Attribute(record1, col2, "value33");
        Attribute att4 = new Attribute(record1, col2, "value4");
        record1.putAttribute("city1", att1);
        record1.putAttribute("city2", att2);
        record1.putAttribute("city3", att3);
        record1.putAttribute("city4", att4);
        col1.putAttribute(record1, att1);
        col2.putAttribute(record1, att2);
        col3.putAttribute(record1, att3);
        col4.putAttribute(record1, att4);
        Record record2 = new Record();
        record2.setId(2);
        att1 = new Attribute(record2, col1, "value11");
        att2 = new Attribute(record2, col2, "value2");
        att3 = new Attribute(record2, col2, "value3");
        att4 = new Attribute(record2, col2, "value4");
        record2.putAttribute("city1", att1);
        record2.putAttribute("city2", att2);
        record2.putAttribute("city3", att3);
        record2.putAttribute("city4", att4);
        col1.putAttribute(record2, att1);
        col2.putAttribute(record2, att2);
        col3.putAttribute(record2, att3);
        col4.putAttribute(record2, att4);
        dataset.getRecordList().add(record1);
        dataset.getRecordList().add(record2);
        dataset.getSurvivorIndexMap().put("city1", 1);
        dataset.getSurvivorIndexMap().put("city2", 0);
        dataset.getSurvivorIndexMap().put("city3", 1);
        dataset.getSurvivorIndexMap().put("city4", 1);
        Object inputData = null;
        int rowNum = 0;
        String column = "city2";
        String ruleName = "rule1";
        String expression = "Tony,Green";
        boolean ignoreBlanks = false;
        ActionParameter actionParameter = new ActionParameter(dataset, inputData, rowNum, column, ruleName, expression,
                ignoreBlanks);
        SurviveAsAction survivedValuePassAction = new SurviveAsAction();
        boolean checkCanHandle = survivedValuePassAction.canHandle(actionParameter);
        Assert.assertTrue("intput data is same with survived value both null so that result should be true", checkCanHandle);

        inputData = 0;
        actionParameter = new ActionParameter(dataset, inputData, rowNum, column, ruleName, expression, ignoreBlanks);
        checkCanHandle = survivedValuePassAction.canHandle(actionParameter);
        Assert.assertFalse("intput data is not same with survived value so that result should be false", checkCanHandle);
    }

}
