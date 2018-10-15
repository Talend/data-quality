package org.talend.dataquality.common.regex;

import org.junit.Assert;
import org.junit.Test;

public class HandlerFactoryTest {

    @Test
    public void createEastAsiaPatternHandler() {
        ChainResponsibilityHandler chainResponsibilityHandler = HandlerFactory.createEastAsiaPatternHandler();

        Assert.assertEquals(chainResponsibilityHandler.handleRequest("ケーキ"), "KKK");
        Assert.assertEquals(chainResponsibilityHandler.handleRequest("ほーむ"), "HHH");
        Assert.assertEquals(chainResponsibilityHandler.handleRequest("ｰヽヾ"), "kKK");
        Assert.assertEquals(chainResponsibilityHandler.handleRequest("ゝゞゟ"), "HHゟ");
        Assert.assertEquals(chainResponsibilityHandler.handleRequest("dー"), "dー");
        Assert.assertEquals(chainResponsibilityHandler.handleRequest("kー"), "kー");
        // --- Known limit of the actual implementation
        Assert.assertEquals(chainResponsibilityHandler.handleRequest("Kー"), "KK");
    }
}