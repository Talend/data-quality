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

    @Test
    public void createLatinPatternHandler() {
        ChainResponsibilityHandler chainResponsibilityHandler = HandlerFactory.createLatinPatternHandler();
        Assert.assertEquals(chainResponsibilityHandler.handleRequest("ACD"), "AAA");
        Assert.assertEquals(chainResponsibilityHandler.handleRequest("åæç"), "aaa");
        Assert.assertEquals(chainResponsibilityHandler.handleRequest("úêðñ"), "aaaa");
        Assert.assertEquals(chainResponsibilityHandler.handleRequest("APZ"), "AAA");
        Assert.assertEquals(chainResponsibilityHandler.handleRequest("Mk0"), "Aa9");
    }

    @Test
    public void createLatinPatternHandlerWithnumber() {
        ChainResponsibilityHandler chainResponsibilityHandler = HandlerFactory.createLatinPatternHandler();
        Assert.assertEquals(chainResponsibilityHandler.handleRequest("098"), "999");

    }
}