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

/**
 * created by jgonzalez on 19 juin 2015. This functions generates a correct french iban number.
 *
 */
public class GenerateAccountNumberSimple extends GenerateAccountNumber {

    private static final long serialVersionUID = 5440282325373170840L;

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder accountNumber = generateIban();
        for (int i = 4; i < accountNumber.length(); i += 5) {
            accountNumber.insert(i, ' ');
        }
        return accountNumber.toString();
    }

}
