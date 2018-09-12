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
        String input = "HelloWord";
        String replaceCharacters = ReplaceCharacterHelper.replaceCharacters(input, new Random(42));
        assertTrue(replaceCharacters.length() == input.length()); //$NON-NLS-1$
        assertTrue(replaceCharacters.codePoints().count() == input.codePoints().count());
    }

    @Test
    public void replaceSurrogate() {
        String input = "He𠀐𠀑Woあい";
        String replaceCharacters = ReplaceCharacterHelper.replaceCharacters(input, new Random(42));
        assertTrue(replaceCharacters.codePoints().count() == input.codePoints().count());
    }

    @Test
    public void replaceSurrogates() {
        String input = "\uD840\uDC40\uD840\uDFD3\uD841\uDC01\uD840\uDFD3";
        String replaceCharacters = ReplaceCharacterHelper.replaceCharacters(input, new Random(42));
        assertTrue(replaceCharacters.codePoints().count() == input.codePoints().count());
    }

}
