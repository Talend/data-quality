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
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class ReplaceFirstCharsIntegerTest {

    private int output;

    private int input = 123456;

    private ReplaceFirstCharsInteger rfci = new ReplaceFirstCharsInteger();

    @Before
    public void setUp() throws Exception {
        rfci.setRandom(new Random(42));
    }

    @Test
    public void defaultBehavior() {
        rfci.parse("3", false);
        output = rfci.generateMaskedRow(input);
        assertEquals(38456, output); // $NON-NLS-1$
    }

    @Test
    public void random() {
        rfci.parse("3", false);
        output = rfci.generateMaskedRow(input, FunctionMode.RANDOM);
        assertEquals(38456, output); // $NON-NLS-1$
    }

    @Test
    public void consistent() {
        rfci.parse("3", false);
        output = rfci.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, (int) rfci.generateMaskedRow(input, FunctionMode.CONSISTENT));
    }

    @Test
    public void consistentNoSeed() {
        rfci.setRandom(null);
        rfci.parse("3", false);
        output = rfci.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, (int) rfci.generateMaskedRow(input, FunctionMode.CONSISTENT));
    }

    @Test
    public void dummyHighParameter() {
        rfci.parse("154", false);
        output = rfci.generateMaskedRow(input);
        assertEquals(38405, output); // $NON-NLS-1$
    }

    @Test
    public void dummyLowParameter() {
        rfci.parse("0", false);
        output = rfci.generateMaskedRow(input);
        assertEquals(input, output); // $NON-NLS-1$
    }

    @Test
    public void letterInParameter() {
        try {
            rfci.parse("j", false);
            fail("should get exception with input " + Arrays.toString(rfci.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = rfci.generateMaskedRow(input);
        assertEquals(0, output); // $NON-NLS-1$
    }
}
