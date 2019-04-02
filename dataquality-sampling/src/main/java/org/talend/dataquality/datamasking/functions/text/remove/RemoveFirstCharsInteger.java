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

import org.talend.dataquality.datamasking.functions.text.keep.RemoveFirstChars;

/**
 * created by jgonzalez on 22 juin 2015. See RemoveFirstChars.
 *
 */
public class RemoveFirstCharsInteger extends RemoveFirstChars<Integer> {

    private static final long serialVersionUID = -8172824699689903857L;

    @Override
    protected Integer getDefaultOutput() {
        return 0;
    }

    @Override
    protected Integer getOutput(String str) {
        return Integer.parseInt(str);
    }
}
