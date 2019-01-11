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
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class ReplaceFirstCharsStringTest {

    private String output;

    private String input = "123456";

    private ReplaceFirstCharsString rfcs = new ReplaceFirstCharsString();

    @Test
    public void defaultBehavior() {
        rfcs.parse("3,y", false, new Random(42));
        output = rfcs.generateMaskedRow(input);
        assertEquals("yyy456", output);
    }

    @Test
    public void random() {
        rfcs.parse("3,y", false, new Random(42));
        output = rfcs.generateMaskedRow(input, FunctionMode.RANDOM);
        assertEquals("yyy456", output);
    }

    @Test
    public void consistent() {
        rfcs.parse("3", false, new RandomWrapper(42));
        output = rfcs.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rfcs.generateMaskedRow(input, FunctionMode.CONSISTENT));
    }

    @Test
    public void consistentNoSeed() {
        rfcs.parse("3", false, new RandomWrapper());
        output = rfcs.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rfcs.generateMaskedRow(input, FunctionMode.CONSISTENT));
    }

    @Test
    public void emptyReturnsEmpty() {
        rfcs.setKeepEmpty(true);
        output = rfcs.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void dummyHighParameter() {
        rfcs.parse("7", false, new Random(42));
        output = rfcs.generateMaskedRow(input);
        assertEquals("038405", output);
    }

    @Test
    public void letterInParameters() {
        try {
            rfcs.parse("0,xs", false, new Random(42));
            fail("should get exception with input " + Arrays.toString(rfcs.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = rfcs.generateMaskedRow(input);
        assertEquals("", output); // $NON-NLS-1$
    }

}
