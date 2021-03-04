// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.utils;

import org.junit.Assert;
import org.junit.Test;

public class AsciiUtilsTest {

    /**
     * List of characters to transform. Correspondence should be maintained with {@link AsciiUtils#PLAIN_ASCII} field.
     * (code from http://www.rgagnon.com/javadetails/java-0456.html)
     */
    static final String UNICODE = "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9" // grave
            + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD" // acute
            + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB" // circumflex
            + "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1" // tilde
            + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u00FF" // diaeresis
            + "\u00C5\u00E5" // ring
            + "\u00C7\u00E7" // cedilla
    ;

    /**
     * List of replacement characters. Correspondence should be maintained with {@link AsciiUtils#UNICODE} field.
     */
    static final String PLAIN_ASCII = "AaEeIiOoUu" // grave
            + "AaEeIiOoUuYy" // acute
            + "AaEeIiOoUu" // circumflex
            + "AaOoNn" // tilde
            + "AaEeIiOoUuy" // diaeresis
            + "Aa" // ring
            + "Cc" // cedilla
    ;

    /**
     * Test method for {@link org.talend.utils.string.AsciiUtils#removeDiacriticalMarks(java.lang.String)} .
     */
    @Test
    public void testRemoveDiacriticalMarks() {
        String toReplace = UNICODE;
        StringBuilder sb = new StringBuilder();
        for (int i = '\u00D0'; i <= '\u024F'; i++) {
            sb.append((char) i);
        }
        Assert.assertEquals(PLAIN_ASCII, AsciiUtils.removeDiacriticalMarks(toReplace));
    }

}
