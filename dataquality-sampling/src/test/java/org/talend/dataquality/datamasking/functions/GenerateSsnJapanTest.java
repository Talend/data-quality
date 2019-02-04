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

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.utils.MockRandom;

/**
 * created by jgonzalez on 20 août 2015 Detailled comment
 *
 */
public class GenerateSsnJapanTest {

    private String output;

    private GenerateSsnJapan gnj = new GenerateSsnJapan();

    @Before
    public void setUp() throws Exception {
        gnj.setRandom(new Random(42));
    }

    @Test
    public void testEmpty() {
        gnj.setKeepEmpty(true);
        output = gnj.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        output = gnj.generateMaskedRow(null);
        assertEquals("038405589322", output); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gnj.keepNull = true;
        output = gnj.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void allDigitCanBeGenerated() {
        MockRandom random = new MockRandom();
        gnj.setRandom(random);
        output = gnj.generateMaskedRow(null);
        assertEquals("012345678901", output);
    }

    @Test
    public void consistentMasking() {
        gnj.setSeed("aSeed");
        String result1 = gnj.doGenerateMaskedField("038405589322", FunctionMode.CONSISTENT);
        String result2 = gnj.doGenerateMaskedField("038405589322", FunctionMode.CONSISTENT);
        assertEquals(result2, result1);
    }
}
