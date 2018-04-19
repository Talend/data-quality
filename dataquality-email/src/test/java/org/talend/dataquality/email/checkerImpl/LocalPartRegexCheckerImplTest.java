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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * created by qiongli on 2014年12月26日 Detailled comment
 *
 */
public class LocalPartRegexCheckerImplTest {

    LocalPartRegexCheckerImpl localPartCheck;

    /**
     * Test method for {@link org.talend.dataquality.email.checkerImpl.LocalPartRegexCheckerImpl#check(java.lang.String)}.
     */
    @Test
    public void testCheck_validate() {
        String regularPattern = "a9w"; //$NON-NLS-1$
        localPartCheck = new LocalPartRegexCheckerImpl(regularPattern, true, false);
        String email = "a1w@sina.com"; //$NON-NLS-1$
        assertTrue(localPartCheck.check(email));
        String email2 = "c1aw@sina.com"; //$NON-NLS-1$
        assertTrue(localPartCheck.check(email2));
    }

    @Test
    public void testCheck_validate_translation() {
        String regularPattern = "*"; //$NON-NLS-1$
        localPartCheck = new LocalPartRegexCheckerImpl(regularPattern, true, false);
        String email = ";a9.w.-@sina.com"; //$NON-NLS-1$
        assertFalse(localPartCheck.check(email));
        assertTrue(localPartCheck.check("a@cc.com")); //$NON-NLS-1$

        regularPattern = "a.9w9"; //$NON-NLS-1$
        localPartCheck = new LocalPartRegexCheckerImpl(regularPattern, true, false);
        email = "c.1de2@sina.com"; //$NON-NLS-1$
        assertTrue(localPartCheck.check(email));
        email = "c1de2.-@sina.com"; //$NON-NLS-1$
        assertFalse(localPartCheck.check(email));

        regularPattern = "a?9w"; //$NON-NLS-1$
        localPartCheck = new LocalPartRegexCheckerImpl(regularPattern, true, false);
        email = "c-3de@sina.com"; //$NON-NLS-1$
        assertTrue(localPartCheck.check(email));
        email = "c-3de-@sina.com"; //$NON-NLS-1$
        assertFalse(localPartCheck.check(email));

        regularPattern = "9a*"; //$NON-NLS-1$
        localPartCheck = new LocalPartRegexCheckerImpl(regularPattern, true, false);
        email = "6b8@qq.com"; //$NON-NLS-1$
        assertTrue(email + " should match with " + regularPattern, localPartCheck.check(email)); //$NON-NLS-1$
        email = "9a@qq.com"; //$NON-NLS-1$
        assertTrue(email + " should match with " + regularPattern, localPartCheck.check(email)); //$NON-NLS-1$
        email = "6b88uu@qq.com"; //$NON-NLS-1$
        assertTrue(email + " should match with " + regularPattern, localPartCheck.check(email)); //$NON-NLS-1$

        regularPattern = "9a?"; //$NON-NLS-1$
        localPartCheck = new LocalPartRegexCheckerImpl(regularPattern, true, false);
        email = "6b8@qq.com"; //$NON-NLS-1$
        assertTrue(email + " should match with " + regularPattern, localPartCheck.check(email)); //$NON-NLS-1$
        email = "9a@qq.com"; //$NON-NLS-1$
        assertFalse(email + " should not match with " + regularPattern, localPartCheck.check(email)); //$NON-NLS-1$
        email = "6b88uu@qq.com"; //$NON-NLS-1$
        assertFalse(email + " should not match with " + regularPattern, localPartCheck.check(email)); //$NON-NLS-1$

    }

    /**
     * Test method for {@link org.talend.dataquality.email.checkerImpl.LocalPartRegexCheckerImpl#check(java.lang.String)}.
     */
    @Test
    public void testCheck_invalid() {
        String regularPattern = "a1w"; //$NON-NLS-1$
        localPartCheck = new LocalPartRegexCheckerImpl(regularPattern, true, false);
        String email = "a_1bc@sina.com"; //$NON-NLS-1$
        assertFalse(localPartCheck.check(email));
        email = "c1-aw@sina.com"; //$NON-NLS-1$
        assertFalse(localPartCheck.check(email));
    }

    @Test
    public void testCheck_valid_1() {
        String regularPattern = "a1w<talend.c>"; //$NON-NLS-1$
        localPartCheck = new LocalPartRegexCheckerImpl(regularPattern, true, false);
        String email = "a1wtalend.c@sina.com"; //$NON-NLS-1$
        assertTrue(localPartCheck.check(email));

    }

    @Test
    public void testCheck_invalid_2() {
        String regularPattern = "a1w<talend.c>"; //$NON-NLS-1$
        localPartCheck = new LocalPartRegexCheckerImpl(regularPattern, true, false);
        String email2 = "c1aw@sina.com"; //$NON-NLS-1$
        assertFalse(localPartCheck.check(email2));
    }

    @Test
    public void testCheck_invalid_null() {
        String regularPattern = "a1w"; //$NON-NLS-1$
        localPartCheck = new LocalPartRegexCheckerImpl(regularPattern, true, false);
        String email2 = null;
        assertFalse(localPartCheck.check(email2));
        localPartCheck = new LocalPartRegexCheckerImpl(null, true, false);
        email2 = "c1aw@sina.com"; //$NON-NLS-1$
        assertFalse(localPartCheck.check(email2));
    }

    @Test
    public void testCheck_casesenstive() {
        String regularPattern = "a9w"; //$NON-NLS-1$
        localPartCheck = new LocalPartRegexCheckerImpl(regularPattern, true, true);
        String email = "c2qw@sina.com"; //$NON-NLS-1$
        assertTrue(localPartCheck.check(email));
        regularPattern = "A9W"; //$NON-NLS-1$
        localPartCheck = new LocalPartRegexCheckerImpl(regularPattern, true, true);
        assertFalse(localPartCheck.check(email));
        String email2 = "C2QW@sina.com"; //$NON-NLS-1$
        assertTrue(localPartCheck.check(email2));
        // no case sensitive
        localPartCheck = new LocalPartRegexCheckerImpl(regularPattern, true, false);
        String email3 = "c2qw@sina.com"; //$NON-NLS-1$
        assertTrue(localPartCheck.check(email2));
    }

    @Test
    public void testTranslateToRegex() {
        localPartCheck = new LocalPartRegexCheckerImpl("", true, false); //$NON-NLS-1$
        String regularPattern = "w.<tal.end>"; //$NON-NLS-1$
        String convertedPattern = localPartCheck.translateToRegex(regularPattern);
        String expectedRegex = "[a-z]+\\.tal.end"; //$NON-NLS-1$
        assertEquals(expectedRegex, convertedPattern);

        regularPattern = "<tal>w<end>"; //$NON-NLS-1$
        convertedPattern = localPartCheck.translateToRegex(regularPattern);
        expectedRegex = "tal[a-z]+end"; //$NON-NLS-1$
        assertEquals(expectedRegex, convertedPattern);

        regularPattern = "<tal>?<end>*"; //$NON-NLS-1$
        convertedPattern = localPartCheck.translateToRegex(regularPattern);

        expectedRegex = "tal[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]end[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]*"; //$NON-NLS-1$
        assertEquals(expectedRegex, convertedPattern);

        regularPattern = "<tal>?<+end>*"; //$NON-NLS-1$
        convertedPattern = localPartCheck.translateToRegex(regularPattern);

        expectedRegex = "tal[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]+end[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]*"; //$NON-NLS-1$
        assertEquals(expectedRegex, convertedPattern);

        regularPattern = "<tal>?<\\+end>*"; //$NON-NLS-1$
        convertedPattern = localPartCheck.translateToRegex(regularPattern);

        expectedRegex = "tal[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]\\+end[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]*"; //$NON-NLS-1$
        assertEquals(expectedRegex, convertedPattern);

        regularPattern = "a<[._-]?>w"; //$NON-NLS-1$
        convertedPattern = localPartCheck.translateToRegex(regularPattern);
        expectedRegex = "[a-z][._-]?[a-z]+"; //$NON-NLS-1$
        assertEquals(expectedRegex, convertedPattern);

        regularPattern = "a.<[._w-]?>w<tat?>"; //$NON-NLS-1$
        convertedPattern = localPartCheck.translateToRegex(regularPattern);
        expectedRegex = "[a-z]\\.[._w-]?[a-z]+tat?"; // [a-z]\.\[\._w\-\]\?[a-z]+tat\? //$NON-NLS-1$
        assertEquals(expectedRegex, convertedPattern);
    }
}
