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
package org.talend.dataquality.email.checkerImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * created by qiongli on 2014年12月26日 Detailled comment
 *
 */
public class TLDsCheckerImplTest {

    TLDsCheckerImpl tldCheck;

    /**
     * Test method for {@link org.talend.dataquality.email.checkerImpl.TLDsCheckerImpl#check(java.lang.String)}.
     */
    @Test
    public void testCheck_valid() {
        List<String> additionalTLDs = new ArrayList<String>();
        additionalTLDs.add("xy1".toUpperCase()); //$NON-NLS-1$
        tldCheck = new TLDsCheckerImpl(additionalTLDs, true);
        String email = "ab.c@sina.com.cn";
        boolean check = tldCheck.check(email);
        Assert.assertTrue(check);
        email = "ab.c@sina.com.xy1";
        check = tldCheck.check(email);
        Assert.assertTrue(check);
    }

    /**
     * Test method for {@link org.talend.dataquality.email.checkerImpl.TLDsCheckerImpl#check(java.lang.String)}.
     */
    @Test
    public void testCheck_invalid() {
        List<String> additionalTLDs = new ArrayList<String>();
        additionalTLDs.add("abx".toUpperCase()); //$NON-NLS-1$
        additionalTLDs.add("cde".toUpperCase()); //$NON-NLS-1$
        additionalTLDs.add("xy1".toUpperCase()); //$NON-NLS-1$
        tldCheck = new TLDsCheckerImpl(additionalTLDs, true);

        String email = "ab.c@sina.com.abx1";
        boolean check = tldCheck.check(email);
        Assert.assertFalse(check);
        email = "ab.c@sina.com.xy";
        check = tldCheck.check(email);
        Assert.assertFalse(check);
    }

    @Test
    public void testCheck_null() {
        // TLD list is null
        tldCheck = new TLDsCheckerImpl(null, true);
        String email = "ab.c@sina.com.abx1";
        boolean check = tldCheck.check(email);
        Assert.assertFalse(check);

        // empty TLD list,and the last char in the email is '.'
        email = "ab.c@sina.com.xy.";
        tldCheck = new TLDsCheckerImpl(new ArrayList<String>(), true);
        check = tldCheck.check(email);
        Assert.assertFalse(check);
    }

}
