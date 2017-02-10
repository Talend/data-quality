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
package org.talend.dataquality.semantic.validator;

import java.util.regex.Pattern;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public abstract class AbstractRegexSemanticValidator implements ISemanticValidator {

    protected Pattern pattern;

    public boolean isValid(String str) {
        if (str == null || pattern == null) {
            return false;
        }
        return pattern.matcher(str.trim()).find();
    }
}
