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

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.email.exception.TalendSMTPRuntimeException;

/**
 * created by qiongli on 2014年12月26日 Detailled comment
 *
 */
public class CallbackMailServerCheckerImplTest {

    CallbackMailServerCheckerImpl callBackServerCheck;

    // the sender email used for test
    public static final String TEST_SEND_EMAIL = "msjian@talend.com"; //$NON-NLS-1$

    /**
     * DOC zhao Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        callBackServerCheck = new CallbackMailServerCheckerImpl();
    }

    /**
     * Test method for {@link org.talend.dataquality.email.checkerImpl.CallbackMailServerCheckerImpl#check(java.lang.String)}.
     */
    public void testCheck_valid() {
        String email = "zshen@talend.com";
        try {
            assertTrue(callBackServerCheck.check(email));
        } catch (TalendSMTPRuntimeException e) {
        }
    }

    public void testCheck_valid_2() {
        String email = "237283696aaaaaaaoooooo-ccccc@qq.com";
        try {
            assertFalse(callBackServerCheck.check(email));
        } catch (TalendSMTPRuntimeException e) {
        }
    }

    public void testCheck_valid3() {
        String email = "gégé@laposte.fr";
        assertTrue(callBackServerCheck.check(email));
        email = "sidbpl@cebpl.caisse-epargne.fr";
        assertTrue(callBackServerCheck.check(email));
    }

    public void testCheck_invalid() {
        String email = "qiongli1@163.com";
        try {
            assertFalse(callBackServerCheck.check(email));
        } catch (TalendSMTPRuntimeException e) {
        }
    }

    public void testCheck_invalid1() {
        String email = "qiongli1@sohu.com";
        try {
            assertFalse(callBackServerCheck.check(email));
        } catch (TalendSMTPRuntimeException e) {
        }
    }

    public void testCheck_invalid2() {
        String email = "talend_test@sina.com";// password:talend
        try {
            assertTrue(callBackServerCheck.check(email));
        } catch (TalendSMTPRuntimeException e) {
        }
    }

    /*
     * no @
     */
    @Test
    public void testCheck_invalid3() {
        String email = "talend_testsina.com";
        assertFalse(callBackServerCheck.check(email));
    }

    /*
     * email is null
     */
    @Test
    public void testCheck_invalid4() {
        String email = null;
        assertFalse(callBackServerCheck.check(email));
    }

    /*
     * regex is error
     */
    @Test
    public void testCheck_invalid5() {
        String email = "talend_test@@sina.com";
        assertFalse(callBackServerCheck.check(email));
    }

    /*
     * error domain
     */
    public void testCheck_invalid6() {
        String email = "talend_test@sinasina.com";
        assertFalse(callBackServerCheck.check(email));
    }

}
