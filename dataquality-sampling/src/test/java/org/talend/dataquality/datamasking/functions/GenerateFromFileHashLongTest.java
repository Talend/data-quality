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
public class GenerateFromFileHashLongTest {

    private GenerateFromFileHashLong gffhl = new GenerateFromFileHashLong();

    @Before
    public void setUp() throws URISyntaxException {
        String path = this.getClass().getResource("data/numbers.txt").toURI().getPath(); //$NON-NLS-1$
        gffhl.setRandom(new Random(42L));
        gffhl.parse(path, false);
    }

    @Test
    public void testGood() {
        assertEquals(18, gffhl.generateMaskedRow(101L).longValue());
        assertEquals(9, gffhl.generateMaskedRow(null).longValue());
    }

    @Test
    public void testBad() {
        gffhl.keepNull = true;
        assertNull(gffhl.generateMaskedRow(null));
    }
}
