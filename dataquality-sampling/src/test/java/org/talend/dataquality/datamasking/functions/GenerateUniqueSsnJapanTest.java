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
public class GenerateUniqueSsnJapanTest {

    private String output;

    private AbstractGenerateUniqueSsn gnj = new GenerateUniqueSsnJapan();

    @Before
    public void setUp() throws Exception {
        gnj.setRandom(new Random(42));
        gnj.setKeepFormat(true);
    }

    @Test
    public void testEmpty() {
        gnj.setKeepEmpty(true);
        output = gnj.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood1() {
        output = gnj.generateMaskedRow("830807527228");
        assertEquals("477564837070", output);
    }

    @Test
    public void testGood2() {
        output = gnj.generateMaskedRow("486953617449");
        assertEquals("370892787197", output);
    }

    @Test
    public void testWrongSsnFieldNumber() {
        gnj.setKeepInvalidPattern(false);
        // without a number
        output = gnj.generateMaskedRow("83080727228");
        assertEquals(null, output);
    }

    @Test
    public void testWrongSsnFieldLetter() {
        gnj.setKeepInvalidPattern(false);
        // with a letter instead of a number
        output = gnj.generateMaskedRow("83080752722P");
        assertEquals(null, output);
    }
}
