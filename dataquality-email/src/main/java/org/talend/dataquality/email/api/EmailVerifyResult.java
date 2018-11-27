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

/**
 * Enumeration of the email verify result.
 */
public enum EmailVerifyResult {

    VALID("VALID"), //$NON-NLS-1$
    INVALID("INVALID"), //$NON-NLS-1$
    CORRECTED("CORRECTED"), //$NON-NLS-1$
    VERIFIED("VERIFIED"), //$NON-NLS-1$
    REJECTED("REJECTED"); //$NON-NLS-1$

    private String resultValue;

    /**
     * Getter for resultValue.
     * 
     * @return the resultValue
     */
    public String getResultValue() {
        return this.resultValue;
    }

    /**
     * Sets the resultValue.
     * 
     * @param resultValue the resultValue to set
     */
    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    private EmailVerifyResult(String resultValue) {
        this.setResultValue(resultValue);
    }

}
