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
 * The factory which creates handler
 */
public class HandlerFactory {

    private static HandlerFactory instance = null;

    /**
     * 
     * The constructor of HandlerFactory.
     */
    private HandlerFactory() {

    }

    public static HandlerFactory getInstance() {
        if (instance == null) {
            instance = new HandlerFactory();
        }
        return instance;
    }

    /**
     * 
     * Create a new CRCRHandler(which maybe CRCRHandler or FillEmptyCRCRHandler)
     * 
     * @param parameter
     * @return
     */
    public CRCRHandler createCRCRHandler(HandlerParameter parameter) {
        CRCRHandler returnHandler = null;
        switch (parameter.getFunction()) {
        case RemoveDuplicate:
            returnHandler = new RemoveDuplicateCRCRHandler(parameter);
            return returnHandler;
        case FillEmpty:
            returnHandler = new FillEmptyCRCRHandler(parameter);
            return returnHandler;
        default:
            returnHandler = new CRCRHandler(parameter);
            return returnHandler;
        }
    }
}
