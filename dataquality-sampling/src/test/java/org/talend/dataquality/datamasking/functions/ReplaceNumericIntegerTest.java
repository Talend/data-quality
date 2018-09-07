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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceNumericIntegerTest {

    private int input = 123;

    private int output;

    private ReplaceNumericInteger rni = new ReplaceNumericInteger();

    @Test
    public void testGood() {
        rni.parse("6", false, new Random(42));
        output = rni.generateMaskedRow(input);
        assertEquals(666, output);
    }

    @Test
    public void testNullParameter() {
        rni.parse(null, false, new Random(42));
        output = rni.generateMaskedRow(input);
        assertEquals(38, output);
    }

    @Test
    public void testWrongParameter() {
        try {
            rni.parse("r", false, new Random(42));
            fail("should get exception with input " + rni.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = rni.generateMaskedRow(input);
        assertEquals(0, output);
    }

    @Test
    public void testBad() {
        try {
            rni.parse("10", false, new Random(42));
            fail("should get exception with input " + rni.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = rni.generateMaskedRow(input);
        assertEquals(0, output);
    }

}
