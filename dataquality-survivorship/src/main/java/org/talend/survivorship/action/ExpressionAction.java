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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Create by zshen define a action which make sure input value is adapt the special expression
 */
public class ExpressionAction extends AbstractSurvivorshipAction {

    private static final Logger LOGGER = Logger.getLogger("ExcludeValuesAction");

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.ISurvivoredAction#checkCanHandle(org.talend.survivorship.model.DataSet,
     * java.lang.Object, java.lang.String, boolean)
     */
    @Override
    public boolean canHandle(ActionParameter actionParameter) {
        if (actionParameter.getExpression() == null) {
            return false;
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript"); //$NON-NLS-1$
        try {
            if (actionParameter.getInputData() != null && actionParameter.getInputData() instanceof Number) {

                return (Boolean) engine.eval(actionParameter.getInputData().toString() + actionParameter.getExpression());
            } else if (actionParameter.getInputData() != null && actionParameter.getInputData() instanceof Date) {
                String varName = actionParameter.getColumn() + "Date"; //$NON-NLS-1$
                engine.put(varName, actionParameter.getInputData());
                return (Boolean) engine.eval("" + varName + actionParameter.getExpression()); //$NON-NLS-1$
            } else {
                String varName = actionParameter.getColumn() + "String"; //$NON-NLS-1$
                engine.put(varName, actionParameter.getInputData());
                return (Boolean) engine.eval("" + varName + actionParameter.getExpression()); //$NON-NLS-1$

            }
        } catch (ScriptException e) {
            LOGGER.log(Level.CONFIG, e.getMessage(), e);
            // no need implement
        }
        return false;
    }

}
