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
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.EcmaError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delight.rhinosandox.RhinoSandbox;
import delight.rhinosandox.RhinoSandboxes;

/**
 * Create by zshen define a action which make sure input value is adapt the
 * special expression
 */
public class ExpressionAction extends AbstractSurvivorshipAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionAction.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.survivorship.action.ISurvivoredAction#checkCanHandle(org.talend.
     * survivorship.model.DataSet, java.lang.Object, java.lang.String, boolean)
     */
    @Override
    public boolean canHandle(ActionParameter actionParameter) {
        if (actionParameter.getExpression() == null) {
            return false;
        }
        final RhinoSandbox sandbox = RhinoSandboxes.create();
        try {
            String varName = actionParameter.getColumn();
            if (actionParameter.getInputData() == null) {
                return false;
            } else {
                if (actionParameter.getInputData() instanceof Number) {
                    return (Boolean) sandbox.eval(null,
                            actionParameter.getInputData().toString() + actionParameter.getExpression());
                } else if (actionParameter.getInputData() instanceof Date) {
                    varName = actionParameter.getColumn() + "Date"; //$NON-NLS-1$
                } else {
                    varName = actionParameter.getColumn() + "String"; //$NON-NLS-1$
                }
                Map<String, Object> values = new HashMap<String, Object>();
                values.put(varName, actionParameter.getInputData());
                return (Boolean) sandbox.eval(null, "" + varName + actionParameter.getExpression(), values);
            }
        } catch (EcmaError e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

}
