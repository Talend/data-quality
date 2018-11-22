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
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class ReplaceLastCharsStringTest {

    private String output;

    private String input = "123456";

    private ReplaceLastCharsString rlcs = new ReplaceLastCharsString();

    @Test
    public void testGood() {
        rlcs.parse("3", false, new Random(42));
        output = rlcs.generateMaskedRow(input);
        assertEquals(output, "123038");
    }

    @Test
    public void testEmpty() {
        rlcs.setKeepEmpty(true);
        output = rlcs.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        rlcs.parse("7", false, new Random(42));
        output = rlcs.generateMaskedRow(input);
        assertEquals(output, "038405");
    }

    @Test
    public void consistent() {
        rlcs.parse("3", false, new RandomWrapper(42));
        output = rlcs.generateMaskedRow(input, true);
        assertEquals(output, rlcs.generateMaskedRow(input, true));
    }

    @Test
    public void consistentNoSeed() {
        rlcs.parse("3", false, new RandomWrapper());
        output = rlcs.generateMaskedRow(input, true);
        assertEquals(output, rlcs.generateMaskedRow(input, true));
    }
}
