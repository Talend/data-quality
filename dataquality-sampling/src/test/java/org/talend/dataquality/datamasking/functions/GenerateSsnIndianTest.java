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
import org.talend.dataquality.utils.MockRandom;

/**
 * @author dprot
 *
 */
public class GenerateSsnIndianTest {

    private String output;

    private GenerateSsnIndia gni = new GenerateSsnIndia();

    @Before
    public void setUp() throws Exception {
        gni.setRandom(new Random(42));
    }

    @Test
    public void testGood() {
        output = gni.generateMaskedRow(null);
        assertEquals("938405589326", output); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() {
        gni.setKeepEmpty(true);
        output = gni.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testCheckFirstDigit() {
        // First digit should not be a '0' in a Indian SSN
        gni.setRandom(new Random());
        boolean res;
        for (int i = 0; i < 10; i++) {
            String tmp = gni.generateMaskedRow(null);
            res = '0' != tmp.charAt(0);
            assertTrue("wrong number : " + tmp, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gni.keepNull = true;
        output = gni.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void allDigitCanBeGenerated() {
        MockRandom random = new MockRandom();
        gni.setRandom(random);
        output = gni.generateMaskedRow(null);
        assertEquals("112345678907", output);
    }
}
