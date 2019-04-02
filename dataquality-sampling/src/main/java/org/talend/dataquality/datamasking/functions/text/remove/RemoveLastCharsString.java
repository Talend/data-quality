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
package org.talend.dataquality.datamasking.functions.text.remove;

/**
 * created by jgonzalez on 22 juin 2015. See RemoveLastChars.
 *
 */
public class RemoveLastCharsString extends RemoveLastChars<String> {

    private static final long serialVersionUID = -7871203355859404648L;

    @Override
    protected String getDefaultOutput() {
        return EMPTY_STRING;
    }

    @Override
    protected String getOutput(String string) {
        return string;
    }
}
