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
package org.talend.dataquality.semantic.validator.impl;

import java.util.Locale;

import org.talend.dataquality.semantic.validator.AbstractPhoneNumberValidator;

/**
 * Created by vlesquere on 24/06/16.
 */
public class DEPhoneNumberValidator extends AbstractPhoneNumberValidator {

    @Override
    public boolean isValid(String phoneNumber) {
        return isValidPhoneNumber(phoneNumber, Locale.GERMANY);
    }
}
