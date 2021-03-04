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
package org.talend.dataquality.email.exception;

/**
 * created by talend on Apr 2, 2015 Detailled comment
 *
 */
public class TalendSMTPRuntimeException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -5527064038964196486L;

    public TalendSMTPRuntimeException(String message) {
        super(message);
    }

}
