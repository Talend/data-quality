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
package org.talend.dataquality.email.checkerImpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * created by qiongli on 2014年12月26日 Detailled comment
 *
 */
public class RegularRegexCheckerImplTest {

    RegularRegexCheckerImpl regularCheck = null;

    /**
     * Test method for {@link org.talend.dataquality.email.checkerImpl.RegularRegexCheckerImpl#check(java.lang.String)}.
     */
    @Test
    public void testCheck_valid() {
        regularCheck = new RegularRegexCheckerImpl("");
        String email = "ab.c@yahoo.com"; //$NON-NLS-1$
        boolean check = regularCheck.check(email);
        assertTrue(check);
        email = "_1ab.c@yahoo.com"; //$NON-NLS-1$
        assertTrue(regularCheck.check(email));
        email = "gégé@laposte.fr"; //$NON-NLS-1$
        assertTrue(regularCheck.check(email));
        // email = "sidbpl@cebpl.caisse-epargne.fr"; //$NON-NLS-1$
        // assertTrue(regularCheck.check(email));
    }

    @Test
    public void testCheck_Invalid() {
        regularCheck = new RegularRegexCheckerImpl(null);
        String email = ";-abc@yahoo.com.";
        boolean check = regularCheck.check(email);
        assertFalse(check);
    }
}
