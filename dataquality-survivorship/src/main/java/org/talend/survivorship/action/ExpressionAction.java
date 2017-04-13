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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class ExpressionAction extends AbstractSurvivoredAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.ISurvivoredAction#checkCanHandle(org.talend.survivorship.model.DataSet,
     * java.lang.Object, java.lang.String, boolean)
     */
    @Override
    public boolean checkCanHandle(ActionParameter actionParameter) {
        if (actionParameter.getExpression() == null) {
            return false;
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript"); //$NON-NLS-1$
        try {
            if (actionParameter.getInputData() != null && actionParameter.getInputData() instanceof Number) {

                return (Boolean) engine.eval(actionParameter.getInputData().toString() + actionParameter.getExpression());
            } else if (actionParameter.getInputData() != null && actionParameter.getInputData() instanceof Date) {
                String varName = actionParameter.getColumn() + "Date";
                engine.put(varName, actionParameter.getInputData());
                return (Boolean) engine.eval("" + varName + actionParameter.getExpression());
            } else {
                engine.put(actionParameter.getColumn(), actionParameter.getInputData());
                return (Boolean) engine.eval("" + actionParameter.getColumn() + actionParameter.getExpression()); //$NON-NLS-1$ 

            }
        } catch (ScriptException e) {
            // no need implement
        }
        return false;
    }

}
