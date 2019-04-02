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
package org.talend.dataquality.datamasking.functions.generation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateFromFileStringTest {

    private String output;

    private GenerateFromFileString gffs = new GenerateFromFileString();

    @Before
    public void setUp() throws URISyntaxException {
        final String path = this.getClass().getResource("name.txt").toURI().getPath(); //$NON-NLS-1$
        gffs.setRandom(new Random(42L));
        gffs.parse(path, false);
    }

    @Test
    public void testEmpty() {
        gffs.setKeepEmpty(true);
        output = gffs.generateMaskedRow("").toString();
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        output = gffs.generateMaskedRow(null);
        assertEquals("Brad X", output); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gffs.setKeepNull(true);
        output = gffs.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void testParse() {

        GenerateFromFileString generateFromFileString = new GenerateFromFileString();
        generateFromFileString.parse("abc1", false);// invalid path //$NON-NLS-1$
        assertEquals("Parameters length should be 1", 1, generateFromFileString.getParsedParameters().length); //$NON-NLS-1$
        assertTrue("Parameters should not be empty", generateFromFileString.getParsedParameters()[0].length() > 0); //$NON-NLS-1$
        assertTrue("genericTokens should not be empty", generateFromFileString.genericTokens.get(0).length() > 0); //$NON-NLS-1$

    }

    @Test
    public void testEmptyParse() {
        GenerateFromFileString generateFromFileString = new GenerateFromFileString();
        generateFromFileString.parse("", false); //$NON-NLS-1$
        String parameterResult = generateFromFileString.getParsedParameters()[0];
        assertEquals("Parameters length should be 1", 1, generateFromFileString.getParsedParameters().length); //$NON-NLS-1$
        assertTrue("Parameters should not be empty", //$NON-NLS-1$
                parameterResult.length() > 0);
        assertEquals("genericTokens should not be empty", "Configuration issue (check your parameters)", //$NON-NLS-1$ //$NON-NLS-2$
                generateFromFileString.genericTokens.get(0));
    }

}
