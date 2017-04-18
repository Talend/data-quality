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

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.talend.survivorship.model.DataSet;

/**
 * create by zshen test for Expression Action
 */
public class ExpressionActionTest {

    /**
     * Test method for
     * {@link org.talend.survivorship.action.ExpressionAction#checkCanHandle(org.talend.survivorship.model.DataSet, java.lang.Object, java.lang.String, java.lang.String, int, boolean)}
     * .
     * 
     * Case1 input data is number
     */
    @Test
    public void testCheckCanHandleWithNumber() {
        DataSet dataset = null;
        Object inputData = 10;
        int rowNum = 0;
        String column = null;
        String ruleName = null;
        String expression = ">1"; //$NON-NLS-1$
        boolean ignoreBlanks = false;
        ActionParameter actionParaneter = new ActionParameter(dataset, inputData, rowNum, column, ruleName, expression,
                ignoreBlanks);

        ExpressionAction expressionAction = new ExpressionAction();
        boolean checkCanHandle = expressionAction.checkCanHandle(actionParaneter);
        Assert.assertEquals("The result of checkCanHandle shuold be true", true, checkCanHandle); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.action.ExpressionAction#checkCanHandle(org.talend.survivorship.model.DataSet, java.lang.Object, java.lang.String, java.lang.String, int, boolean)}
     * .
     * Case2 input data is Date
     */
    @Test
    public void testCheckCanHandleWithDate() {
        DataSet dataset = null;
        Object inputData = new Date();
        int rowNum = 0;
        String column = null;
        String ruleName = null;
        String expression = ".getTime()<new Date().getTime()"; //$NON-NLS-1$
        boolean ignoreBlanks = false;
        ActionParameter actionParaneter = new ActionParameter(dataset, inputData, rowNum, column, ruleName, expression,
                ignoreBlanks);

        ExpressionAction expressionAction = new ExpressionAction();
        boolean checkCanHandle = expressionAction.checkCanHandle(actionParaneter);
        Assert.assertEquals("The result of checkCanHandle shuold be true", true, checkCanHandle); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.action.ExpressionAction#checkCanHandle(org.talend.survivorship.model.DataSet, java.lang.Object, java.lang.String, java.lang.String, int, boolean)}
     * .
     * Case3 input data is String
     */
    @Test
    public void testCheckCanHandleWithString() {
        DataSet dataset = null;
        Object inputData = "string1"; //$NON-NLS-1$
        int rowNum = 0;
        String column = null;
        String ruleName = null;
        String expression = ".equals(\"string1\")"; //$NON-NLS-1$
        boolean ignoreBlanks = false;
        ActionParameter actionParaneter = new ActionParameter(dataset, inputData, rowNum, column, ruleName, expression,
                ignoreBlanks);

        ExpressionAction expressionAction = new ExpressionAction();
        boolean checkCanHandle = expressionAction.checkCanHandle(actionParaneter);
        Assert.assertEquals("The result of checkCanHandle shuold be true", true, checkCanHandle); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.action.ExpressionAction#checkCanHandle(org.talend.survivorship.model.DataSet, java.lang.Object, java.lang.String, java.lang.String, int, boolean)}
     * .
     * Case4 input data is null exception generate
     */
    @Test
    public void testCheckCanHandleInputDataNull() {
        DataSet dataset = null;
        Object inputData = null;
        int rowNum = 0;
        String column = null;
        String ruleName = null;
        String expression = ".equals(\"string1\")"; //$NON-NLS-1$
        boolean ignoreBlanks = false;
        ActionParameter actionParaneter = new ActionParameter(dataset, inputData, rowNum, column, ruleName, expression,
                ignoreBlanks);

        ExpressionAction expressionAction = new ExpressionAction();
        boolean checkCanHandle = expressionAction.checkCanHandle(actionParaneter);
        Assert.assertEquals("The result of checkCanHandle shuold be false because of exception", false, checkCanHandle); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.survivorship.action.ExpressionAction#checkCanHandle(org.talend.survivorship.model.DataSet, java.lang.Object, java.lang.String, java.lang.String, int, boolean)}
     * .
     * Case5 expression is null result is false
     */
    @Test
    public void testCheckCanHandleExpressionNull() {
        DataSet dataset = null;
        Object inputData = null;
        int rowNum = 0;
        String column = null;
        String ruleName = null;
        String expression = ".equals(\"string1\")"; //$NON-NLS-1$
        boolean ignoreBlanks = false;
        ActionParameter actionParaneter = new ActionParameter(dataset, inputData, rowNum, column, ruleName, expression,
                ignoreBlanks);

        ExpressionAction expressionAction = new ExpressionAction();
        boolean checkCanHandle = expressionAction.checkCanHandle(actionParaneter);
        Assert.assertEquals("The result of checkCanHandle shuold be false because of expression is null", false, checkCanHandle); //$NON-NLS-1$
    }

}
