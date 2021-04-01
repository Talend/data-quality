// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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

import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.EcmaError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delight.rhinosandox.RhinoSandbox;
import delight.rhinosandox.RhinoSandboxes;

/**
 * Create by zshen define a action which make sure input value is adapt the
 * special regex
 */
public class MatchRegexAction extends AbstractSurvivorshipAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchRegexAction.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.survivorship.action.ISurvivoredAction#checkCanHandle(org.talend.
     * survivorship.model.DataSet, java.lang.Object, java.lang.String,
     * java.lang.String, int, boolean)
     */
    @Override
    public boolean canHandle(ActionParameter actionParameter) {
        if (actionParameter.getExpression() == null) {
            return false;
        }
        final RhinoSandbox sandbox = RhinoSandboxes.create();
        try {
            if (actionParameter.getInputData() != null) {
                Map<String, Object> values = new HashMap<String, Object>();
                values.put("inputData", actionParameter.getInputData());
                Object eval = sandbox.eval(null, "inputData.match(\"" + actionParameter.getExpression() + "\")", //$NON-NLS-1$ //$NON-NLS-2$
                        values);
                return eval != null;

            }
        } catch (EcmaError e) {
            LOGGER.error(e.getMessage(), e);
            // no need implement
        }
        return false;
    }

}
