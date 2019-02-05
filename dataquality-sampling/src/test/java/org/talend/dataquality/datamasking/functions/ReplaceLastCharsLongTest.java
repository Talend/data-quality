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
 * created by jgonzalez on 1 juil. 2015 Detailled comment
 *
 */
public class ReplaceLastCharsLongTest {

    private long output;

    private Long input = 123456L;

    private ReplaceLastCharsLong rlcl = new ReplaceLastCharsLong();

    @Before
    public void setUp() throws Exception {
        rlcl.setRandom(new Random(42));
    }

    @Test
    public void defaultBehavior() {
        rlcl.parse("3", false);
        output = rlcl.generateMaskedRow(input);
        assertEquals(123038, output); // $NON-NLS-1$
    }

    @Test
    public void random() {
        rlcl.parse("3", false);
        output = rlcl.generateMaskedRow(input, FunctionMode.RANDOM);
        assertEquals(123038, output); // $NON-NLS-1$
    }

    @Test
    public void dummyHighParameter() {
        rlcl.parse("7", false);
        output = rlcl.generateMaskedRow(input);
        assertEquals(38405, output); // $NON-NLS-1$
    }

    @Test
    public void twoParameters() {
        rlcl.parse("4,9", false);
        output = rlcl.generateMaskedRow(input);
        assertEquals(129999, output); // $NON-NLS-1$
    }

    @Test
    public void letterInParameters() {
        try {
            rlcl.parse("0,x", false);
            fail("should get exception with input " + Arrays.toString(rlcl.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        assertEquals(0L, output); // $NON-NLS-1$
    }

    @Test
    public void consistent() {
        rlcl.parse("3", false);
        output = rlcl.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, (long) rlcl.generateMaskedRow(input, FunctionMode.CONSISTENT));
    }

    @Test
    public void consistentNoSeed() {
        rlcl.setRandom(null);
        rlcl.parse("3", false);
        output = rlcl.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, (long) rlcl.generateMaskedRow(input, FunctionMode.CONSISTENT));
    }

}
