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
package org.talend.dataquality.datamasking.semantic;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.duplicating.AllDataqualitySamplingTests;

public class ReplaceCharactersWithGenerationTest {

    private ReplaceCharactersWithGeneration rcwg;

    @Before
    public void setUp() {
        rcwg = new ReplaceCharactersWithGeneration();
    }

    @Test
    public void testInit() {
        rcwg.parse(null, true, new Random(AllDataqualitySamplingTests.RANDOM_SEED));
        assertEquals("Vkfz-Zps-550", rcwg.generateMaskedRow("Abcd-Efg-135"));
        assertEquals("  \t", rcwg.generateMaskedRow("  \t")); // SPACE_SPACE_TAB
    }

    @Test
    public void consistentMasking() {
        rcwg.setSeed("aSeed");
        String result1 = rcwg.doGenerateMaskedField("Abcd-Efg-135", FunctionMode.CONSISTENT);
        String result2 = rcwg.doGenerateMaskedField("Abcd-Efg-135", FunctionMode.CONSISTENT);
        assertEquals(result2, result1);
    }

}
