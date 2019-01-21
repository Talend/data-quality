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
public class LatinUppercasedLettersTest {

    @Test
    public void recognizeCharacters() {
        LatinLetters latinLetters = new LatinLetters();
        String handleRequest = latinLetters.handleRequest("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ"); //$NON-NLS-1$
        Assert.assertEquals("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", handleRequest); //$NON-NLS-1$
    }

    @Test
    public void unrecognizedCharacters() {
        LatinLetters latinLetters = new LatinLetters();
        String handleRequest = latinLetters.handleRequest("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß"); //$NON-NLS-1$
        Assert.assertEquals("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß", handleRequest); //$NON-NLS-1$
    }

}
