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
        assertEquals(output, "069 30807527"); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() {
        gpng.setKeepEmpty(true);
        output = gpng.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        boolean res = true;
        gpng.setRandom(new Random());
        for (int i = 0; i < 10; ++i) {
            String tmp = gpng.generateMaskedRow(null);
            res = (tmp.substring(0, 3).equals("030") || tmp.substring(0, 3).equals("040") || tmp.substring(0, 3).equals("069") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    || tmp.substring(0, 3).equals("089")); //$NON-NLS-1$
            assertEquals("invalid pĥone number " + tmp, res, true); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gpng.setKeepNull(true);
        output = gpng.generateMaskedRow(null);
        assertEquals(output, null);
    }

}
