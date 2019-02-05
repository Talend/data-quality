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

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;

/**
 * @author dprot
 */
public class GenerateSsnChnTest {

    private String output;

    private GenerateSsnChn gnf = new GenerateSsnChn();

    @Before
    public void setUp() throws Exception {
        gnf.setRandom(new Random(42));
    }

    @Test
    public void testEmpty() {
        gnf.setKeepEmpty(true);
        output = gnf.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        output = gnf.generateMaskedRow(null);
        assertEquals("610201206301240556", output); //$NON-NLS-1$
    }

    @Test
    public void testCheckFirstDigit() {
        // First digit should not be a '9' in a Chinese SSN
        gnf.setRandom(null);
        boolean res;
        for (int i = 0; i < 10; i++) {
            String tmp = gnf.generateMaskedRow(null);
            res = !(tmp.charAt(0) == '9');
            assertTrue("wrong number : " + tmp, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testCheckYear() {
        // Year should be between 1900 and 2100
        gnf.setRandom(new Random());
        boolean res = true;
        for (int i = 0; i < 10; i++) {
            String tmp = gnf.generateMaskedRow(null);
            int yyyy = Integer.valueOf(tmp.substring(6, 10));
            res = (yyyy >= 1900 && yyyy < 2100);
            assertTrue("wrong year : " + yyyy, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gnf.keepNull = true;
        output = gnf.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void consistentMasking() {
        gnf.setSeed("aSeed");
        String result1 = gnf.doGenerateMaskedField("610201206301240556", FunctionMode.CONSISTENT);
        String result2 = gnf.doGenerateMaskedField("610201206301240556", FunctionMode.CONSISTENT);
        assertEquals(result2, result1);
    }
}
