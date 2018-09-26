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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.utils.MockRandom;

/**
 * created by jgonzalez on 20 août 2015 Detailled comment
 *
 */
public class GeneratePhoneNumberGermanyTest {

    private String output;

    private GeneratePhoneNumberGermany gpng = new GeneratePhoneNumberGermany();

    @Before
    public void setUp() throws Exception {
        gpng.setRandom(new Random(42));
    }

    @Test
    public void testGood() {
        output = gpng.generateMaskedRow(null);
        assertEquals("069 38405589", output); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() {
        gpng.setKeepEmpty(true);
        output = gpng.generateMaskedRow("");
        assertEquals(output, ""); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        boolean res;
        gpng.setRandom(new Random());
        for (int i = 0; i < 10; i++) {
            String tmp = gpng.generateMaskedRow(null);
            String prefix = tmp.substring(0, 3);
            res = ("030".equals(prefix) || "040".equals(prefix) || "069".equals(prefix) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    || "089".equals(prefix)); //$NON-NLS-1$
            assertTrue("invalid pĥone number " + tmp, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gpng.setKeepNull(true);
        output = gpng.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void allDigitCanBeGenerated() {
        MockRandom random = new MockRandom();
        gpng.setRandom(random);
        output = gpng.generateMaskedRow(null);
        assertEquals("030 12345678", output);
        random.setNext(1);
        output = gpng.generateMaskedRow(null);
        assertEquals("040 23456789", output);
    }
}
