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
public class GeneratePhoneNumberJapanTest {

    private String output;

    private GeneratePhoneNumberJapan gpnj = new GeneratePhoneNumberJapan();

    @Before
    public void setUp() throws Exception {
        gpnj.setRandom(new Random(42));
    }

    @Test
    public void testGood() {
        output = gpnj.generateMaskedRow(null);
        assertEquals("03-0384-0558", output); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() {
        gpnj.setKeepEmpty(true);
        output = gpnj.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        boolean res;
        gpnj.setRandom(new Random());
        for (int i = 0; i < 10; i++) {
            String tmp = gpnj.generateMaskedRow(null);
            res = (tmp.charAt(0) == '0') && (tmp.charAt(1) == '3');
            assertTrue("invalid pĥone number " + tmp, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gpnj.keepNull = true;
        assertNull(output);
    }

    @Test
    public void allDigitCanBeGenerated() {
        MockRandom random = new MockRandom();
        gpnj.setRandom(random);
        output = gpnj.generateMaskedRow(null);
        assertEquals("03-0123-4567", output);
        random.setNext(2);
        output = gpnj.generateMaskedRow(null);
        assertEquals("03-2345-6789", output);
        random.setNext(6);
        output = gpnj.generateMaskedRow(null);
        assertEquals("03-6789-0123", output);
    }
}
