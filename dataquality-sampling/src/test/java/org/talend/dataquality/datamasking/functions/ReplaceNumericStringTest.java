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

import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceNumericStringTest {

    private String input = "abc123def"; //$NON-NLS-1$

    private String output;

    private ReplaceNumericString rns = new ReplaceNumericString();

    @Test
    public void defaultBehavior() {
        rns.parse("0", false, new Random(42));
        output = rns.generateMaskedRow(input);
        assertEquals("abc000def", output); //$NON-NLS-1$
    }

    @Test
    public void random() {
        rns.parse("0", false, new Random(42));
        output = rns.generateMaskedRow(input, FunctionMode.RANDOM);
        assertEquals("abc000def", output); //$NON-NLS-1$
    }

    @Test
    public void emptyReturnsEmpty() {
        rns.setKeepEmpty(true);
        output = rns.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void emptyParameterWorks() {
        rns.parse(" ", false, new Random(42));
        output = rns.generateMaskedRow(input);
        assertEquals("abc038def", output); //$NON-NLS-1$
    }

    @Test
    public void consistent() {
        rns.parse(" ", false, new RandomWrapper(42));
        output = rns.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rns.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void consistentNoSeed() {
        rns.parse(" ", false, new RandomWrapper());
        output = rns.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rns.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void letterInParameter() {
        try {
            rns.parse("0X", false, new Random(42));
            fail("should get exception with input " + Arrays.toString(rns.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
    }
}
