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
import org.talend.dataquality.sampling.exception.DQException;

/**
 * @author dprot
 */
public class GenerateUniqueSsnChnTest {

    private String output;

    private AbstractGenerateUniqueSsn gnf = new GenerateUniqueSsnChn();

    public GenerateUniqueSsnChnTest() throws DQException {
        super();
    }

    @Before
    public void setUp() throws Exception {
        gnf.setRandom(new Random(42));
        gnf.setKeepFormat(true);
    }

    @Test
    public void testEmpty() throws DQException {
        gnf.setKeepEmpty(true);
        output = gnf.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testKeepInvalidPatternTrue() throws DQException {
        gnf.setKeepInvalidPattern(true);
        output = gnf.generateMaskedRow(null);
        assertEquals(null, output);
        output = gnf.generateMaskedRow("");
        assertEquals("", output);
        output = gnf.generateMaskedRow("AHDBNSKD");
        assertEquals("AHDBNSKD", output);
    }

    @Test
    public void testKeepInvalidPatternFalse() throws DQException {
        gnf.setKeepInvalidPattern(false);
        output = gnf.generateMaskedRow(null);
        assertEquals(null, output);
        output = gnf.generateMaskedRow("");
        assertEquals(null, output);
        output = gnf.generateMaskedRow("AHDBNSKD");
        assertEquals(null, output);
    }

    @Test
    public void testGood() throws DQException {
        output = gnf.generateMaskedRow("64010119520414123X");
        assertEquals("15092320521223813X", output);
    }

    @Test
    public void testGoodSpace() throws DQException {
        // with spaces
        output = gnf.generateMaskedRow("231202 19510411 456   4");
        assertEquals("410422 19840319 136   X", output);
    }

    @Test
    public void testGoodLeapYear() throws DQException {
        // leap year for date of birth
        output = gnf.generateMaskedRow("232723 19960229 459 4");
        assertEquals("445322 19370707 229 X", output);
    }

    @Test
    public void testWrongSsnFieldNumber() throws DQException {
        gnf.setKeepInvalidPattern(false);
        // without a number
        output = gnf.generateMaskedRow("6401011920414123X");
        assertEquals(null, output);
    }

    @Test
    public void testWrongSsnFieldLetter() throws DQException {
        gnf.setKeepInvalidPattern(false);
        // with a wrong letter
        output = gnf.generateMaskedRow("640101195204141C3X");
        assertEquals(null, output);
    }

    @Test
    public void testWrongSsnFieldRegion() throws DQException {
        gnf.setKeepInvalidPattern(false);
        // With an invalid region code
        output = gnf.generateMaskedRow("11000119520414123X");
        assertEquals(null, output);
    }

    @Test
    public void testWrongSsnFieldBirth() throws DQException {
        gnf.setKeepInvalidPattern(false);
        // With an invalid date of birth (wrong year)
        output = gnf.generateMaskedRow("64010118520414123X");
        assertEquals(null, output);
    }

    @Test
    public void testWrongSsnFieldBirth2() throws DQException {
        gnf.setKeepInvalidPattern(false);
        // With an invalid date of birth (day not existing)
        output = gnf.generateMaskedRow("64010119520434123X");
        assertEquals(null, output);
    }

    @Test
    public void testWrongSsnFieldBirth3() throws DQException {
        gnf.setKeepInvalidPattern(false);
        // With an invalid date of birth (29th February in a non-leap year)
        output = gnf.generateMaskedRow("64010119530229123X");
        assertEquals(null, output);
    }

}
