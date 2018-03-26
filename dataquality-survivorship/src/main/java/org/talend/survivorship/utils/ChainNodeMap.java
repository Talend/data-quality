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
import java.util.Map;

import org.talend.survivorship.action.handler.AbstractChainOfResponsibilityHandler;

/**
 * Create by zshen Create a map used to store all of handler node.
 * The key is name of column the value is firstly node of handler
 */
public class ChainNodeMap extends HashMap<String, AbstractChainOfResponsibilityHandler> {

    private static final long serialVersionUID = 1L;

    private Map<Integer, AbstractChainOfResponsibilityHandler> orderMap = new HashMap<>();

    public void handleRequest(Object inputData, int rowNum) {
        for (String ruleName : this.keySet()) {
            this.get(ruleName).handleRequest(inputData, rowNum);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    public AbstractChainOfResponsibilityHandler put(String key, AbstractChainOfResponsibilityHandler value, int executeIndex) {
        linkNodes(executeIndex, value);
        return super.put(key, value);
    }

    /**
     * Link node by ui order
     * 
     * @param executeIndex
     * @param value
     */
    public void linkNodes(int executeIndex, AbstractChainOfResponsibilityHandler value) {
        orderMap.put(executeIndex, value);
        AbstractChainOfResponsibilityHandler preNode = orderMap.get(executeIndex - 1);
        AbstractChainOfResponsibilityHandler nextNode = orderMap.get(executeIndex + 1);
        if (preNode != null) {
            preNode.linkUISuccessor(value);
        }
        if (nextNode != null) {
            value.linkUISuccessor(nextNode);
        }
    }

    /**
     * 
     * Get first rule node
     * 
     * @return
     */
    public AbstractChainOfResponsibilityHandler getFirstNode() {
        return orderMap.get(0);
    }
}
