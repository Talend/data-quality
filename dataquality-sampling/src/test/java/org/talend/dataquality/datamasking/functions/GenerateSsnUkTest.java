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
 * created by jgonzalez on 20 août 2015 Detailled comment
 *
 */
public class GenerateSsnUkTest {

    private String output;

    private GenerateSsnUk gsuk = new GenerateSsnUk();

    @Before
    public void setUp() throws Exception {
        gsuk.setRandom(new Random(42));
    }

    @Test
    public void testEmpty() {
        gsuk.setKeepEmpty(true);
        output = gsuk.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        output = gsuk.generateMaskedRow(null);
        assertEquals("HH 84 05 58 C", output); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        gsuk.setRandom(new Random());
        boolean res = true;
        for (int i = 0; i < 10; i++) {
            String tmp = gsuk.generateMaskedRow(null);
            res = Function.UPPER.substring(0, 4).indexOf(tmp.charAt(tmp.length() - 1)) != -1
                    && !GenerateSsnUk.getForbid().contains(tmp.substring(0, 2));
            assertTrue("wrong number : " + tmp, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gsuk.keepNull = true;
        output = gsuk.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void allDigitCanBeGenerated() {
        MockRandom random = new MockRandom();
        gsuk.setRandom(random);
        output = gsuk.generateMaskedRow(null);
        assertEquals("AZ 23 45 67 A", output);
        random.setNext(3);
        output = gsuk.generateMaskedRow(null);
        assertEquals("RT 56 78 90 D", output);
    }
}
