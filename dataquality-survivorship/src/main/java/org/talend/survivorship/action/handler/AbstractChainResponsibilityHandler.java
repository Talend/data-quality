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

import java.util.Map;

import org.talend.survivorship.action.ActionParameter;

/**
 * create by zshen abstract chainResponsibility class
 * all kind of handle node will expands it
 * 
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
    public void handleRequest(Object inputData, int rowNum) {
        if (!isContinue(inputData, rowNum)) {
            return;
        } else {
            doHandle(inputData, rowNum, handlerParameter.getRuleName());
        }

        if (this.getSuccessor() == null) {
            return;
        }
        this.getSuccessor().handleRequest(inputData, rowNum);

    }

    /**
     * 
     * Handle the request
     */
    public void handleRequest() {
        // need to implement on the sub class

    }

    protected void initConflictRowNum(Map<Integer, String> preConflictRowNum) {
        // no need to implement
    }

    /**
     * create by zshen handle inputData in the method.
     * 
     * @param inputData input data
     * @param rowNum row number of inputdata
     * @param ruleName the name of current rule
     */
    protected void doHandle(Object inputData, int rowNum, String ruleName) {
        // no thing to do
    }

    /**
     * Create by zshen whether current rule should be execute by next handler node
     * Note that when current method return false mean that execute will be stop include of behind handler node
     * 
     * @param inputData input data
     * @param rowNum row number of inputdata
     * @return true when current execute should stop immediately
     */
    protected boolean isContinue(Object inputData, int rowNum) {
        // current class alaways return true from here this method need to be overrided in special case
        return true;
    }

    /**
     * 
     * Judge whether current handler should be execute
     * 
     * @param inputData input data
     * @param expression parameter when current Function is(Expression,MappingTo,MatchRegex)
     * @param rowNum row number of inputdata
     * 
     * @return true when current inputdata is adopt to require else false
     */
    protected boolean canHandler(Object inputData, String expression, int rowNum) {
        return this.handlerParameter.getAction()
                .checkCanHandle(new ActionParameter(handlerParameter.getDataset(),
                        handlerParameter.getRefInputData((Object[]) inputData), rowNum, handlerParameter.getRefColumn().getName(),
                        handlerParameter.getRuleName(), expression, handlerParameter.isIgnoreBlank()));
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
    public AbstractChainResponsibilityHandler linkSuccessor(AbstractChainResponsibilityHandler nextHandler) {
        successor = nextHandler;
        return successor;
    }

}
