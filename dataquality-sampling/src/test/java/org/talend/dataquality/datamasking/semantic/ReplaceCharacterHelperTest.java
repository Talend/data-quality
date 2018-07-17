// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.semantic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class ReplaceCharacterHelperTest {

    @Test
    public void testReplaceCharacters() {
        assertEquals("", ReplaceCharacterHelper.replaceCharacters("", new Random()));
        String replaceCharacters = ReplaceCharacterHelper.replaceCharacters("HelloWord", new Random(42));
        assertTrue(replaceCharacters.length() == 9); //$NON-NLS-1$

        replaceCharacters = ReplaceCharacterHelper.replaceCharacters("He𠀐𠀑Woあい", new Random(42));
        assertTrue(replaceCharacters.length() == 10);
        assertTrue(replaceCharacters.codePoints().count() == 8);
        assertTrue(replaceCharacters.contains("𠀐𠀑"));
    }

}
