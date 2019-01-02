// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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

import org.junit.Assert;
import org.junit.Test;
import org.talend.survivorship.action.handler.AbstractChainOfResponsibilityHandler;
import org.talend.survivorship.action.handler.HandlerParameter;
import org.talend.survivorship.action.handler.MCCRHandler;

public class ChainNodeMapTest {

    /**
     * Test method for {@link org.talend.survivorship.utils.ChainNodeMap#getFirstNode()}.
     */
    @Test
    public void testGetFirstNodeNormalCase() {
        MCCRHandler crnode1 = createHandler("node1"); //$NON-NLS-1$
        MCCRHandler crnode2 = createHandler("node2"); //$NON-NLS-1$
        MCCRHandler crnode3 = createHandler("node3"); //$NON-NLS-1$
        MCCRHandler crnode4 = createHandler("node4"); //$NON-NLS-1$
        MCCRHandler crnode5 = createHandler("node5"); //$NON-NLS-1$
        ChainNodeMap nodeMap = new ChainNodeMap();
        nodeMap.linkNodes(2, crnode2);
        nodeMap.linkNodes(1, crnode1);
        nodeMap.linkNodes(5, crnode5);
        nodeMap.linkNodes(4, crnode4);
        nodeMap.linkNodes(3, crnode3);

        AbstractChainOfResponsibilityHandler firstNode = nodeMap.getFirstNode();
        String ruleName = firstNode.getHandlerParameter().getRuleName();
        Assert.assertEquals("The first node should be node1", "node1", ruleName); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for {@link org.talend.survivorship.utils.ChainNodeMap#getFirstNode()}.
     */
    @Test
    public void testGetFirstNodeEmptyCase() {
        ChainNodeMap nodeMap = new ChainNodeMap();
        AbstractChainOfResponsibilityHandler firstNode = nodeMap.getFirstNode();
        Assert.assertNull("The firstNode should be null", firstNode); //$NON-NLS-1$
    }

    protected MCCRHandler createHandler(String name) {
        HandlerParameter parameter = new HandlerParameter(null, null, null, name, null, null, null);
        MCCRHandler crnode1 = new MCCRHandler(parameter);
        return crnode1;
    }

}
