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
 * DOC talend class global comment. Detailled comment
 */
public abstract class AbstractChainResponsibilityHandler {

    /**
     * Next one successor
     */
    protected AbstractChainResponsibilityHandler successor;

    protected HandlerParameter handlerParameter;

    public AbstractChainResponsibilityHandler(AbstractChainResponsibilityHandler acrhandler) {
        handlerParameter = acrhandler.getHandlerParameter();
    }

    public AbstractChainResponsibilityHandler(HandlerParameter handlerParameter) {
        this.handlerParameter = handlerParameter;
    }

    /**
     * Getter for handlerParameter.
     * 
     * @return the handlerParameter
     */
    public HandlerParameter getHandlerParameter() {
        return this.handlerParameter;
    }

    /**
     * 
     * Handle the request
     */
    public void handleRequest(Object inputData, int rowNum, String column) {
        if (!isContinue(inputData, column, rowNum)) {
            return;
        } else {
            doHandle(inputData, rowNum, column, handlerParameter.getRuleName());
        }

        if (this.getSuccessor() == null) {
            return;
        }
        this.getSuccessor().handleRequest(inputData, rowNum, column);
    }

    /**
     * DOC zshen Comment method "doHandle".
     * 
     * @param dataset
     * @param inputData
     * @param column
     * @param ignoreBlanks
     */
    protected void doHandle(Object inputData, int rowNum, String column, String ruleName) {
        // no thing to do
    }

    /**
     * DOC zshen Comment method "isContinue".
     * 
     * @param dataset
     * @param inputData
     * @param column
     * @param ignoreBlanks
     */
    protected boolean isContinue(Object inputData, String column, int rowNum) {
        return true;
    }

    /**
     * 
     * Judge whether current handler should be execute
     * 
     * @return
     */
    protected boolean canHandler(Object inputData, String column, String expression, int rowNum) {
        return this.handlerParameter.getAction().checkCanHandle(new ActionParameter(handlerParameter.getDataset(), inputData,
                rowNum, column, handlerParameter.getRuleName(), expression, handlerParameter.isIgnoreBlank()));
    }

    /**
     * Getter for successor.
     * 
     * @return the successor
     */
    public AbstractChainResponsibilityHandler getSuccessor() {
        return this.successor;
    }

    /**
     * Sets the successor.
     * 
     * @param successor the successor to set
     */
    public AbstractChainResponsibilityHandler linkSuccessor(AbstractChainResponsibilityHandler successor) {
        this.successor = successor;
        return successor;
    }

}
