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
 * Create by zshen test for ShortestAction
 */
public class ShortestActionTest {

    /**
     * Test method for
     * {@link org.talend.survivorship.action.ShortestAction#checkCanHandle(org.talend.survivorship.action.ActionParameter)}.
     */
    @Test
    public void testCheckCanHandle() {
        DataSet dataset = null;
        Object inputData = 100;
        int rowNum = 0;
        String column = "firstName"; //$NON-NLS-1$
        String ruleName = "rule1"; //$NON-NLS-1$
        String expression = "Tony,Green"; //$NON-NLS-1$
        boolean ignoreBlanks = false;
        ActionParameter actionParameter = new ActionParameter(dataset, inputData, rowNum, column, ruleName, expression,
                ignoreBlanks);
        ShortestAction shortestAction = new ShortestAction();
        boolean checkCanHandle = shortestAction.checkCanHandle(actionParameter);
        Assert.assertFalse("100 is not a String so that result should be false", checkCanHandle); //$NON-NLS-1$
    }

}
