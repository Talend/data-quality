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

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class KeepFirstCharsIntegerTest {

    private int output;

    private int input = 123456;

    private KeepFirstCharsInteger kfag = new KeepFirstCharsInteger();

    @Test
    public void testGood() {
        kfag.parse("3", false, new Random(42));
        output = kfag.generateMaskedRow(input);
        assertEquals(123038, output); // $NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        kfag.parse("7", false, new Random(42));
        output = kfag.generateMaskedRow(input);
        assertEquals(input, output);
    }
}
