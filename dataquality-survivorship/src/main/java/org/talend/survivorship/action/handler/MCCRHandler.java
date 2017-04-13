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

/**
 * DOC zshen class global comment. Detailled comment
 */
public class MCCRHandler extends AbstractChainResponsibilityHandler {

    public MCCRHandler(AbstractChainResponsibilityHandler acrHandler) {
        super(acrHandler);
    }

    /**
     * DOC zshen MCCRHandler constructor comment.
     * 
     * @param parameterObject TODO
     */
    public MCCRHandler(HandlerParameter parameterObject) {
        super(parameterObject);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#isContinue(org.talend.survivorship.model.DataSet,
     * java.lang.Object, java.lang.String, boolean)
     */
    @Override
    protected boolean isContinue(Object inputData, int rowNum) {
        if (this.getHandlerParameter().getRefColumn() == null) {
            return false;
        }
        if (this.canHandler(inputData, getHandlerParameter().getExpression(), rowNum)) {
            return true;
        }
        return false;
    }

}
