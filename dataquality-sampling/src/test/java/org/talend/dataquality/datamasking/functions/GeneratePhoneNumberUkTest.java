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
 * created by jgonzalez on 19 août 2015 Detailled comment
 *
 */
public class GeneratePhoneNumberUkTest {

    private String output;

    private GeneratePhoneNumberUK gpnuk = new GeneratePhoneNumberUK();

    @Before
    public void setUp() throws Exception {
        gpnuk.setRandom(new Random(42));
    }

    @Test
    public void testEmpty() {
        gpnuk.setKeepEmpty(true);
        output = gpnuk.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        output = gpnuk.generateMaskedRow(null);
        assertEquals("020 3038 4055", output); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        boolean res = true;
        gpnuk.setRandom(new Random());
        for (int i = 0; i < 10; i++) {
            String tmp = gpnuk.generateMaskedRow(null);
            res = (tmp.substring(0, 5).equals("020 3")); //$NON-NLS-1$
            assertTrue("invalid pĥone number " + tmp, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gpnuk.keepNull = true;
        output = gpnuk.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void allDigitCanBeGenerated() {
        MockRandom random = new MockRandom();
        gpnuk.setRandom(random);
        output = gpnuk.generateMaskedRow(null);
        assertEquals("020 3012 3456", output);
        output = gpnuk.generateMaskedRow(null);
        assertEquals("020 3789 0123", output);
        output = gpnuk.generateMaskedRow(null);
        assertEquals("020 3456 7890", output);
    }
}
