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
package org.talend.dataquality.wordnet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TextUtilsTest {

    /**
     * Test method for {@link org.talend.dataquality.wordnet.TextUtils#cutText(java.lang.String)}.
     */
    @Test
    public void testCutText() {
        assertEquals("", TextUtils.cutText(null));
        assertEquals("", TextUtils.cutText(""));
        String cutText = TextUtils.cutText("HelloWord!");
        assertEquals("Hello Word!", cutText);
        // surrogate pair
        cutText = TextUtils.cutText("ItIs𠀐𠀑");
        assertEquals("It Is𠀐𠀑", cutText);
    }

}
