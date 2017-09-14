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
 * @author jteuladedenantes
 */
public class GenerateUniqueSsnUsTest {

    private String output;

    private AbstractGenerateUniqueSsn gnu = new GenerateUniqueSsnUs();

    @Before
    public void setUp() throws Exception {
        gnu.setRandom(new Random(42));
        gnu.setKeepFormat(true);
    }

    @Test
    public void testEmpty() {
        gnu.setKeepEmpty(true);
        output = gnu.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood1() {
        output = gnu.generateMaskedRow("153 65 4862");
        assertEquals("513 99 6374", output);
    }

    @Test
    public void testGood2() {
        output = gnu.generateMaskedRow("1 56 46 45 99");
        assertEquals("1 63 91 55 89", output);
    }

    @Test
    public void testWrongSsnFieldNumber() {
        gnu.setKeepInvalidPattern(false);
        // without a number
        output = gnu.generateMaskedRow("153 65 486");
        assertEquals(null, output);
    }

    @Test
    public void testWrongSsnField666() {
        gnu.setKeepInvalidPattern(false);
        // with the forbidden number 666
        output = gnu.generateMaskedRow("666 65 4862");
        assertEquals(null, output);
    }

    @Test
    public void testWrongSsnField00() {
        gnu.setKeepInvalidPattern(false);
        // with the forbidden number 00
        output = gnu.generateMaskedRow("153 00 4862");
        assertEquals(null, output);
    }
}
