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
package org.talend.dataquality.semantic.recognizer;

import org.apache.commons.lang3.StringUtils;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public enum MainCategory {

    Alpha,
    AlphaNumeric,
    Numeric,
    BLANK,
    NULL;

    public static MainCategory getMainCategory(String str) {
        if (str == null) {
            return MainCategory.NULL;
        } // else
        if (StringUtils.trim(str).equals(StringUtils.EMPTY)) {
            return MainCategory.BLANK;
        } // else

        boolean notAlpha = false;
        boolean notNumeric = false;

        int length = str.length();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);

            if (Character.isDigit(ch)) {
                notAlpha = true;
            } else if (Character.isLetter(ch)) {
                notNumeric = true;
            }
            if (notAlpha && notNumeric) {
                return MainCategory.AlphaNumeric;
            }
        }

        if (notAlpha) {
            return MainCategory.Numeric;
        } // else
        if (notNumeric) {
            return MainCategory.Alpha;
        }

        return MainCategory.AlphaNumeric;
    }

}
