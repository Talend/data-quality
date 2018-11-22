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
 * @author jteuladedenantes
 */

public class GenerateUniqueSsnUkTest {

    private String output;

    private AbstractGenerateUniqueSsn gnu = new GenerateUniqueSsnUk();

    @Before
    public void setUp() throws Exception {
        gnu.setRandom(new Random(42));
        gnu.setKeepFormat(true);
        gnu.setSecretManager(new SecretManager(FormatPreservingMethod.BASIC, null));

    }

    @Test
    public void testEmpty() {
        gnu.setKeepEmpty(true);
        output = gnu.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
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
}
