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
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateAccountNumberSimpleTest {

    private String output;

    private GenerateAccountNumberSimple gans = new GenerateAccountNumberSimple();

    @Before
    public void setUp() throws Exception {
        gans.setRandom(new Random(42));
    }

    @Test
    public void testEmpty() {
        gans.setKeepFormat(true);
        gans.setKeepEmpty(true);
        output = gans.generateMaskedRow(Function.EMPTY_STRING); // $NON-NLS-1$
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        output = gans.generateMaskedRow(Function.EMPTY_STRING);
        assertEquals("FR54 0384 0558 93A2 20ZR 3V86 K48", output); //$NON-NLS-1$
    }

    @Test
    public void belgianIBan() {
        output = gans.generateIban("BE63507336157537").toString();
        assertEquals("BE08038405589322", output); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gans.keepNull = true;
        output = gans.generateMaskedRow(null);
        assertEquals(null, output);
    }

}
