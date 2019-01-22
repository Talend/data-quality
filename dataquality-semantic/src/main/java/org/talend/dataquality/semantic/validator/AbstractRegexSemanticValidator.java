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

    protected Pattern caseSensitivePattern;

    protected Pattern caseInsensitivePattern;

    public boolean isValid(String str, boolean caseSensitive) {
        if (str == null || caseSensitivePattern == null || caseInsensitivePattern == null) {
            return false;
        }
        return (caseSensitive ? caseSensitivePattern.matcher(str.trim()).find()
                : caseInsensitivePattern.matcher(str.trim()).find());
    }

    public boolean isValid(String str) {
        return isValid(str, false);
    }
}
