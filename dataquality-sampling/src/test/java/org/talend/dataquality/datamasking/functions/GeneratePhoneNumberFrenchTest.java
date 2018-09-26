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
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GeneratePhoneNumberFrenchTest {

    private String output;

    private GeneratePhoneNumberFrench gpn = new GeneratePhoneNumberFrench();

    @Before
    public void setUp() throws Exception {
        gpn.setRandom(new Random(42));
    }

    @Test
    public void testGood() {
        output = gpn.generateMaskedRow(null);
        assertEquals("+33 938405589", output); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() {
        gpn.setKeepEmpty(true);
        output = gpn.generateMaskedRow("");
        assertEquals(output, ""); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        boolean res;
        gpn.setRandom(new Random());
        for (int i = 0; i < 10; i++) {
            String tmp = gpn.generateMaskedRow(null);
            int digit = Integer.valueOf(tmp.charAt(4) + "");
            res = (digit >= 1 && digit <= 9);
            assertTrue("invalid pĥone number " + tmp, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gpn.setKeepNull(true);
        output = gpn.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void allDigitCanBeGenerated() {
        MockRandom random = new MockRandom();
        gpn.setRandom(random);
        output = gpn.generateMaskedRow(null);
        assertEquals("+33 112345678", output);
        random.setNext(1);
        output = gpn.generateMaskedRow(null);
        assertEquals("+33 223456789", output);
    }
}
