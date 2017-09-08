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

import org.junit.Test;
import org.talend.dataquality.sampling.exception.DQException;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class RemoveFirstCharsStringTest {

    private String input = "Steve"; //$NON-NLS-1$

    private String output;

    private RemoveFirstCharsString rfcs = new RemoveFirstCharsString();

    @Test
    public void testEmpty() throws DQException {
        rfcs.setKeepEmpty(true);
        output = rfcs.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void test() throws DQException {
        rfcs.parse("2", false, new Random(42));
        output = rfcs.generateMaskedRow(input);
        assertEquals(output, "eve"); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() throws DQException {
        rfcs.parse("10", false, new Random(42));
        output = rfcs.generateMaskedRow(input);
        assertEquals(output, Function.EMPTY_STRING); // $NON-NLS-1$
    }

    @Test
    public void testParameterToLong() throws DQException {
        rfcs.parse("10000", false, new Random(42));
        output = rfcs.generateMaskedRow(input);
        assertEquals(output, Function.EMPTY_STRING);
    }
}
