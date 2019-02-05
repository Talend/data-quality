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

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateAccountNumberFormatTest {

    private String output;

    private GenerateAccountNumberFormat ganf = new GenerateAccountNumberFormat();

    @Before
    public void setUp() throws Exception {
        ganf.setRandom(new Random(4245L));
    }

    @Test
    public void testGood() {
        ganf.setKeepFormat(true);
        output = ganf.generateMaskedRow("DK49 038 4 0 5 5 8   93  22 62"); //$NON-NLS-1$
        assertEquals("DK82 765 7 2 0 9 5   51  85 63", output); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() {
        ganf.setKeepFormat(true);
        ganf.setKeepEmpty(true);
        output = ganf.generateMaskedRow(""); //$NON-NLS-1$
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood2() {
        output = ganf.generateMaskedRow("DK49 038 4 0 5 5 8   93  22 62"); //$NON-NLS-1$
        assertEquals("DK82 7657 2095 5185 63", output); //$NON-NLS-1$
    }

    @Test
    public void testAmericanNumber() {
        output = ganf.generateMaskedRow("453 654 94 87 4684 687"); //$NON-NLS-1$
        assertEquals("453654948 7657209551", output); //$NON-NLS-1$
    }

    @Test
    public void testAmericanNumber2() {
        ganf.setKeepFormat(true);
        output = ganf.generateMaskedRow("453 654 94 87 58 425 6"); //$NON-NLS-1$
        assertEquals("453 654 94 87 65 720 9551", output); //$NON-NLS-1$
    }

    @Test
    public void testIsNotAmericanNumber() {
        output = ganf.generateMaskedRow("454344678 4536"); //$NON-NLS-1$
        assertEquals("FR33 7657 2095 51R3 4XZP 6F4O 058", output); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        output = ganf.generateMaskedRow("not an iban"); //$NON-NLS-1$
        assertEquals("FR33 7657 2095 51R3 4XZP 6F4O 058", output); //$NON-NLS-1$
    }
}
