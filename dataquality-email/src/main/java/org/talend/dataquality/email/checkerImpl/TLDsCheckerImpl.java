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

import java.util.List;

import org.talend.dataquality.email.CommonCheck.DomainValidator;

/**
 * created by talend on 2014年12月26日 Detailled comment
 *
 */
public class TLDsCheckerImpl extends AbstractEmailChecker {

    private List<String> tLDs;

    private boolean isSelectBlackList = false;

    /**
     * DOC talend TLDsCheckerImpl constructor comment.
     * 
     * @param tLDs
     */
    public TLDsCheckerImpl(List<String> tLDs, boolean isSelectBlackList) {
        super();
        this.tLDs = tLDs;
        this.isSelectBlackList = isSelectBlackList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.api.IEmailChecker#check(java.lang.String)
     */
    @Override
    public boolean check(String email) {
        // no need to check TLD if the "white list" is selected.
        if (!isSelectBlackList) {
            return true;
        }
        // If the email does not contain an '@', it's not valid
        if (email == null || email.indexOf('@') == -1) {
            return false;
        }

        String inputEmailTld = email.substring(email.lastIndexOf(".") + 1); //$NON-NLS-1$
        if (tLDs != null && tLDs.contains(inputEmailTld.toUpperCase())) {
            return true;
        }

        return DomainValidator.getInstance(true).isValidTld(inputEmailTld);

    }

}
