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
import static org.junit.Assert.assertNull;

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
        final String path = this.getClass().getResource("data/name.txt").toURI().getPath(); //$NON-NLS-1$
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
        gffs.keepNull = true;
        output = gffs.generateMaskedRow(null);
        assertNull(output);
    }

}
