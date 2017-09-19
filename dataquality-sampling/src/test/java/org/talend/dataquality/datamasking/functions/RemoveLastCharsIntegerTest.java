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
import org.talend.dataquality.sampling.exception.DQException;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class RemoveLastCharsIntegerTest {

    private int input = 666;

    private int output;

    private RemoveLastCharsInteger rlci = new RemoveLastCharsInteger();

    @Test
    public void test() throws DQException {
        rlci.parse("2", false, new Random(42));
        output = rlci.generateMaskedRow(input);
        assertEquals(output, 6);
    }

    @Test
    public void testDummyGood() throws DQException {
        rlci.parse("10", false, new Random(42));
        output = rlci.generateMaskedRow(input);
        assertEquals(output, 0);
    }

}
