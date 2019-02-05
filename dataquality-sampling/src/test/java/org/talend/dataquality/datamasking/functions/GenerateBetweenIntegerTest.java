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

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateBetweenIntegerTest {

    private String output;

    private GenerateBetweenInteger gbi = new GenerateBetweenInteger();

    @Before
    public void setUp() throws Exception {
        gbi.setRandom(new Random(42L));
    }

    @Test
    public void testGood() {
        gbi.parse("10,20", false); //$NON-NLS-1$
        output = gbi.generateMaskedRow(null).toString();
        assertEquals(output, "17"); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        gbi.setRandom(null);
        gbi.parameters = "0,100".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        boolean res;
        for (int i = 0; i < 10; i++) {
            int tmp = gbi.generateMaskedRow(null);
            res = (tmp <= 100 && tmp >= 0);
            assertTrue("Wrong number : " + tmp, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testBad() {
        gbi.parameters = "jk,df".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = gbi.generateMaskedRow(0).toString();
        assertEquals(output, "0"); //$NON-NLS-1$
    }

    @Test
    public void testWrongParameter() {
        gbi.parse(null, false); // $NON-NLS-1$
        output = gbi.generateMaskedRow(null).toString();
        assertEquals(output, "0"); //$NON-NLS-1$
    }

}
