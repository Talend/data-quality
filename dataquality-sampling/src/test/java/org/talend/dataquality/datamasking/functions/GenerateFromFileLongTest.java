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

import java.net.URISyntaxException;
import java.util.Random;

import org.junit.Test;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class GenerateFromFileLongTest {

    private GenerateFromFileLong gffl = new GenerateFromFileLong();

    @Test
    public void testGood() throws URISyntaxException {
        String path = this.getClass().getResource("data/numbers.txt").toURI().getPath(); //$NON-NLS-1$
        gffl.parse(path, false, new Random(42));
        assertEquals(9, gffl.generateMaskedRow(0L).longValue());
    }

    @Test
    public void testNull() {
        gffl.parse(Function.EMPTY_STRING, false, new Random(42));
        gffl.setKeepNull(true);
        assertEquals(0, gffl.generateMaskedRow(0L).longValue());
        assertEquals(null, gffl.generateMaskedRow(null));

    }

}
