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
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 1 juil. 2015 Detailled comment
 *
 */
public class ReplaceLastCharsLongTest {

    private long output;

    private Long input = 123456L;

    private ReplaceLastCharsLong rlcl = new ReplaceLastCharsLong();

    @Test
    public void testGood() {
        rlcl.parse("3", false, new Random(42));
        output = rlcl.generateMaskedRow(input);
        assertEquals(123038, output); // $NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        rlcl.parse("7", false, new Random(42));
        output = rlcl.generateMaskedRow(input);
        assertEquals(38405, output); // $NON-NLS-1$
    }

    @Test
    public void testParameters() {
        rlcl.parse("4,9", false, new Random(42));
        output = rlcl.generateMaskedRow(input);
        assertEquals(129999, output); // $NON-NLS-1$
    }

    @Test
    public void testWrongParameters() {
        try {
            rlcl.parse("0,x", false, new Random(42));
            fail("should get exception with input " + rlcl.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        assertEquals(0L, output); // $NON-NLS-1$
    }

    @Test
    public void consistent() {
        rlcl.parse("3", false, new RandomWrapper(42));
        output = rlcl.generateMaskedRow(input, true);
        assertEquals(output, (long) rlcl.generateMaskedRow(input, true));
    }

    @Test
    public void consistentNoSeed() {
        rlcl.parse("3", false, new RandomWrapper());
        output = rlcl.generateMaskedRow(input, true);
        assertEquals(output, (long) rlcl.generateMaskedRow(input, true));
    }

}
