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
 * created by jgonzalez on 22 juin 2015. See KeepLastAndGenerate.
 *
 */
public class KeepLastCharsLong extends KeepLastChars<Long> {

    private static final long serialVersionUID = -4367992150535472987L;

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
