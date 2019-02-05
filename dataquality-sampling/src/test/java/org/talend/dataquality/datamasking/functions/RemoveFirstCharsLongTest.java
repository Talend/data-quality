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

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class RemoveFirstCharsLongTest {

    private long input = 666L;

    private long output;

    private RemoveFirstCharsLong rfci = new RemoveFirstCharsLong();

    @Before
    public void setUp() throws Exception {
        rfci.setRandom(new Random(42));
    }

    @Test
    public void defaultBehavior() {
        rfci.parse("2", false);
        output = rfci.generateMaskedRow(input);
        assertEquals(6, output);
    }

    @Test
    public void dummyParameter() {
        rfci.parse("10", false);
        output = rfci.generateMaskedRow(input);
        assertEquals(0, output);
    }

    @Test
    public void negativeParameter() {
        try {
            rfci.parse("-10", false);
            fail("should get exception with input " + Arrays.toString(rfci.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = rfci.generateMaskedRow(input);
        assertEquals(0, output);
    }

}
