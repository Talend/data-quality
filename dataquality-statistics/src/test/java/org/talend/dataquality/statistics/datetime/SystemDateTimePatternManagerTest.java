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
package org.talend.dataquality.statistics.datetime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SystemDateTimePatternManagerTest {

    @Test
    public void BadMonthName_TDQ13347() {
        assertFalse(SystemDateTimePatternManager.isDate("01 TOTO 2015"));
        assertTrue(SystemDateTimePatternManager.isDate("01 JANUARY 2015"));
    }

}
