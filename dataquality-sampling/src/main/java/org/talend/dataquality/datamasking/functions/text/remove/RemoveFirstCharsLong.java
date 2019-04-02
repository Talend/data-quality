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
public class RemoveFirstCharsLong extends RemoveFirstChars<Long> {

    private static final long serialVersionUID = -4656535468430704086L;

    @Override
    protected Long getDefaultOutput() {
        return 0L;
    }

    @Override
    protected Long getOutput(String str) {
        return Long.parseLong(str);
    }
}
