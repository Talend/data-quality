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
public class GenerateUniqueSsnChnTest {

    private String output;

    private AbstractGenerateUniqueSsn gnc = new GenerateUniqueSsnChn();

    @Before
    public void setUp() throws Exception {
        gnc.setRandom(new Random(42));
        gnc.setSecret(FormatPreservingMethod.BASIC.name(), "");
        gnc.setKeepFormat(true);
    }

    @Test
    public void testEmpty() {
        gnc.setKeepEmpty(true);
        output = gnc.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testKeepInvalidPatternTrue() {
        gnc.setKeepInvalidPattern(true);
        output = gnc.generateMaskedRow("AHDBNSKD");
        assertEquals("AHDBNSKD", output);
    }

    @Test
    public void outputsNullWhenInputNull() {
        gnc.setKeepInvalidPattern(false);
        output = gnc.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void outputsNullWhenInputEmpty() {
        gnc.setKeepInvalidPattern(false);
        output = gnc.generateMaskedRow("");
        assertNull(output);
    }

    @Test
    public void unreproducibleWhenNoPasswordSet() {
        String input = "64010119520414123X";
        gnc.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF.name(), "");
        String result1 = gnc.generateMaskedRow(input);

        gnc.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF.name(), "");
        String result2 = gnc.generateMaskedRow(input);

        assertNotEquals(String.format("The result should not be reproducible when no password is set. Input value is %s.", input),
                result1, result2);
    }

    @Test
    public void testGood() {
        output = gnc.generateMaskedRow("64010119520414123X");
        assertTrue(gnc.isValid(output));
        assertEquals("15092320521223813X", output);
    }

    @Test
    public void testGoodSpace() {
        // with spaces
        output = gnc.generateMaskedRow("231202 19510411 456   4");
        assertTrue(gnc.isValid(output));
        assertEquals("410422 19840319 136   X", output);
    }

    @Test
    public void testGoodLeapYear() {
        // leap year for date of birth
        output = gnc.generateMaskedRow("232723 19960229 459 4");
        assertTrue(gnc.isValid(output));
        assertEquals("445322 19370707 229 X", output);
    }

    @Test
    public void testWrongSsnFieldNumber() {
        gnc.setKeepInvalidPattern(false);
        // without a number
        output = gnc.generateMaskedRow("6401011920414123X");
        assertNull(output);
    }

    @Test
    public void testWrongSsnFieldLetter() {
        gnc.setKeepInvalidPattern(false);
        // with a wrong letter
        output = gnc.generateMaskedRow("640101195204141C3X");
        assertNull(output);
    }

    @Test
    public void testWrongSsnFieldRegion() {
        gnc.setKeepInvalidPattern(false);
        // With an invalid region code
        output = gnc.generateMaskedRow("11000119520414123X");
        assertNull(output);
    }

    @Test
    public void testWrongSsnFieldBirth() {
        gnc.setKeepInvalidPattern(false);
        // With an invalid date of birth (wrong year)
        output = gnc.generateMaskedRow("64010118520414123X");
        assertNull(output);
    }

    @Test
    public void testWrongSsnFieldBirth2() {
        gnc.setKeepInvalidPattern(false);
        // With an invalid date of birth (day not existing)
        output = gnc.generateMaskedRow("64010119520434123X");
        assertNull(output);
    }

    @Test
    public void testWrongSsnFieldBirth3() {
        gnc.setKeepInvalidPattern(false);
        // With an invalid date of birth (29th February in a non-leap year)
        output = gnc.generateMaskedRow("64010119530229123X");
        assertNull(output);
    }

}
