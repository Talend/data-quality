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
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateCreditCardLongTest {

    private String output;

    private GenerateCreditCardLong gccl = new GenerateCreditCardLong();

    @Before
    public void setUp() throws Exception {
        gccl.setRandom(new Random(42));
    }

    @Test
    public void test() {
        output = gccl.generateMaskedRow(null).toString();
        assertEquals(output, "4384055893226268"); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        gccl.setRandom(new Random());
        boolean res = true;
        for (int i = 0; i < 10; i++) {
            Long tmp = gccl.generateMaskedRow(null);
            res = GenerateCreditCard.luhnTest(new StringBuilder(tmp.toString()));
            assertEquals("Wrong number : " + tmp, res, true); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gccl.keepNull = true;
        output = String.valueOf(gccl.generateMaskedRow(null));
        assertEquals(output, "null"); //$NON-NLS-1$
    }
}
