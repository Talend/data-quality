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

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class BetweenIndexesReplaceTest {

    private String input = "Steve"; //$NON-NLS-1$

    private String output;

    private BetweenIndexesReplace bir = new BetweenIndexesReplace();

    @Before
    public void tearUp() {
        bir.setRandom(new Random(42));
    }

    @Test
    public void testGood() {
        bir.parse("2, 4, X", false, new Random(42));
        output = bir.generateMaskedRow(input);
        assertEquals("SXXXe", output); //$NON-NLS-1$
    }

    @Test
    public void consistent() {
        bir.parse("2, 4", false, new RandomWrapper(42));
        output = bir.generateMaskedRow(input, true);
        assertEquals(output, bir.generateMaskedRow(input, true)); //$NON-NLS-1$
    }

    @Test
    public void consistentNoSeed() {
        bir.parse("2, 4", false, new RandomWrapper());
        output = bir.generateMaskedRow(input, true);
        assertEquals(output, bir.generateMaskedRow(input, true)); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() {
        bir.parse("2, 4, X", false, new Random(42));
        output = bir.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        bir.parse("1, 8", false, new Random(42));
        output = bir.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        try {
            bir.parse("1", false, new Random(42));
            fail("should get exception with input " + bir.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bir.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testBad2() {
        try {
            bir.parse("lk, df", false, new Random(42));
            fail("should get exception with input " + bir.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bir.generateMaskedRow(input);
        assertEquals("", output);
    }

}
