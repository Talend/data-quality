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

import java.net.URISyntaxException;
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
     * 
     * @throws URISyntaxException
     */
    @Test
    public void testInit() throws URISyntaxException {
        GenerateFromFileStringProvided gfls = new GenerateFromFileStringProvided();
        // this.getClass().getResource("data/numbers.txt").toURI().getPath()
        gfls.parse(this.getClass().getResource("commune.txt").toURI().getPath(), true,
                new Random(AllDataqualitySamplingTests.RANDOM_SEED));
        Assert.assertEquals("Dieppe", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("Savigny-sur-Orge", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("Roanne", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("  \t", gfls.generateMaskedRow("  \t")); // SPACE_SPACE_TAB //$NON-NLS-1$ //$NON-NLS-2$

        gfls = new GenerateFromFileStringProvided();
        gfls.parse(this.getClass().getResource("company.txt").toURI().getPath(), true,
                new Random(AllDataqualitySamplingTests.RANDOM_SEED));
        Assert.assertEquals("Gilead Sciences", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("Fresenius", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("McDonald's", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$

        gfls = new GenerateFromFileStringProvided();
        gfls.parse(this.getClass().getResource("firstName.txt").toURI().getPath(), true,
                new Random(AllDataqualitySamplingTests.RANDOM_SEED));
        Assert.assertEquals("Josiah", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("Mason", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("Cooper", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$

        gfls = new GenerateFromFileStringProvided();
        gfls.parse(this.getClass().getResource("lastName.txt").toURI().getPath(), true,
                new Random(AllDataqualitySamplingTests.RANDOM_SEED));
        Assert.assertEquals("Robbins", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("Lambert", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("Pierce", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$

        gfls = new GenerateFromFileStringProvided();
        gfls.parse(this.getClass().getResource("organization.txt").toURI().getPath(), true,
                new Random(AllDataqualitySamplingTests.RANDOM_SEED));
        Assert.assertEquals("Environmental Defense", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("United Nations Children's Fund (UNICEF)", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("JFK Center for Performing Arts", gfls.generateMaskedRow("A")); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
