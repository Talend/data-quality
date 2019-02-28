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

public class BetweenIndexesRemoveTest {

    private BetweenIndexesRemove bir = new BetweenIndexesRemove();

    private String input = "Steve"; //$NON-NLS-1$

    private String output;

    @Before
    public void setUp() {
        bir.setRandom(new Random(42L));
    }

    @Test
    public void defaultBehavior() {
        bir.parse("2, 4", false);
        output = bir.generateMaskedRow(input);
        assertEquals("Se", output); //$NON-NLS-1$
    }

    @Test
    public void emptyReturnsEmpty() {
        bir.parse("2, 4", false);
        output = bir.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void negativeParameter() {
        try {
            bir.parse("-2, 8", false);
            fail("should get exception with input " + Arrays.toString(bir.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bir.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void swappedParameters() {
        try {
            bir.parse("4, 2", false);
            fail("should get exception with input " + Arrays.toString(bir.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bir.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void tooFewParameters() {
        try {
            bir.parse("1", false);
            fail("should get exception with input " + Arrays.toString(bir.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bir.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void dummyHighParameters() {
        bir.parse("423,452", false);
        output = bir.generateMaskedRow(input);
        assertEquals(input, output);
    }
}
