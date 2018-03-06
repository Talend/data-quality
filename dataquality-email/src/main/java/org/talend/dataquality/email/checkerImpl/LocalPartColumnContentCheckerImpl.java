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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.email.api.EmailVerifyResult;

/**
 * created by yyin on 2015年3月26日 Detailled comment
 *
 */
public class LocalPartColumnContentCheckerImpl extends AbstractEmailChecker {

    private static final Logger LOG = LoggerFactory.getLogger(LocalPartColumnContentCheckerImpl.class);

    private int nFOfFirst, nLOfFirst, nFOfLast, nLOfLast;

    private boolean isCaseSensitive;

    private String separator, usedCaseToGenerate, correctedEmail = StringUtils.EMPTY;

    private static final String LOWER = "L"; //$NON-NLS-1$

    private static final String UPPER = "U"; //$NON-NLS-1$

    /**
     * 
     * for each number, if is blank, then use default : all. "sFAndmLOfFirst" means: pick n first characters and m last
     * characters of the firstname column: when both nF and nL has values.
     * 
     * @param usedCaseToGenerate
     * @param isCaseSensitive
     * 
     * @param nFOfFirst : 0 ~ n,set directly ;
     * @param nLOfFirst : 0 ~ n,set directly;
     * @param sFAndmLOfFirst :"2+3" separate to pickNFOfFirst, pickMLOfFirst
     * @param nFOfLast : 0 ~ n,set directly;
     * @param nLOfLast : 0 ~ n,set directly;
     * @param sFAndmLOfLast :"3+4" separate to pickNFOfLast, pickMLOfLast
     * @param isCaseSensitive: if the checker is case sensitive or not(if true: abc vs ABC = CORRECTED, if false =
     * VERIFIED)
     * @param usedCase: L=lowercase,U=uppercase,K=keepcase, when generate the suggested email, use this option
     */
    public LocalPartColumnContentCheckerImpl(String sFOfFirst, String sLOfFirst, String sFOfLast, String sLOfLast,
            String separator, boolean isCaseSensitive, String usedCase) {
        nFOfFirst = initNumber(sFOfFirst);
        nLOfFirst = initNumber(sLOfFirst);
        nFOfLast = initNumber(sFOfLast);
        nLOfLast = initNumber(sLOfLast);
        this.separator = separator;
        this.isCaseSensitive = isCaseSensitive;
        this.usedCaseToGenerate = usedCase;
    }

    /**
     * 
     * if the string is blank, consider default is 0;
     * if the string can not be parse to a number, use default: 0
     * 
     * @param usedString
     * @return
     */
    private int initNumber(String usedString) {
        if (StringUtils.isBlank(usedString)) {
            return 0;
        }
        try {
            return Integer.parseInt(usedString);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return 0;
        }
    }

    /*
     * Not support
     * 
     * @see org.talend.dataquality.email.api.IEmailChecker#check(java.lang.String)
     */
    @Override
    public boolean check(String email) {
        // not support
        return false;
    }

    /*
     * use the request to spell out the suggested local part: suggestedLocalPart, compare the email's local part with
     * suggestedLocalPart: if equal , return {VERIFY,""}; if not equal, return {CORRECTED,suggestedLocalPart}.
     */
    @Override
    public EmailVerifyResult check(String email, String... strings) {
        EmailVerifyResult result = EmailVerifyResult.INVALID;
        resetCorrectedEmail();
        // when email is null or empty,
        if (StringUtils.isBlank(email) || strings == null || strings.length < 2) {
            return result;
        }
        int pos = email.indexOf("@"); //$NON-NLS-1$
        // if the email does not contains '@', or both first and last name are empty,consider :invalid
        if (pos < 1 || (StringUtils.isBlank(strings[0]) && StringUtils.isBlank(strings[1]))) {
            return result;
        }
        String localpart = email.substring(0, pos);
        // return Invalid when local part is null or empty,
        if (StringUtils.isBlank(localpart)) {
            return result;
        }
        // return invalid, when the domain part is invalid.?? this checker only check local part

        // first: spell the suggested local part (if the column's length < pointed number, use all of the column)
        // if the column is blank,use the other
        String suggestedLocal = getSuggestedLocalPart(strings[0], strings[1]);

        // second: compare the local part of the checked email with the suggested one
        if (isCaseSensitive) {
            if (StringUtils.equals(localpart, suggestedLocal)) {
                result = EmailVerifyResult.VALID;
            } else {
                result = EmailVerifyResult.CORRECTED;
                correctedEmail = suggestedLocal + email.substring(pos);
            }
        } else {
            if (StringUtils.equalsIgnoreCase(localpart, suggestedLocal)) {
                result = EmailVerifyResult.VALID;
            } else {
                result = EmailVerifyResult.CORRECTED;
                correctedEmail = suggestedLocal + email.substring(pos);
            }
        }

        return result;
    }

    /**
     * DOC zshen Comment method "resetCorrectedEmail".
     */
    private void resetCorrectedEmail() {
        correctedEmail = StringUtils.EMPTY;
    }

    public String getSuggestedLocalPart(String firstName, String lastName) {
        if (StringUtils.isBlank(firstName)) {// if first name is empty, no need to handle
            return spellFromColumn(nFOfLast, nLOfLast, lastName);
        }
        if (StringUtils.isBlank(lastName)) {// if last name is empty, no need to handle
            return spellFromColumn(nFOfFirst, nLOfFirst, firstName);
        }

        String firstPart = spellFromColumn(nFOfFirst, nLOfFirst, firstName);
        String lastPart = spellFromColumn(nFOfLast, nLOfLast, lastName);

        // if the picked number of first or last name is 0, means no need to pick any chars from it, then no need to add
        // the separator to the email
        if ((nFOfFirst + nLOfFirst) < 1) {
            return lastPart;
        }
        if ((nFOfLast + nLOfLast) < 1) {
            return firstPart;
        }

        return firstPart + this.separator + lastPart;
    }

    /**
     * get the requested part from the column : first/last name.
     * 
     * @param strings
     */
    private String spellFromColumn(int nFirst, int nLast, String stringColumn) {
        String localPart = StringUtils.EMPTY;
        int length = stringColumn.length();
        // any number bigger than the string's length, return the whole string
        if ((nFirst + nLast) >= length || nFirst >= length || nLast >= length) {
            return getCasedString(stringColumn);

        }
        // get the first n characters of the stringColumn
        if (nFirst > 0) {
            localPart = stringColumn.substring(0, nFirst);
        }
        // get the last n characters of the stringColumn
        if (nLast > 0) {
            localPart = localPart + stringColumn.substring(length - nLast);
        }
        return getCasedString(localPart);
    }

    private String getCasedString(String caseString) {
        String casedOne = caseString;
        if (StringUtils.equals(LOWER, usedCaseToGenerate)) {
            return StringUtils.lowerCase(caseString);
        } else if (StringUtils.equals(UPPER, usedCaseToGenerate)) {
            return StringUtils.upperCase(caseString);
        }
        return casedOne;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.checkerImpl.AbstractEmailChecker#getSuggestedEmail()
     */
    @Override
    public String getSuggestedEmail() {
        return this.correctedEmail;
    }

}
