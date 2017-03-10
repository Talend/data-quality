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
package org.talend.survivorship.action.handler;

import org.talend.survivorship.action.ActionParameter;
import org.talend.survivorship.action.MostRecentAction;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class MTCRHandler extends AbstractChainResponsibilityHandler {

    public MTCRHandler(AbstractChainResponsibilityHandler acrHandler) {
        super(acrHandler);
    }

    public MTCRHandler(HandlerParameter handlerParameter) {
        super(handlerParameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#doHandle(org.talend.survivorship.model.DataSet,
     * java.lang.Object, java.lang.String, boolean)
     */
    @Override
    protected void doHandle(Object inputData, int rowNum, String column, String ruleName) {
        if (this.getHandlerParameter().getAction() instanceof MostRecentAction) {

        }
        this.getHandlerParameter().getAction()
                .handle(new ActionParameter(getHandlerParameter().getDataset(), inputData, rowNum,
                        getHandlerParameter().getTarColumn().getName(), ruleName, getHandlerParameter().getExpression(),
                        getHandlerParameter().isIgnoreBlank()));
    }

}
