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
public class NumericVarianceLongTest {

    private String output;

    private Long input = 123L;

    private NumericVarianceLong nvl = new NumericVarianceLong();

    @Test
    public void testGood() {
        nvl.parse("10", false, new Random(42));
        output = nvl.generateMaskedRow(input).toString();
        assertEquals(output, String.valueOf(114));
    }

    @Test
    public void testDummy() {
        nvl.parse("-10", false, new Random(42));
        output = nvl.generateMaskedRow(input).toString();
        assertEquals(output, String.valueOf(114));
    }

}
