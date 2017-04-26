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
 * Create by zshen define a interface of action
 */
public interface ISurvivoredAction {

    /**
     * 
     * Create by zshen record inputdata exist a possible which is survived value
     * 
     * @param parameterObject the parameter of current action
     */
    public void handle(ActionParameter parameterObject);

    /**
     * 
     * Create by zshen Check whether current inputdata is survived value
     * 
     * @param parameterObject the parameter of action
     * @return true when inpudata is survived value else false
     */
    public boolean checkCanHandle(ActionParameter parameterObject);

}
