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
package org.talend.dataquality.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.email.api.CheckerParams;
import org.talend.dataquality.email.api.EmailVerify;
import org.talend.dataquality.email.api.EmailVerifyResult;
import org.talend.dataquality.email.exception.TalendSMTPRuntimeException;

/**
 * created by qiongli on 2014年12月26日 Detailled comment
 *
 */
public class EmailVerifyTest {

    String email = null;

    String regularPattern = "aw"; //$NON-NLS-1$

    EmailVerify emailVerify;

    final String commontChar = "//##//"; //$NON-NLS-1$

    final String invalidChar = "invalid"; //$NON-NLS-1$

    final String validChar = "valid"; //$NON-NLS-1$

    private static final Logger log = LoggerFactory.getLogger(EmailVerifyTest.class);

    @Before
    public void setUp() throws Exception {
        emailVerify = new EmailVerify();
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerifyRegular() {
        email = "ab_2c@sina.com"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, ""); //$NON-NLS-1$
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail(email));
    }

    @Test
    public void testVerifyRegularInvalid() {
        email = "ab_2c@sina.com."; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, null);
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(email));
    }

    @Test
    public void testVerifyRegularInvalid2() {
        email = "ab_2c"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, ""); //$NON-NLS-1$
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(email));
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerifyLocalPart() {
        email = "abc@sina.com"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, " ").addLocalPartRegexChecker(regularPattern, false, true); //$NON-NLS-1$
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail(email));
        email = "ab2@sina.com"; //$NON-NLS-1$
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(email));
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerifyLocalPart2() {
        email = "abc-sina.com"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, null).addLocalPartRegexChecker(regularPattern, false, true);
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(email));
    }

    @Test
    public void testVerifyLocalPart3() {
        emailVerify = emailVerify.addRegularRegexChecker(true, null).addLocalPartRegexChecker(regularPattern, false, true);
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(null));
    }

    @Test
    public void testVerifyRegularAndlocalPart9985() {
        email = "2@qq.com"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, "^\\w+([-+.]\\w+)@\\w([-.]\\w+)\\.\\w+([-.]\\w+)$") //$NON-NLS-1$
                .addLocalPartRegexChecker("^\\d$", false, false); //$NON-NLS-1$
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(email));
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerifyTLD() {
        email = "ab_2c@sina.com.xyz"; //$NON-NLS-1$
        List<String> tldLs = new ArrayList<String>();
        tldLs.add("xyz".toUpperCase()); //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(false, null).addTLDsChecker(true, tldLs, true);
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail(email));
        email = "ab_2c@sina.com.xy"; //$NON-NLS-1$
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(email));
    }

    @Test
    public void testVerifyTLD2() {
        email = "ab_2c-sina.com.xy"; //$NON-NLS-1$
        List<String> tldLs = new ArrayList<String>();
        tldLs.add("xyz".toUpperCase()); //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(false, "").addTLDsChecker(true, tldLs, true); //$NON-NLS-1$
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(email));
    }

    @Test
    public void testVerifyTLD3() {
        List<String> tldLs = new ArrayList<String>();
        tldLs.add("xyz".toUpperCase()); //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(false, "").addTLDsChecker(true, tldLs, true); //$NON-NLS-1$
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(null));
    }

    @Test
    public void testVerifyTLD4() {
        List<String> tldLs = new ArrayList<String>();
        tldLs.add("xyz".toUpperCase()); //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(false, "").addTLDsChecker(true, tldLs, true); //$NON-NLS-1$
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail("abc")); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerifyDomainWhite() {
        email = "ab@abx.com"; //$NON-NLS-1$
        List<String> ls = new ArrayList<String>();
        ls.add("abx.com"); //$NON-NLS-1$
        emailVerify = emailVerify.addListDomainsChecker(false, ls);
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail(email));
    }

    @Test
    public void testVerifyDomainWhite2() {
        email = "ab-abx.com"; //$NON-NLS-1$
        List<String> ls = new ArrayList<String>();
        ls.add("abx.com"); //$NON-NLS-1$
        emailVerify = emailVerify.addListDomainsChecker(false, ls);
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(email));
    }

    @Test
    public void testVerifyDomainWhite3() {
        List<String> ls = new ArrayList<String>();
        ls.add("abx.com"); //$NON-NLS-1$
        emailVerify = emailVerify.addListDomainsChecker(false, ls);
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(null));
    }

    @Test
    public void testVerify_domain_white_empty() {
        email = "ab@abx.com"; //$NON-NLS-1$
        List<String> ls = new ArrayList<String>();
        emailVerify = emailVerify.addListDomainsChecker(false, ls);
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(email));
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerify_domain_white_multiEmails() {
        List<String> emailLs = new ArrayList<String>();
        emailLs.add("ab@abx.com"); //$NON-NLS-1$
        emailLs.add("ab@;cd12.com"); //$NON-NLS-1$

        List<String> whiteLs = new ArrayList<String>();
        whiteLs.add("abx.com"); //$NON-NLS-1$
        emailVerify = emailVerify.addListDomainsChecker(false, whiteLs);
        for (int i = 0; i < emailLs.size(); i++) {
            String em = emailLs.get(i);
            if (i == 0) {
                assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail(em));
            } else {
                assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(em));
            }
        }

    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerify_domain_black() {
        email = "ab@abx.com"; //$NON-NLS-1$
        List<String> ls = new ArrayList<String>();
        ls.add("abx.com"); //$NON-NLS-1$
        emailVerify = emailVerify.addListDomainsChecker(true, ls);
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(email));
        email = "ab@cd.com"; //$NON-NLS-1$
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail(email));
    }

    @Test
    public void testVerify_domain_black_9985() {
        String[] emails = { "luck@qq.com", "luck@aa.bbzz11", "luck@bb.zbzz12", "luck@as.cn", "luck@cc.zbzz13" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        List<String> ls = new ArrayList<String>();
        ls.add("cc.zbzz13"); //$NON-NLS-1$
        emailVerify = emailVerify.addListDomainsChecker(true, ls);
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(email));
        email = "ab@cd.com"; //$NON-NLS-1$

        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail(emails[0]));
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail(emails[1]));
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail(emails[2]));
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail(emails[3]));
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(emails[4]));
    }

    @Test
    @Ignore
    public void testCallback_valid1() {
        emailVerify = emailVerify.addRegularRegexChecker(true, "").addCallbackMailServerChecker(true); //$NON-NLS-1$
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail("237283696@qq.com")); //$NON-NLS-1$
    }

    @Test
    @Ignore
    public void testCallback_valid_2() {
        emailVerify = emailVerify.addCallbackMailServerChecker(true).addRegularRegexChecker(true, null);
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail("237283696@qq.com")); //$NON-NLS-1$
    }

    @Test
    @Ignore
    public void testCallback_valid2() {
        emailVerify = emailVerify.addRegularRegexChecker(true, null).addCallbackMailServerChecker(true);
        try {
            assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail("qiongli1@163.com")); //$NON-NLS-1$
        } catch (TalendSMTPRuntimeException e) {
        }
    }

    @Test
    @Ignore
    public void testCallback_invalid_2() {
        emailVerify = emailVerify.addRegularRegexChecker(true, "").addCallbackMailServerChecker(true); //$NON-NLS-1$
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail("qiongli-163.com")); //$NON-NLS-1$
    }

    @Test
    @Ignore
    public void testCallback_valid_3() {
        emailVerify = emailVerify.addRegularRegexChecker(true, "").addCallbackMailServerChecker(true); //$NON-NLS-1$
        try {
            assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail("237283696@qq.com")); //$NON-NLS-1$
        } catch (TalendSMTPRuntimeException e) {
        }
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerify_All() {

        List<String> domainLs = new ArrayList<>();
        domainLs.add("abx.com.cn"); //$NON-NLS-1$
        List<String> tldLs = new ArrayList<>();
        tldLs.add("hk".toUpperCase()); //$NON-NLS-1$
        tldLs.add("cn".toUpperCase()); //$NON-NLS-1$
        email = "ab@abx.com.cn"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, "").addLocalPartRegexChecker(regularPattern, true, true) //$NON-NLS-1$
                .addListDomainsChecker(false, domainLs).addTLDsChecker(false, tldLs, false);
        // .addCallbackMailServerChecker(true, "qiongli@talend.com");
        // EmailCheckerFactory.createEmailChecker(true, regularPattern, true, true, true, false, false, tldLs,
        // domainLs);
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail(email));
    }

    @Test
    public void testVerify_All_2() {

        List<String> domainLs = new ArrayList<>();
        domainLs.add("abx.com.cn"); //$NON-NLS-1$
        List<String> tldLs = new ArrayList<>();
        tldLs.add("hk".toUpperCase()); //$NON-NLS-1$
        tldLs.add("cn".toUpperCase()); //$NON-NLS-1$
        email = "ab@abx.com.cn"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, "").addLocalPartRegexChecker("^\\\\d$", true, true) //$NON-NLS-1$ //$NON-NLS-2$
                .addListDomainsChecker(true, domainLs).addTLDsChecker(true, tldLs, true);
        // it fail to balck domain list
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(email));
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(null));
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail("abc")); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerify_email_null() {
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(null));
    }

    /**
     * Test for :use column content, in local part
     */
    @Test
    public void testUseColumnContent_1() {
        // use first 2 of the firstname, use all of the last name
        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, false, "L", "2", "0", "0", "10", "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail("ab-full@email.com", new CheckerParams("abaa", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // first n<2
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("a-full@email.com", new CheckerParams("abaa", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("ab-full@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // first n>2
        assertEquals(EmailVerifyResult.CORRECTED,
                emailVerify.checkEmail("aba-full@email.com", new CheckerParams("abaa", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("ab-full@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // last n<all
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("ab-ful@email.com", new CheckerParams("abaa", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("ab-full@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // last n>all
        assertEquals(EmailVerifyResult.CORRECTED,
                emailVerify.checkEmail("ab-fullyy@email.com", new CheckerParams("abaa", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("ab-full@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // both less
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("a-ful@email.com", new CheckerParams("abaa", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("ab-full@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // both more
        assertEquals(EmailVerifyResult.CORRECTED,
                emailVerify.checkEmail("ababb-fullyy@email.com", new CheckerParams("abaa", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("ab-full@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // only first
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("ab-@email.com", new CheckerParams("abaa", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("ab-full@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // only last
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("-full@email.com", new CheckerParams("abaa", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("ab-full@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("full@email.com", new CheckerParams("abaa", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("ab-full@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // not contain '@'
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail("ccc", new CheckerParams("abaa", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // not contain localpart
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail("@ccc", new CheckerParams("abaa", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // first name empty
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail("ccc", new CheckerParams("", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // first empty, only has last
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("ful@email.com", new CheckerParams("", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("full@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail("full@email.com", new CheckerParams("", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // last name empty
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail("ccc", new CheckerParams("abc", ""))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // last empty, only has first
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("a@email.com", new CheckerParams("abaa", ""))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("ab@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail("ab@email.com", new CheckerParams("abaa", ""))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // empty email -- invalid
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail("", new CheckerParams("abc", "full"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // email = null
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(null, new CheckerParams("abc", "full"))); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testUseColumnContent_2() {
        // use all of the firstname, use 2 first of the last name
        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, false, "L", "20", "0", "2", "0", "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail("full-aa@email.com", new CheckerParams("full", "aaaa"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // first n<all
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("fu-aa@email.com", new CheckerParams("full", "aaaa"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("full-aa@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // first n>all
        assertEquals(EmailVerifyResult.CORRECTED,
                emailVerify.checkEmail("fullyy-aa@email.com", new CheckerParams("full", "aaaa"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("full-aa@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // last n<2
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("full-b@email.com", new CheckerParams("full", "aaaa"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("full-aa@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // last n>2
        assertEquals(EmailVerifyResult.CORRECTED,
                emailVerify.checkEmail("full-aba@email.com", new CheckerParams("full", "aaaa"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("full-aa@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // both less
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("ful-b@email.com", new CheckerParams("full", "aaaa"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("full-aa@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // both more
        assertEquals(EmailVerifyResult.CORRECTED,
                emailVerify.checkEmail("ababb-fullyy@email.com", new CheckerParams("full", "aaaa"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("full-aa@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$
    }

    @Test
    public void testUseColumnContent_4() {
        // use 2+1 of the firstname, use 4+3 first of the last name
        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, false, "L", "2", "1", "4", "3", "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

        assertEquals(EmailVerifyResult.VALID,
                emailVerify.checkEmail("124-1234678@email.com", new CheckerParams("1234", "12345678"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // first n<all
        assertEquals(EmailVerifyResult.CORRECTED,
                emailVerify.checkEmail("1-1234678@email.com", new CheckerParams("1234", "12345678"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("124-1234678@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // first n>all
        assertEquals(EmailVerifyResult.CORRECTED,
                emailVerify.checkEmail("1234-1234678@email.com", new CheckerParams("1234", "12345678"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("124-1234678@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // last n<2
        assertEquals(EmailVerifyResult.CORRECTED,
                emailVerify.checkEmail("124-12@email.com", new CheckerParams("1234", "12345678"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("124-1234678@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // last n>2
        assertEquals(EmailVerifyResult.CORRECTED,
                emailVerify.checkEmail("124-123456789@email.com", new CheckerParams("1234", "12345678"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("124-1234678@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // both less
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("1-1@email.com", new CheckerParams("1234", "12345678"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("124-1234678@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$

        // both more
        assertEquals(EmailVerifyResult.CORRECTED,
                emailVerify.checkEmail("11111-222222222@email.com", new CheckerParams("1234", "12345678"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("124-1234678@email.com", emailVerify.getSuggestedEmail()); //$NON-NLS-1$
    }

    // use invalid parameters of each string
    @Test
    public void testUseColumnContent_3() {
        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, false, "L", "ab", "cc", "x", "z", "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        assertEquals(EmailVerifyResult.CORRECTED,
                emailVerify.checkEmail("124-1234678@email.com", new CheckerParams("1234", "12345678"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // use 0 of the firstname, use all of the last name
        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, false, "L", "0", "0", "10", "0", "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        assertEquals(EmailVerifyResult.VALID,
                emailVerify.checkEmail("12345678@email.com", new CheckerParams("1234", "12345678"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        assertEquals(EmailVerifyResult.CORRECTED,
                emailVerify.checkEmail("1-12345678@email.com", new CheckerParams("1234", "12345678"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    }

    // test case sensitive
    @Test
    public void testUseColumnContent_5() {
        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, true, "K", "0", "2", "0", "2", "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail("aA-bB@email.com", new CheckerParams("AaA", "BbB"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("aa-BB@email.com", new CheckerParams("AaA", "BbB"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("aa-bb@email.com", new CheckerParams("AaA", "BbB"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, true, "L", "0", "2", "0", "2", "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail("aa-bb@email.com", new CheckerParams("AaA", "BbB"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("aa-BB@email.com", new CheckerParams("AaA", "BbB"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("Aa-Bb@email.com", new CheckerParams("AaA", "BbB"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, true, "U", "0", "2", "0", "2", "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail("AA-BB@email.com", new CheckerParams("AaA", "BbB"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("aa-BB@email.com", new CheckerParams("AaA", "BbB"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(EmailVerifyResult.CORRECTED, emailVerify.checkEmail("aa-bb@email.com", new CheckerParams("AaA", "BbB"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    }

    // test fr case
    @Test
    public void testFrCase_6() {
        emailVerify = emailVerify.addRegularRegexChecker(true, StringUtils.EMPTY);
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail("gégé@laposte.fr")); //$NON-NLS-1$
        assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail("sidbpl@cebpl.caisse-epargne.fr")); //$NON-NLS-1$
    }

    // test for batch email address case
    @Test
    public void testFrCase_7() throws IOException {
        boolean startInvalid = false;
        emailVerify = emailVerify.addRegularRegexChecker(true, StringUtils.EMPTY);
        InputStream dateStream = this.getClass().getResourceAsStream("emailList.txt"); //$NON-NLS-1$
        BufferedReader br = new BufferedReader(new InputStreamReader(dateStream, "UTF-8")); //$NON-NLS-1$ //for Hindi language
                                                                                            // Double-byte type
        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.startsWith(commontChar)) {
                    String controlKey = line.replace(commontChar, StringUtils.EMPTY);
                    if (controlKey.equals(invalidChar)) {
                        startInvalid = true;
                    } else if (controlKey.equals(validChar)) {
                        startInvalid = false;
                    }
                    continue;
                }
                if (startInvalid) {
                    assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(line));
                } else {
                    assertEquals(EmailVerifyResult.VALID, emailVerify.checkEmail(line));
                }
            }

        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        } finally {
            br.close();
        }
    }

    @Test
    public void testAddCallbackMailServerChecker() {
        EmailVerify serverChecker = emailVerify.addCallbackMailServerChecker(true);
        assertNotNull(serverChecker);
    }

    @Test
    public void testCheckEmail() {
        emailVerify.initEmailVerify();
        assertEquals(EmailVerifyResult.INVALID, emailVerify.checkEmail(null, null));
    }
}
