// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.duplicating;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class DateChangerTest {

    private Date dateToModify;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //$NON-NLS-1$

    private DateChanger dateChanger = new DateChanger();

    @Before
    public void setUp() throws Exception {
        dateToModify = sdf.parse("13-08-1982"); //$NON-NLS-1$ 
        dateChanger.setSeed(TestConstants.RANDOM_SEED);
    }

    @Test
    public void testModifyDateValue() {
        String result = sdf.format(dateChanger.modifyDateValue(dateToModify));
        assertEquals("23-08-1982", result); //$NON-NLS-1$

        Date result2 = dateChanger.modifyDateValue(null);
        assertTrue(result2 == null);
    }

    @Test
    public void testSwitchDayMonthValue() {
        String result = sdf.format(dateChanger.switchDayMonthValue(dateToModify));
        assertEquals("08-01-1982", result); //$NON-NLS-1$

        Date result2 = dateChanger.switchDayMonthValue(null);
        assertTrue(result2 == null);
    }

    @Test
    public void testReplaceWithRandomDate() {
        String result = sdf.format(dateChanger.replaceWithRandomDate(dateToModify));
        assertEquals("14-03-1963", result); //$NON-NLS-1$

        String result2 = sdf.format(dateChanger.replaceWithRandomDate(null));
        assertEquals("17-10-1971", result2); //$NON-NLS-1$
    }

}
