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
package org.talend.dataquality.email.CommonCheck;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by talend on 2014年12月30日 Detailled comment
 *
 */
public class EmailValidator implements Serializable {

    private static final long serialVersionUID = 1705927040799295880L;

    private static final String SPECIAL_CHARS = "\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]"; //$NON-NLS-1$

    private static final String VALID_CHARS = "[^\\s" + SPECIAL_CHARS + "]"; //$NON-NLS-1$ //$NON-NLS-2$

    private static final String QUOTED_USER = "(\"[^\"]*\")"; //$NON-NLS-1$

    private static final String WORD = "((" + VALID_CHARS + "|')+|" + QUOTED_USER + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private static final String IP_DOMAIN_REGEX = "^\\[(.*)\\]$"; //$NON-NLS-1$

    private static final String USER_REGEX = "^\\s*" + WORD + "(\\." + WORD + ")*$"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private static final Pattern IP_DOMAIN_PATTERN = Pattern.compile(IP_DOMAIN_REGEX);

    private static final Pattern USER_PATTERN = Pattern.compile(USER_REGEX);

    private final boolean allowLocal;

    /**
     * Singleton instance of this class, which doesn't consider local addresses as valid.
     */
    private static final EmailValidator EMAIL_VALIDATOR = new EmailValidator(false);

    /**
     * Singleton instance of this class, which does consider local addresses valid.
     */
    private static final EmailValidator EMAIL_VALIDATOR_WITH_LOCAL = new EmailValidator(true);

    /**
     * Returns the Singleton instance of this validator.
     *
     * @return singleton instance of this validator.
     */
    public static EmailValidator getInstance() {
        return EMAIL_VALIDATOR;
    }

    /**
     * Returns the Singleton instance of this validator, with local validation as required.
     *
     * @param allowLocal Should local addresses be considered valid?
     * @return singleton instance of this validator
     */
    public static EmailValidator getInstance(boolean allowLocal) {
        if (allowLocal) {
            return EMAIL_VALIDATOR_WITH_LOCAL;
        }
        return EMAIL_VALIDATOR;
    }

    /**
     * Protected constructor for subclasses to use.
     *
     * @param allowLocal Should local addresses be considered valid?
     */
    protected EmailValidator(boolean allowLocal) {
        super();
        this.allowLocal = allowLocal;
    }

    /**
     * DOC talend Comment method "isValid".
     * 
     * @param email
     * @return
     */
    public boolean isValid(String email) {

        return true;
    }

    /**
     * Returns true if the domain component of an email address is valid.
     *
     * @param domain being validated.
     * @return true if the email address's domain is valid.
     */
    public boolean isValidDomain(String domain) {
        // see if domain is an IP address in brackets
        Matcher ipDomainMatcher = IP_DOMAIN_PATTERN.matcher(domain);

        if (ipDomainMatcher.matches()) {
            InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();
            return inetAddressValidator.isValid(ipDomainMatcher.group(1));
        } else {
            // Domain is symbolic name
            DomainValidator domainValidator = DomainValidator.getInstance(allowLocal);
            return domainValidator.isValid(domain);
        }
    }

    /**
     * Returns true if the user component of an email address is valid.
     *
     * @param user being validated
     * @return true if the user name is valid.
     */
    public boolean isValidUser(String user) {
        return USER_PATTERN.matcher(user).matches();
    }

}
