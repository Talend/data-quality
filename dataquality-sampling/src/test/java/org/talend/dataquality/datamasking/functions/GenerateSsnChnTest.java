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
        gnf.setRandom(new Random());
        boolean res = true;
        for (int i = 0; i < 10; ++i) {
            String tmp = gnf.generateMaskedRow(null);
            res = !(tmp.charAt(0) == '9');
            assertEquals("wrong number : " + tmp, res, true); //$NON-NLS-1$
        }
    }

    @Test
    public void testCheckYear() {
        // Year should be between 1900 and 2100
        gnf.setRandom(new Random());
        boolean res = true;
        for (int i = 0; i < 10; ++i) {
            String tmp = gnf.generateMaskedRow(null);
            int yyyy = Integer.valueOf(tmp.substring(6, 10));
            res = (yyyy >= 1900 && yyyy < 2100);
            assertEquals("wrong year : " + yyyy, res, true); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gnf.keepNull = true;
        output = gnf.generateMaskedRow(null);
        assertEquals(null, output);
    }
}
