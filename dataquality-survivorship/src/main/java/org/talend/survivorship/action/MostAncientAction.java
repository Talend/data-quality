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
 * Create by zshen define a action which make sure find out most ancient input value in all of values
 */
public class MostAncientAction extends AbstractSurvivorshipAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.ISurvivoredAction#checkCanHandle(org.talend.survivorship.model.DataSet,
     * java.lang.Object, java.lang.String, boolean)
     */
    @Override
    public boolean canHandle(ActionParameter actionParameter) {
        if (!(actionParameter.getInputData() instanceof Date)) {
            return false;
        }
        return actionParameter.getDataset().isEarliest(actionParameter.getInputData(), actionParameter.getColumn());
    }

}
