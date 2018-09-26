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

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class GenerateCreditCardFormatStringTest {

    private String output;

    private GenerateCreditCardFormatString gccfs = new GenerateCreditCardFormatString();

    @Before
    public void setUp() throws Exception {
        gccfs.setRandom(new Random(42));

    }

    @Test
    public void testGood() {
        String input = "4120356987563"; //$NON-NLS-1$
        output = gccfs.generateMaskedRow(input).toString();
        assertEquals(output, String.valueOf(4038405589322L));
    }

    @Test
    public void testEmpty() {
        String input = ""; //$NON-NLS-1$
        gccfs.setKeepEmpty(true);
        output = gccfs.generateMaskedRow(input).toString();
        assertEquals("", output);
    }

    @Test
    public void testSpaces() {
        gccfs.setKeepFormat(true);
        String input = "41 2 0356  9875 63"; //$NON-NLS-1$
        output = gccfs.generateMaskedRow(input).toString();
        assertEquals(output, "40 3 8405  5893 22");
    }

    @Test
    public void testSpaces2() {
        String input = "41 2 0356  9875 63"; //$NON-NLS-1$
        output = gccfs.generateMaskedRow(input).toString();
        assertEquals(output, "4038405589322");
    }

    @Test
    public void testCheck() {
        gccfs.setRandom(new Random());
        boolean res = true;
        for (int i = 0; i < 10; i++) {
            String tmp = gccfs.generateMaskedRow("4120356987563"); //$NON-NLS-1$
            res = GenerateCreditCard.luhnTest(new StringBuilder(tmp));
            assertEquals("Wrong number : " + tmp, res, true); //$NON-NLS-1$
        }
    }

    @Test
    public void testBad() {
        output = gccfs.generateMaskedRow(null).toString();
        assertEquals(output, "4384055893226268"); //$NON-NLS-1$
    }

}
