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

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class ReplaceLastCharsStringTest {

    private String output;

    private String input = "123456";

    private ReplaceLastCharsString rlcs = new ReplaceLastCharsString();

    @Before
    public void setUp() throws Exception {
        rlcs.setRandom(new Random(42));
    }

    @Test
    public void defaultBehavior() {
        rlcs.parse("3", false);
        output = rlcs.generateMaskedRow(input);
        assertEquals("123038", output);
    }

    @Test
    public void random() {
        rlcs.parse("3", false);
        output = rlcs.generateMaskedRow(input, FunctionMode.RANDOM);
        assertEquals("123038", output);
    }

    @Test
    public void emptyREturnsEmpty() {
        rlcs.setKeepEmpty(true);
        output = rlcs.generateMaskedRow("");
        assertEquals(output, ""); //$NON-NLS-1$
    }

    @Test
    public void dummyHighParameter() {
        rlcs.parse("7", false);
        output = rlcs.generateMaskedRow(input);
        assertEquals("038405", output);
    }

    @Test
    public void consistent() {
        rlcs.parse("3", false);
        output = rlcs.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rlcs.generateMaskedRow(input, FunctionMode.CONSISTENT));
    }

    @Test
    public void consistentNoSeed() {
        rlcs.setRandom(null);
        rlcs.parse("3", false);
        output = rlcs.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rlcs.generateMaskedRow(input, FunctionMode.CONSISTENT));
    }
}
