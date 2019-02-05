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
public class GenerateBetweenLongTest {

    private String output;

    private GenerateBetweenLong gbl = new GenerateBetweenLong();

    @Before
    public void setUp() throws Exception {
        gbl.setRandom(new Random(42L));
    }

    @Test
    public void testGood() {
        gbl.parse("10,20", false); //$NON-NLS-1$
        output = gbl.generateMaskedRow(0L).toString();
        assertEquals(output, "17"); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        gbl.setRandom(null);
        gbl.parameters = "0,100".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        boolean res;
        for (int i = 0; i < 10; i++) {
            long tmp = gbl.generateMaskedRow(null);
            res = (tmp <= 100 && tmp >= 0);
            assertTrue("Wrong number : " + tmp, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testBad() {
        gbl.parameters = "jk,df".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = gbl.generateMaskedRow(0L).toString();
        assertEquals(output, "0"); //$NON-NLS-1$
    }

}
