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

import org.junit.Assert;
import org.junit.Test;
import org.talend.survivorship.model.DataSet;

/**
 * Create by zshen test for
 */
public class SmallestActionTest {

    /**
     * Test method for
     * {@link org.talend.survivorship.action.SmallestAction#canHandle(org.talend.survivorship.action.ActionParameter)}.
     */
    @Test
    public void testCheckCanHandle() {
        DataSet dataset = null;
        Object inputData = 100;
        int rowNum = 0;
        String column = "firstName";
        String ruleName = "rule1";
        String expression = "Tony,Green";
        boolean ignoreBlanks = false;
        ActionParameter actionParameter = new ActionParameter(dataset, inputData, rowNum, column, ruleName, expression,
                ignoreBlanks);
        SmallestAction smallestAction = new SmallestAction();
        boolean checkCanHandle = smallestAction.canHandle(actionParameter);
        Assert.assertFalse("100 is not a String so that result should be false", checkCanHandle);
    }

}
