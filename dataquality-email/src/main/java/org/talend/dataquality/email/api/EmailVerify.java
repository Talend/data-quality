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
package org.talend.dataquality.email.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.email.checkerImpl.CallbackMailServerCheckerImpl;
import org.talend.dataquality.email.checkerImpl.ListDomainsCheckerImpl;
import org.talend.dataquality.email.checkerImpl.LocalPartColumnContentCheckerImpl;
import org.talend.dataquality.email.checkerImpl.LocalPartRegexCheckerImpl;
import org.talend.dataquality.email.checkerImpl.RegularRegexCheckerImpl;
import org.talend.dataquality.email.checkerImpl.TLDsCheckerImpl;
import org.talend.dataquality.email.exception.TalendSMTPRuntimeException;

/**
 * created by talend on 2014.12.26 Detailled comment
 *
 */
public class EmailVerify implements IEmailChecker {

    private static final Logger LOG = LoggerFactory.getLogger(EmailVerify.class);

    private List<IEmailChecker> checkers;

    public EmailVerify() {
        checkers = new ArrayList<>();
    }

    public EmailVerify addRegularRegexChecker(boolean isUseRegularRegex, String userDefinedRegex) {
        if (isUseRegularRegex) {
            addChecker(new RegularRegexCheckerImpl(userDefinedRegex));
        }
        return this;
    }

    public EmailVerify addLocalPartRegexChecker(String localPartRegexExpress, boolean isLocalPartCaseSensitive,
            boolean isLocalPartShort) {
        if (!StringUtils.isBlank(localPartRegexExpress)) {
            addChecker(new LocalPartRegexCheckerImpl(localPartRegexExpress, isLocalPartShort, isLocalPartCaseSensitive));
        }
        return this;
    }

    public EmailVerify addListDomainsChecker(boolean isBlackListDomains, List<String> listDomains) {
        // if white, and the list is empty, consider: INVALID
        if ((listDomains == null || listDomains.isEmpty()) && !isBlackListDomains) {
            LOG.warn(
                    " The selected white list is empty. All emails will be invalid. Either select the black list option or add some domains in the white list."); //$NON-NLS-1$
        }

        addChecker(new ListDomainsCheckerImpl(isBlackListDomains, listDomains));
        return this;
    }

    public EmailVerify addTLDsChecker(boolean isValidTLDs, List<String> tLDs, boolean isBlackListDomains) {
        if (isValidTLDs) {
            addChecker(new TLDsCheckerImpl(tLDs, isBlackListDomains));
        }
        return this;
    }

    /**
     * 
     * Call back mail server checker should be last one.
     * 
     * @param isCallbackMailServer whether should be added for current call back checker
     * @return current EmailVerify instance
     */
    public EmailVerify addCallbackMailServerChecker(boolean isCallbackMailServer) {
        if (isCallbackMailServer) {
            addChecker(new CallbackMailServerCheckerImpl());
        }
        return this;
    }

    public EmailVerify addLocalPartColumnContentChecker(boolean isColumnContent, boolean isCaseSensitive,
            String usedCaseToGenerate, String nFOfFirst, String nLOfFirst, String nFOfLast, String nLOfLast, String separator) {
        if (isColumnContent) {
            addChecker(new LocalPartColumnContentCheckerImpl(nFOfFirst, nLOfFirst, nFOfLast, nLOfLast, separator, isCaseSensitive,
                    usedCaseToGenerate));
        }
        return this;
    }

    public EmailVerify addChecker(IEmailChecker checker) {
        if (checker != null) {

            checkers.add(checker);
        }
        return this;
    }

    public void initEmailVerify() {
        checkers.clear();
    }

    /**
     * The state of the checker is invalid
     * 
     * @param email
     * @param checker
     * @return true if result is invalid or rejected
     */
    private boolean shouldStopCheck(EmailVerifyResult checkEmailResult) {
        return EmailVerifyResult.INVALID == checkEmailResult || EmailVerifyResult.REJECTED == checkEmailResult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.api.IEmailChecker#checkEmail(java.lang.String,
     * org.talend.dataquality.email.api.EmailVerifyParams)
     */
    @Override
    public EmailVerifyResult checkEmail(String email, CheckerParams parameters) {
        EmailVerifyResult checkResult = EmailVerifyResult.INVALID;
        for (IEmailChecker checker : checkers) {
            try {
                if (checker != null) {
                    checkResult = checker.checkEmail(email, parameters);
                    // only when one result is invalid, return, do NOT contine for other checkers.
                    if (shouldStopCheck(checkResult)) {
                        return checkResult;
                    }
                }
            } catch (TalendSMTPRuntimeException e) {
                LOG.warn(e.getMessage(), e);
                continue;
            } catch (Exception e) {
                LOG.warn(e.getMessage(), e);
                return checkResult;
            }
        }
        return checkResult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.api.IEmailChecker#getSuggestedEmail()
     */
    @Override
    public String getSuggestedEmail() {
        String suggestedEmail = StringUtils.EMPTY;
        for (IEmailChecker checker : checkers) {
            if (checker != null) {
                suggestedEmail = checker.getSuggestedEmail();
                // only when one result is invalid, return, do NOT contine for other checkers.
                // Current case only localPart checker can return suggest email if any checker added this function then the code
                // need to consisder the issue of order
                if (!suggestedEmail.isEmpty()) {
                    return suggestedEmail;
                }
            }
        }
        return suggestedEmail;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.api.IEmailChecker#checkEmail(java.lang.String)
     */
    @Override
    public EmailVerifyResult checkEmail(String email) {
        EmailVerifyResult checkResult = EmailVerifyResult.INVALID;
        for (IEmailChecker checker : checkers) {
            try {
                if (checker != null) {
                    checkResult = checker.checkEmail(email);
                    // only when one result is invalid, return, do NOT contine for other checkers.
                    if (shouldStopCheck(checkResult)) {
                        return checkResult;
                    }
                }
            } catch (TalendSMTPRuntimeException e) {
                LOG.warn(e.getMessage(), e);
                continue;
            } catch (Exception e) {
                LOG.warn(e.getMessage(), e);
                return checkResult;
            }
        }
        return checkResult;
    }

}
