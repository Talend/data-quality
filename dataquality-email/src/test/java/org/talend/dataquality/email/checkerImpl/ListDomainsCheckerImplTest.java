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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * created by qiongli on 2014年12月26日 Detailled comment
 *
 */
public class ListDomainsCheckerImplTest {

    ListDomainsCheckerImpl domainCheck;

    /**
     * Test method for {@link org.talend.dataquality.email.checkerImpl.ListDomainsCheckerImpl#check(java.lang.String)}.
     */
    @Test
    public void testCheck_white() {

        List<String> domainLs = new ArrayList<String>();
        domainLs.add("sina.com");
        domainLs.add("yahoo.com");
        domainLs.add("163.net");
        domainCheck = new ListDomainsCheckerImpl(false, domainLs);
        String email = "ab.c@sina.com";
        assertTrue(domainCheck.check(email));
        String email2 = "ab.c@yahoo.com";
        assertTrue(domainCheck.check(email2));

    }

    /**
     * Test method for {@link org.talend.dataquality.email.checkerImpl.ListDomainsCheckerImpl#check(java.lang.String)}.
     */
    @Test
    public void testCheck_black() {
        List<String> domainLs = new ArrayList<String>();
        domainLs.add("sina.com");
        domainLs.add("yahoo.com");
        domainLs.add("163.net");
        domainCheck = new ListDomainsCheckerImpl(true, domainLs);
        String email = "ab.c@sina.com";
        assertFalse(domainCheck.check(email));
        String email2 = "ab.c@yahoo.com";
        assertFalse(domainCheck.check(email2));
        String email3 = "ab.c@talend.com";
        assertTrue(domainCheck.check(email3));

    }

    @Test
    public void testCheck_null() {
        // white list is null,but the domian is right
        domainCheck = new ListDomainsCheckerImpl(false, null);
        String email = "ab.c@sina.com";
        assertFalse(domainCheck.check(email));
        // black list is null,but the domian is right
        domainCheck = new ListDomainsCheckerImpl(true, null);
        String email2 = "ab.c@yahoo.com";
        assertTrue(domainCheck.check(email2));
    }

    @Test
    public void testCheck_whitelist_with_wildcard() {
        List<String> domainList = new ArrayList<String>();
        domainList.add("univ-*.fr");

        domainCheck = new ListDomainsCheckerImpl(false, domainList);
        String email = "ab.c@univ-paris-6.fr";
        assertTrue(domainCheck.check(email));

        email = "ab.c@univ-paris-.fr"; // valid according to user domain, but invalid with the Apache domain pattern
        assertFalse(domainCheck.check(email));

        // fix the following false-positive
        email = "ab.c@univ-paris-6.sfr"; // the TLD here is sfr but not fr, it should not be valid
        assertFalse(domainCheck.check(email));

    }

}
