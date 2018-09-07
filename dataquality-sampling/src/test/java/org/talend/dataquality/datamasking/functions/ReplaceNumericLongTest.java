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
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceNumericLongTest {

    private long input = 123;

    private long output;

    private ReplaceNumericLong rnl = new ReplaceNumericLong();

    @Test
    public void testGood() {
        rnl.parse("6", false, new Random(42));
        output = rnl.generateMaskedRow(input);
        assertEquals(output, 666);
    }

    @Test
    public void testEmptyParameter() {
        rnl.parse(" ", false, new Random(42));
        output = rnl.generateMaskedRow(input);
        assertEquals(output, 38);
    }

    @Test
    public void testBad() {
        try {
            rnl.parse("10", false, new Random(42));
            fail("should get exception with input " + rnl.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
    }
}
