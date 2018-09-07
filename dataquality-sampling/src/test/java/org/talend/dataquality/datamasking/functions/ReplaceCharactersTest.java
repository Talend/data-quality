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
public class ReplaceCharactersTest {

    private String output;

    private String input = "inp456ut value"; //$NON-NLS-1$

    private ReplaceCharacters rc = new ReplaceCharacters();

    @Test
    public void testGood() {
        rc.parse("X", false, new Random(42));
        output = rc.generateMaskedRow(input);
        assertEquals("XXX456XX XXXXX", output); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() {
        rc.setKeepEmpty(true);
        output = rc.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testParameter() {
        rc.parse("5", false, new Random(42));
        output = rc.generateMaskedRow(input);
        assertEquals("55545655 55555", output); //$NON-NLS-1$
    }

    @Test
    public void testEmptyParameter() {
        rc.parse(" ", false, new Random(42));
        output = rc.generateMaskedRow(input);
        assertEquals("ñjë456ñï xàiäz", output); //$NON-NLS-1$
    }

    @Test
    public void testWrongParameter() {
        try {
            rc.parse("12", false, new Random(42));
            fail("should get exception with input " + rc.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = rc.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

}
