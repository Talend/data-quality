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
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class GenerateFromFileHashIntegerTest {

    private GenerateFromFileHashInteger gffhi = new GenerateFromFileHashInteger();

    @Before
    public void setUp() throws URISyntaxException {
        String path = this.getClass().getResource("data/numbers.txt").toURI().getPath(); //$NON-NLS-1$
        gffhi.setRandom(new Random(42L));
        gffhi.parse(path, false);
    }

    @Test
    public void testGood() {
        assertEquals(9, gffhi.generateMaskedRow(null).intValue());
    }

    @Test
    public void testNull() {
        gffhi.keepNull = true;
        assertEquals(1, gffhi.generateMaskedRow(0).intValue());
        assertNull(gffhi.generateMaskedRow(null));
    }
}
