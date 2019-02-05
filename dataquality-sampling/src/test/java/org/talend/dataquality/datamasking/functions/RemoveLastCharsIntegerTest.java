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
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class RemoveLastCharsIntegerTest {

    private int input = 666;

    private int output;

    private RemoveLastCharsInteger rlci = new RemoveLastCharsInteger();

    @Before
    public void setUp() throws Exception {
        rlci.setRandom(new Random(42));
    }

    @Test
    public void defaultBehavior() {
        rlci.parse("2", false);
        output = rlci.generateMaskedRow(input);
        assertEquals(6, output);
    }

    @Test
    public void dummyParameter() {
        rlci.parse("10", false);
        output = rlci.generateMaskedRow(input);
        assertEquals(0, output);
    }

}
