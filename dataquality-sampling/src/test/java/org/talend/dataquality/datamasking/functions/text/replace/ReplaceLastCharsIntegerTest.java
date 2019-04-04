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
package org.talend.dataquality.datamasking.functions.text.replace;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;

/**
 * created by jgonzalez on 1 juil. 2015 Detailled comment
 *
 */
public class ReplaceLastCharsIntegerTest {

    private int output;

    private int input = 123456;

    private ReplaceLastCharsInteger rlci = new ReplaceLastCharsInteger();

    @Before
    public void setUp() throws Exception {
        rlci.setRandom(new Random(42));
    }

    @Test
    public void defaultBehavior() {
        rlci.parse("3", false);
        output = rlci.generateMaskedRow(input);
        assertEquals(123038, output); // $NON-NLS-1$
    }

    @Test
    public void random() {
        rlci.parse("3", false);
        output = rlci.generateMaskedRow(input);
        assertEquals(123038, output); // $NON-NLS-1$
    }

    @Test
    public void dummyHighParameter() {
        rlci.parse("7", false);
        output = rlci.generateMaskedRow(input);
        assertEquals(38405, output); // $NON-NLS-1$
    }

    @Test
    public void consistent() {
        rlci.setMaskingMode(FunctionMode.CONSISTENT);
        rlci.parse("3", false);
        output = rlci.generateMaskedRow(input);
        assertEquals(output, rlci.generateMaskedRow(input).intValue());
    }

    @Test
    public void consistentNoSeed() {
        rlci.setMaskingMode(FunctionMode.CONSISTENT);
        rlci.setRandom(null);
        rlci.parse("3", false);
        output = rlci.generateMaskedRow(input);
        assertEquals(output, rlci.generateMaskedRow(input).intValue());
    }
}
