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
public class GenerateUniqueSsnGermanTest {

    private String output;

    private AbstractGenerateUniqueSsn gnj = new GenerateUniqueSsnGermany();

    @Before
    public void setUp() throws Exception {
        gnj.setRandom(new Random(42));
        gnj.setKeepFormat(true);
        gnj.setSecretManager(new SecretManager(FormatPreservingMethod.BASIC, null));
    }

    @Test
    public void testEmpty() {
        gnj.setKeepEmpty(true);
        output = gnj.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood1() {
        output = gnj.generateMaskedRow("83807527228");
        assertTrue(gnj.isValid(output));
        assertEquals("79564837099", output);
    }

    @Test
    public void testGood2() {
        output = gnj.generateMaskedRow("48695361449");
        assertTrue(gnj.isValid(output));
        assertEquals("37088083197", output);
    }

    @Test
    public void testWrongSsnFieldNumber() {
        gnj.setKeepInvalidPattern(false);
        // without a number
        output = gnj.generateMaskedRow("8308072728");
        assertNull(output);
    }

    @Test
    public void testWrongSsnFieldLetter() {
        gnj.setKeepInvalidPattern(false);
        // with a letter instead of a number
        output = gnj.generateMaskedRow("8308752722P");
        assertNull(output);
    }
}
