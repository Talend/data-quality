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

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FormatPreservingMethod;

import static org.junit.Assert.*;

/**
 * @author jteuladedenantes
 */

public class GenerateUniqueSsnUkTest {

    private String output;

    private AbstractGenerateUniqueSsn gnu = new GenerateUniqueSsnUk();

    @Before
    public void setUp() throws Exception {
        gnu.setRandom(new Random(42));
        gnu.setSecret(FormatPreservingMethod.BASIC.name(), "");
        gnu.setKeepFormat(true);
    }

    @Test
    public void testEmpty() {
        gnu.setKeepEmpty(true);
        output = gnu.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testKeepInvalidPatternTrue() {
        gnu.setKeepInvalidPattern(true);
        output = gnu.generateMaskedRow("AHDBNSKD");
        assertEquals("AHDBNSKD", output);
    }

    @Test
    public void outputsNullWhenInputNull() {
        gnu.setKeepInvalidPattern(false);
        output = gnu.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void outputsNullWhenInputEmpty() {
        gnu.setKeepInvalidPattern(false);
        output = gnu.generateMaskedRow("");
        assertNull(output);
    }

    @Test
    public void testGood1() {
        output = gnu.generateMaskedRow("AL 486934 D");
        assertTrue(gnu.isValid(output));
        assertEquals("TG 807846 D", output);
    }

    @Test
    public void testGood2() {
        output = gnu.generateMaskedRow("PP132459A ");
        assertTrue(gnu.isValid(output));
        assertEquals("NJ207147A ", output);
    }

    @Test
    public void testWrongSsnFieldNumber() {
        gnu.setKeepInvalidPattern(false);
        // without a number
        output = gnu.generateMaskedRow("PP13259A");
        assertNull(output);
    }

    @Test
    public void testWrongSsnFieldForbiddenD() {
        gnu.setKeepInvalidPattern(false);
        // with the forbidden letter D
        output = gnu.generateMaskedRow("LO 486934 A");
        assertNull(output);
    }

    @Test
    public void testWrongSsnFieldForbiddenNK() {
        gnu.setKeepInvalidPattern(false);
        // with the forbidden letters NK
        output = gnu.generateMaskedRow("NK 486934 B");
        assertNull(output);
    }

    @Test
    public void unreproducibleWhenNoPasswordSet() {
        String input = "AL 486934 D";
        gnu.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF.name(), "");
        String result1 = gnu.generateMaskedRow(input);

        gnu.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF.name(), "");
        String result2 = gnu.generateMaskedRow(input);

        assertNotEquals(String.format("The result should not be reproducible when no password is set. Input value is %s.", input),
                result1, result2);
    }
}
