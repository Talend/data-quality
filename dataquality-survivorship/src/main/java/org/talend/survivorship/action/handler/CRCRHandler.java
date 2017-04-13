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

import java.util.ArrayList;
import java.util.List;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class CRCRHandler extends AbstractChainResponsibilityHandler {

    List<Integer> conflictRowNum = new ArrayList<>();

    /**
     * DOC zshen CRCRHandler constructor comment.
     * 
     * @param handlerParameter
     */
    public CRCRHandler(HandlerParameter handlerParameter) {
        super(handlerParameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#doHandle(java.lang.Object, int,
     * java.lang.String, java.lang.String)
     */
    @Override
    protected void doHandle(Object inputData, int rowNum, String ruleName) {
        this.conflictRowNum.add(rowNum);
    }

    @Override
    protected void initConflictRowNum(List<Integer> preConflictRowNum) {
        this.conflictRowNum.addAll(preConflictRowNum);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#handleRequest(java.lang.Object, int,
     * java.lang.String)
     */
    @Override
    public void handleRequest(Object inputData, int rowNum) {
        super.handleRequest(inputData, rowNum);
        if (this.getSuccessor() == null) {
            return;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#isContinue(java.lang.Object,
     * java.lang.String, int)
     */
    @Override
    protected boolean isContinue(Object inputData, int rowNum) {
        if (this.conflictRowNum.size() == 1) {
            return false;
        }
        if (this.canHandler(inputData, getHandlerParameter().getExpression(), rowNum)) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#getSuccessor()
     */
    @Override
    public CRCRHandler getSuccessor() {
        return (CRCRHandler) super.getSuccessor();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#linkSuccessor(org.talend.survivorship.action.
     * handler.AbstractChainResponsibilityHandler)
     */
    @Override
    public AbstractChainResponsibilityHandler linkSuccessor(AbstractChainResponsibilityHandler successor) {
        // CRCRHandler link CRCRHandler only else will link fail and return itself
        if (successor instanceof CRCRHandler) {
            return super.linkSuccessor(successor);
        } else {
            return this;
        }
    }

    public int getSurvivoredRowNum() {
        if (this.conflictRowNum.size() == 1) {
            return this.conflictRowNum.get(0);
        } else {
            return this.getSuccessor().getSurvivoredRowNum();
        }
    }

}
