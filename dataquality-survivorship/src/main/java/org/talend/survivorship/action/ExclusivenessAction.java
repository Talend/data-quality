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

import java.util.Arrays;
import java.util.List;

/**
 * Create by zshen define a action which filter some exclusiveness values
 */
public class ExclusivenessAction extends AbstractSurvivoredAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.AbstractSurvivoredAction#checkCanHandle(org.talend.survivorship.action.ActionParameter)
     */
    @Override
    public boolean checkCanHandle(ActionParameter actionParameter) {
        String exclusivenessStr = actionParameter.getExpression();

        Object inputData = actionParameter.getInputData();
        return !checkUnExpect(exclusivenessStr, inputData);
    }

    public static boolean checkUnExpect(String exclusivenessStr, Object inputData) {
        String[] contantsArray = exclusivenessStr.split(","); //$NON-NLS-1$
        List<String> contantsList = Arrays.asList(contantsArray);

        return contantsList.contains(inputData);
    }

}
