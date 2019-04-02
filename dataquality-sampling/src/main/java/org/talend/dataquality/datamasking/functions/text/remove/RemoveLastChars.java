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

import org.talend.dataquality.datamasking.functions.text.CharactersOperation;
import org.talend.dataquality.datamasking.functions.util.CharactersOperationUtils;

/**
 * created by jgonzalez on 22 juin 2015. This function will remove the n last chars of the input.
 *
 */
public abstract class RemoveLastChars<T> extends CharactersOperation<T> {

    private static final long serialVersionUID = 2741426616834434367L;

    @Override
    protected void initAttributes() {
        endNumberToReplace = Integer.parseInt(parameters[0]);
        toRemove = true;
    }

    @Override
    protected boolean validParameters() {
        return CharactersOperationUtils.validParameters1Number(parameters);
    }
}
