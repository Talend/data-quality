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
 * @author dprot
 */
public class GenerateUniqueSsnGermanTest {

    private String output;

    private AbstractGenerateUniqueSsn gng = new GenerateUniqueSsnGermany();

    @Before
    public void setUp() throws Exception {
        gng.setRandom(new Random(42));
        gng.setSecret(FormatPreservingMethod.BASIC, "");
        gng.setKeepFormat(true);
    }

    @Test
    public void testEmpty() {
        gng.setKeepEmpty(true);
        output = gng.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testKeepInvalidPatternTrue() {
        gng.setKeepInvalidPattern(true);
        output = gng.generateMaskedRow("AHDBNSKD");
        assertEquals("AHDBNSKD", output);
    }

    @Test
    public void outputsNullWhenInputNull() {
        gng.setKeepInvalidPattern(false);
        output = gng.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void outputsNullWhenInputEmpty() {
        gng.setKeepInvalidPattern(false);
        output = gng.generateMaskedRow("");
        assertNull(output);
    }

    @Test
    public void testGood1() {
        output = gng.generateMaskedRow("83807527228");
        assertTrue(gng.isValid(output));
        assertEquals("79564837099", output);
    }

    @Test
    public void testGood2() {
        output = gng.generateMaskedRow("48695361449");
        assertTrue(gng.isValid(output));
        assertEquals("37088083197", output);
    }

    @Test
    public void testWrongSsnFieldNumber() {
        gng.setKeepInvalidPattern(false);
        // without a number
        output = gng.generateMaskedRow("8308072728");
        assertNull(output);
    }

    @Test
    public void testWrongSsnFieldLetter() {
        gng.setKeepInvalidPattern(false);
        // with a letter instead of a number
        output = gng.generateMaskedRow("8308752722P");
        assertNull(output);
    }

    @Test
    public void unreproducibleWhenNoPasswordSet() {
        String input = "83807527228";
        gng.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "");
        String result1 = gng.generateMaskedRow(input);

        gng.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "");
        String result2 = gng.generateMaskedRow(input);

        assertNotEquals(String.format("The result should not be reproducible when no password is set. Input value is %s.", input),
                result1, result2);
    }
}
