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

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.common.pattern.TextPatternUtil;

/**
 * created by jgonzalez on 17 juil. 2015 Detailled comment
 *
 */
public class GenerateFromPattern extends Function<String> {

    private static final long serialVersionUID = 7920843158759995757L;

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);
        if (parameters == null) {
            return StringUtils.EMPTY;
        }

        String pattern = parameters[0];
        boolean skipNextLoop = false;
        for (int i = 0; i < pattern.length(); i++) {
            if (skipNextLoop) {
                skipNextLoop = false;
                continue;
            }
            int codePoint = pattern.codePointAt(i);
            switch (codePoint) {
            case '\\':
                skipNextLoop = handleBackslashCase(result, pattern, i);
                break;
            default:
                result.append(TextPatternUtil.replacePatternCharacter(codePoint, rnd));
                break;
            }
            if (Character.isHighSurrogate(pattern.charAt(i))) {
                skipNextLoop = true;
            }
        }
        return result.toString();
    }

    /**
     * Deal backslash case.
     * 
     * @param result
     * @param skipNextLoop
     */
    private boolean handleBackslashCase(StringBuilder result, String pattern, int i) {
        if (i == pattern.length() - 1) {
            result.append('\\');
            return false;
        }
        char nextChar = pattern.charAt(i + 1);
        if (nextChar > '0' && nextChar - '0' < parameters.length) {
            result.append(parameters[nextChar - '0'].trim());
            return true;
        } else {
            result.append('\\');
        }
        return false;
    }
}
