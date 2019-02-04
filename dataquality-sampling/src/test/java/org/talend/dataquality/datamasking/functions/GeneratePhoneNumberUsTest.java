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
import org.talend.dataquality.utils.MockRandom;

/**
 * created by jgonzalez on 19 août 2015 Detailled comment
 *
 */
public class GeneratePhoneNumberUsTest {

    private String output;

    private GeneratePhoneNumberUS gpnus = new GeneratePhoneNumberUS();

    @Before
    public void setUp() throws Exception {
        gpnus.setRandom(new Random(42));
    }

    @Test
    public void testEmpty() {
        gpnus.setKeepEmpty(true);
        output = gpnus.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        output = gpnus.generateMaskedRow(null);
        assertEquals("738-205-5893", output); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        boolean res;
        gpnus.setRandom(new Random());
        for (int i = 0; i < 10; i++) {
            String tmp = gpnus.generateMaskedRow(null);
            res = (tmp.charAt(0) != '0' && tmp.charAt(1) != tmp.charAt(2) && tmp.charAt(4) != '0');
            assertTrue("invalid pĥone number " + tmp, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gpnus.keepNull = true;
        output = gpnus.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void allDigitCanBeGenerated() {
        MockRandom random = new MockRandom();
        gpnus.setRandom(random);
        output = gpnus.generateMaskedRow(null);
        assertEquals("212-545-6789", output);
        output = gpnus.generateMaskedRow(null);
        assertEquals("412-745-6789", output);
        output = gpnus.generateMaskedRow(null);
        assertEquals("612-945-6789", output);
    }

    @Test
    public void consistentMasking() {
        gpnus.setSeed("aSeed");
        String result1 = gpnus.doGenerateMaskedField("612-945-6789", FunctionMode.CONSISTENT);
        String result2 = gpnus.doGenerateMaskedField("612-945-6789", FunctionMode.CONSISTENT);
        assertEquals(result2, result1);
    }

}
