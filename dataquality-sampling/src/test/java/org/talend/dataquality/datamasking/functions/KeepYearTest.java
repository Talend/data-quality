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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.functions.KeepYear;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class KeepYearTest {

    private Date dateToModify;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //$NON-NLS-1$

    private KeepYear ky = new KeepYear();

    @Before
    public void setUp() throws Exception {
        dateToModify = sdf.parse("05-08-1982"); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        dateToModify = ky.generateMaskedRow(dateToModify);
        assertEquals("01-01-1982", sdf.format(dateToModify)); //$NON-NLS-1$
    }
}
