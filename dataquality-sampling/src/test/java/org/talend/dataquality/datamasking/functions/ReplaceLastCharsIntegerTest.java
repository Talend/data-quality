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
 * created by jgonzalez on 1 juil. 2015 Detailled comment
 *
 */
public class ReplaceLastCharsIntegerTest {

    private String output;

    private Integer input = 123456;

    private ReplaceLastCharsInteger rlci = new ReplaceLastCharsInteger();

    @Test
    public void testGood() {
        rlci.parse("3", false, new Random(42));
        output = rlci.generateMaskedRow(input).toString();
        assertEquals(output, "123038"); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        rlci.parse("7", false, new Random(42));
        output = rlci.generateMaskedRow(input).toString();
        assertEquals(output, "38405"); //$NON-NLS-1$
    }
}
