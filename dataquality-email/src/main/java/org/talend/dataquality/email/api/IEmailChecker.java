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
package org.talend.dataquality.email.api;

/**
 * any kind of check interface.
 *
 */
public interface IEmailChecker {

    /**
     * 
     * <p>
     * Checks if a field has a valid e-mail address.
     * </p>
     * 
     * @param email The value validation is being performed on. A <code>null</code> value is considered invalid.
     * @param parameters which checker should be used and record some parameter of checker
     * @return current result of check
     */
    EmailVerifyResult checkEmail(String email, CheckerParams parameters);

    /**
     * 
     * <p>
     * Checks if a field has a valid e-mail address.
     * </p>
     * 
     * @param email The value validation is being performed on. A <code>null</code> value is considered invalid.
     * @return current result of check
     */
    EmailVerifyResult checkEmail(String email);

    /**
     * <p>
     * Get suggested email
     * </p>
     * 
     * @return StringUtils.EMPTY when there is not a suggest email exist else return the string of email
     */
    String getSuggestedEmail();
}
