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
package org.talend.dataquality.datamasking.functions.text.keep;

import org.talend.dataquality.datamasking.functions.util.CharactersOperationUtils;

/**
 * created by jgonzalez on 22 juin 2015. See KeepLastAndGenerate.
 *
 */
public class KeepLastCharsString extends KeepLastChars<String> {

    private static final long serialVersionUID = 5714789810163265429L;

    @Override
    protected String getDefaultOutput() {
        return EMPTY_STRING;
    }

    @Override
    protected String getOutput(String string) {
        return string;
    }

    @Override
    protected boolean validParameters() {
        return CharactersOperationUtils.validParameters1Number1CharReplace(parameters);
    }
}
