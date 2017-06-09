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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Create by zshen define a action which filter values that do not match the defined regex
 */
public class ExcludeValuesAction extends AbstractSurvivorshipAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.AbstractSurvivoredAction#checkCanHandle(org.talend.survivorship.action.ActionParameter)
     */
    @Override
    public boolean canHandle(ActionParameter actionParameter) {
        if (actionParameter.getExpression() == null) {
            return true;
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript"); //$NON-NLS-1$
        try {
            if (actionParameter.getInputData() != null) {
                engine.put("inputData", actionParameter.getInputData()); //$NON-NLS-1$
                Object eval = engine.eval("inputData.match(\"" + actionParameter.getExpression() + "\")"); //$NON-NLS-1$ //$NON-NLS-2$
                return eval == null;

            }
        } catch (ScriptException e) {
            // no need implement
        }
        return false;
    }

    public static boolean checkUnexpectedValue(String valuesToExclude, Object inputData) {
        String[] contantsArray = valuesToExclude.split(","); //$NON-NLS-1$
        List<String> contantsList = Arrays.asList(contantsArray);

        return contantsList.contains(inputData);
    }

}
