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
 * Abstract class about data cleaning process
 */
public abstract class DataCleansingCRCRHandler extends CRCRHandler {

    /**
     * The constructor of DataCleanCRCRHandler
     * 
     * @param handlerParameter
     */
    public DataCleansingCRCRHandler(HandlerParameter handlerParameter) {
        super(handlerParameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#canHandler(java.lang.Object,
     * java.lang.String, int)
     */
    @Override
    protected boolean canHandle(Object inputData, String expression, int rowNum) {
        // This class is not resolve conflict so that any data will return true
        return true;
    }

}
