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

/**
 * create by zshen Multi-Target Responsibility chain handler
 */
public class MTCRHandler extends AbstractChainOfResponsibilityHandler {

    /**
     * constructor
     * 
     * @param abstractCRHandler
     */
    public MTCRHandler(AbstractChainOfResponsibilityHandler abstractCRHandler) {
        super(abstractCRHandler);
    }

    /**
     * construct
     * 
     * @param handlerParameter
     */
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
    protected void doHandle(Object inputData, int rowNum, String ruleName) {
        this.getHandlerParameter().getAction()
                .handle(new ActionParameter(getHandlerParameter().getDataset(), inputData, rowNum,
                        getHandlerParameter().getTargetColumn().getName(), ruleName, getHandlerParameter().getExpression(),
                        getHandlerParameter().isIgnoreBlank()));
    }

}
