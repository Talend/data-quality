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

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.duplicating.AllDataqualitySamplingTests;

/**
 * zshen class global comment. Detailled comment
 */
public class GenerateFromFileStringProvidedTest {

    /**
     * Test method for {@link org.talend.dataquality.datamasking.functions.GenerateFromList#init()}.
     */
    @Test
    public void testInit() {
        GenerateFromFileStringProvided gfls = new GenerateFromFileStringProvided();

        gfls.parse(MaskableCategoryEnum.FR_COMMUNE.getParameter(), true, new Random(AllDataqualitySamplingTests.RANDOM_SEED));
        Assert.assertEquals("Dieppe", gfls.generateMaskedRow("A"));
        Assert.assertEquals("Savigny-sur-Orge", gfls.generateMaskedRow("A"));
        Assert.assertEquals("Roanne", gfls.generateMaskedRow("A"));
        Assert.assertEquals("  \t", gfls.generateMaskedRow("  \t")); // SPACE_SPACE_TAB

        gfls.parse(MaskableCategoryEnum.COMPANY.getParameter(), true, new Random(AllDataqualitySamplingTests.RANDOM_SEED));
        Assert.assertEquals("Gilead Sciences", gfls.generateMaskedRow("A"));
        Assert.assertEquals("Fresenius", gfls.generateMaskedRow("A"));
        Assert.assertEquals("McDonald's", gfls.generateMaskedRow("A"));

        gfls.parse(MaskableCategoryEnum.FIRST_NAME.getParameter(), true, new Random(AllDataqualitySamplingTests.RANDOM_SEED));
        Assert.assertEquals("Josiah", gfls.generateMaskedRow("A"));
        Assert.assertEquals("Mason", gfls.generateMaskedRow("A"));
        Assert.assertEquals("Cooper", gfls.generateMaskedRow("A"));

        gfls.parse(MaskableCategoryEnum.LAST_NAME.getParameter(), true, new Random(AllDataqualitySamplingTests.RANDOM_SEED));
        Assert.assertEquals("Robbins", gfls.generateMaskedRow("A"));
        Assert.assertEquals("Lambert", gfls.generateMaskedRow("A"));
        Assert.assertEquals("Pierce", gfls.generateMaskedRow("A"));

        gfls.parse(MaskableCategoryEnum.ORGANIZATION.getParameter(), true, new Random(AllDataqualitySamplingTests.RANDOM_SEED));
        Assert.assertEquals("Environmental Defense", gfls.generateMaskedRow("A"));
        Assert.assertEquals("United Nations Children's Fund (UNICEF)", gfls.generateMaskedRow("A"));
        Assert.assertEquals("JFK Center for Performing Arts", gfls.generateMaskedRow("A"));
    }

}
