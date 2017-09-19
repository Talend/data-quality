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

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class RemoveFirstCharsLongTest {

    private long input = 666L;

    private long output;

    private RemoveFirstCharsLong rfci = new RemoveFirstCharsLong();

    @Test
    public void test() throws DQException {
        rfci.parse("2", false, new Random(42));
        output = rfci.generateMaskedRow(input);
        assertEquals(6, output);
    }

    @Test
    public void testDummyGood() throws DQException {
        rfci.parse("10", false, new Random(42));
        output = rfci.generateMaskedRow(input);
        assertEquals(0, output);
    }

    @Test
    public void testNegativeParameter() throws DQException {
        try {
            rfci.parse("-10", false, new Random(42));
            fail("should get exception with input " + rfci.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = rfci.generateMaskedRow(input);
        assertEquals(0, output);
    }

}
