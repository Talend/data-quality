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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * created by talend on 2014年12月26日 Detailled comment
 *
 */
public class RegularRegexCheckerImpl extends AbstractEmailChecker {

    // ascii and special character of french
    private static final String LEGAL_ASCII_REGEX = "^[\\p{ASCII}||\\p{L}||\\p{M}]+$"; //$NON-NLS-1$

    private static final String EMAIL_REGEX =
            "^(([^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\\\"[^\\\"]*\\\"))(\\.(([^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\\\"[^\\\"]*\\\")))*@([\\p{L}\\p{M}0-9]([\\p{L}\\p{M}0-9\\-]{0,61}[\\p{L}\\p{M}0-9])?\\.)+[\\p{L}\\p{M}]{2,6}$"; //$NON-NLS-1$

    private static final Pattern MATCH_ASCII_PATTERN = Pattern.compile(LEGAL_ASCII_REGEX);

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private String userDefinedRegex = null;

    public RegularRegexCheckerImpl(String userDefined) {
        this.userDefinedRegex = userDefined;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.IEmailChecker#check(java.lang.String)
     */
    @Override
    public boolean check(String email) {

        if (email == null) {
            return false;
        }

        // added TDQ-9985, if user defined a regex, use it
        if (StringUtils.isNotBlank(userDefinedRegex)) {
            Matcher matcher = Pattern.compile(userDefinedRegex).matcher(email);
            return matcher.matches();
        }

        Matcher asciiMatcher = MATCH_ASCII_PATTERN.matcher(email);
        if (!asciiMatcher.matches()) {
            return false;
        }

        // Check the whole email address structure
        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        if (!emailMatcher.matches()) {
            return false;
        }

        if (email.endsWith(".")) { //$NON-NLS-1$
            return false;
        }

        return true;
    }
}
