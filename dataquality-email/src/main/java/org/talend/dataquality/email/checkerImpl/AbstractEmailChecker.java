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

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.email.api.CheckerParams;
import org.talend.dataquality.email.api.EmailVerifyResult;
import org.talend.dataquality.email.api.IEmailChecker;
import org.talend.dataquality.email.exception.TalendSMTPRuntimeException;

/**
 * created by yyin on 2015年3月26日 Detailled comment
 *
 */
public abstract class AbstractEmailChecker implements IEmailChecker {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.api.IEmailChecker#check(java.lang.String)
     */
    abstract public boolean check(String email);

    @Override
    public EmailVerifyResult checkEmail(String email) throws TalendSMTPRuntimeException {
        return check(email) ? EmailVerifyResult.VALID : EmailVerifyResult.INVALID;
    }

    /*
     * For other checkers who do not use this check method, will return: verify or invalid
     * 
     * @see org.talend.dataquality.email.api.IEmailChecker#check(java.lang.String, java.lang.String[])
     */
    public EmailVerifyResult check(String email, String... strings) {
        EmailVerifyResult result;
        if (check(email)) {
            result = EmailVerifyResult.VALID;
        } else {
            result = EmailVerifyResult.INVALID;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.api.IEmailChecker#checkEmail(java.lang.String,
     * org.talend.dataquality.email.api.EmailVerifyParams)
     */
    @Override
    public EmailVerifyResult checkEmail(String email, CheckerParams parameters) {
        return check(email, parameters.getCheckerParameter());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.api.IEmailChecker#getSuggestedEmail()
     */
    @Override
    public String getSuggestedEmail() {
        return StringUtils.EMPTY;
    }

}
