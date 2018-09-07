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
    public void testEmpty() {
        klads.setKeepEmpty(true);
        output = klads.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        klads.parse("3", false, new Random(42));
        output = klads.generateMaskedRow(input);
        assertEquals("038456", output); //$NON-NLS-1$

        // add msjian test for bug TDQ-11339: fix a "String index out of range: -1" exception
        String inputa[] = new String[] { "test1234", "pp456", "wei@sina.com" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String outputa[] = new String[] { "ñjëñ0534", "ài956", "zßw@sñuu.uom" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        klads.parse("2", false, new Random(42));
        for (int i = 0; i < inputa.length; i++) {
            output = klads.generateMaskedRow(inputa[i]);
            assertEquals(outputa[i], output);
        }
        // TDQ-11339~
    }

    @Test
    public void testDummyGood() {
        klads.parse("7", false, new Random(42));
        output = klads.generateMaskedRow(input);
        assertEquals(input, output);
    }

    @Test
    public void testParameter() {
        klads.parse("3,i", false, new Random(42));
        output = klads.generateMaskedRow(input);
        assertEquals("iii456", output);
    }
}
