package org.talend.dataquality.common.regex;

import org.junit.Assert;
import org.junit.Test;

public class LatinLettersTest {
    @Test
    public void handleRequest() {
        LatinLetters latinLetters = new LatinLetters();
        String handleRequest = latinLetters.handleRequest("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ");
        Assert.assertEquals("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", handleRequest);
    }

    @Test
    public void handleLowerRequest() {
        LatinLetters latinLetters = new LatinLetters();
        String handleRequest = latinLetters.handleRequest("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß");
        Assert.assertEquals("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß", handleRequest);
    }
}
