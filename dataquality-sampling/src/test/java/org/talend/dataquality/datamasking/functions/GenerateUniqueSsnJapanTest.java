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
public class GenerateUniqueSsnJapanTest {

    private String output;

    private AbstractGenerateUniqueSsn gnj = new GenerateUniqueSsnJapan();

    @Before
    public void setUp() throws Exception {
        gnj.setRandom(new Random(42));
        gnj.setSecret(FormatPreservingMethod.BASIC.name(), "");
        gnj.setKeepFormat(true);
    }

    @Test
    public void testEmpty() {
        gnj.setKeepEmpty(true);
        output = gnj.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testKeepInvalidPatternTrue() {
        gnj.setKeepInvalidPattern(true);
        output = gnj.generateMaskedRow("AHDBNSKD");
        assertEquals("AHDBNSKD", output);
    }

    @Test
    public void outputsNullWhenInputNull() {
        gnj.setKeepInvalidPattern(false);
        output = gnj.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void outputsNullWhenInputEmpty() {
        gnj.setKeepInvalidPattern(false);
        output = gnj.generateMaskedRow("");
        assertNull(output);
    }

    @Test
    public void testGood1() {
        output = gnj.generateMaskedRow("830807527228");
        assertTrue(gnj.isValid(output));
        assertEquals("477564837070", output);
    }

    @Test
    public void testGood2() {
        output = gnj.generateMaskedRow("486953617449");
        assertTrue(gnj.isValid(output));
        assertEquals("370892787197", output);
    }

    @Test
    public void testWrongSsnFieldNumber() {
        gnj.setKeepInvalidPattern(false);
        // without a number
        output = gnj.generateMaskedRow("83080727228");
        assertNull(output);
    }

    @Test
    public void testWrongSsnFieldLetter() {
        gnj.setKeepInvalidPattern(false);
        // with a letter instead of a number
        output = gnj.generateMaskedRow("83080752722P");
        assertNull(output);
    }

    @Test
    public void unreproducibleWhenNoPasswordSet() {
        String input = "830807527228";
        gnj.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF.name(), "");
        String result1 = gnj.generateMaskedRow(input);

        gnj.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF.name(), "");
        String result2 = gnj.generateMaskedRow(input);

        assertNotEquals(String.format("The result should not be reproducible when no password is set. Input value is %s.", input),
                result1, result2);
    }
}
