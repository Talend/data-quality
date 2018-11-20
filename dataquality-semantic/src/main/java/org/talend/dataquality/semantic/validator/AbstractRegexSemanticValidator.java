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

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public abstract class AbstractRegexSemanticValidator implements ISemanticValidator {

    protected java.util.regex.Pattern caseSensitivePattern;

    protected java.util.regex.Pattern caseInsensitivePattern;

    protected com.google.re2j.Pattern caseSensitiveRe2JPattern;

    protected com.google.re2j.Pattern caseInsensitiveRe2JPattern;

    public boolean isValid(String str, boolean caseSensitive) {
        return isValid(str, caseSensitive, true);
    }

    public boolean isValid(String str, boolean caseSensitive, Boolean re2jCompliant) {
        if (!canCheck(str, re2jCompliant))
            return false;

        if (re2jCompliant != null && !re2jCompliant)
            return (caseSensitive ? caseSensitivePattern.matcher(str.trim()).find()
                    : caseInsensitivePattern.matcher(str.trim()).find());
        else
            return (caseSensitive ? caseSensitiveRe2JPattern.matcher(str.trim()).find()
                    : caseInsensitiveRe2JPattern.matcher(str.trim()).find());
    }

    public boolean isValid(String str) {
        return isValid(str, false);
    }

    private boolean canCheck(String str, Boolean re2jCompliant) {
        if (str == null)
            return false;
        if (re2jCompliant != null && !re2jCompliant) {
            if (caseSensitivePattern == null || caseInsensitivePattern == null)
                return false;
        } else {
            if (caseSensitiveRe2JPattern == null || caseInsensitiveRe2JPattern == null)
                return false;
        }
        return true;
    }
}
