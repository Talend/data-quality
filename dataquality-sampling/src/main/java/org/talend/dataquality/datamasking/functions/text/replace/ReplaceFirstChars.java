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
package org.talend.dataquality.datamasking.functions.text.replace;

import org.talend.dataquality.datamasking.functions.text.CharactersOperation;

/**
 * created by jgonzalez on 22 juin 2015. This function will replace the n first chars of the input.
 *
 */
public abstract class ReplaceFirstChars<T> extends CharactersOperation<T> {

    private static final long serialVersionUID = 2584207431532787461L;

    @Override
    protected void initAttributes() {
        endIndex = Integer.parseInt(parameters[0]);
        if (parameters.length == 2)
            charToReplace = parameters[1].charAt(0);
    }
}
