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

import org.talend.dataquality.datamasking.generic.Alphabet;

/**
 * created by jgonzalez on 22 juin 2015. See KeepLastAndGenerate.
 *
 */
public class KeepLastCharsLong extends KeepLastChars<Long> {

    private static final long serialVersionUID = -4367992150535472987L;

    @Override
    public void setFF1Cipher(String alphabetName, String method, String password) {
        super.setFF1Cipher(Alphabet.DIGITS.name(), method, password);
    }

    @Override
    protected Long getDefaultOutput() {
        return 0L;
    }

    @Override
    protected Long getOutput(String string) {
        return Long.parseLong(string);
    }

    @Override
    protected boolean validParameters() {
        return CharactersOperationUtils.validParameters1Number1DigitReplace(parameters);
    }

}
