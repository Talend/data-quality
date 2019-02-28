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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class GenerateCreditCardFormatStringTest {

    private String output;

    private GenerateCreditCardFormatString gccfs = new GenerateCreditCardFormatString();

    @Before
    public void setUp() throws Exception {
        gccfs.setRandom(new Random(42L));

    }

    @Test
    public void testGood() {
        String input = "4120356987563"; //$NON-NLS-1$
        output = gccfs.generateMaskedRow(input);
        assertEquals(output, String.valueOf(4038405589322L));
    }

    @Test
    public void testEmpty() {
        String input = ""; //$NON-NLS-1$
        gccfs.setKeepEmpty(true);
        output = gccfs.generateMaskedRow(input);
        assertEquals("", output);
    }

    @Test
    public void testSpaces() {
        gccfs.setKeepFormat(true);
        String input = "41 2 0356  9875 63"; //$NON-NLS-1$
        output = gccfs.generateMaskedRow(input);
        assertEquals(output, "40 3 8405  5893 22");
    }

    @Test
    public void testSpaces2() {
        String input = "41 2 0356  9875 63"; //$NON-NLS-1$
        output = gccfs.generateMaskedRow(input);
        assertEquals(output, "4038405589322");
    }

    @Test
    public void testCheck() {
        gccfs.setRandom(null);
        boolean res;
        for (int i = 0; i < 10; i++) {
            String tmp = gccfs.generateMaskedRow("4120356987563"); //$NON-NLS-1$
            res = GenerateCreditCard.luhnTest(new StringBuilder(tmp));
            assertTrue("Wrong number : " + tmp, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testBad() {
        output = gccfs.generateMaskedRow(null);
        assertEquals(output, "4384055893226268"); //$NON-NLS-1$
    }

    @Test
    public void consistentMasking() {
        gccfs.setSeed("aSeed");
        String result1 = gccfs.doGenerateMaskedField("4384055893226268", FunctionMode.CONSISTENT);
        String result2 = gccfs.doGenerateMaskedField("4384055893226268", FunctionMode.CONSISTENT);
        assertEquals(result2, result1);
    }

    @Test
    public void consistentMaskingWithDifferentValues() {
        gccfs.setSeed("azer1!");
        String input = "5380189322275031"; //$NON-NLS-1$
        String input2 = "5391569788142648";
        String result1 = gccfs.doGenerateMaskedField(input, FunctionMode.CONSISTENT);
        String result2 = gccfs.doGenerateMaskedField(input2, FunctionMode.CONSISTENT);
        assertNotEquals(result2, result1);
    }
}
