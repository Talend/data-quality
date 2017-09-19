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
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class KeepLastCharsLongTest {

    private long output;

    private long input = 123456L;

    private KeepLastCharsLong klag = new KeepLastCharsLong();

    @Test
    public void testGood() throws DQException {
        klag.parse("3", false, new Random(42));
        output = klag.generateMaskedRow(input);
        assertEquals(830456L, output); // $NON-NLS-1$
    }

    @Test
    public void testDummyGood() throws DQException {
        klag.parse("7", false, new Random(42));
        output = klag.generateMaskedRow(input);
        assertEquals(input, output);
    }

    @Test
    public void testParameter() throws DQException {
        klag.parse("3,8", false, new Random(42));
        output = klag.generateMaskedRow(input);
        assertEquals(888456L, output);
    }

    @Test
    public void testWrongParameter() throws DQException {
        try {
            klag.parse("3,r", false, new Random(42));
            fail("should get exception with input " + klag.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = klag.generateMaskedRow(input);
        assertEquals(0L, output);
    }
}
