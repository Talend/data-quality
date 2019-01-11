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
 * created by jgonzalez on 22 juin 2015. This function will replace every digit by the parameter.
 *
 */
public abstract class ReplaceNumeric<T> extends CharactersOperation<T> {

    private static final long serialVersionUID = -6892473143126922554L;

    @Override
    protected void initAttributes() {
        if (parameters != null && parameters.length == 1 && !StringUtils.isEmpty(parameters[0]))
            charToReplace = parameters[0].charAt(0);
    }

    @Override
    protected boolean validParameters() {
        return CharactersOperationUtils.validParameters1DigitReplace(parameters);
    }
}
