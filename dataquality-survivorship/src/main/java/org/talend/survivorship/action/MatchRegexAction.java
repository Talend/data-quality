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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Create by zshen define a action which make sure input value is adapt the special regex
 */
public class MatchRegexAction extends AbstractSurvivorshipAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.ISurvivoredAction#checkCanHandle(org.talend.survivorship.model.DataSet,
     * java.lang.Object, java.lang.String, java.lang.String, int, boolean)
     */
    @Override
    public boolean canHandle(ActionParameter actionParameter) {
        if (actionParameter.getExpression() == null) {
            return false;
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript"); //$NON-NLS-1$
        try {
            if (actionParameter.getInputData() != null) {
                engine.put("inputData", actionParameter.getInputData()); //$NON-NLS-1$
                Object eval = engine.eval("inputData.match(\"" + actionParameter.getExpression() + "\")"); //$NON-NLS-1$ //$NON-NLS-2$
                return eval != null;

            }
        } catch (ScriptException e) {
            // no need implement
        }
        return false;
    }

}
