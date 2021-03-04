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
package org.talend.dataquality.standardization.index;

import java.io.Serializable;

/**
 * record the error information
 * 
 * @author tychu
 *
 */
public class Error implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean status = true;

    private String message = "";//$NON-NLS-1$

    public void set(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public void reset() {
        this.status = true;
        this.message = "";//$NON-NLS-1$
    }

    public boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
