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
import org.talend.dataquality.datamasking.FunctionMode;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class KeepFirstCharsLongTest {

    private long output;

    private long input = 123456L;

    private KeepFirstCharsLong kfag = new KeepFirstCharsLong();

    @Before
    public void setUp() {
        kfag.setRandom(new Random(42));
    }

    @Test
    public void defaultBehavior() {
        kfag.parse("3", false);
        output = kfag.generateMaskedRow(input);
        assertEquals(123038L, output); //$NON-NLS-1$
    }

    @Test
    public void random() {
        kfag.parse("3", false);
        output = kfag.generateMaskedRow(input, FunctionMode.RANDOM);
        assertEquals(123038L, output); //$NON-NLS-1$
    }

    @Test
    public void dummyHighParameter() {
        kfag.parse("7", false);
        output = kfag.generateMaskedRow(input);
        assertEquals(output, input);
    }

    @Test
    public void twoParameters() {
        kfag.parse("2,6", false);
        output = kfag.generateMaskedRow(input);
        assertEquals(126666L, output);
    }
}
