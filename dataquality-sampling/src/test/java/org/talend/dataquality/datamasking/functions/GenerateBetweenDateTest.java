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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateBetweenDateTest {

    private GenerateBetweenDate gbd = new GenerateBetweenDate();

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //$NON-NLS-1$

    private String output;

    @Before
    public void setUp() throws Exception {
        gbd.setRandom(new Random(42L));
    }

    @Test
    public void testGood() {
        gbd.parameters = "01-02-1992, 29-02-1992".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = sdf.format(gbd.generateMaskedRow(null));
        assertEquals(output, "21-02-1992"); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        gbd.parameters = "not a date, 29-02-1992".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = sdf.format(gbd.generateMaskedRow(null));
        assertEquals(output, sdf.format(new Date(System.currentTimeMillis())));
    }
}
