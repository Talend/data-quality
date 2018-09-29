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
        if (parameters == null)
            return "";

        String pattern = parameters[0];
        for (int i = 0; i < pattern.length(); i++) {
            int codePoint = pattern.codePointAt(i);
            switch (codePoint) {
            case '\\':
                if (i == pattern.length() - 1) {
                    result.append('\\');
                    break;
                }
                char nextChar = pattern.charAt(i + 1);
                if (nextChar > '0' && nextChar - '0' < parameters.length) {
                    result.append(parameters[nextChar - '0'].trim());
                    i++;
                } else
                    result.append('\\');
                break;
            default:
                result.append(TextPatternUtil.replacePatternCharacter(codePoint, rnd));
                break;
            }
            if (Character.isHighSurrogate(pattern.charAt(i)))
                i++;
        }
        return result.toString();
    }
}
