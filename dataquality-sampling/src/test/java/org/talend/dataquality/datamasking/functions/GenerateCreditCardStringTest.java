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
import org.talend.dataquality.sampling.exception.DQException;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 */
public class GenerateCreditCardStringTest {

    private String output;

    private GenerateCreditCardString gccs = new GenerateCreditCardString();

    @Before
    public void setUp() throws Exception {
        gccs.setRandom(new Random(42));
    }

    @Test
    public void test() throws DQException {
        output = gccs.generateMaskedRow(null).toString();
        assertEquals(output, "4384055893226268"); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() throws DQException {
        gccs.setKeepEmpty(true);
        output = gccs.generateMaskedRow("").toString();
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testCheck() throws DQException {
        gccs.setRandom(new Random());
        boolean res = true;
        for (int i = 0; i < 10; ++i) {
            String tmp = gccs.generateMaskedRow(null);
            res = GenerateCreditCard.luhnTest(new StringBuilder(tmp));
            assertEquals("Wrong number : " + tmp, res, true); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() throws DQException {
        gccs.keepNull = true;
        output = String.valueOf(gccs.generateMaskedRow(null));
        assertEquals(output, "null"); //$NON-NLS-1$
    }

}
