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
public class GenerateUniqueSsnFrTest {

    private String output;

    private AbstractGenerateUniqueSsn gnf = new GenerateUniqueSsnFr();

    @Before
    public void setUp() throws Exception {
        gnf.setRandom(new Random(42));
        gnf.setKeepFormat(true);
    }

    @Test
    public void testEmpty() {
        gnf.setKeepEmpty(true);
        output = gnf.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testKeepInvalidPatternTrue() {
        gnf.setKeepInvalidPattern(true);
        output = gnf.generateMaskedRow(null);
        assertEquals(null, output);
        output = gnf.generateMaskedRow("");
        assertEquals("", output);
        output = gnf.generateMaskedRow("AHDBNSKD");
        assertEquals("AHDBNSKD", output);
    }

    @Test
    public void testKeepInvalidPatternFalse() {
        gnf.setKeepInvalidPattern(false);
        output = gnf.generateMaskedRow(null);
        assertEquals(null, output);
        output = gnf.generateMaskedRow("");
        assertEquals(null, output);
        output = gnf.generateMaskedRow("AHDBNSKD");
        assertEquals(null, output);
    }

    @Test
    public void testGood1() {
        output = gnf.generateMaskedRow("1860348282074 19");
        assertEquals("2000132446558 52", output);
    }

    @Test
    public void testGood2() {
        gnf.setKeepFormat(false);
        // with spaces
        output = gnf.generateMaskedRow("2 12 12 15 953 006   88");
        assertEquals("117051129317622", output);
    }

    @Test
    public void testGood3() {
        // corse department
        output = gnf.generateMaskedRow("10501  2B 532895 34");
        assertEquals("12312  85 719322 48", output);
    }

    @Test
    public void testGood4() {
        gnf.setKeepFormat(false);
        // with a control key less than 10
        output = gnf.generateMaskedRow("1960159794247 60");
        assertEquals("276115886661903", output);
    }

    @Test
    public void testWrongSsnFieldNumber() {
        gnf.setKeepInvalidPattern(false);
        // without a number
        output = gnf.generateMaskedRow("186034828207 19");
        assertEquals(null, output);
    }

    @Test
    public void testWrongSsnFieldLetter() {
        gnf.setKeepInvalidPattern(false);
        // with a wrong letter
        output = gnf.generateMaskedRow("186034Y282079 19");
        assertEquals(null, output);
    }

    @Test
    public void testWrongSsnFieldPattern() {
        gnf.setKeepInvalidPattern(false);
        // with a letter instead of a number
        output = gnf.generateMaskedRow("1860I48282079 19");
        assertEquals(null, output);
    }

}
