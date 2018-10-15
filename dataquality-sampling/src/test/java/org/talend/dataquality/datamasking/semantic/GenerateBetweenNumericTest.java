// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.semantic;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

public class GenerateBetweenNumericTest {

    private GenerateBetweenNumeric func = new GenerateBetweenNumeric();

    @Test
    public void testGenerateForIntegerInput() {
        func.parse("10, 20", false, new Random(42)); //$NON-NLS-1$
        String output = func.generateMaskedRow("30"); //$NON-NLS-1$
        assertEquals(output, "17"); //$NON-NLS-1$
    }

    @Test
    public void testGenerateForDoubleInput() {
        func.parse("10.0, 20.0", false, new Random(42)); //$NON-NLS-1$
        String output = func.generateMaskedRow("30.012");
        assertEquals(output, "17.276"); //$NON-NLS-1$
    }

    @Test
    public void testGenerateForIntegerInput_WithNoIntegerInRange() {
        func.parse("1.2, 1.3", false, new Random(42)); //$NON-NLS-1$
        String output = func.generateMaskedRow("30");
        assertEquals(output, "1.27"); //$NON-NLS-1$
    }

    @Test
    public void testGenerateForStringInput() {
        func.parse("10.0, 20.0", false, new Random(42)); //$NON-NLS-1$
        String output = func.generateMaskedRow("lol");
        assertEquals(output, "17.28"); //$NON-NLS-1$
    }
}
