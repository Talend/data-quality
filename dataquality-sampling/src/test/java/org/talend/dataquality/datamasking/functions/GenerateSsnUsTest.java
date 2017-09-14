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
public class GenerateSsnUsTest {

    private String output;

    private GenerateSsnUs gsus = new GenerateSsnUs();

    @Before
    public void setUp() throws Exception {
        gsus.setRandom(new Random(42));
    }

    @Test
    public void testEmpty() {
        gsus.setKeepEmpty(true);
        output = gsus.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        output = gsus.generateMaskedRow(null);
        assertEquals(output, "530-80-7527"); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        gsus.setRandom(new Random());
        boolean res = true;
        for (int i = 0; i < 10; ++i) {
            String tmp = gsus.generateMaskedRow(null);
            res = (tmp.charAt(0) != '9' && tmp.charAt(4) == '0' ? tmp.charAt(5) != '0' : tmp.charAt(5) != '9');
            assertEquals("wrong number : " + tmp, res, true); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gsus.keepNull = true;
        output = gsus.generateMaskedRow(null);
        assertEquals(output, null);
    }
}
