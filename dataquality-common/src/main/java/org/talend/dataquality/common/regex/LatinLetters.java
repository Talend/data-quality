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
package org.talend.dataquality.common.regex;

import java.util.regex.Pattern;

import org.talend.daikon.pattern.character.CharPattern;

/**
 * Regex replacement for latin characters "ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ"
 * 
 * @author mzhao
 *
 */
public class LatinLetters extends ChainResponsibilityHandler {

    private Pattern pattern = Pattern.compile(CharPattern.UPPER_LATIN.getPattern().getRegex());

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.util.ChainResponsibilityHandler#getReplaceStr()
     */
    @Override
    protected String getReplaceStr() {
        return "A";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.util.ChainResponsibilityHandler#getRegex()
     */
    @Override
    protected Pattern getRegex() {
        // [A-Z] from http://www.unicode.org/charts/PDF/U0000.pdf
        // [À-ß] exclude ×
        return pattern;
    }

}
