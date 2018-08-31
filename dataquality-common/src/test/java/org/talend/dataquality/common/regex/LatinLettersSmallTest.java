package org.talend.dataquality.common.regex;

import org.junit.Assert;
import org.junit.Test;

public class LatinLettersSmallTest {
    @Test
    public void handleRequest() {
        LatinLettersSmall latinLettersSmall = new LatinLettersSmall();
        String handleRequest = latinLettersSmall.handleRequest("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß");
        Assert.assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", handleRequest);
    }

    @Test
    public void handleRequestUpper() {
        LatinLettersSmall latinLettersSmall = new LatinLettersSmall();
        String handleRequest = latinLettersSmall.handleRequest("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ");
        Assert.assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ", handleRequest);
    }

}
