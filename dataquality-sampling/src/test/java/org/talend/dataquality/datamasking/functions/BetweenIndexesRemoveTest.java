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
import org.talend.dataquality.sampling.exception.DQException;

public class BetweenIndexesRemoveTest {

    private BetweenIndexesRemove bir = new BetweenIndexesRemove();

    private String input = "Steve"; //$NON-NLS-1$

    private String output;

    @Test
    public void testGood() throws DQException {
        bir.parse("2, 4", false, new Random(42));
        output = bir.generateMaskedRow(input);
        assertEquals("Se", output); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() throws DQException {
        bir.parse("2, 4", false, new Random(42));
        output = bir.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testNegativeParameter() throws DQException {
        try {
            bir.parse("-2, 8", false, new Random(42));
            fail("should get exception with input " + bir.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bir.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testSwitchParameter() throws DQException {
        try {
            bir.parse("4, 2", false, new Random(42));
            fail("should get exception with input " + bir.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bir.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testBad() throws DQException {
        try {
            bir.parse("1", false, new Random(42));
            fail("should get exception with input " + bir.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bir.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testDummyParameters() throws DQException {
        bir.parse("423,452", false, new Random(42));
        output = bir.generateMaskedRow(input);
        assertEquals(input, output);
    }

}
