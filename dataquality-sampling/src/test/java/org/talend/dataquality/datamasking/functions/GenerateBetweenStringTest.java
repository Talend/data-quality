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

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateBetweenStringTest {

    private String output;

    private GenerateBetweenString gbs = new GenerateBetweenString();

    @Before
    public void setUp() throws Exception {
        gbs.setRandom(new Random(42L));
    }

    @Test
    public void testGood() {
        gbs.parse("10,20", false); //$NON-NLS-1$
        output = gbs.generateMaskedRow(Function.EMPTY_STRING);
        assertEquals(output, "17"); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        gbs.setRandom(null);
        gbs.parse("0,100", false); //$NON-NLS-1$
        boolean res;
        for (int i = 0; i < 10; i++) {
            String tmp = gbs.generateMaskedRow(null);
            int value = StringUtils.isBlank(tmp) ? 0 : Integer.parseInt(tmp);
            res = (value <= 100 && value >= 0);
            assertTrue("Wrong number : " + value, res); //$NON-NLS-1$
        }
    }

    @Test
    public void testBad() {
        gbs.parse("jk,df", false); //$NON-NLS-1$
        output = gbs.generateMaskedRow(Function.EMPTY_STRING);
        assertEquals(output, ""); //$NON-NLS-1$
    }

}
