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
package org.talend.dataquality.datamasking.functions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceAllTest {

    private String output;

    private String input = "i86ut val 4"; //$NON-NLS-1$

    private ReplaceAll ra = new ReplaceAll();

    @Test
    public void testGood() {
        ra.parse("X", false, new Random(42));
        output = ra.generateMaskedRow(input);
        assertEquals("XXXXXXXXXXX", output); //$NON-NLS-1$
    }

    @Test
    public void testSurrogate() {
        ra.parse("", false, new Random(42));
        output = ra.generateMaskedRow("\uD840\uDC40\uD840\uDFD3\uD841\uDC01\uD840\uDFD3");
        assertEquals(4, output.codePoints().count()); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() {
        ra.setKeepEmpty(true);
        output = ra.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testCharacter() {
        ra.parse("?", false, new Random(42));
        output = ra.generateMaskedRow(input);
        assertEquals("???????????", output); //$NON-NLS-1$
    }

    @Test
    public void testWrongParameter() {
        try {
            ra.parse("zi", false, new Random(42));
            fail("should get exception with input " + ra.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = ra.generateMaskedRow(input);
        assertEquals("", output); // $NON-NLS-1$
    }

    @Test
    public void testNoParameter() {
        ra.parse(" ", false, new Random(42));
        output = ra.generateMaskedRow(input);
        assertEquals("ñ38ñï xài 9", output); //$NON-NLS-1$
    }

}
