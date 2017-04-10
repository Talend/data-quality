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

/**
 * Create by zshen define a action which make sure find out most recent input value in all of values
 */
public class MostRecentAction extends AbstractSurvivoredAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.ISurvivoredAction#checkCanHandle(org.talend.survivorship.model.DataSet,
     * java.lang.Object, java.lang.String, boolean)
     */
    @Override
    public boolean checkCanHandle(ActionParameter actionParameter) {
        if (!(actionParameter.getInputData() instanceof Date)) {
            return false;
        }
        return actionParameter.getDataset().isLatest(actionParameter.getInputData(), actionParameter.getColumn());
    }

}
