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
import static org.junit.Assert.assertNull;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.utils.MockRandom;

/**
 * created by jgonzalez on 20 août 2015 Detailled comment
 *
 */
public class GenerateSsnGermanyTest {

    private String output;

    private GenerateSsnGermany gng = new GenerateSsnGermany();

    @Before
    public void setUp() throws Exception {
        gng.setRandom(new Random(42));
    }

    @Test
    public void testEmpty() {
        gng.setKeepEmpty(true);
        output = gng.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        output = gng.generateMaskedRow(null);
        assertEquals("03840558932", output); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gng.keepNull = true;
        output = gng.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void allDigitCanBeGenerated() {
        MockRandom random = new MockRandom();
        gng.setRandom(random);
        output = gng.generateMaskedRow(null);
        assertEquals("01234567890", output);
    }

    @Test
    public void consistentMasking() {
        gng.setSeed("aSeed");
        String result1 = gng.doGenerateMaskedField("01234567890", FunctionMode.CONSISTENT);
        String result2 = gng.doGenerateMaskedField("01234567890", FunctionMode.CONSISTENT);
        assertEquals(result2, result1);
    }
}
