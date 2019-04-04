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
package org.talend.dataquality.datamasking;

/**
 * created by jgonzalez on 18 juin 2015. This enum stores all the functions that can be used in the component.
 *
 */
public enum FunctionCategory {
    SET_TO_NULL,
    EMAIL_MASKING,
    SSN_GENERATION,
    PHONE_GENERATION,
    DATA_GENERATION,
    SSN_MASKING,
    PHONE_MASKING,
    CHARACTER_HANDLING,
    DATE_HANDLING,
    NUMBER_HANDLING,
    ADDRESS_MASKING,
    CREDIT_CARD_GENERATION,
    BANK_ACCOUNT_GENERATION;

    FunctionCategory() {
    }
}
