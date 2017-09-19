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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class ReplaceFirstCharsLongTest {

    private long output;

    private Long input = 123456L;

    private ReplaceFirstCharsLong rfcl = new ReplaceFirstCharsLong();

    @Test
    public void testGood() {
        rfcl.parse("3", false, new Random(42));
        output = rfcl.generateMaskedRow(input);
        assertEquals(830456L, output); // $NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        rfcl.parse("7", false, new Random(42));
        output = rfcl.generateMaskedRow(input);
        assertEquals(830807L, output); // $NON-NLS-1$
    }

    @Test
    public void testWrongParameters() {
        try {
            rfcl.parse("7,x", false, new Random(42));
            fail("should get exception with input " + rfcl.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = rfcl.generateMaskedRow(input);
        assertEquals(0L, output); // $NON-NLS-1$
    }

    @Test
    public void testGoodParameters() {
        rfcl.parse("4,2", false, new Random(42));
        output = rfcl.generateMaskedRow(input);
        assertEquals(222256, output); // $NON-NLS-1$
    }

}
