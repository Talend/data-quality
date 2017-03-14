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
package org.talend.survivorship.utils;

import java.util.HashMap;

import org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class ChainNodeMap extends HashMap<String, AbstractChainResponsibilityHandler> {

    private static final long serialVersionUID = 1L;

    // public void handleRequest(Object inputData, int rowNum, String column) {
    // for (String ruleName : this.keySet()) {
    // this.get(ruleName).handleRequest(inputData, rowNum, column);
    // }
    // }
    public void handleRequest(Object inputData, int rowNum) {
        for (String ruleName : this.keySet()) {
            this.get(ruleName).handleRequest(inputData, rowNum);
        }
    }
}
