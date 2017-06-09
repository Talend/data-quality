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
 * all kinds of handle node will expands it
 * 
 */
public abstract class AbstractChainOfResponsibilityHandler {

    /**
     * Next one successor
     */
    protected AbstractChainOfResponsibilityHandler successor;

    /**
     * Previous one successor
     */
    protected AbstractChainOfResponsibilityHandler preSuccessor;

    protected HandlerParameter handlerParameter;

    public AbstractChainOfResponsibilityHandler(AbstractChainOfResponsibilityHandler acrhandler) {
        handlerParameter = acrhandler.getHandlerParameter();
    }

    public AbstractChainOfResponsibilityHandler(HandlerParameter handlerParameter) {
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
        if (!needContinue(inputData, rowNum)) {
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
     * handle inputData in the method.
     * 
     * @param inputData input data
     * @param rowNum row number of inputdata
     * @param ruleName the name of current rule
     */
    protected void doHandle(Object inputData, int rowNum, String ruleName) {
        // no thing to do
    }

    /**
     * whether current rule should be execute by next handler node
     * Note that when current method return false mean that execute will be stop include of behind handler node
     * 
     * @param inputData input data
     * @param rowNum row number of inputdata
     * @return true when current execute should stop immediately
     */
    protected boolean needContinue(Object inputData, int rowNum) {
        // current class always return true from here this method need to be overrided in special case
        return true;
    }

    /**
     * 
     * Judge whether current handler should be execute
     * 
     * @param inputData input data
     * @param expression parameter when current Function is(Expression,SurviveAs,MatchRegex)
     * @param rowNum row number of inputdata
     * 
     * @return true when current inputdata is adopt to require else false
     */
    protected boolean canHandle(Object inputData, String expression, int rowNum) {
        return this.handlerParameter.getAction().canHandle(new ActionParameter(handlerParameter.getDataset(),
                handlerParameter.getRefInputData((Object[]) inputData), rowNum, handlerParameter.getReferenceColumn().getName(),
                handlerParameter.getRuleName(), expression, handlerParameter.isIgnoreBlank()));
    }

    /**
     * Getter for successor.
     * 
     * @return the successor
     */
    public AbstractChainOfResponsibilityHandler getSuccessor() {
        return this.successor;
    }

    /**
     * Sets the successor.
     * 
     * @param successor the successor to set
     */
    public AbstractChainOfResponsibilityHandler linkSuccessor(AbstractChainOfResponsibilityHandler nextHandler) {
        successor = nextHandler;
        nextHandler.preSuccessor = this;
        return successor;
    }

    /**
     * Getter for preSuccessor.
     * 
     * @return the preSuccessor
     */
    protected AbstractChainOfResponsibilityHandler getPreSuccessor() {
        return this.preSuccessor;
    }

}
