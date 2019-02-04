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
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.utils.MockRandom;

/**
 * created by jgonzalez on 20 août 2015 Detailled comment
 *
 */
public class GenerateSsnFrenchTest {

    private String output;

    private GenerateSsnFr gnf = new GenerateSsnFr();

    @Test
    public void testGood() {
        gnf.setRandom(new Random(42));
        output = gnf.generateMaskedRow(null);
        assertEquals("2490184631638 26", output); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() {
        gnf.setKeepEmpty(true);
        output = gnf.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        gnf.setRandom(new Random());
        boolean res = true;
        for (int i = 0; i < 10; i++) {
            String tmp = gnf.generateMaskedRow(null);
            res = (tmp.charAt(0) == '1' || tmp.charAt(0) == '2');
            assertTrue("wrong number : " + tmp, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gnf.keepNull = true;
        output = gnf.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void allDigitCanBeGenerated() {
        MockRandom random = new MockRandom();
        gnf.setRandom(random);
        output = gnf.generateMaskedRow(null);
        assertEquals("1020304005006 24", output);
    }

    @Test
    public void consistentMasking() {
        gnf.setSeed("aSeed");
        String result1 = gnf.doGenerateMaskedField("1020304005006 24", FunctionMode.CONSISTENT);
        String result2 = gnf.doGenerateMaskedField("1020304005006 24", FunctionMode.CONSISTENT);
        assertEquals(result2, result1);
    }
}
