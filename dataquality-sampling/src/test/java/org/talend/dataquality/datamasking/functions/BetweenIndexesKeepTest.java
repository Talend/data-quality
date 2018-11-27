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

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class BetweenIndexesKeepTest {

    private BetweenIndexesKeep bik = new BetweenIndexesKeep();

    private String input = "Steve"; //$NON-NLS-1$

    private String output;

    @Test
    public void testGood() {
        bik.parse("2, 4", false, new Random(42));
        output = bik.generateMaskedRow(input);
        assertEquals("tev", output); //$NON-NLS-1$
    }

    @Test
    public void testGood2() {
        bik.parse("1, 2", false, new Random(42));
        output = bik.generateMaskedRow(input);
        assertEquals("St", output); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() {
        bik.parse("2, 4", false, new Random(42));
        output = bik.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testWrongParameter() {
        try {
            bik.parse("0, 8", false, new Random(42));
            fail("should get exception with input " + bik.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bik.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        try {
            bik.parse("1", false, new Random(42));
            fail("should get exception with input " + bik.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bik.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testBad2() {
        try {
            bik.parse("lk, df", false, new Random(42));
            fail("should get exception with input " + bik.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bik.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testDummyParameters() {
        bik.parse("423,452", false, new Random(42));
        output = bik.generateMaskedRow(input);
        assertEquals("", output);
    }

    @Test
    public void TDP6249() {
        bik.parse("5,452", false, new Random(42));
        output = bik.generateMaskedRow(input);
        assertEquals("e", output);
    }

    @Test
    public void testWithNullInput() {
        bik.parse("5,452", false, new Random(42));
        output = bik.generateMaskedRow(null);
        assertEquals("", output);
    }

    @Test
    public void testWithMultipleInputs() {
        bik.parse("5,452", false, new Random(42));
        output = bik.generateMaskedRow(input);
        String output2 = bik.generateMaskedRow("SteveDo");
        assertEquals("e", output);
        assertEquals("eDo", output2);
    }
}
