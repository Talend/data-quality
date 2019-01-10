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
public class GenerateUniqueSsnIndiaTest {

    private String output;

    private AbstractGenerateUniqueSsn gni = new GenerateUniqueSsnIndia();

    @Before
    public void setUp() throws Exception {
        gni.setRandom(new Random(42));
        gni.setSecret(FormatPreservingMethod.BASIC, "");
        gni.setKeepFormat(true);
    }

    @Test
    public void testKeepInvalidPatternTrue() {
        gni.setKeepInvalidPattern(true);
        output = gni.generateMaskedRow("AHDBNSKD");
        assertEquals("AHDBNSKD", output);
    }

    @Test
    public void outputsNullWhenInputNull() {
        gni.setKeepInvalidPattern(false);
        output = gni.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void outputsNullWhenInputEmpty() {
        gni.setKeepInvalidPattern(false);
        output = gni.generateMaskedRow("");
        assertNull(output);
    }

    @Test
    public void testGood1() {
        output = gni.generateMaskedRow("186034828209");
        assertTrue(gni.isValid(output));
        assertEquals("578462130603", output);
    }

    @Test
    public void testGood2() {
        // with spaces
        output = gni.generateMaskedRow("21212159530   8");
        assertTrue(gni.isValid(output));
        assertEquals("48639384490   5", output);
    }

    @Test
    public void testWrongSsnFieldNumber() {
        gni.setKeepInvalidPattern(false);
        // without a number
        output = gni.generateMaskedRow("21860348282");
        assertNull(output);
    }

    @Test
    public void testWrongSsnField1() {
        gni.setKeepInvalidPattern(false);
        // Wrong first field
        output = gni.generateMaskedRow("086034828209");
        assertNull(output);
    }

    @Test
    public void testWrongSsnFieldLetter() {
        gni.setKeepInvalidPattern(false);
        // with a letter instead of a number
        output = gni.generateMaskedRow("186034Y20795");
        assertNull(output);
    }

    @Test
    public void unreproducibleWhenNoPasswordSet() {
        String input = "186034828209";
        gni.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "");
        String result1 = gni.generateMaskedRow(input);

        gni.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "");
        String result2 = gni.generateMaskedRow(input);

        assertNotEquals(String.format("The result should not be reproducible when no password is set. Input value is %s.", input),
                result1, result2);
    }
}
