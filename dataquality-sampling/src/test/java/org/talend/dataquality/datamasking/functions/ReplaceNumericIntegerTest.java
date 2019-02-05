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

import java.util.Arrays;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceNumericIntegerTest {

    private int input = 123;

    private int output;

    private ReplaceNumericInteger rni = new ReplaceNumericInteger();

    @Before
    public void setUp() throws Exception {
        rni.setRandom(new Random(42));
    }

    @Test
    public void defaultBehavior() {
        rni.parse("6", false);
        output = rni.generateMaskedRow(input);
        assertEquals(666, output);
    }

    @Test
    public void random() {
        rni.parse("6", false);
        output = rni.generateMaskedRow(input, FunctionMode.RANDOM);
        assertEquals(666, output);
    }

    @Test
    public void nullParameter() {
        rni.parse(null, false);
        output = rni.generateMaskedRow(input);
        assertEquals(38, output);
    }

    @Test
    public void consistent() {
        rni.parse(" ", false);
        output = rni.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, (int) rni.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void consistentNoSeed() {
        rni.setRandom(null);
        rni.parse(" ", false);
        output = rni.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, (int) rni.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void letterInParameter() {
        try {
            rni.parse("r", false);
            fail("should get exception with input " + Arrays.toString(rni.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = rni.generateMaskedRow(input);
        assertEquals(0, output);
    }

    @Test
    public void twoDigitsInParameter() {
        try {
            rni.parse("10", false);
            fail("should get exception with input " + Arrays.toString(rni.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = rni.generateMaskedRow(input);
        assertEquals(0, output);
    }

}
