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

import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 1 juil. 2015 Detailled comment
 *
 */
public class ReplaceLastCharsIntegerTest {

    private int output;

    private int input = 123456;

    private ReplaceLastCharsInteger rlci = new ReplaceLastCharsInteger();

    @Test
    public void defaultBehavior() {
        rlci.parse("3", false, new Random(42));
        output = rlci.generateMaskedRow(input);
        assertEquals(123038, output); //$NON-NLS-1$
    }

    @Test
    public void random() {
        rlci.parse("3", false, new Random(42));
        output = rlci.generateMaskedRow(input, FunctionMode.RANDOM);
        assertEquals(123038, output); //$NON-NLS-1$
    }

    @Test
    public void dummyHighParameter() {
        rlci.parse("7", false, new Random(42));
        output = rlci.generateMaskedRow(input);
        assertEquals(38405, output); //$NON-NLS-1$
    }

    @Test
    public void consistent() {
        rlci.parse("3", false, new RandomWrapper(42));
        output = rlci.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rlci.generateMaskedRow(input, FunctionMode.CONSISTENT).intValue());
    }

    @Test
    public void consistentNoSeed() {
        rlci.parse("3", false, new RandomWrapper());
        output = rlci.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rlci.generateMaskedRow(input, FunctionMode.CONSISTENT).intValue());
    }
}
