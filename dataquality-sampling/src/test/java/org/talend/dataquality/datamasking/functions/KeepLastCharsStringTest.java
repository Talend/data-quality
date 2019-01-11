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

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class KeepLastCharsStringTest {

    private String output;

    private String input = "123456"; //$NON-NLS-1$

    private KeepLastCharsString klads = new KeepLastCharsString();

    @Before
    public void setUp() throws Exception {
        klads.setRandom(new Random(42));
    }

    @Test
    public void emptyReturnsEmpty() {
        klads.setKeepEmpty(true);
        output = klads.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void defaultBehavior() {
        klads.parse("3", false, new Random(42));
        output = klads.generateMaskedRow(input);
        assertEquals("038456", output); //$NON-NLS-1$

        // add msjian test for bug TDQ-11339: fix a "String index out of range: -1" exception
        String[] input2 = new String[] { "test1234", "pp456", "wei@sina.com" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String[] output2 = new String[] { "ñjëñ0534", "ài956", "zßw@sñuu.uom" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        klads.parse("2", false, new Random(42));
        for (int i = 0; i < input2.length; i++) {
            output = klads.generateMaskedRow(input2[i]);
            assertEquals(output2[i], output);
        }
        // TDQ-11339~
    }

    @Test
    public void dummyHighParameter() {
        klads.parse("7", false, new Random(42));
        output = klads.generateMaskedRow(input);
        assertEquals(input, output);
    }

    @Test
    public void twoParameters() {
        klads.parse("3,i", false, new Random(42));
        output = klads.generateMaskedRow(input);
        assertEquals("iii456", output);
    }
}
