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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.talend.dataquality.email.CommonCheck.EmailValidator;

/**
 * created by talend on 2014年12月26日 Detailled comment
 *
 */
public class ListDomainsCheckerImpl extends AbstractEmailChecker {

    private boolean isBlackListDomains;

    private List<String> listDomains;

    private Map<String, Pattern> listDomainsPatterns = new HashMap<String, Pattern>();

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
        // check domain part
        int pos = email.indexOf('@');
        // If the email does not contain an '@', it's not valid
        if (pos == -1) {
            return false;
        }
        String domainPart = email.substring(pos + 1);

        // if white, and the list is empty, consider: INVALID
        if ((listDomains == null || listDomains.isEmpty()) && !isBlackListDomains) {
            return false;
        }

        // valid: in white list or not in blacklist
        if (listDomains != null) {
            if (isBlackListDomains) {
                // is black list domains
                // for black list domains, enable * sign to represent any char sequence of [\\p{Alnum}-] like the
                // white list example below.
                return !checkListDomainsContainsDomainPart(domainPart);
            } else {
                // is white list domains
                return checkListDomainsContainsDomainPart(domainPart);
            }
        }

        // check from apache
        return EmailValidator.getInstance().isValidDomain(domainPart);
    }

    /**
     * 
     * DOC yyin Comment method "checkListDomainsContainsDomainPart".
     * 
     * @param domainPart
     * @return true: if the domainPart contains in the listDomains; false: if the domainPart does NOT contained in the
     * list Domains
     */
    private boolean checkListDomainsContainsDomainPart(String domainPart) {
        for (String domainPattern : listDomains) {
            boolean isNeedConversion = domainPattern.indexOf('*') >= 0;
            if (isNeedConversion) {
                Pattern domainPattern2 = listDomainsPatterns.get(domainPattern);

                if (domainPattern2.matcher(domainPart).matches() && EmailValidator.getInstance().isValidDomain(domainPart)) {
                    return true;
                }
            } else {
                // when there's no * sign in the domain, compare it with the input data directly.
                if (domainPattern.equals(domainPart)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * DOC talend ListDomainsCheckerImpl constructor comment.
     * 
     * @param isBlackListDomains
     * @param listDomains
     */
    public ListDomainsCheckerImpl(boolean isBlackListDomains, List<String> listDomains) {
        super();
        this.isBlackListDomains = isBlackListDomains;
        this.listDomains = listDomains;

        // pre-compile the converted patterns to get better performance
        if (listDomains != null) {
            for (String domainPattern : listDomains) {
                boolean isNeedConversion = domainPattern.indexOf('*') >= 0;
                if (isNeedConversion) {
                    String convertedPattern = domainPattern.replace("*", "[\\p{Alnum}-.]*").replace(".", "\\."); //$NON-NLS-1$ //$NON-NLS-2$
                    Pattern domainPattern2 = Pattern.compile(convertedPattern);
                    listDomainsPatterns.put(domainPattern, domainPattern2);
                }
            }
        }
    }

}
