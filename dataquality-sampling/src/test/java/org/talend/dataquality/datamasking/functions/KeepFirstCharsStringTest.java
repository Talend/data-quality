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
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class KeepFirstCharsStringTest {

    private String output;

    private String input = "a1b2c3d456"; //$NON-NLS-1$

    private KeepFirstCharsString kfag = new KeepFirstCharsString();

    @Before
    public void setUp() {
        kfag.setRandom(new Random(42));
    }

    @Test
    public void emptyReturnsEmpty() {
        kfag.setKeepEmpty(true);
        output = kfag.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void defaultBehavior() {
        kfag.parse("3", false);
        output = kfag.generateMaskedRow(input);
        assertEquals("a1b0h8m055", output); //$NON-NLS-1$
    }

    @Test
    public void dummyHighParameter() {
        kfag.parse("15", false);
        output = kfag.generateMaskedRow(input);
        assertEquals(input, output);
    }

    @Test
    public void twoParameters() {
        kfag.parse("5,8", false);
        output = kfag.generateMaskedRow(input);
        assertEquals("a1b2c88888", output);
    }

}
