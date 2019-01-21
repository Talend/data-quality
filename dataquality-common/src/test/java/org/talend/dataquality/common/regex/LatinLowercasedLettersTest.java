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
package org.talend.dataquality.common.regex;

import org.junit.Assert;
import org.junit.Test;

/**
 * DOC talend class global comment. Detailled comment
 */
public class LatinLowercasedLettersTest {

    @Test
    public void recognizeCharacters() {
        LatinLettersSmall latinLettersSmall = new LatinLettersSmall();
        String handleRequest = latinLettersSmall.handleRequest("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß"); //$NON-NLS-1$
        Assert.assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", handleRequest); //$NON-NLS-1$
    }

    @Test
    public void unrecognizeCharacters() {
        LatinLettersSmall latinLettersSmall = new LatinLettersSmall();
        String handleRequest = latinLettersSmall.handleRequest("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ"); //$NON-NLS-1$
        Assert.assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ", handleRequest); //$NON-NLS-1$
    }

}
