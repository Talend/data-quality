// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.talend.survivorship.action.handler.AbstractChainOfResponsibilityHandler;

/**
 * Create by zshen Create a map used to store all of handler node.
 * The key is name of column the value is firstly node of handler
 */
public class ChainNodeMap extends HashMap<String, AbstractChainOfResponsibilityHandler> {

    private static final long serialVersionUID = 1L;

    private Map<Integer, AbstractChainOfResponsibilityHandler> orderMap = new HashMap<>();

    public void handleRequest(Object inputData, int rowNum) {
        for (Entry<String, AbstractChainOfResponsibilityHandler> entry : this.entrySet()) {
            entry.getValue().handleRequest(inputData, rowNum);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    public AbstractChainOfResponsibilityHandler put(String key, AbstractChainOfResponsibilityHandler value,
            int executeIndex) {
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
        AbstractChainOfResponsibilityHandler preNode = findpreviousNode(executeIndex);
        AbstractChainOfResponsibilityHandler nextNode = findNextNode(executeIndex, findMaxIndex());
        if (preNode != null) {
            preNode.linkUISuccessor(value);
        }
        if (nextNode != null) {
            value.linkUISuccessor(nextNode);
        }
    }

    private int findMaxIndex() {
        Set<Integer> keySet = orderMap.keySet();
        Integer maxIndex = orderMap.size();
        for (Integer currentIndex : keySet) {
            if (maxIndex < currentIndex) {
                maxIndex = currentIndex;
            }
        }
        return maxIndex;
    }

    private AbstractChainOfResponsibilityHandler findNextNode(int currentIndex, int maxIndex) {
        if (currentIndex >= maxIndex) {
            return null;
        }
        int findIndex = currentIndex;
        AbstractChainOfResponsibilityHandler nextNode = orderMap.get(findIndex + 1);
        if (nextNode == null) {
            findIndex = findIndex + 1;
            return findNextNode(findIndex, maxIndex);
        }
        return nextNode;
    }

    private AbstractChainOfResponsibilityHandler findpreviousNode(int currentIndex) {
        if (currentIndex < 0) {
            return null;
        }
        int findIndex = currentIndex;
        AbstractChainOfResponsibilityHandler previousNode = orderMap.get(findIndex - 1);
        if (previousNode == null) {
            findIndex = findIndex - 1;
            return findpreviousNode(findIndex);
        }
        return previousNode;
    }

    /**
     * 
     * Get first rule node
     * 
     * @return null if nothing here
     */
    public AbstractChainOfResponsibilityHandler getFirstNode() {
        if (orderMap.isEmpty()) {
            return null;
        }
        Iterator<Integer> iterator = orderMap.keySet().iterator();
        return orderMap.get(iterator.next());
    }

    /**
     * Get the orderMap
     * 
     * @return
     */
    public Map<Integer, AbstractChainOfResponsibilityHandler> getOrderMap() {
        return orderMap;
    }
}
