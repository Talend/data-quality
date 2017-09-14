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
package org.talend.dataquality.datamasking.functions;

import org.apache.commons.lang.StringUtils;

/**
 * created by jgonzalez on 19 juin 2015. This function produces a correct account number and tries to keep the country
 * where it's from.
 *
 */
public class GenerateAccountNumberFormat extends GenerateAccountNumber {

    private static final long serialVersionUID = 116648954835024228L;

    @Override
    protected String doGenerateMaskedField(String str) {
        String accountNumber = removeFormatInString(str); // $NON-NLS-1$ //$NON-NLS-2$
        StringBuilder accountNumberFormat;
        boolean isAmerican = false;
        if (!StringUtils.isEmpty(accountNumber) && accountNumber.length() > 9) {
            try {
                if (Character.isDigit(accountNumber.charAt(0)) && isAmericanAccount(accountNumber)) {
                    accountNumberFormat = generateAmericanAccountNumber(accountNumber);
                    isAmerican = true;
                } else {
                    accountNumberFormat = generateIban(accountNumber);
                }
                if (keepFormat)
                    return insertFormatInString(str, accountNumberFormat);
            } catch (NumberFormatException e) {
                accountNumberFormat = generateIban();
            }
        } else {
            accountNumberFormat = generateIban();
        }

        if (isAmerican) {
            accountNumberFormat.insert(9, ' ');
        } else {
            for (int i = 4; i < accountNumberFormat.length(); i += 5) {
                accountNumberFormat.insert(i, ' ');
            }
        }
        return accountNumberFormat.toString();
    }

}
