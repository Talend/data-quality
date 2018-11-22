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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.SecretManager;

/**
 * @author dprot
 */
public class GenerateUniqueSsnIndiaTest {

    private String output;

    private AbstractGenerateUniqueSsn gnf = new GenerateUniqueSsnIndia();

    @Before
    public void setUp() throws Exception {
        gnf.setRandom(new Random(42));
        gnf.setKeepFormat(true);
        gnf.setSecretManager(new SecretManager(FormatPreservingMethod.BASIC, null));

    }

    @Test
    public void testKeepInvalidPatternTrue() {
        gnf.setKeepInvalidPattern(true);
        output = gnf.generateMaskedRow(null);
        assertNull(output);
        output = gnf.generateMaskedRow("");
        assertEquals("", output);
        output = gnf.generateMaskedRow("AHDBNSKD");
        assertEquals("AHDBNSKD", output);
    }

    @Test
    public void testKeepInvalidPatternFalse() {
        gnf.setKeepInvalidPattern(false);
        output = gnf.generateMaskedRow(null);
        assertNull(output);
        output = gnf.generateMaskedRow("");
        assertNull(output);
        output = gnf.generateMaskedRow("AHDBNSKD");
        assertNull(output);
    }

    @Test
    public void testGood1() {
        output = gnf.generateMaskedRow("186034828209");
        assertTrue(gnf.isValid(output));
        assertEquals("578462130603", output);
    }

    @Test
    public void testGood2() {
        // with spaces
        output = gnf.generateMaskedRow("21212159530   8");
        assertTrue(gnf.isValid(output));
        assertEquals("48639384490   5", output);
    }

    @Test
    public void testWrongSsnFieldNumber() {
        gnf.setKeepInvalidPattern(false);
        // without a number
        output = gnf.generateMaskedRow("21860348282");
        assertNull(output);
    }

    @Test
    public void testWrongSsnField1() {
        gnf.setKeepInvalidPattern(false);
        // Wrong first field
        output = gnf.generateMaskedRow("086034828209");
        assertNull(output);
    }

    @Test
    public void testWrongSsnFieldLetter() {
        gnf.setKeepInvalidPattern(false);
        // with a letter instead of a number
        output = gnf.generateMaskedRow("186034Y20795");
        assertNull(output);
    }

}
