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
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceNumericLongTest {

    private long input = 123;

    private long output;

    private ReplaceNumericLong rnl = new ReplaceNumericLong();

    @Before
    public void setUp() throws Exception {
        rnl.setRandom(new Random(42));
    }

    @Test
    public void defaultBehavior() {
        rnl.parse("6", false);
        output = rnl.generateMaskedRow(input);
        assertEquals(666, output);
    }

    @Test
    public void random() {
        rnl.parse("6", false);
        output = rnl.generateMaskedRow(input, FunctionMode.RANDOM);
        assertEquals(666, output);
    }

    @Test
    public void emptyParameter() {
        rnl.parse(" ", false);
        output = rnl.generateMaskedRow(input);
        assertEquals(38, output);
    }

    @Test
    public void consistent() {
        rnl.parse(" ", false);
        output = rnl.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, (long) rnl.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void consistentNoSeed() {
        rnl.setRandom(null);
        rnl.parse(" ", false);
        output = rnl.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, (long) rnl.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void twoDigitsInParameters() {
        try {
            rnl.parse("10", false);
            fail("should get exception with input " + Arrays.toString(rnl.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
    }
}
