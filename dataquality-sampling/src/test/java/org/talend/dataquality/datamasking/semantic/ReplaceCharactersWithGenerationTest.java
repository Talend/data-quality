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

import org.junit.Test;
import org.talend.dataquality.duplicating.AllDataqualitySamplingTests;

public class ReplaceCharactersWithGenerationTest {

    @Test
    public void testInit() {
        ReplaceCharactersWithGeneration rcwg = new ReplaceCharactersWithGeneration();

        rcwg.parse(null, true, new Random(AllDataqualitySamplingTests.RANDOM_SEED));
        assertEquals("Vkfz-Zps-550", rcwg.generateMaskedRow("Abcd-Efg-135"));
        assertEquals("  \t", rcwg.generateMaskedRow("  \t")); // SPACE_SPACE_TAB
    }

}
