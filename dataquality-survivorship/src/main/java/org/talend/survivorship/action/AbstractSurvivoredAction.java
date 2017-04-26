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

/**
 * create by zshen Abstract Action
 */
public abstract class AbstractSurvivoredAction implements ISurvivoredAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.ISurvivoredAction#handle(org.talend.survivorship.model.DataSet, java.lang.Object, int,
     * java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void handle(ActionParameter actionParameter) {
        actionParameter.getDataset().survive(actionParameter.getRowNum(), actionParameter.getColumn(),
                actionParameter.getRuleName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.ISurvivoredAction#checkCanHandle(org.talend.survivorship.model.DataSet,
     * java.lang.Object, java.lang.String, java.lang.String, int, boolean)
     */
    @Override
    public boolean checkCanHandle(ActionParameter actionParameter) {
        return false;
    }

}
